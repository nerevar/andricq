package com.nerevar.andricq.errors;

public class UnknownServerResponseException extends Exception{
	public UnknownServerResponseException(){
		super();
	}

	public UnknownServerResponseException(String descr){
		super(descr);
	}
}
