package com.example.indoorpositioning;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class StartingScreen extends Activity {
	private Button ok;
	// private EditText number;
	private EditText seconds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.starting_screen);
		ok = (Button) findViewById(R.id.ok_button);
		seconds = (EditText) findViewById(R.id.seconds);

		ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(StartingScreen.this, Positions.class);
				intent.putExtra("NUMBER_OF_SECONDS", seconds.getText()
						.toString());
				startActivity(intent);
				finish();
			}
		});
	}


}
