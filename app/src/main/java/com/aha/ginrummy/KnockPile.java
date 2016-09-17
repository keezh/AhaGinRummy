package com.aha.ginrummy;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * �������򣬽����ϵ����������Ժ���
 * 
 */
public class KnockPile
{
	public AhaGameEngine engine;
	public Card card;		
	
	public KnockPile(AhaGameEngine _engine)
	{
		engine = _engine;
	}

	public void Init()
	{
		card = null;
	}
	
	/**
	 * ��ָ������������ƶ�
	 * <p>���棡������������׵��Ҫ����м��㣡�����ⲿȷ����
	 * <p>���棡�ú������ὫĿ���˿˴����������Ƴ��������ⲿȷ����
	 * <p>���棡�ú����������Ϸ״̬�����л��������ⲿȷ����
	 * @param card Ҫ������ƶԵ���
	 */
	public void SetCardToKnock(Card _card)
	{
		card = _card;
		card.SetTargetPosition(AhaCoord.COORD_KNOCK_X, AhaCoord.COORD_KNOCK_Y);
	}
	
	public void Update(long ElapsedTime)
	{
		if (card != null)
		{
			card.Update(ElapsedTime);
		}
	}
	
	public void Draw(Canvas canvas, long ElapsedTime)
	{
		Paint testPaint = new Paint();
		testPaint.setAlpha(100);
		canvas.drawBitmap(engine.mKnockBackBitmap,AhaCoord.COORD_KNOCK_X,AhaCoord.COORD_KNOCK_Y,testPaint);
		if(card != null)
		{
			card.Draw(canvas, ElapsedTime);
		}
	}
}
