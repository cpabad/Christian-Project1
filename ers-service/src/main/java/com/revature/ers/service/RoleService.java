package com.revature.ers.service;

import com.revature.ers.model.Role;
import com.revature.ers.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Business layer for roles. Constructor injection (no field @Autowired) keeps the class
 * testable with a plain mock - the same style the monolith's services can adopt.
 */
@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }
}
