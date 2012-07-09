package com.mewannaplay;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Info extends Activity implements OnClickListener{
	Button ok;
	TextView link;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);
		ok=(Button)findViewById(R.id.btnok);
		link=(TextView)findViewById(R.id.txtlink);
		SpannableString content = new SpannableString((link.getText().toString()));
		content.setSpan(new UnderlineSpan(), 0, (link.getText().length()), 0);

		link.setText(content);
		ok.setOnClickListener(this);
		link.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnok:
			finish();
			break;
		case R.id.txtlink:
			
		    Uri uriUrl = Uri.parse("http://www.mewannaplay.com/");  
		    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);  
		    startActivity(launchBrowser);  
		
		}
	
		
	}

}
