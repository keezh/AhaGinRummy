package com.aha.ginrummy;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.widget.ProgressBar;

public class LoadingDialog extends Dialog {

	private ProgressBar pb=null;
	private int progress=0;
	private int max=100;
	
	public LoadingDialog(Context context, int theme) {
		super(context, theme);
		setContentView(R.layout.loading);
		pb=(ProgressBar)findViewById(R.id.progressBar);
		
	}
	
	public void setMax(int max)
	{
		this.max=max;
		pb.setMax(max);
	}
	
	public void setProgress(int progress)
	{
		this.progress=progress;
		pb.setProgress(progress);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			return true;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
}
