package com.ef;

import java.io.IOException;

import com.beust.jcommander.JCommander;
import com.ef.configuration.Configuration;
import com.ef.service.ParserService;
import com.ef.service.ParserServiceImpl;

public class Parser {
	
	public static void main(String[] args) {
		
		Configuration configuration = new Configuration();
		JCommander.newBuilder()
			.addObject(configuration)
			.build()
			.parse(args);
		
		Parser.check(configuration);
		Parser.run(configuration);
		
	}
	
	public static void check(Configuration configuration) {
			
	}
	
	public static void run(Configuration configuration) {
		try {
			configuration.checkAccessLog();
			System.out.println("Access log file is found...");
			configuration.checkDatabase();
			System.out.println("Database is OK...");
			System.out.println("Running...");
			ParserService service = new ParserServiceImpl(configuration);
			System.out.println("Please wait...");
			service.run();
			System.out.println("Done!");
		} catch (ParserException | IOException e) {
			System.out.println(e.getMessage());
		}
	}

}
