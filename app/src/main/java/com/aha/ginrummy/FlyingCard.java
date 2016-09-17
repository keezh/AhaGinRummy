package com.aha.ginrummy;

import android.graphics.Canvas;

/**
 * ��ʾ����������϶��ŵ���
 * <p>�����ƿ����Ǵӳ����������������������ϳ���
 * <p>����ǳ��ƽ׶Σ����Է���������
 * <p>��������ƽ׶Σ����Է�������ȥ��������������������Ȼ������ܺ��Ļ���-��
 */
public class FlyingCard
{
	/**
	 * ��ǰ�϶��ŵ���
	 */
	Card flyingCard;
	
	/**
	 * ���������
	 */
	AhaGameEngine engine;
	
	/**
	 * ����FlyingCard
	 * @param _engine ���������
	 */
	public FlyingCard(AhaGameEngine _engine)
	{
		engine = _engine;
	}

	/**
	 * ��ʼ��������϶�����
	 */
	public void Init()
	{
		flyingCard = null;
	}
	
	/**
	 * �϶�Ŀ���˿ˡ�
	 * ��ֽ�Ʒ�һ�����
	 * @param card ��Ҫ���е�Ŀ���˿�
	 */
	public void Fly(Card card)
	{
		
		flyingCard = card;
	}
	
	/**
	 * �˿˽��䡣�����ɷ��������в��ݡ�
	 */
	public Card Land()
	{
		Card tempCard = flyingCard;
		flyingCard = null;
		return tempCard;
	}
	
	/**
	 * ��鵱ǰ�Ƿ����Ʊ��϶���
	 * @return �����Ʊ��϶��ţ��򷵻�true�����򷵻�false��
	 */
	public boolean IsFlying()
	{
		if (flyingCard != null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * ���������Ƿ���������ཻ
	 * @return
	 */
	public boolean IsIntersectWithDiscard()
	{
		return engine.IsRectIntersect(
				flyingCard.mX, flyingCard.mY, flyingCard.GetWidth(), flyingCard.GetHeight(), 
				AhaCoord.COORD_DISCARD_X, AhaCoord.COORD_DISCARD_Y, 
				AhaSize.SIZE_DISCARD_WIDTH, AhaSize.SIZE_DISCARD_HEIGHT);
	}
	
	/**
	 * ���������Ƿ�ͺ������ཻ
	 * @return
	 */
	public boolean IsIntersectWithKnock()
	{
		return engine.IsRectIntersect(
				flyingCard.mX, flyingCard.mY, flyingCard.GetWidth(), flyingCard.GetHeight(), 
				AhaCoord.COORD_KNOCK_X, AhaCoord.COORD_KNOCK_Y, 
				AhaSize.SIZE_KNOCK_WIDTH, AhaSize.SIZE_KNOCK_HEIGHT);
	}
	/**
	 * ͨ�����뵱ǰ������λ�ã�����������Ƶ�����
	 * @param x ��ǰ����λ�õ�x������
	 * @param y ��ǰ����λ�õ�y������
	 */
 	public void SetFlyPosition(int x, int y)
	{
		flyingCard.SetPosition(x - AhaSize.SIZE_CARD_WIDTH / 2, y - AhaSize.SIZE_CARD_HEIGHT);
	}
	
	// -------- ��������� --------
	
	public void Draw(Canvas canvas, long ElapsedTime)
	{
		if(IsFlying())
		{
			flyingCard.Draw(canvas, ElapsedTime);
		}
	}
}
