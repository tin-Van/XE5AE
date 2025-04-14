package com.poly.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class serviceimpl implements service {
	@Autowired
	HttpServletRequest req;
	@Autowired 
	HttpServletResponse resp;
	@Autowired
	HttpSession session;
	
	@Override
	public Object getValue(String name) {
		Object result = req.getParameter(name);
		return result;
	}
	
	@Override
	public Cookie saveUserByCookie(String name, String value, int days) {
		Cookie cookies = new Cookie(name, value);
		cookies.setMaxAge(days * 60 * 60);
		cookies.setPath("/");
		resp.addCookie(cookies);
		return cookies;
	}
	
	@Override
	public Cookie getCookieByName(String name) {
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equalsIgnoreCase(name)) {
					return cookie;
				}
			}
		}
		return null;
	}
	@Override
	public void removeCookieByName(String name) {
		// create a cookie
		Cookie cookie = new Cookie(name, null);
		cookie.setMaxAge(0);
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		cookie.setPath("/");

		//add cookie to response
		resp.addCookie(cookie);
	}
	@Override
	public void saveBySession(String name, Object value) {
		session.setAttribute(name, value);

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getSession(String name) {
		session = req.getSession();
		return (T) session.getAttribute(name);
	}

	@Override
	public void removeSession(String name) {
		session.removeAttribute(name);

	}
	@Override
	public Map<Boolean, String> getActive() {
		Map<Boolean, String> map = new LinkedHashMap<>();
		map.put(true, "Có");
		map.put(false, "Không");
		return map;
	}
	
}
