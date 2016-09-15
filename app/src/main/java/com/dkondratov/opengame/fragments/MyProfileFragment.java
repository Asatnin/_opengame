package com.dkondratov.opengame.fragments;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.dkondratov.opengame.R;
import com.dkondratov.opengame.activities.MainActivity;
import com.dkondratov.opengame.network.NetworkUtilities;
import com.dkondratov.opengame.network.UserInfoRequest;
import com.dkondratov.opengame.network.UserUpdateRequest;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static com.dkondratov.opengame.util.ApplicationUserData.loadUserName;
import static com.dkondratov.opengame.util.ApplicationUserData.loadUserSurname;
import static com.dkondratov.opengame.util.ApplicationUserData.loadUserAbout;
import static com.dkondratov.opengame.util.ApplicationUserData.loadUserCity;
import static com.dkondratov.opengame.util.ApplicationUserData.loadUserBirthday;
import static com.dkondratov.opengame.util.ApplicationUserData.loadUserPreferredSport;
import static com.dkondratov.opengame.util.ApplicationUserData.loadUserImageUrl;

import static com.dkondratov.opengame.util.ApplicationUserData.saveUserAbout;
import static com.dkondratov.opengame.util.ApplicationUserData.saveUserCity;
import static com.dkondratov.opengame.util.ApplicationUserData.saveUserBirthday;
import static com.dkondratov.opengame.util.ApplicationUserData.saveUserPreferredSport;

import static com.dkondratov.opengame.util.ImageLoaderHelper.imageLoader;
import static com.dkondratov.opengame.network.NetworkUtilities.networkUtilities;

import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.vk.sdk.VKAccessToken;

public class MyProfileFragment extends Fragment implements View.OnClickListener {

    private List<String> sportsNames;
    private View fragmentView;
    private DisplayImageOptions opt = new DisplayImageOptions.Builder()
            .displayer(new RoundedBitmapDisplayer(1000))
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .build();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private TextView about;
    private TextView birthday;
    private TextView city;
    private TextView sports;


    private Button addButton;

