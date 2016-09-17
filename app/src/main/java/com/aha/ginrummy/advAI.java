package com.aha.ginrummy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * ×´Ì¬±äÁ¿<br>
 *	public boolean knock;<br>
 *	public boolean gin;<br>
 *	private boolean readyKnock;<br>
 *  <br>
 * ¸ß¼¶AIÄ¬ÈÏ¿ª¹Ø£º<br>
 * 	Ã×ºú¿ª¹Ø CAN_KNOCK = false;<br>
 *  <10Á¢¿ÌÃ×ºú  KNOCK_IMMI = false;<br>
 *  ¿ÉÒÔ¿´µ½µÄÅÆÊý watchNum = 4;<br>
 *  --ÐèÒªÎ¬»¤£º<br>
 *  public int playerMi;
 *  public ArrayList<SimCard> stockPile;
 *  public ArrayList<SimCard> discardPile;<br>
 *  public ArrayList<SimCard> playerHandPile;<br>
 *  <br>
 *  
 * @author shrimpy<br><br>
 * 
 * ¸ß¼¶AIÐÂÔöÈ«¾Ö±äÁ¿<br>
 *	public int playerMi;<br>
 *	public int watchNum = 4;<br>
 *	public ArrayList<SimCard> stockPile;<br>
 * ¸ß¼¶AIÊ¹ÓÃµÄÖÐ¼¶AIÌí¼ÓµÄÈ«¾Ö±äÁ¿<br>
 *	public ArrayList<SimCard> discardPile;<br>
 *	private boolean[][] exist = new boolean[14][5];<br>
 *	public ArrayList<SimCard> playerHandPile;<br>
 *	private boolean[][] theyneed = new boolean[14][5];<br>
 *
 */
