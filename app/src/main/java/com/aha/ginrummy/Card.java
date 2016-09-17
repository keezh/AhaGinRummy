package com.aha.ginrummy;

import android.content.res.Resources;
import android.graphics.Canvas;
/**
 * ���������˿��Ƶ�����
 * @author AhaGroup
 *
 */
public class Card extends Entity implements Comparable<Card>
{
	/**
	 *  �ƵĻ�ɫ ���պں컨Ƭ��˳�� ����Ϊ1 ����Ϊ2 �ݻ�Ϊ3 ��ƬΪ4
	 */
	int suit;
	
	/**
	 *  �ƵĴ�С AΪ1��2��10�����ƺ���ֵ��ͬ��JΪ11��QΪ12��KΪ13
	 */
	int rank;

	/**
	 * ��ǣ��Ƿ����泯�ϣ����泯�ϵ�ʱ����ʾ�����ͼƬ��������ʾ�Ʊ���ͼƬ��
	 */
	boolean isFaceUp;

	/**
	 * Ŀ��X���꣬�ƻ���Update����������ƶ�
	 */
	int targetX;
	
	/**
	 * Ŀ��Y���꣬�ƻ���Update����������ƶ�
	 */
	int targetY;
	
	/**
	 * x�᷽���ƶ��ٶȣ�����target��λ�ú͵�ǰ��λ�ã��Զ�����
	 */
	int speedX;
	
	/**
	 * y�᷽���ƶ��ٶȣ�����target��λ�ú͵�ǰ��λ�ã��Զ�����
	 */
	int speedY;
	
	public Card(Resources res, int id, int _mX, int _mY,int _suit, int _rank, AhaGameEngine _engine)
	{
		super(res, id, _mX, _mY, _engine);
		// TODO Auto-generated constructor stub
		isFaceUp = true;
		suit = _suit;
		rank = _rank;
	}
	
	@Override
	public int compareTo(Card another)
	{
		if (suit < another.suit)
		{
			return 1;
		}
		else if (suit > another.suit)
		{
			return -1;
		}
		else 
		{
			if (rank < another.rank)
			{
				return -1;
			}
			else if (rank > another.rank)
			{
				return 1;
			}
			else 
			{
				return 0;
			}
		}
	}
	
	public void SetFaceUp(boolean _isFaceUp)
	{
		isFaceUp = _isFaceUp;
	}
	public void SetFaceUp(Hand hand)
	{
		if (hand.GetHandType() == Hand.TYPE_PLAYER)
		{
			isFaceUp = true;
		}
		else if (hand.GetHandType() == Hand.TYPE_OPPONENT)
		{
			isFaceUp = false;
			//isFaceUp = true;
		}
	}
	
	public boolean IsFaceUp()
	{
		return isFaceUp;
	}
	
	public void SetTargetPosition(int _x, int _y)
	{
		targetX = _x;
		targetY = _y;
		
		double a = Math.abs(targetY - mY);
		double b = Math.abs(targetX - mX);
		double c = Math.sqrt(a * a + b * b);
		
		//double theta = Math.atan2(xWidth, yHeight);
		
		//speedX = (int) (Math.cos(theta) * AhaGameEngine.SPEED_CARD);
		//speedY = (int) (Math.cos(theta) * AhaGameEngine.SPEED_CARD);
		speedX = (int) (b / c * AhaGameEngine.SPEED_CARD);
		speedY = (int) (a / c * AhaGameEngine.SPEED_CARD);
		
	}
	
	@Override
	public void Update(long ElapsedTime)
	{
		// ��ǰ������Ŀ�����ߣ������ƶ���
		if (mX < targetX)	
		{
			// �����ƶ�
			mX += speedX;
			
			// �����֣��ƹ�ͷ��
			if (mX > targetX)
			{
				mX = targetX;
			}
		}
		// ��ǰ������Ŀ����ұߣ������ƶ���
		else if(mX > targetX)
		{
			// �����ƶ�
			mX -= speedX;
			
			// �����֣��ƹ�ͷ��
			if (mX < targetX)
			{
				mX = targetX;
			}
		}
		
		// X�������ƶ�Done.
		
		// ��ǰ������Ŀ����ϱߣ������ƶ���
		if (mY < targetY)	
		{
			// �����ƶ�
			mY += speedY;
			
			// �����֣��ƹ�ͷ��
			if (mY > targetY)
			{
				mY = targetY;
			}
		}
		// ��ǰ������Ŀ����±ߣ������ƶ���
		else if(mY > targetY)
		{
			// �����ƶ�
			mY -= speedY;
			
			// �����֣��ƹ�ͷ��
			if (mY < targetY)
			{
				mY = targetY;
			}
		}
		
		// Y�������ƶ�Done.
		
	}

	@Override
	public void Draw(Canvas canvas, long ElapsedTime)
	{
		if (isFaceUp)
		{
			canvas.drawBitmap(mBitmap,mX,mY,null);
		}
		else 
		{
			canvas.drawBitmap(engine.mCardBackBitmap,mX,mY,null);
		}
		
	}



}
