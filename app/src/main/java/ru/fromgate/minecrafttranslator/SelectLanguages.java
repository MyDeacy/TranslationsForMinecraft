package ru.fromgate.minecrafttranslator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public class SelectLanguages extends AlertDialog.Builder {

    // "кликнутые значения", 0-й элемент - это "Select all"
    private boolean[] values;
    // Изначальные коды языков (ru_RU и т.п.).
    private String[] langCodes;


    protected SelectLanguages() {
        super(MainActivity.getActivity());

        this.setTitle(R.string.button_select_lang);

        Set<String> langList = new TreeSet<>();
        langList.addAll(MainActivity.getActivity().getLangNames());

        Set<String> selected = MainActivity.getActivity().getSelected();

        if (selected == null) {
            selected = new HashSet<>();
            String sysLang = Locale.getDefault().toString();
            if (langList.contains(sysLang)) {
                selected.add(sysLang);
            }
        }

        langCodes = new String[langList.size()];
        String[] langs = new String[langList.size() + 1];
        langs[0] = MainActivity.getActivity().getString(R.string.select_all);
        int i = 0;
        for (String lang : langList) {
            langs[i + 1] = lang; ///Html.fromHtml("<b>"+MainActivity.getActivity().getLangName(lang).nativeName+"</b>\n"+MainActivity.getActivity().getLangName(lang).name).toString();
            langCodes[i] = lang;
            i++;
        }


        values = new boolean[langs.length];
        for (i = 0; i < langs.length; i++) {
            values[i] = i == 0 ? false : selected.contains(langCodes[i - 1]);
        }

        this.setMultiChoiceItems(langs, values, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                AlertDialog aDialog = (AlertDialog) dialog;
                ListView listView = aDialog.getListView();

                if (which == 0) {
                    values[0] = false;
                    for (int i = 1; i < values.length; i++) {
                        aDialog.getListView().setItemChecked(i, listView.isItemChecked(0));
                        values[i] = listView.isItemChecked(0);
                    }
                } else {
                    listView.setItemChecked(0, false);
                }
            }
        });

        this.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog aDialog = (AlertDialog) dialog;
                Set<String> sel = new HashSet<>();
                for (int i = 1; i < values.length; i++) {
                    if (values[i]) {
                        sel.add(langCodes[i - 1]);
                    }
                }
                MainActivity.getActivity().setSelection(sel);
                String selected = MainActivity.getActivity().getResources()
                        .getString(R.string.select_lang_toast).replace("%1%", String.valueOf(sel.size()));
                Toast.makeText(MainActivity.getActivity(), selected, Toast.LENGTH_SHORT).show();
                aDialog.dismiss();
                MainActivity.getActivity().buttonsEnable(true);
            }
        });

        this.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                MainActivity.getActivity().buttonsEnable(true);
            }
        });


    }


}
