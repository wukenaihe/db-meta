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

public class OracleMetaCrawler extends AbstractMetaCrawler {
	private Logger logger = LoggerFactory.getLogger(OracleMetaCrawler.class);

	public final static String GET_CONSTRAINT_SQL = "select constraint_name name,constraint_type type,search_condition definition,deferrable from All_Constraints where owner=? "
			+ "and TABLE_NAME=? and (Constraint_Type='C' or Constraint_Type='U') ";

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
		ResultSet rs=null;
		PreparedStatement st = null;
		Map<String, Constraint> constraints = new HashMap<String, Constraint>();
		try {
			Connection con = dbm.getConnection();
			st = con.prepareStatement(GET_CONSTRAINT_SQL);
			String schema;
			if (schemaInfo == null) {
				schema = dbm.getUserName();
			} else {
				schema = schemaInfo.getSchemaName();
			}
			st.setString(1, schema);
			st.setString(2, tableName);
			rs = st.executeQuery();

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

		} catch (SQLException e) {
			throw new DatabaseMetaGetMetaException("Get database(Oracle) "+tableName+"'s constraint information error!", e);
		}finally{
			JDBCUtils.closePreparedStatement(st);
			JDBCUtils.closeResultSet(rs);
		}

		return constraints;
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

}
