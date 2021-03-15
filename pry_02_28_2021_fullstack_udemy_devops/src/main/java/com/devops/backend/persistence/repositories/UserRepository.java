package com.devops.backend.persistence.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.devops.backend.persistence.domain.backend.User;

@Repository
@Transactional(readOnly = true)
public interface UserRepository extends CrudRepository<User, Long> {
	/**
	 * Returns a User given a username or null if not found.
	 * @param username - The username
	 * @return a User given a username or null if not found.
	 * */
	User findByUsername(String username);
	
    /**
     * Returns a User for the given email or null if none was found.
     * @param email - The user's email
     * @return a User for the given email or null if none was found.
     */
    User findByEmail(String email);	
    
    @Modifying
    @Query("update User u set u.password = :password where u.id = :userId")
    void updateUserPassword(@Param("userId") long userId, @Param("password") String password);

}
