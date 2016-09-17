package com.aha.ginrummy;

/**
 * 为了纪念在长久的战争中，SuperMXC对资源载入做出的巨大贡献
 * @author SuperMXC
 *
 */
public class LoadingHero
{	
	public int[] spadeArray;
	public int[] heartArray;
	public int[] clubArray;
	public int[] diamondArray;
	
	public int cardback;
	public int knockback;
	public int discardback;
	
	public int sortButton;
	public int changeSort;
	
	public int aidiscardhint;
	public int aidrawcardhint;
	public int drawcardhint;
	public int discardhint;
	
	public int dealhint;
	public int youwinhint;
	public int youlosehint;
	public int drawhandhint;
	
	public int modeselectbackground;
	public int tutorialmode1;
	public int tutorialmode2;
	public int stagemode1;
	public int stagemode2;
	public int simplemode1;
	public int simplemode2;
	public int standardmode1;
	public int standardmode2;
	public int rushmode1;
	public int rushmode2;
	
	public int infoback;
	
	public LoadingHero()
	{
		// 黑桃 A~K
		
		spadeArray = new int[13];
		
		spadeArray[0] = R.drawable.spade_1;
		spadeArray[1] = R.drawable.spade_2;
		spadeArray[2] = R.drawable.spade_3;
		spadeArray[3] = R.drawable.spade_4;
		spadeArray[4] = R.drawable.spade_5;
		spadeArray[5] = R.drawable.spade_6;
		spadeArray[6] = R.drawable.spade_7;
		spadeArray[7] = R.drawable.spade_8;
		spadeArray[8] = R.drawable.spade_9;
		spadeArray[9] = R.drawable.spade_10;
		spadeArray[10] = R.drawable.spade_11;
		spadeArray[11] = R.drawable.spade_12;
		spadeArray[12] = R.drawable.spade_13;
				
		// 红桃 A~K
		
		heartArray = new int[13];
		
		heartArray[0] = R.drawable.heart_1;
		heartArray[1] = R.drawable.heart_2;
		heartArray[2] = R.drawable.heart_3;
		heartArray[3] = R.drawable.heart_4;
		heartArray[4] = R.drawable.heart_5;
		heartArray[5] = R.drawable.heart_6;
		heartArray[6] = R.drawable.heart_7;
		heartArray[7] = R.drawable.heart_8;
		heartArray[8] = R.drawable.heart_9;
		heartArray[9] = R.drawable.heart_10;
		heartArray[10] = R.drawable.heart_11;
		heartArray[11] = R.drawable.heart_12;
		heartArray[12] = R.drawable.heart_13;
		
		// 草花 A~K
		
		clubArray = new int[13];
		
		clubArray[0] = R.drawable.club_1;
		clubArray[1] = R.drawable.club_2;
		clubArray[2] = R.drawable.club_3;
		clubArray[3] = R.drawable.club_4;
		clubArray[4] = R.drawable.club_5;
		clubArray[5] = R.drawable.club_6;
		clubArray[6] = R.drawable.club_7;
		clubArray[7] = R.drawable.club_8;
		clubArray[8] = R.drawable.club_9;
		clubArray[9] = R.drawable.club_10;
		clubArray[10] = R.drawable.club_11;
		clubArray[11] = R.drawable.club_12;
		clubArray[12] = R.drawable.club_13;
		
		// 方片 A~K
		
		diamondArray = new int[13];
		
		diamondArray[0] = R.drawable.diamond_1;
		diamondArray[1] = R.drawable.diamond_2;
		diamondArray[2] = R.drawable.diamond_3;
		diamondArray[3] = R.drawable.diamond_4;
		diamondArray[4] = R.drawable.diamond_5;
		diamondArray[5] = R.drawable.diamond_6;
		diamondArray[6] = R.drawable.diamond_7;
		diamondArray[7] = R.drawable.diamond_8;
		diamondArray[8] = R.drawable.diamond_9;
		diamondArray[9] = R.drawable.diamond_10;
		diamondArray[10] = R.drawable.diamond_11;
		diamondArray[11] = R.drawable.diamond_12;
		diamondArray[12] = R.drawable.diamond_13;
		
		// 扑克背面
		
		cardback = R.drawable.b_4;
		
		// 弃牌区背景
		
		discardback = R.drawable.discardback;
		
		// 胡牌区背景
		
		knockback = R.drawable.knockback;
		
		// 手动排序按钮
		
		sortButton = R.drawable.sortbutton;
		
		// 切换排序模式
		
		changeSort = R.drawable.changesort;
		
		// 图片提示信息
		aidiscardhint = R.drawable.aidiscardhint;
		aidrawcardhint = R.drawable.aidrawcardhint;
		discardhint = R.drawable.discardhint;
		drawcardhint = R.drawable.drawcardhint;
		
		dealhint = R.drawable.dealhint;
		youwinhint = R.drawable.youwinhint;
		youlosehint = R.drawable.youlosehint;
		drawhandhint = R.drawable.drawhandhint;
		
		
		// 模式选择画面
		modeselectbackground = R.drawable.modeselectbackground;
		
		tutorialmode1 = R.drawable.tutorialmode1;
		tutorialmode2 = R.drawable.tutorialmode2;
		
		stagemode1 = R.drawable.stagemode1;
		stagemode2 = R.drawable.stagemode2;
		
		simplemode1 = R.drawable.simplemode1;
		simplemode2 = R.drawable.simplemode2;
		
		standardmode1 = R.drawable.standardmode1;
		standardmode2 = R.drawable.standardmode2;
		
		rushmode1 = R.drawable.rushmode1;
		rushmode2 = R.drawable.rushmode2;
		
		// 提示板
		infoback = R.drawable.infoboardback;
	}
}
