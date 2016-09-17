package com.aha.ginrummy;

import java.lang.Thread.State;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

// ����Surface��AhaView�����Խ����û�����Ĺ��ܡ�
// �����ڲ���AhaThread�����߼����ƺͻ�ͼ���в���
public class AhaView extends SurfaceView implements SurfaceHolder.Callback, OnTouchListener
{
	// ���Խ����߼����ƺͻ�ͼ�������߳�
	class AhaThread extends Thread
	{
        /** Handle to the surface manager object we interact with */
		// ��������Ҫ��֮������Surface Manager
        private SurfaceHolder mSurfaceHolder;
             
        // �߳��Ƿ����еı�ǡ����ҽ������Ϊtrue��ʱ��
        private boolean mRunning;
        
		// ���캯��
		public AhaThread(SurfaceHolder surfaceHolder)
		{
            mSurfaceHolder = surfaceHolder;
		}

		long TimeFlagA;
		long TimeFlagB;
		long start;
		long end;
		long mLastFrameTime;
		long mElapsedTime;
		// �̵߳����к���
		@Override
		public void run()
		{
			Canvas c = null;
			mEngine.RunInit();
			mLastFrameTime = System.currentTimeMillis();
			while (mRunning) 
			{	
				start = System.currentTimeMillis();	
				
				mElapsedTime = System.currentTimeMillis() - mLastFrameTime;
				mLastFrameTime = System.currentTimeMillis();
				//mEngine.Update(mElapsedTime);
				mEngine.UpdateMain(mElapsedTime);
//				synchronized (mSurfaceHolder) 
                {
					try 
					{
	                	c = mSurfaceHolder.lockCanvas();   //Log.w("Canvas", "������");
	                	
	                	//mEngine.Draw(c,mElapsedTime);					// ���������10~20ms
	                	if (c!=null)
	                	{
	                		mEngine.DrawMain(c, mElapsedTime);
	                	}
	                	end = System.currentTimeMillis();
	                	
	                	TimeFlagA = System.currentTimeMillis();
	                	Thread.sleep(Math.max(0, 40 - (end - start)));
					} 
					catch (InterruptedException e)
					{
					}
					finally 
					{
						if (c != null) 
						{
							mSurfaceHolder.unlockCanvasAndPost(c);  // ���ֻ�����˼���1ms
						}
					}
                }
				TimeFlagB = System.currentTimeMillis();
				//Log.w("Timetest", "MS:" + (TimeFlagB - TimeFlagA));
									// A-Plan��������ʹ����30+ms
                //Log.w("֡ʱ��", "֡����ʱ�䣺" + (end - start));
                //Log.w("֡ʱ��", "����ʱ��: " + Math.max(0, 50 - (end - start)));
                
               
            }
			
		}
		
		// �����̵߳����б��
		public void SetRunning(boolean b)
		{
			mRunning = b;
		}

		// ���û�ͼ����Ĵ�С�������ﱣ֤��mSurfaceHolder��ͬ��
		// ������������С�Լ����������ã����������
		public void SetSurfaceSize(int width, int height)
		{
//			synchronized (mSurfaceHolder) 
			{
				mEngine.SetSurfaceSize(width, height);
            }
		}
		
	}
	
	// ���棨���ã�������Activity�д����������ڴ���AhaView��ʱ�򣬴��ݵ�����
	AhaGameEngine mEngine;
	
	// �����߼��ͻ�ͼ���߳�
	AhaThread mThread;

	public AhaView(Context context, AttributeSet attrs)
	{
		super(context,attrs);
		
		// ��ȡSurfaceHolder�����ҽ���������������ӵ�����
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		
		// ��֤���view�ܹ��������
		setFocusable(true); 
		
		// ��ȡ���������
		mEngine = (AhaGameEngine)(context.getApplicationContext());
		
		//getHolder().setType(SurfaceHolder.SURFACE_TYPE_HARDWARE);
		
		// ����AhaView�Ĵ����¼�������
		setOnTouchListener(this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		mThread = new AhaThread(holder);	
		mThread.SetRunning(true);
		mThread.start();
		Log.w("Surface", "����");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height)
	{
		Log.w("Surface Changed!", "height:" + height + " weight:" + width);
		mThread.SetSurfaceSize(width, height);
		Log.w("Surface", "�仯");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		Log.w("Surface", "�ݻ�");
		// ͣ��AhaThread��ֹͣ����Update��Draw
		boolean retry = true;
		mThread.SetRunning(false);

		// ��֤mTheadִ�����
		while (retry)
		{
			try
			{
				mThread.join();
				retry = false;
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
	}

	public AhaThread GetThread()
	{
		return mThread;
	}

	/**
	 * ����AhaView�Ĵ������¼�����ת����AhaGameEngine���д���
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		mEngine.onTouch(v, event);
		return true;
	}
	
}
