package com.cgs.db.meta.core;

import java.util.Map;
import java.util.Set;

import com.cgs.db.meta.schema.Database;
import com.cgs.db.meta.schema.Procedure;
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
	
	
	Table getTable(String tableName,SchemaInfo schemaInfo);
		
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
	
	Schema getSchema(SchemaInfo schemaInfo);
	
	/**
	 *  get current datasource own Schema
	 * 
	 * @param level
	 * @return Schema
	 */
	Schema getSchema(SchemaInfoLevel level);
	
	
	/**
	 * get currrent schema's procedure names.
	 * 
	 * @return Set<String>
	 */
	Set<String> getProcedureNames();
	
	/**
	 * get the procedure names
	 * 
	 * @param schemaInfo
	 * @return
	 */
//	Set<String> getProcedureNames(SchemaInfo schemaInfo);
	
	Procedure getProcedure(String procedureName);
	
//	Procedure getProcedure(String procedureName,SchemaInfo schemaInfo);
	
	Map<String,Procedure> getProcedures();
	
	/**
	 * get this database's all the Schema.
	 * 
	 * <p><b>In oracle it is a dangerous function.There are too many system tables</b></p>
	 * 
	 * @return Database
	 */
	@Deprecated
	Database getDatabase();
	
	/**
	 * get this database's all the Schema
	 * 
	 * <b>In oracle it is a dangerous function.There are too many system tables</b>
	 * 
	 * @param level
	 * @return Database
	 */
	@Deprecated
	Database getDatabase(SchemaInfoLevel level);
}
