package com.aha.ginrummy;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

/**
 * ���Ƶ��ƶ�
 * 
 * @author Administrator
 * 
 */
public class DiscardPile
{
	// -------- ��Ա���� --------

	/**
	 * ���������˿�������
	 */
	ArrayList<Card> discards;

	/**
	 * ���������
	 */
	AhaGameEngine engine;

	/**
	 * С���ӣ������ƶ��������ǲ�������
	 */
	Card nodisCard;

	// -------- ���캯�� --------

	public DiscardPile(AhaGameEngine _engine)
	{
		discards = new ArrayList<Card>();
		engine = _engine;
	}

	// -------- �������� --------

	/**
	 * ��ʼ��������
	 * <p>
	 * �����������
	 */
	public void Init()
	{
		discards.clear();
	}

	/**
	 * ��ָ�����Ƽ���������β����������Player���ƣ�����Opponent���ƣ���ʹ���������
	 * 
	 * @param card
	 *            Ҫ��������������
	 */
	public void AddDiscard(Card card)
	{
		if (card != null)
		{
			card.SetTargetPosition(AhaCoord.COORD_DISCARD_X,
					AhaCoord.COORD_DISCARD_Y);
			discards.add(card);

			// �л�����Ļغ�״̬
			engine.gameState = AhaGameState.STATE_DRAWCARD;
			if (engine.mTurnState == AhaTurnState.TURN_PLAYER)
			{
				engine.mTurnState = AhaTurnState.TURN_OPPONENT;
				engine.aiEngine.aiState = AIEngine.STATE_READY;
			} else
			{
				engine.mTurnState = AhaTurnState.TURN_PLAYER;
				// ����Ǿ���ģʽ�����ü�ʱ��
				if(engine.isRushMode)
				{
					engine.rushTimerHint.RecountTimer();
				}
			}

			
			// ��ղ���������
			ClearNodisCard();
		}
	}

	/**
	 * �����ƶѳ��� - ������ - lv1
	 * <p>
	 * ����������������ĩλһ���ƣ�����������֮��
	 * <p>
	 * �����ƻ�ӳ������������Ƴ���
	 * 
	 * @param flyingCard
	 */
	public void DrawCardToFly(FlyingCard flyingCard)
	{
		if (!discards.isEmpty())
		{
			// ��ȡβ������
			int index = discards.size() - 1;
			Card drawCard = discards.get(index);

			// ���ƴ��������Ƴ���������ӵ�������
			discards.remove(index);
			flyingCard.Fly(drawCard);

			Log.w("ץ����-������", "Suit:" + drawCard.suit + " Rank��" + drawCard.rank);

			// �����ȥ���Ƽ��벻�������
			nodisCard = drawCard;

			// �����ƣ�������һ���׶�
			engine.gameState = AhaGameState.STATE_DISCARD;
		}
	}


	/**
	 * 	/**
	 * �����ƶѳ��� - ������ - lv1
	 * <p>
	 * �����ƶѳ鶥�����ƣ�����ֱ�Ӽ�������֮��
	 * <p>
	 * �����ƻ�ӳ������������Ƴ�
	 * 
	 * @param hand Ŀ��������
	 * @return ���ط���ȥ���Ƶ����á�����ĳЩ״���»��õ���
	 */
	public Card DrawCardToHand(Hand hand)
	{
		if (!discards.isEmpty())
		{
			// ��ȡβ������
			int index = discards.size() - 1;
			Card drawCard = discards.get(index);

			// ���ƴ��������Ƴ���������ӵ�����ȥ
			discards.remove(index);
			hand.AddCardAndSort(drawCard);

			drawCard.SetFaceUp(hand);

			Log.w("ץ����-ֱ�ӷ���������", "Suit:" + drawCard.suit + " Rank��"
					+ drawCard.rank);

			// �����ȥ���Ƽ��벻�������
			nodisCard = drawCard;

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
	 * ��ȡ���ƶѵ��˿�������
	 * 
	 * @return �˿�������
	 */
	public int GetCardCount()
	{
		return discards.size();
	}

	/**
	 * ��ȡ���ƶѶ�����һ���˿�
	 * 
	 * @return �������ƶѶ����˿˵�����
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
	 * �жϲ���ָ�������ܷ�����������Ǹ�ץ���ƣ��޷����ơ�
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
		// ������discard��һ������
		else
		{
			return true;
		}

	}

	/**
	 * ��NodisCard�ÿ�
	 */
	public void ClearNodisCard()
	{
		nodisCard = null;
	}

	/**
	 * ��ȡ���ƶѵ�����
	 */
	public ArrayList<Card> GetCards()
	{
		return discards;
	}
	// -------- ��������� --------

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
		// ��û�����ƣ�����ֻ��һ�����Ƶ�����£������������ı�����
		if (discards.size() <= 1)
		{
			canvas.drawBitmap(engine.mDiscardBackBitmap,
					AhaCoord.COORD_DISCARD_X,
					AhaCoord.COORD_DISCARD_Y, testPaint);
		}

		// ���ֻ��һ�����ƣ�ֻ������һ�����ơ�
		// ����������������
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
