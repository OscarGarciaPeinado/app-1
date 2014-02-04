package com.oscar.garcia.appBQ.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;

import com.oscar.garcia.appBQ.R;

/**
 * Esta clase genera una pantalla de bienvenida para el usuario de 4 segundos.
 * 
 * @author Tenesis
 * 
 */
public class SplashScreenActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash_screen);
		Thread myThread = new Thread() {
			public void run() {
				try {
					sleep(6000);
					finish();

					Intent main = new Intent(SplashScreenActivity.this,
							LoginScreenActivity.class);
					startActivity(main);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		myThread.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}

}
