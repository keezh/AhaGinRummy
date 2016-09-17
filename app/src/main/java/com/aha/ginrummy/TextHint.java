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
 * ������ʾ������ʾ��Ϣ��װ��
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
	 * ���ʻ�������Index����TextHintItem��target
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
	// ��ʾ����
	String text;
	
	// ��ʾ���ֵĵ�ǰx����
	int x;
	
	// ��ʾ���ֵĵ�ǰy����
	int y;
	
    // ��ʾ���ֵĵ�ǰalpha
	int alpha;
	
	// ��ʾ���ֵ�Ŀ��x����
	int targetX;
	
	// ��ʾ���ֵ�Ŀ��y����
	int targetY;
	
	// ��ʾ���ֵ�X�����ƶ��ٶ�
	int speedX;
	
	// ��ʾ���ֵ�Y�����ƶ��ٶ�
	int speedY;
	
	// ��ʾ���ֵ�alphadown�ٶ�
	int alphadown;
	
	// ����ʱ�䣨�����ڣ�
	long duration;
	
	// �ѳ���ʱ��
	long timer;
	
	// �����
	boolean isAlive;
	
	// �ⲿpaint������
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
