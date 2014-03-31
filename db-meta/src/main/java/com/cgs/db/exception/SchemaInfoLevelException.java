package com.cgs.db.exception;

public class SchemaInfoLevelException extends RuntimeException{
	public SchemaInfoLevelException(String msg) {
		super(msg);
	}
	
	public SchemaInfoLevelException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
