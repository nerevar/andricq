package com.nerevar.andricq;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.nerevar.andricq.errors.EmptyResponseException;
import com.nerevar.andricq.errors.ServerRefuseException;
import com.nerevar.andricq.errors.UnknownServerResponseException;

public class User extends NetworkEntity implements Parcelable {
	public int id;
	public String login;
	public String status;
	
	public int total_received = 0;
	public int new_messages = 0;
	public ArrayList<Message> MessageList = new ArrayList<Message>();  		
	
	public User() {
		
	}
	
	/**
	 * Отправляет сообщение указанному пользователю
	 * @param message
	 */
	public Message sendMessage(String from, String message)
	throws IOException, JSONException, EmptyResponseException, UnknownServerResponseException
	{
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		Message m = new Message(df.format(new java.util.Date()), from, login, message, true);
		MessageList.add(m);
		m.send();
		return m;
	}
	
	public ArrayList<Message> loadMessages(String icq_login)
	throws IOException, JSONException, EmptyResponseException
	{
		return Message.loadMessages(icq_login, this.login);
	}
	
	public int checkNewMessages(String user)
	throws ClientProtocolException, IOException, JSONException, EmptyResponseException 
	{
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("type", "check_new_messages"));
		nameValuePairs.add(new BasicNameValuePair("user", user));
		nameValuePairs.add(new BasicNameValuePair("buddy", this.login));

		String response = postData(SERVER, nameValuePairs);
		if (response == null) {
			throw new EmptyResponseException();
		}

		JSONObject json = new JSONObject(response);
		
		try {
			int total = json.getJSONObject("info").getInt("total");
			return total;
		} catch(Exception e) {
			return 0;
		}
	}
	
	
	/**
	 * Returns array of users with their information
	 * and saves them to public class variable - users
	 */
	public static ArrayList<User> reloadUsersList(String icq_login)
	throws ClientProtocolException, IOException, JSONException, EmptyResponseException, ServerRefuseException, UnknownServerResponseException
	{
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("type", "get_users_list"));
		nameValuePairs.add(new BasicNameValuePair("user", icq_login));

		String response = postData(SERVER, nameValuePairs);
		if (response == null) {
			throw new EmptyResponseException();
		}
		
		return parseUsersList(response);		
	}
	

	
	/**
	 * Парсит список пользователей в онлайне
	 * @param response
	 * @return
	 */
	private static ArrayList<User> parseUsersList(String response) throws JSONException {
		ArrayList<User> users = new ArrayList<User>();
		
		JSONObject json = new JSONObject(response);
		JSONArray jsonUsers = null;
		
		try {
			jsonUsers = json.getJSONArray("info");
		} catch(Exception e) {
			return users;
		}
		
		for (int i=0; i<jsonUsers.length(); i++) {
			JSONObject jsonUser = jsonUsers.getJSONObject(i);
			
			User user = new User();
			user.id = jsonUser.getInt("id");
			user.login = jsonUser.getString("login");
			user.status = jsonUser.getString("status");
			try {
				user.total_received = jsonUser.getInt("total_received");
			} catch(Exception e) {
				user.total_received = 0;
			}
			
			users.add(user);
		}
		
		return users;
	}	
	
	
	/* ============================================================================================ */	
	
	public User(Parcel in) {
		readFromParcel(in);
	}
	
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(this.id);
		out.writeString(this.login);
		out.writeString(this.status);
		out.writeInt(this.new_messages);
		out.writeInt(this.total_received);
		out.writeTypedList(this.MessageList);
	}
	
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };		
    
    private void readFromParcel(Parcel in) {
    	this.id = in.readInt();
    	this.login = in.readString();
    	this.status = in.readString();
    	this.new_messages = in.readInt();
    	this.total_received = in.readInt();
    	in.readTypedList(this.MessageList, Message.CREATOR);
    }
}	