public class advAI extends AI{
	private SimCard newGetDiscard(SimCard simCard)
	{
		//½«µÚÒ»ÕÅdiscardÅÆÈ¥µô
		if (simCard != null)
		{
			exist[discardPile.get(discardPile.size()-1).rank][discardPile.get(discardPile.size()-1).suit] = true;
		}
		//³õÊ¼»¯¶Ô·½ÐèÇó
		theyNeed();
		
		SimCard ansCard = null;
		try{
			
			//ÕÒ¼È²»ÄÜ³ÉcompleteSeqÓÖ²»ÄÜ³ÉcompleteGroµÄÅÆ
			ArrayList<SimCard> feiList = new ArrayList<SimCard>();
			ArrayList<SimCard> deadList = new ArrayList<SimCard>();
			for (int i = 0; i < deadwoodsOri.size(); ++i)
				feiList.add(deadwoodsOri.get(i));
			for (int i = 0; i < partialOriSeq.size(); ++i)
				for (int j = 0; j < partialOriSeq.get(i).size(); ++j)
					feiList.add(partialOriSeq.get(i).get(j));
			for (int i = 0; i < partialGro.size(); ++i)
				for (int j = 0; j < partialGro.get(i).size(); ++j)
					feiList.add(partialGro.get(i).get(j));
			
			for (int i = 0; i < feiList.size(); ++i)
			{
				int rank = feiList.get(i).rank;
				int suit = feiList.get(i).suit;
				int rankup = rank+1;
				int rankdown = rank-1;
				while (rankup < 14 && exist[rankup][suit])++rankup;
				while (rankdown > 0 && exist[rankdown][suit])--rankdown;
				//²»ÄÜ³ÉcompleteSeq
				if (rankup-1 - (rankdown+1) < 2)
				{
					int num = 0;
					for (int j = 1; j <=4; ++j)
						if (exist[rank][j])
							++num;
					//²»ÄÜ³ÉcompleteGro
					if (num < 3)
					{
						deadList.add(feiList.get(i));
					}
				}
			}
			if (deadList.size() != 0)
			{
				//Èç¹ûdiscardÔÚdeadlistÖÐ£¬ÓÅÏÈÈÓµô¡£
				if (simCard != null)
				for (int i = 0; i < deadList.size(); ++i)
				{
					if (simCard.rank == deadList.get(i).rank && simCard.suit == deadList.get(i).suit)
					{
						ansCard = simCard;
						//System.out.println("IT WORKS!!!!!!!!!!!!!!!!!!!discardFirst"+simCard.rank);
						return ansCard;
					}
				}
				//ÕÒdeadlistÖÐ×î´óµÄÅÆÈÓµô
				int maxrank = 0, idx = 0;
				for (int i = 0; i < deadList.size(); ++i)
					if (deadList.get(i).rank > maxrank)
					{
						maxrank = deadList.get(i).rank;
						idx = i;
					}
				//System.out.println("IT WORKS!!!!!!!!!!!!!!!!!!!dropFromDeadlist"+deadList.get(idx).rank);
				ansCard = deadList.get(idx);
				return ansCard;
			}
			//Õý³£´¦Àí
			ansCard = getDiscard(simCard);
			return ansCard;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			//·µ»Ø·ÏÅÆÇ°µÄ´¦Àí
			//System.out.println((ansCard==null)+" "+ansCard);
			for (int i = 0; i < cards.size(); ++i)
				if (ansCard.suit == cards.get(i).suit && ansCard.rank == cards.get(i).rank)
				{
					cards.remove(i);
					break;
				}
			makeSeqGro();
			testGin();
			if (CAN_KNOCK) checkKnock();
		}
		return ansCard;
		
	}
	
	
	//Ã×ºú¼ì²â,ÔÚgetDiscardÖÐ×Ô¶¯µ÷ÓÃ
	private void checkKnock()
	{
		if (readyKnock && sumPoint() < playerMi)
		{
			//ÅÐ¶Ï²»»á±»Õ©ºÍ
			AITie aiTie = new AITie();
			ArrayList<Card> tmpCards = new ArrayList<Card>();
			for (int i = 0; i < cards.size(); ++i)
				tmpCards.add(aiEngine.FindCardBySimCard(cards.get(i)));
			aiTie.tie(aiEngine.engine.playerHand.GetCards(), tmpCards);
			int num1 = 0, num2 = 0;
			AITie tmp1 = new AITie();
			tmp1.process(aiTie.tier);
			AITie tmp2 = new AITie();
			tmp2.process(aiTie.tied);
			for (int i = 0; i < tmp1.deadwoods.size(); ++i)
				num1 += tmp1.deadwoods.get(i).rank>10?10:tmp1.deadwoods.get(i).rank;
			for (int i = 0; i < tmp2.deadwoods.size(); ++i)
				num2 += tmp2.deadwoods.get(i).rank>10?10:tmp2.deadwoods.get(i).rank;
			if (num1 > num2)
				knock = true;
		}
		else if (!readyKnock && sumPoint() < 10)
		{
			readyKnock = true;
			if (KNOCK_IMMI)
				knock = true;
		}
	}
	
	//´«Èëdiscard pileÅÆ£¬·µ»Ø·Ç¿ÕÔòÎªÒª¶ªµÄÅÆ£¬·µ»ØnullÎª²»ÄÃ´ËÅÆ
	public SimCard draw(SimCard card)
	{
//		++round;
		int rank = card.rank;
		int suit = card.suit;
		SimCard ansSimCard = null;
		cards.add(card);
		makeSeqGro();
		ansSimCard = newGetDiscard(card);
		if (ansSimCard.rank == rank && ansSimCard.suit == suit)
		{
			exist[rank][suit] = false;
			return null;
		}
		else {
		//	knock_noProgress=0;
			return ansSimCard;
		}
	}
	
