package com.dkondratov.opengame.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dkondratov.opengame.R;
import com.dkondratov.opengame.database.DatabaseManager;
import com.dkondratov.opengame.fragments.PlaceDetailFragment;
import com.dkondratov.opengame.fragments.PlacesMapFragment;
import com.dkondratov.opengame.model.Field;

import java.sql.SQLException;
import java.util.ArrayList;

public class PlacesActivity extends ActionBarActivity implements
        PlacesMapFragment.PlacesMapFragmentCallbacks {

    private Toolbar toolbar;
    private Boolean hideButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        hideButtons = getIntent().getBooleanExtra("hide_buttons", false);

                ((PlacesMapFragment) getSupportFragmentManager().getFragments().get(0)).getMapAsync((PlacesMapFragment) getSupportFragmentManager().getFragments().get(0));
        setupActionBar();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_right);
        }
        return true;
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView titleTextView = (TextView) findViewById(R.id.toolbar_title);
        titleTextView.setText("Площадки");
    }

    @Override
    public void onPlaceSelected(Field field) {
        Intent intent = new Intent(this, PlaceDetailActivity.class);
        Bundle args = new Bundle();
        try {
            args.putParcelable("field", DatabaseManager.getAllFieldsByFieldId(field.getFieldId()).get(0));
        } catch (SQLException ex) {
            ex.printStackTrace();
            args.putParcelable("field", field);
        }
        intent.putExtras(args);
        startActivityForResult(intent, 399);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

}
