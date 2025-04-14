package com.poly.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.poly.entity.Account;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountDAO extends JpaRepository<Account, Integer> {
	
	List<Account> findTop4ByOrderByCreatedDateDesc();
	  
    Optional<Account> findByUsername(String username);
    

    boolean existsByUsername(String username);

    Optional<Account> findByEmail(String email);

    boolean existsByEmail(String email);
    

    @Query("SELECT COUNT(b) FROM Account b")
    Long countTotalAccount();
    
    static void setCreatedDate(java.util.Date date) {
//     
    }

  
}
