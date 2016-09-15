package com.dkondratov.opengame.adapters;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dkondratov.opengame.fragments.HistoryInviteFragment;
import com.dkondratov.opengame.fragments.NewInviteFragment;

public class InvitesPagerAdapter extends FragmentStatePagerAdapter {

    private String[] array;

    public InvitesPagerAdapter(FragmentManager fm, String[] pageTitles) {
        super(fm);
        array = pageTitles;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new NewInviteFragment();
        } else {
            return new HistoryInviteFragment();
        }
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
        //super.restoreState(state, loader);
    }
}
