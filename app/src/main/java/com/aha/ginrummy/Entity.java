package com.aha.ginrummy;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;

// 描述一个可以在场景中绘制的场景实体
public class Entity
{
	/**
	 * 引擎的引用
	 */
	protected AhaGameEngine engine;
	
	// 用以绘制的资源
	Bitmap mBitmap;
	
	// 物体在屏幕上的坐标
	int mX;
	int mY;
	
	// 物体的宽高（目前由图片资源确定）
	int mHeight;
	int mWidth;

	boolean isShow;
	boolean isActive;
	

	/**
	 * 将场景实体缩放到指定的大小
	 * @param _width 缩放的目标宽度
	 * @param _height 缩放的目标高度
	 */
	public void SetScale(int _width, int _height)
	{
		mBitmap = Bitmap.createScaledBitmap(mBitmap,_width,_height,true);
		mHeight = mBitmap.getHeight();
		mWidth = mBitmap.getWidth();
	}
	
	// 构造函数
	public Entity(Resources res, int id, int _mX, int _mY, AhaGameEngine _engine)
	{
		mBitmap = BitmapFliter.GetScaleBitmap(res, id);
		
		mX = _mX;
		mY = _mY;
		
		mHeight = mBitmap.getHeight();
		mWidth = mBitmap.getWidth();
		
		isShow = true;
		isActive = true;
		
		// 引擎引用
		engine = _engine;
	}
	
	
	public void Update(long ElapsedTime)
	{
		
	}
	
	public void Draw(Canvas canvas, long ElapsedTime)
	{
		if (isShow)
		{
			canvas.drawBitmap(mBitmap,mX,mY,null);
		}
	}
	
	public void SetPosition(int _x, int _y)
	{
		mX = _x;
		mY = _y;
	}
	
	public void SetActive(boolean _active)
	{
		isActive = _active;
	}
	
	public void SetVisible(boolean _show)
	{
		isShow = _show;
	}
	
	public int GetHeight()
	{
		return mHeight;
	}
	
	public int GetWidth()
	{
		return mWidth;
	}
	
}
