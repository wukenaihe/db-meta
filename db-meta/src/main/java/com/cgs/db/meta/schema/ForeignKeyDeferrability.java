package com.cgs.db.meta.schema;

import java.sql.DatabaseMetaData;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The deferrability value for foreign keys.
 */
public enum ForeignKeyDeferrability
{

  /**
   * Unknown
   */
  unknown(-1, "unknown"),
  /**
   * Initially deferred.
   */
  initiallyDeferred(DatabaseMetaData.importedKeyInitiallyDeferred,
    "initially deferred"),
  /**
   * Initially immediate.
   */
  initiallyImmediate(DatabaseMetaData.importedKeyInitiallyImmediate,
    "initially immediate"),
  /**
   * Not deferrable.
   */
  keyNotDeferrable(DatabaseMetaData.importedKeyNotDeferrable, "not deferrable");

  private static final Logger LOGGER = Logger
    .getLogger(ForeignKeyDeferrability.class.getName());

  /**
   * Gets the enum value from the integer.
   * 
   * @param id
   *        Id of the integer
   * @return ForeignKeyDeferrability
   */
  public static ForeignKeyDeferrability valueOf(final int id)
  {
    for (final ForeignKeyDeferrability fkDeferrability: ForeignKeyDeferrability
      .values())
    {
      if (fkDeferrability.getId() == id)
      {
        return fkDeferrability;
      }
    }
    LOGGER.log(Level.FINE, "Unknown id " + id);
    return unknown;
  }

  private final int id;
  private final String text;

  private ForeignKeyDeferrability(final int id, final String text)
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

