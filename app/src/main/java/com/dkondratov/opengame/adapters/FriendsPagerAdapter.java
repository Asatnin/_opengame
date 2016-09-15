package com.dkondratov.opengame.adapters;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.dkondratov.opengame.fragments.FriendsPageFragment;

public class FriendsPagerAdapter extends FragmentStatePagerAdapter {
    private String[] array;

    public FriendsPagerAdapter(FragmentManager fm, String[] pageTitles) {
        super(fm);
        array = pageTitles;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new FriendsPageFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return array.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return array[position];
    }

    /**
     * �������, ����� �� �������� pages ��� ������ ��������
     */
    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
//        super.restoreState(state, loader);
    }

    @Override
    public void startUpdate(ViewGroup container) {
        super.startUpdate(container);
    }
}
