package com.devops.test.integration;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.TestName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.devops.backend.persistence.domain.backend.Role;
import com.devops.backend.persistence.domain.backend.User;
import com.devops.backend.persistence.domain.backend.UserRole;
import com.devops.backend.service.UserService;
import com.devops.enums.PlansEnum;
import com.devops.enums.RolesEnum;
import com.devops.utils.UserUtils;

@SpringBootTest
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Rule
    public TestName testName = new TestName();

    @Test
    public void testCreateNewUser() throws Exception {

        String username = testName.getMethodName();
        String email = testName.getMethodName() + "@devops.com";
        Set<UserRole> userRoles = new HashSet<>();
        User basicUser = UserUtils.createBasicUser(username, email);
        userRoles.add(new UserRole(basicUser, new Role(RolesEnum.BASIC)));

        User user = userService.createUser(basicUser, PlansEnum.BASIC, userRoles);
        Assert.assertNotNull(user);
        Assert.assertNotNull(user.getId());

    }
}
