package com.aha.ginrummy;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

public class RushTimerHint
{
	/**
	 * �������кõĺ������ֵ�����ͼƬ
	 */
	Bitmap numberImage;

	/**
	 * ��timer��������ģ���Ҫ��ʾ��ʱ��
	 */
	int number;

	/**
	 * ��ʱ������timerMax��ʼ�ݼ���һֱ����0
	 */
	long timer;

	/**
	 * ����ʱʱ��
	 */
	long timerMax;

	/**
	 * ��������Ŀ����
	 */
	int width;

	/**
	 * ��������Ŀ��߶�
	 */
	int height;

	/**
	 * ��ǣ���ʱ���Ƿ񴥷�
	 */
	boolean isTrigger;

	/**
	 * ���������
	 */
	AhaGameEngine engine;

	/**
	 * Ŀ������x����
	 */
	int x;

	/**
	 * Ŀ������y����
	 */
	int y;

	public RushTimerHint(long _timerMax, int _width, int _height,
			int _x, int _y, Resources res, int id, AhaGameEngine _engine)
	{
		numberImage = BitmapFactory.decodeResource(res, id);
		number = 0;
		timer = 0;
		timerMax = _timerMax;
		width = _width;
		height = _height;
		isTrigger = false;
		engine = _engine;

		x = _x;
		y = _y;
	}

	public void SetTimerMax(long _t)
	{
		timerMax = _t;
	}
	public void RecountTimer()
	{
		isTrigger = false;
		timer = timerMax;
		number = (int) (timer / 1000);
	}

	public void Update(long ElapsedTime)
	{
		if (!isTrigger)
		{
			timer -= ElapsedTime;
			number = (int) (timer / 1000);
			if (number > 9)
			{
				number = 9;
			}
			if (number < 0)
			{
				number = 10;
			}
			if (timer <= 0)
			{
				isTrigger = true;
			}
		}

	}

	public void Draw(Canvas canvas, long ElapsedTime)
	{
		if (!isTrigger)
		{
			Rect src = new Rect(number * width, 0, number * width + width, height);
			Rect dst = new Rect(x, y, x + width, y + height);
			canvas.drawBitmap(numberImage, src, dst, null);
		}
		
	}
}
