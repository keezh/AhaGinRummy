package com.aha.ginrummy;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.util.Log;

/**
 * ��δ��ȡ���ƶ�
 * @author Firzencode
 *
 */
public class StockPile
{
	// -------- ��Ա���� --------
	
	/**
	 * ���������˿������� 
	 */
	private ArrayList<Card> stockCards;
	
	/**
	 * ���������
	 */
	private AhaGameEngine engine;

	// -------- ���캯�� --------
	
	/**
	 * �������������
	 * @param _x ����������Ƶ��Ʊ��������Ͻǵ�X������
	 * @param _y ����������Ƶ��Ʊ��������Ͻǵ�Y������
	 * @param _cardBackBitmap �Ʊ�λͼ��Դ
	 */
	public StockPile(AhaGameEngine _engine)
	{
		engine = _engine;
		stockCards = new ArrayList<Card>();
	}
	
	// -------- �������� --------
	
	/**
	 * ��ʼ��������
	 * <p>��52���Ʒ���������Ȼ��ϴ�ơ�
	 * <p>����52���ƾ�Ϊ���ɼ���
	 * <p>����52���Ƶ�λ�ö����г���δ��������
	 */
	public void Init()
	{
		// ���ó�����
		stockCards.clear();
		
		// ���˿��ƴ�ԭʼ����������ȥ
		for(int i = 0; i <= 51; i++)
		{
			// ÿ���ƶ����ɼ�
			engine.allCards.get(i).SetVisible(false);
			
			// ÿ���ƶ��Ƶ���������
			engine.allCards.get(i).SetPosition(AhaCoord.COORD_STOCK_X, AhaCoord.COORD_STOCK_Y);
			
			// ������ӵ�������
			stockCards.add(engine.allCards.get(i));
		}
		
		// ϴ�ƣ�����
		Shuffle();
		
	}
	
	/**
	 * ��ȡ��δ��ȡ���˿�������
	 * @return �˿�������
	 */
	public int GetCardCount()
	{
		return stockCards.size();
	}
	
	/**
	 * ϴ���㷨 - lv1
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
	 * �����㷨 - lv1
	 * <p>����������������ĩλһ���ƣ�����ָ����������֮�С�
	 * <p>���������������ͣ�
	 * <P>���������Ϊ������ң�handType = TYPE_PLAYER)�������ƻ��Ϊ���泯�ϣ�isFaceUp = true����
	 * <P>���������Ϊ������ң�handType = TYPE_OPPONENT���������ƻ��Ϊ���泯�£�isFaceUp = false����
	 * <p>�ƻ�����ʾ״̬��isShow = true)
	 * <p>������ϲ���֮�󣬸����ƻ�ӳ������������Ƴ�������ӵ������������С�
	 * 
	 * @param hand Ŀ��������
	 */
	public void Deal(Hand hand)
	{
		if (!stockCards.isEmpty())
		{
			// ��ȡβ������
			int index = stockCards.size() - 1;
			Card dealCard = stockCards.get(index);
			
			// ���÷���ȥ���ƣ������Ƿ���
			dealCard.SetFaceUp(hand);
			
			// ������ȥ������Ϊ�ɼ�
			dealCard.SetVisible(true);
			
			// ���ƴӳ�������ȡ��������ӵ�������
			stockCards.remove(index);
			hand.AddCardNoSort(dealCard);
			
			Log.w("����", "Suit:" + dealCard.suit + " Rank��" + dealCard.rank);
		}
	}
	
