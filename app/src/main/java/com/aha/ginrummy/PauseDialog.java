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
		 * ��ȡ��Ļ�Ŀ�͸�
		 */
		WindowManager m = this.getWindow().getWindowManager();
		Display d = m.getDefaultDisplay();
		LinearLayout layout = (LinearLayout) findViewById(R.id.pauselinearlayout);

		LayoutParams p = getWindow().getAttributes(); // ��ȡ�Ի���ǰ�Ĳ���ֵ
		double scale = p.width / p.height; // ��¼��߱�

		/*
		 * �ж��Ǻ����������� ������Ϊ�̶��������п��߳�����Ļ��Χ
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
		p.alpha = 1f;// ���öԻ���͸����
		p.dimAmount = 0.6f; // ������һ��Activity�ĺڰ���

		/*
		 * ���öԻ����padding
		 */
		layout.setPadding(p.height / 20, p.height / 10, p.height / 10,
				p.height / 10);
		getWindow().setAttributes(p); // ������Ч
		getWindow().setGravity(Gravity.CENTER); // ���ÿ��Ҷ���

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
