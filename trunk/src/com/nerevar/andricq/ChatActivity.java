package com.nerevar.andricq;


import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends Activity {
	AndrICQ icq = null;
	ArrayList<Message> Messages = null;
	User Buddy = null; 
		
	private MessagesListAdapter ad = null;
	
	private Handler handler = new Handler();
	private Runnable timerAction = new Runnable() {
		public void run() {
			checkNewMessages();
			handler.postDelayed(this, 5000);
		}
	};	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);		

		// получаем объект icq
		Intent i = getIntent();
		icq = (AndrICQ) i.getParcelableExtra("icq");
		
		Buddy = icq.findUser(icq.buddy);
		
		// устанавливаем имя собеседника
		TextView tTextBuddy = (TextView) findViewById(R.id.buddy_name);	
		tTextBuddy.setText(Buddy.login);		
		
		fetchNewMessages();
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
	
	public void fetchNewMessages() {
		try {
			// загрузка сообщений собеседника
			Messages = Buddy.loadMessages(icq.login);
			
			ListView mList = (ListView) findViewById(R.id.listView1);
			ad = new MessagesListAdapter(this, R.layout.userslist_row, Messages);
			mList.setAdapter(ad);
		} catch (Exception e) {
			t("Ошибка при загрузке списка сообщений: " + e);
		}
	}
	
	public void checkNewMessages() {
		int total = 0;
		try {
			total = Buddy.checkNewMessages(icq.login);
		} catch (Exception e) {
			t("Ошибка при обновлении сообщений: " + e);
		}
		
		if ((total > 0) && (total > Messages.size())) {
			fetchNewMessages();
			// TODO: sound
		}
	}
	
	public void t(String text) {
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
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
						
					tDate.setText(m.date_str);
						
					tName.setText(m.from + ":");
					if (m.yours) {
						tName.setTextColor(Color.RED);
					} else {
						tName.setTextColor(Color.BLUE);
					}

					tText.setText(m.message);
				}
                return v;
    	}
	}	
	
	/**
	 * Обработчик нажатия кнопки Отправить
	 * отправляет сообщение
	 * @param view
	 */
	public void sendMessageClick(View view) {
		EditText text = (EditText) findViewById(R.id.chat_text);
		String message = text.getText().toString();
		String from = icq.login;
		String to = icq.buddy;
		
		try {
			Message m = icq.findUser(to).sendMessage(from, message);
			Messages.add(m);
			ad.notifyDataSetChanged();
			text.setText("");
		} catch (Exception e) {
			t("Ошибка при отправке сообщения: " + e + " " + e.getStackTrace());
		}
	}
	
}
