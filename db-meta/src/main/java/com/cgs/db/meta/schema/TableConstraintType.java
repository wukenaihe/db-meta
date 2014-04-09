package com.cgs.db.meta.schema;

/**
 * <p>Primary key and Foreign key is constraint. But we have show it an other way</p>
 * 
 * @author xumh
 *
 */
public enum TableConstraintType {
	unknown("unknown"), check("CHECK"), unique("UNIQUE");
//	, primary_key("PRIMARY KEY"), foreign_key("FOREIGN KEY");

	/**
	 * Find the enumeration value corresponding to the string.
	 * 
	 * @param value
	 *            Sort sequence code.
	 * @return Enumeration value
	 */
	public static TableConstraintType valueOfFromValue(final String value) {
		for (final TableConstraintType type : TableConstraintType.values()) {
			if (type.getValue().equalsIgnoreCase(value)) {
				return type;
			}
		}
		return unknown;
	}

	private final String value;

	private TableConstraintType(final String value) {
		this.value = value;
	}

	/**
	 * Gets the value.
	 * 
	 * @return Value
	 */
	public final String getValue() {
		return value;
	}
}
