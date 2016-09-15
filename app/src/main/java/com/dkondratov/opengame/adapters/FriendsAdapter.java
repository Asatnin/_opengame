package com.dkondratov.opengame.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dkondratov.opengame.R;
import com.dkondratov.opengame.model.User;
import com.dkondratov.opengame.util.UsersData;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.List;

import static com.dkondratov.opengame.util.ImageLoaderHelper.imageLoader;

public class FriendsAdapter extends BaseAdapter {

    private List<User> cashedFriends;
    private List<User> cashedTeammates;


    private final LayoutInflater inflater;
    private List<User> friends;
    private DisplayImageOptions opt = new DisplayImageOptions.Builder()
            .displayer(new RoundedBitmapDisplayer(1000))
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .build();

    public FriendsAdapter(List<User> friends, Context context, LayoutInflater inflater) {
        this.friends = friends;
        this.inflater = inflater;
        this.cashedFriends = UsersData.getFriends(context);
        this.cashedTeammates = UsersData.getTeammates(context);
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public User getItem(int position) {
        return friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FriendHolder friendHolder;
        if (convertView != null) {
            friendHolder = (FriendHolder) convertView.getTag();
        } else {
            friendHolder = new FriendHolder();
            convertView = inflater.inflate(R.layout.friend_item, parent, false);
            friendHolder.imageView = (ImageView) convertView.findViewById(R.id.friend_image);
            friendHolder.textView = (TextView) convertView.findViewById(R.id.friend_name);
            friendHolder.team = (ImageView) convertView.findViewById(R.id.team);
            friendHolder.check = (ImageView) convertView.findViewById(R.id.check);
            convertView.setTag(friendHolder);
        }

        User user = getItem(position);
        friendHolder.imageView.setImageResource(R.drawable.default_profile);
        if (user.getImageUrl()!=null)
            imageLoader().displayImage(user.getImageUrl(), friendHolder.imageView, opt);

        friendHolder.textView.setText(user.toString());

        friendHolder.check.setVisibility(cashedFriends.contains(user) ? View.VISIBLE : View.INVISIBLE);
        friendHolder.team.setVisibility(cashedTeammates.contains(user) ? View.VISIBLE : View.INVISIBLE);

        return convertView;
    }

    static class FriendHolder {
        ImageView imageView;
        TextView textView;
        ImageView team;
        ImageView check;
    }
}
