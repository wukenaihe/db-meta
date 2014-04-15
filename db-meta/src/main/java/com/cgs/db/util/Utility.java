package com.cgs.db.util;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tool class
 * 
 * @author xumh
 *
 */
public final class Utility {
	private static Logger logger=LoggerFactory.getLogger(Utility.class);
	
	private static final Pattern isAllWhitespacePattern = Pattern.compile("^\\s*$");
	
	  /**
	   * Checks if the text is null or empty.
	   * 
	   * @param text
	   *        Text to check.
	   * @return Whether the string is blank.
	   */
	  public static boolean isBlank(final String text)
	  {
	    return text == null || text.isEmpty()
	           || isAllWhitespacePattern.matcher(text).matches();
	  }
	  
	  public static String quote(String s){
		  return "'"+s+"'";
	  }
}
