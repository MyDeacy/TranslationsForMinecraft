package ru.fromgate.minecrafttranslator.selectlang;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ru.fromgate.minecrafttranslator.LangName;
import ru.fromgate.minecrafttranslator.MainActivity;
import ru.fromgate.minecrafttranslator.R;


public class SelangAdapter extends BaseAdapter {

    List<LangName> langNames;
    Set<Integer> checked;
    String lastSearch;
    int lastJump;

    private SelectLangActivity activity;

    public SelangAdapter(SelectLangActivity activity) {
        this.activity = activity;
        lastSearch = "";
        lastJump = -1;
        checked = new HashSet<>();
        resetLangs();
        selectPrevious();
    }


    public void resetLangs() {
        langNames = new ArrayList<>();
        langNames.addAll(MainActivity.getActivity().getLangNamesList());
    }

    public void selectPrevious() {
        Set<String> selected = MainActivity.getActivity().getSelected();
        if (selected != null && !selected.isEmpty()) {
            for (int i = 0; i < langNames.size(); i++) {
                LangName l = langNames.get(i);
                if (selected.contains(l.code)) {
                    setItemChecked(i, true);
                }
            }
            notifyDataSetChanged();
        }
    }


    @Override
    public int getCount() {
        return langNames.size();
    }

    @Override
    public LangName getItem(int position) {
        return langNames.size() > position ? langNames.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            row = activity.getLayoutInflater().inflate(R.layout.checklist, parent, false);
        }

        ViewHolder holder = (ViewHolder) row.getTag();
        if (holder == null) {
            holder = new ViewHolder(row);
            row.setTag(holder);
        }

        LangName langName = getItem(position);

        holder.first.setText(langName.nativeName);
        holder.second.setText(langName.name);

        holder.checkBox.setChecked(checked.contains(position));

        return row;
    }

    public int findNext(String search) {
        if (!lastSearch.equalsIgnoreCase(search)) {
            lastJump = -1;
        }

        List<Integer> found = new ArrayList<>();

        for (int i = 0; i < langNames.size(); i++) {
            if (langNames.get(i).find(search)) {
                found.add(i);
            }
        }

        if (found.isEmpty()) {
            lastJump = -1;
        } else {

            if (lastJump >= found.get(found.size() - 1)) {
                lastJump = 0;
            } else {
                for (int i : found) {
                    if (lastJump < i) {
                        lastJump = i;
                        break;
                    }
                }
            }

        }
        if (lastJump > 0) {
            lastSearch = search;
        } else {
            lastSearch = "";
        }
        return lastJump;
    }

    public void selectAll() {
        boolean check = langNames.size() != checked.size();
        for (int i = 0; i < langNames.size(); i++) {
            setItemChecked(i, check);
        }
        notifyDataSetChanged();
    }

    public void setItemChecked(int position, boolean value) {
        if (value) {
            checked.add(position);
        } else {
            if (checked.contains(position)) {
                checked.remove(position);
            }
        }
    }


    public View getViewByPosition(int pos) {
        ListView listView = activity.listView;
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    public void updateCheckedLangs() {
        for (int i = 0; i < langNames.size(); i++) {
            View view = getViewByPosition(i);
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder == null) continue;
            setItemChecked(i, holder.checkBox.isChecked());
        }
    }

    public Set<String> getSelection() {
        Set<String> selection = new TreeSet<>();
        for (int i : checked) {
            selection.add(langNames.get(i).code);
        }
        return selection;
    }

    public int getSelectionCount() {
        return checked.size();
    }
}
