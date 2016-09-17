package com.aha.ginrummy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.graphics.Canvas;
import android.util.Log;

/**
 * 用以描述玩家手上的手牌的装置
 * <p>包含扑克牌容器，保存了手上的手牌。
 * <p>装置中含有可以对手上牌进行绘制、排序、出牌摸牌处理等方法
 * @author Firzencode
 *
 */
public class Hand
{
	// -------- 静态常量 --------
	
	/**
	 * 手牌区类型：本地玩家
	 */
	public static final int TYPE_PLAYER = 1;
	
	/**
	 * 手牌区类型：对手
	 */
	public static final int TYPE_OPPONENT = 2;
	
	// -------- 成员对象 --------
	
	/**
	 * 手牌区的扑克牌容器
	 */
	private ArrayList<Card> handCards;
	
	/**
	 * 手牌区的类型标识
	 */
	private int handType;
	
	/**
	 * 引擎的引用
	 */
	private AhaGameEngine engine;
	
	/**
	 * 米点，使用之前请先计算。
	 */
	public int miPoint;
	
	/**
	 * 当前排序类型。
	 */
	public int sortType;
	
	// -------- 构造函数 --------
	
	public Hand(int _type, AhaGameEngine _engine)
	{
		handType = _type;
		handCards = new ArrayList<Card>();
		engine = _engine;
		sortType = AhaHandSortType.SORT_BY_MELT;
	}
	
	// -------- 公共方法 --------
	
	/**
	 * 初始化手牌区
	 * 清空手上的所有牌。
	 * 然后，
	 * 然后就没有了。等着给你发牌吧XDDDD
	 */
	public void Init()
	{
		handCards.clear();
	}
	
	/**
	 * 获取手牌区的类型
	 * @return 返回手牌区的类型
	 */
	public int GetHandType()
	{
		return handType;
	}
	
	/**
	 * 获取手牌区的扑克牌容器
	 * @return 返回手牌区的扑克牌容器的引用
	 */
	public ArrayList<Card> GetCards()
	{
		return handCards;
	}
	
	/**
	 * 获取手牌的数量
	 * @return 扑克牌数量
	 */
	public int GetCardCount()
	{
		return handCards.size();
	}
	
	/**
	 * 将牌添加到手牌区，添加完成之后对牌进行排序
	 * @param card 待添加的牌
	 */
	public void AddCardAndSort(Card card)
	{
		handCards.add(card);
		
		// 添加完一张牌之后，要进行排序。（这里应该采用不同的排序策略）
		//SortBySuit();
		Sort();
	}
	
	/**
	 * 将牌添加到手牌区最末尾，添加完成之后不对牌进行排序
	 * @param card
	 */
	public void AddCardNoSort(Card card)
	{
		handCards.add(card);
	}
	
	/**
	 * 将牌添加到指定的位置（手动排序）
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
	 * 按指定的方式排序
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
	 * 切换到下一种自动排序方式
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
	 * 获取指定坐标的点，相对应的牌的序号（指当前指向的位置的后一张）
	 * @param _x
	 * @param _y
	 * @return
	 */
	public int CheckIndex(int _x, int _y)
	{
		int index;
		if (_y < AhaCoord.COORD_CARD_Y_PLAYER - AhaSize.SIZE_CARD_HEIGHT)
		{
			// 超出可排序范围
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
	 * 将手牌按照花色排序。花色相同的按照牌面值排序。
	 * （目前是黑红花片【0~max】的顺序）
	 */
	public void SortBySuit()
	{		
		Collections.sort(handCards);
		Collections.reverse(handCards);
		
	}
	
	/**
	 * 将手牌按照牌面值大小排序，大小相同的牌按照花色排序。
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
	 * 将手牌按照牌规则分组排序。
	 */
	public void SortByMelt()
	{
		Log.w("SortByMelt","run");
		ArrayList<SimCard> tempSimCardList = new ArrayList<SimCard>();
		for (int i = 0; i < handCards.size(); ++i)
			tempSimCardList.add(new SimCard(handCards.get(i).rank, handCards.get(i).suit));
		
		AITie aiTie = new AITie();
		aiTie.process(tempSimCardList);	
		
		//按照completeSeq,completeGrp,deadwood的顺序由左到右排序
		int index=0;
		
		//先加上顺子
		for(int i=aiTie.completeSeq.size()-1;i>=0;i--)
		{
			for(int j=aiTie.completeSeq.get(i).size()-1;j>=0;j--)
			{	
				tempSimCardList.set(index, aiTie.completeSeq.get(i).get(j));
				index++;
			}
		}
		
		//再加上对子
		for(int i=aiTie.completeGro.size()-1;i>=0;i--)
		{
			for(int j=aiTie.completeGro.get(i).size()-1;j>=0;j--)
			{	
				tempSimCardList.set(index, aiTie.completeGro.get(i).get(j));
				index++;
			}
		}
		
		//最后是deadwood
		for(int i=aiTie.deadwoods.size()-1;i>=0;i--)
		{	
			tempSimCardList.set(index, aiTie.deadwoods.get(i));
			index++;
		}
		
		RebuildHandCardFromSimCard(tempSimCardList);
	}
	
	/**
	 * 将指定坐标的牌提交给飞牌区
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
			
			
			// 获取需要抽取的牌：第index个
			Card iwannaflyCard = handCards.get(index);
			handCards.remove(index);
			flyingCard.Fly(iwannaflyCard);
			
		}

		
	}
	
	/**
	 * 将指定弃牌丢弃
	 * @param card 打算丢弃的牌
	 * @return 如果这张牌在手牌中，则从容器中删除，并且返回这张牌的引用
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
	 * 扑克定位装置 - lv1
	 * <p>根据当前手牌数量，以及坐标系统的限定，计算handCards中指定序号的card的坐标
	 * <p>简单模式：（7 or 8）
	 * <p>普通模式：（10 or 11）
	 * @param index 指定
	 */
	public void SetCardPosition(int index)
	{
		int y;
		
		// 设置目标Y坐标
		if (handType == Hand.TYPE_PLAYER)
		{
			y = AhaCoord.COORD_CARD_Y_PLAYER;
		}
		else 
		{
			y = AhaCoord.COORD_CARD_Y_OPPONENT;
		}
		
		//handCards.get(index).SetTargetPosition(14 + index * 65, y);
		
		// 简单模式下
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
	 * 作弊专用方法，将手牌全部正面朝上
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
	 * 作弊专用方法，将手牌全部正面朝下
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
	 * 由指定的包含simCard的Arraylist重建手牌
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
	
  	//提纯版makeSqlGro，计算米点。
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
	
	// -------- 更新与绘制 --------
	
	public void Update(long ElapsedTime)
	
	{
		// 遍历手牌位置，并且更新手牌位置
		int i = 0;
		while(i < handCards.size())
		{
			SetCardPosition(i);
			// 更新手牌
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
