package com.oscar.garcia.appBQ;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

import com.oscar.garcia.appBQ.utils.fragments.BookDetailsFragment;

public class DetailsActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		Bundle extras = getIntent().getExtras();

		BookDetailsFragment fragment = (BookDetailsFragment) getSupportFragmentManager()
				.findFragmentById(R.id.FrgDetalle);
		fragment.showDetails(extras.getInt("id"));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.details, menu);
		return true;
	}

}
