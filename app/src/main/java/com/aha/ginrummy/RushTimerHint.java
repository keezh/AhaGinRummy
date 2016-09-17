package com.aha.ginrummy;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

public class RushTimerHint
{
	/**
	 * 保存排列好的含有数字的纹理图片
	 */
	Bitmap numberImage;

	/**
	 * 由timer计算得来的，需要显示的时间
	 */
	int number;

	/**
	 * 计时器。从timerMax开始递减。一直减到0
	 */
	long timer;

	/**
	 * 倒计时时间
	 */
	long timerMax;

	/**
	 * 单个数字目标宽度
	 */
	int width;

	/**
	 * 单个数字目标高度
	 */
	int height;

	/**
	 * 标记，计时器是否触发
	 */
	boolean isTrigger;

	/**
	 * 引擎的引用
	 */
	AhaGameEngine engine;

	/**
	 * 目标区域x坐标
	 */
	int x;

	/**
	 * 目标区域y坐标
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
