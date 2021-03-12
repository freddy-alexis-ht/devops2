package com.devops.test.integration;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.devops.backend.persistence.domain.backend.Plan;
import com.devops.backend.persistence.domain.backend.Role;
import com.devops.backend.persistence.domain.backend.User;
import com.devops.backend.persistence.domain.backend.UserRole;
import com.devops.backend.persistence.repositories.PlanRepository;
import com.devops.backend.persistence.repositories.RoleRepository;
import com.devops.backend.persistence.repositories.UserRepository;

@SpringBootTest
public class RepositoriesIntegrationTest {

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;
    
    private static final int BASIC_PLAN_ID = 1;
    private static final int BASIC_ROLE_ID = 1;
    
    // Este método se ejecutará antes de que se ejecute cada test
    // Aquí queremos asegurarnos que nuestros repositorios no son null
    // 'assert' significa 'debe ser', por lo tanto 'assertNotNull' significa que no debe ser null
    @Before // org.junit
    public void init() {
    	Assert.assertNotNull(planRepository);
    	Assert.assertNotNull(roleRepository);
    	Assert.assertNotNull(userRepository);
    }
    
    //-----------------> Tests

    // Este método creará un Plan, lo guardará, lo recuperará y verificará que no sea null
    // En el video usaba 'planRepository.findOne(..)', pero en la versión que uso..
    // ..ya no existe ese método, en su lugar está 'findById(..)', este método devuelve..
    // ..un Optional<T>. En este caso se usa orElse(null), en caso exista el valor, lo..
    // ..devolverá, caso contrario devolverá null
    @Test 
    public void testCreateNewPlan() throws Exception {
        Plan basicPlan = createBasicPlan();
        planRepository.save(basicPlan);
        Plan retrievedPlan = planRepository.findById(BASIC_PLAN_ID).orElse(null);
        Assert.assertNotNull(retrievedPlan);
    }
    
    @Test 
    public void testCreateNewRole() throws Exception {
    	Role userRole = createBasicRole();
    	roleRepository.save(userRole);
    	Role retrievedRole = roleRepository.findById(BASIC_ROLE_ID).orElse(null);
    	Assert.assertNotNull(retrievedRole);
    }
    
    @Test 
    public void testCreateNewUser() throws Exception {
    
    	// Create and save a Plan record
        Plan basicPlan = createBasicPlan();
        planRepository.save(basicPlan);

        // Se crea un User-instance con el método-private
        // Se settea la entidad-Plan guardada como FK en User
        User basicUser = createBasicUser();
        basicUser.setPlan(basicPlan);

        // Se crea un Role, UserRole y un Set-UserRole
        // userRoles tendrá todas roles de un usuario específico.
        Role basicRole = createBasicRole();
        Set<UserRole> userRoles = new HashSet<>();
        UserRole userRole = new UserRole();
        userRole.setUser(basicUser);
        userRole.setRole(basicRole);
        userRoles.add(userRole);
        
        // * To add values to a collection within a JPA entity, always use the getter..
        // ..method first and add all the objects afterwards.
        // Si solo se usara el setter los valores anteriores se borrarían.
        basicUser.getUserRoles().addAll(userRoles);

        // Before saving the User-instance, it's necessary to save the other side of the..
        // ..'User to Role' relationship by persisting all Role's. De otro modo tal vez la..
        // ..relación M:N no funcione por problemas de inconsistencia.
        for (UserRole ur : userRoles) {
            roleRepository.save(ur.getRole());
        }

        // Now that all relationship entities have been saved, it saves the User-entity
        basicUser = userRepository.save(basicUser);
        User newlyCreatedUser = userRepository.findById(basicUser.getId()).orElse(null);
        
        // If all relationships contain data after running 'findById()', it means..
        // ..our Repositories work correctly
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
    
    //-----------------> Private methods

    private Plan createBasicPlan() {
    	Plan plan = new Plan();
    	plan.setId(BASIC_PLAN_ID);
    	plan.setName("Basic");
    	return plan;
    }
    
    private Role createBasicRole() {
    	Role role = new Role();
    	role.setId(BASIC_ROLE_ID);
    	role.setName("ROLE_USER");
    	return role;
    }
    
    private User createBasicUser() {

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

