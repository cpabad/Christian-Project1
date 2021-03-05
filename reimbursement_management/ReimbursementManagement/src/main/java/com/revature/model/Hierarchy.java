package com.revature.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * A model that representing the relationship between a supervisor and an employee.
 * @author ca132
 *
 */
@Entity
@Table(name = "employee_supervisor_jt", schema = "\"ExpenseReimbursementManagementSystem\"")
public class Hierarchy {
	
	/**
	 * The hierarchyId field is a unique numeric identifier for a specific relationship between a supervisor and an employee.
	 */
	@Column
	@Id
	private int hierarchyId;
	/**
	 * The supervisorUser field is the supervisor for a supervisor-employee relationship.
	 */
	@ManyToOne
	@JoinColumn(name = "userIdSupervisor")
	private User supervisorUser;
	/**
	 * The employeeUser field is the employee for a supervisor-employee relationship.
	 */
	@ManyToOne
	@JoinColumn(name = "userIdEmployee")
	private User employeeUser;
	public int getHierarchyId() {
		return hierarchyId;
	}
	public void setHierarchyId(int hierarchyId) {
		this.hierarchyId = hierarchyId;
	}
	public User getSupervisorUser() {
		return supervisorUser;
	}
	public void setSupervisorUser(User supervisorUser) {
		this.supervisorUser = supervisorUser;
	}
	public User getEmployeeUser() {
		return employeeUser;
	}
	public void setEmployeeUser(User employeeUser) {
		this.employeeUser = employeeUser;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((employeeUser == null) ? 0 : employeeUser.hashCode());
		result = prime * result + hierarchyId;
		result = prime * result + ((supervisorUser == null) ? 0 : supervisorUser.hashCode());
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
		Hierarchy other = (Hierarchy) obj;
		if (employeeUser == null) {
			if (other.employeeUser != null)
				return false;
		} else if (!employeeUser.equals(other.employeeUser))
			return false;
		if (hierarchyId != other.hierarchyId)
			return false;
		if (supervisorUser == null) {
			if (other.supervisorUser != null)
				return false;
		} else if (!supervisorUser.equals(other.supervisorUser))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Hierarchy [hierarchyId=" + hierarchyId + ", supervisorUser=" + supervisorUser + ", employeeUser="
				+ employeeUser + "]";
	}
	public Hierarchy(int hierarchyId, User supervisorUser, User employeeUser) {
		super();
		this.hierarchyId = hierarchyId;
		this.supervisorUser = supervisorUser;
		this.employeeUser = employeeUser;
	}
	public Hierarchy() {
		super();
	}
	
	
	

}
