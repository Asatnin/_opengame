package com.dkondratov.opengame.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.dkondratov.opengame.R;
import com.dkondratov.opengame.activities.SearchActivity;
import com.dkondratov.opengame.activities.AddFieldActivity;
import com.dkondratov.opengame.adapters.GoogleMapPopupAdapter;
import com.dkondratov.opengame.model.Field;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dkondratov.opengame.util.ApplicationUserData.loadUserId;

/**
 * Created by andrew on 11.04.2015.
 */
public class PlacesMapFragment extends SupportMapFragment implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener {

    private final static double mLat = 55.7484394;
    private final static double mLon = 37.5250349;

    private ArrayList<Field> fields;
    private FloatingActionButton searchButton;
    private FloatingActionButton addButton;
    private PlacesMapFragmentCallbacks mCallbacks;
    private Map<String, Integer> markerFieldMap = new HashMap<>();
    private boolean hideFlag;
    private boolean searchFlag;

    public PlacesMapFragment() { }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (PlacesMapFragmentCallbacks) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fields = getActivity().getIntent().getExtras().getParcelableArrayList("fields");
        hideFlag = getActivity().getIntent().getExtras().getBoolean("hide", false);
        searchFlag = getActivity().getIntent().getExtras().getBoolean("search", false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            if (field.getLat() == null || field.getLon() == null) {
                continue;
            }
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .snippet("" + i)
                    .position(new LatLng(field.getLat(), field.getLon()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_map)));

            markerFieldMap.put(marker.getId(), i);
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLat, mLon)));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(10));

        googleMap.setOnInfoWindowClickListener(this);
        googleMap.setInfoWindowAdapter(new GoogleMapPopupAdapter(getActivity().getLayoutInflater(), fields));
        
        googleMap.setMyLocationEnabled(true);
        final GoogleMap mapLocal = googleMap;
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                try {
                    Location location = mapLocal.getMyLocation();
                    final LatLng now = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraUpdate ownLocation = CameraUpdateFactory.newLatLngZoom(now, 10);
                    mapLocal.animateCamera(ownLocation);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return true;
            }
        });

        addButton = (FloatingActionButton) getActivity().findViewById(R.id.plus_button);
        searchButton = (FloatingActionButton) getActivity().findViewById(R.id.search_button);
        if (!(addButton == null || searchButton == null)) {
            if (hideFlag) {
                addButton.setVisibility(View.GONE);
                searchButton.setVisibility(View.GONE);
            } else {
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(loadUserId(getActivity()))) {
                            Toast.makeText(getActivity(), getString(R.string.for_registered_user), Toast.LENGTH_LONG).show();
                            return;
                        }
                        Intent newIntent = new Intent(getActivity(), AddFieldActivity.class);
                        startActivityForResult(newIntent, 101);
                    }
                });
                searchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), SearchActivity.class);
                        getActivity().startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

                    }
                });
            }
        }
        if (searchFlag) {
            addButton.setVisibility(View.GONE);
            searchButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        mCallbacks.onPlaceSelected(fields.get(markerFieldMap.get(marker.getId())));
    }

    public interface PlacesMapFragmentCallbacks {
        void onPlaceSelected(Field field);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (data != null) {
                ArrayList<Field> newField = data.getParcelableArrayListExtra("new_field");
                if (newField != null && newField.size() > 0) {
                    fields.addAll(newField);
                    this.onMapReady(getMap());
                }
            }
        }
    }
}
