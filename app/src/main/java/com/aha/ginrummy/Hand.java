package com.aha.ginrummy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.graphics.Canvas;
import android.util.Log;

/**
 * ��������������ϵ����Ƶ�װ��
 * <p>�����˿������������������ϵ����ơ�
 * <p>װ���к��п��Զ������ƽ��л��ơ����򡢳������ƴ���ȷ���
 * @author Firzencode
 *
 */
public class Hand
{
	// -------- ��̬���� --------
	
	/**
	 * ���������ͣ��������
	 */
	public static final int TYPE_PLAYER = 1;
	
	/**
	 * ���������ͣ�����
	 */
	public static final int TYPE_OPPONENT = 2;
	
	// -------- ��Ա���� --------
	
	/**
	 * ���������˿�������
	 */
	private ArrayList<Card> handCards;
	
	/**
	 * �����������ͱ�ʶ
	 */
	private int handType;
	
	/**
	 * ���������
	 */
	private AhaGameEngine engine;
	
	/**
	 * �׵㣬ʹ��֮ǰ���ȼ��㡣
	 */
	public int miPoint;
	
	/**
	 * ��ǰ�������͡�
	 */
	public int sortType;
	
	// -------- ���캯�� --------
	
	public Hand(int _type, AhaGameEngine _engine)
	{
		handType = _type;
		handCards = new ArrayList<Card>();
		engine = _engine;
		sortType = AhaHandSortType.SORT_BY_MELT;
	}
	
	// -------- �������� --------
	
	/**
	 * ��ʼ��������
	 * ������ϵ������ơ�
	 * Ȼ��
	 * Ȼ���û���ˡ����Ÿ��㷢�ư�XDDDD
	 */
	public void Init()
	{
		handCards.clear();
	}
	
	/**
	 * ��ȡ������������
	 * @return ����������������
	 */
	public int GetHandType()
	{
		return handType;
	}
	
	/**
	 * ��ȡ���������˿�������
	 * @return �������������˿�������������
	 */
	public ArrayList<Card> GetCards()
	{
		return handCards;
	}
	
	/**
	 * ��ȡ���Ƶ�����
	 * @return �˿�������
	 */
	public int GetCardCount()
	{
		return handCards.size();
	}
	
	/**
	 * ������ӵ���������������֮����ƽ�������
	 * @param card ����ӵ���
	 */
	public void AddCardAndSort(Card card)
	{
		handCards.add(card);
		
		// �����һ����֮��Ҫ�������򡣣�����Ӧ�ò��ò�ͬ��������ԣ�
		//SortBySuit();
		Sort();
	}
	
	/**
	 * ������ӵ���������ĩβ��������֮�󲻶��ƽ�������
	 * @param card
	 */
	public void AddCardNoSort(Card card)
	{
		handCards.add(card);
	}
	
	/**
	 * ������ӵ�ָ����λ�ã��ֶ�����
	 */
	public void AddCardInPosition(Card card)
	{
		int index = CheckIndex(card.mX, card.mY);
		if (index == -1)
		{
			handCards.add(card);
		}
		else 
		{
			handCards.add(index, card);
		}
	}
	
	/**
	 * ��ָ���ķ�ʽ����
	 */
	public void Sort()
	{
		if(sortType == AhaHandSortType.SORT_BY_SUIT)
		{
			SortBySuit();
		}
		else if (sortType == AhaHandSortType.SORT_BY_RANK)
		{
			SortByRank();
		}
		else if (sortType == AhaHandSortType.SORT_BY_MELT)
		{
			SortByMelt();
		}
	}
	
	/**
	 * �л�����һ���Զ�����ʽ
	 */
	public void NextSortType()
	{
		switch(sortType)
		{
		case AhaHandSortType.SORT_BY_SUIT:
			sortType = AhaHandSortType.SORT_BY_RANK;
			break;
		case AhaHandSortType.SORT_BY_RANK:
			sortType = AhaHandSortType.SORT_BY_MELT;
			break;
		case AhaHandSortType.SORT_BY_MELT:
			sortType = AhaHandSortType.SORT_BY_SUIT;
			break;
		}
		Log.w("SortType", "type" + sortType);
	}
	
