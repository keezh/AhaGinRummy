package com.aha.ginrummy;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

public class StateHint
{
	AhaGameEngine engine;
	
	int alpha = 255;
	int alphaMax = 255;
	int alphaMin = 150;
	int speed = 0;
	int defaultSpeed = 5;
    int direction = -1;
	int mX;
	int mY = 520;
	
	Paint paint;
	
	Bitmap aidiscardBitmap;
	Bitmap aidrawcardBitmap;
	Bitmap discardBitmap;
	Bitmap drawcardBitmap;
	Bitmap dealBitmap;
	
	Bitmap youwinBitmap;
	Bitmap youloseBitmap;
	Bitmap drawhandBitmap;
	
	Bitmap currentBitmap;
	
	public StateHint(AhaGameEngine _engine, Resources res, LoadingHero loadingHero)
	{		
		paint = new Paint();

		currentBitmap = null;
		
		aidiscardBitmap = BitmapFactory.decodeResource(res, loadingHero.aidiscardhint);
		aidrawcardBitmap = BitmapFactory.decodeResource(res, loadingHero.aidrawcardhint);
		discardBitmap = BitmapFactory.decodeResource(res, loadingHero.discardhint);
		drawcardBitmap = BitmapFactory.decodeResource(res, loadingHero.drawcardhint);
		
		dealBitmap = BitmapFactory.decodeResource(res, loadingHero.dealhint);
		youwinBitmap = BitmapFactory.decodeResource(res, loadingHero.youwinhint);
		youloseBitmap = BitmapFactory.decodeResource(res, loadingHero.youlosehint);
		drawhandBitmap = BitmapFactory.decodeResource(res, loadingHero.drawhandhint);
		
		engine = _engine;
	}

	public void Update(long ElapsedTime)
	{
		if (speed != 0)
		{
			alpha += direction * speed;
		}
		
		if (alpha > alphaMax )
		{
			alpha = alphaMax;
		}
		if (alpha < alphaMin)
		{
			alpha = alphaMin;
		}
		if (alpha == alphaMax || alpha == alphaMin)
		{
			direction *= -1;
		}
		paint.setAlpha(alpha);
		
	}

	public void Draw(Canvas canvas, long ElapsedTime)
	{
		if (currentBitmap != null)
		{
			canvas.drawBitmap(currentBitmap, mX, mY, paint);
		}
	}
	
	public void SetAlpha(int a)
	{
		if (paint != null)
		{
			paint.setAlpha(a);
		}
	}
	
	public void SetSpeed(int s)
	{
		speed = s;
	}
	public void ChangeTextAndPosition()
	{
		switch (engine.gameState)
		{
			case AhaGameState.STATE_DEALING:
			{
				currentBitmap = dealBitmap;
				SetAlpha(255);
				SetSpeed(defaultSpeed);
			}
				break;
	
			case AhaGameState.STATE_DRAWCARD:
			{
				if (engine.mTurnState == AhaTurnState.TURN_PLAYER)
				{
					currentBitmap = drawcardBitmap;
					SetAlpha(255);
					SetSpeed(defaultSpeed);
				} else
				{
					currentBitmap = aidrawcardBitmap;
					SetAlpha(255);
					SetSpeed(0);
				}
			}
				break;
	
			case AhaGameState.STATE_DISCARD:
			{
				if (engine.mTurnState == AhaTurnState.TURN_PLAYER)
				{
					currentBitmap = discardBitmap;
					SetAlpha(255);
					SetSpeed(defaultSpeed);
				} else
				{
					currentBitmap = aidiscardBitmap;
					SetAlpha(255);
					SetSpeed(0);
				}
			}
				break;
	
			case AhaGameState.STATE_END:
			{
				if (engine.mWhoIsWin == AhaWinFlag.PLAYER_WIN
						|| engine.mWhoIsWin == AhaWinFlag.PLAYER_MI_WIN
						|| engine.mWhoIsWin == AhaWinFlag.OPPONENT_CHEAT_WIN)
				{
					currentBitmap = youwinBitmap;
					SetAlpha(255);
					SetSpeed(defaultSpeed);
				} else if (engine.mWhoIsWin == AhaWinFlag.OPPONENT_WIN
						|| engine.mWhoIsWin == AhaWinFlag.OPPONENT_MI_WIN
						|| engine.mWhoIsWin == AhaWinFlag.PLAYER_CHEAT_WIN)
				{
					currentBitmap = youloseBitmap;
					SetAlpha(255);
					SetSpeed(defaultSpeed);
				} else if (engine.mWhoIsWin == AhaWinFlag.DRAW)
				{
					currentBitmap = drawhandBitmap;
					SetAlpha(255);
					SetSpeed(defaultSpeed);
				}
			}
				break;
	
			default:
			{
				currentBitmap = null;
				SetAlpha(255);
				SetSpeed(defaultSpeed);
			}
				break;

		}
		if (currentBitmap != null)
		{
			mX = engine.mCanvasWidth / 2 - currentBitmap.getWidth() / 2;
		}
	}
	
}
