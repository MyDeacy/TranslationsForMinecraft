package ru.fromgate.minecrafttranslator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ru.fromgate.minecrafttranslator.selectlang.SelectLangActivity;

public class MainActivity extends AppCompatActivity {

    private List<LangName> langNames;

    private Set<String> selection;

    public static MainActivity getActivity() {
        return mainActivity;
    }

    private static MainActivity mainActivity;

    public Button buttonStart;

    public Button buttonSelect;

    public CheckBox checkBoxFontCreate;


    DownloadTask task;


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonSelect = (Button) findViewById(R.id.buttonSelect);
        checkBoxFontCreate = (CheckBox) findViewById(R.id.checkBoxFont);


        mainActivity = this;

        selection = null;

        updateLangNames();
    }

    public void clickButtonSelect(View view) {
        buttonsEnable(false);
        Intent intent = new Intent(this, SelectLangActivity.class);
        startActivity(intent);
    }


    public void clickButtonStart(View view) {
        verifyStoragePermissions();
        if (task == null || !task.isActive()) {
            task = new DownloadTask(selection);
            task.execute();
        }
        buttonsEnable(false);
    }

    public void verifyStoragePermissions() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public void setSelection(Set<String> selection) {
        this.selection = selection;
    }


    public Set<String> getSelected() {
        return this.selection;
    }

    private void updateLangNames() {

        Gson gson = new Gson();
        langNames = new ArrayList<>();
        try {
            InputStreamReader is = new InputStreamReader(MainActivity.getActivity().getAssets().open("lang-names.json"), "UTF-8");
            BufferedReader reader = new BufferedReader(is);
            Type langType = new TypeToken<List<LangName>>() {
            }.getType();
            langNames.addAll((List<LangName>) gson.fromJson(reader, langType));
            reader.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("LANGS", "" + langNames.size());
    }

    public void buttonsEnable(boolean state) {
        buttonStart.setEnabled(state);
        buttonSelect.setEnabled(state);
    }

    public Set<String> getLangNames() {
        Set<String> set = new TreeSet<>();
        for (LangName l : langNames) {
            set.add(l.code);
        }
        return set;
    }


    public List<LangName> getLangNamesList() {
        return langNames;
    }

    public void clickSearch() {

    }
}