	/**
	 * ��ȡָ������ĵ㣬���Ӧ���Ƶ���ţ�ָ��ǰָ���λ�õĺ�һ�ţ�
	 * @param _x
	 * @param _y
	 * @return
	 */
	public int CheckIndex(int _x, int _y)
	{
		int index;
		if (_y < AhaCoord.COORD_CARD_Y_PLAYER - AhaSize.SIZE_CARD_HEIGHT)
		{
			// ����������Χ
			return -1;
		}
		else
		{
			int coordX = _x - AhaCoord.COORD_CARD_X_LEFT;
			
			if (coordX < 0)
				return 0;
			
			switch (engine.gameMode)
			{
				case AhaGameMode.GAMEMODE_SIMPLE:
				{
					if ( handCards.size() <= 7 )
					{
						index = ( coordX / AhaCoord.COORD_CARD_X_SPACE_SIMPLE_7 ) + 1; 
					}
					else //if (handCards.size() >= 8)
					{
						index = ( coordX / AhaCoord.COORD_CARD_X_SPACE_SIMPLE_8) + 1;
					}
					if (index < 0)
					{
						index = 0;
					}
					else if (index > handCards.size() - 1)
					{
						//index = handCards.size() - 1;
						index = -1;
					}
					return index;
				}
				case AhaGameMode.GAMEMODE_STANDARD:
				{
					if ( handCards.size() <= 10 )
					{
						index = ( coordX / AhaCoord.COORD_CARD_X_SPACE_STANDARD_10 ) + 1; 
					}
					else //if (handCards.size() >= 10)
					{
						index = ( coordX / AhaCoord.COORD_CARD_X_SPACE_STANDARD_11) + 1;
					}
					if (index < 0)
					{
						index = 0;
					}
					else if (index > handCards.size() - 1)
					{
						//index = handCards.size() - 1;
						index = -1;
					}
					return index;
				}
			}
			return 0;
		}
	}
	
	/**
	 * �����ư��ջ�ɫ���򡣻�ɫ��ͬ�İ�������ֵ����
	 * ��Ŀǰ�Ǻں컨Ƭ��0~max����˳��
	 */
	public void SortBySuit()
	{		
		Collections.sort(handCards);
		Collections.reverse(handCards);
		
	}
	
	/**
	 * �����ư�������ֵ��С���򣬴�С��ͬ���ư��ջ�ɫ����
	 */
	public void SortByRank()
	{
		Collections.sort(handCards, new Comparator<Card>(){
			@Override
			public int compare(Card lhs, Card rhs) {
				if(lhs.rank<rhs.rank)
					return 1;
				else if(lhs.rank>rhs.rank)
					return -1;
				else 
				{
					if(lhs.suit<rhs.suit)
						return -1;
					else 
						return 1;
				}
			}
		});
	}
	
	/**
	 * �����ư����ƹ����������
	 */
	public void SortByMelt()
	{
		Log.w("SortByMelt","run");
		ArrayList<SimCard> tempSimCardList = new ArrayList<SimCard>();
		for (int i = 0; i < handCards.size(); ++i)
			tempSimCardList.add(new SimCard(handCards.get(i).rank, handCards.get(i).suit));
		
		AITie aiTie = new AITie();
		aiTie.process(tempSimCardList);	
		
		//����completeSeq,completeGrp,deadwood��˳������������
		int index=0;
		
		//�ȼ���˳��
		for(int i=aiTie.completeSeq.size()-1;i>=0;i--)
		{
			for(int j=aiTie.completeSeq.get(i).size()-1;j>=0;j--)
			{	
				tempSimCardList.set(index, aiTie.completeSeq.get(i).get(j));
				index++;
			}
		}
		
		//�ټ��϶���
		for(int i=aiTie.completeGro.size()-1;i>=0;i--)
		{
			for(int j=aiTie.completeGro.get(i).size()-1;j>=0;j--)
			{	
				tempSimCardList.set(index, aiTie.completeGro.get(i).get(j));
				index++;
			}
		}
		
		//�����deadwood
		for(int i=aiTie.deadwoods.size()-1;i>=0;i--)
		{	
			tempSimCardList.set(index, aiTie.deadwoods.get(i));
			index++;
		}
		
		RebuildHandCardFromSimCard(tempSimCardList);
	}
	
