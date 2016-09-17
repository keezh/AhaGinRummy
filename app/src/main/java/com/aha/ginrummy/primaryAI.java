package com.aha.ginrummy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * 状态变量<br>
 * public boolean knock;<br>
 * public boolean gin;<br>
 * private boolean readyKnock;<br>
 * private int knock_noProgress;<br>
 * <br>
 * 初级AI默认开关：<br>
 * 	米胡开关 CAN_KNOCK = false;<br>
 *  <10立刻米胡  KNOCK_IMMI = false;<br>
 *	<10(KNOCK_NOPROGRESS_SIZE+1)局没有进展就米胡  KNOCK_NOPROGRESS_SIZE = 4;<br>
 *
 * @author shrimpy
 *
 */
public class primaryAI extends AI{
	//米胡检测,在getDiscard中自动调用
	private void checkKnock()
	{
		if (readyKnock && knock_noProgress >= KNOCK_NOPROGRESS_SIZE)
				knock = true;
		else if (!readyKnock && sumPoint() < 10)
		{
			readyKnock = true;
			if (KNOCK_IMMI)
				knock = true;
		}
	}
	
	//传入discard pile牌，返回非空则为要丢的牌，返回null为不拿此牌
	public SimCard draw(SimCard card)
	{
		int rank = card.rank;
		int suit = card.suit;
		SimCard ansSimCard = null;
		cards.add(card);
		makeSeqGro();
		ansSimCard = getDiscard(card);
		if (ansSimCard.rank == rank && ansSimCard.suit == suit)
		{
			return null;
		}
		else {
			knock_noProgress=0;
			return ansSimCard;
		}
	}
	
