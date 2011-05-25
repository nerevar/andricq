package com.nerevar.andricq;


import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends Activity {
	AndrICQ icq = null; // объект icq
	ArrayList<Message> Messages = null; // список сообщения
	User Buddy = null; // собеседник
		
	private MessagesListAdapter ad = null;
	private ListView mList = null;
	
	// Проверка новых сообщений каждые 3 секунды
	private Handler handler = new Handler();
	private Runnable timerAction = new Runnable() {
		public void run() {
			checkNewMessages();
			handler.postDelayed(this, 3000);
		}
	};	
	
	// Отправка сообщений по enter при вводе текста
	TextView.OnEditorActionListener onSendListener = new TextView.OnEditorActionListener(){
		public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
			if(actionId == EditorInfo.IME_NULL){
				sendMessageClick(exampleView);
			}
			return true;
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
		
		mList = (ListView) findViewById(R.id.listView1);
		
		// устанавливаем имя собеседника
		TextView tTextBuddy = (TextView) findViewById(R.id.buddy_name);	
		tTextBuddy.setText(Buddy.login);		
		
		fetchNewMessages();
		
		// задаём отправку сообщений по enter
		EditText text = (EditText) findViewById(R.id.chat_text);
		text.setOnEditorActionListener(onSendListener);
	}

	@Override
	public void onStart() {
		super.onStart();
		// Запуск таймера
		handler.postDelayed(timerAction, 3000);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		// Стоп таймера
		handler.removeCallbacks(timerAction);
	}	
	
	/**
	 * Получаем сообщения с сервера
	 */
	public void fetchNewMessages() {
		try {
			// загрузка сообщений собеседника
			Messages = Buddy.loadMessages(icq.login);
			
			ad = new MessagesListAdapter(this, R.layout.userslist_row, Messages);
			mList.setAdapter(ad);
			mList.setSelection(Messages.size() - 1);
		} catch (Exception e) {
			t("Ошибка при загрузке списка сообщений: " + e);
		}
	}
	
	/**
	 * Проверяет новые сообщения
	 */
	public void checkNewMessages() {
		int unread = Buddy.checkNewMessages(icq.login);
		
		if (unread > 0) {
			fetchNewMessages();
			// TODO: sound
		}
	}
	
	public void t(String text) {
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * Адаптер для списка сообщений
	 */
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
		
		// отправляем сообщение
		if ((message != null) && (message.length() > 0)) {		
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
	
}
