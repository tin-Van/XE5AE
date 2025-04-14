package com.poly.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;

@Service
public interface service {
	
	public Object getValue(String name);
	
	public Cookie saveUserByCookie(String name, String value, int days);
	
	public Cookie getCookieByName(String name);

	public void removeCookieByName(String name);

	public void saveBySession(String name, Object value);

	public <T> T getSession(String name);

	public void removeSession(String name);
	
	public Map<Boolean, String> getActive();
}
