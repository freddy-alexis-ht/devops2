package com.devops.utils;

import com.devops.backend.persistence.domain.backend.User;

public class UserUtils {

	// Ya que no queremos que se creen instancias de esta clase..
	private UserUtils() {
		throw new AssertionError("Non instantiable");
	}
	
	/** 
	 * Creates a user with basic attributes set.
	 * @param username - The username.
	 * @param email - The email.
	 * @return A User entity
	 */
    public static User createBasicUser(String username, String email) {

		User user = new User();
		user.setUsername(username);
		user.setPassword("secret");
		user.setEmail(email);
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
