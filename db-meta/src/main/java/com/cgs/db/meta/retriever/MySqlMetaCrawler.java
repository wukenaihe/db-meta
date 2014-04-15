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
import com.cgs.db.meta.schema.Function;
import com.cgs.db.meta.schema.Procedure;
import com.cgs.db.meta.schema.SchemaInfo;
import com.cgs.db.meta.schema.Table;
import com.cgs.db.meta.schema.TableConstraintType;
import com.cgs.db.meta.schema.Trigger;
import com.cgs.db.util.Assert;
import com.cgs.db.util.JDBCUtils;
import com.cgs.db.util.ResultSetExtractor;
import com.cgs.db.util.Utility;

public class MySqlMetaCrawler extends AbstractMetaCrawler {

	private static Logger logger = LoggerFactory.getLogger(MySqlMetaCrawler.class);
	public static final String GET_CONSTRAINT_SQL = "select a.CONSTRAINT_NAME name,b.column_name columnName from information_schema.TABLE_CONSTRAINTS a left join information_schema.KEY_COLUMN_USAGE b "
			+ "on a.CONSTRAINT_NAME = b.CONSTRAINT_NAME where a.CONSTRAINT_SCHEMA = ? and a.TABLE_NAME = ? and a.CONSTRAINT_TYPE= 'UNIQUE'";

	public static final String GET_PROCEDURE_SQL = "select routine_name name,routine_definition definition from information_schema.ROUTINES "
			+ "where ROUTINE_TYPE='PROCEDURE' and routine_name =? and ROUTINE_SCHEMA=?";

	public static final String GET_PROCEDURES_SQL = "select routine_name name,routine_definition definition from information_schema.ROUTINES "
			+ "where ROUTINE_TYPE='PROCEDURE'";
	
	public static final String GET_TRIGGERNAMES_SQL="select trigger_name name from information_schema.TRIGGERS "
			+ "where trigger_schema=?";
	
	public static final String GET_TRIGGER_SQL="select definer,trigger_schema,trigger_name,action_timing,"
			+ "event_manipulation,event_object_table,action_orientation,action_statement from information_schema.TRIGGERS where trigger_schema =? and TRIGGER_NAME=?";
	
	public static final String GET_TRIGGERS_SQL="select definer,trigger_schema,trigger_name,action_timing,"
			+ "event_manipulation,event_object_table,action_orientation,action_statement"
			+ " from information_schema.TRIGGERS where trigger_schema =?";
	
	
	public static final String GET_TRIGGERS_BYTABLE_SQL="select definer,trigger_schema,trigger_name,action_timing,event_manipulation,event_object_table,action_orientation,action_statement"
			+ " from information_schema.TRIGGERS where trigger_schema =? and event_object_table=?";
	
	
	public static final String GET_FUNCTIONNAME_SQL="select routine_name name from information_schema.ROUTINES "
			+ "where ROUTINE_TYPE='FUNCTION' and ROUTINE_SCHEMA=?";
	
	public static final String GET_FUNCTION_SQL="select routine_name name,routine_definition definition from information_schema.ROUTINES "
			+ "where ROUTINE_TYPE='FUNCTION' and routine_name =? and ROUTINE_SCHEMA=?";
	
	
	public static final String GET_FUNCTIONS_SQL="select routine_name name,routine_definition definition "
			+ "from information_schema.ROUTINES where ROUTINE_TYPE='FUNCTION' and ROUTINE_SCHEMA =?";

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
		Assert.notNull(procedureName, "procedure name can not be null");
		String message = "Get database(MySql)  procedure information error!";
		String schema = getSchemaName();

		Procedure p = JDBCUtils.query(dbm, GET_PROCEDURE_SQL, message, new ResultSetExtractor<Procedure>() {

			public Procedure extractData(ResultSet rs) throws SQLException {
				Procedure p = null;
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
		}, procedureName, schema);

		return p;

	}

	private String getSchemaName() {
		String schema;
		try {
			Connection con = dbm.getConnection();
			schema = con.getCatalog();
		} catch (SQLException e) {
			throw new DatabaseMetaGetMetaException("Get database(mysql) cataglog name error!", e);
		}
		return schema;
	}

