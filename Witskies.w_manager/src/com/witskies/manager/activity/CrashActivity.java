package com.witskies.manager.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.witskies.w_manager.R;

public class CrashActivity extends Activity implements OnClickListener {
	private Button mSend;
	private Button mCancle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_crash);
		mSend = (Button) findViewById(R.id.activity_crash_send);
		mCancle = (Button) findViewById(R.id.activity_crash_cancle);
		mSend.setOnClickListener(this);
		mCancle.setOnClickListener(this);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_crash_send:
			finish();
			break;
		case R.id.activity_crash_cancle:
			finish();
			break;
		}

	}
}
