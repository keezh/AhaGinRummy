package com.aha.ginrummy;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

/**
 * 引擎机制控制下的按钮控件
 * @author firzencode
 *
 */
public class AhaButton 
{
	Bitmap buttonImage1;
	Bitmap buttonImage2;
	
	boolean isPressed;
	int width;
	int height;
	int x;
	int y;
	
	public AhaButton(int _x, int _y, int _width, int _height, Resources res, int id1, int id2)
	{
		x = _x;
		y = _y;
		width = _width;
		height = _height;
		buttonImage1 = Bitmap.createScaledBitmap(
				BitmapFactory.decodeResource(res, id1), width, height, true);
		buttonImage2 =Bitmap.createScaledBitmap(
				BitmapFactory.decodeResource(res, id2), width, height, true);
		isPressed = false;
	}
	
	public void Draw(Canvas canvas, long ElapsedTime)
	{
		if (!isPressed)
		{
			canvas.drawBitmap(buttonImage1,x,y,null);
		}
		else
		{
			canvas.drawBitmap(buttonImage2, x, y, null);
		}
	}
	
	public void SetPressed(boolean _p)
	{
		isPressed = _p;
	}
	
	public boolean CheckIntersect(int _x, int _y)
	{
		return AhaGameEngine.IsPointInRect(_x, _y, x, y, width, height);
	}
	
	
}
