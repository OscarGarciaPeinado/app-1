package com.oscar.garcia.appBQ.utils.managers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.oscar.garcia.appBQ.assests.Constants;
import com.oscar.garcia.appBQ.entities.Book;

public class DownloadFileManager extends AsyncTask<String, Long, Boolean> {

	private DropboxFileManager _fileManager;
	private Book _book;
	private boolean mCanceled;
	private Long _mFileLen;
	private String _mErrorMsg;
	private Context _mContext;
	private final ProgressDialog _mDialog;
	private FileOutputStream _mFos;
	private int _position;

	public DownloadFileManager(Context context, int id, int position) {
		_fileManager = DropboxFileManager.get();
		_position = position;
		_mContext = context;
		_book = _fileManager.getBookById(id);
		_mDialog = new ProgressDialog(context);
		_mDialog.setMessage("Downloading Image");
		_mDialog.setButton("Cancel", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				mCanceled = true;
				_mErrorMsg = "Canceled";
				if (_mFos != null) {
					try {
						_mFos.close();
					} catch (IOException e) {
					}
				}
			}
		});

		_mDialog.show();
	}

	@Override
	protected Boolean doInBackground(String... arg0) {
		FileOutputStream outputStream = null;
		String path = _mContext.getFilesDir() + "/" + _book.getFileName();
		if (mCanceled) {
			return false;
		}
		try {

			File file = new File(path);

			outputStream = new FileOutputStream(file);
			DropboxFileInfo info = _fileManager.get_mApi().getFile(_book.get_dropBoxPath(), null, outputStream, null);

			Log.d(Constants.TAG_INFO, "Se ha descargado: " + info.getMetadata().fileName());
		} catch (Exception e) {
			System.out.println("Something went wrong: " + e);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
				}
			}
		}
		_book.set_downloaded(true);
		_book.set_internalPath(path);
		_fileManager.uploadBookList(_book, _position);
		return true;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		_fileManager = DropboxFileManager.get();
	}

	@Override
	protected void onProgressUpdate(Long... progress) {
		super.onProgressUpdate(progress);
		int percent = (int) (100.0 * (double) progress[0] / _mFileLen + 0.5);
		_mDialog.setProgress(percent);
	}

}
