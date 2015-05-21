package com.ruoyan.map500px.ui;

import android.app.Activity;
import android.os.Bundle;

import com.ruoyan.map500px.ui.fragment.PreferenceFragment;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenceFragment())
                .commit();
    }

}
