package com.cgs.db.meta.retriever;

import java.util.Map;
import java.util.Set;

import com.cgs.db.meta.schema.Column;
import com.cgs.db.meta.schema.DatabaseInfo;
import com.cgs.db.meta.schema.ForeignKey;
import com.cgs.db.meta.schema.PrimaryKey;
import com.cgs.db.meta.schema.Schema;
import com.cgs.db.meta.schema.SchemaInfo;
import com.cgs.db.meta.schema.Table;

public interface MetaCrawler {
	Set<String> getTableNames();
	
	Table getTable(String tableName);
	
	Map<String, Column> crawlColumnInfo(String tableName);
	
	PrimaryKey crawlPrimaryKey(String tableName);
	
	Map<String,ForeignKey> crawlForeignKey(String tableName);
	
	Set<SchemaInfo> getSchemaInfos();
	
	Schema getSchema();
	
	DatabaseInfo getDatabaseInfo();
}
