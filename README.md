# README

## Deliverables

### (1) Java program that can be run from command line
	
    java -cp "parser.jar" com.ef.Parser --accesslog=/path/to/file --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=100
	
Run the following command to compile:
	
	mvn clean compile assembly:single
	
The jar can be found in `target` folder

To set database username and password add the parameters jdbcUsername and jdbcPassword. Example:

	java -cp "parser.jar" com.ef.Parser --accesslog=/path/to/file --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=100 --jdbcUsername=user --jdbcPassword=pass

### (2) Source Code for the Java program

### (3) MySQL schema used for the log data

Please check `parser.sql`. This file will create database `parser`, and tables `access_log` and `blocked_log`

### (4) SQL queries for SQL test

Please check `test.sql`