package com.nerevar.andricq;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class UsersActivity extends Activity{	
	private String lv_arr[]={"Android","iPhone","BlackBerry","AndroidPeople"};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.users);
		
		ListView uList=(ListView)findViewById(R.id.usersListView);
		// By using setAdpater method in listview we an add string array in list.
		uList.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , lv_arr));		
	}	
}
