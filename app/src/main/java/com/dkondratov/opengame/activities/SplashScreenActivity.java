package com.dkondratov.opengame.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dkondratov.opengame.R;
import com.dkondratov.opengame.model.Category;
import com.dkondratov.opengame.network.CategoriesRequest;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dkondratov.opengame.network.CoversRequest;
import com.dkondratov.opengame.network.UserUpdateRequest;
import com.dkondratov.opengame.util.ApplicationUserData;
import com.parse.ParseObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.dkondratov.opengame.network.NetworkUtilities.networkUtilities;
import static com.dkondratov.opengame.database.HelperFactory.getHelper;

public class SplashScreenActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Log.e("user_id", "user_id: " + ApplicationUserData.loadUserId(this));
        // temp clearing
        //clear(this);
        loadCategories();

        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash_screen_activivty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadCategories() {
        networkUtilities(this).addToRequestQueue(new CategoriesRequest(
                new Response.Listener<List<Category>>() {
            @Override
            public void onResponse(List<Category> response) {
                writeCategoriesToDb(response);

                startActivityWithCategories(convertFromListToArrayList(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError", ""+error.getMessage());

                ArrayList<Category> categories = getCategoriesFromDb();
                if (categories == null || categories.isEmpty()) {
                    loadCategories();
                    Toast.makeText(getApplicationContext(), getString(R.string.check_internet), Toast.LENGTH_SHORT);
                } else {
                    startActivityWithCategories(categories);
                }
            }
        }, this));

        networkUtilities(this).addToRequestQueue(new CoversRequest(
                new Response.Listener<List<String>>() {
                    @Override
                    public void onResponse(List<String> response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }, this));
        if (!TextUtils.isEmpty(ApplicationUserData.loadUserId(getApplicationContext()))) {
            Log.e("user id", ApplicationUserData.loadUserId(this));
            networkUtilities(this).addToRequestQueue(new UserUpdateRequest(
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            ApplicationUserData.saveUserId(getApplicationContext(), response);
                            Log.e("response", "save " + response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }, this));
        }
    }

    private void startActivityWithCategories(ArrayList<Category> categories) {
        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
        intent.putParcelableArrayListExtra("categories", categories);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    private ArrayList<Category> convertFromListToArrayList(List<Category> source) {
        return new ArrayList<>(source);
    }

    private void writeCategoriesToDb(List<Category> categories) {
        for (Category category : categories) {
            try {
                getHelper().getCategoryDao().createOrUpdate(category);
            } catch (SQLException e) {
                Log.e("CategoryWriteException", "CategoryWriteException");
            }
        }
    }

    private ArrayList<Category> getCategoriesFromDb() {
        try {
            return convertFromListToArrayList(getHelper().getCategoryDao().getAllCategories());
        } catch (SQLException e) {
            Log.e("CategoryReadException", "CategoryReadException");
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
