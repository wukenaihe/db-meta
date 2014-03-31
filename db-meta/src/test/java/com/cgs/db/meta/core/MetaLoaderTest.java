package com.cgs.db.meta.core;

import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cgs.db.meta.schema.Table;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:config.xml")
public class MetaLoaderTest {
	
	@Autowired
	public MetaLoader metaLoader;
	
	@Test
	@Ignore
	public void getTableNames(){
		Set<String> tableNames=metaLoader.getTableNames();
		
		System.out.println(tableNames);
	}
	
	@Test
//	@Ignore
	public void getTable(){
		String tableName="Dataset";//Oracle:"CH_RDDATA",mySql:"person_info":sql server:"Dataset"
		Table table=metaLoader.getTable(tableName);
		System.out.println(table);
	}
}
