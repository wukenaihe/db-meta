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

import oracle.net.aso.d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cgs.db.exception.DatabaseMetaGetMetaException;
import com.cgs.db.exception.NonTransientDataAccessException;
import com.cgs.db.meta.core.SchemaInfoLevel;
import com.cgs.db.meta.schema.Constraint;
import com.cgs.db.meta.schema.Procedure;
import com.cgs.db.meta.schema.SchemaInfo;
import com.cgs.db.meta.schema.Table;
import com.cgs.db.meta.schema.TableConstraintType;
import com.cgs.db.util.Assert;
import com.cgs.db.util.JDBCUtils;
import com.cgs.db.util.ResultSetExtractor;

public class SqlServerMetaCrawler extends AbstractMetaCrawler {

	private static Logger logger = LoggerFactory.getLogger(SqlServerMetaCrawler.class);

	public final static String GET_CHECK_CONSTRAINT_SQL = "SELECT a.name name,a.type type,a.definition definition,b.name tableName FROM  "
			+ "(select OBJECT_ID,name from sys.tables where name=?) b left join " + "sys.check_constraints a  on a.parent_object_id=b.object_id";

	public final static String GET_UNIQUE_CONSTRAINT_SQL = "select a.name name,b.name columnName from "
			+ "(SELECT i.object_id,name,column_id FROM sys.indexes i JOIN sys.index_columns ic ON i.index_id = ic.index_id AND i.object_id = ic.object_id WHERE i.is_unique_constraint = 1 and i.object_id=(select object_id from sys.tables where name=?)) a "
			+ "left join sys.all_columns b on (a.object_id=b.object_id and a.column_id=b.column_id)";

	public final static String GET_PROCEDURENAMES_SQL = "select o.name name from sys.sql_modules procs "
			+ "left join sys.objects o on procs.object_id=o.object_id left join sys.schemas s " + "ON o.schema_id = s.schema_id where o.type='P'";

	public final static String GET_PROCEDURE_SQL = "select o.name name,procs.definition definition from sys.all_sql_modules procs "
			+ "left join sys.objects o on procs.object_id=o.object_id "
			+ "left join sys.schemas s ON o.schema_id = s.schema_id where o.type='P' and o.name=?";
	
	public final static String GET_PROCEDURES_SQL = "select o.name name,procs.definition definition from sys.all_sql_modules procs "
			+ "left join sys.objects o on procs.object_id=o.object_id "
			+ "left join sys.schemas s ON o.schema_id = s.schema_id where o.type='P'";

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
		String message = "Get database(sql server) " + tableName + "'s check constraint information error!";
		Map<String, Constraint> constraints2 = JDBCUtils.query(dbm, GET_CHECK_CONSTRAINT_SQL, message, new ResultSetExtractor<Map<String, Constraint>>() {

			public Map<String, Constraint> extractData(ResultSet rs) throws SQLException {
				Map<String, Constraint> constraints = new HashMap<String, Constraint>();
				while (rs.next()) {
					Constraint c = new Constraint();
					String name = rs.getString("name");
					String definition = rs.getString("definition");
					c.setName(name);
					c.setDefinition(definition);
					c.setTableConstraintType(TableConstraintType.check);
					constraints.put(name, c);
				}
				return constraints;
			}
		}, tableName);
		constraints.putAll(constraints2);
		return constraints;
	}

	private Map<String, Constraint> crawlUniqueConstraint(Map<String, Constraint> constraints, String tableName) {
		String message = "Get database(sql server) " + tableName + "'s unique constraint information error!";
		Map<String, Constraint> uniqueConstraints = JDBCUtils.query(dbm, GET_UNIQUE_CONSTRAINT_SQL, message, new ResultSetExtractor<Map<String, Constraint>>() {

			public Map<String, Constraint> extractData(ResultSet rs) throws SQLException {
				Map<String, Constraint> uniqueConstraints = new HashMap<String, Constraint>();
				while (rs.next()) {
					Constraint c = new Constraint();
					String name = rs.getString("name");
					String columnName = rs.getString("columnName");
					c.setName(name);
					c.setDefinition("[" + columnName + "] IS Unique");
					c.setTableConstraintType(TableConstraintType.unique);
					uniqueConstraints.put(name, c);
				}
				return uniqueConstraints;
			}
		}, tableName);

		constraints.putAll(uniqueConstraints);

		return constraints;
	}

	public Set<String> getProcedureNames(SchemaInfo schemaInfo) {
		Set<String> procedures;
		String message = "Get procedure name error";
		procedures = JDBCUtils.query(dbm, GET_PROCEDURENAMES_SQL, message, new ResultSetExtractor<Set<String>>() {

			public Set<String> extractData(ResultSet rs) throws SQLException {
				Set<String> procedures = new HashSet<String>();
				while (rs.next()) {
					String name = rs.getString("name");
					procedures.add(name);
				}
				return procedures;
			}
		});
		return procedures;
	}

	public Procedure getProcedure(String procedureName) {
		Assert.notNull(procedureName, "procedure name can not be null");
		String message="Get database(sql server) " + procedureName + "'s definition information error!";
		Procedure p=JDBCUtils.query(dbm, GET_PROCEDURE_SQL, message, new ResultSetExtractor<Procedure>() {

			public Procedure extractData(ResultSet rs) throws SQLException {
				Procedure p=null;
				while(rs.next()){
					if(p==null){
						p=new Procedure();
					}
					String name=rs.getString("name");
					String definition=rs.getString("definition");
					p.setName(name);
					p.appendStr(definition);
				}
				return p;
			}
		}, procedureName);
		return p;
	}
	
	public Map<String,Procedure> getProcedures(){
		String message="Get database(sql server)  definition information error!";
		
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
