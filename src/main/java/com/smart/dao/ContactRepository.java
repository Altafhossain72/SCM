package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;
import com.smart.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
	
	@Query("from Contact as c where c.user.id=:userId")
	public Page<Contact> getContactsByUserId(@Param("userId") int userId,Pageable pageable);
	
	public List<Contact> findByNameContainingAndUser(String name, User user);
	
	//for seraching email
	@Modifying
    @Query(
            value = "select e.email from Contact e where e.email LIKE %:term% and e.user_id=:user",
            nativeQuery = true
    )
   public List<String> getEmail(String term, User user);

	
	//for seraching phone
		@Modifying
	    @Query(
	            value = "select e.phone from Contact e where e.phone LIKE %:term% and e.user_id=:user",
	            nativeQuery = true
	    )
	   public List<String> getPhone(String term, User user);
	
	
	
}
