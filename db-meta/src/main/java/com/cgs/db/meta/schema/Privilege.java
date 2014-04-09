package com.cgs.db.meta.schema;

import java.io.Serializable;

public class Privilege implements Serializable{

	private static final long serialVersionUID = 6517818738846606160L;
	private String grantor;
    private String grantee;
    private String privilege;
    private boolean isGrantable;
    
    public Privilege(String grantor,String grantee,String privilege,boolean isGrantable){
    	this.grantor=grantor;
    	this.grantee=grantee;
    	this.privilege=privilege;
    	this.isGrantable=isGrantable;
    }

	public String getGrantor() {
		return grantor;
	}

	public void setGrantor(String grantor) {
		this.grantor = grantor;
	}

	public String getGrantee() {
		return grantee;
	}

	public void setGrantee(String grantee) {
		this.grantee = grantee;
	}

	public boolean isGrantable() {
		return isGrantable;
	}

	public void setGrantable(boolean isGrantable) {
		this.isGrantable = isGrantable;
	}

	public String getPrivilege() {
		return privilege;
	}

	public void setPrivilege(String privilege) {
		this.privilege = privilege;
	}

	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((grantee == null) ? 0 : grantee.hashCode());
		result = prime * result + ((grantor == null) ? 0 : grantor.hashCode());
		result = prime * result + (isGrantable ? 1231 : 1237);
		result = prime * result + ((privilege == null) ? 0 : privilege.hashCode());
		return result;
	}

	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Privilege other = (Privilege) obj;
		if (grantee == null) {
			if (other.grantee != null)
				return false;
		} else if (!grantee.equals(other.grantee))
			return false;
		if (grantor == null) {
			if (other.grantor != null)
				return false;
		} else if (!grantor.equals(other.grantor))
			return false;
		if (isGrantable != other.isGrantable)
			return false;
		if (privilege == null) {
			if (other.privilege != null)
				return false;
		} else if (!privilege.equals(other.privilege))
			return false;
		return true;
	}

	
	public String toString() {
		return "Privilege [grantor=" + grantor + ", grantee=" + grantee + ", privilege=" + privilege + ", isGrantable=" + isGrantable + "]";
	}

    
}
