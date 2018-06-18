package com.ef.service;

import java.io.IOException;

import com.ef.ParserException;
import com.ef.configuration.Configuration;

public interface ParserService {
	
	public void init(Configuration configuration);
	public void run() throws ParserException, IOException;
	
}