	//¼òµ¥ÅÐ¶Ï¶ÔÊÖÐèÒªµÄÅÆ
	private void theyNeed()
	{
		for (int i = 1; i <= 13; ++i)
			for(int j = 1; j <= 4; ++j)
				theyneed[i][j] = false;
		primaryAI tmpAi = new primaryAI();
		tmpAi.cards = playerHandPile;
		tmpAi.makeSeqGro();
		for (int i = 0; i < tmpAi.partialGro.size(); ++i)
		{
			for (int j = 1; j <= 4 ; ++j)
				if (j != tmpAi.partialGro.get(i).get(0).suit && j != tmpAi.partialGro.get(i).get(1).suit)
					theyneed[tmpAi.partialGro.get(i).get(0).rank][j] = true;
		}
		for (int i = 0; i < tmpAi.partialSeq.size(); ++i)
		{
			if (tmpAi.partialSeq.get(i).get(0).rank - 1 > 0)
				theyneed[tmpAi.partialSeq.get(i).get(0).rank-1][tmpAi.partialSeq.get(i).get(0).suit] = true;
			if (tmpAi.partialSeq.get(i).get(1).rank + 1 < 14)
				theyneed[tmpAi.partialSeq.get(i).get(1).rank+1][tmpAi.partialSeq.get(i).get(1).suit] = true;
		}
		for (int i = 0; i < tmpAi.completeGro.size(); ++i)
		if (tmpAi.completeGro.size() == 3)
		{
			int k = 1;
			for (; k <= 4; ++k)
			{
				int j = 0;
				for (; j < 3; ++j)
					if (tmpAi.completeGro.get(i).get(j).suit == k)
						break;
				if (j == 3)
					break;
			}
			theyneed[tmpAi.completeGro.get(i).get(0).rank][k] = true;
		}
		for (int i = 0; i < tmpAi.completeSeq.size(); ++i)
		{
			if (tmpAi.completeSeq.get(i).get(0).rank - 1 > 0)
				theyneed[tmpAi.completeSeq.get(i).get(0).rank -1][tmpAi.completeSeq.get(i).get(0).suit] = true;
			if (tmpAi.completeSeq.get(i).get(tmpAi.completeSeq.get(i).size()-1).rank + 1 < 14)
				theyneed[tmpAi.completeSeq.get(i).get(tmpAi.completeSeq.get(i).size()-1).rank + 1][tmpAi.completeSeq.get(i).get(0).suit] = true;
		}
	}
	
