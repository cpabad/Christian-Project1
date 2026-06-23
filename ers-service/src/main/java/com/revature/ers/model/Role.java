package com.revature.ers.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA entity for the existing {@code roles} table (roleid 1 = Supervisor, 2 = Employee).
 *
 * Notes vs the monolith's model:
 *   - jakarta.persistence (not javax) - the Spring Boot 3 namespace change.
 *   - Lombok @Data/@NoArgsConstructor/@AllArgsConstructor replace the hand-written
 *     getters/setters/constructors/equals/hashCode/toString.
 *   - Columns are named explicitly (roleid, roles) so we do NOT depend on the naming
 *     strategy: the DB was created by plain Hibernate (as-is), but Spring Boot's default
 *     strategy is snake_case, which would otherwise look for "role_id".
 *   - The mixed-case schema is wrapped in embedded quotes so Postgres keeps the exact case.
 */
@Entity
@Table(name = "roles", schema = "\"ExpenseReimbursementManagementSystem\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @Column(name = "roleid")
    private Integer roleId;

    @Column(name = "roles", nullable = false, unique = true)
    private String role;
}