	/**
	 * �ӳ��ƶѳ��� - ������ - lv1
	 * <p>����������������ĩλһ���ƣ�����������֮��
	 * <p>�����ƻ��Ϊ���泯�ϵ�״̬��isFaceUp = true��
	 * <p>�����ƻ��Ϊ��ʾ״̬��isShow = true��
	 * <p>������ϲ���󣬸����ƻ�ӳ������������Ƴ�������ӵ�������֮�С�
	 */
	public void DrawCardToFly(FlyingCard flyingCard)
	{
		if (!stockCards.isEmpty())
		{
			// ��ȡβ������
			int index = stockCards.size() - 1;
			Card drawCard = stockCards.get(index);
			
			// �������泯��
			drawCard.SetFaceUp(true);
			
			// ������ȥ������Ϊ�ɼ�
			drawCard.SetVisible(true);
			
			// ���ƴӳ�������ȡ��������ӵ�������
			stockCards.remove(index);
			flyingCard.Fly(drawCard);
			
			Log.w("����-������", "Suit:" + drawCard.suit + " Rank��" + drawCard.rank);
			
			// �����ƣ�������һ���׶�
			engine.gameState = AhaGameState.STATE_DISCARD;
		}
	}
	
	/**
	 * �ӳ��ƶѳ��� - ������ - lv1
	 * <p>����������������ĩλһ���ƣ�ֱ�ӷ�������֮��
	 * <p>�����ƻ�������Ƶ����ͣ���Ϊ�Ƿ����泯��
	 * <p>�����ƻ�����ʾ״̬��isShow = true��
	 * <p>������ϲ���󣬸����ƻ�ӳ������������Ƴ�������ӵ�������֮��
	 * @param hand
	 * @return ���ط���ȥ���ƣ���ĳЩ���������
	 */
	public Card DrawCardToHand(Hand hand)
	{
		if (!stockCards.isEmpty())
		{
			// ��ȡβ������
			int index = stockCards.size() - 1;
			Card drawCard = stockCards.get(index);
			
			// ����hand�����Ƿ����泯��
			drawCard.SetFaceUp(hand);
			
			// ������ȥ������Ϊ�ɼ�
			drawCard.SetVisible(true);
			
			// ���ƴӳ�������ȡ��������ӵ�������
			stockCards.remove(index);
			hand.AddCardAndSort(drawCard);
			
			Log.w("����-������", "Suit:" + drawCard.suit + " Rank��" + drawCard.rank);
			
			// �����ƣ�������һ���׶�
			engine.gameState = AhaGameState.STATE_DISCARD;
			
			return drawCard;
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * ��Ŀ���Ʒ������������߼�AIר��
	 */
	public Card DrawTargetCardToHand(Hand hand, Card targetCard)
	{
		if (!stockCards.isEmpty() && stockCards.contains(targetCard) && targetCard != null)
		{
			Card drawCard = targetCard;
			
			// ����hand�����Ƿ����泯��
			drawCard.SetFaceUp(hand);
			
			// ������ȥ������Ϊ�ɼ�
			drawCard.SetVisible(true);
			
			// ���ƴӳ�������ȡ��������ӵ�������
			stockCards.remove(drawCard);
			hand.AddCardAndSort(drawCard);
			
			Log.w("����-������", "Suit:" + drawCard.suit + " Rank��" + drawCard.rank);
			
			// �����ƣ�������һ���׶�
			engine.gameState = AhaGameState.STATE_DISCARD;
			
			return drawCard;
		}
		else
		{
			Log.w("StockPile����", "û�ҵ�Suit:" + targetCard.suit + "Rank:" + targetCard.rank + "in StockPile");
			return null;
		}
	}
	
	/**
	 * ��ȡ�����ĵ�һ����
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
	 * ���������ĵ�һ����
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
	 * ��ȡ���ƶ�����
	 */
	public ArrayList<Card> GetCards()
	{
		return stockCards;
	}
	// -------- ��������� --------
	

	/**
	 * ����δ���Ƶ��ƶѡ�
	 * <p>����ƶ��л���ʣ�ƣ�����Ŀ����������Ʊ�һ�š�����ƶ���û���ˣ��򲻻��ơ�
	 * @param canvas ���Ի��Ƶ�Canvas
	 * @param ElapsedTime ������һ֡��������ʱ��
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
