package com.oscar.garcia.appBQ.utils.fragments;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import nl.siegmann.epublib.epub.EpubReader;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oscar.garcia.appBQ.R;
import com.oscar.garcia.appBQ.entities.Book;
import com.oscar.garcia.appBQ.utils.managers.DropboxFileManager;

public class BookDetailsFragment extends Fragment {
	private TextView _titleDetailTextView, _dateDetailTextView, _authorDetailTextView;

	private Activity _activity;
	private DropboxFileManager _fileManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		return inflater.inflate(R.layout.fragment_file_details, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_activity = this.getActivity();
		_fileManager = DropboxFileManager.get();
	}

	public void showDetails(int id) {

		_titleDetailTextView = (TextView) _activity.findViewById(R.id.titleDetailTextview);
		_dateDetailTextView = (TextView) _activity.findViewById(R.id.dateDetailTextview);
		_authorDetailTextView = (TextView) _activity.findViewById(R.id.authorDetailTextview);
		Book book = _fileManager.getBookById(id);
		_titleDetailTextView.setText("Titulo: " + book.getFileName());
		_dateDetailTextView.setText("Ultima modificacion: " + book.get_date());
		_authorDetailTextView.setText("Autor: ");
		File file;
		try {
			file = new File(_fileManager.getBookById(id).get_internalPath());
			InputStream epubInputStream = new BufferedInputStream(new FileInputStream(file));
			EpubReader reader = new EpubReader();
			// Book book = reader.readEpub(epubInputStream);
			// book.getCoverPage();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
