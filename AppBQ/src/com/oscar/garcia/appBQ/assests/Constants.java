package com.oscar.garcia.appBQ.assests;

public class Constants {
	public static final String TAG_ERROR = "AppBQ ERROR";
	public static final String TAG_INFO = "AppBQ INFO";

	/**
	 * El tipo de comparación 1 significa comparar por nombre, el tipo de comparador 2 significa
	 * comparar por fecha.
	 */

	/**
	 * Nota: La dejo como int para dejar la posibilidad de un mayor tipo de casos de comparación,
	 * pero podría ser perfectamente boolean para este caso expecífico.
	 */
	private static int TYPE_COMPARATOR = 1;

	/**
	 * @return the tYPE_COMPARATOR
	 */
	public static int getTYPE_COMPARATOR() {
		return TYPE_COMPARATOR;
	}

	/**
	 * @param tYPE_COMPARATOR
	 *            the tYPE_COMPARATOR to set
	 */
	public static void setTYPE_COMPARATOR(int tYPE_COMPARATOR) {
		TYPE_COMPARATOR = tYPE_COMPARATOR;
	}

}
