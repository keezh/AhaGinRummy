package com.aha.ginrummy;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 胡牌区域，将牌拖到胡牌区域以胡牌
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
	 * 将指定的牌塞入胡牌堆
	 * <p>警告！胡牌区不会对米点等要求进行计算！请在外部确保！
	 * <p>警告！该函数不会将目标扑克从其他区域移除！请在外部确保！
	 * <p>警告！该函数不会对游戏状态进行切换！请在外部确保！
	 * @param card 要塞入胡牌对的牌
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
