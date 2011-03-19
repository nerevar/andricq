package com.nerevar.andricq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class UsersActivity extends Activity {
	AndrICQ icq = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.users);

		icq = new AndrICQ();

		reloadUsersList();

		ListView uList = (ListView) findViewById(R.id.usersListView);

		uList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String text = ((TextView) view).getText().toString();
				
				Intent i = new Intent(UsersActivity.this, ChatActivity.class);
				i.putExtra("userLogin", text);
				startActivity(i);
			}
		});

	}

	public void reloadUsersList() {
		ArrayList<String> arrUsers = new ArrayList<String>();
		
		try {
			icq.reloadUsersList();
		} catch (Exception e) {
			// TODO: handle exception
			t(e.toString());
		}

		Iterator<HashMap<String, String>> it = icq.getUsers().iterator();
		while (it.hasNext()) {
			HashMap<String, String> user = it.next();
			arrUsers.add(user.get("login"));
		}
		ListView uList = (ListView) findViewById(R.id.usersListView);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, arrUsers);
		uList.setAdapter(adapter);

	}

	public void t(String text) {
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
	}

}
