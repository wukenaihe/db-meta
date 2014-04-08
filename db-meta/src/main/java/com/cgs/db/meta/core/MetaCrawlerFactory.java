package com.cgs.db.meta.core;

import java.sql.Connection;

import com.cgs.db.meta.retriever.MetaCrawler;

public interface MetaCrawlerFactory {
	/**
	 * according the database information create a MetaCrawler instance.
	 * You can implements an other MetaCrawlerFactory using you's MetaCrawler instance to
	 * crawle database meta;  
	 * 
	 * @param con
	 * @return
	 */
	MetaCrawler newInstance(Connection con);
}
