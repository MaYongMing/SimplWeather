package app.com.cris.simplweather.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;
import java.util.List;

import app.com.cris.simplweather.fragment.CityWeatherFragment;


/**
 * Created by Cris on 2017/6/28.
 */

public class WeatherPagerAdapter extends FragmentStatePagerAdapter {


    private List<CityWeatherFragment> mFragments;

    public WeatherPagerAdapter(FragmentManager fm, List<CityWeatherFragment> fragments) {
        super(fm);
        mFragments = fragments;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
