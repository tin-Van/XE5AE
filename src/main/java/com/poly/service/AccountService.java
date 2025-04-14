package com.poly.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

import com.poly.DAO.AccountDAO;
import com.poly.entity.Account;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AccountService implements UserDetailsService  {
	
	@Autowired
	AccountDAO dao;  // DAO để thao tác với database
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Account userObj = dao.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException(email));
        if (userObj == null || !userObj.getIsEnabled()) {
            throw new UsernameNotFoundException("User not found");
        }
		return User.builder()
			.username(userObj.getEmail())
			.password(userObj.getPassword())  
			.roles(userObj.getRole())
			.build();
	}
}
