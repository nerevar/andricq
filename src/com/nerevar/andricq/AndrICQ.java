package com.nerevar.andricq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import android.os.Parcel;
import android.os.Parcelable;

import com.nerevar.andricq.errors.EmptyResponseException;
import com.nerevar.andricq.errors.ServerRefuseException;
import com.nerevar.andricq.errors.UnknownServerResponseException;

/**
 * Класс отвечающий за работу клиента с сервером
 */
public class AndrICQ extends NetworkEntity implements Parcelable{

	public String login; // icq логин	
	public String buddy; // логин собеседника
	public int buddy_id; // id собеседника
	public ArrayList<User> UserList = new ArrayList<User>(); // список пользователей		
	
	/**
	 * Задаёт собеседника
	 * @param buddy_id
	 * @param buddy
	 */
	public void setBuddy(int buddy_id, String buddy) {
		this.buddy = buddy;
		this.buddy_id = buddy_id;
	}
	

	/**
	 * Ищет и возвращает пользователя по логину
	 * @param login
	 * @return
	 */
	public User findUser(String login) {
		Iterator<User> it = UserList.iterator();
		while (it.hasNext()) {
			User u = it.next();
			if ((u != null) && (u.login != null)) {
				if (u.login.equals(login)) {
					return u;
				}
			}
		}
		return null;
	}
	
	
	/**
	 * Устанавливает соединение с сервером
	 * @return - true или false в зависимости от результата соединение
	 */
	public boolean connect() 
	throws ClientProtocolException, IOException, JSONException, ServerRefuseException, EmptyResponseException, UnknownServerResponseException 
	{
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("type", "connect"));
		
		String response = postData(SERVER, nameValuePairs);
		if (response == null) {
			throw new EmptyResponseException();
		}
		
		HashMap<String, String> resp = parseJSON(response);
		
		if (resp != null) {
			if (resp.containsKey("result")) {
				if (resp.get("result").toString().equals("ok")) {
					return true;
				} else {
					throw new ServerRefuseException(resp.get("result").toString());
				}
			}
		}
		throw new UnknownServerResponseException();
	}
	
	/**
	 * Задаёт значение логина пользователя
	 * @param login
	 */
	public void setLogin(String login) {
		this.login = login;
	}
	
	/**
	 * Авторизирует пользователя
	 * @return
	 */
	public boolean auth() 
	throws ClientProtocolException, IOException, JSONException, EmptyResponseException, ServerRefuseException, UnknownServerResponseException 
	{
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("type", "auth"));
		nameValuePairs.add(new BasicNameValuePair("login", this.login));

		String response = postData(SERVER, nameValuePairs);
		if (response == null) {
			throw new EmptyResponseException();
		}
		
		HashMap<String, String> resp = parseJSON(response);
		
		if (resp != null) {
			if (resp.containsKey("result")) {
				if (resp.get("result").toString().equals("ok")) {
					return true;
				} else {
					throw new ServerRefuseException(resp.get("result").toString());
				}
			}
		}		
		throw new UnknownServerResponseException();
	}
	
	/* ============================================================================================ */
	
	public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.login);
        out.writeInt(this.buddy_id);
        out.writeString(this.buddy);
        out.writeTypedList(UserList);
    }
	
    public AndrICQ(Parcel in) {
    	this.login = in.readString();
    	this.buddy_id = in.readInt();
    	this.buddy = in.readString();
    	in.readTypedList(this.UserList, User.CREATOR);
    }
    
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<AndrICQ> CREATOR = new Parcelable.Creator<AndrICQ>() {
        public AndrICQ createFromParcel(Parcel in) {
            return new AndrICQ(in);
        }

        public AndrICQ[] newArray(int size) {
            return new AndrICQ[size];
        }
    };

	public AndrICQ(){
		
	}	
		
}
