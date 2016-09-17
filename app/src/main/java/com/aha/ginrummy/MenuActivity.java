package com.aha.ginrummy;

import android.R.integer;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends Activity
{
	/** Called when the activity is first created. */
	private ImageView startImageView = null;
	private ImageView optionsImageView = null;
	private ImageView helpImageView = null;
	private ImageView exitImageView = null;
	private String newName = null;
	
	public LoadingDialog loadingDialog;
	public LoadingThread loadingThread;
	/*
	 * 启动RenameActivity时,StartForResult的标识
	 */
	private final int RENAME = 0x0001;

	/**
	 * 引擎的引用
	 */
	public AhaGameEngine mGameEngine;

	class LoadingThread extends Thread
	{
		AhaGameEngine engine;
		LoadingDialog dialog;
		
		public LoadingThread(LoadingDialog _dialog, AhaGameEngine _engine)
		{
			engine = _engine;
			dialog = _dialog;
		}

		@Override
		public void run()
		{
			engine.LoadData(dialog);
			//engine.musicPlayer.start(0);
			dialog.dismiss();
			
			/*
			 * 启动Activity,并从中获得结果
			 */
			//Intent intent = new Intent(MenuActivity.this, RenameActivity.class);
			//startActivityForResult(intent, RENAME);
			// PauseDialog dialog = new PauseDialog(this,R.style.dialog);
			// dialog.show();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// 获得引擎引用，进行初始化操作
		mGameEngine = ((AhaGameEngine) getApplicationContext());

		loadingDialog = new LoadingDialog(this,
				R.style.Dialog_Fullscreen);
		loadingThread = new LoadingThread(loadingDialog, mGameEngine);
		
		loadingThread.start();
		loadingDialog.show();

		setContentView(R.layout.menu);
		
		 /*
		 * 获取当前屏幕信息
		 */
		
		 WindowManager m = this.getWindow().getWindowManager();
		 Display d = m.getDefaultDisplay();
		
		 LinearLayout background = (LinearLayout)
		 findViewById(R.id.innermenubackground);
		 ImageView menuLogo = (ImageView) findViewById(R.id.ivlogo);
		 /*
		 * 根据比例设置背景(木板)的padding
		 */
		 background.setPadding(0, 0, 0, d.getHeight() * 3 / 2 / 16);
		
		 startImageView = (ImageView) findViewById(R.id.ivstart);
		 optionsImageView = (ImageView) findViewById(R.id.ivoptions);
		 helpImageView = (ImageView) findViewById(R.id.ivhelp);
		 exitImageView = (ImageView) findViewById(R.id.ivexit);
		
		 resources = getResources();
		
		 startImageView.setOnTouchListener(ivTouchListener);
		 optionsImageView.setOnTouchListener(ivTouchListener);
		 helpImageView.setOnTouchListener(ivTouchListener);
		 exitImageView.setOnTouchListener(ivTouchListener);
		
		 /*
		 * 启动Activity,并从中获得结果
		 */
//		 Intent intent = new Intent(this, RenameActivity.class);
//		 startActivityForResult(intent, RENAME);
		 // PauseDialog dialog = new PauseDialog(this,R.style.dialog);
		 // dialog.show();
	}

	Resources resources;
	OnTouchListener ivTouchListener = new OnTouchListener()
	{
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			int actionId = event.getAction();
			switch (v.getId())
			// 判断是哪个组件获得了事件
			{
			case R.id.ivstart: // 开始按钮部分
				if (actionId == MotionEvent.ACTION_DOWN) // 按下按钮
				{
					((ImageView) v).setImageDrawable(resources
							.getDrawable(R.drawable.startselectedpng));
				}
				if (actionId == MotionEvent.ACTION_UP) // 松开按钮
				{
					((ImageView) v).setImageDrawable(resources
							.getDrawable(R.drawable.startpng));
					/*
					 * 如果松开按钮时,指针坐标还在按钮范围内,则判定为 按钮生效
					 */
					if (isPointerInView(v, (int) event.getRawX(),
							(int) event.getRawY()))
					{
						Intent intent = new Intent(MenuActivity.this,
								SecondActivity.class);
						mGameEngine.engineMode = AhaEngineMode.MODESELECT;
						startActivity(intent);
					}
				}
				break;
			case R.id.ivoptions: // 选项按钮部分
				if (actionId == MotionEvent.ACTION_DOWN)
				{
					((ImageView) v).setImageDrawable(resources
							.getDrawable(R.drawable.optionsselectedpng));
				}
				if (actionId == MotionEvent.ACTION_UP)
				{
					((ImageView) v).setImageDrawable(resources
							.getDrawable(R.drawable.optionspng));
					if (isPointerInView(v, (int) event.getRawX(),
							(int) event.getRawY()))
					{
						Intent intent = new Intent(MenuActivity.this,
								OptionsActivity.class);
						startActivity(intent);
					}
				}
				break;
			case R.id.ivhelp: // 帮助按钮部分
				if (actionId == MotionEvent.ACTION_DOWN)
				{
					((ImageView) v).setImageDrawable(resources
							.getDrawable(R.drawable.helpselectedpng));
				}
				if (actionId == MotionEvent.ACTION_UP)
				{
					((ImageView) v).setImageDrawable(resources
							.getDrawable(R.drawable.helppng));
					if (isPointerInView(v, (int) event.getRawX(),
							(int) event.getRawY()))
					{
						Intent intent = new Intent(MenuActivity.this,HelpActivity.class);
						startActivity(intent);						
					}
				}
				break;
			case R.id.ivexit: // 退出
				if (actionId == MotionEvent.ACTION_DOWN)
				{
					((ImageView) v).setImageDrawable(resources
							.getDrawable(R.drawable.exitselectedpng));
				}
				if (actionId == MotionEvent.ACTION_UP)
				{
					((ImageView) v).setImageDrawable(resources
							.getDrawable(R.drawable.exitpng));
					if (isPointerInView(v, (int) event.getRawX(),
							(int) event.getRawY()))
					{
						MenuActivity.this.finish(); // 退出
					}
				}
				break;
			default:
				return false;
			}

			return true;
		}
	};

	/*
	 * 判断按钮在组件的范围内的辅助函数
	 */
	private boolean isPointerInView(View v, int x, int y)
	{
		int rect[] = new int[2];
		v.getLocationOnScreen(rect); // 获取屏幕坐标系的坐标值

		return (new Rect(rect[0], rect[1], rect[0] + v.getWidth(), rect[1]
				+ v.getHeight()).contains(x, y));
	}

	/*
	 * Activity用来响应 startActivityForResult函数产生的消息的消息响应函数
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode)
		// 按照Request的Flag来判断Activity
		{
		case RENAME: // 按照Result的Flag来判断返回的内容
			if (resultCode == RenameActivity.RENAMERESULT)
			{
				Bundle result = data.getExtras();
				String rename = result.getString("NAME");
				Toast.makeText(MenuActivity.this, rename + "你孽畜啊，名字这么轻易就改了",
						Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
	}

}