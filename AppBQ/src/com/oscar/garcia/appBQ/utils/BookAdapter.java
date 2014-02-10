package com.oscar.garcia.appBQ.utils;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.oscar.garcia.appBQ.R;
import com.oscar.garcia.appBQ.entities.Book;

class BookAdapter extends ArrayAdapter<Book> {

	private Book[] _books;
	private Activity _context;

	/**
	 * Adaptador que permite mostrar todos los libros en una lista.
	 * 
	 * @param context
	 * @param books
	 */
	BookAdapter(Fragment fragment, int resource, Book[] books) {
		super(fragment.getActivity(), resource);
		_books = books;
		_context = fragment.getActivity();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = _context.getLayoutInflater();
		View item = inflater.inflate(R.layout.fragment_books_list, null);

		TextView lblDe = (TextView) item.findViewById(R.id.titulo_item);
		lblDe.setText(_books[position].get_tittle());

		TextView lblAsunto = (TextView) item.findViewById(R.id.fecha_item);
		lblAsunto.setText(_books[position].get_date().toString());

		return (item);
	}
}