	//得到弃牌。draw（）传入刚刚拿到的discard堆里的牌（或假设拿到的牌），拿stock堆牌传null。
	private SimCard getDiscard(SimCard simCard)
	{
		
		SimCard ansSimCard = null;
		try {
			if (deadwoods.size() != 0)
			{
				//如果有刚拿的discard堆里的牌在deadwoods里，直接扔出。
				if (simCard != null)
				for (int i = 0; i < deadwoods.size(); ++i)
					if (simCard.rank == deadwoods.get(i).rank && simCard.suit == deadwoods.get(i).suit)
					{
						ansSimCard = simCard;
						return ansSimCard;
					}
				
				/**
				 * 单牌的丢牌优先级判断：
				 * 
				 * 3、5、7...以及3、56；3、567...；3、55;3、5555的情况，单牌优先级=1。
				 * 
				 * 最后找优先级最小的中的最大牌。
				 */
				int[] level = new int[deadwoods.size()];
				boolean[] used = new boolean[deadwoods.size()];
				Arrays.fill(level, 0);
				Arrays.fill(used, false);

				//优先级=1
				//3\5\7...
				for (int i = 0; i < deadwoods.size(); ++i)
				if (!used[i])
				{
					SimCard card = deadwoods.get(i);
					int up = card.rank+2;
					for (int j = i+1; j < deadwoods.size(); ++j)
					if (!used[j] && deadwoods.get(j).suit == card.suit
						&& deadwoods.get(j).rank == up)
						{
							used[i] = used[j] = true;
							level[i] = level[j] = 1;
							up+=2;
						}
				}
				
				//3\567...;3、56;3、55;3、5555.
				ArrayList<SimCard> ranks = new ArrayList<SimCard>();
				for (int i = 0; i < completeSeq.size(); ++i)
				{
					ranks.add(new SimCard(completeSeq.get(i).get(0).rank-2, completeSeq.get(i).get(0).suit));
					ranks.add(new SimCard(completeSeq.get(i).get(completeSeq.get(i).size()-1).rank+2
							, completeSeq.get(i).get(completeSeq.get(i).size()-1).suit));
				}
				for (int i = 0; i < partialSeq.size(); ++i)
				{
					ranks.add(new SimCard(partialSeq.get(i).get(0).rank-2, partialSeq.get(i).get(0).suit));
					ranks.add(new SimCard(partialSeq.get(i).get(1).rank+2, partialSeq.get(i).get(1).suit));
				}
				for (int i = 0; i < partialGro.size(); ++i)
				{
					ranks.add(new SimCard(partialGro.get(i).get(0).rank-2, partialGro.get(i).get(0).suit));
					ranks.add(new SimCard(partialGro.get(i).get(1).rank+2, partialGro.get(i).get(1).suit));
				}
				for (int i = 0; i < completeGro.size(); ++i)
				if (completeGro.get(i).size() > 3)
				{
					ranks.add(new SimCard(completeGro.get(i).get(0).rank-2, completeGro.get(i).get(0).suit));
					ranks.add(new SimCard(completeGro.get(i).get(completeGro.get(i).size()-1).rank+2
							, completeGro.get(i).get(completeGro.get(i).size()-1).suit));
				}
				
				for (int i = 0; i < deadwoods.size(); ++i)
				if (!used[i])
					for (int j = 0; j < ranks.size(); ++j)
					{
						if (deadwoods.get(i).rank == ranks.get(j).rank
							&& deadwoods.get(i).suit == ranks.get(j).suit)
						{
							level[i] = 1;
							break;
						}
					}
				
				//找最低优先级中最大元素
				int minLevel = 100;
				for (int i = 0; i < deadwoods.size(); ++i)
				{
					if (level[i] < minLevel)
						minLevel = level[i];
				}
				int maxRank = 0;
				int id = -1;
				for (int i = 0; i < deadwoods.size(); ++i)
				if (level[i] == minLevel && deadwoods.get(i).rank > maxRank)
				{
					id = i;
					maxRank = deadwoods.get(i).rank;
				}
				ansSimCard = deadwoods.get(id);
				return ansSimCard;
			}
			
			/**
			 * partial牌的丢牌优先级判断：
			 * 
			 * "*6*7 *9$9","...*5*6*7 *9$9","*7%7 *9%9","7777 99","455中间的5","45555中的4"，优先级=1；
			 * 注意判断若有discard牌且属于最小优先级类别，先扔。
			 * 
			 * 最后找优先级最小的中的最大牌。
			 */
			if (partialGro.size() + partialSeq.size() != 0)
			{
			//所有参与考虑的牌
			boolean[][] flags = new boolean[14][5];
			//属于partial的牌
			boolean[][] nums = new boolean[14][5];
			//优先级
			int[][] lev = new int[14][5];
			
			for (int i = 1; i <= 13; ++i)
				for (int j = 1; j <= 4; ++j)
				{
					flags[i][j] = false;
					nums[i][j] = false;
					lev[i][j] = 0;
				}
			
			//存在455、45555的情况

			for (int i = 0; i < deadwoodsOri.size(); ++i)
			{
				nums[deadwoodsOri.get(i).rank][deadwoodsOri.get(i).suit] = true;
				flags[deadwoodsOri.get(i).rank][deadwoodsOri.get(i).suit] = true;
				for (int j = 0; j < partialGro.size(); ++j)
				{
					//455
					if (deadwoodsOri.get(i).rank+1 == partialGro.get(j).get(0).rank)
					{
						lev[partialGro.get(j).get(0).rank][partialGro.get(j).get(0).suit] = 1;
						break;
					}
					//556
					if (deadwoodsOri.get(i).rank-1 == partialGro.get(j).get(0).rank)
					{
						lev[partialGro.get(j).get(0).rank][partialGro.get(j).get(0).suit] = 1;
						break;
					}
				}
				otter:
				for (int j = 0; j < completeGro.size(); ++j)
					if (completeGro.get(j).size() == 4)
						for (int k = 0; k < 4; ++k)
						{
							//45555
							if (deadwoodsOri.get(i).rank+1 == completeGro.get(j).get(k).rank
									&& deadwoodsOri.get(i).suit == completeGro.get(j).get(k).suit)
							{
								lev[deadwoodsOri.get(i).rank][deadwoodsOri.get(i).suit] = 1;
								break otter;
							}
							//55556
							if (deadwoodsOri.get(i).rank-1 == completeGro.get(j).get(k).rank
									&& deadwoodsOri.get(i).suit == completeGro.get(j).get(k).suit)
							{
								lev[deadwoodsOri.get(i).rank][deadwoodsOri.get(i).suit] = 1;
								break otter;
							}
						}
			}
			//"*6*7 *9$9","...*5*6*7 *9$9","*7%7 *9%9","7777 99"
			for (int i = 0; i < partialOriSeq.size(); ++i)
			{
				nums[partialOriSeq.get(i).get(0).rank][partialOriSeq.get(i).get(0).suit] = true;
				nums[partialOriSeq.get(i).get(1).rank][partialOriSeq.get(i).get(1).suit] = true;
				flags[partialOriSeq.get(i).get(0).rank][partialOriSeq.get(i).get(0).suit] = true;
				flags[partialOriSeq.get(i).get(1).rank][partialOriSeq.get(i).get(1).suit] = true;
			}
			for (int i = 0; i < partialGro.size(); ++i)
			{
				nums[partialGro.get(i).get(0).rank][partialGro.get(i).get(0).suit] = true;
				nums[partialGro.get(i).get(1).rank][partialGro.get(i).get(1).suit] = true;
				flags[partialGro.get(i).get(0).rank][partialGro.get(i).get(0).suit] = true;
				flags[partialGro.get(i).get(1).rank][partialGro.get(i).get(1).suit] = true;
			}
			for (int i = 0; i < completeGro.size(); ++i)
				if (completeGro.get(i).size() == 4)
					for (int j = 0; j < 4; ++j)
					{
						flags[completeGro.get(i).get(j).rank][completeGro.get(i).get(j).suit] = true;
					}
			for (int i = 0; i < completeSeq.size(); ++i)
				for (int j = 0; j < completeSeq.get(i).size(); ++j)
				{
					flags[completeSeq.get(i).get(j).rank][completeSeq.get(i).get(j).suit] = true;
				}
			//start
			for (int i = 1; i <= 13; ++i)
				for (int j = 1; j <=4; ++j)
				if (nums[i][j])
				{
					if (i-2 > 0 && flags[i-2][j])
						lev[i][j] = 1;
					else if (i+2 < 14 && flags[i+2][j])
						lev[i][j] = 1;
				}
			
			//找到最小lev中的最大牌
			int minLevel = 100;
			for (int i = 1; i <= 13; ++i)
				for (int j = 1; j <=4; ++j)
				{
					if (nums[i][j] && lev[i][j] < minLevel)
					{
						minLevel = lev[i][j];
					}
				}
			//discard牌如果在最小优先级中，先丢。
			if (simCard != null)
			{
				if (nums[simCard.rank][simCard.suit] && lev[simCard.rank][simCard.suit] == minLevel)
				{
					ansSimCard = simCard;
					return ansSimCard;
				}
			}
	
			int maxRank = 0;
			int st = 0,i = 0, j = 0;
			for (i = 1; i <= 13; ++i)
				for (j = 1; j <=4; ++j)
					if (nums[i][j] && lev[i][j] == minLevel && i >= maxRank)
					{
						st = j;
						maxRank = i;
					}
			ansSimCard = new SimCard(maxRank, st);
			return ansSimCard;
			}
			
			if (completeSeq.size() != 0)
			{
				int maxRank = 0;
				int idx = -1, rank;
				for (int i = 0; i < completeSeq.size(); i++)
					if (completeSeq.get(i).size() > 3)
					{
						rank = completeSeq.get(i).get(completeSeq.get(i).size()-1).rank;
						if (maxRank < rank)
						{
							maxRank = rank;
							idx = i;
						}
					}
				if (idx != -1)
				{
					ArrayList<SimCard> tmp = completeSeq.get(idx);
					ansSimCard = tmp.get(tmp.size()-1);
					return ansSimCard;
				}
			}
			
			int maxRank = 0;
			int idx = -1, rank;
			for (int i = 0; i < completeGro.size(); i++)
			if(completeGro.get(i).size() > 3) 
			{
				rank = completeGro.get(i).get(0).rank;
				if (maxRank < rank)
				{
					maxRank = rank;
					idx = i;
				}
			}
			ArrayList<SimCard> tmp = completeGro.get(idx);
			ansSimCard = tmp.get(tmp.size()-1);
			return ansSimCard;
			
		} finally{
			for (int i = 0; i < cards.size(); ++i)
				if (ansSimCard.suit == cards.get(i).suit && ansSimCard.rank == cards.get(i).rank)
				{
					cards.remove(i);
					break;
				}
			makeSeqGro();
			testGin();
			if (CAN_KNOCK) checkKnock();
		}
	}
	
