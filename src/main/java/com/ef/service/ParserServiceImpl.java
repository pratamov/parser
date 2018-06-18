package com.ef.service;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import com.ef.ParserException;
import com.ef.configuration.Configuration;
import com.ef.entity.AccessLog;

public class ParserServiceImpl implements ParserService{
	
	private static final String SQL_INSERT_BLOCKED_LOG = 
			"INSERT IGNORE INTO `blocked_log` (`ip`, `requests_count`, `message`) VALUES (?, ?, ?)";
	
	private static final String SQL_INSERT_ACCESS_LOG = 
			"INSERT IGNORE INTO `access_log` (`date`, `ip`, `request`, `status`, `user_agent`) VALUES (?, ?, ?, ?, ?)";
	
	private static final String BLOCKED_MESSAGE = "This IP address has been blocked due to too many requests";
	
	Configuration configuration;
	Map<String, Integer> requests = new HashMap<>();
	Connection connection;
	
	public ParserServiceImpl() {}
	
	public ParserServiceImpl(Configuration configuration) {
		init(configuration);
	}
	
	@Override
	public void init(Configuration configuration) {
		this.configuration = configuration;
		try {
			this.connectToDatabase();
		} catch (ParserException e) {}
	}
	
	private void connectToDatabase() throws ParserException {
		
		String jdbcClass = configuration.getJdbcClass();
		String jdbcUrl = configuration.getJdbcUrl();
		String jdbcUsername = configuration.getJdbcUsername();
		String jdbcPassword = configuration.getJdbcPassword();
		
		try {
			Class.forName(jdbcClass);
		} catch (ClassNotFoundException e) {
			throw new ParserException("JDBC class " + jdbcClass + " is not found");
		}
		
		try {
			this.connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
			this.connection.setAutoCommit(false);
		} catch (SQLException e) {
			throw new ParserException(e.getMessage());
		}
		
	}
	
	private void loadAndParseLogFile() throws ParserException, IOException {
		
		if (configuration == null) { throw new ParserException("Configuration is null. Please call init() method"); }
		
		File file = new File(configuration.getAccesslog());
		
		LineIterator it = FileUtils.lineIterator(file, "UTF-8");
		
		PreparedStatement ps = null;
		
		Date startDate = configuration.getStartDate();
		Date endDate = configuration.getEndDate();
		try {
			ps = connection.prepareStatement(SQL_INSERT_ACCESS_LOG);
		} catch (SQLException e1) {}
		
		while (it.hasNext()) {
			
			String line = it.nextLine();
			AccessLog accessLog = AccessLog.parse(line);
			
			try {
				ps.setTimestamp(1, new java.sql.Timestamp(accessLog.getDate().getTime()));
				ps.setString(2, accessLog.getIp());
				ps.setString(3, accessLog.getRequest());
				ps.setInt(4, Integer.parseInt(accessLog.getStatus()));
				ps.setString(5, accessLog.getUserAgent());
				ps.addBatch();
			} catch (SQLException e) {}
			
			if (accessLog.meets(startDate, endDate)) {
				String ip = accessLog.getIp();
				Integer requestCount = requests.get(ip);
				if (requestCount == null) requestCount = 0;
				requests.put(ip, requestCount + 1);
			}
			
		}

		try {
			ps.executeBatch();
			connection.commit(); 
			ps.close();
		} catch (SQLException e) {}
		
	}

	private void selectAndSaveBlockedIp() throws ParserException{
		
		Iterator<Map.Entry<String,Integer>> it = requests.entrySet().iterator();
		PreparedStatement ps = null;
		
		try {
			ps = connection.prepareStatement(SQL_INSERT_BLOCKED_LOG);
		} catch (SQLException e) {}
		
		while (it.hasNext()) {
	    	Map.Entry<String,Integer> pair = (Map.Entry<String,Integer>) it.next();
	    	String ip = pair.getKey();
			int requestCount = pair.getValue();
	    	if ((int) pair.getValue() <= configuration.getThreshold())
	    		it.remove();
	    	else {
	    		try {
					ps.setString(1, ip);
					ps.setInt(2, requestCount);
					ps.setString(3, BLOCKED_MESSAGE);
					ps.addBatch();
				} catch (SQLException e) {}
	    	}
	    }
		
		try {
			ps.executeBatch();
			connection.commit(); 
			ps.close();
		} catch (SQLException e) {}
	}
	
	private void finish() {
		if (connection != null)
			try {
				connection.close();
			} catch (SQLException e) {}
	}

	@Override
	public void run() throws ParserException, IOException {
		
		this.loadAndParseLogFile();
		this.selectAndSaveBlockedIp();
		this.finish();
		
	}
	
}
