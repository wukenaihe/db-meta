package com.cgs.db.meta.retriever;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cgs.db.exception.DataAccessException;
import com.cgs.db.exception.DatabaseMetaGetMetaException;
import com.cgs.db.exception.NonTransientDataAccessException;
import com.cgs.db.exception.SchemaInfoLevelException;
import com.cgs.db.meta.core.SchemaInfoLevel;
import com.cgs.db.meta.schema.Column;
import com.cgs.db.meta.schema.Constraint;
import com.cgs.db.meta.schema.Database;
import com.cgs.db.meta.schema.DatabaseInfo;
import com.cgs.db.meta.schema.ForeignKey;
import com.cgs.db.meta.schema.ForeignKeyColumnReference;
import com.cgs.db.meta.schema.ForeignKeyDeferrability;
import com.cgs.db.meta.schema.ForeignKeyUpdateRule;
import com.cgs.db.meta.schema.Function;
import com.cgs.db.meta.schema.Index;
import com.cgs.db.meta.schema.IndexType;
import com.cgs.db.meta.schema.PrimaryKey;
import com.cgs.db.meta.schema.Privilege;
import com.cgs.db.meta.schema.Schema;
import com.cgs.db.meta.schema.SchemaInfo;
import com.cgs.db.meta.schema.Table;
import com.cgs.db.meta.schema.TableType;
import com.cgs.db.meta.schema.Trigger;
import com.cgs.db.util.Assert;
import com.cgs.db.util.JDBCUtils;

public abstract class AbstractMetaCrawler implements MetaCrawler {

	private Logger logger = LoggerFactory.getLogger(AbstractMetaCrawler.class);

	protected DatabaseMetaData dbm;

	public void setDbm(DatabaseMetaData dbm) {
		this.dbm = dbm;
	}

	public AbstractMetaCrawler(DatabaseMetaData dbm) {
		this.dbm = dbm;
	}

	public AbstractMetaCrawler() {

	}

