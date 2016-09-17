package com.aha.ginrummy;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PauseDialog extends Dialog
{
	ImageView goHome;
	ImageView newGame;
	ImageView resume;
	private MiddleActivity owner;

	public PauseDialog(Context context, int theme)
	{
		super(context, theme);
		this.setContentView(R.layout.pause);
		goHome = (ImageView) findViewById(R.id.pause_iv_pausehome);
		newGame = (ImageView) findViewById(R.id.pause_iv_pausenewgame);
		resume = (ImageView) findViewById(R.id.pause_iv_pauseresume);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		/*
		 * 获取屏幕的宽和高
		 */
		WindowManager m = this.getWindow().getWindowManager();
		Display d = m.getDefaultDisplay();
		LinearLayout layout = (LinearLayout) findViewById(R.id.pauselinearlayout);

		LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
		double scale = p.width / p.height; // 记录宽高比

		/*
		 * 判断是横屏还是竖屏 避免因为固定比例会有宽或高超出屏幕范围
		 */
		if (d.getWidth() > d.getHeight())
		{
			p.height = (int) (d.getHeight());
			p.width = (int) (p.height * scale);
		} else
		{
			p.width = (int) (d.getWidth());
			p.height = (int) (p.width / scale);
		}
		p.alpha = 1f;// 设置对话框透明度
		p.dimAmount = 0.6f; // 设置下一层Activity的黑暗度

		/*
		 * 设置对话框的padding
		 */
		layout.setPadding(p.height / 20, p.height / 10, p.height / 10,
				p.height / 10);
		getWindow().setAttributes(p); // 设置生效
		getWindow().setGravity(Gravity.CENTER); // 设置靠右对齐

	}
	
	public void setOnTouchListener(android.view.View.OnTouchListener listener)
	{
		goHome.setOnTouchListener(listener);
		newGame.setOnTouchListener(listener);
		resume.setOnTouchListener(listener);
	}
	public void setOwner(MiddleActivity owner)
	{
		this.owner = owner;
	}
	
	@Override
	public void dismiss()
	{
		owner.finish();
		super.dismiss();
	}
}
