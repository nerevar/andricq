package com.nerevar.andricq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcelable;

import com.nerevar.andricq.errors.EmptyResponseException;
import com.nerevar.andricq.errors.ServerRefuseException;
import com.nerevar.andricq.errors.UnknownServerResponseException;

/**
 * Класс отвечающий за работу клиента с сервером
 */
public class AndrICQ {
	public static final String SERVER = "http://192.168.1.77/api.php";

	public AndrICQ(String l){
		this.login = l;
	}
	public AndrICQ(){
	}
	
	private ArrayList<HashMap<String, String>> users;
	public void setUsers(ArrayList<HashMap<String, String>> u) {
		this.users = u;
	}
	public ArrayList<HashMap<String, String>> getUsers() {
		return this.users;
	}
	
	public String login;
		
	public String text() 
	throws ClientProtocolException, IOException, JSONException, ServerRefuseException, EmptyResponseException, UnknownServerResponseException
	{
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("type", "text"));

		return this.postData(SERVER, nameValuePairs);
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

		String response = this.postData(SERVER, nameValuePairs);
		if (response == null) {
			throw new EmptyResponseException();
		}
		
		HashMap<String, String> resp = this.parseJSON(response);
		
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
	 * Авторизирует пользователя
	 * @return
	 */
	public boolean auth(String login) 
	throws ClientProtocolException, IOException, JSONException, EmptyResponseException, ServerRefuseException, UnknownServerResponseException 
	{
		this.login = login;
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("type", "auth"));
		nameValuePairs.add(new BasicNameValuePair("login", this.login));

		String response = this.postData(SERVER, nameValuePairs);
		if (response == null) {
			throw new EmptyResponseException();
		}
		
		HashMap<String, String> resp = this.parseJSON(response);
		
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
	 * Returns array of users with their information
	 * and saves them to public class variable - users
	 */
	public ArrayList<HashMap<String, String>> reloadUsersList()
	throws ClientProtocolException, IOException, JSONException, EmptyResponseException, ServerRefuseException, UnknownServerResponseException
	{
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("type", "get_users_list"));

		String response = this.postData(SERVER, nameValuePairs);
		if (response == null) {
			throw new EmptyResponseException();
		}
		
		this.setUsers(this.parseUsersList(response));
		
		return this.getUsers();
				
	}
	
	/**
	 * Парсит входящую строку с сервера
	 * @param response - строка json
	 * @return - результирующий ассоциативный массив или null
	 */
	private HashMap<String, String> parseJSON(String response) throws JSONException {
		HashMap<String, String> out = null;
		
		JSONObject json = new JSONObject(response);
		
		out = new HashMap<String, String>();
		Iterator jk = json.keys();
		while (jk.hasNext()) {
			String key = (String)jk.next();
			out.put(key, json.getString(key));
		}

		return out;
	}
	
	private ArrayList<HashMap<String, String>> parseUsersList(String response) throws JSONException {
		ArrayList<HashMap<String, String>> users = new ArrayList<HashMap<String,String>>();
		
		JSONObject json = new JSONObject(response);
		JSONArray jsonUsers = json.getJSONArray("info");
		for (int i=0; i<jsonUsers.length(); i++) {
			JSONObject jsonUser = jsonUsers.getJSONObject(i);
			
			HashMap<String, String> user = new HashMap<String, String>();
			user.put("id", jsonUser.getString("id"));
			user.put("login", jsonUser.getString("login"));
			user.put("status", jsonUser.getString("status"));
			
			users.add(user);
		}
		
		return users;
	}

	/**
	 * Отправляет HTTP POST сообщение на удаленный сервер
	 * @param host - удаленный сервер
	 * @param nameValuePairs - POST переменные в формате имя-значение
	 */
	private String postData(String host, List<NameValuePair> nameValuePairs) 
		throws ClientProtocolException, IOException 
	{

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(host);

		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,	HTTP.UTF_8));

		HttpResponse response = httpclient.execute(httppost);

		HttpEntity resEntity = response.getEntity();

		String resp = EntityUtils.toString(resEntity, HTTP.UTF_8);
		String respEncoded = new String (resp.getBytes("Cp1251"), HTTP.UTF_8);
		
		return respEncoded;
	}
}
