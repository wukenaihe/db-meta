package com.cgs.db.meta.retriever;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cgs.db.exception.DatabaseMetaGetMetaException;
import com.cgs.db.exception.NonTransientDataAccessException;
import com.cgs.db.meta.core.SchemaInfoLevel;
import com.cgs.db.meta.schema.ForeignKeyColumnReference;
import com.cgs.db.meta.schema.SchemaInfo;
import com.cgs.db.meta.schema.Table;

public class OracleSqlMetaLoader extends AbstractSqlMetaLoader {
	private Logger logger = LoggerFactory.getLogger(OracleSqlMetaLoader.class);

	public OracleSqlMetaLoader() {

	}

	public OracleSqlMetaLoader(DatabaseMetaData databaseMetaData) {
		super(databaseMetaData);
	}

	/*
	 * In oracle, every user have it's deafult schema(schema name==userName).
	 * Here we return the tableNames,which this owner has;
	 * 
	 * @see com.cgs.db.meta.retriever.AbstractSqlMetaLoader#getTableNames()
	 */
	public Set<String> getTableNames() {
		Set<String> tables = new HashSet<String>();
		try {
			String userName = dbm.getUserName();
			ResultSet rs = dbm.getTables(null, userName, null, new String[] { "TABLE" });

			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				tables.add(tableName);
			}
			
		} catch (SQLException e) {
			logger.debug(e.getMessage());
			throw new NonTransientDataAccessException(e.getMessage(), e);
		}
		return tables;
	}

	
	public Table invokeCrawlTableInfo(String tableName,SchemaInfoLevel level) {
		logger.trace("Get schema name by username");
		String schemaName=null;
		try {
			schemaName=dbm.getUserName();
		} catch (SQLException e) {
			logger.debug("can not get schema name, so see schema as null");
		}
		
		Table table=crawlTableInfo(null, schemaName, tableName, level);
		return table;
	}

	
	public Set<SchemaInfo> getSchemaInfos() {
		Set<SchemaInfo> schemaInfos=new HashSet<SchemaInfo>();
		try {
			ResultSet rs=dbm.getSchemas();
			while(rs.next()){
				String schemaName=rs.getString("TABLE_SCHEM");
				SchemaInfo schemaInfo=new SchemaInfo(null,schemaName);
				schemaInfos.add(schemaInfo);
			}
		} catch (SQLException e) {
			throw new DatabaseMetaGetMetaException("Get database(Oracle) schema information error!", e);
		}
		return schemaInfos;
	}
}
