package jieehd.villain.updater;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class about extends Activity {
	TextView tvVersion;
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		tvVersion = (TextView) findViewById(R.id.tvAbout);

		tvVersion.setText("1.0.0");

		
	}
}
