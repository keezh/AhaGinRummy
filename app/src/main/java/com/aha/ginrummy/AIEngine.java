package com.aha.ginrummy;

import java.util.ArrayList;

public class AIEngine
{
	// -------- 静态成员 --------

	/**
	 * 任务：检测弃牌区的第一张牌是否需要。
	 * <p>
	 * 输入：弃牌区的第一张牌
	 * <p>
	 * 输出：
	 * <p>
	 * 如果需要这张弃牌，则会在result中返回需要丢弃的牌。
	 * <p>
	 * 如果不需要这张弃牌，则会在result中返回null。
	 */
	public static final int TASK_IsDiscardNeed = 1;

	/**
	 * 任务：从抽牌区摸第一张牌，计算需要丢弃的牌
	 * <p>
	 * 输入：抽牌区的第一张牌的引用
	 * <p>
	 * 输出：在result中返回需要丢弃的牌
	 */
	public static final int TASK_DrawFromStockAndDiscard = 2;

	public static final int STATE_READY = 1;
	public static final int STATE_THINKING_IsDiscardNeed = 2;
	public static final int STATE_RESULT_IsDiscardNeed_TRUE = 3;
	public static final int STATE_RESULT_IsDiscardNeed_FALSE = 4;
	public static final int STATE_THINKING_DrawFromStockAndDiscard = 5;
	public static final int STATE_RESULT_DrawFromStockAndDiscard = 6;
	public static final int STATE_DONE = 7;
	
	// -------- 成员变量 -------
	
	/**
	 * 电脑手牌的simCard版本，初始化时和真实手牌同步，之后需要AI自行同步
	 */
	public ArrayList<SimCard> cards;

	// 用以进行思考的线程
	public AIThinking thinkingThread;

	// 引擎的引用
	public AhaGameEngine engine;

	// 标记AI引擎的状态
	public int aiState;
	
	// 保存思考结果的Card
	public Card result;

	// 保存高级AI需要的牌
	public Card cheatNeedCard;
	
	// ----------【状态变量】-------------
	//米胡开关
	public boolean CAN_KNOCK = false;
	
	//<10立刻米胡
	public boolean KNOCK_IMMI = false;
	
	//<10(KNOCK_NOPROGRESS_SIZE+1)局没有进展就米胡
	public int KNOCK_NOPROGRESS_SIZE = 4;
	
	//前多少局能米胡就米胡
	public int KNOCK_ROUND = 13;
	
	//看牌张数
	public int watchNum = 5;
	
	public boolean gin;
	public int knock_noProgress;
	public boolean knock;
	public boolean readyKnock;
	
	// -----------------------------
	
	// -------- 构造函数 --------

	public AIEngine(AhaGameEngine _engine)
	{
		engine = _engine;
		cards = new ArrayList<SimCard>();
	}

	// -------- 公共方法 --------

	/**
	 * 初始化AI引擎，需要在每局发完基本手牌之后调用
	 */
	public void InitAfterDeal(ArrayList<Card> c)
	{
		gin = false;
		knock_noProgress = 0;
		knock = false;
		readyKnock = false;

		cards.clear();
		int i = 0;
		while (i < c.size())
		{
			SimCard temp = new SimCard(c.get(i));
			cards.add(temp);
			++i;
		}

		aiState = STATE_READY;
		
		result = null;
	}

	/**
	 * 启动AI思考任务
	 */
	public void StartTask(int _task, Card _card)
	{
		result = null;

		if(_task == TASK_IsDiscardNeed)
		{
			aiState = STATE_THINKING_IsDiscardNeed;
		}
		else if(_task == TASK_DrawFromStockAndDiscard)
		{
			aiState = STATE_THINKING_DrawFromStockAndDiscard;
		}
		
		thinkingThread = new AIThinking(_task, _card, this);
		thinkingThread.start();
	}
	
	public Card FindCardBySimCard(SimCard simCard)
	{
		return engine.FindCard(simCard.suit, simCard.rank);
	}
	
	public ArrayList<SimCard> CreateSimCardsFromNormalCards(ArrayList<Card> cards)
	{
		ArrayList<SimCard> tempSimCards = new ArrayList<SimCard>();
		
		int i = 0;
		while(i < cards.size())
		{
			tempSimCards.add(new SimCard(cards.get(i)));
			i++;
		}
		return tempSimCards;
	}
}

class SimCard implements Comparable<SimCard>
{
	int suit, rank;

	public SimCard(SimCard simCard)
	{
		suit = simCard.suit;
		rank = simCard.rank;
	}

	public SimCard(Card card)
	{
		suit = card.suit;
		rank = card.rank;
	}

	public SimCard(int rank, int suit)
	{
		this.suit = suit;
		this.rank = rank;
	}

	public int compareTo(SimCard another)
	{
		if (rank < another.rank)
			return -1;
		else if (rank > another.rank)
			return 1;
		else
		{
			if (suit < another.suit)
			{
				return -1;
			} else
			{
				return 1;
			}
		}
	}

}
