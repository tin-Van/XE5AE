package com.poly.DAO;

import com.poly.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenDAO extends JpaRepository<VerificationToken, Integer> {
	
	VerificationToken findByToken(String token);
    
    boolean existsByToken(String token);
}
