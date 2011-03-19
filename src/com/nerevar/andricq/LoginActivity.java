package com.nerevar.andricq;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.nerevar.andricq.errors.EmptyResponseException;
import com.nerevar.andricq.errors.ServerRefuseException;
import com.nerevar.andricq.errors.UnknownServerResponseException;

/**
 * First Login activity, you can enter your login and
 * press button to connect to AndrICQ server  
 */
public class LoginActivity extends Activity {
	
	private AndrICQ icq;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		this.icq = new AndrICQ();
	}

	/**
	 * Connects to server, sends requests to get server info and avaibility
	 * and then sends user authorization info to server
	 * @param view
	 */
	public void connectButtonClick(View view) {

		EditText text = (EditText) findViewById(R.id.editText1);

		switch (view.getId()) {
		case R.id.button1:
			if (text.getText().length() == 0) {
				t(this.getString(R.string.ErrorEmptyLogin));
			}

			try {
				icq.connect();
			} catch(JSONException e) {
				t(this.getString(R.string.ErrorJSONException));
				return;
			} catch(ClientProtocolException e) {
				t(this.getString(R.string.ErrorClientProtocolException));
				return;
			} catch(IOException e) {
				t(this.getString(R.string.ErrorIOException));
				return;
			} catch(ServerRefuseException e) {
				t(this.getString(R.string.ErrorServerRefuseException));
				return;
			} catch (EmptyResponseException e) {
				t(this.getString(R.string.ErrorEmptyResponseException));
				return;
			} catch (UnknownServerResponseException e) {
				t(this.getString(R.string.ErrorUnknownServerResponseException));
				return;
			}
			
			t(this.getString(R.string.SuccessConnected));
			
			String login = text.getText().toString();
			
			try {
				icq.auth(login);
			} catch(JSONException e) {
				t(this.getString(R.string.ErrorJSONException));
				return;
			} catch(ClientProtocolException e) {
				t(this.getString(R.string.ErrorClientProtocolException));
				return;
			} catch(IOException e) {
				t(this.getString(R.string.ErrorIOException));
				return;
			} catch(EmptyResponseException e) {
				t(this.getString(R.string.ErrorEmptyResponseException));
				return;
			} catch(ServerRefuseException e) {
				t(this.getString(R.string.ErrorServerRefuseException));
				return;
			} catch(UnknownServerResponseException e) {
				t(this.getString(R.string.ErrorUnknownServerResponseException));
				return;
			}
			
			t(this.getString(R.string.SuccessAuthenticated) + login);

			Intent i = new Intent(LoginActivity.this, UsersActivity.class);
			//i.putExtra("icq", "asda");
			startActivity(i);
			
			break;
		}
	}
	
	public void t(String text) {
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
	}
}
