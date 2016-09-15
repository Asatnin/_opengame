package com.dkondratov.opengame.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.dkondratov.opengame.R;
import com.dkondratov.opengame.model.AutocompleteAddress;
import com.dkondratov.opengame.model.AutocompleteCity;
import com.dkondratov.opengame.model.Category;
import com.dkondratov.opengame.model.Cover;
import com.dkondratov.opengame.model.Field;
import com.dkondratov.opengame.network.FilterSportPlacesByAddressRequest;
import com.dkondratov.opengame.network.FilterSportPlacesByParamsRequest;
import com.dkondratov.opengame.network.SearchAddressRequest;
import com.dkondratov.opengame.network.SearchCityRequest;
import com.dkondratov.opengame.util.ApplicationUserData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.dkondratov.opengame.database.HelperFactory.getHelper;

public class SearchActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private AutoCompleteTextView city;
    private AutoCompleteTextView address;
    private Spinner sportType;
    private Spinner pokritie;
    private Button search;

    private RequestQueue queue;
    private ArrayAdapter<AutocompleteCity> cityAdapter;
    private ArrayAdapter<AutocompleteAddress> addressAdapter;

    private String cityValue;
    private String addressValue;
    private String homeValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        city = (AutoCompleteTextView)findViewById(R.id.city);
        address = (AutoCompleteTextView)findViewById(R.id.address);
        sportType = (Spinner) findViewById(R.id.sport_type);
        pokritie = (Spinner) findViewById(R.id.pokrytie);
        search = (Button) findViewById(R.id.search);

        queue = Volley.newRequestQueue(this);

        setupActionBar();
        setUpAutoComplete();
        setUpSpinners();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndSend();
            }
        });
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView titleTextView = (TextView) findViewById(R.id.toolbar_title);
        titleTextView.setText("Поиск");
    }

    private void setUpAutoComplete() {
        city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cityValue = s.toString();
                loadCityAutocompleteValues(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                cityValue = s.toString();
                Log.e("city", s.toString());
            }
        });
        city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cityValue = ((AutocompleteCity) parent.getAdapter().getItem(position)).address.city;
            }
        });

        address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addressValue = null;
                homeValue = null;
                loadAutocompleteValues(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addressValue = ((AutocompleteAddress) parent.getAdapter().getItem(position)).address.street;
                homeValue = ((AutocompleteAddress) parent.getAdapter().getItem(position)).address.home;
            }
        });
    }
    private void loadCityAutocompleteValues(String name)  {
        try {
            queue.cancelAll(this);
            Request request = new SearchCityRequest(new Response.Listener<List<AutocompleteCity>>() {
                @Override
                public void onResponse(List<AutocompleteCity> response) {
                    Log.e("autocomplete response", "success " + response.size());
                    if (cityAdapter!=null)
                        cityAdapter.clear();
                    cityAdapter = new ArrayAdapter<AutocompleteCity>(SearchActivity.this,  android.R.layout.simple_list_item_1, response);
                    cityAdapter.notifyDataSetChanged();
                    city.setAdapter(cityAdapter);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("ERROR", "autocomplete");
                    error.printStackTrace();
                }
            }, name);
            request.setTag(this);
            queue.add(request);
        } catch (Exception e)
        {
            Log.e("ERROR", "load auto");
            e.printStackTrace();
        }

    }
    private void loadAutocompleteValues(String name)  {
        try {
            queue.cancelAll(this);
            Request request = new SearchAddressRequest(new Response.Listener<List<AutocompleteAddress>>() {
                @Override
                public void onResponse(List<AutocompleteAddress> response) {
                    Log.e("autocomplete response", "success " + response.size());
                    if (addressAdapter!=null)
                        addressAdapter.clear();
                    addressAdapter = new ArrayAdapter<AutocompleteAddress>(SearchActivity.this,  android.R.layout.simple_list_item_1, response);
                    addressAdapter.notifyDataSetChanged();
                    address.setAdapter(addressAdapter);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("ERROR", "autocomplete");
                    error.printStackTrace();
                }
            }, TextUtils.isEmpty(cityValue) ? "" : cityValue, name);
            request.setTag(this);
            queue.add(request);
        } catch (Exception e) {
            Log.e("ERROR", "load auto");
            e.printStackTrace();
        }
    }
    private void setUpSpinners() {
        try {
            ArrayList<Category> list = new ArrayList<>(getHelper().getCategoryDao().getAllCategories());
            ArrayAdapter<Category> spinnerArrayAdapter1 = new ArrayAdapter<Category>(this, R.layout.my_spinner_item, list); //selected item will look like a spinner set from XML
            spinnerArrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sportType.setAdapter(spinnerArrayAdapter1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        List<Cover> covers = ApplicationUserData.loadCoversJSON(this);
        ArrayAdapter<Cover> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.my_spinner_item, covers); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pokritie.setAdapter(spinnerArrayAdapter);


    }
    private ArrayList<Category> getCategoriesFromDb() {
        try {
            return convertFromListToArrayList(getHelper().getCategoryDao().getAllCategories());
        } catch (SQLException e) {
            Log.e("CategoryReadException", "CategoryReadException");
            return null;
        }
    }
    private ArrayList<Category> convertFromListToArrayList(List<Category> source) {
        return new ArrayList<>(source);
    }
    private void checkAndSend() {
        queue.cancelAll(this);
        if (TextUtils.isEmpty(cityValue) || TextUtils.isEmpty(addressValue)) {
            Request request = new FilterSportPlacesByParamsRequest(new Response.Listener<List<Field>>() {
                @Override
                public void onResponse(List<Field> response) {
                    Log.e("fields", String.valueOf(response.size()));
                    Intent intent = new Intent(SearchActivity.this, PlacesActivity.class);
                    intent.putExtra("search", true);
                    Bundle args = new Bundle();
                    ArrayList<Field> fieldsList = new ArrayList<>();
                    fieldsList.addAll(response);
                    Log.e("fields", "fields size: " + fieldsList.size());
                    args.putParcelableArrayList("fields", fieldsList);
                    intent.putExtras(args);
                    intent.putExtra("hide_buttons", true);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(SearchActivity.this, "Ошибка, проверьте соединение с интерентом", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
            }, "" + ((Cover)pokritie.getSelectedItem()).getId(), "" + ((Category)sportType.getSelectedItem()).getSportId());
            // TODO - in request remove item positions



            request.setTag(this);
            queue.add(request);
        } else {
            Request request = new FilterSportPlacesByAddressRequest(new Response.Listener<List<Field>>() {
                @Override
                public void onResponse(List<Field> response) {
                    Log.e("fields", String.valueOf(response.size()));
                    Intent intent = new Intent(SearchActivity.this, PlacesActivity.class);
                    Bundle args = new Bundle();
                    ArrayList<Field> fieldsList = new ArrayList<>();
                    fieldsList.addAll(response);
                    Log.e("fields", "fields size: " + fieldsList.size());
                    args.putParcelableArrayList("fields", fieldsList);
                    intent.putExtras(args);
                    intent.putExtra("hide_buttons", true);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(SearchActivity.this, "Ошибка, проверьте соединение с интерентом", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
            }, cityValue, addressValue, homeValue);
            request.setTag(this);
            queue.add(request);
        }
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
        }
        return true;
    }

}
