package com.nerevar.andricq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EmptyStackException;
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
import org.json.JSONException;
import org.json.JSONObject;

import com.nerevar.andricq.errors.EmptyResponseException;
import com.nerevar.andricq.errors.ServerRefuseException;
import com.nerevar.andricq.errors.UnknownServerResponseException;

/**
 * Класс отвечающий за работу клиента с сервером
 */
public class AndrICQ {
	private static final String SERVER = "http://192.168.1.77/api.php";
		
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
	public boolean auth(String login) throws ClientProtocolException, IOException, JSONException, EmptyResponseException, ServerRefuseException, UnknownServerResponseException 
	{
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("type", "auth"));
		nameValuePairs.add(new BasicNameValuePair("login", login));

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
	 * Парсит входящую строку с сервера
	 * @param response - строка json
	 * @return - результирующий ассоциативный массив или null
	 */
	private HashMap<String, String> parseJSON(String response) throws JSONException {
		HashMap<String, String> out = null;
		
		JSONObject json = null;

		json = new JSONObject(response);
		
		out = new HashMap<String, String>();
		Iterator jk = json.keys();
		while (jk.hasNext()) {
			String key = (String)jk.next();
			out.put(key, json.getString(key));
		}

		
		return out;
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

		return resp;
	}
}
