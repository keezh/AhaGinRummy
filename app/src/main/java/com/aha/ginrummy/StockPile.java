package com.aha.ginrummy;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.util.Log;

/**
 * 尚未抽取的牌堆
 * @author Firzencode
 *
 */
public class StockPile
{
	// -------- 成员变量 --------
	
	/**
	 * 抽牌区的扑克牌容器 
	 */
	private ArrayList<Card> stockCards;
	
	/**
	 * 引擎的引用
	 */
	private AhaGameEngine engine;

	// -------- 构造函数 --------
	
	/**
	 * 构造抽牌区对象
	 * @param _x 抽牌区域绘制的牌背，其左上角的X轴坐标
	 * @param _y 抽牌区域绘制的牌背，其左上角的Y轴坐标
	 * @param _cardBackBitmap 牌背位图资源
	 */
	public StockPile(AhaGameEngine _engine)
	{
		engine = _engine;
		stockCards = new ArrayList<Card>();
	}
	
	// -------- 公共方法 --------
	
	/**
	 * 初始化抽牌区
	 * <p>将52张牌放入容器，然后洗牌。
	 * <p>设置52张牌均为不可见。
	 * <p>设置52张牌的位置都在中场的未抽牌区域。
	 */
	public void Init()
	{
		// 重置抽牌区
		stockCards.clear();
		
		// 将扑克牌从原始牌组加入抽牌去
		for(int i = 0; i <= 51; i++)
		{
			// 每张牌都不可见
			engine.allCards.get(i).SetVisible(false);
			
			// 每张牌都移到抽牌区域
			engine.allCards.get(i).SetPosition(AhaCoord.COORD_STOCK_X, AhaCoord.COORD_STOCK_Y);
			
			// 将牌添加到抽牌区
			stockCards.add(engine.allCards.get(i));
		}
		
		// 洗牌！！！
		Shuffle();
		
	}
	
	/**
	 * 获取尚未抽取的扑克牌数量
	 * @return 扑克牌数量
	 */
	public int GetCardCount()
	{
		return stockCards.size();
	}
	
	/**
	 * 洗牌算法 - lv1
	 * @author SuperMXC
	 */
	public void Shuffle()
	{
		int count=0;
		
		boolean [] cardMap=new boolean[stockCards.size()];
		
		for(int i=0; i < cardMap.length; i++)
		{
			cardMap[i]=false;
		}
		while(count < stockCards.size())
		{
			int src=((int)(Math.random()*1000))%stockCards.size();
			int des=((int)(Math.random()*1000))%stockCards.size();
			if(cardMap[src]==false)
				count++;
			if(cardMap[des]==false)
				count++;
			cardMap[src]=cardMap[des]=true;
			Card tempCard=stockCards.get(src);
			stockCards.set(src,stockCards.get(des));
			stockCards.set(des,tempCard);
		}	
	}
	
	/**
	 * 发牌算法 - lv1
	 * <p>将抽牌区容器的最末位一张牌，发到指定的手牌区之中。
	 * <p>根据手牌区的类型，
	 * <P>如果手牌区为本机玩家（handType = TYPE_PLAYER)，该张牌会变为牌面朝上（isFaceUp = true），
	 * <P>如果手牌区为对手玩家（handType = TYPE_OPPONENT），该张牌会变为牌面朝下（isFaceUp = false），
	 * <p>牌会变成显示状态（isShow = true)
	 * <p>完成以上步骤之后，该张牌会从抽牌区容器中移除，并添加到手牌区容器中。
	 * 
	 * @param hand 目标手牌区
	 */
	public void Deal(Hand hand)
	{
		if (!stockCards.isEmpty())
		{
			// 获取尾部的牌
			int index = stockCards.size() - 1;
			Card dealCard = stockCards.get(index);
			
			// 设置发出去的牌，牌面是否朝上
			dealCard.SetFaceUp(hand);
			
			// 将发出去的牌设为可见
			dealCard.SetVisible(true);
			
			// 将牌从抽牌区中取出，并添加到手牌区
			stockCards.remove(index);
			hand.AddCardNoSort(dealCard);
			
			Log.w("发牌", "Suit:" + dealCard.suit + " Rank：" + dealCard.rank);
		}
	}
	
