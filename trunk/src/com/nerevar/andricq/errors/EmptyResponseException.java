package com.nerevar.andricq.errors;

public class EmptyResponseException extends Exception{
	public EmptyResponseException(){
		super();
	}

	public EmptyResponseException(String descr){
		super(descr);
	}
}
