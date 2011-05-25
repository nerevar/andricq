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

/***
 * Класс Сообщение
 */
public class Message extends NetworkEntity implements Parcelable {
	public String date_str; // дата сообщения
	public String from; // автор сообщения
	public String to; // получатель сообщения
	public String message; // текст сообщения
	boolean yours = true; // флаг - моё ли сообщение
	
	/**
	 * Конструктор
	 */
	public Message(String from, String to, String message, boolean yours) {
		this.from = from;
		this.to = to;
		this.message = message;
		this.yours = yours;
	}

	/**
	 * Конструктор
	 */
	public Message(String date, String from, String to, String message, boolean yours) {
		this.date_str = date;
		this.from = from;
		this.to = to;
		this.message = message;
		this.yours = yours;
	}	
	
	/**
	 * Отправляет сообщение на сервер
	 * @return
	 */
	public void send() 
	throws IOException, JSONException, EmptyResponseException, UnknownServerResponseException 
	{
		// формируем параметры запроса
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("type", "send_message"));
		nameValuePairs.add(new BasicNameValuePair("message", this.message));
		nameValuePairs.add(new BasicNameValuePair("from", this.from));
		nameValuePairs.add(new BasicNameValuePair("to", this.to));

		// отправляем запрос
		String response = postData(SERVER, nameValuePairs);
		if (response == null) {
			throw new EmptyResponseException();
		}
		
		HashMap<String, String> resp = parseJSON(response);
		
		// результат
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
	 * Загружает и возвращает список историю сообщений
	 * @param user - icq пользователь
	 * @param buddy - для этого пользователя 
	 */
	public static ArrayList<Message> loadMessages(String user, String buddy) 
	throws IOException, JSONException, EmptyResponseException
	{
		// формируем параметры запроса
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("type", "load_messages"));
		nameValuePairs.add(new BasicNameValuePair("buddy", buddy));
		nameValuePairs.add(new BasicNameValuePair("user", user));

		// отправляем запрос
		String response = postData(SERVER, nameValuePairs);
		if (response == null) {
			throw new EmptyResponseException();
		}
		
		ArrayList<Message> messages = new ArrayList<Message>();
		
		JSONObject json = new JSONObject(response);
		
		// получаем json массив с сообщениями
		JSONArray jsonMessages = null;
		try {
			jsonMessages = json.getJSONArray("info");
		} catch(Exception e) {
			return messages;
		}

		// в цикле формируем каждое сообщение
		for (int i=0; i<jsonMessages.length(); i++) {
			JSONObject jsonMessage = jsonMessages.getJSONObject(i);
			
			// определяем принадлежность сообщения
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