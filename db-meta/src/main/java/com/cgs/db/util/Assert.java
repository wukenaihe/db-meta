package com.cgs.db.util;

/**
 * 
 * Assertion utility class that assists in validating arguments.
 * Useful for identifying programmer errors early and clearly at runtime.
 * 
 * @author xumh
 *
 */
public class Assert {
	/**
	 * Assert that an object is not {@code null} .
	 * <pre class="code">Assert.notNull(clazz, "The class must not be null");</pre>
	 * @param object the object to check
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if the object is {@code null}
	 */
	public static void notNull(Object object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}
	
	/**
	 * Assert that an string is not empty
	 * 
	 * @param str the String to check
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if the object is {@code null}
	 */
	public static void notEmpty(String str,String message){
		notNull(str, "str must not be null");
		if(str.equals("")){
			throw new IllegalArgumentException(message);
		}
	}
}
