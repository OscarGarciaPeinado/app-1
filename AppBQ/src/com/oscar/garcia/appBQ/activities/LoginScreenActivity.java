package com.oscar.garcia.appBQ.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.oscar.garcia.appBQ.R;
import com.oscar.garcia.appBQ.utils.managers.DropboxFileManager;
import com.oscar.garcia.appBQ.utils.managers.LoginHelper;

public class LoginScreenActivity extends LoginHelper {

	private Button _loginButton;
	private SharedPreferences _preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		createSession();
		setContentView(R.layout.activity_login_screen);
		_loginButton = (Button) findViewById(R.id.login_button);
		_loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				login();
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		AndroidAuthSession session = _mApi.getSession();
		if (session.authenticationSuccessful()) {
			try {
				session.finishAuthentication();
				System.out.println(_mApi.accountInfo());
				storeAuth(session);
				DropboxFileManager.get(_mApi);
				Intent intent = new Intent(this, MainAppBqActivity.class);
				startActivity(intent);
			} catch (IllegalStateException e) {
				Log.i("App BQ", "Error authenticating", e);
			} catch (DropboxException e) {
				e.printStackTrace();
			}
		}
	}
}
