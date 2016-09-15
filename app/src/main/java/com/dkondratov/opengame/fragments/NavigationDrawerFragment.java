package com.dkondratov.opengame.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dkondratov.opengame.R;
import com.dkondratov.opengame.adapters.DrawerMenuAdapter;
import com.dkondratov.opengame.network.NetworkUtilities;
import com.dkondratov.opengame.network.UserInfoRequest;
import com.dkondratov.opengame.util.ApplicationUserData;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import static com.dkondratov.opengame.util.ImageLoaderHelper.imageLoader;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment
        implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ListView drawer_menu_list;
    private DrawerLayout drawerLayout;
    private View fragmentView;
    private DrawerMenuAdapter drawerMenuAdapter;
    private ActionBarDrawerToggle mToggle;
    private NavigationDrawerCallbacks mCallbacks;
    private View mFragmentContainerView;
    private TextView userName;
    private ImageView profileImage;

    private DisplayImageOptions opt = new DisplayImageOptions.Builder()
            .displayer(new RoundedBitmapDisplayer(1000))
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .build();

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.navigation_drawer_fragment, container, false);
        userName = (TextView) fragmentView.findViewById(R.id.user_name);
        profileImage = (ImageView) fragmentView.findViewById(R.id.profile_img);

        setupDrawerMenu();
        return fragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (NavigationDrawerCallbacks) activity;
    }

    public void setUp(int fragmentId, DrawerLayout pDrawerLayout, Toolbar toolbar) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        drawerLayout = pDrawerLayout;

        mToggle = new ActionBarDrawerToggle(
                getActivity(),
                drawerLayout,
                //toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mCallbacks.onDrawerClosed();
                syncState();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mCallbacks.onDrawerOpened();
                syncState();
                userName.setText(String.format("%s %s", ApplicationUserData.loadUserName(getActivity()), ApplicationUserData.loadUserSurname(getActivity())));
                String url = ApplicationUserData.loadUserImageUrl(getActivity());
                if (!TextUtils.isEmpty(url))
                    imageLoader().displayImage(url, profileImage, opt);
                NetworkUtilities.networkUtilities(getActivity()).addToRequestQueue(new UserInfoRequest(new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        drawerMenuAdapter.notifyDataSetChanged();
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
            public boolean onOptionsItemSelected(MenuItem item) {
                Toast.makeText(getActivity(), "111111", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
            }
        };

        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mToggle.syncState();
            }
        });

        drawerLayout.setDrawerListener(mToggle);
    }

    public ActionBarDrawerToggle getmToggle() {
        return mToggle;
    }

    private void setupDrawerMenu() {
        drawer_menu_list = (ListView) fragmentView.findViewById(R.id.drawer_menu_list);

        drawerMenuAdapter = new DrawerMenuAdapter(getActivity().getApplicationContext());
        drawer_menu_list.setAdapter(drawerMenuAdapter);
        drawer_menu_list.setOnItemClickListener(this);

        View profileButton = fragmentView.findViewById(R.id.profile_button);
        profileButton.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        drawer_menu_list.setItemChecked(position, true);
        mCallbacks.onNavigationDrawerItemSelected(position);
        drawerLayout.closeDrawer(mFragmentContainerView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_button:
                drawerLayout.closeDrawer(mFragmentContainerView);
                mCallbacks.onProfilePageSelected();
                break;

        }
    }

    public interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
        void onProfilePageSelected();
        void onDrawerClosed();
        void onDrawerOpened();
    }

    @Override
    public void onResume() {
        super.onResume();
        userName.setText(String.format("%s %s", ApplicationUserData.loadUserName(getActivity()), ApplicationUserData.loadUserSurname(getActivity())));
        String url = ApplicationUserData.loadUserImageUrl(getActivity());
        if (!TextUtils.isEmpty(url))
            imageLoader().displayImage(url, profileImage, opt);

    }
}
