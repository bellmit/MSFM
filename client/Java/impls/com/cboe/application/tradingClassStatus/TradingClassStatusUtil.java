/**
 * 
 */
package com.cboe.application.tradingClassStatus;

/**
 * @author Arun Ramachandran May 25, 2010
 *
 */
public class TradingClassStatusUtil {
	
	public static String intArrayToString(int[] listOfClasses) {
		StringBuilder sb = new StringBuilder();
		int length = listOfClasses.length;
		for (int i = 0; i < length; i++) {
			if (i != 0 && i != length) {
				sb.append(",");
			}
			sb.append(listOfClasses[i]);
		}
		return sb.toString();
	}
	
	public static String arrayToString(Object[] array) {
		StringBuilder sb = new StringBuilder();
		int length = array.length;
		for (int i = 0; i < length; i++) {
			if (i != 0 && i != length) {
				sb.append(", ");
			}
			sb.append(array[i]);
		}
		return sb.toString();
	}


}
