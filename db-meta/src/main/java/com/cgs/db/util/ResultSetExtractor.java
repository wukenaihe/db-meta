package com.cgs.db.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.cgs.db.exception.DataAccessException;

public interface ResultSetExtractor<T> {
	
	T extractData(ResultSet rs) throws SQLException;
}
