package com.nerevar.andricq.errors;

public class ServerRefuseException extends Exception {
	public ServerRefuseException() {
		super();
	}
	
	public ServerRefuseException(String descr) {
		super(descr);
	}
}
