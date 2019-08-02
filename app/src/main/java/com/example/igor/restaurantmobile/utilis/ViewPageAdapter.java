package com.example.igor.restaurantmobile.utilis;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.igor.restaurantmobile.TabLicense;
import com.example.igor.restaurantmobile.TabSetting;

public class ViewPageAdapter extends FragmentPagerAdapter {
    public ViewPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
       if (position== 0){
            TabSetting one= new TabSetting();
            return one;
       }else{
         TabLicense two= new TabLicense();
         return  two;
       }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