	public Map<String, Procedure> getProcedures() {
		String message = "Get database(mysql)  definition information error!";

		Map<String, Procedure> procedures = JDBCUtils.query(dbm, GET_PROCEDURES_SQL, message, new ResultSetExtractor<Map<String, Procedure>>() {

			public Map<String, Procedure> extractData(ResultSet rs) throws SQLException {
				Map<String, Procedure> procedures = new HashMap<String, Procedure>();
				while (rs.next()) {
					Procedure p = new Procedure();
					String name = rs.getString("name");
					String definition = rs.getString("definition");
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
		String message="Get database(My sql) current user's trigger names";
		Set<String> names=JDBCUtils.query(dbm, GET_TRIGGERNAMES_SQL, message, new ResultSetExtractor<Set<String>>() {

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
	
	public Trigger getTrigger(String triggerName){
		Assert.notNull(triggerName, "triggerName can not be null");
		String message = "Get database(Oracle) " + triggerName + "'s definition information error!";
		String schema=getSchemaName();
		Trigger trigger=JDBCUtils.query(dbm, GET_TRIGGER_SQL, message, new ResultSetExtractor<Trigger>() {

			public Trigger extractData(ResultSet rs) throws SQLException {
				Trigger trigger = null;
				while(rs.next()){
					String definer=rs.getString("definer");
					String trigger_schema=rs.getString("trigger_schema");
					String trigger_name=rs.getString("trigger_name");
					String action_timing=rs.getString("action_timing");
					String table=rs.getString("event_object_table");
					String action_orientation=rs.getString("action_orientation");
					String action_statement=rs.getString("action_statement");
					trigger=new Trigger();
					trigger.appendStr("CREATE DEFINER = "+definer+"\n");
					trigger.appendStr("\tTrigger "+Utility.quote(trigger_schema)+"."+Utility.quote(trigger_name)+"\n");
					trigger.appendStr("\t"+action_timing+" on "+Utility.quote(table)+"\n");
					trigger.appendStr("\tFor each "+action_orientation+"\n");
					trigger.appendStr(action_statement);
					trigger.setName(trigger_name);
					trigger.setTableName(table);
				}
				return trigger;
			}
		}, schema,triggerName);
		return trigger;
	}
	
	public Map<String, Trigger> getTriggers(){
		String message = "Get database(My sql)  trigger information error!";
		String schema=getSchemaName();
		Map<String, Trigger> triggers=JDBCUtils.query(dbm, GET_TRIGGERS_SQL, message, new ResultSetExtractor<Map<String, Trigger>>() {

			public Map<String, Trigger> extractData(ResultSet rs) throws SQLException {
				Map<String, Trigger> triggers = new HashMap<String, Trigger>();
				while(rs.next()){
					String definer=rs.getString("definer");
					String trigger_schema=rs.getString("trigger_schema");
					String trigger_name=rs.getString("trigger_name");
					String action_timing=rs.getString("action_timing");
					String table=rs.getString("event_object_table");
					String action_orientation=rs.getString("action_orientation");
					String action_statement=rs.getString("action_statement");
					Trigger trigger=new Trigger();
					trigger.appendStr("CREATE DEFINER = "+definer+"\n");
					trigger.appendStr("\tTrigger "+Utility.quote(trigger_schema)+"."+Utility.quote(trigger_name)+"\n");
					trigger.appendStr("\t"+action_timing+" on "+Utility.quote(table)+"\n");
					trigger.appendStr("\tFor each "+action_orientation+"\n");
					trigger.appendStr(action_statement);
					trigger.setName(trigger_name);
					trigger.setTableName(table);
					
					triggers.put(trigger_name, trigger);
				}
				return triggers;
			}
		}, schema);
		return triggers;
	}

	
	protected Map<String, Trigger> crawleTriggers(String tableName, SchemaInfo schemaInfo) {
		String message = "Get database(My sql)  "+tableName+"'s triggers information error!";
		String schema;
		if(schemaInfo==null||schemaInfo.getCatalogName()==null){
			schema=getSchemaName();
		}else{
			schema=schemaInfo.getCatalogName();
		}
		Map<String, Trigger> triggers=JDBCUtils.query(dbm, GET_TRIGGERS_BYTABLE_SQL, message, new ResultSetExtractor<Map<String, Trigger>>() {

			public Map<String, Trigger> extractData(ResultSet rs) throws SQLException {
				Map<String, Trigger> triggers = new HashMap<String, Trigger>();
				while(rs.next()){
					String definer=rs.getString("definer");
					String trigger_schema=rs.getString("trigger_schema");
					String trigger_name=rs.getString("trigger_name");
					String action_timing=rs.getString("action_timing");
					String table=rs.getString("event_object_table");
					String action_orientation=rs.getString("action_orientation");
					String action_statement=rs.getString("action_statement");
					Trigger trigger=new Trigger();
					trigger.appendStr("CREATE DEFINER = "+definer+"\n");
					trigger.appendStr("\tTrigger "+Utility.quote(trigger_schema)+"."+Utility.quote(trigger_name)+"\n");
					trigger.appendStr("\t"+action_timing+" on "+Utility.quote(table)+"\n");
					trigger.appendStr("\tFor each "+action_orientation+"\n");
					trigger.appendStr(action_statement);
					trigger.setName(trigger_name);
					trigger.setTableName(table);
					
					triggers.put(trigger_name, trigger);
				}
				return triggers;
			}
		}, schema,tableName);
		return triggers;
	}
	
	public Set<String> getFunctionNames(){
		String message="Get database(My sql) current user's function names";
		String schema=getSchemaName();
		Set<String> names=JDBCUtils.query(dbm, GET_FUNCTIONNAME_SQL, message, new ResultSetExtractor<Set<String>>() {

			public Set<String> extractData(ResultSet rs) throws SQLException {
				Set<String> names=new HashSet<String>();
				while(rs.next()){
					String name=rs.getString("name");
					names.add(name);
				}
				return names;
			}
		},schema);
		return names;
	}
	
	public Function getFunction(String name) {
		Assert.notNull(name, "function name can not be null");
		String message = "Get database(MySql) function information error!";
		String schema = getSchemaName();

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
		}, name, schema);

		return p;

	}
	
	public Map<String, Function> getFunctions() {
		String message = "Get database(mysql)  function information error!";
		String schema=getSchemaName();

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
		},schema);

		return functions;
	}
}
