package com.avallon.autool.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import com.avallon.autool.fragments.PrefsFragment;

public class PreferenceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            PreferenceActivity.this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
