package ru.fromgate.minecrafttranslator.selectlang;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import ru.fromgate.minecrafttranslator.R;

class ViewHolder {
    CheckBox checkBox = null;
    TextView first = null;
    TextView second = null;

    ViewHolder(View row) {
        this.checkBox = (CheckBox) row.findViewById(R.id.checkBox);
        this.first = (TextView) row.findViewById(R.id.nativeName);
        this.second = (TextView) row.findViewById(R.id.textEngName);
    }
}