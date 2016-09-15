package com.dkondratov.opengame.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dkondratov.opengame.R;
import com.dkondratov.opengame.database.DatabaseManager;
import com.dkondratov.opengame.fragments.CalendarFragment;
import com.dkondratov.opengame.fragments.CategoriesFragment;
import com.dkondratov.opengame.fragments.FriendsPageFragment;
import com.dkondratov.opengame.fragments.FriendProfileFragment;
import com.dkondratov.opengame.fragments.FriendsFragment;
import com.dkondratov.opengame.fragments.InvitesFragment;
import com.dkondratov.opengame.fragments.MyProfileFragment;
import com.dkondratov.opengame.fragments.NavigationDrawerFragment;
import com.dkondratov.opengame.fragments.PlacesMapFragment;
import com.dkondratov.opengame.fragments.RegistrationFragment;
import com.dkondratov.opengame.fragments.SettingsFragment;
import com.dkondratov.opengame.model.Category;
import com.dkondratov.opengame.model.Event;
import com.dkondratov.opengame.model.Field;
import com.dkondratov.opengame.model.User;
import com.dkondratov.opengame.network.FieldsRequest;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dkondratov.opengame.network.NetworkUtilities;
import com.dkondratov.opengame.network.NewEventRequest;
import com.dkondratov.opengame.network.UserInfoRequest;
import com.parse.ParsePush;
import com.vk.sdk.util.VKUtil;

import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.dkondratov.opengame.network.NetworkUtilities.networkUtilities;
import static com.dkondratov.opengame.util.ApplicationUserData.loadUserId;

import static com.dkondratov.opengame.database.DatabaseManager.writeFields;
import static com.dkondratov.opengame.database.DatabaseManager.getAllFieldsBySportId;

