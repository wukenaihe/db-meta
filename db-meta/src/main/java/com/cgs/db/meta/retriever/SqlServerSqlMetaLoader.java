package com.cgs.db.meta.retriever;

import java.sql.DatabaseMetaData;

import com.cgs.db.meta.core.SchemaInfoLevel;
import com.cgs.db.meta.schema.Table;

public class SqlServerSqlMetaLoader extends AbstractSqlMetaLoader{

	
	public SqlServerSqlMetaLoader(){
		
	}
	
	public SqlServerSqlMetaLoader(DatabaseMetaData dbm){
		super(dbm);
	}

	
	public Table invokeCrawlTableInfo(String tableName, SchemaInfoLevel level) {
		return crawlTableInfo(null, null, tableName, level);
	}
	
	
}
