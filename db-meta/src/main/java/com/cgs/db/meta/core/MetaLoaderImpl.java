package com.cgs.db.meta.core;

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
import com.cgs.db.meta.retriever.MetaCrawler;
import com.cgs.db.meta.retriever.MySqlSqlMetaLoader;
import com.cgs.db.meta.retriever.OracleSqlMetaLoader;
import com.cgs.db.meta.retriever.SqlServerSqlMetaLoader;
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

//	public static final int MYSQL = 1;
//	public static final int SQL_SERVER = 2;
//	public static final int ORACLE = 3;

	private DataSource dataSource;
	
	private MetaCrawlerFactory factory=new DefaultMetaCrawlerFactory();

	public void setFactory(MetaCrawlerFactory factory) {
		this.factory = factory;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public MetaLoaderImpl() {

	}

	public MetaLoaderImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Set<String> getTableNames() {
		Connection con = JDBCUtils.getConnection(dataSource);
		MetaCrawler metaCrawler=null;
		try{
			metaCrawler=factory.newInstance(con);
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
			metaCrawler=factory.newInstance(con);
			return metaCrawler.getTable(tableName,SchemaInfoLevel.standard());
		}catch(DataAccessException e){
			logger.debug(e.getMessage(),e);
			throw new DatabaseMetaGetMetaException("Get tables error!", e);
		}finally{
			JDBCUtils.closeConnection(con);
		}
	}
	
	public Table getTable(String tableName, SchemaInfoLevel schemaLevel) {
		Connection con = JDBCUtils.getConnection(dataSource);
		MetaCrawler metaCrawler=null;
		try{
			metaCrawler=factory.newInstance(con);
			return metaCrawler.getTable(tableName, schemaLevel);
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
			metaCrawler=factory.newInstance(con);
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
			metaCrawler=factory.newInstance(con);
			return metaCrawler.getSchema(SchemaInfoLevel.standard());
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
		Database database;
		try{
			metaCrawler=factory.newInstance(con);
			database=metaCrawler.getDatabase(SchemaInfoLevel.standard());
			return database;
		}catch(DataAccessException e){
			logger.debug(e.getMessage(),e);
			throw new DatabaseMetaGetMetaException("Get tables error!", e);
		}finally{
			JDBCUtils.closeConnection(con);
		}
	}


}
