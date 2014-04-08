package com.cgs.db.meta.retriever;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cgs.db.exception.CannotGetJdbcConnectionException;
import com.cgs.db.exception.DataAccessException;
import com.cgs.db.exception.DatabaseMetaGetMetaException;
import com.cgs.db.exception.NonTransientDataAccessException;
import com.cgs.db.meta.core.MetaLoader;
import com.cgs.db.meta.schema.Database;
import com.cgs.db.meta.schema.DatabaseInfo;
import com.cgs.db.meta.schema.Schema;
import com.cgs.db.meta.schema.SchemaInfo;
import com.cgs.db.meta.schema.Table;
import com.cgs.db.util.Assert;
import com.cgs.db.util.JDBCUtils;

/**
 * 
 * According the datasource information,decide which native metaLoader implement
 * class to use.
 * 
 * 
 * @author xumh
 * 
 */
public class MetaLoaderImpl implements MetaLoader {
	private static Logger logger = LoggerFactory.getLogger(MetaLoaderImpl.class);

	public static final int MYSQL = 1;
	public static final int SQL_SERVER = 2;
	public static final int ORACLE = 3;

	private DataSource dataSource;
	

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public MetaLoaderImpl() {

	}

	public MetaLoaderImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}


	private DatabaseMetaData getDatabaseMetaData(Connection connection) {
		DatabaseMetaData dbm;
		try {
			dbm = connection.getMetaData();
		} catch (SQLException e) {
			JDBCUtils.closeConnection(connection);
			throw new NonTransientDataAccessException("Could not get DatabaseMeta");
		}
		return dbm;
	}

	private MetaCrawler getMetaCrawler(Connection conn) {
		int type=-1;
		DatabaseMetaData databaseMetaData=null;
		try {
			type = getProductName(conn);
			databaseMetaData=getDatabaseMetaData(conn);
		} catch (SQLException e) {
			logger.debug(e.getMessage(),e);
			throw new NonTransientDataAccessException("can not get database information", e);
		}
		MetaCrawler mc = null;
		switch (type) {
		case MYSQL:
			mc=new MySqlSqlMetaLoader(databaseMetaData);
			break;
		case ORACLE:
			mc = new OracleSqlMetaLoader(databaseMetaData);
			break;
		case SQL_SERVER:
			mc=new SqlServerSqlMetaLoader(databaseMetaData);
			break;
		default:
			break;
		}
		return mc;
	}

	private int getProductName(Connection conn) throws SQLException {
		String product = null;
		try {
			DatabaseMetaData dbm = conn.getMetaData();
			product = dbm.getDatabaseProductName();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (product.equals("MySQL")) {
			return MYSQL;
		} else if (product.equals("Oracle")) {
			return ORACLE;
		} else if (product.equals("Microsoft SQL Server")) {
			return SQL_SERVER;
		} else {
			return 0;
		}
	}

	public Set<String> getTableNames() {
		Connection con = JDBCUtils.getConnection(dataSource);
		MetaCrawler metaCrawler=null;
		try{
			metaCrawler=getMetaCrawler(con);
			return metaCrawler.getTableNames();
		}catch(DataAccessException e){
			logger.debug(e.getMessage(),e);
			throw new DatabaseMetaGetMetaException("Get tables error!", e);
		}finally{
			JDBCUtils.closeConnection(con);
		}
	}

	public Table getTable(String tableName) {
		Connection con = JDBCUtils.getConnection(dataSource);
		MetaCrawler metaCrawler=null;
		try{
			metaCrawler=getMetaCrawler(con);
			return metaCrawler.getTable(tableName);
		}catch(DataAccessException e){
			logger.debug(e.getMessage(),e);
			throw new DatabaseMetaGetMetaException("Get tables error!", e);
		}finally{
			JDBCUtils.closeConnection(con);
		}
	}

	public Set<SchemaInfo> getSchemaInfos() {
		Connection con = JDBCUtils.getConnection(dataSource);
		MetaCrawler metaCrawler=null;
		try{
			metaCrawler=getMetaCrawler(con);
			return metaCrawler.getSchemaInfos();
		}catch(DataAccessException e){
			logger.debug(e.getMessage(),e);
			throw new DatabaseMetaGetMetaException("Get tables error!", e);
		}finally{
			JDBCUtils.closeConnection(con);
		}
	}

	public Schema getSchema() {
		Connection con = JDBCUtils.getConnection(dataSource);
		MetaCrawler metaCrawler=null;
		try{
			metaCrawler=getMetaCrawler(con);
			return metaCrawler.getSchema();
		}catch(DataAccessException e){
			logger.debug(e.getMessage(),e);
			throw new DatabaseMetaGetMetaException("Get tables error!", e);
		}finally{
			JDBCUtils.closeConnection(con);
		}
	}

	public Database getDatabase() {
		Connection con = JDBCUtils.getConnection(dataSource);
		MetaCrawler metaCrawler=null;
		Database database=new Database();
		try{
			metaCrawler=getMetaCrawler(con);
			DatabaseInfo databaseInfo=metaCrawler.getDatabaseInfo();
			database.setDatabaseInfo(databaseInfo);
			
			Set<Schema> schemaSet=new HashSet<Schema>();
			Set<SchemaInfo> schemas=metaCrawler.getSchemaInfos();
			for (SchemaInfo schemaInfo : schemas) {
				Schema schema=metaCrawler.getSchema(schemaInfo);
				schemaSet.add(schema);
			}
			database.setSchemas(schemaSet);
			return database;
		}catch(DataAccessException e){
			logger.debug(e.getMessage(),e);
			throw new DatabaseMetaGetMetaException("Get tables error!", e);
		}finally{
			JDBCUtils.closeConnection(con);
		}
	}


}
