package com.test.idp.exception;

public class UserException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4606542992004573358L;

	public UserException(){
		super();
	}
	
	public UserException(String message) {
        super(message);
    }
	
	 public String getMessage() {
	        return super.getMessage();
	 } 
	
	
}