	/**
	 * 从抽牌堆抽牌 - 飞行类 - lv1
	 * <p>将抽牌区容器的最末位一张牌，发到飞行区之中
	 * <p>该张牌会变为正面朝上的状态（isFaceUp = true）
	 * <p>该张牌会变为显示状态（isShow = true）
	 * <p>完成以上步骤后，该张牌会从抽牌区容器中移除，并添加到飞行区之中。
	 */
	public void DrawCardToFly(FlyingCard flyingCard)
	{
		if (!stockCards.isEmpty())
		{
			// 获取尾部的牌
			int index = stockCards.size() - 1;
			Card drawCard = stockCards.get(index);
			
			// 设置正面朝上
			drawCard.SetFaceUp(true);
			
			// 将发出去的牌设为可见
			drawCard.SetVisible(true);
			
			// 将牌从抽牌区中取出，并添加到飞行区
			stockCards.remove(index);
			flyingCard.Fly(drawCard);
			
			Log.w("抽牌-飞行中", "Suit:" + drawCard.suit + " Rank：" + drawCard.rank);
			
			// 抽完牌，进入下一个阶段
			engine.gameState = AhaGameState.STATE_DISCARD;
		}
	}
	
	/**
	 * 从抽牌堆抽牌 - 手牌类 - lv1
	 * <p>将抽牌区容器的最末位一张牌，直接发到手牌之中
	 * <p>该张牌会根据手牌的类型，变为是否正面朝上
	 * <p>该张牌会变成显示状态（isShow = true）
	 * <p>完成以上步骤后，该张牌会从抽牌区容器中移除，并添加到手牌区之中
	 * @param hand
	 * @return 返回发出去的牌，在某些情况下有用
	 */
	public Card DrawCardToHand(Hand hand)
	{
		if (!stockCards.isEmpty())
		{
			// 获取尾部的牌
			int index = stockCards.size() - 1;
			Card drawCard = stockCards.get(index);
			
			// 根据hand设置是否正面朝上
			drawCard.SetFaceUp(hand);
			
			// 将发出去的牌设为可见
			drawCard.SetVisible(true);
			
			// 将牌从抽牌区中取出，并添加到手牌区
			stockCards.remove(index);
			hand.AddCardAndSort(drawCard);
			
			Log.w("抽牌-手牌类", "Suit:" + drawCard.suit + " Rank：" + drawCard.rank);
			
			// 抽完牌，进入下一个阶段
			engine.gameState = AhaGameState.STATE_DISCARD;
			
			return drawCard;
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * 将目标牌发到手牌区。高级AI专用
	 */
	public Card DrawTargetCardToHand(Hand hand, Card targetCard)
	{
		if (!stockCards.isEmpty() && stockCards.contains(targetCard) && targetCard != null)
		{
			Card drawCard = targetCard;
			
			// 根据hand设置是否正面朝上
			drawCard.SetFaceUp(hand);
			
			// 将发出去的牌设为可见
			drawCard.SetVisible(true);
			
			// 将牌从抽牌区中取出，并添加到手牌区
			stockCards.remove(drawCard);
			hand.AddCardAndSort(drawCard);
			
			Log.w("抽牌-手牌类", "Suit:" + drawCard.suit + " Rank：" + drawCard.rank);
			
			// 抽完牌，进入下一个阶段
			engine.gameState = AhaGameState.STATE_DISCARD;
			
			return drawCard;
		}
		else
		{
			Log.w("StockPile错误！", "没找到Suit:" + targetCard.suit + "Rank:" + targetCard.rank + "in StockPile");
			return null;
		}
	}
	
	/**
	 * 获取顶部的第一张牌
	 * @return
	 */
	public Card GetTopCard()
	{
		if (!stockCards.isEmpty())
		{
			int index = stockCards.size() - 1;
			return stockCards.get(index);
		}
		else 
		{
			return null;
		}
	}
	
	/**
	 * 弹出顶部的第一张牌
	 * @return
	 */
	public Card PopTopCard()
	{
		if (!stockCards.isEmpty())
		{
			int index = stockCards.size() - 1;
			Card tempCard = stockCards.get(index);
			tempCard.SetFaceUp(true);
			stockCards.remove(index);
			return tempCard;
		}
		else 
		{
			return null;
		}
	}
	
	/**
	 * 获取抽牌堆容器
	 */
	public ArrayList<Card> GetCards()
	{
		return stockCards;
	}
	// -------- 更新与绘制 --------
	

	/**
	 * 绘制未抽牌的牌堆。
	 * <p>如果牌堆中还有剩牌，则在目标区域绘制牌背一张。如果牌堆中没牌了，则不绘制。
	 * @param canvas 用以绘制的Canvas
	 * @param ElapsedTime 距离上一帧所经历的时间
	 */
	public void Draw(Canvas canvas, long ElapsedTime)
	{
		if (!stockCards.isEmpty())
		{
			canvas.drawBitmap(engine.mCardBackBitmap,
					AhaCoord.COORD_STOCK_X,
					AhaCoord.COORD_STOCK_Y,
					null);
		}
	}

	
}
