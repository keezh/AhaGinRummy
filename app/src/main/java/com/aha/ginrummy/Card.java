package com.aha.ginrummy;

import android.content.res.Resources;
import android.graphics.Canvas;
/**
 * 用以描述扑克牌的类型
 * @author AhaGroup
 *
 */
public class Card extends Entity implements Comparable<Card>
{
	/**
	 *  牌的花色 按照黑红花片的顺序 黑桃为1 红桃为2 草花为3 方片为4
	 */
	int suit;
	
	/**
	 *  牌的大小 A为1，2到10的数牌和数值相同，J为11，Q为12，K为13
	 */
	int rank;

	/**
	 * 标记，是否正面朝上，正面朝上的时候，显示牌面的图片，否则显示牌背的图片。
	 */
	boolean isFaceUp;

	/**
	 * 目标X坐标，牌会在Update中向该坐标移动
	 */
	int targetX;
	
	/**
	 * 目标Y坐标，牌会在Update中向该坐标移动
	 */
	int targetY;
	
	/**
	 * x轴方向移动速度，根据target的位置和当前的位置，自动更新
	 */
	int speedX;
	
	/**
	 * y轴方向移动速度，根据target的位置和当前的位置，自动更新
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
		// 当前坐标在目标的左边，向右移动！
		if (mX < targetX)	
		{
			// 向右移动
			mX += speedX;
			
			// 啊咧咧，移过头了
			if (mX > targetX)
			{
				mX = targetX;
			}
		}
		// 当前坐标在目标的右边，向左移动！
		else if(mX > targetX)
		{
			// 向左移动
			mX -= speedX;
			
			// 啊咧咧，移过头了
			if (mX < targetX)
			{
				mX = targetX;
			}
		}
		
		// X轴坐标移动Done.
		
		// 当前坐标在目标的上边，向下移动！
		if (mY < targetY)	
		{
			// 向右移动
			mY += speedY;
			
			// 啊咧咧，移过头了
			if (mY > targetY)
			{
				mY = targetY;
			}
		}
		// 当前坐标在目标的下边，向上移动！
		else if(mY > targetY)
		{
			// 向上移动
			mY -= speedY;
			
			// 啊咧咧，移过头了
			if (mY < targetY)
			{
				mY = targetY;
			}
		}
		
		// Y轴坐标移动Done.
		
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
