package com.cgs.db.meta.retriever;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cgs.db.exception.DatabaseMetaGetMetaException;
import com.cgs.db.exception.NonTransientDataAccessException;
import com.cgs.db.meta.core.SchemaInfoLevel;
import com.cgs.db.meta.schema.Constraint;
import com.cgs.db.meta.schema.SchemaInfo;
import com.cgs.db.meta.schema.Table;
import com.cgs.db.meta.schema.TableConstraintType;
import com.cgs.db.util.JDBCUtils;

public class SqlServerMetaCrawler extends AbstractMetaCrawler {

	private static Logger logger = LoggerFactory.getLogger(SqlServerMetaCrawler.class);

	public final static String GET_CHECK_CONSTRAINT_SQL = "SELECT a.name name,a.type type,a.definition definition,b.name tableName FROM  "
			+ "(select OBJECT_ID,name from sys.tables where name=?) b left join " + "sys.check_constraints a  on a.parent_object_id=b.object_id";

	public final static String GET_UNIQUE_CONSTRAINT_SQL = "select a.name name,b.name columnName from "
			+ "(SELECT i.object_id,name,column_id FROM sys.indexes i JOIN sys.index_columns ic ON i.index_id = ic.index_id AND i.object_id = ic.object_id WHERE i.is_unique_constraint = 1 and i.object_id=(select object_id from sys.tables where name=?)) a "
			+ "left join sys.all_columns b on (a.object_id=b.object_id and a.column_id=b.column_id)";

	public SqlServerMetaCrawler() {

	}

	public SqlServerMetaCrawler(DatabaseMetaData dbm) {
		super(dbm);
	}

	public Table invokeCrawlTableInfo(String tableName, SchemaInfoLevel level) {
		return crawlTableInfo(null, null, tableName, level);
	}

	public Set<SchemaInfo> getSchemaInfos() {
		Set<SchemaInfo> schemaInfos = new HashSet<SchemaInfo>();
		try {
			ResultSet rs = dbm.getCatalogs();
			while (rs.next()) {
				// String schemaName=rs.getString("TABLE_SCHEM");
				String catalogName = rs.getString("TABLE_CAT");
				SchemaInfo schemaInfo = new SchemaInfo(catalogName, null);
				schemaInfos.add(schemaInfo);
			}
		} catch (SQLException e) {
			throw new DatabaseMetaGetMetaException("Get database(Oracle) schema information error!", e);
		}
		return schemaInfos;
	}

	public Set<String> getTableNames(SchemaInfo schemaInfo) {
		Set<String> tables = new HashSet<String>();
		ResultSet rs;
		try {
			rs = dbm.getTables(schemaInfo.getCatalogName(), schemaInfo.getSchemaName(), null, new String[] { "TABLE" });

			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				tables.add(tableName);
			}
		} catch (SQLException e) {
			throw new NonTransientDataAccessException(e.getMessage(), e);
		}

		return tables;
	}

	protected Map<String, Constraint> crawlConstraint(String tableName, SchemaInfo schemaInfo) {
		Map<String, Constraint> constraints = new HashMap<String, Constraint>();
		crawlCheckConstraint(constraints, tableName);
		crawlUniqueConstraint(constraints, tableName);
		return constraints;
	}

	private Map<String, Constraint> crawlCheckConstraint(Map<String, Constraint> constraints, String tableName) {
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			Connection con = dbm.getConnection();
			st = con.prepareStatement(GET_CHECK_CONSTRAINT_SQL);
			st.setString(1, tableName);
			rs = st.executeQuery();
			while (rs.next()) {
				Constraint c = new Constraint();
				String name = rs.getString("name");
				String definition = rs.getString("definition");
				c.setName(name);
				c.setDefinition(definition);
				c.setTableConstraintType(TableConstraintType.check);
				constraints.put(name, c);
			}
		} catch (SQLException e) {
			throw new DatabaseMetaGetMetaException("Get database(sql server) " + tableName + "'s check constraint information error!", e);
		} finally {
			JDBCUtils.closePreparedStatement(st);
			JDBCUtils.closeResultSet(rs);
		}

		return constraints;
	}

	private Map<String, Constraint> crawlUniqueConstraint(Map<String, Constraint> constraints, String tableName) {
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			Connection con = dbm.getConnection();
			st = con.prepareStatement(GET_UNIQUE_CONSTRAINT_SQL);
			st.setString(1, tableName);
			rs = st.executeQuery();
			while (rs.next()) {
				Constraint c = new Constraint();
				String name = rs.getString("name");
				String columnName=rs.getString("columnName");
				c.setName(name);
				c.setDefinition("["+columnName+"] IS Unique");
				c.setTableConstraintType(TableConstraintType.unique);
				constraints.put(name, c);
			}
		} catch (SQLException e) {
			throw new DatabaseMetaGetMetaException("Get database(sql server) " + tableName + "'s unique constraint information error!", e);
		} finally {
			JDBCUtils.closePreparedStatement(st);
			JDBCUtils.closeResultSet(rs);
		}

		return constraints;
	}

}
