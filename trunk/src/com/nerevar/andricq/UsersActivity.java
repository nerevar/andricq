package com.nerevar.andricq;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.users);

		Intent i = getIntent();
		icq = (AndrICQ) i.getParcelableExtra("icq");

		reloadUsersList();

		ListView uList = (ListView) findViewById(R.id.usersListView);
		final ListView lv1 = uList;
		
		uList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				HashMap<String, String> u = (HashMap<String, String>) lv1.getItemAtPosition(position);

				icq.setBuddy( Integer.parseInt(u.get("id")), u.get("login"));
				
				Intent i = new Intent(UsersActivity.this, ChatActivity.class);
				i.putExtra("icq", icq);
				startActivity(i);
			}
		});

	}
	
	public void onResume(Bundle savedInstanceState) {
		reloadUsersList();
	}
	

	public void reloadUsersList() {
		try {
			// get users list from server
			icq.reloadUsersList();
		} catch (Exception e) {
			t(e.toString());
		}

		ListView uList = (ListView) findViewById(R.id.usersListView);
		
		uList.setAdapter(new UsersListAdapter(this, R.layout.userslist_row, icq.users.getUsers()));

	}

	public void t(String text) {
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
	}

	
	private class UsersListAdapter extends ArrayAdapter<HashMap<String, String>> {

        private ArrayList<HashMap<String, String>> mData = new ArrayList<HashMap<String, String>>();
        
        @Override
        public int getCount() {
            return mData.size();
        }
 
        @Override
        public HashMap<String, String> getItem(int position) {
            return mData.get(position);
        }
 
        @Override
        public long getItemId(int position) {
            return position;
        }        

        public UsersListAdapter(Context context, int textViewResourceId, ArrayList<HashMap<String, String>> items) {
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
                
                HashMap<String, String> u = mData.get(position);
				
                if (u != null) {
					TextView tt = (TextView) v.findViewById(R.id.toptext);
					TextView bt = (TextView) v.findViewById(R.id.bottomtext);
					ImageView is = (ImageView) v.findViewById(R.id.img_status);
					if (tt != null) {
						tt.setText(u.get("login"));
					}
					if (bt != null) {
						bt.setText("status: " + u.get("status"));
					}
					if (is != null) {
						if (u.get("status").equals("online")) {
							is.setImageResource(R.drawable.status_online);
						}
						if (u.get("status").equals("offline")) {
							is.setImageResource(R.drawable.status_offline);
						}						
						if (u.get("status").equals("afk")) {
							is.setImageResource(R.drawable.status_afk);
						}						
						
					}					
				}
                return v;
    	}
	}
	
}
