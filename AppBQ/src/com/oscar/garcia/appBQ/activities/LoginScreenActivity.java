package com.oscar.garcia.appBQ.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.dropbox.client2.session.AccessTokenPair;
import com.oscar.garcia.appBQ.R;
import com.oscar.garcia.appBQ.activities.utils.managers.DropboxHelper;

public class LoginScreenActivity extends Activity {
	private DropboxHelper _helper;
	private Button _loginButton;
	private SharedPreferences _preferences;
	private boolean _load = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_screen);

		_helper = new DropboxHelper(this) {
		};

		_loginButton = (Button) findViewById(R.id.login_button);
		_loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				_load = true;
				_helper.createDropboxSession();
				_helper.start();
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (_load)
			if (_helper.getmDBApi().getSession().authenticationSuccessful()) {
				try {
					_helper.getmDBApi().getSession().finishAuthentication();

					AccessTokenPair tokens = _helper.getmDBApi().getSession()
							.getAccessTokenPair();

					storeKeys(tokens.key, tokens.secret);
					Intent intent = new Intent(this, MainAppBqActivity.class);
					startActivity(intent);
				} catch (IllegalStateException e) {
					Log.i("AppDBinfo", "No se ha podido autentificar.", e);
				}
			}
	}

	private void storeKeys(String key, String secret) {
		_preferences = getSharedPreferences("Preferencias", MODE_PRIVATE);
		_preferences.edit().putString("k", key);
		_preferences.edit().putString("s", secret);
		_preferences.edit().commit();
	}
}
