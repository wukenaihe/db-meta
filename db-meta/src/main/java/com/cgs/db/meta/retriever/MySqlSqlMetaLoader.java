package com.cgs.db.meta.retriever;

import java.sql.DatabaseMetaData;

import com.cgs.db.meta.core.SchemaInfoLevel;
import com.cgs.db.meta.schema.Table;

public class MySqlSqlMetaLoader extends AbstractSqlMetaLoader{
	
	public MySqlSqlMetaLoader(){
		
	}
	
	public MySqlSqlMetaLoader(DatabaseMetaData databaseMetaData){
		super(databaseMetaData);
	}

	
	public Table invokeCrawlTableInfo(String tableName,SchemaInfoLevel level) {
		return crawlTableInfo(null, null, tableName, level);
	}
}
