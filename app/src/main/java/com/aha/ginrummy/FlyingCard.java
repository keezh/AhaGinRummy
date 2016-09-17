package com.aha.ginrummy;

import android.graphics.Canvas;

/**
 * 表示被本地玩家拖动着的牌
 * <p>这张牌可能是从抽牌区、弃牌区、手牌区拖出来
 * <p>如果是抽牌阶段，可以放入手牌区
 * <p>如果是弃牌阶段，可以放入手牌去，弃牌区，胡牌区，当然，如果能胡的话：-）
 */
public class FlyingCard
{
	/**
	 * 当前拖动着的牌
	 */
	Card flyingCard;
	
	/**
	 * 引擎的引用
	 */
	AhaGameEngine engine;
	
	/**
	 * 创建FlyingCard
	 * @param _engine 引擎的引用
	 */
	public FlyingCard(AhaGameEngine _engine)
	{
		engine = _engine;
	}

	/**
	 * 初始化，清空拖动的牌
	 */
	public void Init()
	{
		flyingCard = null;
	}
	
	/**
	 * 拖动目标扑克。
	 * 让纸牌飞一会儿！
	 * @param card 需要飞行的目标扑克
	 */
	public void Fly(Card card)
	{
		
		flyingCard = card;
	}
	
	/**
	 * 扑克降落。不再由飞行区进行操纵。
	 */
	public Card Land()
	{
		Card tempCard = flyingCard;
		flyingCard = null;
		return tempCard;
	}
	
	/**
	 * 检查当前是否有牌被拖动着
	 * @return 若有牌被拖动着，则返回true，否则返回false。
	 */
	public boolean IsFlying()
	{
		if (flyingCard != null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * 检查飞行牌是否和弃牌区相交
	 * @return
	 */
	public boolean IsIntersectWithDiscard()
	{
		return engine.IsRectIntersect(
				flyingCard.mX, flyingCard.mY, flyingCard.GetWidth(), flyingCard.GetHeight(), 
				AhaCoord.COORD_DISCARD_X, AhaCoord.COORD_DISCARD_Y, 
				AhaSize.SIZE_DISCARD_WIDTH, AhaSize.SIZE_DISCARD_HEIGHT);
	}
	
	/**
	 * 检查飞行牌是否和胡牌区相交
	 * @return
	 */
	public boolean IsIntersectWithKnock()
	{
		return engine.IsRectIntersect(
				flyingCard.mX, flyingCard.mY, flyingCard.GetWidth(), flyingCard.GetHeight(), 
				AhaCoord.COORD_KNOCK_X, AhaCoord.COORD_KNOCK_Y, 
				AhaSize.SIZE_KNOCK_WIDTH, AhaSize.SIZE_KNOCK_HEIGHT);
	}
	/**
	 * 通过输入当前触摸的位置，计算出飞行牌的坐标
	 * @param x 当前触摸位置的x轴坐标
	 * @param y 当前触摸位置的y轴坐标
	 */
 	public void SetFlyPosition(int x, int y)
	{
		flyingCard.SetPosition(x - AhaSize.SIZE_CARD_WIDTH / 2, y - AhaSize.SIZE_CARD_HEIGHT);
	}
	
	// -------- 更新与绘制 --------
	
	public void Draw(Canvas canvas, long ElapsedTime)
	{
		if(IsFlying())
		{
			flyingCard.Draw(canvas, ElapsedTime);
		}
	}
}
