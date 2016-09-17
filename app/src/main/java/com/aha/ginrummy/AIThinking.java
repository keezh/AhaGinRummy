package com.aha.ginrummy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import android.util.Log;

// ���Խ���˼�����̣߳�����AI�������˼���ķ�������
public class AIThinking extends Thread
{
    // -------- ��Ա���� --------

	/**
	 * �������洫�ݶ�����Card��������ֵ������Ӧ��SimCard��
	 * <p>���棡���inputCard�Ͻ��޸ģ�Υ���ϳ�ȥ��ʮ��壡
	 */
	Card inputCard;
	
    // ����AI�����´�ľ���˼��ָ����
    int task;

    // ��AIEngine������
    AIEngine aiEngine;
	
    // ������AI
    AI ai;   
   
    // -------- ���캯�� --------

	public AIThinking(int _task, Card _card, AIEngine _aiengine)
	{
        task = _task;
        aiEngine = _aiengine;

        //-----------------------------------
        
        switch(aiEngine.engine.AILevel)
        {
        case 1:
        	ai = new primaryAI();
        	break;
        case 2:
        	ai = new midAI();
        	break;
        case 3:
        	ai = new advAI();
        	break;
        default:
        	ai = new advAI();
        	break;
        }
        ai.aiEngine = aiEngine;
        
        
		ai.gin = aiEngine.gin;
		ai.knock_noProgress = aiEngine.knock_noProgress;
		ai.knock = aiEngine.knock;
		ai.readyKnock = aiEngine.readyKnock;
		
		ai.CAN_KNOCK = aiEngine.CAN_KNOCK;
		ai.KNOCK_IMMI = aiEngine.KNOCK_IMMI;
		ai.KNOCK_NOPROGRESS_SIZE = aiEngine.KNOCK_NOPROGRESS_SIZE;
		
		ai.KNOCK_ROUND = aiEngine.KNOCK_ROUND;
		ai.watchNum = aiEngine.watchNum;
		
		inputCard = _card;
		
		ai.cards = aiEngine.cards;
		
		// ����׵�
		aiEngine.engine.playerHand.CountMiPoints();
		ai.playerMi = aiEngine.engine.playerHand.miPoint;
		
		// ���ƶ�
		ai.stockPile = aiEngine.CreateSimCardsFromNormalCards(aiEngine.engine.stockPile.GetCards());
		
		// ���ƶ�
		ai.discardPile = aiEngine.CreateSimCardsFromNormalCards(aiEngine.engine.discardPile.GetCards());
		
		// ���ֵ�����
		ai.playerHandPile = aiEngine.CreateSimCardsFromNormalCards(aiEngine.engine.playerHand.GetCards());
		
	}
	
	// �������д����
	public void rewriteData()
	{
		// ��д����
		aiEngine.gin = ai.gin;
		aiEngine.knock_noProgress = ai.knock_noProgress;
		aiEngine.knock = ai.knock;
		aiEngine.readyKnock = ai.readyKnock;
	}
	
	@Override
	public void run()
	{
		SimCard simCard = new SimCard(inputCard);
		
		// ���񣺼���������ĵ�һ�����Ƿ���Ҫ
		if(task == AIEngine.TASK_IsDiscardNeed)
		{
			// �����������Ҫ���ͷ��������ƣ����򷵻�null
			SimCard tempCard = draw(simCard);
			
			// ��Ҫ������
			if (tempCard != null)
			{
				aiEngine.result = aiEngine.FindCardBySimCard(tempCard);

			
				aiEngine.aiState = AIEngine.STATE_RESULT_IsDiscardNeed_TRUE;
				
				rewriteData();
				return;
			}
			else
			// ����Ҫ������
			{
				aiEngine.result = null;
				
				aiEngine.aiState = AIEngine.STATE_RESULT_IsDiscardNeed_FALSE;
				
				rewriteData();
				return;
			}
		}
		// ���񣺴ӳ���������һ���ƣ�������Ҫ��������
		else if(task == AIEngine.TASK_DrawFromStockAndDiscard)
		{
			// ��ȡ����������
			SimCard tempCard = discardStock(simCard);
			
			aiEngine.result = aiEngine.FindCardBySimCard(tempCard);;
			
			aiEngine.aiState = AIEngine.STATE_RESULT_DrawFromStockAndDiscard;
			rewriteData();
			return;
		}
		
		Log.w("����", "�Բۣ��㾹Ȼ�������������StartTask��ֵ������˰ɻ쵰��");
	}

