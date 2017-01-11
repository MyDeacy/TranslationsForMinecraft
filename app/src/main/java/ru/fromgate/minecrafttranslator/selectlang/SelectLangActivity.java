package ru.fromgate.minecrafttranslator.selectlang;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import ru.fromgate.minecrafttranslator.MainActivity;
import ru.fromgate.minecrafttranslator.R;

public class SelectLangActivity extends AppCompatActivity {

    public EditText textSearch;
    public ListView listView;
    SelangAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);

        textSearch = (EditText) findViewById(R.id.editLangSearch);

        listView = (ListView) findViewById(R.id.langList);

        adapter = new SelangAdapter(this);

        listView.setAdapter(adapter);
    }


    public void clickSearch(View view) {
        int jumpTo = adapter.findNext(textSearch.getText().toString());
        if (jumpTo > 0) {
            listView.smoothScrollToPositionFromTop(jumpTo, 0, 200);
        } else {
            Toast.makeText(this, getResources().getString(R.string.select_toast_searh_fail), Toast.LENGTH_SHORT).show();
        }
    }

    public void clickSelectAll(View view) {
        adapter.selectAll();
    }

    public void clickCancel(View view) {
        MainActivity.getActivity().buttonsEnable(true);
        this.finish();
    }

    public void clickOk(View view) {
        MainActivity.getActivity().setSelection(adapter.getSelection());

        MainActivity.getActivity().buttonsEnable(true);
        this.finish();

        Toast.makeText(MainActivity.getActivity(),
                MainActivity.getActivity().getResources().getString(R.string.select_lang_toast)
                        .replace("%1%", Integer.toString(adapter.getSelectionCount())),
                Toast.LENGTH_SHORT).show();

    }


    public void checkLangClick(View view) {
        adapter.updateCheckedLangs();
    }


    @Override
    public void onBackPressed() {
        MainActivity.getActivity().buttonsEnable(true);
        super.onBackPressed();
    }
}
