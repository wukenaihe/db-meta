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

public class OracleMetaCrawler extends AbstractMetaCrawler {
	private Logger logger = LoggerFactory.getLogger(OracleMetaCrawler.class);

	public final static String GET_CONSTRAINT_SQL = "select constraint_name name,constraint_type type,search_condition definition,deferrable from All_Constraints where owner=? "
			+ "and TABLE_NAME=? and (Constraint_Type='C' or Constraint_Type='U') ";

	public final static String GET_PROCEDURE_SQL = "select name,text from user_source where type='PROCEDURE' and name=? order by Line";

	public final static String GET_PROCEDURES_SQL = "select name,text from user_source where type='PROCEDURE' order by name,Line";
	
	public final static String GET_TRIGGERNAME_SQL="select Distinct name from user_source where type='TRIGGER'";
	
	public final static String GET_TRIGGER_SQL = "select name,text from user_source where type='TRIGGER' and name=? order by Line";
	
	public final static String GET_TRIGGERS_SQL = "select name,text from user_source where type='TRIGGER' order by name,Line";
	
	public final static String GET_TRIGGERS_BYTABLE_SQL="select trigger_name,Description,Trigger_Body,Table_Name"
			+ " from All_Triggers where Owner=? and table_name= ?";
	
	public final static String GET_FUNCTIONNAME_SQL="select Distinct name from user_source where type='FUNCTION'";
	
	public final static String GET_FUNCTION_SQL="select name,text from user_source where type='FUNCTION' and name=? order by Line";
	
	public final static String GET_FUNCTIONS_SQL = "select name,text from user_source where type='FUNCTION' order by name,Line";

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
		Assert.notNull(procedureName, "procedure name can not be null");
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
	
	public Set<String> getTriggerNames(){
		String message="Get database(Oracle) current user's trigger names";
		Set<String> names=JDBCUtils.query(dbm, GET_TRIGGERNAME_SQL, message, new ResultSetExtractor<Set<String>>() {

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
	
	public Trigger getTrigger(String triggerName) {
		Assert.notNull(triggerName, "triggerName can not be null");
		String message = "Get database(Oracle) " + triggerName + "'s definition information error!";
		return JDBCUtils.query(dbm, GET_TRIGGER_SQL, message, new ResultSetExtractor<Trigger>() {

			public Trigger extractData(ResultSet rs) throws SQLException {
				Trigger p = null;
				while (rs.next()) {
					if (p == null) {
						p = new Trigger();
						p.setName(rs.getString("name"));
					}
					p.appendStr(rs.getString("text"));
				}
				return p;
			}

		}, triggerName);
	}
	
	public Map<String, Trigger> getTriggers() {
		String message = "Get database(Oracle)  definition information error!";
		return JDBCUtils.query(dbm, GET_TRIGGERS_SQL, message, new ResultSetExtractor<Map<String, Trigger>>() {

			public Map<String, Trigger> extractData(ResultSet rs) throws SQLException {
				Map<String, Trigger> triggers = new HashMap<String, Trigger>();
				Trigger p;
				while (rs.next()) {
					String name = rs.getString("name");
					p = triggers.get(name);
					if (p == null) {
						p = new Trigger();
						p.setName(rs.getString("name"));
						triggers.put(name, p);
					}
					p.appendStr(rs.getString("text"));
				}
				return triggers;
			}
		});
	}
	
	public Set<String> getFunctionNames(){
		String message="Get database(My sql) current user's function names";
		Set<String> names=JDBCUtils.query(dbm, GET_FUNCTIONNAME_SQL, message, new ResultSetExtractor<Set<String>>() {

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
	
	public Function getFunction(String name){
		Assert.notNull(name, "procedure name can not be null");
		String message = "Get database(Oracle) " + name + "'s definition information error!";
		return JDBCUtils.query(dbm, GET_FUNCTION_SQL, message, new ResultSetExtractor<Function>() {

			public Function extractData(ResultSet rs) throws SQLException {
				Function p = null;
				while (rs.next()) {
					if (p == null) {
						p = new Function();
						p.setName(rs.getString("name"));
					}
					p.appendStr(rs.getString("text"));
				}
				return p;
			}

		}, name);
	}
	
	public Map<String, Function> getFunctions(){
		String message = "Get database(Oracle)  definition information error!";
		return JDBCUtils.query(dbm, GET_FUNCTIONS_SQL, message, new ResultSetExtractor<Map<String, Function>>() {

			public Map<String, Function> extractData(ResultSet rs) throws SQLException {
				Map<String, Function> functions = new HashMap<String, Function>();
				Function f;
				while (rs.next()) {
					String name = rs.getString("name");
					f = functions.get(name);
					if (f == null) {
						f = new Function();
						f.setName(rs.getString("name"));
						functions.put(name, f);
					}
					f.appendStr(rs.getString("text"));
				}
				return functions;
			}
		});
	}
	

	protected Map<String, Trigger> crawleTriggers(String tableName, SchemaInfo schemaInfo) {
		String message = "Get database(My sql)  "+tableName+"'s triggers information error!";
		String schema;
		if(schemaInfo==null||schemaInfo.getSchemaName()==null){
			schema=getSchemaName();
		}else{
			schema=schemaInfo.getSchemaName();
		}
		Map<String, Trigger> triggers=JDBCUtils.query(dbm, GET_TRIGGERS_BYTABLE_SQL, message, new ResultSetExtractor<Map<String, Trigger>>() {

			public Map<String, Trigger> extractData(ResultSet rs) throws SQLException {
				Map<String, Trigger> triggers = new HashMap<String, Trigger>();
				while(rs.next()){
					String trigger_name=rs.getString("trigger_name");
					String description=rs.getString("Description");
					String trigger_Body=rs.getString("Trigger_Body");
					String table_name=rs.getString("Table_Name");
					Trigger trigger=new Trigger();
					trigger.appendStr("create or replace Trigger \n");
					trigger.appendStr(description);
					trigger.appendStr(trigger_Body);
					trigger.setTableName(table_name);
					
					triggers.put(trigger_name, trigger);
				}
				return triggers;
			}
		}, schema,tableName);
		return triggers;
	}
	
	
	private String getSchemaName(){
		try {
			String schema=dbm.getUserName();
			return schema;
		} catch (SQLException e) {
			throw new DatabaseMetaGetMetaException("Get database(Oracle) cataglog name error!", e);
		}
	}

}
