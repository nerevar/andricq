package com.nerevar.andricq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.nerevar.andricq.errors.EmptyResponseException;
import com.nerevar.andricq.errors.UnknownServerResponseException;

public class Message extends NetworkEntity implements Parcelable {
	public String date_str;
	public String from;
	public String to;
	public String message;
	boolean yours = true;
	
	public Message(String from, String to, String message, boolean yours) {
		this.from = from;
		this.to = to;
		this.message = message;
		this.yours = yours;
	}

	public Message(String date, String from, String to, String message, boolean yours) {
		this.date_str = date;
		this.from = from;
		this.to = to;
		this.message = message;
		this.yours = yours;
	}	
	
	
	/**
	 * Отправляет сообщение на сервер
	 * @param message - текст сообщения
	 * @param from - имя пользователя От кого
	 * @param to - имя пользователя Кому
	 * @return
	 */
	public void send() 
	throws IOException, JSONException, EmptyResponseException, UnknownServerResponseException 
	{
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("type", "send_message"));
		nameValuePairs.add(new BasicNameValuePair("message", this.message));
		nameValuePairs.add(new BasicNameValuePair("from", this.from));
		nameValuePairs.add(new BasicNameValuePair("to", this.to));

		String response = postData(SERVER, nameValuePairs);
		if (response == null) {
			throw new EmptyResponseException();
		}
		
		HashMap<String, String> resp = parseJSON(response);
		
		if (resp != null) {
			if (resp.containsKey("result")) {
				if (resp.get("result").toString().equals("ok")) {
					return;
				}
			}
		}
		
		throw new UnknownServerResponseException();
	}	
	
	
	
	/**
	 * Загружает список сообщений
	 */
	public static ArrayList<Message> loadMessages(String user, String buddy) 
	throws IOException, JSONException, EmptyResponseException
	{
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("type", "load_messages"));
		nameValuePairs.add(new BasicNameValuePair("buddy", buddy));
		nameValuePairs.add(new BasicNameValuePair("user", user));

		String response = postData(SERVER, nameValuePairs);
		if (response == null) {
			throw new EmptyResponseException();
		}
		
		ArrayList<Message> messages = new ArrayList<Message>();
		
		JSONObject json = new JSONObject(response);
		
		JSONArray jsonMessages = null;
		try {
			jsonMessages = json.getJSONArray("info");
		} catch(Exception e) {
			return messages;
		}

		for (int i=0; i<jsonMessages.length(); i++) {
			JSONObject jsonMessage = jsonMessages.getJSONObject(i);
			
			boolean isBelongToMe = false;
			if (jsonMessage.getString("from").equals(user)) {
				isBelongToMe = true;
			}
			Message m = new Message(jsonMessage.getString("date_str"),
									jsonMessage.getString("from"),
									jsonMessage.getString("to"),
									jsonMessage.getString("message"), 
									isBelongToMe);
						
			messages.add(m);
		}
		
		return messages;
	}	
	
	
	
	/* ============================================================================================ */
	
	public Message(Parcel in) {
		readFromParcel(in);
	}
	
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(this.date_str);
		out.writeString(this.from);
		out.writeString(this.to);
		out.writeString(this.message);
		out.writeInt(this.yours ? 1 : 0);
	}
	
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };		
    
    private void readFromParcel(Parcel in) {
    	this.date_str = in.readString();
    	this.from = in.readString();
    	this.to = in.readString();
    	this.message = in.readString();
		this.yours = in.readInt() > 0 ? true : false;	    	
    }
	
}	