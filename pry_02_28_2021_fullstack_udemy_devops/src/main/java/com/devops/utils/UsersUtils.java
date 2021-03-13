package com.devops.utils;

import com.devops.backend.persistence.domain.backend.User;

public class UsersUtils {

	// Ya que no queremos que se creen instancias de esta clase..
	private UsersUtils() {
		throw new AssertionError("Non instantiable");
	}
	
    public static User createBasicUser() {

		User user = new User();
		user.setUsername("basicUsername");
		user.setPassword("secret");
		user.setEmail("me@example.com");
		user.setFirstName("firstName");
		user.setLastName("lastName");
		user.setPhoneNumber("123123123");
		user.setCountry("GB");
		user.setEnabled(true);
		user.setDescription("A basic user");
		user.setProfileImageUrl("https://blabla.images.com/basicuser");
		return user;
    }	
	
}
