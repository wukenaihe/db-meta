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
import com.cgs.db.meta.schema.SchemaInfo;
import com.cgs.db.meta.schema.Table;
import com.cgs.db.meta.schema.TableConstraintType;
import com.cgs.db.util.JDBCUtils;

public class MySqlMetaCrawler extends AbstractMetaCrawler{
	
	private static Logger logger=LoggerFactory.getLogger(MySqlMetaCrawler.class);
	public static final String GET_CONSTRAINT_SQL="select a.CONSTRAINT_NAME name,b.column_name columnName from information_schema.TABLE_CONSTRAINTS a left join information_schema.KEY_COLUMN_USAGE b "
			+ "on a.CONSTRAINT_NAME = b.CONSTRAINT_NAME where a.CONSTRAINT_SCHEMA = ? and a.TABLE_NAME = ? and a.CONSTRAINT_TYPE= 'UNIQUE'";
	
	public MySqlMetaCrawler(){
		
	}
	
	public MySqlMetaCrawler(DatabaseMetaData databaseMetaData){
		super(databaseMetaData);
	}

	
	public Table invokeCrawlTableInfo(String tableName,SchemaInfoLevel level) {
		return crawlTableInfo(null, null, tableName, level);
	}

	
	public Set<SchemaInfo> getSchemaInfos() {
		Set<SchemaInfo> schemaInfos=new HashSet<SchemaInfo>();
		try {
			ResultSet rs=dbm.getCatalogs();
			while(rs.next()){
				String catalogName=rs.getString("TABLE_CAT");
				SchemaInfo schemaInfo=new SchemaInfo(catalogName,null);
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
	 * @see com.cgs.db.meta.retriever.AbstractMetaCrawler#crawlConstraint(java.lang.String, com.cgs.db.meta.schema.SchemaInfo)
	 */
	protected Map<String, Constraint> crawlConstraint(String tableName, SchemaInfo schemaInfo) {
		ResultSet rs=null;
		PreparedStatement st = null;
		Map<String, Constraint> constraints = new HashMap<String, Constraint>();
		
		try {
			Connection con = dbm.getConnection();
			st = con.prepareStatement(GET_CONSTRAINT_SQL);
			String schema;
			if(schemaInfo==null){
				schema=con.getCatalog();
			}else{
				schema=schemaInfo.getCatalogName();
			}
			st.setString(1, schema);
			st.setString(2, tableName);
			rs = st.executeQuery();
			while(rs.next()){
				String name=rs.getString("name");
				String columnName=rs.getString("columnName");
				Constraint c=new Constraint();
				c.setName(name);
				c.setDefinition(columnName+" IS UNIQUE");
				c.setTableConstraintType(TableConstraintType.unique);
				constraints.put(name, c);
			}
		} catch (SQLException e) {
			throw new DatabaseMetaGetMetaException("Get database(MySql) "+tableName+"'s constraint information error!", e);
		}finally{
			JDBCUtils.closePreparedStatement(st);
			JDBCUtils.closeResultSet(rs);
		}
		return constraints;
	}
}
