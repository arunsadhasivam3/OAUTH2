package com.oauth2.authorization.servlet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
@WebServlet(value="/headerRequest", name="headerRequest")
public class HeaderAttributeRequestWrapper extends HttpServletRequestWrapper {
	private Map<String,String> headerMap;

	
	public void addAuthHeader(java.lang.String name, java.lang.String value, int encType) {
		if (encType == 1) {
			headerMap.put(name, "Basic " + value);
		} else {
			headerMap.put(name, "Bearer " + value);
		}
	}
	
	
	public void addHeader(java.lang.String name, java.lang.String value) {
			headerMap.put(name,"Basic " +value);
	}
	public HeaderAttributeRequestWrapper(HttpServletRequest request) {
		super(request);
		headerMap= new HashMap<String,String>();

	}
	
	
	
	public Enumeration<String> getHeaderNames() {
		HttpServletRequest request = (HttpServletRequest) getRequest();
		List<String> list = new ArrayList<String>();
		for (Enumeration<String> e = request.getHeaderNames(); e.hasMoreElements();)
			list.add(e.nextElement().toString());
		for (Iterator<String> i = headerMap.keySet().iterator(); i.hasNext();) {
			list.add(i.next());
		}
		return Collections.enumeration(list);
	}

	public String getHeader(String name) {
		Object value;
		if ((value = headerMap.get(name)) != null)
			return value.toString();
		else
			return ((HttpServletRequest) getRequest()).getHeader(name);
	}

}
