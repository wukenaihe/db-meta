package com.cgs.db.exception;

/**
 * 
 * @author xumh
 * @see org.springframework.core.DataAccessException
 */
@SuppressWarnings("serial")
public abstract class DataAccessException extends NestedRuntimeException {

	/**
	 * Constructor for DataAccessException.
	 * @param msg the detail message
	 */
	public DataAccessException(String msg) {
		super(msg);
	}

	/**
	 * Constructor for DataAccessException.
	 * @param msg the detail message
	 * @param cause the root cause (usually from using a underlying
	 * data access API such as JDBC)
	 */
	public DataAccessException(String msg, Throwable cause) {
		super(msg, cause);
	}

}