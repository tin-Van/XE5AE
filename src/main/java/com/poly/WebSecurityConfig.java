package com.poly;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.poly.service.AccountService;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {
	
	@Autowired
	private final AccountService service;
	
	@Bean
	public UserDetailsService userDetailsService() {
		return service;
	}
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(service);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	http
			.csrf(csrf -> csrf.disable())
			.cors(cors -> cors.disable())
			//Phân Quyền Sử Dụng 
			.authorizeHttpRequests((requests) -> requests
				.requestMatchers("/admin").hasAnyRole("admin","staff")
				.requestMatchers("/user").hasAnyRole("admin","staff")
				.requestMatchers("/manager").hasAnyRole("admin","staff","authenticated")
				.requestMatchers("/order/**").hasAnyRole("admin","staff","authenticated")
				.anyRequest().permitAll()
			)
			
			 .exceptionHandling(exceptionHandling -> exceptionHandling
		                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
		            )
			
			//Chuyển người dùng khi chưa authenticated
			.formLogin(httpform -> {
			httpform.loginPage("/login");
			httpform.defaultSuccessUrl("/home");
			})
			//Đăng xuất
			.logout(logout ->{
				logout.logoutUrl("/logout");
				logout.logoutSuccessUrl("/home");
//				logout.permitAll();
			})
			
			//Điều khiển lỗi khi không đúng vai trò
			.exceptionHandling(ex ->
			ex.accessDeniedPage("/denied/403")
					
		)
			;
		
		return http.build();
	}
	
	//Mã Hóa mật Khẩu
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
