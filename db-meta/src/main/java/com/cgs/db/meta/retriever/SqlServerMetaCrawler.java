package com.cgs.db.meta.retriever;

import java.sql.DatabaseMetaData;
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
import com.cgs.db.meta.schema.Function;
import com.cgs.db.meta.schema.Procedure;
import com.cgs.db.meta.schema.SchemaInfo;
import com.cgs.db.meta.schema.Table;
import com.cgs.db.meta.schema.TableConstraintType;
import com.cgs.db.meta.schema.Trigger;
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
	
	
	public final static String GET_TRIGGERNAMES_SQL="select tb1.name from Sysobjects tb1 join sys.objects"
			+ " tb2 on tb1.parent_obj=tb2.object_id where tb1.type='TR'";
	
	public final static String GET_TRIGGER_TABLENAME_SQL="select tb2.name name from Sysobjects tb1 join sys.objects tb2 "
			+ "on tb1.parent_obj=tb2.object_id where tb1.type='TR' and tb1.name=?";
	
	public final static String GET_TRIGGER_SQL="exec sp_helptext ?";
	
	
	public final static String GET_TRIGGER_BYTABLE_SQL="select tb1.name from Sysobjects tb1 join sys.objects"
			+ " tb2 on tb1.parent_obj=tb2.object_id where tb1.type='TR' and tb2.name = ?";
	
	
	public final static String GET_FUNCTIONNAMES_SQL="select o.name from sys.sql_modules procs left join "
			+ "sys.objects o on procs.object_id=o.object_id left join sys.schemas s ON o.schema_id = s.schema_id where o.type='FN'";
	
	public final static String GET_FUNCTION_SQL="select o.name name,procs.definition definition from "
			+ "sys.sql_modules procs left join sys.objects o on procs.object_id=o.object_id left join "
			+ "sys.schemas s ON o.schema_id = s.schema_id where o.type='FN' and o.name=?";
	
	public final static String GET_FUNCTIONS_SQL="select o.name name,procs.definition definition from "
			+ "sys.sql_modules procs left join sys.objects o on procs.object_id=o.object_id left join "
			+ "sys.schemas s ON o.schema_id = s.schema_id where o.type='FN'";

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
	
	public Set<String> getTriggerNames(){
		String message="Get database(Sql server) current user's trigger names";
		Set<String> triggers=JDBCUtils.query(dbm, GET_TRIGGERNAMES_SQL, message, new ResultSetExtractor<Set<String>>() {

			public Set<String> extractData(ResultSet rs) throws SQLException {
				Set<String> names=new HashSet<String>();
				while(rs.next()){
					String name=rs.getString("name");
					names.add(name);
				}
				return names;
			}
		});
		return triggers;
	}
	
	public Trigger getTrigger(String triggerName){
		Assert.notNull(triggerName, "triggerName can not be null");
		String message = "Get database(Sql server) " + triggerName + "'s definition information error!";
		Trigger trigger=JDBCUtils.query(dbm, GET_TRIGGER_SQL, message, new ResultSetExtractor<Trigger>() {

			public Trigger extractData(ResultSet rs) throws SQLException {
				Trigger t=null;
				while(rs.next()){
					if(t==null){
						t=new Trigger();
					}
					String text=rs.getString("Text");
					t.appendStr(text);
				}
				return t;
			}
		}, triggerName);
		String tableName=JDBCUtils.query(dbm, GET_TRIGGER_TABLENAME_SQL, message, new ResultSetExtractor<String>() {

			public String extractData(ResultSet rs) throws SQLException {
				while(rs.next()){
					return rs.getString("name");
				}
				return null;
			}
		}, triggerName);
		trigger.setName(triggerName);
		trigger.setTableName(tableName);
		return trigger;
	}
	
	public Map<String, Trigger> getTriggers(){
		Set<String> triggerNames=getTriggerNames();
		Map<String, Trigger> triggers=new HashMap<String, Trigger>();
		for (String string : triggerNames) {
			Trigger t=getTrigger(string);
			triggers.put(string, t);
		}
		return triggers;
	}
	
	protected Map<String, Trigger> crawleTriggers(String tableName, SchemaInfo schemaInfo) {
		String message = "Get database(sql server)  "+tableName+"'s triggers information error!";
		Set<String> triggerNames=JDBCUtils.query(dbm, GET_TRIGGER_BYTABLE_SQL, message, new ResultSetExtractor<Set<String>>() {

			public Set<String> extractData(ResultSet rs) throws SQLException {
				Set<String> names=new HashSet<String>();
				while(rs.next()){
					String name=rs.getString("name");
					names.add(name);
				}
				return names;
			}
		}, tableName);
		
		Map<String, Trigger> triggers=new HashMap<String, Trigger>();
		for (String string : triggerNames) {
			Trigger t=getTrigger(string);
			t.setTableName(tableName);
			t.setName(string);
			triggers.put(string, t);
		}
		return triggers;
	}
	
	
	public Set<String> getFunctionNames(){
		String message="Get database(SQL server) current user's function names";
		Set<String> names=JDBCUtils.query(dbm, GET_FUNCTIONNAMES_SQL, message, new ResultSetExtractor<Set<String>>() {

			public Set<String> extractData(ResultSet rs) throws SQLException {
				Set<String> names=new HashSet<String>();
				while(rs.next()){
					String name=rs.getString("name");
					names.add(name);
				}
				return names;
			}
		});
		return names;
	}
	
	public Function getFunction(String name) {
		Assert.notNull(name, "function name can not be null");
		String message = "Get database(Sql server) function information error!";

		Function p = JDBCUtils.query(dbm, GET_FUNCTION_SQL, message, new ResultSetExtractor<Function>() {

			public Function extractData(ResultSet rs) throws SQLException {
				Function p = null;
				while (rs.next()) {
					String name = rs.getString("name");
					String definition = rs.getString("definition");
					if (p == null) {
						p = new Function();
						p.setName(name);
					}
					p.appendStr(definition);
				}
				return p;
			}
		}, name);

		return p;

	}
	
	public Map<String, Function> getFunctions() {
		String message = "Get database(Sql server)  function information error!";

		Map<String, Function> functions = JDBCUtils.query(dbm, GET_FUNCTIONS_SQL, message, new ResultSetExtractor<Map<String, Function>>() {

			public Map<String, Function> extractData(ResultSet rs) throws SQLException {
				Map<String, Function> functions = new HashMap<String, Function>();
				while (rs.next()) {
					Function p = new Function();
					String name = rs.getString("name");
					String definition = rs.getString("definition");
					p.setName(name);
					p.appendStr(definition);
					functions.put(name, p);
				}
				return functions;
			}
		});

		return functions;
	}
}
