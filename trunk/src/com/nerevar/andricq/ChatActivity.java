package com.nerevar.andricq;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class ChatActivity extends Activity {
	AndrICQ icq = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);		
		String userLogin = getIntent().getExtras().getString("userLogin");
			
		icq = new AndrICQ(userLogin);
		t(userLogin);
	}

	public void t(String text) {
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
	}
}
