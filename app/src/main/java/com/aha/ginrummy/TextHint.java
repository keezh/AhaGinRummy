package com.aha.ginrummy;

import java.util.ArrayList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

class TextHintData
{
	public static int X = 200;
	public static int Y = 580;
	public static int X_BLANK = 0;
	public static int Y_BLANK = 20;
	public static int CARD_SPEED = 5;
	public static int ALPHA_DOWN = 20;
	public static int DURATION = 1000;
}


/**
 * 用以显示文字提示信息的装置
 *
 */
public class TextHint
{
	ArrayList<TextHintItem> text;
	Paint paint;
	AhaGameEngine engine;
	public TextHint(AhaGameEngine _engine)
	{
		text = new ArrayList<TextHintItem>();
		paint = new Paint();
		paint.setARGB(255, 255, 255, 255);
		paint.setTextSize(21);
		engine = _engine;
	}
	
	public void Update(long ElapsedTime)
	{
		int i = 0;
		while(i < text.size())
		{
			text.get(i).Update(ElapsedTime);
			if (!text.get(i).isAlive)
			{
				text.remove(i);
			}
			else 
			{
				SetPositionFromIndex(i);
				i++;
			}
		}
	}
	
	/**
	 * 特质化，根据Index设置TextHintItem的target
	 */
	public void SetPositionFromIndex(int index)
	{
		text.get(index).SetTarget(
				text.get(index).x,
				TextHintData.Y + TextHintData.Y_BLANK * index);
	}
	
	public void Draw(Canvas canvas, long ElapsedTime)
	{
		int i = 0;
		while(i < text.size())
		{
			text.get(i).Draw(canvas, ElapsedTime);
			i++;
		}
	}
	
	public void AddText(String string)
	{
		Rect rect = new Rect();
		paint.getTextBounds(string, 0,
				string.length(), rect);
		int width = rect.width();
		
		int newIndex = text.size();
		TextHintItem newItem = new TextHintItem(string, 
				engine.mCanvasWidth / 2 - width / 2, 
				TextHintData.Y + TextHintData.Y_BLANK * newIndex,
				TextHintData.CARD_SPEED,
				TextHintData.CARD_SPEED, 
				TextHintData.ALPHA_DOWN,
				TextHintData.DURATION, 
				paint);
		text.add(newItem);
	}
}

class TextHintItem
{
	// 提示文字
	String text;
	
	// 提示文字的当前x坐标
	int x;
	
	// 提示文字的当前y坐标
	int y;
	
    // 提示文字的当前alpha
	int alpha;
	
	// 提示文字的目标x坐标
	int targetX;
	
	// 提示文字的目标y坐标
	int targetY;
	
	// 提示文字的X方向移动速度
	int speedX;
	
	// 提示文字的Y方向移动速度
	int speedY;
	
	// 提示文字的alphadown速度
	int alphadown;
	
	// 持续时间（生存期）
	long duration;
	
	// 已持续时间
	long timer;
	
	// 存活标记
	boolean isAlive;
	
	// 外部paint的引用
	Paint paint;
	
	public TextHintItem(String _text, int _x, int _y, int _speedX, int _speedY, int _alphadown, long _duration, Paint _paint)
	{
		text = _text;
		x = _x;
		y = _y;
		alpha = 255;
		targetX = _x;
		targetY = _y;
		speedX = _speedX;
		speedY = _speedY;
		alphadown = _alphadown;
		duration = _duration;
		timer = 0;
		isAlive = true;
		paint = _paint;
	}
	
	public void SetTarget(int _x, int _y)
	{
		targetX = _x;
		targetY = _y;
	}
	
	public void Update(long ElapsedTime)
	{
		if (isAlive)
		{
			timer+=ElapsedTime;
			
			if (x != targetX)
			{
				if (x > targetX)
				{
					x -= speedX;
					if (x < targetX)
					{
						x = targetX;
					}
				}
				else if (x < targetX)
				{
					x += speedX;
					if (x > targetX)
					{
						x = targetX;
					}
				}
			}
			if (y != targetY)
			{
				if (y > targetY)
				{
					y -= speedY;
					if (y < targetY)
					{
						y = targetY;
					}
				}
				else if (y < targetY)
				{
					y += speedY;
					if (y > targetY)
					{
						y = targetY;
					}
				}
			}
			
			if (timer > duration)
			{
				alpha-=alphadown;
				if (alpha <= 0)
				{
					alpha = 0;
					isAlive = false;
				}
			}
		}

	}
	
	public void Draw(Canvas canvas, long ElapsedTime)
	{
		if (isAlive)
		{
			paint.setAlpha(alpha);
			canvas.drawText(text, x, y, paint);
		}
	} 
	
}
