package io.bega.kduino.activities;

import android.app.Activity;
import android.os.Bundle;

import io.bega.kduino.fragments.help.UserSettingsFragment;

public class UserSettingsActivity extends Activity {

	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
	  // TODO Auto-generated method stub
	  super.onCreate(savedInstanceState);
	  
	  getFragmentManager().beginTransaction().replace(android.R.id.content,
	                new UserSettingsFragment()).commit();
	 }
	
	
}
