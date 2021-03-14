package com.devops.backend.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devops.backend.persistence.domain.backend.Plan;
import com.devops.backend.persistence.domain.backend.User;
import com.devops.backend.persistence.domain.backend.UserRole;
import com.devops.backend.persistence.repositories.PlanRepository;
import com.devops.backend.persistence.repositories.RoleRepository;
import com.devops.backend.persistence.repositories.UserRepository;
import com.devops.enums.PlansEnum;

@Service
@Transactional(readOnly=true)
public class UserService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // To methods that update or write records to the DB
    // no-arg -> read & write transactions
    @Transactional
    public User createUser(User user, PlansEnum plansEnum, Set<UserRole> userRoles) {

    	String encryptedPassword = passwordEncoder.encode(user.getPassword());
    	user.setPassword(encryptedPassword);
    	
        Plan plan = new Plan(plansEnum);
        // It makes sure the plans exist in the database
        // En el video usa exists(), ya no es valido, en su lugar existsById()
        // Si no existe el plan lo crea
        if (!planRepository.existsById(plansEnum.getId())) {
            plan = planRepository.save(plan);
        }

        user.setPlan(plan);

        for (UserRole ur : userRoles) {
            roleRepository.save(ur.getRole());
        }

        user.getUserRoles().addAll(userRoles);

        user = userRepository.save(user);

        return user;

    }
}
