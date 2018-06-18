package com.ef;

public class ParserException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public ParserException() {
		super("There is something error, please consult the developer");
	}
	
	public ParserException(String message) {
		super(message);
    }
	
}
