package com.dkondratov.opengame.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.dkondratov.opengame.R;
import com.dkondratov.opengame.model.Field;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import java.util.List;

public class GoogleMapPopupAdapter implements GoogleMap.InfoWindowAdapter {

    private LayoutInflater inflater;
    private List<Field> fields;

    public GoogleMapPopupAdapter(LayoutInflater inflater, List<Field> fields) {
        this.inflater = inflater;
        this.fields = fields;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getInfoContents(Marker marker) {
        int i = Integer.parseInt(marker.getSnippet());

        Field field = fields.get(i);

        View view = inflater.inflate(R.layout.map_marker_item, null);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(field.getName());

        TextView address = (TextView) view.findViewById(R.id.address);
        address.setText(field.getAddress());

        TextView players = (TextView) view.findViewById(R.id.players);
        players.setText("Максимальное число игроков: " + field.getMax_users());

        return view;
    }

}
