package com.aha.ginrummy;

import java.util.ArrayList;

/**
 * 初级AI默认开关：<br>
 * 	米胡开关 CAN_KNOCK = false;<br>
 *  <10立刻米胡  KNOCK_IMMI = false;<br>
 *	<10(KNOCK_NOPROGRESS_SIZE+1)局没有进展就米胡  KNOCK_NOPROGRESS_SIZE = 4;<br>
 *  <br>
 * 中级AI默认开关：<br>
 *  米胡开关 CAN_KNOCK = false;<br>
 *  <10立刻米胡  KNOCK_IMMI = false;<br>
 *  <del>前多少局能米胡就米胡 KNOCK_ROUND = 13;</del><br>
 *  --需要维护：<br>
 *  public ArrayList<SimCard> discardPile;<br>
 *  public ArrayList<SimCard> playerHandPile;<br>
 * <br> 
 * 高级AI默认开关：<br>
 * 	米胡开关 CAN_KNOCK = false;<br>
 *  <10立刻米胡  KNOCK_IMMI = false;<br>
 *  可以看到的牌数 watchNum = 5;<br>
 *  --需要维护：<br>
 *  public int playerMi;<br>
 *  public ArrayList<SimCard> stockPile;<br>
 *  public ArrayList<SimCard> discardPile;<br>
 *  public ArrayList<SimCard> playerHandPile;<br>
 *  public AIEngine aiEngine;<br>
 *  <br>
 * 米胡说明：<br>
 *  初级AIKNOCK_NOPROGRESS_SIZE米胡或立即米胡开关打开米胡；<br>
 *  <del>中级AI在前13轮能米胡就米胡，后期坚决不米胡,</del>或立即米胡开关打开；中级AI米胡必杀,同时考虑不被诈和！ <br>
 *  高级AI当米点比对方小时米胡，同时考虑不被炸和，或立即米胡开关打开。<br>
 * 
 * @author shrimpy
 *
 */
public abstract class AI {
	//传入discard pile牌，返回非空则为要丢的牌，返回null为不拿此牌
	public abstract SimCard draw(SimCard card);
	//传入的是stock pile的牌,返回要丢的牌
	public abstract SimCard discardStock(SimCard card);
	
	//米胡开关
	public boolean CAN_KNOCK = false;
	//<10立刻米胡
	public boolean KNOCK_IMMI = false;
	//<10(KNOCK_NOPROGRESS_SIZE+1)局没有进展就米胡
	public int KNOCK_NOPROGRESS_SIZE = 4;
	//前多少局能米胡就米胡
	public int KNOCK_ROUND = 13;
	//看牌张数
	public int watchNum = 4;
	//状态变量
	public boolean knock;
	public boolean gin;
	protected boolean readyKnock;
	protected int knock_noProgress;
	
	//中级AI添加的全局变量
	public ArrayList<SimCard> discardPile;
	protected boolean[][] exist = new boolean[14][5];
	public ArrayList<SimCard> playerHandPile;
	protected int round;
	protected boolean[][] theyneed = new boolean[14][5];
	
	//高级AI添加
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
	
	//检测gin,在getDiscard后自动调用
	protected void testGin()
	{
		if (deadwoodsOri.size()==0 && partialGro.size()==0 && partialOriSeq.size()==0) gin=true;
	}
	
	//散牌点数和
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
	
	//计算一个有序Seq某范围的散牌点数和。（长度>3返回0）
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
