package com.aha.ginrummy;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

/**
 * 弃牌的牌堆
 * 
 * @author Administrator
 * 
 */
public class DiscardPile
{
	// -------- 成员变量 --------

	/**
	 * 弃牌区的扑克牌容器
	 */
	ArrayList<Card> discards;

	/**
	 * 引擎的引用
	 */
	AhaGameEngine engine;

	/**
	 * 小伙子，从弃牌堆摸的牌是不能弃滴
	 */
	Card nodisCard;

	// -------- 构造函数 --------

	public DiscardPile(AhaGameEngine _engine)
	{
		discards = new ArrayList<Card>();
		engine = _engine;
	}

	// -------- 公共方法 --------

	/**
	 * 初始化弃牌区
	 * <p>
	 * 将弃牌区清空
	 */
	public void Init()
	{
		discards.clear();
	}

	/**
	 * 将指定的牌加入弃牌区尾部，无论是Player弃牌，还是Opponent弃牌，都使用这个方法
	 * 
	 * @param card
	 *            要加入弃牌区的牌
	 */
	public void AddDiscard(Card card)
	{
		if (card != null)
		{
			card.SetTargetPosition(AhaCoord.COORD_DISCARD_X,
					AhaCoord.COORD_DISCARD_Y);
			discards.add(card);

			// 切换引擎的回合状态
			engine.gameState = AhaGameState.STATE_DRAWCARD;
			if (engine.mTurnState == AhaTurnState.TURN_PLAYER)
			{
				engine.mTurnState = AhaTurnState.TURN_OPPONENT;
				engine.aiEngine.aiState = AIEngine.STATE_READY;
			} else
			{
				engine.mTurnState = AhaTurnState.TURN_PLAYER;
				// 如果是竞速模式，重置计时器
				if(engine.isRushMode)
				{
					engine.rushTimerHint.RecountTimer();
				}
			}

			
			// 清空不可弃区域
			ClearNodisCard();
		}
	}

	/**
	 * 从弃牌堆抽牌 - 飞行类 - lv1
	 * <p>
	 * 将弃牌区容器的最末位一张牌，发到飞行区之中
	 * <p>
	 * 该张牌会从抽牌区容器中移除。
	 * 
	 * @param flyingCard
	 */
	public void DrawCardToFly(FlyingCard flyingCard)
	{
		if (!discards.isEmpty())
		{
			// 获取尾部的牌
			int index = discards.size() - 1;
			Card drawCard = discards.get(index);

			// 将牌从弃牌区移除，并且添加到飞行区
			discards.remove(index);
			flyingCard.Fly(drawCard);

			Log.w("抓弃牌-飞行中", "Suit:" + drawCard.suit + " Rank：" + drawCard.rank);

			// 将抽出去的牌加入不可弃标记
			nodisCard = drawCard;

			// 抽完牌，进入下一个阶段
			engine.gameState = AhaGameState.STATE_DISCARD;
		}
	}


	/**
	 * 	/**
	 * 从弃牌堆抽牌 - 手牌类 - lv1
	 * <p>
	 * 从弃牌堆抽顶部的牌，并且直接加入手牌之中
	 * <p>
	 * 该张牌会从抽牌区容器中移除
	 * 
	 * @param hand 目标手牌区
	 * @return 返回发出去的牌的引用。（在某些状况下会用到）
	 */
	public Card DrawCardToHand(Hand hand)
	{
		if (!discards.isEmpty())
		{
			// 获取尾部的牌
			int index = discards.size() - 1;
			Card drawCard = discards.get(index);

			// 将牌从弃牌区移除，并且添加到手牌去
			discards.remove(index);
			hand.AddCardAndSort(drawCard);

			drawCard.SetFaceUp(hand);

			Log.w("抓弃牌-直接发到手牌区", "Suit:" + drawCard.suit + " Rank："
					+ drawCard.rank);

			// 将抽出去的牌加入不可弃标记
			nodisCard = drawCard;

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
	 * 获取弃牌堆的扑克牌数量
	 * 
	 * @return 扑克牌数量
	 */
	public int GetCardCount()
	{
		return discards.size();
	}

	/**
	 * 获取弃牌堆顶部的一张扑克
	 * 
	 * @return 返回弃牌堆顶部扑克的引用
	 */
	public Card GetTopCard()
	{
		if (!discards.isEmpty())
		{
			int index = discards.size() - 1;
			return discards.get(index);
		} else
		{
			return null;
		}
	}

	/**
	 * 判断参数指定的牌能否弃掉。如果是刚抓的牌，无法弃牌。
	 */
	public boolean IsCardCanDiscard(Card card)
	{
		if (nodisCard != null)
		{
			if (card.suit == nodisCard.suit && card.rank == nodisCard.rank)
			{
				return false;
			} else
			{
				return true;
			}
		}
		// 不存在discard，一定可弃
		else
		{
			return true;
		}

	}

	/**
	 * 将NodisCard置空
	 */
	public void ClearNodisCard()
	{
		nodisCard = null;
	}

	/**
	 * 获取弃牌堆的容器
	 */
	public ArrayList<Card> GetCards()
	{
		return discards;
	}
	// -------- 更新与绘制 --------

	public void Update(long ElapsedTime)
	{
		if (discards.size() == 1)
		{
			discards.get(0).Update(ElapsedTime);
		} else if (discards.size() >= 2)
		{
			discards.get(discards.size() - 2).Update(ElapsedTime);
			discards.get(discards.size() - 1).Update(ElapsedTime);
		}
	}

	public void Draw(Canvas canvas, long ElapsedTime)
	{
		Paint testPaint = new Paint();
		testPaint.setAlpha(100);
		// 在没有弃牌，或者只有一张弃牌的情况下，绘制弃牌区的背景。
		if (discards.size() <= 1)
		{
			canvas.drawBitmap(engine.mDiscardBackBitmap,
					AhaCoord.COORD_DISCARD_X,
					AhaCoord.COORD_DISCARD_Y, testPaint);
		}

		// 如果只有一张弃牌，只绘制这一张弃牌。
		// 否则绘制最后两张牌
		if (discards.size() == 1)
		{
			discards.get(0).Draw(canvas, ElapsedTime);
		} else if (discards.size() >= 2)
		{
			discards.get(discards.size() - 2).Draw(canvas, ElapsedTime);
			discards.get(discards.size() - 1).Draw(canvas, ElapsedTime);
		}
	}

}
