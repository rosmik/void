package com.letsbecreative.yoin;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class UpdatePersonalData extends Activity {

	
	public Card personalCard = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_personal_data);

		final EditText firstName = (EditText) findViewById(R.id.first_name);
		final EditText lastName = (EditText) findViewById(R.id.last_name);
		final EditText mail = (EditText) findViewById(R.id.mail);
		final EditText phone = (EditText) findViewById(R.id.phone);
		final EditText linkedin = (EditText) findViewById(R.id.linkedin);

		final Button saveButton = (Button) findViewById(R.id.save_button);
		saveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//TODO Handle when you should be able to save (all req. fields filled)
				personalCard = new Card(firstName.getText().toString(), lastName.getText().toString());
				personalCard.addEntry("mail", mail.getText().toString());
				personalCard.addEntry("phone", phone.getText().toString());
				personalCard.addEntry("linkedin", linkedin.getText().toString());
				Log.d("JSONData", personalCard.toString());
				Toast.makeText(getApplicationContext(), personalCard.toString(), Toast.LENGTH_LONG).show();
				Intent personalCardIntent = new Intent(UpdatePersonalData.this, MainActivity.class);
				personalCardIntent.putExtra("card", personalCard);
				startActivity(personalCardIntent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.update_personal_data, menu);
		return true;
	}

	
}