public class MainActivity extends ActionBarActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        FragmentManager.OnBackStackChangedListener,
        CategoriesFragment.CategoriesFragmentCallbacks,
        FriendsPageFragment.FriendPageFragmentCallbacks,
        RegistrationFragment.RegistrationFragmentCallbacks,
        PlacesMapFragment.PlacesMapFragmentCallbacks {

    private FrameLayout mainScreenFrame;
    private FragmentTransaction fragmentTransaction;
    private TextView toolbarTitle;
    private Toolbar toolbar;
    private Fragment pendingFragment;
    private Fragment[] menuFragments = new Fragment[7];
    private String [] arrayStrings;
    private RegistrationFragment registrationFragment;
    private MyProfileFragment myProfileFragment;
    private Context mContext;

    private DrawerLayout drawerLayout;
    private NavigationDrawerFragment navigationDrawerFragment;
    private View mFragmentContainerView;
    private ActionBarDrawerToggle mToggle;

    private ArrayList<String> sportsNames;

    @Override
    protected void onResume() {
        super.onResume();
        mContext = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ParsePush.subscribeInBackground("test");
        mContext = this;
        String[] fingerprints = VKUtil.getCertificateFingerprint(this, getPackageName());
        for (String r: fingerprints)
            Log.e("fingerprint",r);
        arrayStrings = getResources().getStringArray(R.array.drawer_menu_array);
        mainScreenFrame = (FrameLayout) findViewById(R.id.main_screen_frame);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_fragment);
        navigationDrawerFragment.setUp(R.id.navigation_fragment, drawerLayout, toolbar);
        mFragmentContainerView = findViewById(R.id.navigation_fragment);
        mToggle = navigationDrawerFragment.getmToggle();

        setupActionBar();
        setupMainFrame();

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        shouldDisplayHomeUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    drawerLayout.openDrawer(mFragmentContainerView);
                } else {
                    getSupportFragmentManager().popBackStack();
                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addBackStackFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_screen_frame, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Initializing UI
     */
    private void setupActionBar() {
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setToolbarTitle("Поиск");
    }

    private void setupMainFrame() {
        menuFragments[0] = new CategoriesFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("categories", getIntent().getParcelableArrayListExtra("categories"));
        collectSportsNames();
        menuFragments[0].setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_screen_frame, menuFragments[0])
                .commit();
    }

    private void collectSportsNames() {
        List<Category> categories = getIntent().getParcelableArrayListExtra("categories");
        sportsNames = new ArrayList<>();
        for (Category category : categories) {
            sportsNames.add(category.getName());
        }
    }


    public void setToolbarTitle(String title) {
        toolbarTitle.setText(title);
    }

    /**
     * NavigationDrawerFragment.NavigationDrawerCallbacks implementation
     */
    @Override
    public void onNavigationDrawerItemSelected(final int position) {
        pendingFragment = null;
        switch (position) {
            case 0: // Поиск площадки
                if (menuFragments[position] == null) {
                    menuFragments[position] = new CategoriesFragment();
                }
                break;
            case 1: // Приглашения
                if (TextUtils.isEmpty(loadUserId(this))) {
                    new DialogFragment() {
                        @Override
                        public Dialog onCreateDialog(Bundle savedInstanceState) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage(R.string.for_registered_user)
                                    .setPositiveButton(R.string.register_me, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            onProfilePageSelected();
                                        }
                                    })
                                    .setNegativeButton(R.string.ok_word, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                            return builder.create();
                        }
                    }.show(getSupportFragmentManager(), "register_dialog");
                    return;
                }
                if (menuFragments[position] == null) {
                    menuFragments[position] = new InvitesFragment();
                }
                break;
            case 2: // Избранное

                PlacesMapFragment placesMapFragment = new PlacesMapFragment();
                ArrayList<Field> favoriteFields = new ArrayList<>();
                try {
                    favoriteFields.addAll(DatabaseManager.getFavoriteFields());
                } catch (SQLException e) {
                    Log.e("ReadFieldException", "ReadFavoriteFieldsException");
                }
              //  Bundle args = new Bundle();
                getIntent().putParcelableArrayListExtra("fields", favoriteFields);
                //placesMapFragment.setArguments(args);
                pendingFragment = placesMapFragment;
                placesMapFragment.getMapAsync(placesMapFragment);

                toolbarTitle.setText(arrayStrings[position]);
                return;

            case 3: // Мои друзья
                if (TextUtils.isEmpty(loadUserId(this))) {
                    new DialogFragment() {
                        @Override
                        public Dialog onCreateDialog(Bundle savedInstanceState) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage(R.string.for_registered_user)
                                    .setPositiveButton(R.string.register_me, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            onProfilePageSelected();
                                        }
                                    })
                                    .setNegativeButton(R.string.ok_word, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                            return builder.create();
                        }
                    }.show(getSupportFragmentManager(), "register_dialog");
                    return;
                }
                if (menuFragments[position] == null) {
                    menuFragments[position] = new FriendsFragment();
                }
                break;
            case 4:
                if (TextUtils.isEmpty(loadUserId(this))) {
                    new DialogFragment() {
                        @Override
                        public Dialog onCreateDialog(Bundle savedInstanceState) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage(R.string.for_registered_user)
                                    .setPositiveButton(R.string.register_me, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            onProfilePageSelected();
                                        }
                                    })
                                    .setNegativeButton(R.string.ok_word, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                            return builder.create();
                        }
                    }.show(getSupportFragmentManager(), "register_dialog");
                    return;
                }
                menuFragments[position] = new CalendarFragment();
                break;
            case 5:
                menuFragments[position] = new SettingsFragment();
                break;
            case 6:
                writeToUs();
                return;
        }
        toolbarTitle.setText(arrayStrings[position]);
        pendingFragment = menuFragments[position];
    }

    public void registration() {
        registrationFragment = new RegistrationFragment();
        toolbarTitle.setText("Ваш профиль");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_screen_frame, registrationFragment)
                .commit();
    }

    @Override
    public void onProfilePageSelected() {
        String userId = loadUserId(this);
        if (TextUtils.isEmpty(userId)) { // экран регистрации
            registrationFragment = new RegistrationFragment();
            pendingFragment = registrationFragment;
        } else { // экран профиля
            myProfileFragment = new MyProfileFragment();
            Bundle args = new Bundle();
            args.putStringArrayList("sports_names", sportsNames);
            myProfileFragment.setArguments(args);
            pendingFragment = myProfileFragment;
        }
        toolbarTitle.setText("Ваш профиль");
    }

    @Override
    public void onDrawerClosed() {
        if (pendingFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_screen_frame, pendingFragment)
                    .commit();
        }
        invalidateOptionsMenu();
    }

    @Override
    public void onDrawerOpened() {
        invalidateOptionsMenu();
        hideKeyboard();
    }

    /**
     * FragmentManager.OnBackStackChangedListener implementation
     */
    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
        hideKeyboard();
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private void hideKeyboard() {
        InputMethodManager inputManager =
                (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void shouldDisplayHomeUp() {
        boolean canBack = getSupportFragmentManager().getBackStackEntryCount() > 0;
        getSupportActionBar().setDisplayHomeAsUpEnabled(canBack);
        if (canBack) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        if (!canBack) {
            mToggle.syncState();
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_right,
                    R.anim.slide_out_right);
        }
    }

    /**
     * CategoriesFragment.CategoriesFragmentCallbacks implementation
     */

    @Override
    public void onCategoryItemSelected(final Category category, final int position) {
        final ProgressDialog progressBar = new ProgressDialog(mContext);
        progressBar.setMessage("Загрузка площадок");
        progressBar.setCancelable(false);
        progressBar.show();
        List<Field> dbFields = null;
        try {
            dbFields = getAllFieldsBySportId(category.getSportId());
        } catch (SQLException e) {
            Log.e("ReadFieldsException", "ReadFieldsException");
        }
        boolean started = false;
        if (dbFields != null && !dbFields.isEmpty()) {
            if (progressBar!= null) {
                progressBar.dismiss();
            }
            started = true;
            startPlaceMapActivity(dbFields);
        }
        final boolean startedFlag = started;

        networkUtilities(this).addToRequestQueue(new FieldsRequest(new Response.Listener<List<Field>>() {
            @Override
            public void onResponse(List<Field> response) {
                try {
                    List<Field> dbFields = getAllFieldsBySportId(category.getSportId());
                    for (Field field : response) {
                        for (Field dbField : dbFields) {
                            if (field.getFieldId().equals(dbField.getFieldId())) {
                                field.setFavorite(dbField.getFavorite());
                            }
                        }
                    }
                } catch (SQLException e) {
                    Log.e("ReadFieldsException", "ReadFieldsException");
                }

                try {
                    writeFields(response);
                } catch (SQLException e) {
                    Log.e("WriteFieldsException", "WriteFieldsException");
                }
                if (progressBar!= null) {
                    progressBar.dismiss();
                }
                if (!startedFlag) {
                    startPlaceMapActivity(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!startedFlag) {
                    List<Field> dbFields = null;
                    try {
                        dbFields = getAllFieldsBySportId(category.getSportId());
                    } catch (SQLException e) {
                        Log.e("ReadFieldsException", "ReadFieldsException");
                    }
                    if (progressBar!= null) {
                        progressBar.dismiss();
                    }

                    if (dbFields == null) {
                        Toast.makeText(MainActivity.this, R.string.check_network, Toast.LENGTH_SHORT).show();
                    } else {
                        startPlaceMapActivity(dbFields);
                    }
                }
            }
        }, category.getSportId()));
    }

    private void startPlaceMapActivity(List<Field> fields) {
        Intent intent = new Intent(this, PlacesActivity.class);
        Bundle args = new Bundle();
        ArrayList<Field> fieldsList = new ArrayList<>();
        fieldsList.addAll(fields);
        Log.e("fields", "fields size: " + fieldsList.size());
        args.putParcelableArrayList("fields", fieldsList);
        intent.putExtras(args);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    /**
     * FriendPageFragment.FriendPageFragmentCallbacks implementation
     */
    private FriendProfileFragment friendProfileFragment;

    @Override
    public void onFriendItemSelected(User user, int position) {
        if (friendProfileFragment == null) {
            friendProfileFragment = new FriendProfileFragment();
        }

        Bundle args = new Bundle();
        args.putParcelable("profile", user);
        friendProfileFragment.setArguments(args);

        addBackStackFragment(friendProfileFragment);
    }

    /**
     * PlaceDetailFragment.PlaceDetailFragmentCallbacks implementation
     */



    /**
     * RegistrationFragment.RegistrationFragmentCallbacks implementation
     */
    @Override
    public void onUserRegistered() {
        myProfileFragment = new MyProfileFragment();

        Bundle args = new Bundle();
        args.putStringArrayList("sports_names", sportsNames);
        myProfileFragment.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_screen_frame, myProfileFragment)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment != null)
                fragment.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == 399) {
            if (data != null) {
                //set izbrannoe
                onNavigationDrawerItemSelected(2);
                if (pendingFragment != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_screen_frame, pendingFragment)
                            .commit();
                }
                invalidateOptionsMenu();
            }
        }
    }

    public void writeToUs() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "d-1316@yandex.ru", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "OpenGame");
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        try {
            startActivity(emailIntent);
            //overridePendingTransition(R.anim.left, R.anim.stay);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Отсутствует email клиент", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPlaceSelected(Field field) {
        Intent intent = new Intent(this, PlaceDetailActivity.class);
        Bundle args = new Bundle();
        args.putParcelable("field", field);
        intent.putExtras(args);
        startActivityForResult(intent, 399);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

}
