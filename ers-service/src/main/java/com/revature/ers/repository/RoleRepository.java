package com.revature.ers.repository;

import com.revature.ers.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * THE QUICK WIN: the monolith's ~50-line RoleRepositoryImpl (manual Session, try/catch,
 * commit/rollback, close) collapses to this. JpaRepository already provides findAll(),
 * findById(), save(), delete(), count(), etc. - Spring generates the implementation at
 * runtime. Custom finders would be added here as derived query methods (e.g.
 * {@code Optional<Role> findByRole(String role)}).
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
}
