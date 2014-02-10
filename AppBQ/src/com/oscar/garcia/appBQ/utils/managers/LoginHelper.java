package com.oscar.garcia.appBQ.utils.managers;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

/**
 * Esta clase ayuda a enmascarar el manager account de dropbox, permitiendo hacer más fácil el uso
 * del mismo y generando la posibilidad de añadir funcionalidades al mismo.
 * 
 * @author Oscar Garcia Peinado
 * 
 */
public abstract class LoginHelper extends Activity {

	protected static final String ACCOUNT_PREFS_NAME = "PREFERENCES";
	protected static final AccessType ACCESS_TYPE = AccessType.DROPBOX;
	protected static final int CALL_BACK_REQUEST_CODE = 0;
	final static protected String ACCESS_KEY_NAME = "ACCESS_KEY";
	final static protected String ACCESS_SECRET_NAME = "ACCESS_SECRET";

	final static private String APP_KEY = "j00u4a3dzfhpbce";
	final static private String APP_SECRET = "b81l5erxg6y3ww8";
	protected static final boolean USE_OAUTH1 = false;

	protected static DropboxAPI<AndroidAuthSession> _mApi;

	protected boolean _logged = false;

	public void login() {
		if (USE_OAUTH1) {
			_mApi.getSession().startAuthentication(this);
		} else {
			_mApi.getSession().startOAuth2Authentication(this);
		}
		_logged = true;
	}

	/**
	 * @return the _callbackRequestCode
	 */
	public int get_callbackRequestCode() {
		return CALL_BACK_REQUEST_CODE;
	}

	protected void loadAuth(AndroidAuthSession session) {
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		String key = prefs.getString(ACCESS_KEY_NAME, null);
		String secret = prefs.getString(ACCESS_SECRET_NAME, null);
		if (key == null || secret == null || key.length() == 0
				|| secret.length() == 0)
			return;

		if (key.equals("oauth2:")) {
			session.setOAuth2AccessToken(secret);
		} else {
			session.setAccessTokenPair(new AccessTokenPair(key, secret));
		}
	}

	/**
	 * Guarda en preferencias los parámetros de la sessión.
	 * 
	 * @param session
	 */
	protected void storeAuth(AndroidAuthSession session) {
		String oauth2AccessToken = session.getOAuth2AccessToken();
		if (oauth2AccessToken != null) {
			SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME,
					0);
			Editor edit = prefs.edit();
			edit.putString(ACCESS_KEY_NAME, "oauth2:");
			edit.putString(ACCESS_SECRET_NAME, oauth2AccessToken);
			edit.commit();
			return;
		}
		AccessTokenPair oauth1AccessToken = session.getAccessTokenPair();
		if (oauth1AccessToken != null) {
			SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME,
					0);
			Editor edit = prefs.edit();
			edit.putString(ACCESS_KEY_NAME, oauth1AccessToken.key);
			edit.putString(ACCESS_SECRET_NAME, oauth1AccessToken.secret);
			edit.commit();
			return;
		}
	}

	/**
	 * @return the mDBApi
	 */
	public DropboxAPI<AndroidAuthSession> getmDBApi() {
		return _mApi;
	}

	/**
	 * @return the _logged
	 */
	public boolean is_logged() {
		return _logged;
	}

	/**
	 * @param _logged
	 *            the _logged to set
	 */
	public void set_logged(boolean _logged) {
		this._logged = _logged;
	}

	protected void clearKeys() {
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs.edit();
		edit.clear();
		edit.commit();
	}

	protected void createSession() {
		AndroidAuthSession session = buildSession();
		_mApi = new DropboxAPI<AndroidAuthSession>(session);
		checkAppKeySetup();
	}

	protected AndroidAuthSession buildSession() {
		AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);

		AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
		loadAuth(session);
		return session;
	}

	protected void checkAppKeySetup() {
		// Check if the app has set up its manifest properly.
		Intent testIntent = new Intent(Intent.ACTION_VIEW);
		String scheme = "db-" + APP_KEY;
		String uri = scheme + "://" + AuthActivity.AUTH_VERSION + "/test";
		testIntent.setData(Uri.parse(uri));
		PackageManager pm = getPackageManager();
		if (0 == pm.queryIntentActivities(testIntent, 0).size()) {
			System.out.println("URL scheme in your app's "
					+ "manifest is not set up correctly. You should have a "
					+ "com.dropbox.client2.android.AuthActivity with the "
					+ "scheme: " + scheme);
			finish();
		}
	}

}
