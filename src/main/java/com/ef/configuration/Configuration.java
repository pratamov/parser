package com.ef.configuration;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.ef.ParserException;

@Parameters(separators = "=")
public class Configuration {
	
	@Parameter
	private List<String> parameters = new ArrayList<>();
	
	@Parameter(names={"--help", "-h"}, help = true)
	private boolean help;
	
	@Parameter(names={"--accesslog", "-a"}, description = "Path to access log location")
	String accesslog = "access.log";
	
	@Parameter(names={"--startDate", "-s"}, converter = DateConverter.class, description = "Start Date")
	Date startDate;

	@Parameter(names={"--duration", "-d"}, description = "Duration")
	String duration;
	
	@Parameter(names={"--threshold", "-t"}, description = "Treshold")
	int threshold;
	
	@Parameter(names={"--jdbcClass", "-c"}, description = "JDBC Driver Class")
	String jdbcClass = "org.mariadb.jdbc.Driver";

	@Parameter(names={"--jdbcUrl", "-j"}, description = "JDBC URL")
	String jdbcUrl = "jdbc:mysql://localhost:3306/parser?useFractionalSeconds=true";
	
	@Parameter(names={"--jdbcUsername", "-u"}, description = "JDBC Username")
	String jdbcUsername = "";
	
	@Parameter(names={"--jdbcPassword", "-p"}, description = "JDBC Password")
	String jdbcPassword = "";
	
	public String getAccesslog() {
		return accesslog;
	}
	public void setAccesslog(String accesslog) {
		this.accesslog = accesslog;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public int getThreshold() {
		return threshold;
	}
	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}
	public String getJdbcClass() {
		return jdbcClass;
	}
	public void setJdbcClass(String jdbcClass) {
		this.jdbcClass = jdbcClass;
	}
	public String getJdbcUrl() {
		return jdbcUrl;
	}
	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}
	public String getJdbcUsername() {
		return jdbcUsername;
	}
	public void setJdbcUsername(String jdbcUsername) {
		this.jdbcUsername = jdbcUsername;
	}
	public String getJdbcPassword() {
		return jdbcPassword;
	}
	public void setJdbcPassword(String jdbcPassword) {
		this.jdbcPassword = jdbcPassword;
	}
	public boolean isHelp() {
		return help;
	}
	public void setHelp(boolean help) {
		this.help = help;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public List<String> getParameters() {
		return parameters;
	}
	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}
	public Date getEndDate() {
		
		int hourIncrement = 1;
		if (duration != null && duration.equals("daily"))
			hourIncrement = 24;
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.getStartDate());
		calendar.add(Calendar.HOUR_OF_DAY, hourIncrement);
		return calendar.getTime();
		
	}
	
	public void checkAccessLog() throws ParserException {
		File file = new File(accesslog);
		if (!file.exists() || file.isDirectory())
			throw new ParserException("File " + accesslog + " is not found or invalid");
	}
	
	public void checkDatabase() throws ParserException {
		
		try {
			Class.forName(jdbcClass);
		} catch (ClassNotFoundException e) {
			throw new ParserException(e.getMessage());
		}
		
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
		} catch (SQLException e) {
			throw new ParserException(e.getMessage());
		}
		
		if (connection == null)
			throw new ParserException("Something failed in database connection");
		
		Statement statement = null;
		try {
			statement = connection.createStatement();
			statement.executeQuery("SELECT `date`, `ip`, `request`, `status`, `user_agent` FROM access_log");
		} catch (SQLException e1) {
			throw new ParserException("Table access_log is not exists or invalid");
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				statement = null;
			}
		}
		
		try {
			statement = connection.createStatement();
			statement.executeQuery("SELECT `ip`, `requests_count`, `message`, `timestamp` FROM blocked_log");
		} catch (SQLException e1) {
			throw new ParserException("Table blocked_log is not exists or invalid");
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				statement = null;
			}
		}
		
	}
	
	
}
