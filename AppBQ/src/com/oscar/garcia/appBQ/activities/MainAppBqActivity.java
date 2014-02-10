package com.oscar.garcia.appBQ.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;

import com.oscar.garcia.appBQ.DetailsActivity;
import com.oscar.garcia.appBQ.R;
import com.oscar.garcia.appBQ.assests.Constants;
import com.oscar.garcia.appBQ.utils.fragments.BookDetailsFragment;
import com.oscar.garcia.appBQ.utils.fragments.BookListFragment;
import com.oscar.garcia.appBQ.utils.fragments.BookListFragment.BookListFragmentListener;
import com.oscar.garcia.appBQ.utils.managers.DropboxFileManager;

public class MainAppBqActivity extends FragmentActivity implements
		BookListFragmentListener {
	private boolean _twoFragment = false;
	private DropboxFileManager _fileManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_app_bq);
		BookListFragment frgListado = (BookListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.FrgListado);
		frgListado.set_listener(this);

		if (findViewById(R.layout.fragment_file_details) != null)
			_twoFragment = true;
		_fileManager = DropboxFileManager.get();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_app_bq, menu);
		return true;
	}

	@Override
	public void onBookSelected(int id) {
		_fileManager.downloadBook(this, id);

		if (_twoFragment) {
			((BookDetailsFragment) getSupportFragmentManager()
					.findFragmentById(R.id.FrgDetalle)).showDetails(id);

		} else {
			Intent intent = new Intent(this, DetailsActivity.class);
			intent.putExtra("id", id);
			startActivity(intent);
		}
	}

	@Override
	public void onSpinnerSelected(long _spinnerSelection) {
		Log.i(Constants.TAG_INFO, "Se ha elegido la opción ordenar: "
				+ _spinnerSelection);
	}
}
