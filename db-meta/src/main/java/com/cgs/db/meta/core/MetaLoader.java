package com.cgs.db.meta.core;

import java.util.Set;

import com.cgs.db.meta.schema.Database;
import com.cgs.db.meta.schema.Schema;
import com.cgs.db.meta.schema.SchemaInfo;
import com.cgs.db.meta.schema.Table;

public interface MetaLoader {
	/**
	 * get current datasource own Schema's table names
	 * 
	 * @return Set<String>
	 */
	Set<String> getTableNames();
	
	/**
	 * get current datasource own schema's table.Default Level Table contaion
	 *  columns、primaryKey、ForeignKey、index
	 *  
	 * 
	 * @param tableName
	 * @return Table
	 */
	Table getTable(String tableName);
	
	
	/**
	 * get current datasource own schema's table. 
	 * 
	 * @param tableName
	 * @param schemaLevel 
	 * @return
	 */
	Table getTable(String tableName,SchemaInfoLevel schemaLevel);
		
	/**
	 * Gets the database's schema information
	 * 
	 * @return SchemaInfo
	 */
	Set<SchemaInfo> getSchemaInfos();
	
	/**
	 * get current datasource own Schema
	 * 
	 * @return Schema
	 */
	Schema getSchema();
	
	
	/**
	 * get current datasource's all the Schema
	 * 
	 * @return
	 */
	Database getDatabase();
}
