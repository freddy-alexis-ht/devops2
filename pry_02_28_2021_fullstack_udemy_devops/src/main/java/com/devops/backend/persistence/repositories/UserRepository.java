package com.devops.backend.persistence.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.devops.backend.persistence.domain.backend.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
	/**
	 * Returns a User given a username or null if not found.
	 * @param username - The username
	 * @return a User given a username or null if not found.
	 * */
	public User findByUsername(String username);
}
