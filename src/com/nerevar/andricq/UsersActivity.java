package com.nerevar.andricq;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class UsersActivity extends Activity {
	AndrICQ icq = null;
	ListView uList = null;
		
	private Handler handler = new Handler();
	private Runnable timerAction = new Runnable() {
		public void run() {
			reloadUsersList();
			handler.postDelayed(this, 5000);
		}
	};		
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.users);

		Intent i = getIntent();
		icq = (AndrICQ) i.getParcelableExtra("icq");

		reloadUsersList();

		uList = (ListView) findViewById(R.id.usersListView);
		
		uList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				User u = (User) uList.getItemAtPosition(position);

				icq.setBuddy( u.id, u.login);
				
				icq.findUser(u.login).new_messages = 0;
				
				Intent i = new Intent(UsersActivity.this, ChatActivity.class);
				i.putExtra("icq", icq);
				startActivity(i);
			}
		});
		
	}
	
	@Override
	public void onStart() {
		super.onStart();
		// Запуск таймера
		handler.postDelayed(timerAction, 5000);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		// Стоп таймера
		handler.removeCallbacks(timerAction);
	}
	

	public void reloadUsersList() {
		ArrayList<User> userList = new ArrayList<User>();
		try {
			// get users list from server
			 userList = User.reloadUsersList(icq.login);
		} catch (Exception e) {
			t(e.toString());
		}
		
		if (icq.UserList.size() > 0) {
			// Проверка новых сообщений
			Iterator<User> it = userList.iterator();
			while (it.hasNext()) {
				User u = it.next();
				User fu = icq.findUser(u.login);
				
				if (fu != null) {
					if (u.total_received > fu.total_received) {
						u.new_messages = u.total_received - fu.total_received; 
					} else {
						u.new_messages = fu.new_messages;
					}
				}
			}
		}
		
		icq.UserList = userList;
		
		ListView uList = (ListView) findViewById(R.id.usersListView);
		uList.setAdapter(new UsersListAdapter(this, R.layout.userslist_row, this.icq.UserList));

	}

	public void t(String text) {
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
	}

	
	private class UsersListAdapter extends ArrayAdapter<User> {

        private ArrayList<User> mData = new ArrayList<User>();
        
        @Override
        public int getCount() {
            return mData.size();
        }
 
        @Override
        public User getItem(int position) {
            return mData.get(position);
        }
 
        @Override
        public long getItemId(int position) {
            return position;
        }        

        public UsersListAdapter(Context context, int textViewResourceId, ArrayList<User> items) {
            super(context, textViewResourceId, items);    
        	this.mData = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.userslist_row, null);
                }
                
                User u = mData.get(position);
				
                if (u != null) {
					TextView tt = (TextView) v.findViewById(R.id.toptext);
					TextView bt = (TextView) v.findViewById(R.id.bottomtext);
					ImageView is = (ImageView) v.findViewById(R.id.img_status);
					
					
					if (u.new_messages > 0) {
						tt.setText(u.login + " (+" + u.new_messages + ")");
					} else {
						tt.setText(u.login);
					}
					// TODO: new messages
				
					bt.setText("status: " + u.status);
				
					if (u.status.equals("online")) {
						is.setImageResource(R.drawable.status_online);
					} else if (u.status.equals("offline")) {
						is.setImageResource(R.drawable.status_offline);
					} else if (u.status.equals("afk")) {
						is.setImageResource(R.drawable.status_afk);
					}						
						
										
				}
                return v;
    	}
	}
	
}