    private RequestQueue queue;
    private TextView profileAge;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sportsNames = getArguments().getStringArrayList("sports_names");
        queue = Volley.newRequestQueue(getActivity());
        NetworkUtilities.networkUtilities(getActivity()).addToRequestQueue(new UserInfoRequest(new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                //
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //
            }
        }, getActivity()));

    }

    @Override
    public void onPause() {
        super.onPause();
       updateProfile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_my_profile, container, false);

        setupProfile();
        setupEditableLayouts();
        return fragmentView;
    }

    private void setupProfile() {
        addButton = (Button) fragmentView.findViewById(R.id.add_button);
        profileAge = (TextView) fragmentView.findViewById(R.id.profile_age);
        ImageView profileImage = (ImageView) fragmentView.findViewById(R.id.profile_image);
        TextView profileName = (TextView) fragmentView.findViewById(R.id.profile_name);
        about = (TextView) fragmentView.findViewById(R.id.about_me);
        birthday = (TextView) fragmentView.findViewById(R.id.birthday);
        city = (TextView) fragmentView.findViewById(R.id.city);
        sports = (TextView) fragmentView.findViewById(R.id.sports);

        String imageUrl = loadUserImageUrl(getActivity());
        String name = loadUserName(getActivity());
        String surname = loadUserSurname(getActivity());
        String aboutUser = loadUserAbout(getActivity());
        String birthdayUser = loadUserBirthday(getActivity());
        String userCity = loadUserCity(getActivity());
        String userSports = loadUserPreferredSport(getActivity());

        if (imageUrl == null) {
            profileImage.setImageResource(R.drawable.default_profile);
        } else {
            imageLoader().displayImage(imageUrl, profileImage, opt);
        }

        profileName.setText(name + " " + surname);

        if (aboutUser != null) {
            about.setText(aboutUser);
        }

        String profileInfo = "";

        if (!TextUtils.isEmpty(birthdayUser)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
            Date convertedDate = new Date();
            try {
                convertedDate = dateFormat.parse(birthdayUser);
                System.out.println(convertedDate);

                birthday.setText(dateFormat.format(convertedDate.getTime()));

                Date bd = convertedDate;
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
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        if (userCity != null) {
            city.setText(userCity);
        }

        if (!TextUtils.isEmpty(profileInfo)) {
            profileAge.setText(profileInfo);
            profileAge.setVisibility(View.VISIBLE);
        }

        if (userSports != null) {
            sports.setText(userSports);
        }

        addButton.setText("Выйти");
        addButton.setVisibility(View.VISIBLE);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VKAccessToken.removeTokenAtKey(getActivity(), RegistrationFragment.sTokenKey);
                ((MainActivity)getActivity()).registration();

            }
        });
    }

    private void setupEditableLayouts() {
        RelativeLayout aboutLayout = (RelativeLayout) fragmentView.findViewById(R.id.about_layout);
        RelativeLayout birthdayLayout = (RelativeLayout) fragmentView.findViewById(R.id.birthday_layout);
        RelativeLayout cityLayout = (RelativeLayout) fragmentView.findViewById(R.id.city_layout);
        RelativeLayout sportsLayout = (RelativeLayout) fragmentView.findViewById(R.id.sports_layout);

        aboutLayout.setOnClickListener(this);
        birthdayLayout.setOnClickListener(this);
        cityLayout.setOnClickListener(this);
        sportsLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.about_layout:
                DialogFragment aboutMeDialog = new DialogFragment() {
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        LayoutInflater inflater = getActivity().getLayoutInflater();

                        View aboutEditView = inflater.inflate(R.layout.dialog_about_me, null);
                        final EditText aboutEditText = (EditText) aboutEditView.findViewById(R.id.about_me);
                        builder.setView(aboutEditView)
                                .setTitle(R.string.input_about_me)
                                .setPositiveButton(R.string.ok_word, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        saveUserAbout(getActivity(), aboutEditText.getText().toString());
                                        about.setText(loadUserAbout(getActivity()));
                                        updateProfile();
                                    }
                                })
                                .setNegativeButton(R.string.cancel_word, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });

                        return builder.create();
                    }
                };
                aboutMeDialog.show(getFragmentManager(), "about_me_dialog");
                break;

            case R.id.birthday_layout:
                DatePickerFragment birthdayDialog = new DatePickerFragment() {
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        String birthdayUser = loadUserBirthday(getActivity());
                        if (birthdayUser == null) {
                            final Calendar c = Calendar.getInstance();
                            int year = c.get(Calendar.YEAR);
                            int month = c.get(Calendar.MONTH);
                            int day = c.get(Calendar.DAY_OF_MONTH);

                            // Create a new instance of DatePickerDialog and return it
                            return new DatePickerDialog(getActivity(), this, year, month, day);
                        }

                        Date date = new Date();
                        try{
                            date = MyProfileFragment.this.dateFormat.parse(birthdayUser);
                        } catch (Exception e) { }
                        Calendar c = Calendar.getInstance();
                        c.setTime(date);
                        int year = c.get(Calendar.YEAR);
                        int month = c.get(Calendar.MONTH);
                        int day = c.get(Calendar.DAY_OF_MONTH);

                        return new DatePickerDialog(getActivity(), this, year, month, day);
                    }

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar c = Calendar.getInstance();
                        c.set(year, monthOfYear, dayOfMonth);
                        String date = MyProfileFragment.this.dateFormat.format(c.getTime());
                        saveUserBirthday(getActivity(), date);
                        birthday.setText(loadUserBirthday(getActivity()));
                        updateProfile();
                    }
                };
                birthdayDialog.show(getFragmentManager(), "birthday_dialog");
                break;

            case R.id.city_layout:
                DialogFragment cityDialog = new DialogFragment() {
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        LayoutInflater inflater = getActivity().getLayoutInflater();

                        View cityEditView = inflater.inflate(R.layout.dialog_city, null);
                        final EditText cityEditText = (EditText) cityEditView.findViewById(R.id.city_edit);
                        builder.setView(cityEditView)
                                .setTitle(R.string.choose_city)
                                .setPositiveButton(R.string.ok_word, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        saveUserCity(getActivity(), cityEditText.getText().toString());
                                        city.setText(loadUserCity(getActivity()));
                                        updateProfile();
                                    }
                                })
                                .setNegativeButton(R.string.cancel_word, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });

                        return builder.create();
                    }
                };
                cityDialog.show(getFragmentManager(), "city_dialog");
                break;

            case R.id.sports_layout:
                DialogFragment sportsDialog = new DialogFragment() {
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        CharSequence[] cs = sportsNames.toArray(new CharSequence[sportsNames.size()]);
                        builder.setTitle(R.string.choose_sport)
                                .setItems(cs, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        saveUserPreferredSport(getActivity(), sportsNames.get(which).toString());
                                        sports.setText(loadUserPreferredSport(getActivity()));
                                    }
                                });

                        return builder.create();
                    }
                };
                sportsDialog.show(getFragmentManager(), "sports_dialog");
                break;
        }

    }


    private void updateProfile() {
        networkUtilities(getActivity()).addToRequestQueue(new UserUpdateRequest(new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                Log.d("VolleySuccess", "User update success");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError", "User update error");
            }
        }, getActivity()));
    }

    private static abstract class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener { }
}
