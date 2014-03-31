package com.cgs.db.meta.schema;

import java.sql.DatabaseMetaData;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
* Foreign key update and delete rules.
*/
public enum ForeignKeyUpdateRule
{

 /**
  * Unknown
  */
 unknown(-1, "unknown"),
 /**
  * No action.
  */
 noAction(DatabaseMetaData.importedKeyNoAction, "no action"),
 /**
  * Cascade.
  */
 cascade(DatabaseMetaData.importedKeyCascade, "cascade"),
 /**
  * Set null.
  */
 setNull(DatabaseMetaData.importedKeySetNull, "set null"),
 /**
  * Set default.
  */
 setDefault(DatabaseMetaData.importedKeySetDefault, "set default"),
 /**
  * Restrict.
  */
 restrict(DatabaseMetaData.importedKeyRestrict, "restrict");

 private static final Logger LOGGER = Logger
   .getLogger(ForeignKeyUpdateRule.class.getName());

 /**
  * Gets the enum value from the integer.
  * 
  * @param id
  *        Id of the integer
  * @return ForeignKeyUpdateRule
  */
 public static ForeignKeyUpdateRule valueOf(final int id)
 {
   for (final ForeignKeyUpdateRule type: ForeignKeyUpdateRule.values())
   {
     if (type.getId() == id)
     {
       return type;
     }
   }
   LOGGER.log(Level.FINE, "Unknown id " + id);
   return unknown;
 }

 private final String text;
 private final int id;

 private ForeignKeyUpdateRule(final int id, final String text)
 {
   this.id = id;
   this.text = text;
 }

 /**
  * Gets the id.
  * 
  * @return id
  */
 public int getId()
 {
   return id;
 }

 /**
  * {@inheritDoc}
  * 
  * @see Object#toString()
  */
 @Override
 public String toString()
 {
   return text;
 }

}
