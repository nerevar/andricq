package com.nerevar.andricq;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
			} catch (Exception e) {
				t(e.toString());
			}
			
			t(this.getString(R.string.SuccessConnected));
			
			String login = text.getText().toString();
			icq.setLogin(login);
			
			try {
				icq.auth();
			} catch (Exception e) {
				t(e.toString());
			}
			
			t(this.getString(R.string.SuccessAuthenticated) + login);

			Intent i = new Intent(LoginActivity.this, UsersActivity.class);
			i.putExtra("icq", icq);
			startActivity(i);
			
			break;
		}
	}
	
	public void t(String text) {
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
	}
}
