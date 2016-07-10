package com.avallon.autool.activities;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.avallon.autool.R;
import com.avallon.autool.fragments.CarDetailsFragment;
import com.avallon.autool.fragments.RoutsFragment;
import com.avallon.autool.fragments.ServicesFragment;
import com.avallon.autool.fragments.TipsFragment;
import com.avallon.autool.items.ServiceItem;
import com.avallon.autool.utils.LocationBroadcastReceiver;
import com.crashlytics.android.Crashlytics;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;

public class MainActivity extends FragmentActivity {

	private ViewPager pager;
	private static MyPagerAdapter pagerAdapter;
	private PagerTabStrip tabStrip;
	public static boolean currentLocationSelected = false;
	private boolean mapDisplayed = true;
	private MenuItem serviceListSwitch;
	private boolean displayServiceListSwitch = false;
	private MenuItem shareTip;
	private boolean displayShareTip = false;
	private LocationBroadcastReceiver lftBroadcastReceiver;
	private OnPageChangeListener mPageChangeListener = new OnPageChangeListener() {
		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int pos) {
		    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			if (pos == 1 && ServicesFragment.serviceItems != null && ServicesFragment.extended) {
				if (serviceListSwitch != null) {
					serviceListSwitch.setVisible(true);
				} else {
					displayServiceListSwitch = true;
				}

				if (shareTip != null) {
					shareTip.setVisible(false);
				} else {
					displayShareTip = false;
				}
			} else if (pos == 3) {
				if (shareTip != null) {
					shareTip.setVisible(true);
				} else {
					displayShareTip = true;
				}

				if (serviceListSwitch != null) {
					serviceListSwitch.setVisible(false);
				} else {
					displayServiceListSwitch = false;
				}
			} else {
				if (shareTip != null) {
					shareTip.setVisible(false);
				} else {
					displayShareTip = false;
				}

				if (serviceListSwitch != null) {
					serviceListSwitch.setVisible(false);
				} else {
					displayServiceListSwitch = false;
				}
			}
		}
	};

	public static WeakReference<MainActivity> wrActivity = null;
	public static boolean openedTabFromWidget = false;
	public static String serviceType;
	public static String serviceTypeSelected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature((int) Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		
		Crashlytics.start(this);
		
        wrActivity = new WeakReference<MainActivity>(this);
		setContentView(R.layout.main_layout);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		pager = (ViewPager) findViewById(R.id.viewPager);
		pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(pagerAdapter);
		pager.setOnPageChangeListener(mPageChangeListener);
		pager.setOffscreenPageLimit(3);
		tabStrip = (PagerTabStrip) findViewById(R.id.pager_header);
	}

	@Override
	protected void onResume() {
		super.onResume();

		final IntentFilter lftIntentFilter = new IntentFilter(LocationLibraryConstants.getLocationChangedPeriodicBroadcastAction());
		lftBroadcastReceiver = new LocationBroadcastReceiver();
		registerReceiver(lftBroadcastReceiver, lftIntentFilter);

		currentLocationSelected = false;
		RoutsActivity.requestMade = false;
		
		serviceType = getIntent().getStringExtra(ServicesFragment.SERVICE_TYPE);
		if (serviceTypeSelected != null && serviceType != null && !serviceType.equals(serviceTypeSelected)) {
			openedTabFromWidget = false;
		}
		
		if (getIntent().getExtras() != null && serviceType != null && !openedTabFromWidget) {
			serviceTypeSelected = getIntent().getStringExtra(ServicesFragment.SERVICE_TYPE);
			pager.setCurrentItem(1, false);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (lftBroadcastReceiver != null) {
			unregisterReceiver(lftBroadcastReceiver);
		}
	}

	private class MyPagerAdapter extends FragmentPagerAdapter {

		private RoutsFragment routsFragment;
		private ServicesFragment servicesFragment;
		private CarDetailsFragment carDetailsFragment;
		private TipsFragment tipsFragment;

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int pos) {
			switch (pos) {
			case 0:
				if (routsFragment == null) {
					routsFragment = new RoutsFragment();
				}
				return routsFragment;
			case 1:
				if (servicesFragment == null) {
					servicesFragment = new ServicesFragment();
				}
				return servicesFragment;
			case 2:
				if (carDetailsFragment == null) {
					carDetailsFragment = new CarDetailsFragment();
				}
				return carDetailsFragment;
			case 3:
				if (tipsFragment == null) {
					tipsFragment = new TipsFragment();
				}
				return tipsFragment;
			default:
				return null;
			}
		}

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.title_section1);
			case 1:
				return getString(R.string.title_section2);
			case 2:
				return getString(R.string.title_section3);
			case 3:
				return getString(R.string.title_section4);
			default:
				return null;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		
		shareTip = menu.getItem(0);
		if (displayShareTip) {
			shareTip.setVisible(true);
		} else {
			shareTip.setVisible(false);
		}

		serviceListSwitch = menu.getItem(1);
		if (displayServiceListSwitch) {
			serviceListSwitch.setVisible(true);
		} else {
			serviceListSwitch.setVisible(false);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent(MainActivity.this, PreferenceActivity.class);
			startActivity(intent);
			break;
		case R.id.service_list_switch:
			toggleServicesMapList(null);
			break;
		case R.id.tips_share:
			shareTip();
			break;
		}

		return true;
	}
	
	@Override
	public void onBackPressed() {
	    if (pager.getCurrentItem() == 1) {
	        ServicesFragment servicesFragment = (ServicesFragment) pagerAdapter.getItem(1);
	        if (ServicesFragment.map.getUiSettings().isScrollGesturesEnabled()) {
	            servicesFragment.minimizeMap();
	        } else {
	            ServicesFragment.extended = false;
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

	public void hideServiceListSwitch() {
		serviceListSwitch.setVisible(false);
	}

	public void showServiceListSwitch() {
		serviceListSwitch.setVisible(true);
	}

	public void toggleServicesMapList(ServiceItem serviceItem) {
		if (mapDisplayed) {
			mapDisplayed = false;
			serviceListSwitch.setIcon(R.drawable.ic_action_map);
		} else {
			mapDisplayed = true;
			serviceListSwitch.setIcon(R.drawable.ic_action_view_as_list);
		}
		ServicesFragment servicesFragment = (ServicesFragment) pagerAdapter.getItem(1);
		servicesFragment.toggleServicesMapList(serviceItem);
	}

	public void resetServiceListSwitch() {
		mapDisplayed = true;
		if (serviceListSwitch != null) {
		    serviceListSwitch.setIcon(R.drawable.ic_action_view_as_list);
		}
	}

	public void displayProgress(boolean display) {
		if (serviceListSwitch != null) {
			serviceListSwitch.setVisible(false);
		} else {
			displayServiceListSwitch = false;
		}
		setProgressBarIndeterminateVisibility(display);
	}

	public void shareTip() {
		TipsFragment tipsFragment = (TipsFragment) pagerAdapter.getItem(3);
		tipsFragment.shareTip();
	}
}
