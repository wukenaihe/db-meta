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

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cgs.db.exception.DatabaseMetaGetMetaException;
import com.cgs.db.exception.NonTransientDataAccessException;
import com.cgs.db.exception.SchemaInfoLevelException;
import com.cgs.db.meta.core.MetaLoader;
import com.cgs.db.meta.core.SchemaInfoLevel;
import com.cgs.db.meta.schema.Column;
import com.cgs.db.meta.schema.Table;
import com.cgs.db.meta.schema.TableType;
import com.cgs.db.util.Assert;

public abstract class AbstractSqlMetaLoader implements MetaCrawler {
	
	private Logger logger=LoggerFactory.getLogger(AbstractSqlMetaLoader.class);

	protected DatabaseMetaData dbm;

	public void setDbm(DatabaseMetaData dbm) {
		this.dbm = dbm;
	}
	
	public AbstractSqlMetaLoader(DatabaseMetaData dbm){
		this.dbm=dbm;
	}
	
	public AbstractSqlMetaLoader(){
		
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
			throw new NonTransientDataAccessException(e.getMessage(),e);
		}

		return tables;
	}
	
	protected Table crawlTableInfo(String catalog,String schema,String tableName,SchemaInfoLevel level){
		List<String> types=new ArrayList<String>();
		
		if(level.isRetrieveTable()){
			types.add("TABLE");
		}
		if(level.isRetrieveViewInformation()){
			types.add("VIEW");
		}
		if(types.size()==0){
			throw new SchemaInfoLevelException(level.getTag()+" Schema level ,can not get Table information");
		}
		String[] typeStr=new String[types.size()];
		for (int i=0;i<typeStr.length;++i) {
			typeStr[i]=types.get(i);
		}
		Table table=new Table();
		try {
			ResultSet rs=dbm.getTables(catalog, schema, tableName,typeStr);
			while(rs.next()){
				table.setName(rs.getString("TABLE_NAME"));
				table.setComment(rs.getString("REMARKS"));
				table.setTableType(TableType.fromString(rs.getString("TABLE_TYPE")));
			}
			
		} catch (SQLException e) {
			throw new DatabaseMetaGetMetaException("Table "+tableName+" information get error!", e);
		}
		
		return table;
	}
	
	public Table getTable(String tableName){
		return getTable(tableName,SchemaInfoLevel.standard());
	}
	
	public Table getTable(String tableName,SchemaInfoLevel level){
		logger.debug("crawl table information,table name is "+tableName+" ;use standard schemaInfoLevel");
		
		Assert.notNull(tableName, "tableName must not be null");
		Assert.notEmpty(tableName, "tableName must not be an empty string");
		//Get table base information
		Table table=invokeCrawlTableInfo(tableName, level);
		
		if(level.isRetrieveTableColumns()){
			Map<String, Column> columns=crawlColumnInfo(tableName);
			table.setColumns(columns);
		}
		
		return table;
	}
	
	
	/**
	 * getting the table's column
	 * 
	 * @param tableName
	 * @return
	 */
	protected Map<String, Column> crawlColumnInfo(String tableName){
		Map<String, Column> columns=new HashMap<String, Column>();
		try {
			ResultSet rs=dbm.getColumns(null, null, tableName, null);
			while(rs.next()){
				Column column=packColumn(rs);
				columns.put(column.getName(), column);
			}
		} catch (SQLException e) {
			throw new DatabaseMetaGetMetaException("Table "+tableName+" get column information error!", e);
		}
		return columns;
	}
	
	protected Column packColumn(ResultSet rs) throws SQLException{
		String name=rs.getString("COLUMN_NAME");
		Column column=new Column();
		column.setName(name);
		column.setComment(rs.getString(12));
		column.setDefaultValue(rs.getString("COLUMN_DEF"));
		column.setLength(rs.getInt("CHAR_OCTET_LENGTH"));
		column.setNullable(rs.getInt("NULLABLE")==1?true:false);
		column.setPrecision(rs.getInt("COLUMN_SIZE"));
		column.setScale(rs.getInt("DECIMAL_DIGITS"));
		column.setType(rs.getInt("DATA_TYPE"));
		column.setTypeName(rs.getString("TYPE_NAME"));
//		column.setUnique(unique);
		return column;
	}
	
	
	public abstract Table invokeCrawlTableInfo(String tableName,SchemaInfoLevel level);
	

}
