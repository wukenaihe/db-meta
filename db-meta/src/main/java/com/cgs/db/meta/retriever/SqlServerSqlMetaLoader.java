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

public class SqlServerSqlMetaLoader extends AbstractSqlMetaLoader{

	
	public SqlServerSqlMetaLoader(){
		
	}
	
	public SqlServerSqlMetaLoader(DatabaseMetaData dbm){
		super(dbm);
	}

	
	public Table invokeCrawlTableInfo(String tableName, SchemaInfoLevel level) {
		return crawlTableInfo(null, null, tableName, level);
	}
	
	public Set<SchemaInfo> getSchemaInfos() {
		Set<SchemaInfo> schemaInfos=new HashSet<SchemaInfo>();
		try {
			ResultSet rs=dbm.getCatalogs();
			while(rs.next()){
//				String schemaName=rs.getString("TABLE_SCHEM");
				String catalogName=rs.getString("TABLE_CAT");
				SchemaInfo schemaInfo=new SchemaInfo(catalogName,null);
				schemaInfos.add(schemaInfo);
			}
		} catch (SQLException e) {
			throw new DatabaseMetaGetMetaException("Get database(Oracle) schema information error!", e);
		}
		return schemaInfos;
	}
}
