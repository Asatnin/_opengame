package com.dkondratov.opengame.activities;

import android.app.ProgressDialog;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.dkondratov.opengame.R;
import com.dkondratov.opengame.model.Sport;
import com.dkondratov.opengame.model.User;
import com.dkondratov.opengame.network.AcceptFriendsRequest;
import com.dkondratov.opengame.network.AddUserToFriendsRequest;
import com.dkondratov.opengame.util.ApplicationUserData;
import com.dkondratov.opengame.util.UsersData;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.parse.codec.binary.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.dkondratov.opengame.util.ImageLoaderHelper.imageLoader;

public class UserActivity extends ActionBarActivity
        implements View.OnClickListener, Response.ErrorListener, Response.Listener {

    private ImageView profileImage;
    private TextView profileAge;
    private TextView profileName;
    private TextView about;
    private TextView birthday;
    private TextView city;
    private TextView sports;
    private Button addButton;

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static DisplayImageOptions opt = new DisplayImageOptions.Builder()
            .displayer(new RoundedBitmapDisplayer(1000))
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .build();

    private User user;
    private RequestQueue queue;
    private ProgressDialog dialog;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        toolbar = (Toolbar) findViewById(R.id.toolbar);


        profileImage = (ImageView) findViewById(R.id.profile_image);
        profileAge = (TextView) findViewById(R.id.profile_age);
        profileName = (TextView) findViewById(R.id.profile_name);
        about = (TextView) findViewById(R.id.about_me);
        birthday = (TextView) findViewById(R.id.birthday);
        city = (TextView) findViewById(R.id.city);
        sports = (TextView) findViewById(R.id.sports);
        addButton = (Button) findViewById(R.id.add_button);

        queue = Volley.newRequestQueue(this);
        dialog = new ProgressDialog(this);

        user = getIntent().getParcelableExtra("user");

        String profileInfo = "";
        if (user.getImageUrl() == null) {
            profileImage.setImageResource(R.drawable.default_profile);
        } else {
            imageLoader().displayImage(user.getImageUrl(), profileImage, opt);
        }

        profileName.setText(user.getName() + " " + user.getSurname());

        if (user.getDescription() != null) {
            about.setText(user.getDescription());
        }

        if (user.getBirth() != null) {
            birthday.setText(dateFormat.format(Long.parseLong(user.getBirth())*1000));

            Date bd = new Date(Long.parseLong(user.getBirth())*1000);
            Calendar dateOfYourBirth = new GregorianCalendar(bd.getYear(), bd.getMonth(),bd.getDay());
            Calendar today = Calendar.getInstance();
            Log.e("today year",""+today.get(Calendar.YEAR));
            Log.e("birthday year",""+dateOfYourBirth.get(Calendar.YEAR));
            int age = today.get(Calendar.YEAR) - (dateOfYourBirth.get(Calendar.YEAR)+1900);
            dateOfYourBirth.add(Calendar.YEAR, age);
            if (today.before(dateOfYourBirth)) {
                age--;
            }

            String result= "";
            if(age % 10 == 0){
                result = "лет";
            }

            if(age % 10 == 1){
                result = "год";
            }

            if(age % 10 >= 2 && age % 10 <= 4){
                result = "года";
            }

            if(age % 10 >= 5 && age % 10 <= 9){
                result = "лет";
            }

            if(age >= 11 && age <= 20){
                result = "лет";
            }

            profileInfo = age + " "+result;
        }


        if (user.getCity() != null) {
            city.setText(user.getCity());
            profileInfo += (TextUtils.isEmpty(profileInfo) ? "": ", ") + user.getCity();
        }

        if (!TextUtils.isEmpty(profileInfo)) {
            profileAge.setText(profileInfo);
            profileAge.setVisibility(View.VISIBLE);
        }

        if (!user.getUserId().equals(ApplicationUserData.loadUserId(this)))
            if (UsersData.getFollowers(this).contains(user)){
                addButton.setVisibility(View.VISIBLE);
                addButton.setText("Подтвердить запрос");
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        queue.cancelAll(this);
                        dialog.setMessage("Подтверждение запроса на добавление в друзья...");
                        dialog.show();
                        AcceptFriendsRequest request = new AcceptFriendsRequest(UserActivity.this, UserActivity.this, UserActivity.this, ApplicationUserData.loadUserId(UserActivity.this), user.getUserId());
                        request.setTag(this);
                        queue.add(request);
                    }
                });
            }
            else if(!UsersData.getFriends(this).contains(user)) {
                addButton.setVisibility(View.VISIBLE);
                addButton.setOnClickListener(this);
        }

        StringBuilder sb = new StringBuilder();
        for(Sport s: user.getSports()) {
            sb.append(" "+s.getName()).append(',');
        }
        if (!TextUtils.isEmpty(sb.toString().trim())) {
            sb.deleteCharAt(sb.length() - 1); //delete last comma
            String newString = sb.toString();
            if (!TextUtils.isEmpty(newString.trim()))
                sports.setText(newString.trim());
        }

        //if (userSports != null) {
        //    sports.setText(userSports);
        //}
        setupActionBar();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        queue.cancelAll(this);
        dialog.setMessage("Отправка запроса на добавление в друзья...");
        dialog.show();
        AddUserToFriendsRequest request = new AddUserToFriendsRequest(this, this, this, ApplicationUserData.loadUserId(this), user.getUserId());
        request.setTag(this);
        queue.add(request);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        dialog.hide();
        Toast.makeText(this, "Ошибка запроса", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(Object response) {
        dialog.hide();
        Toast.makeText(this, "Успех!", Toast.LENGTH_LONG).show();
        addButton.setVisibility(View.GONE);
        // TODO push channel "friend_android_"
    }

    @Override
    protected void onDestroy() {
        queue.cancelAll(this);
        dialog.dismiss();
        super.onDestroy();
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView titleTextView = (TextView) findViewById(R.id.toolbar_title);
        titleTextView.setText(user.toString());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_right);
    }
}

