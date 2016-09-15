package com.dkondratov.opengame.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.dkondratov.opengame.R;
import com.dkondratov.opengame.database.DatabaseManager;
import com.dkondratov.opengame.fragments.PlaceDetailFragment;
import com.dkondratov.opengame.model.Event;
import com.dkondratov.opengame.model.Field;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.dkondratov.opengame.util.ApplicationUserData.loadUserId;

public class PlaceDetailActivity extends ActionBarActivity implements
        PlaceDetailFragment.PlaceDetailFragmentCallbacks {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupActionBar();
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra("field_id", ((Field) getIntent().getExtras().getParcelable("field")).getFieldId());
        data.putExtra("flag", getIntent().getBooleanExtra("switch", false));
        setResult(RESULT_OK, data);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent data = new Intent();
                data.putExtra("field_id", ((Field) getIntent().getExtras().getParcelable("field")).getFieldId());
                data.putExtra("flag", getIntent().getBooleanExtra("switch", false));
                setResult(RESULT_OK, data);
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
        titleTextView.setText("Площадка");
    }

    @Override
    public void onNewEventButtonClicked(Field field) {
        if (TextUtils.isEmpty(loadUserId(getApplicationContext()))) {
            Toast.makeText(getApplicationContext(), getString(R.string.for_registered_user), Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, NewEventActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("field", field);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

}
