package com.aha.ginrummy;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

// ��Ϸ���н׶ε�Activity�����ĳ�����������Activity֮��
public class SecondActivity extends Activity
{
	AhaView ahaView;
	boolean fromPause;
	AhaGameEngine mGameEngine;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gamescreen);

		// ��ȡ������ͼװ�ã�AhaView
		ahaView = (AhaView) findViewById(R.id.ahaview);

		mGameEngine = ((AhaGameEngine) getApplicationContext());
		mGameEngine.gameState = AhaGameState.STATE_READY;
		mGameEngine.sbActivity = this;
		mGameEngine.musicPlayer.start(0);
	}

	@Override
	protected void onPause()
	{
		Log.w("SecondActivity", "On Pause()");
		super.onPause();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (mGameEngine.engineMode == AhaEngineMode.NORMAL)
			{
				if (!mGameEngine.mDealHasDone)
				{
					return true;
				}
				if (mGameEngine.gameState != AhaGameState.STATE_END)
				{
					Intent intent = new Intent(this,MiddleActivity.class);
					intent.putExtra("DIALOGTYPE", GameState.PAUSE);
					startActivity(intent);
					return true;
				}
				else
					mGameEngine.ShowScoreBoardDialog();
			}
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onResume()
	{
		Log.w("SecondActivity", "On Pause()");
		super.onResume();

	}

	@Override
	protected void onDestroy()
	{
		mGameEngine.musicPlayer.stop();
		Log.w("SecondActivity","on Destory");
		super.onDestroy();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	
}
