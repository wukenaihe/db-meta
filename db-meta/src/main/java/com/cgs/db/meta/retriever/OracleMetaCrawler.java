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

import com.cgs.db.exception.DataAccessException;
import com.cgs.db.exception.DatabaseMetaGetMetaException;
import com.cgs.db.exception.NonTransientDataAccessException;
import com.cgs.db.meta.core.SchemaInfoLevel;
import com.cgs.db.meta.schema.Constraint;
import com.cgs.db.meta.schema.Procedure;
import com.cgs.db.meta.schema.SchemaInfo;
import com.cgs.db.meta.schema.Table;
import com.cgs.db.meta.schema.TableConstraintType;
import com.cgs.db.util.JDBCUtils;
import com.cgs.db.util.ResultSetExtractor;

public class OracleMetaCrawler extends AbstractMetaCrawler {
	private Logger logger = LoggerFactory.getLogger(OracleMetaCrawler.class);

	public final static String GET_CONSTRAINT_SQL = "select constraint_name name,constraint_type type,search_condition definition,deferrable from All_Constraints where owner=? "
			+ "and TABLE_NAME=? and (Constraint_Type='C' or Constraint_Type='U') ";

	public final static String GET_PROCEDURE_SQL = "select name,text from user_source where type='PROCEDURE' and name=? order by Line";

	public final static String GET_PROCEDURES_SQL = "select name,text from user_source where type='PROCEDURE' order by name,Line";

	public OracleMetaCrawler() {

	}

	public OracleMetaCrawler(DatabaseMetaData databaseMetaData) {
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
				if (!isRubbishTable(tableName)) {
					tables.add(tableName);
				}
			}

		} catch (SQLException e) {
			logger.debug(e.getMessage());
			throw new NonTransientDataAccessException(e.getMessage(), e);
		}
		return tables;
	}

	public Set<String> getTableNames(SchemaInfo schemaInfo) {
		Set<String> tables = new HashSet<String>();
		ResultSet rs;
		try {
			rs = dbm.getTables(schemaInfo.getCatalogName(), schemaInfo.getSchemaName(), null, new String[] { "TABLE" });

			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				if (!isRubbishTable(tableName)) {
					tables.add(tableName);
				}
			}
		} catch (SQLException e) {
			throw new NonTransientDataAccessException(e.getMessage(), e);
		}

		return tables;
	}

	public Table invokeCrawlTableInfo(String tableName, SchemaInfoLevel level) {
		logger.trace("Get schema name by username");
		String schemaName = null;
		try {
			schemaName = dbm.getUserName();
		} catch (SQLException e) {
			logger.debug("can not get schema name, so see schema as null");
		}

		Table table = crawlTableInfo(null, schemaName, tableName, level);
		return table;
	}

	public Set<SchemaInfo> getSchemaInfos() {
		Set<SchemaInfo> schemaInfos = new HashSet<SchemaInfo>();
		try {
			ResultSet rs = dbm.getSchemas();
			while (rs.next()) {
				String schemaName = rs.getString("TABLE_SCHEM");
				SchemaInfo schemaInfo = new SchemaInfo(null, schemaName);
				schemaInfos.add(schemaInfo);
			}
		} catch (SQLException e) {
			throw new DatabaseMetaGetMetaException("Get database(Oracle) schema information error!", e);
		}
		return schemaInfos;
	}

	protected SchemaInfo getSchemaInfo() {
		String schema = null;
		try {
			schema = dbm.getUserName();
		} catch (SQLException e) {
			throw new NonTransientDataAccessException(e.getMessage(), e);
		}

		return new SchemaInfo(null, schema);
	}

	private boolean isRubbishTable(String tableName) {
		if (tableName == null || tableName.length() > 30) {
			return true;
		}
		String rex = "[a-zA-Z_0-9$#]+";
		return !tableName.matches(rex);
	}


	protected Map<String, Constraint> crawlConstraint(String tableName, SchemaInfo schemaInfo) {
		String schema = null;
		if (schemaInfo == null) {
			try {
				schema = dbm.getUserName();
			} catch (SQLException e) {
				throw new DatabaseMetaGetMetaException("Get database(Oracle) user name error!", e);
			}
		} else {
			schema = schemaInfo.getSchemaName();
		}
		String message = "Get database(Oracle) " + tableName + "'s constraint information error!";

		return JDBCUtils.query(dbm, GET_CONSTRAINT_SQL, message, new ResultSetExtractor<Map<String, Constraint>>() {

			public Map<String, Constraint> extractData(ResultSet rs) throws SQLException {
				Map<String, Constraint> constraints = new HashMap<String, Constraint>();
				while (rs.next()) {
					String name = rs.getString("name");
					String type = rs.getString("type");
					String definition = rs.getString("definition");
					String deferrable = rs.getString("deferrable");
					Constraint c = new Constraint();
					c.setName(name);
					c.setTableConstraintType(parseConstraintType(type));
					c.setDefinition(definition);
					if (deferrable != null && deferrable.equals("DEFERRABLE")) {
						c.setDeferrable(true);
					} else {
						c.setDeferrable(false);
					}
					constraints.put(name, c);
				}
				return constraints;
			}
		}, schema, tableName);
	}

	private TableConstraintType parseConstraintType(String type) {
		if (type == null) {
			return TableConstraintType.unknown;
		} else if (type.equals("C")) {
			return TableConstraintType.check;
		} else if (type.equals("U")) {
			return TableConstraintType.unique;
		} else {
			return TableConstraintType.unknown;
		}
	}

	public Set<String> getProcedureNames(SchemaInfo schemaInfo) {
		Set<String> procedures = new HashSet<String>();
		ResultSet rs = null;
		try {
			if (schemaInfo == null) {
				String userName = dbm.getUserName();
				rs = dbm.getProcedures(null, userName, null);
			} else {
				rs = dbm.getProcedures(schemaInfo.getCatalogName(), schemaInfo.getSchemaName(), null);
			}
			while (rs.next()) {
				String tableName = rs.getString("PROCEDURE_NAME");
				procedures.add(tableName);
			}
		} catch (SQLException e) {
			throw new NonTransientDataAccessException(e.getMessage(), e);
		} finally {
			JDBCUtils.closeResultSet(rs);
		}

		return procedures;
	}

	public Procedure getProcedure(String procedureName) {
		String message = "Get database(Oracle) " + procedureName + "'s definition information error!";
		return JDBCUtils.query(dbm, GET_PROCEDURE_SQL, message, new ResultSetExtractor<Procedure>() {

			public Procedure extractData(ResultSet rs) throws SQLException {
				Procedure p = null;
				while (rs.next()) {
					if (p == null) {
						p = new Procedure();
						p.setName(rs.getString("name"));
					}
					p.appendStr(rs.getString("text"));
				}
				return p;
			}

		}, procedureName);
	}

	public Map<String, Procedure> getProcedures() {
		String message = "Get database(Oracle)  definition information error!";
		return JDBCUtils.query(dbm, GET_PROCEDURES_SQL, message, new ResultSetExtractor<Map<String, Procedure>>() {

			public Map<String, Procedure> extractData(ResultSet rs) throws SQLException {
				Map<String, Procedure> procedures = new HashMap<String, Procedure>();
				Procedure p;
				while (rs.next()) {
					String name = rs.getString("name");
					p = procedures.get(name);
					if (p == null) {
						p = new Procedure();
						p.setName(rs.getString("name"));
						procedures.put(name, p);
					}
					p.appendStr(rs.getString("text"));
				}
				return procedures;
			}
		});
	}
}
