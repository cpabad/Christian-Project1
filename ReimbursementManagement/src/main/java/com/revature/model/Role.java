package com.revature.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * A model representing a user's role. Only two roles exist: Supervisor and Employee.
 * @author ca132
 *
 */
@Entity
@Table(name = "roles", schema = "\"ExpenseReimbursementManagementSystem\"")
public class Role {
	
	/**
	 * The roleId field is a unique numeric identifier for a specific role.
	 */
	@Column
	@Id
	private int roleId;
	/**
	 * The role field is the String value for a specific role.
	 */
	@Column(name = "roles", nullable = false, unique = true)
	private String role;
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result + roleId;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Role other = (Role) obj;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		if (roleId != other.roleId)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Role [roleId=" + roleId + ", role=" + role + "]";
	}
	public Role(int roleId, String role) {
		super();
		this.roleId = roleId;
		this.role = role;
	}
	public Role() {
		super();
	}
	
	

}
