package com.dkondratov.opengame.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.dkondratov.opengame.R;
import com.dkondratov.opengame.activities.UserActivity;
import com.dkondratov.opengame.adapters.FriendsPagerAdapter;
import com.dkondratov.opengame.model.User;
import com.dkondratov.opengame.network.SearchUserRequest;
import com.dkondratov.opengame.util.ApplicationUserData;
import com.dkondratov.opengame.widgets.SlidingTabLayout;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

import static com.dkondratov.opengame.util.ImageLoaderHelper.imageLoader;

public class FriendsFragment extends Fragment  {

    public static final String ACTION_UPDATE_FRIENDS = "update_friends";
    private static final String[] pageTitles = new String[] {"Новые", "Друзья", "Команда"};

    private ViewPager viewPager;
    private SlidingTabLayout slidingTabLayout;
    private FriendsPagerAdapter pagerAdapter;
    private ImageView button;



    private RequestQueue queue;
    private ArrayAdapter<User> adapter;


    private DisplayImageOptions opt = new DisplayImageOptions.Builder()
            .displayer(new RoundedBitmapDisplayer(1000))
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .showImageOnFail(R.drawable.default_profile)
            .showImageOnLoading(R.drawable.default_profile)
            .build();


    private AutoCompleteTextView search;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_friends, container, false);
        viewPager = (ViewPager) fragmentView.findViewById(R.id.friends_pager);
        search = (AutoCompleteTextView)fragmentView.findViewById(R.id.name);
        slidingTabLayout = (SlidingTabLayout) fragmentView.findViewById(R.id.sliding_tabs);
        button = (ImageView)fragmentView.findViewById(R.id.close);
        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        queue = Volley.newRequestQueue(getActivity());

        pagerAdapter = new FriendsPagerAdapter(getChildFragmentManager(), pageTitles);
        viewPager.setAdapter(pagerAdapter);

        slidingTabLayout.setCustomTabView(R.layout.tab_view, R.id.tab);
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.gold_color));
        slidingTabLayout.setDividerColors(getResources().getColor(R.color.gold_color));
        slidingTabLayout.setViewPager(viewPager);
        slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.e("position",""+position);
                Intent intent = new Intent(ACTION_UPDATE_FRIENDS);
                getActivity().sendBroadcast(intent);

                //fragmentPagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setCurrentItem(0);

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, new ArrayList());
        search.setAdapter(adapter);

        adapter.clear();
        adapter.notifyDataSetChanged();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length()>2)
                    loadAutocompleteValues(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = (User) parent.getAdapter().getItem(position);
                Intent intent = new Intent(getActivity(), UserActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });
    }

    private void loadAutocompleteValues(String name)  {
        try {
            queue.cancelAll(this);
            Request request = new SearchUserRequest(new Response.Listener<List<User>>() {
                @Override
                public void onResponse(List<User> response) {
                    Log.e("autocomplete response", "success " + response.size());
                    adapter.clear();
                    adapter = new ArrayAdapter<User>(getActivity(),  android.R.layout.simple_list_item_1, response) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            User user = getItem(position);
                            View view = getActivity().getLayoutInflater().inflate(R.layout.user_list_item, null, false);
                            TextView name = (TextView) view.findViewById(R.id.name);
                            ImageView profileImage = (ImageView) view.findViewById(R.id.profile_image);
                            profileImage.setImageResource(R.drawable.default_profile);
                            if (user.getImageUrl()!=null)
                                imageLoader().displayImage(user.getImageUrl(), profileImage, opt);
                            name.setText(String.format("%s %s", user.getName(), user.getSurname()));

                            return view;
                        }
                    };
                    adapter.notifyDataSetChanged();
                    search.setAdapter(adapter);
                    search.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("ERROR", "autocomplete");
                    error.printStackTrace();
                }
            }, name, ApplicationUserData.loadUserId(getActivity()));
            request.setTag(this);
            queue.add(request);
        } catch (Exception e)
        {
            Log.e("ERROR", "load auto");
            e.printStackTrace();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setText("");
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        pagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
