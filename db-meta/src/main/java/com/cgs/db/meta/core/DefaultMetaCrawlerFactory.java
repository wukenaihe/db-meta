package com.cgs.db.meta.core;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import com.cgs.db.exception.NonTransientDataAccessException;
import com.cgs.db.meta.retriever.MetaCrawler;
import com.cgs.db.meta.retriever.MySqlSqlMetaLoader;
import com.cgs.db.meta.retriever.OracleSqlMetaLoader;
import com.cgs.db.meta.retriever.SqlServerSqlMetaLoader;
import com.cgs.db.util.JDBCUtils;

/**
 * can be inherit and override the newInstance function
 * 
 * @author xumh
 *
 */
public class DefaultMetaCrawlerFactory implements MetaCrawlerFactory{
	
	public static final int MYSQL = 1;
	public static final int SQL_SERVER = 2;
	public static final int ORACLE = 3;

	public MetaCrawler newInstance(Connection con) {
		String product=getProductName(con);
		DatabaseMetaData dbm=getDatabaseMetaData(con);
		if (product.equals("MySQL")) {
			return new MySqlSqlMetaLoader(dbm);
		} else if (product.equals("Oracle")) {
			return new OracleSqlMetaLoader(dbm);
		} else if (product.equals("Microsoft SQL Server")) {
			return new SqlServerSqlMetaLoader(dbm);
		} else {
			return null;
		}
	}

	protected String getProductName(Connection conn) {
		String product = null;
		try {
			DatabaseMetaData dbm = conn.getMetaData();
			product = dbm.getDatabaseProductName();
			return product;

		} catch (SQLException e) {
			throw new NonTransientDataAccessException("can not get database product information!",e);
		}
	}
	
	protected DatabaseMetaData getDatabaseMetaData(Connection connection) {
		DatabaseMetaData dbm;
		try {
			dbm = connection.getMetaData();
		} catch (SQLException e) {
			JDBCUtils.closeConnection(connection);
			throw new NonTransientDataAccessException("Could not get DatabaseMeta");
		}
		return dbm;
	}
	
	
}
