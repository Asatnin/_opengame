package com.dkondratov.opengame.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberFormattingTextWatcher;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.dkondratov.opengame.R;
import com.dkondratov.opengame.database.DatabaseManager;
import com.dkondratov.opengame.model.AutocompleteAddress;
import com.dkondratov.opengame.model.AutocompleteCity;
import com.dkondratov.opengame.model.Category;
import com.dkondratov.opengame.model.Field;
import com.dkondratov.opengame.network.CreateFieldRequest;
import com.dkondratov.opengame.network.NetworkUtilities;
import com.dkondratov.opengame.network.SearchAddressRequest;
import com.dkondratov.opengame.network.SearchCityRequest;
import com.dkondratov.opengame.network.gson_deserializers.CategoryDeserializer;
import com.dkondratov.opengame.util.ApplicationUserData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.parse.ParsePush;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AddFieldActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private Spinner sport;
    private AutoCompleteTextView city;
    private AutoCompleteTextView address;
    private Spinner cover;
    private EditText maxPlayers;
    private Spinner lightning;
    private Spinner winter;
    private EditText price;
    private EditText time;
    private EditText phone;
    private Switch like;
    private Button button;
    private Context mContext;
    private List<Category> categories;

    private RequestQueue queue;
    private ArrayAdapter<AutocompleteCity> cityAdapter;
    private ArrayAdapter<AutocompleteAddress> addressAdapter;

    private String cityValue;
    private AutocompleteAddress addressValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_field);
        queue = Volley.newRequestQueue(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mContext = this;
        setUpActionBar();
        setUpViews();
        setUpAutoComplete();
        setUpSpinners();
    }

    private void setUpViews() {
        sport = (Spinner) findViewById(R.id.spinner_sports);
        city = (AutoCompleteTextView) findViewById(R.id.edittext_city);
        address = (AutoCompleteTextView) findViewById(R.id.edittext_address);
        cover = (Spinner) findViewById(R.id.spinner_covers);
        maxPlayers = (EditText) findViewById(R.id.edittext_max_players);
        lightning = (Spinner) findViewById(R.id.spinner_lightning);
        winter = (Spinner) findViewById(R.id.spinner_winter);
        price = (EditText) findViewById(R.id.edittext_price);
        time = (EditText) findViewById(R.id.edittext_time);
        phone = (EditText) findViewById(R.id.edittext_time);
        phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        like = (Switch) findViewById(R.id.switch_favourite);
        button = (Button) findViewById(R.id.accept_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(AddFieldActivity.this);
                progressDialog.setMessage("Добавление поля");
                progressDialog.show();
                NetworkUtilities.networkUtilities(AddFieldActivity.this).addToRequestQueue(new CreateFieldRequest(new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        progressDialog.dismiss();
                        final Field fieldObject = (Field) response;
                        ArrayList<Field> field = new ArrayList<Field>();
                        fieldObject.setFavorite(like.isChecked());
                        field.add(fieldObject);
                        if (like.isChecked()) {
                            ParsePush.subscribeInBackground("field_android_" + fieldObject.getFieldId());
                        }
                        try {
                            DatabaseManager.writeFields(field);
                            Intent putIntent = new Intent();
                            putIntent.putParcelableArrayListExtra("new_field", field);
                            AddFieldActivity.this.setResult(RESULT_OK, putIntent);
                            AddFieldActivity.this.finish();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data));
                            Log.e("error", jsonObject.toString());
                            if (jsonObject.has("description")) {
                                Toast.makeText(mContext, jsonObject.optString("description"), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        Toast.makeText(mContext, getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }
                }, getParams()));
            }
        });
    }

    private void setUpSpinners() {
        String[] winterString = new String[] {"Чистится зимой", "Не чистится зимой"};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.my_spinner_item, winterString); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        winter.setAdapter(spinnerArrayAdapter);



        String[] lightningString = new String[] {"Освещается", "Не освещается"};
        ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>(this, R.layout.my_spinner_item, lightningString); //selected item will look like a spinner set from XML
        spinnerArrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lightning.setAdapter(spinnerArrayAdapter1);

        Type collectionType = new TypeToken<List<Category>>(){}.getType();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Category.class, new CategoryDeserializer())
                .create();
        categories = gson.fromJson(ApplicationUserData.loadCategories(mContext).toString(), collectionType);
        String[] sportsString = new String[categories.size()];
        for (int i = 0; i < categories.size(); i++) {
            sportsString[i] = categories.get(i).getName();
        }
        ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(this, R.layout.my_spinner_item, sportsString); //selected item will look like a spinner set from XML
        spinnerArrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sport.setAdapter(spinnerArrayAdapter2);

        Set<String> covers = ApplicationUserData.loadCovers(this);
        String[] coverString = new String[covers.size()];

        Iterator<String> iterator = covers.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            coverString[i] = iterator.next();
            i ++;
        }
        ArrayAdapter<String> spinnerArrayAdapter3 = new ArrayAdapter<String>(this, R.layout.my_spinner_item, coverString); //selected item will look like a spinner set from XML
        spinnerArrayAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cover.setAdapter(spinnerArrayAdapter3);

    }

    private void setUpActionBar() {
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView titleTextView = (TextView) findViewById(R.id.toolbar_title);
        titleTextView.setText(getString(R.string.add_field));
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

    private Map<String, String> getParams() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", "" + ApplicationUserData.loadUserId(mContext));
        map.put("sport_id", "" + categories.get(sport.getSelectedItemPosition()).getSportId());
        map.put("description", "");
        map.put("cover", "" + cover.getSelectedItemPosition());
        map.put("max_user", "" + maxPlayers.getText().toString());
        map.put("lighting", "" + !((String) lightning.getSelectedItem()).contains("Не"));
        map.put("winter", "" + !((String) winter.getSelectedItem()).contains("Не"));
        map.put("price", "" + price.getText().toString());
        map.put("schedule", "" + time.getText().toString());
        map.put("phone", "" + phone.getText().toString());
        map.put("favourite", "" + like.isChecked());
        if (addressValue!=null && addressValue.address!=null && addressValue.coordinates!=null) {
            map.put("city", TextUtils.isEmpty(addressValue.address.city) ? "" : addressValue.address.city);
            map.put("street", TextUtils.isEmpty(addressValue.address.street) ? "" : addressValue.address.street);
            map.put("home", TextUtils.isEmpty(addressValue.address.home) ? "" : addressValue.address.home);
            map.put("lat", String.valueOf(addressValue.coordinates.lat));
            map.put("lon", String.valueOf(addressValue.coordinates.lon));
        }
        Log.e("addfieldparams", map.toString());
        return map;
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

            }
        });
        city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cityValue = parent.getAdapter().getItem(position).toString();
            }
        });

        address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //addressValue = null;
                loadAutocompleteValues(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addressValue = (AutocompleteAddress) parent.getAdapter().getItem(position);
            }
        });
    }

    private void loadCityAutocompleteValues(String name)  {
        try {
            queue.cancelAll(this);
            Request request = new SearchCityRequest(new Response.Listener<List<AutocompleteCity>>() {
                @Override
                public void onResponse(List<AutocompleteCity> response) {
                    cityAdapter = new ArrayAdapter<AutocompleteCity>(AddFieldActivity.this,  android.R.layout.simple_list_item_1, new ArrayList<AutocompleteCity>());
                    city.setAdapter(cityAdapter);
                    cityAdapter.clear();
                    cityAdapter = new ArrayAdapter<AutocompleteCity>(AddFieldActivity.this,  android.R.layout.simple_list_item_1, response);
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
                    addressAdapter = new ArrayAdapter<AutocompleteAddress>(AddFieldActivity.this,  android.R.layout.simple_list_item_1, new ArrayList<AutocompleteAddress>());
                    address.setAdapter(addressAdapter);
                    Log.e("autocomplete response", "success " + response.size());
                    addressAdapter.clear();
                    addressAdapter = new ArrayAdapter<AutocompleteAddress>(AddFieldActivity.this,  android.R.layout.simple_list_item_1, response);
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
        } catch (Exception e)
        {
            Log.e("ERROR", "load auto");
            e.printStackTrace();
        }
    }

}
