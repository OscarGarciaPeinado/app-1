package com.oscar.garcia.appBQ.utils.managers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

import android.content.Context;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.oscar.garcia.appBQ.entities.Book;

/**
 * Esta clase se encarga de gestionar todos los archivos asociados a una cuenta dropbox
 * 
 * @author Oscar Garcia Peinado
 * 
 */
public class DropboxFileManager {

	private static final String PATH = "/AppBq/";
	private static DropboxFileManager _instance;
	private DropboxAPI<AndroidAuthSession> _mApi;
	private String _mErrorMsg;
	private ArrayList<Entry> _booksListEntry;
	private ArrayList<Book> _booksList;

	public static DropboxFileManager get(DropboxAPI<AndroidAuthSession> mDBApi) {
		if (_instance != null)
			return _instance;
		return _instance = new DropboxFileManager(mDBApi);
	}

	public static DropboxFileManager get() {
		return _instance;
	}

	public DropboxFileManager(DropboxAPI<AndroidAuthSession> mDBApi) {
		_mApi = mDBApi;
		_booksList = new ArrayList<Book>();
	}

	/**
	 * Carga los ficheros del directorio.
	 * 
	 * @return true si se han podido cargar los archivos.
	 */
	public boolean loadBooksFromDirectory() {
		try {
			_booksListEntry = (ArrayList<Entry>) _mApi.metadata(PATH, 0, null, true, null).contents;
		} catch (DropboxException e) {
			e.printStackTrace();
		}
		if (is_EpubDir() && !_booksListEntry.isEmpty()) {
			return true;
		} else
			return false;
	}

	private boolean isEbook(Entry nodo) {
		String path = nodo.path;
		if (path.contains(".epub"))
			return true;
		return false;
	}

	/**
	 * Carga en una lista todos los libros encontrados.
	 */
	public void searchAllFiles() {
		/**
		 * Realizamos un pequeño algoritmo de recorrido de árboles en preorden para listar todos los
		 * ebooks con un coste de 2n+1 donde n es el número de nodos.
		 */
		ArrayList<Entry> booksEntry = null;
		try {
			String auxPath;
			booksEntry = new ArrayList<DropboxAPI.Entry>();
			LinkedList<Entry> fifo = new LinkedList<DropboxAPI.Entry>();
			Entry nodo = _mApi.metadata("/", 0, null, true, null);
			fifo.addAll(nodo.contents);
			while (!fifo.isEmpty()) {
				System.out.println(fifo);
				nodo = fifo.getFirst();
				fifo.removeFirst();
				auxPath = nodo.path;
				if (nodo.isDir) {
					fifo.addAll(_mApi.metadata(auxPath, 0, null, true, null).contents);
				} else {
					if (isEbook(nodo))
						booksEntry.add(nodo);
				}
			}
			System.out.println("parar");
		} catch (DropboxUnlinkedException e) {
			// The AuthSession wasn't properly authenticated or user unlinked.
		} catch (DropboxPartialFileException e) {
			// We canceled the operation
			_mErrorMsg = "Download canceled";
		} catch (DropboxServerException e) {
			// Server-side exception. These are examples of what could happen,
			// but we don't do anything special with them here.
			if (e.error == DropboxServerException._304_NOT_MODIFIED) {
				// won't happen since we don't pass in revision with metadata
			} else if (e.error == DropboxServerException._401_UNAUTHORIZED) {
				// Unauthorized, so we should unlink them. You may want to
				// automatically log the user out in this case.
			} else if (e.error == DropboxServerException._403_FORBIDDEN) {
				// Not allowed to access this
			} else if (e.error == DropboxServerException._404_NOT_FOUND) {
				// path not found (or if it was the thumbnail, can't be
				// thumbnailed)
			} else if (e.error == DropboxServerException._406_NOT_ACCEPTABLE) {
				// too many entries to return
			} else if (e.error == DropboxServerException._415_UNSUPPORTED_MEDIA) {
				// can't be thumbnailed
			} else if (e.error == DropboxServerException._507_INSUFFICIENT_STORAGE) {
				// user is over quota
			} else {
				// Something else
			}
			// This gets the Dropbox error, translated into the user's language
			_mErrorMsg = e.body.userError;
			if (_mErrorMsg == null) {
				_mErrorMsg = e.body.error;
			}
		} catch (DropboxIOException e) {
			// Happens all the time, probably want to retry automatically.
			_mErrorMsg = "Network error.  Try again.";
		} catch (DropboxParseException e) {
			// Probably due to Dropbox server restarting, should retry
			_mErrorMsg = "Dropbox error.  Try again.";
		} catch (DropboxException e) {
			// Unknown error
			_mErrorMsg = "Unknown error.  Try again.";
		}
		_booksListEntry = booksEntry;
		createListBooks();
	}

	/**
	 * Comprueba que existe un directorio donde localizar los libros.
	 * 
	 * @return
	 */
	public boolean is_EpubDir() {
		Entry dir = null;
		try {
			dir = _mApi.metadata(PATH, 0, null, true, null);
		} catch (DropboxException e) {
			e.printStackTrace();
		}
		if (dir != null)
			if (dir.isDir)
				return true;
		return false;

	}

	public void createListBooks() {
		_booksList.clear();
		for (Entry aux : _booksListEntry) {
			_booksList.add(new Book(aux));
		}
	}

	/**
	 * @return the _mApi
	 */
	public DropboxAPI<AndroidAuthSession> get_mApi() {
		return _mApi;
	}

	/**
	 * @return the _booksListEntry
	 */
	public ArrayList<Entry> get_booksListEntry() {
		return _booksListEntry;
	}

	/**
	 * @return the _booksList
	 */
	public ArrayList<Book> get_booksList() {
		return _booksList;
	}

	/**
	 * Devuelve un libro de la lista.
	 * 
	 * @param id
	 * @return
	 */
	public Book getBookById(int id) {
		Book result = null;
		for (Book aux : _booksList) {
			if (aux.get_id() == id)
				return aux;
		}
		return result;
	}

	public boolean downloadBook(Context context, int id) {
		Book book = getBookById(id);
		int positionList = _booksList.indexOf(book);
		DownloadFileManager dowloaderFileManager = new DownloadFileManager(context, id, positionList);
		try {
			return dowloaderFileManager.execute().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return false;

	}

	public void uploadBookList(Book _book, int _position) {
		_booksList.set(_position, _book);
	}
}
