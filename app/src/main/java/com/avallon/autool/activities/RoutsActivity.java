package com.avallon.autool.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.avallon.autool.R;
import com.avallon.autool.fragments.RoutsDetailsFragment;
import com.avallon.autool.fragments.RoutsDetailsTabsFragment;
import com.avallon.autool.fragments.RoutsMapFragment;
import com.avallon.autool.requests.RequestDirections;
import com.avallon.autool.utils.MapHelper;
import com.avallon.autool.utils.Utils;
import com.google.android.gms.maps.model.LatLng;

public class RoutsActivity extends FragmentActivity {

    private ViewPager pager;
    public static final String ROUTES_LINK = "ROUTES_LINK";
    public static JSONObject routeJson;
    public static List<LatLng> elevationLocations;
    public static List<LatLng> allLocations;
    private static MyPagerAdapter pagerAdapter;
    private RoutsDetailsTabsFragment detailsTabsFragment;
    private RoutsDetailsFragment detailsFragment;
    private RoutsMapFragment mapFragment;
    public static boolean requestMade = false;
    private PagerTabStrip tabStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        pager = (ViewPager) findViewById(R.id.viewPager);
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        tabStrip = (PagerTabStrip) findViewById(R.id.pager_header);

        if (!requestMade) {
            requestMade = true;
            Utils.resetRequest();

            RequestDirections requestDirections = new RequestDirections(this);
            String link = getIntent().getStringExtra(ROUTES_LINK);
            requestDirections.execute(new String[] { link });
        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        private RoutsDetailsTabsFragment routsDetailsTabFragment;
        private RoutsDetailsFragment routsDetailsFragment;
        private RoutsMapFragment routsMapFragment;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {
            case 0:
                if (Utils.getScreelLayout(getApplicationContext()) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
                    if (routsDetailsFragment == null) {
                        routsDetailsFragment = new RoutsDetailsFragment();
                    }
                    return routsDetailsFragment;
                } else {
                    if (routsDetailsTabFragment == null) {
                        routsDetailsTabFragment = new RoutsDetailsTabsFragment();
                    }
                    return routsDetailsTabFragment;
                }
            case 1:
                if (routsMapFragment == null) {
                    routsMapFragment = new RoutsMapFragment();
                }
                return routsMapFragment;
            default:
                return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
            case 0:
                return "ROUTE DETAILS";
            case 1:
                return "ROUTE MAP";
            default:
                return null;
            }
        }
    }

    public void onResultsReceived(JSONObject json) {
        routeJson = json;

        allLocations = new ArrayList<LatLng>();
        elevationLocations = new ArrayList<LatLng>();
        try {
            String status = json.getString("status");

            if (status.equals("OK")) {
                JSONArray routes = json.getJSONArray("routes");
                JSONObject route = routes.getJSONObject(0);
                JSONArray legs = route.getJSONArray("legs");

                for (int i = 0; i < legs.length(); i++) {
                    JSONObject leg = new JSONObject(legs.get(i).toString());

                    JSONArray steps = leg.getJSONArray("steps");
                    for (int j = 0; j < steps.length(); j++) {
                        JSONObject step = new JSONObject(steps.get(j).toString());

                        JSONObject polylineObj = step.getJSONObject("polyline");
                        String polyline = polylineObj.getString("points");
                        List<LatLng> polyList = MapHelper.decodePoly(polyline);
                        allLocations.addAll(polyList);
                    }

                    if (i == 0) {
                        elevationLocations.add(allLocations.get(0));
                    }

                    elevationLocations.add(allLocations.get(allLocations.size() - 1));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        if (Utils.getScreelLayout(getApplicationContext()) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            detailsFragment = (RoutsDetailsFragment) pagerAdapter.getItem(0);
            detailsFragment.onResultsReceived(json, elevationLocations);
        } else {
            detailsTabsFragment = (RoutsDetailsTabsFragment) pagerAdapter.getItem(0);
            detailsTabsFragment.onResultsReceived(json, elevationLocations);
        }

        mapFragment = (RoutsMapFragment) pagerAdapter.getItem(1);
        mapFragment.onResultsReceived(json, allLocations);
    }

    public void moveToMap(LatLng latLng) {
        pager.setCurrentItem(1, true);
        if (mapFragment == null) {
            mapFragment = (RoutsMapFragment) pagerAdapter.getItem(1);
        }

        mapFragment.moveMapToLocation(latLng);
    }
    
    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 1) {
            mapFragment = (RoutsMapFragment) pagerAdapter.getItem(1);
            if (RoutsMapFragment.map.getUiSettings().isScrollGesturesEnabled()) {
                mapFragment.minimizeMap();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    public void hideActionBar() {
        getActionBar().hide();
        tabStrip.setVisibility(View.GONE);
    }

    public void showActionBar() {
        getActionBar().show();
        tabStrip.setVisibility(View.VISIBLE);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main2, menu);
        
        return true;
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_settings:
            Intent intent = new Intent(RoutsActivity.this, PreferenceActivity.class);
            startActivity(intent);
            break;
        case android.R.id.home:
            RoutsActivity.this.finish();
            break;
        }

        return true;
    }
}
