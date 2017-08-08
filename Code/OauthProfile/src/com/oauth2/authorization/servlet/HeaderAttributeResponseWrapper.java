package com.oauth2.authorization.servlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class HeaderAttributeResponseWrapper extends HttpServletResponseWrapper{

	
	
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
	public HeaderAttributeResponseWrapper(HttpServletResponse response) {
		super(response);
		headerMap= new HashMap<String,String>();

	}
	
	
	
	public Collection<String> getHeaderNames() {
		HttpServletRequest request = (HttpServletRequest) getResponse();
		List<String> list = new ArrayList<String>();
		for (Enumeration<String> e = request.getHeaderNames(); e.hasMoreElements();)
			list.add(e.nextElement().toString());
		for (Iterator<String> i = headerMap.keySet().iterator(); i.hasNext();) {
			list.add(i.next());
		}
		return list;
	}

	public String getHeader(String name) {
		Object value;
		if ((value = headerMap.get(name)) != null)
			return value.toString();
		else
			return ((HttpServletResponse) getResponse()).getHeader(name);
	}

}
