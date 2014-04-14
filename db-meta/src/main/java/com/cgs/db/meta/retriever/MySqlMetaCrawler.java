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
import com.cgs.db.meta.core.SchemaInfoLevel;
import com.cgs.db.meta.schema.Constraint;
import com.cgs.db.meta.schema.Procedure;
import com.cgs.db.meta.schema.SchemaInfo;
import com.cgs.db.meta.schema.Table;
import com.cgs.db.meta.schema.TableConstraintType;
import com.cgs.db.util.JDBCUtils;
import com.cgs.db.util.ResultSetExtractor;

public class MySqlMetaCrawler extends AbstractMetaCrawler {

	private static Logger logger = LoggerFactory.getLogger(MySqlMetaCrawler.class);
	public static final String GET_CONSTRAINT_SQL = "select a.CONSTRAINT_NAME name,b.column_name columnName from information_schema.TABLE_CONSTRAINTS a left join information_schema.KEY_COLUMN_USAGE b "
			+ "on a.CONSTRAINT_NAME = b.CONSTRAINT_NAME where a.CONSTRAINT_SCHEMA = ? and a.TABLE_NAME = ? and a.CONSTRAINT_TYPE= 'UNIQUE'";

	public static final String GET_PROCEDURE_SQL = "select routine_name name,routine_definition definition from information_schema.ROUTINES "
			+ "where ROUTINE_TYPE='PROCEDURE' and routine_name =? and ROUTINE_SCHEMA=?";
	
	public static final String GET_PROCEDURES_SQL = "select routine_name name,routine_definition definition from information_schema.ROUTINES "
			+ "where ROUTINE_TYPE='PROCEDURE'";

	public MySqlMetaCrawler() {

	}

	public MySqlMetaCrawler(DatabaseMetaData databaseMetaData) {
		super(databaseMetaData);
	}

	public Table invokeCrawlTableInfo(String tableName, SchemaInfoLevel level) {
		return crawlTableInfo(null, null, tableName, level);
	}

	public Set<SchemaInfo> getSchemaInfos() {
		Set<SchemaInfo> schemaInfos = new HashSet<SchemaInfo>();
		try {
			ResultSet rs = dbm.getCatalogs();
			while (rs.next()) {
				String catalogName = rs.getString("TABLE_CAT");
				SchemaInfo schemaInfo = new SchemaInfo(catalogName, null);
				schemaInfos.add(schemaInfo);
			}
		} catch (SQLException e) {
			throw new DatabaseMetaGetMetaException("Get database(MySql) schema information error!", e);
		}
		return schemaInfos;
	}

	/*
	 * MySql only Primary key ,Foreign Key and Unique.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cgs.db.meta.retriever.AbstractMetaCrawler#crawlConstraint(java.lang
	 * .String, com.cgs.db.meta.schema.SchemaInfo)
	 */
	protected Map<String, Constraint> crawlConstraint(String tableName, SchemaInfo schemaInfo) {
		String message = "Get database(MySql) " + tableName + "'s constraint information error!";
		Connection con;
		String schema;
		try {
			con = dbm.getConnection();
			if (schemaInfo == null) {
				schema = con.getCatalog();
			} else {
				schema = schemaInfo.getCatalogName();
			}
		} catch (SQLException e) {
			throw new DatabaseMetaGetMetaException("Get database(mysql) cataglog name error!", e);
		}

		Map<String, Constraint> constraints = JDBCUtils.query(dbm, GET_CONSTRAINT_SQL, message, new ResultSetExtractor<Map<String, Constraint>>() {

			public Map<String, Constraint> extractData(ResultSet rs) throws SQLException {
				Map<String, Constraint> constraints = new HashMap<String, Constraint>();
				while (rs.next()) {
					String name = rs.getString("name");
					String columnName = rs.getString("columnName");
					Constraint c = new Constraint();
					c.setName(name);
					c.setDefinition(columnName + " IS UNIQUE");
					c.setTableConstraintType(TableConstraintType.unique);
					constraints.put(name, c);
				}
				return constraints;
			}
		}, schema, tableName);
		return constraints;
	}

	public Procedure getProcedure(String procedureName) {
		String message="Get database(MySql)  procedure information error!";
		String schema=getSchemaName();
	
		Procedure p = JDBCUtils.query(dbm, GET_PROCEDURE_SQL, message, new ResultSetExtractor<Procedure>() {

			public Procedure extractData(ResultSet rs) throws SQLException {
				Procedure p=null;
				while (rs.next()) {
					String name = rs.getString("name");
					String definition = rs.getString("definition");
					if (p == null) {
						p = new Procedure();
						p.setName(rs.getString("name"));
					}
					p.appendStr(definition);
				}
				return p;
			}
		}, procedureName,schema);
		
		return p;

	}
	
	private String getSchemaName(){
		String schema;
		try {
			Connection con = dbm.getConnection();
			schema=con.getCatalog();
		} catch (SQLException e) {
			throw new DatabaseMetaGetMetaException("Get database(mysql) cataglog name error!", e);
		}
		return schema;
	}

	public Map<String, Procedure> getProcedures() {
String message="Get database(mysql)  definition information error!";
		
		Map<String, Procedure> procedures=JDBCUtils.query(dbm, GET_PROCEDURES_SQL, message, new ResultSetExtractor<Map<String, Procedure>>() {

			public Map<String, Procedure> extractData(ResultSet rs) throws SQLException {
				Map<String, Procedure> procedures=new HashMap<String, Procedure>();
				while(rs.next()){
					Procedure p=new Procedure();
					String name=rs.getString("name");
					String definition=rs.getString("definition");
					p.setName(name);
					p.appendStr(definition);
					procedures.put(name, p);
				}
				return procedures;
			}
		});
		
		return procedures;
	}
}
