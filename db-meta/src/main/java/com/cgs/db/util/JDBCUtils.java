package com.cgs.db.util;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cgs.db.exception.CannotGetJdbcConnectionException;
import com.cgs.db.exception.DataAccessException;

public class JDBCUtils {
	private static Logger logger=LoggerFactory.getLogger(JDBCUtils.class);
	/**
	 * Close the given JDBC Connection and ignore any thrown exception.
	 * This is useful for typical finally blocks in manual JDBC code.
	 * @param con the JDBC Connection to close (may be {@code null})
	 */
	public static void closeConnection(Connection con) {
		if (con != null) {
			try {
				logger.debug("close connection "+con+" "+con.hashCode());
				con.close();
			}
			catch (SQLException ex) {
				logger.debug("Could not close JDBC Connection", ex);
			}
			catch (Throwable ex) {
				// We don't trust the JDBC driver: It might throw RuntimeException or Error.
				logger.debug("Unexpected exception on closing JDBC Connection", ex);
			}
		}
	}
	
	
	/**
	 * Get a JDBC Connection from datasource
	 * 
	 * @return Connection
	 * @throws DataAccessException
	 */
	public static Connection getConnection(DataSource dataSource) throws CannotGetJdbcConnectionException {
		Assert.notNull(dataSource, "no datasource can be find!");
		Connection con;
		try {
			con = dataSource.getConnection();
			logger.debug("Get the connection "+con+" "+con.hashCode());
		} catch (SQLException e) {
			throw new CannotGetJdbcConnectionException("Could not get JDBC Connection", e);
		}
		return con;
	}
}