	//µÃµ½ÆúÅÆ¡£draw£¨£©´«Èë¸Õ¸ÕÄÃµ½µÄdiscard¶ÑÀïµÄÅÆ£¨»ò¼ÙÉèÄÃµ½µÄÅÆ£©£¬ÄÃstock¶ÑÅÆ´«null¡£
	private SimCard getDiscard(SimCard simCard)
	{
		
		SimCard ansSimCard = null;
		//try {
			if (deadwoods.size() != 0)
			{
				//Èç¹ûÓÐ¸ÕÄÃµÄdiscard¶ÑÀïµÄÅÆÔÚdeadwoodsÀï£¬Ö±½ÓÈÓ³ö¡£
				if (simCard != null)
				for (int i = 0; i < deadwoods.size(); ++i)
					if (simCard.rank == deadwoods.get(i).rank && simCard.suit == deadwoods.get(i).suit)
					{
						ansSimCard = simCard;
						return ansSimCard;
					}
				
				/**
				 * µ¥ÅÆµÄ¶ªÅÆÓÅÏÈ¼¶ÅÐ¶Ï£º
				 * 
				 * 3¡¢5¡¢7...ÒÔ¼°3¡¢56£»3¡¢567...£»3¡¢55;3¡¢5555µÄÇé¿ö£¬µ¥ÅÆÓÅÏÈ¼¶=2¡£
				 * ÖÐ¼¶AIÌí¼Ó£ºÇÒÖÐ¼äµÄÅÆ´æÔÚ£¡
				 * 
				 * ×îºóÕÒÓÅÏÈ¼¶×îÐ¡µÄÖÐµÄ×î´óÅÆ¡£
				 */
				int[] level = new int[deadwoods.size()];
				boolean[] used = new boolean[deadwoods.size()];
				Arrays.fill(level, 0);
				Arrays.fill(used, false);

				//ÓÅÏÈ¼¶=2
				//3\5\7...
				for (int i = 0; i < deadwoods.size(); ++i)
				if (!used[i])
				{
					SimCard card = deadwoods.get(i);
					int up = card.rank+2;
					for (int j = i+1; j < deadwoods.size(); ++j)
					if (!used[j] && deadwoods.get(j).suit == card.suit
					//²¢ÇÒÖÐ¼äµÄÅÆ´æÔÚ
						&& deadwoods.get(j).rank == up && exist[up-1][card.suit])
						{
							used[i] = used[j] = true;
							level[i] = level[j] = 2;
							up+=2;
						}
				}
				
				//3\567...;3¡¢56;3¡¢55;3¡¢5555.
				//ÇÒÖÐ¼äµÄÅÆ´æÔÚ
				ArrayList<SimCard> ranks = new ArrayList<SimCard>();
				for (int i = 0; i < completeSeq.size(); ++i)
				{
					if (completeSeq.get(i).get(0).rank-2 > 0
						&& exist[completeSeq.get(i).get(0).rank-1][completeSeq.get(i).get(0).suit])
							ranks.add(new SimCard(completeSeq.get(i).get(0).rank-2, completeSeq.get(i).get(0).suit));
					if (completeSeq.get(i).get(completeSeq.get(i).size()-1).rank+2 < 14
						&& exist[completeSeq.get(i).get(completeSeq.get(i).size()-1).rank+1]
								[completeSeq.get(i).get(completeSeq.get(i).size()-1).suit])
						ranks.add(new SimCard(completeSeq.get(i).get(completeSeq.get(i).size()-1).rank+2
								, completeSeq.get(i).get(completeSeq.get(i).size()-1).suit));
				}
				for (int i = 0; i < partialSeq.size(); ++i)
				{
					if (partialSeq.get(i).get(0).rank-2 > 0
						&& exist[partialSeq.get(i).get(0).rank-1][partialSeq.get(i).get(0).suit])
						ranks.add(new SimCard(partialSeq.get(i).get(0).rank-2, partialSeq.get(i).get(0).suit));
					if (partialSeq.get(i).get(1).rank+2 < 14 
						&& exist[partialSeq.get(i).get(1).rank+1][partialSeq.get(i).get(1).suit])
						ranks.add(new SimCard(partialSeq.get(i).get(1).rank+2, partialSeq.get(i).get(1).suit));
				}
				for (int i = 0; i < partialGro.size(); ++i)
				{
					if (partialGro.get(i).get(0).rank-2 > 0
						&& exist[partialGro.get(i).get(0).rank-1][partialGro.get(i).get(0).suit])
						ranks.add(new SimCard(partialGro.get(i).get(0).rank-2, partialGro.get(i).get(0).suit));
					if (partialGro.get(i).get(1).rank+2 < 14
						&& exist[partialGro.get(i).get(1).rank+1][partialGro.get(i).get(1).suit])
						ranks.add(new SimCard(partialGro.get(i).get(1).rank+2, partialGro.get(i).get(1).suit));
				}
				for (int i = 0; i < completeGro.size(); ++i)
				if (completeGro.get(i).size() > 3)
				{
					if (completeGro.get(i).get(0).rank-2 > 0
						&& exist[completeGro.get(i).get(0).rank-1][completeGro.get(i).get(0).suit])
						ranks.add(new SimCard(completeGro.get(i).get(0).rank-2, completeGro.get(i).get(0).suit));
					if (completeGro.get(i).get(completeGro.get(i).size()-1).rank+2 < 14
						&& exist[completeGro.get(i).get(completeGro.get(i).size()-1).rank+1]
								[completeGro.get(i).get(completeGro.get(i).size()-1).suit])
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
							level[i] = 2;
							break;
						}
					}
				
				//¶Ô·½ÐèÒªµÄÅÆÓÅÏÈ¼¶+1
				for (int i = 0; i < deadwoods.size(); ++i)
					if (theyneed[deadwoods.get(i).rank][deadwoods.get(i).suit])
						level[i] += 1;
				
				//ÕÒ×îµÍÓÅÏÈ¼¶ÖÐ×î´óÔªËØ
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
			 * partialÅÆµÄ¶ªÅÆÓÅÏÈ¼¶ÅÐ¶Ï£º
			 * 
			 * "*6*7 *9$9","...*5*6*7 *9$9","*7%7 *9%9","7777 99","455ÖÐ¼äµÄ5","45555ÖÐµÄ4"£¬ÓÅÏÈ¼¶=2£»
			 * ×¢ÒâÅÐ¶ÏÈôÓÐdiscardÅÆÇÒÊôÓÚ×îÐ¡ÓÅÏÈ¼¶Àà±ð£¬ÏÈÈÓ¡£
			 * ÖÐ¼¶Ìí¼Ó£º¸÷ÖÖÖÐ¼äÅÆÊÇ·ñ´æÔÚ
			 * ×îºóÕÒÓÅÏÈ¼¶×îÐ¡µÄÖÐµÄ×î´óÅÆ¡£
			 */
			if (partialGro.size() + partialSeq.size() != 0)
			{
				//ËùÓÐ²ÎÓë¿¼ÂÇµÄÅÆ
				boolean[][] flags = new boolean[14][5];
				//ÊôÓÚpartialµÄÅÆ
				boolean[][] nums = new boolean[14][5];
				//ÓÅÏÈ¼¶
				int[][] lev = new int[14][5];
				
				for (int i = 1; i <= 13; ++i)
					for (int j = 1; j <= 4; ++j)
					{
						flags[i][j] = false;
						nums[i][j] = false;
						lev[i][j] = 0;
					}
				
				//´æÔÚ455¡¢45555µÄÇé¿ö:"455ÖÐ¼äµÄ5","45555ÖÐµÄ4"
				for (int i = 0; i < deadwoodsOri.size(); ++i)
				{
					nums[deadwoodsOri.get(i).rank][deadwoodsOri.get(i).suit] = true;
					flags[deadwoodsOri.get(i).rank][deadwoodsOri.get(i).suit] = true;
					for (int j = 0; j < partialGro.size(); ++j)
					{
						//455
						if (deadwoodsOri.get(i).rank+1 == partialGro.get(j).get(0).rank)
						{
							lev[partialGro.get(j).get(0).rank][partialGro.get(j).get(0).suit] = 2;
							break;
						}
						//556
						if (deadwoodsOri.get(i).rank-1 == partialGro.get(j).get(0).rank)
						{
							lev[partialGro.get(j).get(0).rank][partialGro.get(j).get(0).suit] = 2;
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
									lev[deadwoodsOri.get(i).rank][deadwoodsOri.get(i).suit] = 2;
									break otter;
								}
								//55556
								if (deadwoodsOri.get(i).rank-1 == completeGro.get(j).get(k).rank
										&& deadwoodsOri.get(i).suit == completeGro.get(j).get(k).suit)
								{
									lev[deadwoodsOri.get(i).rank][deadwoodsOri.get(i).suit] = 2;
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
						//ÖÐ¼¶AIÌí¼Ó£ºÇÒÖÐ¼äµÄÅÆ´æÔÚ
						if (i-2 > 0 && exist[i-1][j] && flags[i-2][j])
							lev[i][j] = 2;
						else if (i+2 < 14 && exist[i+1][j] && flags[i+2][j])
							lev[i][j] = 2;
					}
				//¶Ô·½ÐèÒªµÄÅÆÓÅÏÈ¼¶+1
				for (int i = 1; i <= 13; ++i)
					for (int j = 1; j <=4; ++j)
						if (nums[i][j] && theyneed[i][j])
							lev[i][j] += 1;
				//ÕÒµ½×îÐ¡levÖÐµÄ×î´óÅÆ
				int minLevel = 100;
				for (int i = 1; i <= 13; ++i)
					for (int j = 1; j <=4; ++j)
					{
						if (nums[i][j] && lev[i][j] < minLevel)
						{
							minLevel = lev[i][j];
						}
					}
				//discardÅÆÈç¹ûÔÚ×îÐ¡ÓÅÏÈ¼¶ÖÐ£¬ÏÈ¶ª¡£
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
			
//		} finally{
//			for (int i = 0; i < cards.size(); ++i)
//				if (ansSimCard.suit == cards.get(i).suit && ansSimCard.rank == cards.get(i).rank)
//				{
//					cards.remove(i);
//					break;
//				}
//			makeSeqGro();
//			testGin();
//			if (CAN_KNOCK) checkKnock();
//		}
	}
	
	
	//newDiscardStock
	public SimCard discardStock(SimCard noUse)
	{
		makeSeqGro();
		//Ä¿±êstock
		boolean[][] wanted = new boolean[14][5];
		for (int i = 1; i <= 13 ; ++i)
			for (int j = 1; j <= 4 ; ++j)
				wanted[i][j] = false;
		for (int j = 0; j < partialGro.size(); ++j)
		{
			for (int z = 1; z <= 4; ++z)
				if (z != partialGro.get(j).get(0).suit && z != partialGro.get(j).get(1).suit)
					wanted[partialGro.get(j).get(0).rank][z] = true;
		}
		for (int j = 0; j < partialSeq.size(); ++j)
		{
			if (partialSeq.get(j).get(0).rank-1 > 0)
				wanted[partialSeq.get(j).get(0).rank-1][partialSeq.get(j).get(0).suit] = true;
			if (partialSeq.get(j).get(1).rank+1 < 14)
				wanted[partialSeq.get(j).get(1).rank+1][partialSeq.get(j).get(1).suit] = true;
		}
		for (int j = 0; j < completeGro.size(); ++j)
		if (completeGro.get(j).size() == 3)
		{
			for (int z = 1; z <= 4; ++z)
				if (z != completeGro.get(j).get(0).suit && z != completeGro.get(j).get(1).suit
					&& z != completeGro.get(j).get(2).suit)
				{
					wanted[completeGro.get(j).get(0).rank][z] = true;
					break;
				}
		}
		for (int j = 0; j < completeSeq.size(); ++j)
		{
			if (completeSeq.get(j).get(0).rank-1 > 0)
				wanted[completeSeq.get(j).get(0).rank-1][completeSeq.get(j).get(0).suit] = true;
			if (completeSeq.get(j).get(completeSeq.get(j).size()-1).rank+1 < 14)
				wanted[completeSeq.get(j).get(completeSeq.get(j).size()-1).rank+1][completeSeq.get(j).get(0).suit] = true;
		}
		
		SimCard ansSimCard = null;
		for (int i = 0; i < watchNum; ++i)
		{
			if (i > stockPile.size()-1) break;
			SimCard card = stockPile.get(stockPile.size()-1-i);
			if (wanted[card.rank][card.suit])
			{
				cards.add(card);
				makeSeqGro();
				ansSimCard = newGetDiscard(null);
				aiEngine.cheatNeedCard = aiEngine.FindCardBySimCard(card);
				break;
			}
		}
		if (ansSimCard == null)
			return olddiscardStock(noUse);
		return ansSimCard;
	}
	
	//·µ»ØstockPileÒª¶ªµÄÅÆ
	public SimCard olddiscardStock(SimCard noUse)
	{
		SimCard ansSimCard = null;
		SimCard cardTaken = null;
		for (int i = 0; i < watchNum; ++i)
		{
			if (i > stockPile.size()-1) break;
			SimCard card = stockPile.get(stockPile.size()-1-i);
			cards.add(card);
			makeSeqGro();
			ansSimCard = newGetDiscard(null);
			cardTaken = card;
			if (ansSimCard.rank == card.rank && ansSimCard.suit == card.suit)
			{
				continue;
			}
			else break;
		}
		aiEngine.cheatNeedCard = aiEngine.FindCardBySimCard(cardTaken);
		return ansSimCard;
	}
	
	//³õÊ¼»¯
	public void initial(ArrayList<SimCard> c)
	{
		gin = false;
//		knock_noProgress = 0;
		knock = false;
		readyKnock = false;
		
//		round = 0;
		
		cards = c;
	}
	
	//ÖÆÔìSeq&Gro
	public void makeSeqGro()
	{
		//existÊý×é³õÊ¼»¯
		for (int i = 1; i <= 13; ++i)
			for (int j = 1; j <= 4; ++j)
			{
				exist[i][j] = true;
			}
		for (int j = 0; j < discardPile.size(); ++j)
			exist[discardPile.get(j).rank][discardPile.get(j).suit] = false;
		//Íæ¼ÒÊÖÖÐµÄ³É×éÅÆÈ¥µô
		AITie cheat = new AITie();
		cheat.process(playerHandPile);
		ArrayList<ArrayList<SimCard>> playList1 = cheat.completeGro;
		ArrayList<ArrayList<SimCard>> playList2 = cheat.completeSeq;
		for (int j = 0; j < playList1.size(); ++j)
			for (int k = 0; k < playList1.get(j).size(); ++k)
			exist[playList1.get(j).get(k).rank][playList1.get(j).get(k).suit] = false;
		for (int j = 0; j < playList2.size(); ++j)
			for (int k = 0; k < playList2.get(j).size(); ++k)
			exist[playList2.get(j).get(k).rank][playList2.get(j).get(k).suit] = false;

		
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
		
		//µÚ¶þÖÖÅÅÐòË³Ðò
		//ÕÒcompleteGro
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
		//ÕÒcompleteSeq
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
		//±£´æ×´Ì¬
		boolean[] usedSave = used;
		ArrayList<ArrayList<SimCard>> completeSeqSave = completeSeq;
		ArrayList<ArrayList<SimCard>> completeGroSave = completeGro;
		
		completeSeq = new ArrayList<ArrayList<SimCard>>();
		completeGro = new ArrayList<ArrayList<SimCard>>();
		used = new boolean[SIZE];
		Arrays.fill(used, false);
		
		
		//ÕÒcompleteSeq
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
		
		//ÕÒcompleteGro
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
		
		//partialGro¼°Æä¶ÔcompleteSeqµÄbreak´¦Àí
		for (int i = 0; i < SIZE;)
		if (!used[i])
		{
			int rank = cards.get(i).rank;
			int nxtidx = i+1;
			while (nxtidx < SIZE && used[nxtidx]) ++nxtidx;
			if (nxtidx == SIZE) break;
			if (cards.get(nxtidx).rank == rank)
			{
				//ÖÐ¼¶AI£º¶Ô²»ÄÜ³ÉcompleteGroµÄpartialGro½øÐÐ²ð·Ö
				int kk = 1;
				for (; kk <= 4; ++kk)
					if (kk != cards.get(i).suit && kk != cards.get(nxtidx).suit)
					{
						if (exist[cards.get(i).rank][kk])
							break;
					}
				if (kk == 5)
				{
					i = nxtidx;
					continue;
				}
				
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
								ArrayList<SimCard> tmpList = new ArrayList<SimCard>();
								tmpList.add(cards.get(i));
								tmpList.add(cards.get(nxtidx));
								tmpList.add(tmpListJ.get(k));
								
								/**
								 * ÖÐ¼¶AIÌí¼Ó£º¶ÔÒª²ð·ÖµÄÐòÁÐ£¬ÅÐ¶ÏÊÇ·ñÄÜ×é³É4ÕÅÅÆµÄcompleteGro¡£
								 * ÈôÄÜ£¬ÅÐ¶Ï²ð³öµÄpartialSeqÊÇ·ñÄÜ³ÉcompleteSeq,²ð³öµÄdeadwood²»¹ÜËü¡£Èô²»ÄÜ³É£¬²»²ð¡£
								 */
								
								int x = 1;
								for (; x <= 4; ++x)
									if (x != tmpList.get(0).suit && x != tmpList.get(1).suit && x != tmpList.get(2).suit)
										break;
								if (x != 5)
								{
									if (k-1 - 0 == 1)
									{
										if (!(tmpListJ.get(0).rank -1 > 0 && exist[tmpListJ.get(0).rank-1][tmpListJ.get(0).suit]))
											//|| tmpListJ.get(1).rank+1 < 14 && exist[tmpListJ.get(1).rank+1][tmpListJ.get(1).suit]))
											{
												ArrayList<SimCard> ttmpArrayList = new ArrayList<SimCard>();
												ttmpArrayList.add(cards.get(i));
												ttmpArrayList.add(cards.get(nxtidx));
												partialGro.add(ttmpArrayList);
												break outter;
											}
									}
									
									if (tmpListJ.size()-1 - k-1 == 1)
									{
										if (!//(tmpListJ.get(k+1).rank -1 > 0 && exist[tmpListJ.get(k+1).rank-1][tmpListJ.get(k+1).suit] ||
												(tmpListJ.get(tmpListJ.size()-1).rank+1 < 14 && exist[tmpListJ.get(tmpListJ.size()-1).rank+1][tmpListJ.get(tmpListJ.size()-1).suit]))
												{
													ArrayList<SimCard> ttmpArrayList = new ArrayList<SimCard>();
													ttmpArrayList.add(cards.get(i));
													ttmpArrayList.add(cards.get(nxtidx));
													partialGro.add(ttmpArrayList);
													break outter;
												}
									}
								}
								//Ìí¼ÓÍê±Ï
								
								completeSeq.remove(j);
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
		
		//±È½ÏµãÊý
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
			//±£´æµÄÊý¾ÝÈ¡³ö
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
						//ÖÐ¼¶AI£º¶Ô²»ÄÜ³ÉcompleteGroµÄpartialGro½øÐÐ²ð·Ö
						int kk = 1;
						for (; kk <= 4; ++kk)
							if (kk != cards.get(i).suit && kk != cards.get(nxtidx).suit)
							{
								if (exist[cards.get(i).rank][kk])
									break;
							}
						if (kk == 5)
						{
							i = nxtidx;
							continue;
						}
						
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
		for (int i = 0; i < SIZE; ++i)
		if (!used[i])
		{
			int rank = cards.get(i).rank;
			int suit = cards.get(i).suit;
			int nxtidx = i+1;
			for (; nxtidx < SIZE; ++nxtidx)
				if (!used[nxtidx] && cards.get(nxtidx).rank == rank+1 && suit == cards.get(nxtidx).suit)
					break;
			if (nxtidx == SIZE) continue;
			//ÈôpartialSeq²»ÄÜ×é³ÉcompleteSeq£¬ ²ð·Ö
			if (!(cards.get(i).rank - 1 > 0 && exist[cards.get(i).rank-1][cards.get(i).suit]
				|| cards.get(nxtidx).rank + 1 < 14 && exist[cards.get(nxtidx).rank+1][cards.get(nxtidx).suit]))
			{
				continue;
			}
			
			used[i] = used[nxtidx] = true;
			ArrayList<SimCard> tmpArray = new ArrayList<SimCard>();
			tmpArray.add(cards.get(i));
			tmpArray.add(cards.get(nxtidx));
			partialSeq.add(tmpArray);
		};
		
		//partialOriSeq
		for (int i = 0; i < partialSeq.size(); ++i)
			partialOriSeq.add(partialSeq.get(i));
		
		//deadwoods+4\55;4\5555,²¢±£Ö¤4¡¢55ÖÐÓë4Í¬»¨µÄ5ÔÚ55µÄµÚÒ»¸ö;
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
					//ÖÐ¼¶Ìí¼Ó£ºÇÒÓÐ3¡¢6
					if (rank+1 == partialGro.get(j).get(0).rank 
						&& (rank - 1 > 0 && exist[rank-1][suit] || rank + 2 < 14 && exist[rank+2][suit]))
					{
						used[i] = true;
						ArrayList<SimCard> tmpList = new ArrayList<SimCard>();
						tmpList.add(cards.get(i));
						tmpList.add(partialGro.get(j).get(0));
						partialSeq.add(tmpList);
						break;
					}else if (rank-1 == partialGro.get(j).get(0).rank
						&& (rank - 2 > 0 && exist[rank-2][suit] || rank + 1 < 14 && exist[rank+1][suit]))
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
					if (rank+1 == partialGro.get(j).get(1).rank
						&& (rank - 1 > 0 && exist[rank-1][suit] || rank + 2 < 14 && exist[rank+2][suit]))
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
					}else if (rank-1 == partialGro.get(j).get(1).rank
						&& (rank - 2 > 0 && exist[rank-2][suit] || rank + 1 < 14 && exist[rank+1][suit]))
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
						if (rank+1 == completeGro.get(j).get(k).rank
								&& (rank - 1 > 0 && exist[rank-1][suit] || rank + 2 < 14 && exist[rank+2][suit]))
						{
							used[i] = true;
							ArrayList<SimCard> tmpList = new ArrayList<SimCard>();
							tmpList.add(cards.get(i));
							tmpList.add(completeGro.get(j).get(k));
							partialSeq.add(tmpList);
							break;
						}else if (rank-1 == completeGro.get(j).get(k).rank
								&& (rank - 2 > 0 && exist[rank-2][suit] || rank + 1 < 14 && exist[rank+1][suit]))
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
