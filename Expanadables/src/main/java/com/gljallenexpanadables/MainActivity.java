package com.gljallenexpanadables;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<MenuItem> subItems = new ArrayList<MenuItem>();
        subItems.add(new MenuItem("Cars", staticClickListener));
        subItems.add(new MenuItem("Bikes", staticClickListener));
        subItems.add(new MenuItem("Dealers near me", staticClickListener));

        List<MenuItem> items = new ArrayList<MenuItem>();
        items.add(new MenuItem("Search", true, subItems, expandableClickListener));
        items.add(new MenuItem("My Garage", staticClickListener));
        items.add(new MenuItem("Vehicle Check", staticClickListener));
        items.add(new MenuItem("Settings", staticClickListener));


        ListView listView = (ListView) findViewById(R.id.list);

        listView.setAdapter(new MenuAdapter(this, R.layout.static_menu_item, items));

    }

    private View.OnClickListener staticClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(MainActivity.this, ((TextView)view.findViewById(R.id.title)).getText(), Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener expandableClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            View sublist = findViewById(R.id.sublist);
            switch(sublist.getVisibility()) {
                case View.VISIBLE:
                    close(sublist);
                    break;
                default:
                    open(sublist);
                    break;
            }
        }

        private void open(View view){
            ExpandCollapseAnimation animation = new ExpandCollapseAnimation(view, 500, ExpandCollapseAnimation.EXPAND);
            view.startAnimation(animation);
        }

        private void close(View view){
            ExpandCollapseAnimation animation = new ExpandCollapseAnimation(view, 500, ExpandCollapseAnimation.COLLAPSE);
            view.startAnimation(animation);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    class MenuAdapter extends ArrayAdapter<MenuItem> {

        private Context context;
        private List<MenuItem> items;

        public MenuAdapter(Context context, int textViewResourceId, List<MenuItem> items) {
            super(context, textViewResourceId, items);
            this.context = context;
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            MenuItem item = items.get(position);
            View rowView;
            if(item.isExpandable()) {
                rowView = inflater.inflate(R.layout.expandable_menu_item, parent, false);
                ListView list = (ListView) rowView.findViewById(R.id.sublist);

                MenuAdapter menuAdapter = new MenuAdapter(rowView.getContext(), R.layout.static_menu_item, item.getSubMenu());
                list.setAdapter(menuAdapter);

                // Start of Badness - resizing the cell to include all items
                int totalHeight = 0;
                for(int i=0; i < list.getAdapter().getCount(); i++) {
                    View listItem = list.getAdapter().getView(i, null, list);
                    listItem.measure(0, 0);
                    totalHeight += listItem.getMeasuredHeight();
                }
                ViewGroup.LayoutParams params = list.getLayoutParams();
                params.height = totalHeight + (list.getDividerHeight() * (list.getAdapter().getCount() - 1));
                list.setLayoutParams(params);
                list.requestLayout();
                // End of Badness
            } else {
                rowView = inflater.inflate(R.layout.static_menu_item, parent, false);
            }
            rowView.setOnClickListener(item.getClickListener());
            TextView title = (TextView) rowView.findViewById(R.id.title);
            title.setText(item.getName());
            return rowView;
        }

    }

    class MenuItem {
        private final String name;
        private final boolean expandable;
        private final List<MenuItem> subMenu;
        private final View.OnClickListener clickListener;

        MenuItem(String name, View.OnClickListener clickListener) {
            this.name = name;
            this.clickListener = clickListener;
            this.expandable = false;
            subMenu = null;
        }

        MenuItem(String name, boolean expandable, List<MenuItem> subMenu, View.OnClickListener clickListener) {
            this.name = name;
            this.expandable = expandable;
            this.subMenu = subMenu;
            this.clickListener = clickListener;
        }

        String getName() {
            return name;
        }

        boolean isExpandable() {
            return expandable;
        }

        List<MenuItem> getSubMenu() {
            return subMenu;
        }

        View.OnClickListener getClickListener() {
            return clickListener;
        }
    }
    
}
