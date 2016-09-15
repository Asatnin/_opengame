package com.dkondratov.opengame.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dkondratov.opengame.R;
import com.dkondratov.opengame.model.User;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import static com.dkondratov.opengame.util.ImageLoaderHelper.imageLoader;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendProfileFragment extends Fragment {

    private User user;
    private DisplayImageOptions opt = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .build();

    public FriendProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getArguments().getParcelable("profile");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_friend_profile, container, false);
        setupContent(fragmentView);
        return fragmentView;
    }

    private void setupContent(View fragmentView) {
        ImageView friendImage = (ImageView) fragmentView.findViewById(R.id.friend_image);
        imageLoader().displayImage(user.getImageUrl(), friendImage, opt);

        TextView name = (TextView) fragmentView.findViewById(R.id.friend_name);
        name.setText(user.getName() + " " + user.getSurname() + "\n" + user.getCity());

        TextView aboutMe = (TextView) fragmentView.findViewById(R.id.about_me);

        TextView birthday = (TextView) fragmentView.findViewById(R.id.birthday);

        TextView city = (TextView) fragmentView.findViewById(R.id.city);
        city.setText(user.getCity());

        TextView sports = (TextView) fragmentView.findViewById(R.id.sports);
    }
}
