package ru.fromgate.minecrafttranslator;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.JsonWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ResourceDownloader {



    private static byte[] BUFFER = new byte[1024];

    private static File CACHE = MainActivity.getActivity().getCacheDir();
    private static File DIR = Environment.getExternalStorageDirectory();

    public static boolean downloadLangs(Set<String> langList) {
        try {
            File zipFile = download(Const.LANGPACK_DIRNAME + ".zip", Const.LANG_PROJECT_URL);
            if (zipFile == null) return false;
            File target = new File(DIR, Const.LANGPACK_DIR);
            if (target.exists()) {
                deleteRecursive(target);
            }
            target.mkdirs();
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry ze = zis.getNextEntry();

            File targetTexts = new File(target, "texts");
            targetTexts.mkdir();

            Set<String> langs = new HashSet<>();

            while (ze != null) {
                String zipped = new File(ze.getName()).getName();
                if (!ze.isDirectory()) {
                    if (zipped.equals("pack_manifest.json") || zipped.equals("pack_icon.png")) {
                        extractFromZipStream(zis, new File(target, zipped));
                    } else if (zipped.matches(".*\\.lang")) {
                        String langCode = zipped.replace(".lang", "");
                        if (langList == null || langList.isEmpty() || langList.contains(langCode)) {
                            extractFromZipStream(zis, new File(targetTexts, zipped));
                            langs.add(langCode);
                        }
                    }
                }
                ze = zis.getNextEntry();
            }
            if (!langs.isEmpty()) {
                saveLanguagesJson(targetTexts, langs);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static void saveLanguagesJson(File langDir, Set<String> selectedLangs) throws IOException {
        langDir.mkdirs();
        File langList = new File(langDir, "languages.json");
        FileOutputStream lngJsonFos = new FileOutputStream(langList);
        JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(lngJsonFos, "UTF-8"));
        jsonWriter.setIndent("  ");
        jsonWriter.beginArray();
        for (String langCode : selectedLangs) {
            jsonWriter.value(langCode);
        }
        jsonWriter.endArray();
        jsonWriter.flush();
        jsonWriter.close();
        lngJsonFos.flush();
        lngJsonFos.close();
    }

    private static void extractFromZipStream(ZipInputStream stream, File fileOut) {
        try {
            fileOut.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(fileOut);
            int lenght;
            while ((lenght = stream.read(BUFFER)) > 0) {
                fos.write(BUFFER, 0, lenght);
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean downloadFont() {
        File file = download(Const.FONTPACK_FILENAME, Const.FONT_PROJECT_URL);
        if (file == null) return false;

        File target = new File(DIR, Const.FONTPACK_FILE);

        if (target.exists()) {
            target.delete();
        }
        try {
            copyFile(file, target);
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    public static boolean needDownload(String fileName, String projectUrl) {


        return true;
    }


    public static File download(String fileName, String projectUrl) {
        try {


            SharedPreferences pref = MainActivity.getActivity().getPreferences(Context.MODE_PRIVATE);
            File file = new File(CACHE, fileName);
            if (file.length() > 0) {
                long lastTime = pref.getLong(fileName, 0);
                long updateTime = getResourceUpdate(projectUrl);
                if (updateTime < lastTime) {
                    return file;
                }
            }

            if (file.exists()) {
                file.delete();
            }

            SharedPreferences.Editor edit = pref.edit();
            edit.putLong(fileName, System.currentTimeMillis());
            edit.commit();

            HttpURLConnection connection = openConnections(projectUrl + "/files/latest");
            if (connection == null) {
                return null;
            }
            InputStream inputStream = connection.getInputStream();
            FileOutputStream fileOutput = new FileOutputStream(file);
            int totalSize = connection.getContentLength();
            int downloadedSize = 0;

            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return new File(CACHE, fileName);
    }


    public static HttpURLConnection openConnections(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            int responseCode = huc.getResponseCode(); //get response code
            // huc.setRequestProperty("User-Agent",USER_AGENT);

            while ((responseCode / 100) == 3) { /* codes 3XX are redirections */
                String newLocationHeader = huc.getHeaderField("Location");
                url = new URL(newLocationHeader);
                huc = (HttpURLConnection) url.openConnection();
                responseCode = huc.getResponseCode();
            }

            return huc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void copyFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }

    public static long getResourceUpdate(String urlStr) {
        try {
            URL url = new URL(urlStr);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            String inputLine;
            boolean catchDate = false;
            String dateStr = "";
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.contains("Last Released File")) {
                    catchDate = true;
                }
                if (catchDate && inputLine.contains("data-epoch=")) {
                    Matcher matcher = Const.UNIX_TIME_DIGITS.matcher(inputLine);
                    if (matcher.find()) {
                        dateStr = matcher.group(1);
                        break;
                    }
                }
            }
            in.close();
            if (!dateStr.isEmpty()) {
                return Long.parseLong(dateStr) * 1000;
            }
        } catch (Exception e) {
        }
        return 0L;
    }


    public void unZipLangFile(String zipFile, String outputFolder, Collection<String> langs) {

        byte[] buffer = new byte[1024];

        try {
            File folder = new File(outputFolder);
            if (!folder.exists()) {
                folder.mkdir();
            }

            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {
                String fileName = ze.getName();
                if (ze.isDirectory()) {
                    ze = zis.getNextEntry();
                    continue;
                }

                if (fileName.equalsIgnoreCase("languages.json")) {
                    continue;
                }

                if (fileName.endsWith(".lang") && !langs.isEmpty() && !langs.contains(fileName.replaceAll("\\.lang$", ""))) {
                    continue;
                }
                fileName = new File(fileName).getName();
                File newFile = new File(outputFolder + File.separator + fileName);

                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    private static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        fileOrDirectory.delete();
    }
}
