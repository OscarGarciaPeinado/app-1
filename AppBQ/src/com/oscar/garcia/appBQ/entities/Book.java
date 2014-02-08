package com.oscar.garcia.appBQ.entities;

import java.util.Date;

public class Book {
	private String _tittle;
	private Date _date;
	private String _path;

	public Book(String _tittle, Date _date, String _path) {
		super();
		this._tittle = _tittle;
		this._date = _date;
		this._path = _path;
	}

	/**
	 * @return the _tittle
	 */
	public String get_tittle() {
		return _tittle;
	}

	/**
	 * @param _tittle
	 *            the _tittle to set
	 */
	public void set_tittle(String _tittle) {
		this._tittle = _tittle;
	}

	/**
	 * @return the _date
	 */
	public Date get_date() {
		return _date;
	}

	/**
	 * @param _date
	 *            the _date to set
	 */
	public void set_date(Date _date) {
		this._date = _date;
	}

	/**
	 * @return the _path
	 */
	public String get_path() {
		return _path;
	}

	/**
	 * @param _path
	 *            the _path to set
	 */
	public void set_path(String _path) {
		this._path = _path;
	}

}