	//TODO ������AI.java�ĺ���
	
	//����discard pile�ƣ����طǿ���ΪҪ�����ƣ�����nullΪ���ô���
	public SimCard draw(SimCard card)
	{
		return ai.draw(card);
	}

	
	//�������stock pile����,����Ҫ������
	public SimCard discardStock(SimCard card)
	{
		return ai.discardStock(card);
	}
	
	

}



class AITie
{
	//�������
	ArrayList<SimCard> tier, tied, deadwoods;
	ArrayList<ArrayList<SimCard>> completeSeq;
	ArrayList<ArrayList<SimCard>> completeGro;
	//���ơ�ter��ɢ������ted�ϡ������ʣ�µ�������tier��tied�С�
	public void tie(ArrayList<Card> ter, ArrayList<Card> ted)
	{
		tier = new ArrayList<SimCard>();
		tied = new ArrayList<SimCard>();
		for (int i = 0; i < ter.size(); ++i)
			tier.add(new SimCard(ter.get(i).rank, ter.get(i).suit));
		for (int i = 0; i < ted.size(); ++i)
			tied.add(new SimCard(ted.get(i).rank, ted.get(i).suit));
		
		ArrayList<SimCard> tierArrayList = new ArrayList<SimCard>();
		AITie tierAI = new AITie();
		tierAI.process(tier);
		AITie tiedAI = new AITie();
		tiedAI.process(tied);
		//�õ�tier��ɢ��
		for (int i = 0; i < tierAI.deadwoods.size(); ++i)
			tierArrayList.add(tierAI.deadwoods.get(i));
		//��completeGro
		for (int i = 0; i < tiedAI.completeGro.size(); ++i)
			if (tiedAI.completeGro.get(i).size() == 3)
			for (int j = 0; j < tierArrayList.size(); ++j)
				if (tierArrayList.get(j).rank == tiedAI.completeGro.get(i).get(0).rank)
				{
					tierArrayList.remove(j);
				}
		//��completeSeq
		boolean[][] has = new boolean[14][5];
		for (int i = 1; i <= 13; ++i)
			for (int j = 1; j <= 4; ++j)
				has[i][j] = false;
		for (int i = 0; i < tierArrayList.size(); ++i)
			has[tierArrayList.get(i).rank][tierArrayList.get(i).suit] = true;
		for (int i = 0; i < tiedAI.completeSeq.size(); ++i)
		{
			int up = tiedAI.completeSeq.get(i).get(tiedAI.completeSeq.get(i).size()-1).rank+1;
			int down = tiedAI.completeSeq.get(i).get(0).rank-1;
			while (up < 14 && has[up][tiedAI.completeSeq.get(i).get(0).suit])
			{
				has[up][tiedAI.completeSeq.get(i).get(0).suit] = false;
				++up;
			}
			while (down > 0 && has[down][tiedAI.completeSeq.get(i).get(0).suit])
			{
				has[down][tiedAI.completeSeq.get(i).get(0).suit] = false;
				--down;
			}
		}
/*		//��tier��ɢ�ƺ�tied������һ����
		for (int i = 0; i < tiedAI.completeSeq.size(); ++i)
			for (int j = 0; j < tiedAI.completeSeq.get(i).size(); ++j)
				tierArrayList.add(tiedAI.completeSeq.get(i).get(j));
		AITie ansAi=new AITie();
		ansAi.process(tierArrayList);*/
		//ansListΪtier�����ʣ�µ�ɢ�ơ�
		ArrayList<SimCard> ansList = new ArrayList<SimCard>();
		for (int i = 1; i <= 13; ++i)
			for (int j = 1; j <= 4; ++j)
				if(has[i][j])
					ansList.add(new SimCard(i, j));
		
		for (int i = 0; i < tierAI.deadwoods.size();)
		{
			boolean flag = false;
			for (int j = 0; j < ansList.size(); ++j)
			{
				if (ansList.get(j).rank == tierAI.deadwoods.get(i).rank
						&& ansList.get(j).suit == tierAI.deadwoods.get(i).suit)
				{
					flag = true;
					break;
				}
			}
			if (!flag)
			{
				//����tied�У�����tierAi��ɾ����
				tied.add(tierAI.deadwoods.remove(i));
			}else ++i;
		}
		//�ҳ�tierʣ�µ���������
		tier = new ArrayList<SimCard>();
		for (int i = 0; i < tierAI.deadwoods.size(); ++i)
			tier.add(tierAI.deadwoods.get(i));
		for (int i = 0; i < tierAI.completeGro.size(); ++i)
			for (int j = 0; j < tierAI.completeGro.get(i).size(); ++j)
				tier.add(tierAI.completeGro.get(i).get(j));
		for (int i = 0; i < tierAI.completeSeq.size(); ++i)
			for (int j = 0; j < tierAI.completeSeq.get(i).size(); ++j)
				tier.add(tierAI.completeSeq.get(i).get(j));
	}
	
