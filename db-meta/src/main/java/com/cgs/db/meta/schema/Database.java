package com.cgs.db.meta.schema;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * <p>Database class represent the server instance
 * 
 * <p>In Oracle, a database can have more then one schema(not support catalog)
 * In Mysql, a database can have more then  one catalog(not support schema)
 * In SqlServer , database can have more then one catalog
 * 
 * 
 * @author xumh
 *
 */
public class Database implements Serializable{
	private static final long serialVersionUID = 4791419417571119610L;
	
	private DatabaseInfo databaseInfo;
	
	private Set<Schema> schemas;

	public DatabaseInfo getDatabaseInfo() {
		return databaseInfo;
	}

	public void setDatabaseInfo(DatabaseInfo databaseInfo) {
		this.databaseInfo = databaseInfo;
	}

	public Set<Schema> getSchemas() {
		return schemas;
	}

	public void setSchemas(Set<Schema> schemas) {
		this.schemas = schemas;
	}

	@Override
	public String toString() {
		return "Database [databaseInfo=" + databaseInfo + ", schemas=" + schemas + "]";
	}
	
	
	
}
