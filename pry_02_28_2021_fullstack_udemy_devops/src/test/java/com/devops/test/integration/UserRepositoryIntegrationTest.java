package com.devops.test.integration;

import java.util.Set;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.TestName;
import org.springframework.boot.test.context.SpringBootTest;

import com.devops.backend.persistence.domain.backend.Plan;
import com.devops.backend.persistence.domain.backend.Role;
import com.devops.backend.persistence.domain.backend.User;
import com.devops.backend.persistence.domain.backend.UserRole;
import com.devops.enums.PlansEnum;
import com.devops.enums.RolesEnum;

@SpringBootTest
public class UserRepositoryIntegrationTest extends AbstractIntegrationTest {
    
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
    
    @Test
    public void testGetUserByEmail() throws Exception {
        User user = createUser(testName);

        User newlyFoundUser = userRepository.findByEmail(user.getEmail());
        Assert.assertNotNull(newlyFoundUser);
        Assert.assertNotNull(newlyFoundUser.getId());
    }

    @Test
    public void testUpdateUserPassword() throws Exception {
        User user = createUser(testName);
        Assert.assertNotNull(user);
        Assert.assertNotNull(user.getId());

        String newPassword = UUID.randomUUID().toString();

        userRepository.updateUserPassword(user.getId(), newPassword);

        user = userRepository.findById(user.getId()).orElse(null);
        Assert.assertEquals(newPassword, user.getPassword());

    }
}

