package com.nerevar.andricq;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends Activity {
	AndrICQ icq = null;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);		

		Intent i = getIntent();
		icq = (AndrICQ) i.getParcelableExtra("icq");
		
		this.testChat();
	}
	
	public void testChat() {
		
		ListView mList = (ListView) findViewById(R.id.listView1);
		
		ArrayList<Message> mess = new ArrayList<ChatActivity.Message>();
		
		mess.add(new Message(new Date(), "nerevar", "сообщение", true));
		mess.add(new Message(new Date(), "user", "ответ на сообщение", false));
		mess.add(new Message(new Date(), "nerevar", "оооо чень длиннгео осооб ещен фыв фывр ыфврпа ывап ывпа фрамыфвра сообщение", true));
		mess.add(new Message(new Date(), "nerevar", "оооо чень длиннгео осооб ещен фыв фывр ыфврпа ывап ывпа фрамыфвра сообщение", true));
		mess.add(new Message(new Date(), "nerevar", "оооо чень длиннгео осооб ещен фыв фывр ыфврпа ывап ывпа фрамыфвра сообщение", true));
		mess.add(new Message(new Date(), "nerevar", "оооо чень длиннгео осооб ещен фыв фывр ыфврпа ывап ывпа фрамыфвра сообщение", true));
		mess.add(new Message(new Date(), "nerevar", "оооо чень длиннгео осооб ещен фыв фывр ыфврпа ывап ывпа фрамыфвра сообщение", true));
		mess.add(new Message(new Date(), "user", "оооо чень длиннгео осооб ещен фыв фывр ыфврпа ывап ывпа фрамыфвра сообщение", false));
		mess.add(new Message(new Date(), "nerevar", "оооо чень длиннгео осооб ещен фыв фывр ыфврпа ывап ывпа фрамыфвра сообщение", true));
		mess.add(new Message(new Date(), "nerevar", "оооо чень длиннгео осооб ещен фыв фывр ыфврпа ывап ывпа фрамыфвра сообщение", true));
		mess.add(new Message(new Date(), "nerevar", "оооо чень длиннгео осооб ещен фыв фывр ыфврпа ывап ывпа фрамыфвра сообщение", true));
		mess.add(new Message(new Date(), "nerevar", "оооо чень длиннгео осооб ещен фыв фывр ыфврпа ывап ывпа фрамыфвра сообщение", true));
		mess.add(new Message(new Date(), "nerevar", "оооо чень длиннгео осооб ещен фыв фывр ыфврпа ывап ывпа фрамыфвра сообщение", true));
		mess.add(new Message(new Date(), "nerevar", "последнее сообщение", true));

		//mList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		mList.setAdapter(new MessagesListAdapter(this, R.layout.userslist_row, mess));
		mList.setSelection(mess.size() - 1);
	}
	
	public void t(String text) {
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
	}
	
	
	public class Message {
		public Date date;
		public String user;
		public String message;
		boolean yours = true;
		public Message(Date d, String u, String m, boolean yours) {
			this.date = d;
			this.user = u;
			this.message = m;
			this.yours = yours;
		}
	}
	
	private class MessagesListAdapter extends ArrayAdapter<Message> {

        private ArrayList<Message> mData = new ArrayList<Message>();
        
        @Override
        public int getCount() {
            return mData.size();
        }
 
        @Override
        public Message getItem(int position) {
            return mData.get(position);
        }
 
        @Override
        public long getItemId(int position) {
            return position;
        }        

        public MessagesListAdapter(Context context, int textViewResourceId, ArrayList<Message> items) {
            super(context, textViewResourceId, items);    
        	this.mData = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.chat_row, null);
                }
                
                Message m = mData.get(position);
				
                if (m != null) {
					TextView tDate = (TextView) v.findViewById(R.id.tv_date);
					TextView tName = (TextView) v.findViewById(R.id.tv_user_name);
					TextView tText = (TextView) v.findViewById(R.id.tv_text);
					if (tDate != null) {
						Format formatter = new SimpleDateFormat("[HH:mm]");
						tDate.setText(formatter.format(m.date));
					}					
					if (tName != null) {
						tName.setText(m.user + ":");
						if (m.yours) {
							tName.setTextColor(Color.RED);
						} else {
							tName.setTextColor(Color.BLUE);
						}
					}					
					if (tText != null) {
						tText.setText(m.message);
					}					
				}
                return v;
    	}
	}	
	
}