	//传入的是stock pile的牌,返回要丢的牌
	public SimCard discardStock(SimCard card)
	{
		cards.add(card);
		makeSeqGro();
		SimCard ansSimCard = getDiscard(null);
		if (ansSimCard.rank == card.rank && ansSimCard.suit == card.suit)
		{
			++knock_noProgress;
		}
		else knock_noProgress=0;
		return ansSimCard;
	}
	
	//初始化
	public void initial(ArrayList<SimCard> c)
	{
		gin = false;
		knock_noProgress = 0;
		knock = false;
		readyKnock = false;
		
		cards = c;
	}
	
	//制造Seq&Gro
	public void makeSeqGro()
	{
		Collections.sort(cards);
		
		int SIZE = cards.size();
		
		completeSeq = new ArrayList<ArrayList<SimCard>>();
		completeGro = new ArrayList<ArrayList<SimCard>>();
		partialSeq = new ArrayList<ArrayList<SimCard>>();
		partialGro = new ArrayList<ArrayList<SimCard>>();
		deadwoods = new ArrayList<SimCard>();
		partialOriSeq = new ArrayList<ArrayList<SimCard>>();
		deadwoodsOri = new ArrayList<SimCard>();
		
		boolean[] flags = new boolean[SIZE];	
		boolean[] used = new boolean[SIZE];
		Arrays.fill(used, false);
		
		//第二种排序顺序
		//找completeGro
		int tmpidx = 0;
		while (tmpidx < SIZE-2)
		if (!used[tmpidx])
		{
			Arrays.fill(flags, false);
			SimCard card = cards.get(tmpidx);
			flags[tmpidx] = true;
			int rank = card.rank;
			int num = 1;
			
			//findNext
			int nxtidx = tmpidx;
			while(true)
			{
				do{
					++nxtidx;
				}while (nxtidx < SIZE && used[nxtidx]);
				if (nxtidx == SIZE) break;
				if (cards.get(nxtidx).rank == rank)
				{
					++num;
					flags[nxtidx] = true;
				}
				else break;
			}
			
			if (num > 2)
			{
				ArrayList<SimCard> tmpList = new ArrayList<SimCard>();
				for (int j = tmpidx; j < SIZE; ++j)
					if(flags[j])
					{
						tmpList.add(cards.get(j));
						used[j] = true;
					}
				completeGro.add(tmpList);
			}
			tmpidx = nxtidx;
		}else ++tmpidx;
		//找completeSeq
		for (int i = 0; i < SIZE; ++i)
		if (!used[i])
		{
			Arrays.fill(flags, false);
			SimCard card = cards.get(i);
			flags[i] = true;
			int suit = card.suit;
			int rankNow = card.rank+1;
			int idx = 1;
			
			for (int j = i+1; j < SIZE; ++j)
			if (!used[j])
			{
				if (suit == cards.get(j).suit && cards.get(j).rank == rankNow)
				{
					flags[j] = true;
					++rankNow;
					++idx;
				}
				else if (cards.get(j).rank > rankNow)
					break;
			}
			if (idx > 2)
			{
				ArrayList<SimCard> tmpList = new ArrayList<SimCard>();
				for (int j = i; j < SIZE; ++j)
					if(flags[j])
					{
						tmpList.add(cards.get(j));
						used[j] = true;
					}
				completeSeq.add(tmpList);
			}
		}
		//保存状态
		boolean[] usedSave = used;
		ArrayList<ArrayList<SimCard>> completeSeqSave = completeSeq;
		ArrayList<ArrayList<SimCard>> completeGroSave = completeGro;
		
		completeSeq = new ArrayList<ArrayList<SimCard>>();
		completeGro = new ArrayList<ArrayList<SimCard>>();
		used = new boolean[SIZE];
		Arrays.fill(used, false);
		
		
		//找completeSeq
		for (int i = 0; i < SIZE; ++i)
		if (!used[i])
		{
			Arrays.fill(flags, false);
			SimCard card = cards.get(i);
			flags[i] = true;
			int suit = card.suit;
			int rankNow = card.rank+1;
			int idx = 1;
			
			for (int j = i+1; j < SIZE; ++j)
			if (!used[j])
			{
				if (suit == cards.get(j).suit && cards.get(j).rank == rankNow)
				{
					flags[j] = true;
					++rankNow;
					++idx;
				}
				else if (cards.get(j).rank > rankNow)
					break;
			}
			if (idx > 2)
			{
				ArrayList<SimCard> tmpList = new ArrayList<SimCard>();
				for (int j = i; j < SIZE; ++j)
					if(flags[j])
					{
						tmpList.add(cards.get(j));
						used[j] = true;
					}
				completeSeq.add(tmpList);
			}
		}
		
		//找completeGro
		tmpidx = 0;
		while (tmpidx < SIZE-2)
		if (!used[tmpidx])
		{
			Arrays.fill(flags, false);
			SimCard card = cards.get(tmpidx);
			flags[tmpidx] = true;
			int rank = card.rank;
			int num = 1;
			
			//findNext
			int nxtidx = tmpidx;
			while(true)
			{
				do{
					++nxtidx;
				}while (nxtidx < SIZE && used[nxtidx]);
				if (nxtidx == SIZE) break;
				if (cards.get(nxtidx).rank == rank)
				{
					++num;
					flags[nxtidx] = true;
				}
				else break;
			}
			
			if (num > 2)
			{
				ArrayList<SimCard> tmpList = new ArrayList<SimCard>();
				for (int j = tmpidx; j < SIZE; ++j)
					if(flags[j])
					{
						tmpList.add(cards.get(j));
						used[j] = true;
					}
				completeGro.add(tmpList);
			}
			tmpidx = nxtidx;
		}else ++tmpidx;
		
		//partialGro及其对completeSeq的break处理
		for (int i = 0; i < SIZE;)
		if (!used[i])
		{
			int rank = cards.get(i).rank;
			int nxtidx = i+1;
			while (nxtidx < SIZE && used[nxtidx]) ++nxtidx;
			if (nxtidx == SIZE) break;
			if (cards.get(nxtidx).rank == rank)
			{
				used[i] = used[nxtidx] = true;
				int j = 0, sz = completeSeq.size();
				outter:
				for (; j < sz; ++j)
				{
					ArrayList<SimCard> tmpListJ = completeSeq.get(j);
					for (int k = 0; k < tmpListJ.size(); ++k)
						if (rank == tmpListJ.get(k).rank)
						{
							int beg = countPoint(tmpListJ, 0, k-1);
							int end = countPoint(tmpListJ, k+1, tmpListJ.size()-1);
							int now = 2*(rank>10?10:rank);
							if (beg + end < now)
							{
								completeSeq.remove(j);
								ArrayList<SimCard> tmpList = new ArrayList<SimCard>();
								tmpList.add(cards.get(i));
								tmpList.add(cards.get(nxtidx));
								tmpList.add(tmpListJ.get(k));
								completeGro.add(tmpList);
								
								ArrayList<SimCard> tmpSeq = new ArrayList<SimCard>();
								if (k-1 - 0 > 1)
								{
									for (int z = 0; z <= k-1; ++z)
										tmpSeq.add(tmpListJ.get(z));
									completeSeq.add(tmpSeq);
								}
								else{
									for (int z = 0; z <= k-1; ++z)
										for (int t = 0; t < SIZE; ++t)
											if (tmpListJ.get(z).suit == cards.get(t).suit
												&& tmpListJ.get(z).rank == cards.get(t).rank)
												{
													used[t] = false;
													break;
												}
								}
								ArrayList<SimCard> tmpSeq2 = new ArrayList<SimCard>();
								if (tmpListJ.size()-1 - k-1 > 1)
								{
									for (int z = k+1; z <= tmpListJ.size()-1; ++z)
										tmpSeq2.add(tmpListJ.get(z));
									completeSeq.add(tmpSeq2);
								}
								else{
									for (int z = k+1; z <= tmpListJ.size()-1; ++z)
										for (int t = 0; t < SIZE; ++t)
											if (tmpListJ.get(z).suit == cards.get(t).suit
											&& tmpListJ.get(z).rank == cards.get(t).rank)
											{
												used[t] = false;
												break;
											}
								}
							}
							else
							{
								ArrayList<SimCard> ttmpArrayList = new ArrayList<SimCard>();
								ttmpArrayList.add(cards.get(i));
								ttmpArrayList.add(cards.get(nxtidx));
								partialGro.add(ttmpArrayList);
							}
							break outter;
						}
				}
				if (j == sz)
				{
					ArrayList<SimCard> tmpArray = new ArrayList<SimCard>();
					tmpArray.add(cards.get(i));
					tmpArray.add(cards.get(nxtidx));
					partialGro.add(tmpArray);
				}
			}
			i = nxtidx;
		}else ++i;
		
		//比较点数
		int num1 = 0, num2 = 0;
		for (int i = 0; i < completeGro.size(); ++i)
			for (int j = 0; j < completeGro.get(i).size(); ++j)
			{
				int tmp = completeGro.get(i).get(j).rank;
				num1 += tmp>10?10:tmp;
			}
		for (int i = 0; i < completeSeq.size(); ++i)
			for (int j = 0; j < completeSeq.get(i).size(); ++j)
			{
				int tmp = completeSeq.get(i).get(j).rank;
				num1 += tmp>10?10:tmp;
			}
		for (int i = 0; i < completeGroSave.size(); ++i)
			for (int j = 0; j < completeGroSave.get(i).size(); ++j)
			{
				int tmp = completeGroSave.get(i).get(j).rank;
				num2 += tmp>10?10:tmp;
			}
		for (int i = 0; i < completeSeqSave.size(); ++i)
			for (int j = 0; j < completeSeqSave.get(i).size(); ++j)
			{
				int tmp = completeSeqSave.get(i).get(j).rank;
				num2 += tmp>10?10:tmp;
			}
		if (num1 < num2)
		{
			//保存的数据取出
			used = usedSave;
			completeSeq = completeSeqSave;
			completeGro = completeGroSave;
			//partialGro
			partialGro = new ArrayList<ArrayList<SimCard>>();
			for (int i = 0; i < SIZE;)
				if (!used[i])
				{
					int rank = cards.get(i).rank;
					int nxtidx = i+1;
					while (nxtidx < SIZE && used[nxtidx]) ++nxtidx;
					if (nxtidx == SIZE) break;
					if (cards.get(nxtidx).rank == rank)
					{
						used[i] = used[nxtidx] = true;
						ArrayList<SimCard> tmpArray = new ArrayList<SimCard>();
						tmpArray.add(cards.get(i));
						tmpArray.add(cards.get(nxtidx));
						partialGro.add(tmpArray);
					}
					i = nxtidx;
				}else ++i;
		}
		
		
		//partialSeq
		for (int i = 0; i < SIZE;)
		if (!used[i])
		{
			int rank = cards.get(i).rank;
			int suit = cards.get(i).suit;
			int nxtidx = i+1;
			while (nxtidx < SIZE && used[nxtidx]) ++nxtidx;
			if (nxtidx == SIZE) break;
			if (cards.get(nxtidx).rank != rank+1 || suit != cards.get(nxtidx).suit)
			{
				i = nxtidx;
				continue;
			}
			used[i] = used[nxtidx] = true;
			ArrayList<SimCard> tmpArray = new ArrayList<SimCard>();
			tmpArray.add(cards.get(i));
			tmpArray.add(cards.get(nxtidx));
			partialSeq.add(tmpArray);
			i = nxtidx+1;
		}else ++i;
		
		//partialOriSeq
		for (int i = 0; i < partialSeq.size(); ++i)
			partialOriSeq.add(partialSeq.get(i));
		
		//deadwoods+4\55;4\5555,并保证4、55中与4同花的5在55的第一个;
		for (int i = 0; i < SIZE; ++i)
		if (!used[i])
		{
			int rank = cards.get(i).rank;
			int suit = cards.get(i).suit;
			//4\55
			for (int j = 0; j < partialGro.size(); ++j)
			{
				if (suit == partialGro.get(j).get(0).suit)
				{
					if (rank+1 == partialGro.get(j).get(0).rank)
					{
						used[i] = true;
						ArrayList<SimCard> tmpList = new ArrayList<SimCard>();
						tmpList.add(cards.get(i));
						tmpList.add(partialGro.get(j).get(0));
						partialSeq.add(tmpList);
						break;
					}else if (rank-1 == partialGro.get(j).get(0).rank)
					{
						used[i] = true;
						ArrayList<SimCard> tmpList = new ArrayList<SimCard>();
						tmpList.add(partialGro.get(j).get(0));
						tmpList.add(cards.get(i));
						partialSeq.add(tmpList);
						break;
					}
				}else if (suit == partialGro.get(j).get(1).suit)
				{
					if (rank+1 == partialGro.get(j).get(1).rank)
					{
						used[i] = true;
						ArrayList<SimCard> tmpList = new ArrayList<SimCard>();
						tmpList.add(cards.get(i));
						tmpList.add(partialGro.get(j).get(1));
						partialSeq.add(tmpList);
						//exchange partialGro
						SimCard sc = partialGro.get(j).get(0);
						partialGro.get(j).set(0, partialGro.get(j).get(1));
						partialGro.get(j).set(1, sc);
						break;
					}else if (rank-1 == partialGro.get(j).get(1).rank)
					{
						used[i] = true;
						ArrayList<SimCard> tmpList = new ArrayList<SimCard>();
						tmpList.add(partialGro.get(j).get(1));
						tmpList.add(cards.get(i));
						partialSeq.add(tmpList);
						//exchange partialGro
						SimCard sc = partialGro.get(j).get(0);
						partialGro.get(j).set(0, partialGro.get(j).get(1));
						partialGro.get(j).set(1, sc);
						break;
					}
				}
			}
			//4\5555
			for (int j = 0; j < completeGro.size(); ++j)
			if (completeGro.size() > 3)
			{
				for (int k = 0; k < completeGro.get(j).size(); ++k)
				{
					if (completeGro.get(j).get(k).suit == suit)
					{
						if (rank+1 == completeGro.get(j).get(k).rank)
						{
							used[i] = true;
							ArrayList<SimCard> tmpList = new ArrayList<SimCard>();
							tmpList.add(cards.get(i));
							tmpList.add(completeGro.get(j).get(k));
							partialSeq.add(tmpList);
							break;
						}else if (rank-1 == completeGro.get(j).get(k).rank)
						{
							used[i] = true;
							ArrayList<SimCard> tmpList = new ArrayList<SimCard>();
							tmpList.add(completeGro.get(j).get(k));
							tmpList.add(cards.get(i));
							partialSeq.add(tmpList);
							break;
						}
					}
				}
			}
			deadwoodsOri.add(cards.get(i));
			if (!used[i])
				deadwoods.add(cards.get(i));
		}
	}
	
}
