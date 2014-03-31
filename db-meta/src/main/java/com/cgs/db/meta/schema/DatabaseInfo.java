package com.cgs.db.meta.schema;

import java.io.Serializable;

/**
 * information about database,such as Oracle\MySql\SQL SERVER
 * 
 * @author xumh
 *
 */
public class DatabaseInfo implements Serializable{
	private static final long serialVersionUID = -6518151715322677390L;

	private static final String NEWLINE = System.getProperty("line.separator");
	
	private String userName;
	private String productName;
	private String productVersion;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductVersion() {
		return productVersion;
	}

	public void setProductVersion(String productVersion) {
		this.productVersion = productVersion;
	}

	@Override
	public String toString() {
		final StringBuilder info = new StringBuilder();
		info.append("-- database: ").append(getProductName()).append(' ').append(getProductVersion()).append(NEWLINE);
		return info.toString();
	}
}
