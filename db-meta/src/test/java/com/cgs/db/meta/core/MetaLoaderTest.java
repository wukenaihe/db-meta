package com.cgs.db.meta.core;

import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cgs.db.exception.DataAccessException;
import com.cgs.db.meta.schema.Database;
import com.cgs.db.meta.schema.Function;
import com.cgs.db.meta.schema.Procedure;
import com.cgs.db.meta.schema.Schema;
import com.cgs.db.meta.schema.SchemaInfo;
import com.cgs.db.meta.schema.Table;
import com.cgs.db.meta.schema.Trigger;
import com.cgs.db.util.PrintUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:config.xml")
public class MetaLoaderTest {

	@Autowired
	public MetaLoader metaLoader;
	private long currentTime;

	@Before
	public void getCurrentTime() {
		currentTime = System.currentTimeMillis();
	}

	@After
	public void printTime() {
		System.out.println(System.currentTimeMillis() - currentTime + " millSecond");
	}

	@Test
	@Ignore
	public void getTableNames() {
		Set<String> tableNames = metaLoader.getTableNames();

		System.out.println(tableNames);
	}

	@Test
	@Ignore
	public void getTable() {
		String tableName = "des_primary_key_des_column";// Oracle:"PUMP",mySql:"person_info":sql
													// server:"Dataset"
		Table table = metaLoader.getTable(tableName);
		String result=PrintUtils.getTableInfo(table);
		System.out.println(result);
	}

	@Test
	@Ignore
	public void getTableInfo() {
		long startTime = System.currentTimeMillis();
		String tableName = "testcolumntype";// Oracle:"PUMP",mySql:"person_info":sql
													// server:"Dataset"
		Table table = metaLoader.getTable(tableName,SchemaInfoLevel.max());
		System.out.println(table);

		long endTime = System.currentTimeMillis();
		System.out.println("耗时：" + (endTime - startTime));
	}

	@Test
	@Ignore
	public void getTableLevel() {
		long startTime = System.currentTimeMillis();
		String tableName = "TYPETEST";// Oracle:"PUMP",mySql:"person_info":sql
										// server:"Dataset"
		Table table = metaLoader.getTable(tableName, SchemaInfoLevel.max());
		System.out.println(table);

		long endTime = System.currentTimeMillis();
		System.out.println("耗时：" + (endTime - startTime));
	}

	@Test
	@Ignore
	public void getSchemaInfos() {
		long startTime = System.currentTimeMillis();

		Set<SchemaInfo> schemaInfos = metaLoader.getSchemaInfos();
		System.out.println(schemaInfos);
		long endTime = System.currentTimeMillis();
		System.out.println("耗时：" + (endTime - startTime));
	}

	@Test
	@Ignore
	public void getSchema() {
		Schema schema = metaLoader.getSchema(SchemaInfoLevel.max());
		System.out.println(schema);
	}

	@Test
	@Ignore
	public void getDatabase() {
		@SuppressWarnings("deprecation")
		Database database = metaLoader.getDatabase(SchemaInfoLevel.min());
		System.out.println(database);
	}

	@Test
	@Ignore
	public void getSchemas() {
		Set<SchemaInfo> schemas = metaLoader.getSchemaInfos();
		int num = 0;
		int schemaNum=0;
		try {
			for (SchemaInfo schemaInfo : schemas) {
				if(schemaInfo.getSchemaName()!=null&&schemaInfo.getSchemaName().equals("XDB")){
					continue;
				}
				Schema s = metaLoader.getSchema(schemaInfo);
				num += s.getTables().size();
				schemaNum++;
//				System.out.println(s);
//				System.out.println(num);
			}
		} catch (DataAccessException e) {
			e.printStackTrace();
			System.out.println(num);
			System.out.println(schemaNum);
		}
	}
	
	@Test
	@Ignore
	public void getProcedureNames(){
		Set<String> procedureNames=metaLoader.getProcedureNames();
		System.out.println(procedureNames);
	}
	
	@Test
	@Ignore
	public void getProcedure(){
		Procedure p=metaLoader.getProcedure("dt_generateansiname");
		System.out.println(p);
	}
	
	@Test
	@Ignore
	public void getProcedures(){
		Map<String, Procedure> ps=metaLoader.getProcedures();
		System.out.println(ps);
		System.out.println(ps.size());
	}
	
	@Test
	@Ignore
	public void getTriggerNames(){
		Set<String> names=metaLoader.getTriggerNames();
		System.out.println(names);
	}
	
	@Test
	@Ignore
	public void getTrigger(){
		Trigger t=metaLoader.getTrigger("trigger_test");
		System.out.println(t);
	}
	
	@Test
	@Ignore
	public void getTriggers(){
		Map<String, Trigger> ts=metaLoader.getTriggers();
		System.out.println(ts);
	}
	
	
	@Test
	@Ignore
	public void getFunctionNames(){
		Set<String>  names=metaLoader.getFunctionNames();
		System.out.println(names);
	}
	
	@Test
	@Ignore
	public void getFunction(){
		Function functio=metaLoader.getFunction("Functontest");
		System.out.println(functio);
	}
	
	@Test
	@Ignore
	public void getFunctions(){
		Map<String, Function> functions=metaLoader.getFunctions();
		System.out.println(functions);
	}
}
