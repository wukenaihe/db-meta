package com.cgs.db.meta.core;

import java.util.Set;

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
	 * @param tableName
	 * @return Table
	 */
	Table getTable(String tableName);
	
	/**
	 * Gets the database's schema information
	 * 
	 * @return SchemaInfo
	 */
	Set<SchemaInfo> getSchemaInfos();
}
