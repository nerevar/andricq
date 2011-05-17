package com.nerevar.andricq;

import java.util.Date;

public class Message {
	public Date date;
	public String user;
	public String message;
	boolean yours = true;
	public Message(Date d, String u, String m, boolean yours) {
		this.date = d;
		this.user = u;
		this.message = m;
		this.yours = yours;
	}
}	
