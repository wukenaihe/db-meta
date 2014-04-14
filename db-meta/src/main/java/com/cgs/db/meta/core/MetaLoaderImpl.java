package com.cgs.db.meta.core;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cgs.db.exception.CannotGetJdbcConnectionException;
import com.cgs.db.exception.DataAccessException;
import com.cgs.db.exception.DatabaseMetaGetMetaException;
import com.cgs.db.exception.NonTransientDataAccessException;
import com.cgs.db.meta.retriever.MetaCrawler;
import com.cgs.db.meta.retriever.MySqlMetaCrawler;
import com.cgs.db.meta.retriever.OracleMetaCrawler;
import com.cgs.db.meta.retriever.SqlServerMetaCrawler;
import com.cgs.db.meta.schema.Database;
import com.cgs.db.meta.schema.DatabaseInfo;
import com.cgs.db.meta.schema.Procedure;
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
		return getTable(tableName, SchemaInfoLevel.standard());
	}
	
	public Table getTable(String tableName,SchemaInfoLevel level) {
		return getTable(tableName, level, null);
	}
	
	public Table getTable(String tableName, SchemaInfo schemaInfo) {
		return getTable(tableName, SchemaInfoLevel.standard(), schemaInfo);
	}
	
	public Table getTable(String tableName, SchemaInfoLevel schemaLevel,SchemaInfo schemaInfo) {
		Connection con = JDBCUtils.getConnection(dataSource);
		MetaCrawler metaCrawler=null;
		try{
			metaCrawler=factory.newInstance(con);
			return metaCrawler.getTable(tableName, schemaLevel,schemaInfo);
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

	public Schema getSchema(SchemaInfoLevel level) {
		Connection con = JDBCUtils.getConnection(dataSource);
		MetaCrawler metaCrawler=null;
		try{
			metaCrawler=factory.newInstance(con);
			return metaCrawler.getSchema(level);
		}catch(DataAccessException e){
			logger.debug(e.getMessage(),e);
			throw new DatabaseMetaGetMetaException("Get tables error!", e);
		}finally{
			JDBCUtils.closeConnection(con);
		}
	}
	
	public Schema getSchema(SchemaInfo schemaInfo) {
		Connection con = JDBCUtils.getConnection(dataSource);
		MetaCrawler metaCrawler=null;
		try{
			metaCrawler=factory.newInstance(con);
			return metaCrawler.getSchema(schemaInfo,SchemaInfoLevel.standard());
		}catch(DataAccessException e){
			logger.debug(e.getMessage(),e);
			throw new DatabaseMetaGetMetaException("Get tables error!", e);
		}finally{
			JDBCUtils.closeConnection(con);
		}
	}


	public Database getDatabase(SchemaInfoLevel level) {
		Connection con = JDBCUtils.getConnection(dataSource);
		MetaCrawler metaCrawler=null;
		Database database;
		try{
			metaCrawler=factory.newInstance(con);
			database=metaCrawler.getDatabase(level);
			return database;
		}catch(DataAccessException e){
			logger.debug(e.getMessage(),e);
			throw new DatabaseMetaGetMetaException("Get tables error!", e);
		}finally{
			JDBCUtils.closeConnection(con);
		}
	}

	public Schema getSchema() {
		return getSchema(SchemaInfoLevel.standard());
	}

	public Database getDatabase() {
		return getDatabase(SchemaInfoLevel.standard());
	}

	private Set<String> getProcedureNames(SchemaInfo schemaInfo) {
		Connection con = JDBCUtils.getConnection(dataSource);
		MetaCrawler metaCrawler=null;
		Set<String> procedureNames;
		try{
			metaCrawler=factory.newInstance(con);
			procedureNames=metaCrawler.getProcedureNames(schemaInfo);
			return procedureNames;
		}catch(DataAccessException e){
			logger.debug(e.getMessage(),e);
			throw new DatabaseMetaGetMetaException("Get tables error!", e);
		}finally{
			JDBCUtils.closeConnection(con);
		}
	}
	
	public Set<String> getProcedureNames(){
		return getProcedureNames(null);
	}

	public Procedure getProcedure(String procedureName) {
		Connection con = JDBCUtils.getConnection(dataSource);
		MetaCrawler metaCrawler=null;
		Procedure p;
		try{
			metaCrawler=factory.newInstance(con);
			p=metaCrawler.getProcedure(procedureName);
			return p;
		}catch(DataAccessException e){
			logger.debug(e.getMessage(),e);
			throw new DatabaseMetaGetMetaException("Get tables error!", e);
		}finally{
			JDBCUtils.closeConnection(con);
		}
	}

	public Map<String,Procedure> getProcedures() {
		Connection con = JDBCUtils.getConnection(dataSource);
		MetaCrawler metaCrawler=null;
		Map<String, Procedure> p;
		try{
			metaCrawler=factory.newInstance(con);
			p=metaCrawler.getProcedures();
			return p;
		}catch(DataAccessException e){
			logger.debug(e.getMessage(),e);
			throw new DatabaseMetaGetMetaException("Get tables error!", e);
		}finally{
			JDBCUtils.closeConnection(con);
		}
	}

	

}
