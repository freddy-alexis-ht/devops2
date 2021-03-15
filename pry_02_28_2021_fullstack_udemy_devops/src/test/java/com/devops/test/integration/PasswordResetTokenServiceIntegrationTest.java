package com.devops.test.integration;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.TestName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.devops.backend.persistence.domain.backend.PasswordResetToken;
import com.devops.backend.persistence.domain.backend.User;
import com.devops.backend.service.PasswordResetTokenService;

@SpringBootTest
public class PasswordResetTokenServiceIntegrationTest extends AbstractServiceIntegrationTest {

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Rule 
    public TestName testName = new TestName();

    @Test
    public void testCreateNewTokenForUserEmail() throws Exception {

        User user = createUser(testName);

        PasswordResetToken passwordResetToken =
                passwordResetTokenService.createPasswordResetTokenForEmail(user.getEmail());
        Assert.assertNotNull(passwordResetToken);
        Assert.assertNotNull(passwordResetToken.getToken());

    }

//    @Test
//    public void testFindByToken() throws Exception {
//        User user = createUser(testName);
//
//        PasswordResetToken passwordResetToken =
//                passwordResetTokenService.createPasswordResetTokenForEmail(user.getEmail());
//        Assert.assertNotNull(passwordResetToken);
//        Assert.assertNotNull(passwordResetToken.getToken());
//
//        PasswordResetToken token = passwordResetTokenService.findByToken(passwordResetToken.getToken());
//        Assert.assertNotNull(token);
//
//    }

}