	public Set<String> getTableNames() {
		Set<String> tables = new HashSet<String>();
		ResultSet rs;
		try {
			rs = dbm.getTables(null, null, null, new String[] { "TABLE" });

			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				tables.add(tableName);
			}
		} catch (SQLException e) {
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
				tables.add(tableName);
			}
		} catch (SQLException e) {
			throw new NonTransientDataAccessException(e.getMessage(), e);
		}

		return tables;
	}

	protected Table crawlTableInfo(String catalog, String schema, String tableName, SchemaInfoLevel level) {
		List<String> types = new ArrayList<String>();

		if (level.isRetrieveTable()) {
			types.add("TABLE");
		}
		if (level.isRetrieveViewInformation()) {
			types.add("VIEW");
		}
		if (types.size() == 0) {
			throw new SchemaInfoLevelException(level.getTag() + " Schema level ,can not get Table information");
		}
		String[] typeStr = new String[types.size()];
		for (int i = 0; i < typeStr.length; ++i) {
			typeStr[i] = types.get(i);
		}
		Table table = new Table();
		try {
			ResultSet rs = dbm.getTables(catalog, schema, tableName, typeStr);
			while (rs.next()) {
				String tableN=rs.getString("TABLE_NAME");
				if(!tableN.equals(tableName)) continue;
				table.setName(tableN);
				table.setComment(rs.getString("REMARKS"));
				table.setTableType(TableType.fromString(rs.getString("TABLE_TYPE")));
			}

		} catch (SQLException e) {
			throw new DatabaseMetaGetMetaException("Table " + tableName + " information get error!", e);
		}

		return table;
	}

	public Table getTable(String tableName) {
		return getTable(tableName, SchemaInfoLevel.standard());
	}

	public Table getTable(String tableName, SchemaInfoLevel level) {
		return getTable(tableName, level, null);
	}

	public Table getTable(String tableName, SchemaInfoLevel level, SchemaInfo schemaInfo) {
		logger.debug("crawl table information,table name is " + tableName + " ;use standard schemaInfoLevel");

		Assert.notNull(tableName, "tableName must not be null");
		Assert.notEmpty(tableName, "tableName must not be an empty string");
		// Get table base information

		Table table;

		if (schemaInfo == null) {
			table = invokeCrawlTableInfo(tableName, level);
		} else {
			table = crawlTableInfo(schemaInfo.getCatalogName(), schemaInfo.getSchemaName(), tableName, level);
		}

		// crawl column information
		if (level.isRetrieveTableColumns()) {
			Map<String, Column> columns = crawlColumnInfo(tableName, schemaInfo);
			table.setColumns(columns);
		}

		// crawl primary key
		if (level.isRetrievePrimaryKey()) {
			PrimaryKey pk = crawlPrimaryKey(tableName, schemaInfo);
			table.setPrimaryKey(pk);
		}

		// crawl foreign
		if (level.isRetrieveForeignKeys()) {
			Map<String, ForeignKey> foreignKeys = crawlForeignKey(tableName, schemaInfo);
			table.setForeignkeys(foreignKeys);
		}

		// crawl privilege
		if (level.isRetrieveTablePrivileges()) {
			Privilege p = crawlPrivilge(tableName, schemaInfo);
			table.setPrivilege(p);
		}
		
		//crawl index
		if(level.isRetrieveIndexInformation()){
			Map<String, Index> indexs=crawlIndex(tableName, schemaInfo);
			table.setIndexs(indexs);
		}
		
		//crawl constraint
		if(level.isRetrieveTableConstraintInformation()){
			Map<String, Constraint> constraints=crawlConstraint(tableName, schemaInfo);
			table.setConstraints(constraints);
		}
		
		//craw trigger
		if(level.isRetrieveTriggerInformation()){
			Map<String, Trigger> triggers=crawleTriggers(tableName, schemaInfo);
			table.setTriggers(triggers);
		}

		return table;
	}

	/**
	 * getting the table's column
	 * 
	 * @param tableName
	 * @return
	 */
	protected Map<String, Column> crawlColumnInfo(String tableName, SchemaInfo schemaInfo) {
		Map<String, Column> columns = new HashMap<String, Column>();
		ResultSet rs = null;
		try {
			if (schemaInfo == null) {
				rs = dbm.getColumns(null, null, tableName, null);
			} else {
				rs = dbm.getColumns(schemaInfo.getCatalogName(), schemaInfo.getSchemaName(), tableName, null);
			}
			while (rs.next()) {
				if(!rs.getString("TABLE_NAME").equals(tableName)) continue;
				Column column = packColumn(rs);
				columns.put(column.getName(), column);
			}
		} catch (SQLException e) {
			throw new DatabaseMetaGetMetaException("Table " + tableName + " get column information error!", e);
		} finally {
			JDBCUtils.closeResultSet(rs);
		}
		return columns;
	}

	protected Map<String, Column> crawlColumnInfo(String tableName) {
		return crawlColumnInfo(tableName, null);
	}

	protected Column packColumn(ResultSet rs) throws SQLException {
		String name = rs.getString("COLUMN_NAME");
		Column column = new Column();
		column.setName(name);
		column.setComment(rs.getString(12));// Oracle and Mssql can not get
											// comment information
		column.setDefaultValue(rs.getString("COLUMN_DEF"));
		column.setLength(rs.getInt("CHAR_OCTET_LENGTH"));
		column.setNullable(rs.getInt("NULLABLE") == 1 ? true : false);
		column.setPrecision(rs.getInt("COLUMN_SIZE"));
		column.setScale(rs.getInt("DECIMAL_DIGITS"));
		column.setType(rs.getInt("DATA_TYPE"));
		column.setTypeName(rs.getString("TYPE_NAME"));
		// column.setUnique(unique);
		return column;
	}

	protected abstract Table invokeCrawlTableInfo(String tableName, SchemaInfoLevel level);

	protected PrimaryKey crawlPrimaryKey(String tableName, SchemaInfo schemaInfo) {
		List<String> columns = new ArrayList<String>();
		TreeMap<Integer, String> columnMaps = new TreeMap<Integer, String>();
		PrimaryKey pk = new PrimaryKey();
		ResultSet rs = null;
		try {
			if (schemaInfo == null) {
				rs = dbm.getPrimaryKeys(null, null, tableName);
			} else {
				rs = dbm.getPrimaryKeys(schemaInfo.getCatalogName(), schemaInfo.getSchemaName(), tableName);
			}
			while (rs.next()) {
				String pkName = rs.getString("PK_NAME");
				pk.setName(pkName);
				int seq = rs.getInt("KEY_SEQ");
				String columnName = rs.getString("COLUMN_NAME");
				columnMaps.put(seq, columnName);
			}

			Set<Integer> keys = columnMaps.keySet();
			for (Integer integer : keys) {
				String columnName = columnMaps.get(integer);
				columns.add(columnName);
			}
			pk.setColumns(columns);
		} catch (SQLException e) {
			throw new DatabaseMetaGetMetaException("Table " + tableName + " get primary key information error!", e);
		} finally {
			JDBCUtils.closeResultSet(rs);
		}
		return pk;
	}

	protected PrimaryKey crawlPrimaryKey(String tableName) {
		return crawlPrimaryKey(tableName, null);
	}

	protected Map<String, ForeignKey> crawlForeignKey(String tableName) {
		return crawlForeignKey(tableName, null);
	}

	protected Map<String, ForeignKey> crawlForeignKey(String tableName, SchemaInfo schemaInfo) {
		Map<String, ForeignKey> foreignKeys = new HashMap<String, ForeignKey>();
		ResultSet rs = null;
		try {
			if (schemaInfo == null) {
				rs = dbm.getImportedKeys(null, null, tableName);
			} else {
				rs = dbm.getImportedKeys(schemaInfo.getCatalogName(), schemaInfo.getSchemaName(), tableName);
			}
			while (rs.next()) {
				String fk_name = rs.getString("FK_NAME");
				ForeignKey key;
				if (!foreignKeys.containsKey(fk_name)) {
					key = new ForeignKey();
					key.setName(rs.getString("FK_NAME"));
					key.setUpdateRule(ForeignKeyUpdateRule.valueOf(rs.getInt("UPDATE_RULE")));
					key.setDeleteRule(ForeignKeyUpdateRule.valueOf(rs.getInt("DELETE_RULE")));
					key.setDeferrability(ForeignKeyDeferrability.valueOf(rs.getInt("DEFERRABILITY")));
					foreignKeys.put(fk_name, key);
				} else {
					key = foreignKeys.get(fk_name);
				}
				ForeignKeyColumnReference fcr = packForeignKeyColumnReference(rs);
				key.getColumnReferences().add(fcr);
			}
		} catch (SQLException e) {
			throw new DatabaseMetaGetMetaException("Table " + tableName + " get foreign key information error!", e);
		} finally {
			JDBCUtils.closeResultSet(rs);
		}

		return foreignKeys;
	}

	protected Privilege crawlPrivilge(String tableName, SchemaInfo schemaInfo) {
		ResultSet rs = null;
		Privilege p = null;
		try {
			if (schemaInfo == null) {
				rs = dbm.getTablePrivileges(null, null, tableName);
			} else {
				rs = dbm.getTablePrivileges(schemaInfo.getCatalogName(), schemaInfo.getSchemaName(), tableName);
			}

			while (rs.next()) {
				String grantor = rs.getString("GRANTOR");
				String grantee = rs.getString("GRANTEE");
				String privilege = rs.getString("PRIVILEGE");
				String is_Grantable = rs.getString("IS_GRANTABLE");
				boolean isGrantable;
				if (is_Grantable == null || is_Grantable.equals("NO")) {
					isGrantable = false;
				} else {
					isGrantable = true;
				}
				p = new Privilege(grantor, grantee, privilege, isGrantable);
			}
		} catch (SQLException e) {
			throw new DatabaseMetaGetMetaException("Table " + tableName + " get Privilege information error!", e);
		} finally {
			JDBCUtils.closeResultSet(rs);
		}
		return p;

	}

	protected Map<String,Index> crawlIndex(String tableName, SchemaInfo schemaInfo) {
		ResultSet rs = null;
		Map<String, Index> indexs=new HashMap<String, Index>();
		try {
			if (schemaInfo == null) {
				rs = dbm.getIndexInfo(null, null, tableName, false, false);
			}else{
				rs=dbm.getIndexInfo(schemaInfo.getCatalogName(), schemaInfo.getSchemaName(), tableName, false, false);
			}
			while(rs.next()){
				String name=rs.getString("INDEX_NAME");
				if(name==null){
					continue;//oracle will have a null name index
				}
				Index i=indexs.get(name);
				if(i==null){
					i=new Index();
					boolean isUnique=rs.getBoolean("NON_UNIQUE");
					int type=rs.getInt("TYPE");
					IndexType indexType=IndexType.valueOf(type);
					int page=rs.getInt("PAGES");
					i.setUnique(isUnique);
					i.setIndexType(indexType);
					i.setPages(page);
					i.setColumnNames(new ArrayList<String>());
					indexs.put(name, i);
					
				}
				String columnName=rs.getString("COLUMN_NAME");
				i.getColumnNames().add(columnName);
			}

		} catch (SQLException e) {
			throw new DatabaseMetaGetMetaException("Table " + tableName + " get index information error!", e);
		}finally {
			JDBCUtils.closeResultSet(rs);
		}
		return indexs;

	}
	
	protected abstract Map<String, Constraint> crawlConstraint(String tableName, SchemaInfo schemaInfo);
	
	

	protected ForeignKeyColumnReference packForeignKeyColumnReference(ResultSet rs) throws SQLException {
		String pk_cat = rs.getString("PKTABLE_CAT");
		String pk_schema = rs.getString("PKTABLE_SCHEM");
		String pk_table = rs.getString("PKTABLE_NAME");
		String pk_column = rs.getString("PKCOLUMN_NAME");

		String fk_cat = rs.getString("FKTABLE_CAT");
		String fk_schema = rs.getString("FKTABLE_SCHEM");
		String fk_table = rs.getString("FKTABLE_NAME");
		String fk_column = rs.getString("PKCOLUMN_NAME");

		int keySeq = rs.getInt("KEY_SEQ");

		ForeignKeyColumnReference foreignKeyColumnReference = new ForeignKeyColumnReference();
		foreignKeyColumnReference.setKeySequence(keySeq);
		foreignKeyColumnReference.setForeignColumn(new ForeignKeyColumnReference.ColumnReference(fk_cat, fk_schema, fk_table, fk_column));
		foreignKeyColumnReference.setPrimaryColumn(new ForeignKeyColumnReference.ColumnReference(pk_cat, pk_schema, pk_table, pk_column));

		return foreignKeyColumnReference;
	}
	
	protected abstract Map<String, Trigger> crawleTriggers(String tableName,SchemaInfo schemaInfo);
	
	protected Map<String, Trigger> crawlerTriggers(String tableName){
		return crawleTriggers(tableName,null);
	}

	public Schema getSchema(SchemaInfoLevel level) {
		ResultSet rs;
		SchemaInfo schemaInfo = null;
		Schema schema = new Schema();
		Map<String, Table> tables = new HashMap<String, Table>();
		// TODO use other method to information
		try {
			schemaInfo = getSchemaInfo();
			schema.setSchemaInfo(schemaInfo);
			Set<String> tableNames = getTableNames();
			for (String string : tableNames) {
				Table table = getTable(string, level);
				tables.put(string, table);
			}
			schema.setTables(tables);
		} catch (DataAccessException e) {
			throw new DatabaseMetaGetMetaException("get schema information error!", e);
		}

		return schema;
	}

	public Schema getSchema(SchemaInfo schemaInfo, SchemaInfoLevel level) {
		ResultSet rs = null;
		Schema schema = new Schema();
		Map<String, Table> tables = new HashMap<String, Table>();
		// TODO use other method to information
		try {
			Set<String> tableNames = getTableNames(schemaInfo);
			for (String string : tableNames) {
				Table table = getTable(string, level, schemaInfo);
				tables.put(string, table);
			}
			schema.setTables(tables);
		} catch (DataAccessException e) {
			throw new DatabaseMetaGetMetaException("get schema information error!", e);
		} finally {
			JDBCUtils.closeResultSet(rs);
		}

		return schema;
	}

	protected SchemaInfo getSchemaInfo() {
		ResultSet rs = null;
		SchemaInfo schemaInfo = null;
		try {
			rs = dbm.getTables(null, null, null, new String[] { "TABLE" });

			while (rs.next()) {
				String catalog = rs.getString("TABLE_CAT");
				String schema = rs.getString("TABLE_SCHEM");
				schemaInfo = new SchemaInfo(catalog, schema);
				break;
			}
		} catch (SQLException e) {
			throw new NonTransientDataAccessException(e.getMessage(), e);
		} finally {
			JDBCUtils.closeResultSet(rs);
		}
		return schemaInfo;
	}

	public DatabaseInfo getDatabaseInfo() {
		try {
			String productName = dbm.getDatabaseProductName();
			String productVersion = dbm.getDatabaseProductVersion();
			String userName = dbm.getUserName();
			DatabaseInfo databaseInfo = new DatabaseInfo();
			databaseInfo.setProductName(productName);
			databaseInfo.setProductVersion(productVersion);
			databaseInfo.setUserName(userName);
			return databaseInfo;
		} catch (SQLException e) {
			throw new NonTransientDataAccessException(e.getMessage(), e);
		}
	}

	public Database getDatabase(SchemaInfoLevel level) {
		Database database = new Database();
		DatabaseInfo databaseInfo = getDatabaseInfo();
		database.setDatabaseInfo(databaseInfo);

		Set<Schema> schemaSet = new HashSet<Schema>();
		Set<SchemaInfo> schemas = getSchemaInfos();
		for (SchemaInfo schemaInfo : schemas) {
			Schema schema = getSchema(schemaInfo, level);
			schemaSet.add(schema);
		}
		database.setSchemas(schemaSet);
		return database;
	}

	public Set<String> getProcedureNames(SchemaInfo schemaInfo){
		Set<String> procedures = new HashSet<String>();
		ResultSet rs;
		try {
			if(schemaInfo==null){
				rs = dbm.getProcedures(null, null, null);
			}else{
				rs=dbm.getProcedures(schemaInfo.getCatalogName(), schemaInfo.getSchemaName(), null);
			}
			while (rs.next()) {
				String tableName = rs.getString("PROCEDURE_NAME");
				procedures.add(tableName);
			}
		} catch (SQLException e) {
			throw new NonTransientDataAccessException(e.getMessage(), e);
		}

		return procedures;
	}
}
