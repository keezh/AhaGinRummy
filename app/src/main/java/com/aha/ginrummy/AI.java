package com.aha.ginrummy;

import java.util.ArrayList;

/**
 * ����AIĬ�Ͽ��أ�<br>
 * 	�׺����� CAN_KNOCK = false;<br>
 *  <10�����׺�  KNOCK_IMMI = false;<br>
 *	<10(KNOCK_NOPROGRESS_SIZE+1)��û�н�չ���׺�  KNOCK_NOPROGRESS_SIZE = 4;<br>
 *  <br>
 * �м�AIĬ�Ͽ��أ�<br>
 *  �׺����� CAN_KNOCK = false;<br>
 *  <10�����׺�  KNOCK_IMMI = false;<br>
 *  <del>ǰ���پ����׺����׺� KNOCK_ROUND = 13;</del><br>
 *  --��Ҫά����<br>
 *  public ArrayList<SimCard> discardPile;<br>
 *  public ArrayList<SimCard> playerHandPile;<br>
 * <br> 
 * �߼�AIĬ�Ͽ��أ�<br>
 * 	�׺����� CAN_KNOCK = false;<br>
 *  <10�����׺�  KNOCK_IMMI = false;<br>
 *  ���Կ��������� watchNum = 5;<br>
 *  --��Ҫά����<br>
 *  public int playerMi;<br>
 *  public ArrayList<SimCard> stockPile;<br>
 *  public ArrayList<SimCard> discardPile;<br>
 *  public ArrayList<SimCard> playerHandPile;<br>
 *  public AIEngine aiEngine;<br>
 *  <br>
 * �׺�˵����<br>
 *  ����AIKNOCK_NOPROGRESS_SIZE�׺��������׺����ش��׺���<br>
 *  <del>�м�AI��ǰ13�����׺����׺������ڼ�����׺�,</del>�������׺����ش򿪣��м�AI�׺���ɱ,ͬʱ���ǲ���թ�ͣ� <br>
 *  �߼�AI���׵�ȶԷ�Сʱ�׺���ͬʱ���ǲ���ը�ͣ��������׺����ش򿪡�<br>
 * 
 * @author shrimpy
 *
 */
public abstract class AI {
	//����discard pile�ƣ����طǿ���ΪҪ�����ƣ�����nullΪ���ô���
	public abstract SimCard draw(SimCard card);
	//�������stock pile����,����Ҫ������
	public abstract SimCard discardStock(SimCard card);
	
	//�׺�����
	public boolean CAN_KNOCK = false;
	//<10�����׺�
	public boolean KNOCK_IMMI = false;
	//<10(KNOCK_NOPROGRESS_SIZE+1)��û�н�չ���׺�
	public int KNOCK_NOPROGRESS_SIZE = 4;
	//ǰ���پ����׺����׺�
	public int KNOCK_ROUND = 13;
	//��������
	public int watchNum = 4;
	//״̬����
	public boolean knock;
	public boolean gin;
	protected boolean readyKnock;
	protected int knock_noProgress;
	
	//�м�AI��ӵ�ȫ�ֱ���
	public ArrayList<SimCard> discardPile;
	protected boolean[][] exist = new boolean[14][5];
	public ArrayList<SimCard> playerHandPile;
	protected int round;
	protected boolean[][] theyneed = new boolean[14][5];
	
	//�߼�AI���
	public int playerMi;
	
	public ArrayList<SimCard> stockPile;
	
	public ArrayList<ArrayList<SimCard>> completeSeq;
	public ArrayList<ArrayList<SimCard>> completeGro;
	public ArrayList<ArrayList<SimCard>> partialSeq;
	public ArrayList<ArrayList<SimCard>> partialGro;
	public ArrayList<SimCard> deadwoods;
	public ArrayList<SimCard> cards;
	public ArrayList<ArrayList<SimCard>> partialOriSeq;
	public ArrayList<SimCard> deadwoodsOri;
	
	public AIEngine aiEngine;
	
	//���gin,��getDiscard���Զ�����
	protected void testGin()
	{
		if (deadwoodsOri.size()==0 && partialGro.size()==0 && partialOriSeq.size()==0) gin=true;
	}
	
	//ɢ�Ƶ�����
	public int sumPoint()
	{
		int sum = 0, tmp;
		for (int i = 0; i < deadwoodsOri.size(); ++i)
		{
			tmp = deadwoodsOri.get(i).rank;
			sum += tmp>10?10:tmp;
		}
		for (int i = 0; i < partialGro.size(); ++i)
		{
			tmp = partialGro.get(i).get(0).rank;
			sum += (tmp>10?10:tmp)*2;
		}
		for (int i = 0; i < partialOriSeq.size(); ++i)
		{
			tmp = partialOriSeq.get(i).get(0).rank;
			sum += (tmp>10?10:tmp)+(tmp+1>10?10:tmp+1);
		}
		return sum;
	}
	
	//����һ������Seqĳ��Χ��ɢ�Ƶ����͡�������>3����0��
	public static int countPoint(ArrayList<SimCard> seq, int beg, int end)
	{
		if (beg > end || end - beg > 1) return 0;
		int point = 0, tmp;
		for (int i = beg; i <= end; i++)
		{
			tmp = seq.get(i).rank;
			point += (tmp>10?10:tmp);
		}
		return point;
	}
}
