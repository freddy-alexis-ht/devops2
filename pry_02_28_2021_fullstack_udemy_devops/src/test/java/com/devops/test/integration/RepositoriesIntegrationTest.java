package com.devops.test.integration;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.TestName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.devops.backend.persistence.domain.backend.Plan;
import com.devops.backend.persistence.domain.backend.Role;
import com.devops.backend.persistence.domain.backend.User;
import com.devops.backend.persistence.domain.backend.UserRole;
import com.devops.backend.persistence.repositories.PlanRepository;
import com.devops.backend.persistence.repositories.RoleRepository;
import com.devops.backend.persistence.repositories.UserRepository;
import com.devops.enums.PlansEnum;
import com.devops.enums.RolesEnum;
import com.devops.utils.UserUtils;

@SpringBootTest
public class RepositoriesIntegrationTest {

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Rule
    public TestName testName = new TestName();
    
    @Before 
    public void init() {
    	Assert.assertNotNull(planRepository);
    	Assert.assertNotNull(roleRepository);
    	Assert.assertNotNull(userRepository);
    }
    
    //-----------------> Tests

    @Test 
    public void testCreateNewPlan() throws Exception {
        Plan basicPlan = createPlan(PlansEnum.BASIC);
        planRepository.save(basicPlan);
        Plan retrievedPlan = planRepository.findById(PlansEnum.BASIC.getId()).orElse(null);
        Assert.assertNotNull(retrievedPlan);
    }
    
    @Test 
    public void testCreateNewRole() throws Exception {
    	Role userRole = createRole(RolesEnum.BASIC);
    	roleRepository.save(userRole);
    	Role retrievedRole = roleRepository.findById(RolesEnum.BASIC.getId()).orElse(null);
    	Assert.assertNotNull(retrievedRole);
    }
    
    @Test 
    public void testCreateNewUser() throws Exception {

    	String username = testName.getMethodName();
    	String email = testName.getMethodName() + "@devops.com";
        User basicUser = createUser(username, email);

        User newlyCreatedUser = userRepository.findById(basicUser.getId()).orElse(null);
        Assert.assertNotNull(newlyCreatedUser);
        Assert.assertTrue(newlyCreatedUser.getId() != 0);
        Assert.assertNotNull(newlyCreatedUser.getPlan());
        Assert.assertNotNull(newlyCreatedUser.getPlan().getId());
        Set<UserRole> newlyCreatedUserUserRoles = newlyCreatedUser.getUserRoles();
        for (UserRole ur : newlyCreatedUserUserRoles) {
            Assert.assertNotNull(ur.getRole());
            Assert.assertNotNull(ur.getRole().getId());
        }
    }
    
    @Test
    public void testDeleteUser() throws Exception {
    	String username = testName.getMethodName();
    	String email = testName.getMethodName() + "@devops.com";
        User basicUser = createUser(username, email);
    	userRepository.deleteById(basicUser.getId());
    }
    
    //-----------------> Private methods

    private Plan createPlan(PlansEnum plansEnum) {
    	return new Plan(plansEnum);
    }
    
    private Role createRole(RolesEnum rolesEnum) {
    	return new Role(rolesEnum);
    }
    
    private User createUser(String username, String email) {
        Plan basicPlan = createPlan(PlansEnum.BASIC);
        planRepository.save(basicPlan);

        User basicUser = UserUtils.createBasicUser(username, email);
        basicUser.setPlan(basicPlan);

        Role basicRole = createRole(RolesEnum.BASIC);
        
        roleRepository.save(basicRole);

        Set<UserRole> userRoles = new HashSet<>();
        
        UserRole userRole = new UserRole(basicUser, basicRole);
        userRoles.add(userRole);

        basicUser.getUserRoles().addAll(userRoles);
        
        basicUser = userRepository.save(basicUser);
        return basicUser;
    }    
}

