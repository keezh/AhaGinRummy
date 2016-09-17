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

// 包含Surface的AhaView，用以接受用户输入的功能。
// 包含内部类AhaThread，对逻辑控制和绘图进行操作
public class AhaView extends SurfaceView implements SurfaceHolder.Callback, OnTouchListener
{
	// 用以进行逻辑控制和绘图操作的线程
	class AhaThread extends Thread
	{
        /** Handle to the surface manager object we interact with */
		// 操纵我们要与之交互的Surface Manager
        private SurfaceHolder mSurfaceHolder;
             
        // 线程是否运行的标记。当且仅当标记为true的时候，
        private boolean mRunning;
        
		// 构造函数
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
		// 线程的运行函数
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
	                	c = mSurfaceHolder.lockCanvas();   //Log.w("Canvas", "锁定！");
	                	
	                	//mEngine.Draw(c,mElapsedTime);					// 这个消耗了10~20ms
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
							mSurfaceHolder.unlockCanvasAndPost(c);  // 这个只消耗了几乎1ms
						}
					}
                }
				TimeFlagB = System.currentTimeMillis();
				//Log.w("Timetest", "MS:" + (TimeFlagB - TimeFlagA));
									// A-Plan：到这了使用了30+ms
                //Log.w("帧时间", "帧消耗时间：" + (end - start));
                //Log.w("帧时间", "休眠时间: " + Math.max(0, 50 - (end - start)));
                
               
            }
			
		}
		
		// 设置线程的运行标记
		public void SetRunning(boolean b)
		{
			mRunning = b;
		}

		// 设置绘图区域的大小，在这里保证了mSurfaceHolder的同步
		// 具体对于区域大小以及背景的设置，由引擎完成
		public void SetSurfaceSize(int width, int height)
		{
//			synchronized (mSurfaceHolder) 
			{
				mEngine.SetSurfaceSize(width, height);
            }
		}
		
	}
	
	// 引擎（引用），在主Activity中创建，并且在创建AhaView的时候，传递到这里
	AhaGameEngine mEngine;
	
	// 处理逻辑和绘图的线程
	AhaThread mThread;

	public AhaView(Context context, AttributeSet attrs)
	{
		super(context,attrs);
		
		// 获取SurfaceHolder，并且将监听器（自身）添加到上面
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		
		// 保证这个view能够获得输入
		setFocusable(true); 
		
		// 获取引擎的引用
		mEngine = (AhaGameEngine)(context.getApplicationContext());
		
		//getHolder().setType(SurfaceHolder.SURFACE_TYPE_HARDWARE);
		
		// 设置AhaView的触摸事件监听器
		setOnTouchListener(this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		mThread = new AhaThread(holder);	
		mThread.SetRunning(true);
		mThread.start();
		Log.w("Surface", "创建");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height)
	{
		Log.w("Surface Changed!", "height:" + height + " weight:" + width);
		mThread.SetSurfaceSize(width, height);
		Log.w("Surface", "变化");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		Log.w("Surface", "摧毁");
		// 停掉AhaThread，停止处理Update和Draw
		boolean retry = true;
		mThread.SetRunning(false);

		// 保证mThead执行完成
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
	 * 触发AhaView的触摸屏事件，并转交给AhaGameEngine进行处理
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		mEngine.onTouch(v, event);
		return true;
	}
	
}