	/**
	 * ���ؽ��������
	 * @param cards ����
	 * @return 1Ϊ���� 2Ϊ���� 3Ϊ����
	 */
	public static int checkGinType(ArrayList<Card> cards)
	{
		ArrayList<SimCard> ansList = new ArrayList<SimCard>();
		for (int i = 0; i < cards.size(); ++i)
		{
			ansList.add(new SimCard(cards.get(i)));
		}
		AITie aiTie = new AITie();
		aiTie.process(ansList);
		return aiTie.completeSeq.size()+aiTie.completeGro.size();
	}
	
	//�ᴿ��makeSqlGro��ֻ������:completeSeq, completeGro, deadwoods.
	public void process(ArrayList<SimCard> cards)
	{
		Collections.sort(cards);
		int SIZE = cards.size();
		
		completeSeq = new ArrayList<ArrayList<SimCard>>();
		completeGro = new ArrayList<ArrayList<SimCard>>();
		deadwoods = new ArrayList<SimCard>();
		
		boolean[] flags = new boolean[SIZE];	
		boolean[] used = new boolean[SIZE];
		Arrays.fill(used, false);
		
		//�ڶ�������˳��
		//��completeGro
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
		//��completeSeq
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
		//����״̬
		boolean[] usedSave = used;
		ArrayList<ArrayList<SimCard>> completeSeqSave = completeSeq;
		ArrayList<ArrayList<SimCard>> completeGroSave = completeGro;
		
		completeSeq = new ArrayList<ArrayList<SimCard>>();
		completeGro = new ArrayList<ArrayList<SimCard>>();
		used = new boolean[SIZE];
		Arrays.fill(used, false);
		
		//��completeSeq
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
		
		//��completeGro
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

		//partialGro�����completeSeq��break����
		for (int i = 0; i < SIZE;)
		if (!used[i])
		{
			int rank = cards.get(i).rank;
			int nxtidx = i+1;
			while (nxtidx < SIZE && used[nxtidx]) ++nxtidx;
			if (nxtidx == SIZE) break;
			if (cards.get(nxtidx).rank == rank)
			{
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
								used[i] = used[nxtidx] = true;
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
							break outter;
						}
				}
			}
			i = nxtidx;
		}else ++i;
		
		//�Ƚϵ���
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
			//���������ȡ��
			used = usedSave;
			completeSeq = completeSeqSave;
			completeGro = completeGroSave;
		}
		
		
		for (int i = 0; i < SIZE; ++i)
			if (!used[i])
				deadwoods.add(cards.get(i));
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