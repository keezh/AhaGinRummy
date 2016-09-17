package com.aha.ginrummy;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;

// ����һ�������ڳ����л��Ƶĳ���ʵ��
public class Entity
{
	/**
	 * ���������
	 */
	protected AhaGameEngine engine;
	
	// ���Ի��Ƶ���Դ
	Bitmap mBitmap;
	
	// ��������Ļ�ϵ�����
	int mX;
	int mY;
	
	// ����Ŀ�ߣ�Ŀǰ��ͼƬ��Դȷ����
	int mHeight;
	int mWidth;

	boolean isShow;
	boolean isActive;
	

	/**
	 * ������ʵ�����ŵ�ָ���Ĵ�С
	 * @param _width ���ŵ�Ŀ����
	 * @param _height ���ŵ�Ŀ��߶�
	 */
	public void SetScale(int _width, int _height)
	{
		mBitmap = Bitmap.createScaledBitmap(mBitmap,_width,_height,true);
		mHeight = mBitmap.getHeight();
		mWidth = mBitmap.getWidth();
	}
	
	// ���캯��
	public Entity(Resources res, int id, int _mX, int _mY, AhaGameEngine _engine)
	{
		mBitmap = BitmapFliter.GetScaleBitmap(res, id);
		
		mX = _mX;
		mY = _mY;
		
		mHeight = mBitmap.getHeight();
		mWidth = mBitmap.getWidth();
		
		isShow = true;
		isActive = true;
		
		// ��������
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
