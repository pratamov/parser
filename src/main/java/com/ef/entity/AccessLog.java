package com.ef.entity;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ef.configuration.DateConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AccessLog {
	
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.S";
	public static final String ACCESS_LOG_REGEX = 
			// Date
			"^([^|]*)\\|"
			// IP address
			+ "([0-9.]*)\\|"
			// request
			+ "([^|]*)\\|"
			// status
			+ "(\\d*)\\|"
			// User agent
			+ "([^\\n\\r]*)";
	
	private Date date;
	private String ip;
	private String request;
	private String status;
	private String userAgent;
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public static AccessLog parse(String line) {
		
		AccessLog accessLog = new AccessLog();
		Pattern pattern = Pattern.compile(ACCESS_LOG_REGEX);
		Matcher matcher = pattern.matcher(line);
		
		while (matcher.find()) {
			Date date = DateConverter.convert(matcher.group(1), DATE_FORMAT);			
			accessLog.setDate(date);
			accessLog.setIp(matcher.group(2));
			accessLog.setRequest(matcher.group(3));
			accessLog.setStatus(matcher.group(4));
			accessLog.setUserAgent(matcher.group(5));
		}
		return accessLog;
		
	}
	
	public boolean meets(Date startDate, Date endDate) {
		return (date.compareTo(endDate) < 0 && date.compareTo(startDate) >= 0);
	}
	
	public String toString() {
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			String json = mapper.writeValueAsString(this);
			return json;
		} catch (JsonProcessingException e) {
			return "{}";
		}
		
	}
	
}
