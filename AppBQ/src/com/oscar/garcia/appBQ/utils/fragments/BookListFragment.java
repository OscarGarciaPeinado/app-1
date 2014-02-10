package com.oscar.garcia.appBQ.utils.fragments;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.oscar.garcia.appBQ.R;
import com.oscar.garcia.appBQ.assests.Constants;
import com.oscar.garcia.appBQ.entities.Book;
import com.oscar.garcia.appBQ.utils.ViewHolder;
import com.oscar.garcia.appBQ.utils.managers.DropboxFileManager;

public class BookListFragment extends Fragment {

	private Book[] _books;
	private ListView _listBook;
	private DropboxFileManager _fileManager;
	private Spinner _spinner;
	private BookListFragmentListener _listener;
	private BookAdapter _adapter;
	private Fragment _fragment;
	private long _prevTime;
	private boolean _firstTap = false;
	private long _thisTime;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_books_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);
		_fragment = this;
		_fileManager = DropboxFileManager.get();

		// Comprobamos que existe la carpeta AppBq y que contiene los ficheros .epub
		if (_fileManager.loadBooksFromDirectory()) {
			_fileManager.createListBooks();
			_books = arrayListToArrayBook();
		} else {
			showDialog();
			_fileManager.createListBooks();
			_books = arrayListToArrayBook();
		}

		// Gestión del spinner
		_spinner = (Spinner) getActivity().findViewById(R.id.ordenar_lista_spinner);
		setUpSpinner();

		// Gestión del adaptador
		_listBook = (ListView) getView().findViewById(R.id.LstListado);
		setUpListview();
	}

	private void setUpListview() {
		if (_books != null) {
			_adapter = new BookAdapter(this);
			_listBook.setAdapter(_adapter);

		} else {
			// TODO: No se encontró ningún libro para mostrar.
		}
	}

	public boolean checkDoubleTap(int position) {
		if (_firstTap) {
			_thisTime = SystemClock.uptimeMillis();
			_firstTap = false;

		} else {
			_prevTime = _thisTime;
			_thisTime = SystemClock.uptimeMillis();

			if (_thisTime > _prevTime) {
				if ((_thisTime - _prevTime) <= ViewConfiguration.getDoubleTapTimeout()) {
					// Ejecuta la acción al hacer doble click
					_listener.onBookSelected(position);
				} else {
					_firstTap = true;
				}
			} else {
				_firstTap = true;
			}
		}
		return false;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			_listener = (BookListFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
		}
	}

	/**
	 * Adaptador para mostrar adecuadamente los items en la lista, en este caso muestra el título
	 * del libro y su fecha.
	 * 
	 * @author Oscar Garcia Peinado
	 * 
	 */
	class BookAdapter extends ArrayAdapter<Book> {

		Activity context;

		BookAdapter(Fragment context) {
			super(context.getActivity(), R.layout.list_item_layout, _books);
			this.context = context.getActivity();
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				LayoutInflater inflater = context.getLayoutInflater();
				convertView = inflater.inflate(R.layout.list_item_layout, null);

				TextView titleTextView = (TextView) convertView.findViewById(R.id.titulo_item);

				TextView dateTextView = (TextView) convertView.findViewById(R.id.fecha_item);

				ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageIconList);

				holder = new ViewHolder();
				holder.titleTextView = titleTextView;
				holder.dateTextView = dateTextView;
				holder.iconImageView = iconImageView;
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.titleTextView.setText(_books[position].getFileName());
			holder.dateTextView.setText(_books[position].get_date().toString());

			holder.iconImageView.setOnClickListener(new android.view.View.OnClickListener() {

				@Override
				public void onClick(View v) {
					checkDoubleTap(position);
				}
			});

			return convertView;

		}

	}

	/**
	 * Interfaz para gestionar la comunicación entre activity y BookListFragment
	 * 
	 * @author Oscar Garcia Peinado
	 * 
	 */
	public interface BookListFragmentListener {
		void onBookSelected(int id);

		void onSpinnerSelected(long _spinnerSelection);

		// TODO: Gestionar los listeners
	}

	/**
	 * Mostrar dialogo de error si no se han encontrado libros.
	 */
	public void showDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(_fragment.getActivity());

		dialog.setMessage("No se ha encontrado el directorio AppBQ, ¿Desea que AppBQ busque automáticamente los libros existentes?");
		dialog.setCancelable(true);
		dialog.setNegativeButton("No", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				_fileManager.searchAllFiles();
			}
		});
		dialog.show();
	}

	private Book[] arrayListToArrayBook() {
		Book[] result = new Book[_fileManager.get_booksList().size()];
		int contador = 0;
		for (Book aux : _fileManager.get_booksList()) {
			result[contador++] = aux;
		}
		return result;
	}

	private void setUpSpinner() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
				R.array.opciones_spinner, android.R.layout.simple_spinner_item);
		_spinner.setAdapter(adapter);
		_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (_listener != null) {
					switch (arg2) {
					case 0:
						sortAscentByFileName();
						break;
					case 1:
						sortAscentByFileDate();
						break;
					default:
						break;
					}
					_listener.onSpinnerSelected(arg3);
					Toast.makeText(_fragment.getActivity(), "Orenado por: " + arg0.getItemAtPosition(arg2).toString(),
							Toast.LENGTH_SHORT).show();

				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void sortAscentByFileName() {
		Constants.setTYPE_COMPARATOR(1);
		List<Book> sorted = Arrays.asList(_books);
		Collections.sort(sorted);
		_books = (Book[]) sorted.toArray();
		_adapter.notifyDataSetChanged();
	}

	@SuppressWarnings("unchecked")
	private void sortAscentByFileDate() {
		Constants.setTYPE_COMPARATOR(2);
		List<Book> sorted = Arrays.asList(_books);
		Collections.sort(sorted);
		_books = (Book[]) sorted.toArray();
		_adapter.notifyDataSetChanged();
	}

	/**
	 * @param _listener
	 *            the _listener to set
	 */
	public void set_listener(BookListFragmentListener _listener) {
		this._listener = _listener;
	}
}