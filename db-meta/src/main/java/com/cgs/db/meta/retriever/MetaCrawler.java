package com.cgs.db.meta.retriever;

import java.util.Map;
import java.util.Set;

import com.cgs.db.exception.DataAccessException;
import com.cgs.db.meta.core.SchemaInfoLevel;
import com.cgs.db.meta.schema.Column;
import com.cgs.db.meta.schema.Database;
import com.cgs.db.meta.schema.DatabaseInfo;
import com.cgs.db.meta.schema.ForeignKey;
import com.cgs.db.meta.schema.Function;
import com.cgs.db.meta.schema.PrimaryKey;
import com.cgs.db.meta.schema.Procedure;
import com.cgs.db.meta.schema.Schema;
import com.cgs.db.meta.schema.SchemaInfo;
import com.cgs.db.meta.schema.Table;
import com.cgs.db.meta.schema.Trigger;

public interface MetaCrawler {
	Set<String> getTableNames();
	
//	Table getTable(String tableName);
	
	Table getTable(String tableName,SchemaInfoLevel schemaInfoLevel);
	
	Table getTable(String tableName, SchemaInfoLevel level, SchemaInfo schemaInfo);
	
//	Map<String, Column> crawlColumnInfo(String tableName);
//	
//	PrimaryKey crawlPrimaryKey(String tableName);
//	
//	Map<String,ForeignKey> crawlForeignKey(String tableName);
	
	Set<SchemaInfo> getSchemaInfos();
	
	Schema getSchema(SchemaInfoLevel level);
	
	Schema getSchema(SchemaInfo schemaInfo,SchemaInfoLevel level);
	
	DatabaseInfo getDatabaseInfo();
	
	Database getDatabase(SchemaInfoLevel level);
	
	Set<String> getProcedureNames(SchemaInfo schemaInfo);
	
	Procedure getProcedure(String procedureName);
	
	Map<String,Procedure> getProcedures();
	
	Set<String> getTriggerNames();
	
	Trigger getTrigger(String triggerName);
	
	Map<String, Trigger> getTriggers();
	
	Set<String> getFunctionNames();
	
	Function getFunction(String name);
	
	Map<String, Function> getFunctions();
}
