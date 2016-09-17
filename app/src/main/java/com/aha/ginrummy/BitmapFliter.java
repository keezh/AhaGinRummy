package com.aha.ginrummy;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

// λͼ��Դ�ļ�������װ��
// ��ʹ��֮ǰ��
// STEP-1������Ҫ���ñ�׼��Դ�����ķֱ���ģʽ
// STPE-2��Ȼ�����õ�ǰ����Ļ�ֱ���ģʽ
// STEP-3��Ȼ��Ϳ���ʹ��GetScaleBitmap��λͼ��Դ�����Զ�����

public class BitmapFliter
{
	public static int srcMode;
	public static int srcScreenWidth;
	public static int srcScreenHeight;
	
	public static int dstMode;
	public static int dstScreenWidth;
	public static int dstScreenHeight;
	
	public static float scaleWidth;
	public static float scaleHeight;
	
	public static final int MODE_800_480 = 1;
	public static final int MODE_480_320 = 2;
	
	public static void SetMode(int _srcMode, int _dstMode)
	{
		if (_srcMode == MODE_800_480)
		{
			srcScreenWidth = 480;
			srcScreenHeight = 800;
		}
		
		if (_srcMode == MODE_480_320)
		{
			srcScreenWidth = 320;
			srcScreenHeight = 480;
		}
		
		if (_dstMode == MODE_800_480)
		{
			dstScreenWidth = 480;
			dstScreenHeight = 800;
		}
		
		if (_dstMode == MODE_480_320)
		{
			dstScreenWidth = 320;
			dstScreenHeight = 482;
		}
		
		scaleWidth = dstScreenWidth / (float)srcScreenWidth;
		scaleHeight = dstScreenHeight / (float)srcScreenHeight;
		
	}

	public static Bitmap GetScaleBitmap(Resources res, int id)
	{
		Bitmap src = BitmapFactory.decodeResource(res, id);
		return Bitmap.createScaledBitmap(src, 
				(int)( src.getWidth() * scaleWidth ), 
				(int)( src.getHeight() * scaleHeight ), 
				true);
	}
}