	/**
	 * ��ָ����������ύ��������
	 */
	public void FlyCard(int x, FlyingCard flyingCard)
	{
		int index;
		if (!handCards.isEmpty())
		{
			switch (engine.gameMode)
			{
			case AhaGameMode.GAMEMODE_SIMPLE:
				if (handCards.size() <= 7)
				{
					index = ( x - AhaCoord.COORD_CARD_X_LEFT ) / AhaCoord.COORD_CARD_X_SPACE_SIMPLE_7;
					Log.w("index", " " + index);
					if (index < 0) 
					{ 
						index = 0; 
					}
					else if (index >= handCards.size())
					{
						index = handCards.size() - 1;
					}
				}
				else 
				{
					index = ( x - AhaCoord.COORD_CARD_X_LEFT ) / AhaCoord.COORD_CARD_X_SPACE_SIMPLE_8;
					if (index < 0) 
					{ 
						index = 0; 
					}
					else if (index >= handCards.size())
					{
						index = handCards.size() - 1;
					}
				}
				break;
			case AhaGameMode.GAMEMODE_STANDARD:
				if (handCards.size() <= 10)
				{
					index = ( x - AhaCoord.COORD_CARD_X_LEFT ) / AhaCoord.COORD_CARD_X_SPACE_STANDARD_10;
					if (index < 0) 
					{ 
						index = 0; 
					}
					else if (index >= handCards.size())
					{
						index = handCards.size() - 1;
					}
				}
				else 
				{
					index = ( x - AhaCoord.COORD_CARD_X_LEFT ) / AhaCoord.COORD_CARD_X_SPACE_STANDARD_11;
					if (index < 0) 
					{ 
						index = 0; 
					}
					else if (index >= handCards.size())
					{
						index = handCards.size() - 1;
					}
				}break;
			default:
				index = 0;
				break;
			}
			
			
			// ��ȡ��Ҫ��ȡ���ƣ���index��
			Card iwannaflyCard = handCards.get(index);
			handCards.remove(index);
			flyingCard.Fly(iwannaflyCard);
			
		}

		
	}
	
	/**
	 * ��ָ�����ƶ���
	 * @param card ���㶪������
	 * @return ����������������У����������ɾ�������ҷ��������Ƶ�����
	 */
	public Card Discard(Card card)
	{
		
		if ( handCards.contains(card) )
		{
			handCards.remove(card);
			card.SetFaceUp(true);
			return card;
		}
		
		return null;
		
	}
	
