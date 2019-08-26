package com.example.igor.restaurantmobile.utilis;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.igor.restaurantmobile.TabOther;
import com.example.igor.restaurantmobile.TabConnect;

public class ViewPageAdapter extends FragmentPagerAdapter {
    public ViewPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
       if (position== 0){
            TabConnect one= new TabConnect();
            return one;
       }else{
         TabOther two= new TabOther();
         return  two;
       }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
