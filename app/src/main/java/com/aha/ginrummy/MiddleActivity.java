package com.aha.ginrummy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MiddleActivity extends Activity
{
	ScoreBoardDialog scoreBoard = null;
	PauseDialog pauseDialog = null;
	AhaGameEngine engine;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		engine = (AhaGameEngine) getApplication();
		engine.isUpdateLocked = true;
		
		scoreBoard = new ScoreBoardDialog(this, R.style.nullframedialog,
				engine.scoreRecord, engine);
		pauseDialog = new PauseDialog(this, R.style.dialog);
		scoreBoard.setOwner(this);
		pauseDialog.setOwner(this);
		scoreBoard.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
//				MiddleActivity.this.finish();
				scoreBoard.dismiss();
				
				switch (v.getId())
				{
				case R.id.scoreboard_btn_continue:
				case R.id.scoreboard_btn_examine:
					break;
				case R.id.scoreboard_btn_newgame:
					engine.gameState = AhaGameState.STATE_READY;
					break;
				case R.id.scoreboard_btn_nexthand:
					engine.gameState = AhaGameState.STATE_INITED_ROUND;
					engine.currentHand++;
					break;
				case R.id.scoreboard_btn_returnhome:
					engine.sbActivity.finish();
					break;
				default:
					break;
				}
			}
		});

		pauseDialog.setOnTouchListener(new View.OnTouchListener()
		{
			
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				
				int actionId = event.getAction();
				switch (v.getId())
				{
				case R.id.pause_iv_pauseresume:
					if (actionId == MotionEvent.ACTION_DOWN)
					{
						((ImageView) v).setImageDrawable(getResources()
								.getDrawable(R.drawable.resumeselectedpng));
					}
					if (actionId == MotionEvent.ACTION_UP) // 松开按钮
					{
						((ImageView) v).setImageDrawable(getResources()
								.getDrawable(R.drawable.resume));
						/*
						 * 如果松开按钮时,指针坐标还在按钮范围内,则判定为 按钮生效
						 */
						if (isPointerInView(v, (int) event.getRawX(),
								(int) event.getRawY()))
						{
							pauseDialog.dismiss();
						}
					}
					break;	
					case R.id.pause_iv_pausenewgame:
						if (actionId == MotionEvent.ACTION_DOWN)
						{
							((ImageView) v).setImageDrawable(getResources()
									.getDrawable(R.drawable.newgameselectedpng));
						}
						if (actionId == MotionEvent.ACTION_UP) // 松开按钮
						{
							((ImageView) v).setImageDrawable(getResources()
									.getDrawable(R.drawable.newgame));
							/*
							 * 如果松开按钮时,指针坐标还在按钮范围内,则判定为 按钮生效
							 */
							if (isPointerInView(v, (int) event.getRawX(),
									(int) event.getRawY()))
							{
								engine.gameState = AhaGameState.STATE_READY;
								pauseDialog.dismiss();
							}
						}
						break;	
					case R.id.pause_iv_pausehome:
						if (actionId == MotionEvent.ACTION_DOWN)
						{
							((ImageView) v).setImageDrawable(getResources()
									.getDrawable(R.drawable.menuselectedpng));
						}
						if (actionId == MotionEvent.ACTION_UP) // 松开按钮
						{
							((ImageView) v).setImageDrawable(getResources()
									.getDrawable(R.drawable.menu));
							/*
							 * 如果松开按钮时,指针坐标还在按钮范围内,则判定为 按钮生效
							 */
							if (isPointerInView(v, (int) event.getRawX(),
									(int) event.getRawY()))
							{
								engine.sbActivity.finish();
								pauseDialog.dismiss();
							}
						}
						break;	
				default:
					return false;
				}
				
				return true;
			}
		});
		
		engine.scoreRecord.setParentDialog(scoreBoard);
		
		if (getIntent().getExtras().getInt("DIALOGTYPE") == GameState.PAUSE)
		{
			pauseDialog.show();
		} 
		else if (getIntent().getExtras().getInt("DIALOGTYPE") == GameState.GAMING)
		{
			scoreBoard.show(GameState.GAMING);
		} 
		else if (getIntent().getExtras().getInt("DIALOGTYPE") == GameState.END_OF_GAME)
		{
			engine.scoreRecord.AddRecord(engine.scoreItem);
			scoreBoard.show(GameState.END_OF_GAME);
		} 
		else if (getIntent().getExtras().getInt("DIALOGTYPE") == GameState.END_OF_HAND)
		{
			engine.scoreRecord.AddRecord(engine.scoreItem);
			scoreBoard.show(GameState.END_OF_HAND);
		} else if (getIntent().getExtras().getInt("DIALOGTYPE") == GameState.END_OF_GAME_ADDED)
		{
			scoreBoard.show(GameState.END_OF_GAME);
		} 
		else if (getIntent().getExtras().getInt("DIALOGTYPE") == GameState.END_OF_HAND_ADDED)
		{
			scoreBoard.show(GameState.END_OF_HAND);
		} 
		else if (getIntent().getExtras().getInt("DIALOGTYPE") == GameState.END_OF_STAGE)
		{
			engine.scoreRecord.AddRecord(engine.scoreItem);
			scoreBoard.show(GameState.END_OF_STAGE);
		} 
	}

	@Override
	protected void onDestroy()
	{
		// scoreBoard.dismiss();
		// pauseDialog.dismiss();
		engine.isUpdateLocked = false;
		engine.isScoreDialogShow = false;
		super.onDestroy();
	}

	private boolean isPointerInView(View v, int x, int y)
	{
		int rect[] = new int[2];
		v.getLocationOnScreen(rect); // 获取屏幕坐标系的坐标值

		return (new Rect(rect[0], rect[1], rect[0] + v.getWidth(), rect[1]
				+ v.getHeight()).contains(x, y));
	}
}
