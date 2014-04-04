package com.cgs.db.meta.core;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cgs.db.meta.schema.Database;
import com.cgs.db.meta.schema.Schema;
import com.cgs.db.meta.schema.SchemaInfo;
import com.cgs.db.meta.schema.Table;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:config.xml")
public class MetaLoaderTest {
	
	@Autowired
	public MetaLoader metaLoader;
	private long currentTime;
	
	@Before
	public void getCurrentTime(){
		currentTime=System.currentTimeMillis();
	}
	
	@After
	public void printTime(){
		System.out.println(System.currentTimeMillis()-currentTime+" millSecond");
	}
	
	@Test
	@Ignore
	public void getTableNames(){
		Set<String> tableNames=metaLoader.getTableNames();
		
		System.out.println(tableNames);
	}
	
	@Test
	@Ignore
	public void getTable(){
		long startTime=System.currentTimeMillis();
		String tableName="des_table";//Oracle:"PUMP",mySql:"person_info":sql server:"Dataset"
		Table table=metaLoader.getTable(tableName);
		System.out.println(table);
		
		long endTime=System.currentTimeMillis();
		System.out.println("耗时："+(endTime-startTime));
	}
	
	@Test
	@Ignore
	public void getSchemaInfos(){
		long startTime=System.currentTimeMillis();
		
		Set<SchemaInfo> schemaInfos=metaLoader.getSchemaInfos();
		System.out.println(schemaInfos);
		long endTime=System.currentTimeMillis();
		System.out.println("耗时："+(endTime-startTime));
	}
	
	@Test
	@Ignore
	public void getSchema(){
		Schema schema=metaLoader.getSchema();
		System.out.println(schema);
	}
	
	@Test
	public void getDatabase(){
		Database database=metaLoader.getDatabase();
		System.out.println(database);
	}
}
