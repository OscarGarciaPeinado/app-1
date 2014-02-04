package com.oscar.garcia.appBQ.activities;

import com.oscar.garcia.appBQ.R;
import com.oscar.garcia.appBQ.R.layout;
import com.oscar.garcia.appBQ.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class LoginScreenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_screen);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login_screen, menu);
		return true;
	}

}