	/**
	 * �˿˶�λװ�� - lv1
	 * <p>���ݵ�ǰ�����������Լ�����ϵͳ���޶�������handCards��ָ����ŵ�card������
	 * <p>��ģʽ����7 or 8��
	 * <p>��ͨģʽ����10 or 11��
	 * @param index ָ��
	 */
	public void SetCardPosition(int index)
	{
		int y;
		
		// ����Ŀ��Y����
		if (handType == Hand.TYPE_PLAYER)
		{
			y = AhaCoord.COORD_CARD_Y_PLAYER;
		}
		else 
		{
			y = AhaCoord.COORD_CARD_Y_OPPONENT;
		}
		
		//handCards.get(index).SetTargetPosition(14 + index * 65, y);
		
		// ��ģʽ��
		if (engine.gameMode == AhaGameMode.GAMEMODE_SIMPLE)
		{
			if (handCards.size() <= 7)
			{
				handCards.get(index).SetTargetPosition(
						AhaCoord.COORD_CARD_X_LEFT + index * AhaCoord.COORD_CARD_X_SPACE_SIMPLE_7
						, y);
			}
			else if (handCards.size() >= 8)
			{
				handCards.get(index).SetTargetPosition(
						AhaCoord.COORD_CARD_X_LEFT + index * AhaCoord.COORD_CARD_X_SPACE_SIMPLE_8
						, y);
			}
		}
		else if (engine.gameMode == AhaGameMode.GAMEMODE_STANDARD)
		{
			if (handCards.size() <= 10)
			{
				handCards.get(index).SetTargetPosition(
						AhaCoord.COORD_CARD_X_LEFT + index * AhaCoord.COORD_CARD_X_SPACE_STANDARD_10
						, y);
			}
			else if (handCards.size() == 11)
			{
				handCards.get(index).SetTargetPosition(
						AhaCoord.COORD_CARD_X_LEFT + index * AhaCoord.COORD_CARD_X_SPACE_STANDARD_11
						, y);
			}
			else
			{
				handCards.get(index).SetTargetPosition(
						AhaCoord.COORD_CARD_X_LEFT + index * ((AhaCoord.COORD_CARD_X_RIGHT - AhaCoord.COORD_CARD_X_LEFT) / ( handCards.size() - 1 ))
						, y);
			}
		}
	}
	
	/**
	 * ����ר�÷�����������ȫ�����泯��
	 */
	public void CheatAllFaceUp()
	{
		int index = 0;
		while (index < handCards.size())
		{
			handCards.get(index).SetFaceUp(true);
			index++;
		}
	}
	
	/**
	 * ����ר�÷�����������ȫ�����泯��
	 */
	public void CheatAllFaceDown()
	{
		int index = 0;
		while (index < handCards.size())
		{
			handCards.get(index).SetFaceUp(false);
			index++;
		}
	}
	
	/**
	 * ��ָ���İ���simCard��Arraylist�ؽ�����
	 */
	public void RebuildHandCardFromSimCard(ArrayList<SimCard> simcards)
	{
		handCards.clear();
		int i = 0;
		while (i < simcards.size())
		{
			handCards.add(engine.FindCard(simcards.get(i)));
			i++;
		}
		//SortByRank();
		
	}
	
	public Card PopRandomCard()
	{
		if (!handCards.isEmpty())
		{
			int index = (int) (Math.random() * 100 ) % handCards.size();
			Card tempCard = handCards.get(index);
			handCards.remove(tempCard);
			return tempCard;
		}
		else
			return null;
	}
	
  	//�ᴿ��makeSqlGro�������׵㡣
	public void CountMiPoints()
	{
		ArrayList<SimCard> cards = new ArrayList<SimCard>();
		
		int index = 0;
		while (index < handCards.size())
		{
			SimCard tempCard = new SimCard(handCards.get(index));
			cards.add(tempCard);
			index++;
		}
		
		AITie aiTie = new AITie();
		aiTie.process(cards);

		int sum = 0;
		for (int i = 0; i < aiTie.deadwoods.size(); ++i)
			sum += ( aiTie.deadwoods.get(i).rank > 10)? 10:aiTie.deadwoods.get(i).rank;
		miPoint = sum;
	}
	
	// -------- ��������� --------
	
	public void Update(long ElapsedTime)
	
	{
		// ��������λ�ã����Ҹ�������λ��
		int i = 0;
		while(i < handCards.size())
		{
			SetCardPosition(i);
			// ��������
			handCards.get(i).Update(ElapsedTime);
			i++;
		}
	}
	
	public void Draw(Canvas canvas, long ElapsedTime)
	{
		int i = 0;
		while(i < handCards.size())
		{
			handCards.get(i).Draw(canvas, ElapsedTime);
			i++;
		}
	}
}

class AhaHandSortType
{
	public static final int SORT_BY_SUIT = 1;
	public static final int SORT_BY_RANK = 2;
	public static final int SORT_BY_MELT = 3;
}
