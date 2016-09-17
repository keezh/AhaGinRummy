package com.aha.ginrummy;

import java.util.ArrayList;

public class AIEngine
{
	// -------- ��̬��Ա --------

	/**
	 * ���񣺼���������ĵ�һ�����Ƿ���Ҫ��
	 * <p>
	 * ���룺�������ĵ�һ����
	 * <p>
	 * �����
	 * <p>
	 * �����Ҫ�������ƣ������result�з�����Ҫ�������ơ�
	 * <p>
	 * �������Ҫ�������ƣ������result�з���null��
	 */
	public static final int TASK_IsDiscardNeed = 1;

	/**
	 * ���񣺴ӳ���������һ���ƣ�������Ҫ��������
	 * <p>
	 * ���룺�������ĵ�һ���Ƶ�����
	 * <p>
	 * �������result�з�����Ҫ��������
	 */
	public static final int TASK_DrawFromStockAndDiscard = 2;

	public static final int STATE_READY = 1;
	public static final int STATE_THINKING_IsDiscardNeed = 2;
	public static final int STATE_RESULT_IsDiscardNeed_TRUE = 3;
	public static final int STATE_RESULT_IsDiscardNeed_FALSE = 4;
	public static final int STATE_THINKING_DrawFromStockAndDiscard = 5;
	public static final int STATE_RESULT_DrawFromStockAndDiscard = 6;
	public static final int STATE_DONE = 7;
	
	// -------- ��Ա���� -------
	
	/**
	 * �������Ƶ�simCard�汾����ʼ��ʱ����ʵ����ͬ����֮����ҪAI����ͬ��
	 */
	public ArrayList<SimCard> cards;

	// ���Խ���˼�����߳�
	public AIThinking thinkingThread;

	// ���������
	public AhaGameEngine engine;

	// ���AI�����״̬
	public int aiState;
	
	// ����˼�������Card
	public Card result;

	// ����߼�AI��Ҫ����
	public Card cheatNeedCard;
	
	// ----------��״̬������-------------
	//�׺�����
	public boolean CAN_KNOCK = false;
	
	//<10�����׺�
	public boolean KNOCK_IMMI = false;
	
	//<10(KNOCK_NOPROGRESS_SIZE+1)��û�н�չ���׺�
	public int KNOCK_NOPROGRESS_SIZE = 4;
	
	//ǰ���پ����׺����׺�
	public int KNOCK_ROUND = 13;
	
	//��������
	public int watchNum = 5;
	
	public boolean gin;
	public int knock_noProgress;
	public boolean knock;
	public boolean readyKnock;
	
	// -----------------------------
	
	// -------- ���캯�� --------

	public AIEngine(AhaGameEngine _engine)
	{
		engine = _engine;
		cards = new ArrayList<SimCard>();
	}

	// -------- �������� --------

	/**
	 * ��ʼ��AI���棬��Ҫ��ÿ�ַ����������֮�����
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
	 * ����AI˼������
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
