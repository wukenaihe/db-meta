package com.cgs.db.meta.core;

import java.util.Set;

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
}
