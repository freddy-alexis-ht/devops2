package com.devops.test.unit;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

import com.devops.utils.UserUtils;
import com.devops.web.controllers.ForgotMyPasswordController;

@SpringBootTest
public class UserUtilsUnitTest {

	// import org.springframework.mock.web.MockHttpServletRequest;
    @Autowired
	private MockHttpServletRequest mockHttpServletRequest;

    @Before
    public void init() {
        this.mockHttpServletRequest = new MockHttpServletRequest();
    }

    @Test
    public void testPasswordResetEmailUrlConstruction() throws Exception {

        mockHttpServletRequest.setServerPort(8080); //Default is 80

        String token = UUID.randomUUID().toString();
        long userId = 123456;

        String expectedUrl = "http://localhost:8080" +
                ForgotMyPasswordController.CHANGE_PASSWORD_PATH + "?id=" + userId + "&token=" + token;

        String actualUrl = UserUtils.createPasswordResetUrl(mockHttpServletRequest, userId, token);

        Assert.assertEquals(expectedUrl, actualUrl);

    }
	
}
