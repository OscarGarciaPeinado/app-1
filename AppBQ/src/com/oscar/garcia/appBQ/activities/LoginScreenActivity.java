package com.oscar.garcia.appBQ.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.oscar.garcia.appBQ.R;
import com.oscar.garcia.appBQ.activities.utils.managers.DropboxHelper;

public class LoginScreenActivity extends Activity {

	public DropboxHelper _helper;
	private Button _loginButton;
	private SharedPreferences _preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_screen);

		_helper = new DropboxHelper(this);

		_loginButton = (Button) findViewById(R.id.login_button);
		_loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				_helper.start();
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();

		if (_helper.is_logged())
			if (_helper.getmDBApi().getSession().authenticationSuccessful()) {
				try {
					_helper.getmDBApi().getSession().finishAuthentication();
					_helper.storeAuth(_helper.getmDBApi().getSession());
					Intent intent = new Intent(this, MainAppBqActivity.class);
					startActivity(intent);
				} catch (IllegalStateException e) {
					Log.i("AppDBinfo", "No se ha podido autentificar.", e);
				}
			}
	}
}
