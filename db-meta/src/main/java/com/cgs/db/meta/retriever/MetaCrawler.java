package com.cgs.db.meta.retriever;

import java.util.Map;
import java.util.Set;

import com.cgs.db.meta.schema.Column;
import com.cgs.db.meta.schema.Table;

public interface MetaCrawler {
	Set<String> getTableNames();
	
	Table getTable(String tableName);
	
}
