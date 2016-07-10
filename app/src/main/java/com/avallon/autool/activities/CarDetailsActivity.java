package com.avallon.autool.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.avallon.autool.R;
import com.avallon.autool.fragments.CarDetailsFragment;

public class CarDetailsActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.car_details_activity);
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		CarDetailsFragment carDetailsFragment = new CarDetailsFragment();
		ft.add(R.id.car_details_frame, carDetailsFragment);
		ft.commit();
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
			Intent intent = new Intent(CarDetailsActivity.this, PreferenceActivity.class);
			startActivity(intent);
			break;
		case android.R.id.home:
			CarDetailsActivity.this.finish();
			break;
		}

		return true;
	}
}
