package com.cgs.db.meta.retriever;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import com.cgs.db.exception.DatabaseMetaGetMetaException;
import com.cgs.db.meta.core.SchemaInfoLevel;
import com.cgs.db.meta.schema.SchemaInfo;
import com.cgs.db.meta.schema.Table;

public class MySqlMetaCrawler extends AbstractMetaCrawler{
	
	public MySqlMetaCrawler(){
		
	}
	
	public MySqlMetaCrawler(DatabaseMetaData databaseMetaData){
		super(databaseMetaData);
	}

	
	public Table invokeCrawlTableInfo(String tableName,SchemaInfoLevel level) {
		return crawlTableInfo(null, null, tableName, level);
	}

	
	public Set<SchemaInfo> getSchemaInfos() {
		Set<SchemaInfo> schemaInfos=new HashSet<SchemaInfo>();
		try {
			ResultSet rs=dbm.getCatalogs();
			while(rs.next()){
				String catalogName=rs.getString("TABLE_CAT");
				SchemaInfo schemaInfo=new SchemaInfo(catalogName,null);
				schemaInfos.add(schemaInfo);
			}
		} catch (SQLException e) {
			throw new DatabaseMetaGetMetaException("Get database(MySql) schema information error!", e);
		}
		return schemaInfos;
	}
}
