package com.dkondratov.opengame.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dkondratov.opengame.R;
import com.dkondratov.opengame.util.ApplicationUserData;

public class DrawerMenuAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private final Context context;
    private String[] array;

    public DrawerMenuAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        array = context.getResources().getStringArray(R.array.drawer_menu_array);
    }

    @Override
    public int getCount() {
        return array.length;
    }

    @Override
    public String getItem(int position) {
        return array[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = inflater.inflate(R.layout.menu_drawer_item, parent, false);
        TextView count = (TextView) item.findViewById(R.id.count);
        TextView textView = (TextView) item.findViewById(R.id.textViewDrawer);

        textView.setText(array[position]);
        switch(position) {
            case 1:
                int invites = ApplicationUserData.getInvitationNumber(context);
                if (invites>0) {
                    count.setText(String.valueOf(invites));
                    count.setVisibility(View.VISIBLE);
                }
                break;
            case 3:
                int friends = ApplicationUserData.getUserNumber(context);
                if (friends>0) {
                    count.setText(String.valueOf(friends));
                    count.setVisibility(View.VISIBLE);
                }
                break;
        }
        return item;
    }
}
