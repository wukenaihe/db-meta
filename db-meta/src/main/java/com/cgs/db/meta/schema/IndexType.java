package com.cgs.db.meta.schema;

import java.sql.DatabaseMetaData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An enumeration wrapper around index types.
 */
public enum IndexType
{

  /**
   * Statistic.
   */
  statistic(DatabaseMetaData.tableIndexStatistic),
  /**
   * Clustered.
   */
  clustered(DatabaseMetaData.tableIndexClustered),
  /**
   * Hashed.
   */
  hashed(DatabaseMetaData.tableIndexHashed),
  /**
   * Other.
   */
  other(DatabaseMetaData.tableIndexOther);

  /**
   * Gets the value from the id.
   * 
   * @param id
   *        Id of the enumeration.
   * @return IndexType
   */
  public static IndexType valueOf(final int id)
  {
    for (final IndexType type: IndexType.values())
    {
      if (type.getId() == id)
      {
        return type;
      }
    }
    return other;
  }

  private final int id;

  private IndexType(final int id)
  {
    this.id = id;
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

}
