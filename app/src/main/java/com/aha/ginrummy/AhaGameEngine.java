package com.aha.ginrummy;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.R.color;
import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class AhaGameEngine extends Application
{
	long startTime; // 测试帧率
	long endTime;

	/**
	 * 调试模式开关
	 */
	boolean debugMode = false;
	
	/**
	 * AhaView所在的Activity的引用
	 */
	SecondActivity sbActivity;
	
	boolean isUpdateLocked = false;
	
	// -------- 模式选择画面相关 --------
	Bitmap modeSelectBackground;
	AhaButton tutorialModeButton;
	AhaButton stageModeButton;
	AhaButton simpleModeButton;
	AhaButton standardModeButton;
	AhaButton rushModeButton;
	
	// -------- 绘图相关的成员变量 --------

	int mCanvasWidth; // 绘图区域的宽度
	int mCanvasHeight; // 绘图区域的高度

	Entity mBackground; // 背景图片
	Bitmap mCardBackBitmap; // 扑克牌背面的资源图片，在某些特殊情况下会使用到。
	Bitmap mKnockBackBitmap; // 胡牌区背面的资源图片
	Bitmap mDiscardBackBitmap;		// 弃牌区背面的资源图片
	Bitmap mSortButtonBitmap;		// 排序按钮的资源图片
	Bitmap mChangeSortBitmap;		// 改变排序方式按钮的资源图片
	Bitmap story1Back;
	Bitmap story2Back;
	Bitmap story3Back;
	
	// -------- 逻辑控制相关的成员变量 --------

	String playerNameString;
	
	boolean isSoundEffectOn = true;
	int AILevel;
	int currentStage;
	
	String opponentNameString = "嘉嘉萌";
	
	/**
	 * 竞速模式最大等待时间
	 */
	long rushModeTimer = 7000;
	
	/**
	 * 标记当前引擎模式。
	 * <p>AhaEngineMode.NORMAL：通常模式
	 * <p>AhaEngineMode.MODESELECT：游戏模式选择
	 * <p>AhaEngineMode.STORY：剧情显示
	 */
	int engineMode;
	
	/**
	 * 当前游戏状态
	 */
	int gameState;

	/**
	 * 标记，是否为竞速模式
	 */
	boolean isRushMode = false;
	
	/**
	 * 游戏模式
	 */
	int gameMode = AhaGameMode.GAMEMODE_STANDARD;

	/**
	 * 当前回合状态
	 */
	public int mTurnState;

	/**
	 * 一轮中的游戏局数
	 */
	int mMaxHandInRound = 5;

	/**
	 * 当前是游戏第几局
	 */
	int currentHand = 0;
	
	/**
	 * 发牌标记，和发牌时的计时器相关。
	 * <p>
	 * 若为true，表示发牌已经启动
	 * <p>
	 * 若为false，表示发牌尚未启动
	 */
	private boolean mDealStarted;

	/**
	 * 发牌完成标记，和发牌线程相关
	 * <p>
	 * 若为true，则表示发牌已经完成
	 * <p>
	 * 若为false，则表示发牌尚未完成
	 */
	public boolean mDealHasDone;

	/**
	 * 引擎是否在发完牌之后进行了初始化
	 * <p>
	 * 若为true则完成了初始化
	 * <p>
	 * 若为false则没有进行初始化
	 */
	private boolean mAIEngineHasInitAfterDeal;

	/**
	 * 先手标记
	 * <p>
	 * 若为true，则本地玩家先抽第一张牌。
	 * <p>
	 * 若为false，则对手玩家先抽第一张牌。
	 */
	private boolean mIsPlayerFirstHand;

	/**
	 * 标记这局游戏谁赢了
	 */
	public int mWhoIsWin;
	
	/**
	 * 竞速模式计时器&提示信息
	 */
	public RushTimerHint rushTimerHint;
	
	/**
	 * 标记，本局是否加过分
	 */
	boolean hasAddScore;
	
	/**
	 * 关卡&教学模式标记，即将进行跳转
	 */
	boolean readyToGo;
	
	/**
	 * 目标关卡序号
	 */
	int targetStage;
	
	/**
	 * 计分对话框锁
	 * <br>若为true则表示计分对话框已显示
	 * <br>若为false则表示计分对话框未显示 
	 */
	boolean isScoreDialogShow;
	// -------- 游戏内容相关的成员变量 --------

	/**
	 * 保存所有扑克牌的容器。
	 * <p>
	 * 这个是扑克对象最原始的容器，即使其他地方清空了,也可以通过这个容器找到所需要的扑克对象。
	 * <p>
	 * 通过FindCard函数获取指定的牌。
	 */
	ArrayList<Card> allCards;

	/**
	 * 保存抽牌区牌组的装置，内含扑克牌容器，以及一些交互的方法。
	 */
	StockPile stockPile;

	/**
	 * 保存本地玩家的手牌，内含扑克牌容器，以及一些交互的方法。
	 */
	Hand playerHand;

	/**
	 * 保存对手的手牌，内含扑克牌容器，以及一些交互的方法。
	 */
	Hand opponentHand;

	/**
	 * 保存当前被拖动着的某张扑克
	 * <p>
	 * “是的，我们管它叫【飞牌】！难道这个名字预兆着什么？”
	 */
	FlyingCard flyingCard;

	/**
	 * 保存着废弃的牌，内含扑克牌容器，以及一些交互的方法。
	 */
	DiscardPile discardPile;

	/**
	 * 保存着胡牌，内含一个扑克牌引用，以及一些交互的方法。
	 */
	KnockPile knockPile;
	
	/**
	 * 保存着AI相关的核心算法与控制机制
	 */
	AIEngine aiEngine;

	/**
	 * 保存ai行为相关的某个card的引用。用以防止AI手速过快
	 */
	Card aiTempCard = null;
	
	/**
	 * 提示当前游戏状态的信息
	 */
	StateHint stateHint;
	
	/**
	 * 提示文字信息的提示板
	 */
	TextHint textHint;
	
	/**
	 * 顶部信息提示板
	 */
	InfoBoard infoBoard;
	
	/**
	 * 计分对象
	 */
	ScoreRecord scoreRecord;
	
	/**
	 * 加分对象
	 */
	ScoreItem scoreItem;
	
	/**
	 * 文本显示器
	 */
	StoryHolder storyHolder;
	
	/**
	 * 音乐播放器
	 */
	MusicPlayer musicPlayer;
	
	/**
	 * 文本内容器
	 */
	ArrayList<String> story1;
	ArrayList<String> story2;
	ArrayList<String> story3;
	ArrayList<String> story4;
	
	ArrayList<String> tutorial1;
	ArrayList<String> tutorial2;
	
	// -------- 坐标系统相关的成员变量 --------

	public static final int SPEED_CARD = 50; // 扑克牌的移动速度

	// -------- 【构造函数】 --------

	public AhaGameEngine()
	{
		mCanvasWidth = 0;
		mCanvasHeight = 0;

		// 初始化：超跃迁型位图载入缩放装置。这是载入资源必须的一步
		BitmapFliter.SetMode(BitmapFliter.MODE_800_480,
				BitmapFliter.MODE_800_480);
		
	}

	// -------- 【绘制与更新】 --------

	public void RunInit()
	{

	}

	public void UpdateMain(long ElapsedTime)
	{
		switch(engineMode)
		{
			case AhaEngineMode.NORMAL:
			{
				Update(ElapsedTime);
			}break;
		case AhaEngineMode.MODESELECT:
			{
				UpdateModeSelect(ElapsedTime);
			}break;
		case AhaEngineMode.STAGE:
			{
				UpdateStage(ElapsedTime);
			}break;
		}
	}
	
	public void DrawMain(Canvas canvas, long ElapsedTime)
	{
		switch(engineMode)
		{
			case AhaEngineMode.NORMAL:
			{
				Draw(canvas, ElapsedTime);
			}break;
			case AhaEngineMode.MODESELECT:
			{
				DrawModeSelect(canvas, ElapsedTime);
			}break;
			case AhaEngineMode.STAGE:
			{
				DrawStage(canvas, ElapsedTime);
			}break;
		}
	}
	
	public void Update(long ElapsedTime)
	{
		if (isUpdateLocked)
		{
			return;
		}
		startTime = System.currentTimeMillis();

		// --------------------------

		// 引擎就绪，开始进行轮初始化
		if (gameState == AhaGameState.STATE_READY)
		{
			// 初始化一轮游戏
			InitRound();
			switch(gameMode)
			{
				case AhaGameMode.GAMEMODE_SIMPLE:
				{
					aiEngine.CAN_KNOCK = false;
				}break;
				case AhaGameMode.GAMEMODE_STANDARD:
				{
					aiEngine.CAN_KNOCK = true;
				}
			}
		}

		// 如果轮初始化完毕，开始进行局初始化
		if (gameState == AhaGameState.STATE_INITED_ROUND)
		{
			// 初始化一局游戏
			InitHand();
		}

		// 发牌阶段，给每个玩家发十张牌，确定第一个抽牌的人，然后切换状态
		if (gameState == AhaGameState.STATE_DEALING)
		{
			if (!mDealStarted)
			{
				// 启动发牌任务
				DealCardStart();

				// 标记，发牌任务已经启动
				mDealStarted = true;
			}

		}

		if (mDealHasDone == true && mAIEngineHasInitAfterDeal == false)
		{
			aiEngine.InitAfterDeal(opponentHand.GetCards());
			mAIEngineHasInitAfterDeal = true;
			// 重新计算米点
			playerHand.CountMiPoints();
			//sbActivity.finish();
		}

		// ai思考回路 --------

		if (mTurnState == AhaTurnState.TURN_OPPONENT
				&& (gameState == AhaGameState.STATE_DRAWCARD || gameState == AhaGameState.STATE_DISCARD))
		{
			// 就绪，开始思考并行动
			if (aiEngine.aiState == AIEngine.STATE_READY)
			{
				// 如果弃牌堆有牌，需要先考虑是否需要这张牌
				if (discardPile.GetCardCount() != 0)
				{
					aiEngine.StartTask(AIEngine.TASK_IsDiscardNeed,
							discardPile.GetTopCard());
				}
				// 如果弃牌堆无牌，即就是“不需要弃牌”，直接跳转状态
				else
				{
					aiEngine.aiState = AIEngine.STATE_RESULT_IsDiscardNeed_FALSE;
				}
			} else if (aiEngine.aiState == AIEngine.STATE_THINKING_IsDiscardNeed)
			{
				// 思考中，what can i do？
			} else if (aiEngine.aiState == AIEngine.STATE_RESULT_IsDiscardNeed_TRUE)
			{
				// 需要弃牌，并且result中已经保存了需要弃的牌，what can i do？
			} else if (aiEngine.aiState == AIEngine.STATE_RESULT_IsDiscardNeed_FALSE)
			{
				// 摸顶部的牌，并且在result中返回弃的牌
				aiEngine.StartTask(AIEngine.TASK_DrawFromStockAndDiscard,
						stockPile.GetTopCard());
			} else if (aiEngine.aiState == AIEngine.STATE_THINKING_DrawFromStockAndDiscard)
			{
				// 在result中已经保存了返回了弃牌，what can i do？
			}

		}
		// ai思考回路 done.
		
		// 竞速模式的处理
		if (isRushMode
				&& mTurnState == AhaTurnState.TURN_PLAYER
				&& (gameState == AhaGameState.STATE_DRAWCARD || gameState == AhaGameState.STATE_DISCARD))
		{
			rushTimerHint.Update(ElapsedTime);
			if (rushTimerHint.isTrigger)
			{
				if (gameState == AhaGameState.STATE_DRAWCARD)
				{
					//discardPile.AddDiscard(stockPile.PopTopCard());
					stockPile.DrawCardToHand(playerHand);
					discardPile.AddDiscard(playerHand.PopRandomCard());
				}
				else if (gameState == AhaGameState.STATE_DISCARD)
				{
					if (flyingCard.IsFlying())
					{
						discardPile.AddDiscard(flyingCard.Land());
					}
					else
					{
						discardPile.AddDiscard(playerHand.PopRandomCard());
					}
				}
			}
			
		}
		
		
		
		// 抽牌阶段
		if (gameState == AhaGameState.STATE_DRAWCARD)
		{
			// Player回合的状态的切换在StockPile.DrawCardToFly中进行
			if (mTurnState == AhaTurnState.TURN_PLAYER)
			{

			}

			// Opponent回合的状态
			else if (mTurnState == AhaTurnState.TURN_OPPONENT)
			{
				// 需要这张弃牌，
				if (aiEngine.aiState == AIEngine.STATE_RESULT_IsDiscardNeed_TRUE)
				{
					// 将弃牌区的顶部牌直接发到对手手牌区
					aiTempCard = discardPile.DrawCardToHand(opponentHand);
					stateHint.ChangeTextAndPosition();
				} else if (aiEngine.aiState == AIEngine.STATE_RESULT_DrawFromStockAndDiscard)
				{
					// 将抽牌区的顶部牌直接发到对手手牌区
					if (AILevel == 3)
					{
						aiTempCard = stockPile.DrawTargetCardToHand(opponentHand, aiEngine.cheatNeedCard);
					}
					else
					{
						aiTempCard = stockPile.DrawCardToHand(opponentHand);
					}
					
					stateHint.ChangeTextAndPosition();
				}
			}
		}
		// 弃牌阶段
		if (gameState == AhaGameState.STATE_DISCARD)
		{

			if (mTurnState == AhaTurnState.TURN_PLAYER)
			{
				// 等待玩家对状态进行切换
			} 
			else if (mTurnState == AhaTurnState.TURN_OPPONENT
					&& aiTempCard != null
					&& aiTempCard.mX == aiTempCard.targetX
					&& aiTempCard.mY == aiTempCard.targetY)
			{
				aiTempCard = null;
				// 抓弃牌之后的弃牌（未胡牌）
				if (aiEngine.aiState == AIEngine.STATE_RESULT_IsDiscardNeed_TRUE
						&& !aiEngine.gin && !aiEngine.knock)
				{
					// 弃牌！注意保存！
					discardPile.AddDiscard(opponentHand
							.Discard(aiEngine.result));
					aiEngine.aiState = AIEngine.STATE_DONE;
				}
				// 抓抽牌之后的弃牌（未胡牌）
				else if (aiEngine.aiState == AIEngine.STATE_RESULT_DrawFromStockAndDiscard
						&& !aiEngine.gin && !aiEngine.knock)
				{
					// 弃牌！注意保存！
					discardPile.AddDiscard(opponentHand
							.Discard(aiEngine.result));
					aiEngine.aiState = AIEngine.STATE_DONE;
				}
				// 胜利判断（金胡！）
				else if (aiEngine.gin)
				{
					mWhoIsWin = AhaWinFlag.OPPONENT_WIN;
					gameState = AhaGameState.STATE_END;
					knockPile.SetCardToKnock(opponentHand
							.Discard(aiEngine.result));
					opponentHand.CheatAllFaceUp();
					AddScoreRecord();
					ShowScoreBoardDialog();
					
				}
				// 米胡判断（非简单模式下）
				else if ( gameMode == AhaGameMode.GAMEMODE_STANDARD && aiEngine.knock)
				{
					// 先把要扔的牌扔出去
					knockPile.SetCardToKnock(opponentHand
							.Discard(aiEngine.result));
					
					// 自动贴牌并且计算米点
					AITie aiTie = new AITie();
					aiTie.tie(playerHand.GetCards(), opponentHand.GetCards());
					opponentHand.RebuildHandCardFromSimCard(aiTie.tied);
					opponentHand.CountMiPoints();
					opponentHand.SortByMelt();
					playerHand.RebuildHandCardFromSimCard(aiTie.tier);
					playerHand.CountMiPoints();
					playerHand.SortByMelt();
					
					// 胜利判断
					// 对手米胡
					if (opponentHand.miPoint <= playerHand.miPoint)
					{
						mWhoIsWin = AhaWinFlag.OPPONENT_MI_WIN;
						gameState = AhaGameState.STATE_END;
						opponentHand.CheatAllFaceUp();
						playerHand.CheatAllFaceUp();
						AddScoreRecord();
						ShowScoreBoardDialog();
					}
					// 对手诈和
					else
					{
						mWhoIsWin = AhaWinFlag.OPPONENT_CHEAT_WIN;
						gameState = AhaGameState.STATE_END;
						opponentHand.CheatAllFaceUp();
						playerHand.CheatAllFaceUp();
						AddScoreRecord();
						ShowScoreBoardDialog();
					}
					
				}
				// 和牌判断
				if (!aiEngine.gin && !aiEngine.knock && stockPile.GetCardCount() == 0)
				{
					if (gameMode == AhaGameMode.GAMEMODE_SIMPLE)
					{
						CheckWinIfNoCardWhenSimple();
					}
					else 
					{
						mWhoIsWin = AhaWinFlag.DRAW;
						gameState = AhaGameState.STATE_END;
						opponentHand.CheatAllFaceUp();
						AddScoreRecord();
						ShowScoreBoardDialog();
					}
				}
				stateHint.ChangeTextAndPosition();
			}

		}
		// 结算阶段
		if (gameState == AhaGameState.STATE_END)
		{
			

		}

		playerHand.Update(ElapsedTime);
		opponentHand.Update(ElapsedTime);
		discardPile.Update(ElapsedTime);
		knockPile.Update(ElapsedTime);
		stateHint.Update(ElapsedTime);
		textHint.Update(ElapsedTime);
	}

	public void Draw(Canvas canvas, long ElapsedTime)
	{
		// 绘制背景
		// canvas.drawBitmap(mBackground,0,0,null);
		
		Paint paint = new Paint();
		paint.setARGB(255, 0, 255, 0);
		paint.setTextSize(15);
		// mBackground.draw(canvas);
		Paint testPaint = new Paint();
		testPaint.setARGB(255, 0, 255, 0);
		testPaint.setTextSize(20);
		
		// ===============================
		// 正式绘图
		mBackground.Draw(canvas, ElapsedTime);
		infoBoard.Draw(canvas, ElapsedTime);
		stockPile.Draw(canvas, ElapsedTime);
		canvas.drawBitmap(mSortButtonBitmap, AhaCoord.COORD_SORT_X,AhaCoord.COORD_SORT_Y, null);
		canvas.drawBitmap(mChangeSortBitmap, AhaCoord.COORD_CHANGESORT_X, AhaCoord.COORD_CHANGESORT_Y, null);
		
		textHint.Draw(canvas, ElapsedTime);
		
		playerHand.Draw(canvas, ElapsedTime);
		opponentHand.Draw(canvas, ElapsedTime);

		discardPile.Draw(canvas, ElapsedTime);
		knockPile.Draw(canvas, ElapsedTime);
		flyingCard.Draw(canvas, ElapsedTime);
		
		stateHint.Draw(canvas, ElapsedTime);
		
		if (isRushMode)
		{
			rushTimerHint.Draw(canvas, ElapsedTime);
		}
		// -------------------------
		// 显示数据
		
		if (debugMode)
		{
			// 帧率
			endTime = System.currentTimeMillis();

			canvas.drawText("Update&Draw Time Elapsed:" + (endTime - startTime)
					+ "ms", 10, 10, paint);
			canvas.drawText("Frame Time Elapsed:" + ElapsedTime + "ms", 10, 30,
					paint);

			// 游戏状态
			switch (gameState)
			{
			case AhaGameState.STATE_DEALING:
				canvas.drawText("GameState: 发牌阶段", 10, 50, paint);
				break;
			case AhaGameState.STATE_DRAWCARD:
				canvas.drawText("GameState: 抽牌阶段", 10, 50, paint);
				break;
			case AhaGameState.STATE_DISCARD:
				canvas.drawText("GameState: 弃牌阶段", 10, 50, paint);
			case AhaGameState.STATE_END:
				canvas.drawText("GameState: 结束", 10, 50, paint);
			default:
				canvas.drawText("GameState: ", 10, 50, paint);
				break;
			}

			// 回合状态
			switch (mTurnState)
			{
			case AhaTurnState.TURN_PLAYER:
				canvas.drawText("TurnState： 自己", 10, 70, paint);
				break;
			case AhaTurnState.TURN_OPPONENT:
				canvas.drawText("TurnState： 对手", 10, 70, paint);
			default:
				canvas.drawText("TurnState： ", 10, 70, paint);
				break;
			}

			// ai状态
			switch (aiEngine.aiState)
			{
			case AIEngine.STATE_READY:
				canvas.drawText("AIEngine： SYATE_READY", 10, 90, paint);
				break;
			case AIEngine.STATE_THINKING_IsDiscardNeed:
				canvas.drawText("AIEngine： STATE_THINKING_IsDiscardNeed", 10, 90,
						paint);
				break;
			case AIEngine.STATE_RESULT_IsDiscardNeed_TRUE:
				canvas.drawText("AIEngine： STATE_RESULT_IsDiscardNeed_TRUE", 10,
						90, paint);
				break;
			case AIEngine.STATE_RESULT_IsDiscardNeed_FALSE:
				canvas.drawText("AIEngine： STATE_RESULT_IsDiscardNeed_FALSE", 10,
						90, paint);
				break;
			case AIEngine.STATE_THINKING_DrawFromStockAndDiscard:
				canvas.drawText("AIEngine： STATE_THINKING_DrawFromStockAndDiscard",
						10, 90, paint);
				break;
			case AIEngine.STATE_RESULT_DrawFromStockAndDiscard:
				canvas.drawText("AIEngine： STATE_RESULT_DrawFromStockAndDiscard",
						10, 90, paint);
				break;
			case AIEngine.STATE_DONE:
				canvas.drawText("AIEngine： STATE_DONE", 10, 90, paint);
				break;
			default:
				canvas.drawText("AIEngine： ", 10, 90, paint);
				break;
			}

			// ai思考结果
			if (aiEngine.result != null)
			{
				canvas.drawText("AI Result: suit:" + aiEngine.result.suit
						+ " rank:" + aiEngine.result.rank, 10, 110, paint);

			} else
			{
				canvas.drawText("AI Result: Null", 10, 110, paint);
			}

			// 显示我方米点
			canvas.drawText("我方米点：" + playerHand.miPoint + "排序方式：" + playerHand.sortType, 10, 130, paint);
			paint.setTextSize(20);
			
		}
		
	}

	public void UpdateModeSelect(long ElapsedTime)
	{
		
	}
	
	public void DrawModeSelect(Canvas canvas, long ElapsedTime)
	{
		canvas.drawBitmap(modeSelectBackground, 0, 0, null);
		
		tutorialModeButton.Draw(canvas, ElapsedTime);
		stageModeButton.Draw(canvas, ElapsedTime);
		simpleModeButton.Draw(canvas, ElapsedTime);
		standardModeButton.Draw(canvas, ElapsedTime);
		rushModeButton.Draw(canvas, ElapsedTime);
		
	}
	
	public void UpdateStage(long ElapsedTime)
	{
		storyHolder.Update(ElapsedTime);
		
		if (readyToGo)
		{
			readyToGo = false;
			// 根据目标关卡跳转
			switch(targetStage)
			{
			case 1:
				opponentNameString = "Leo";
				gameState = AhaGameState.STATE_READY;
				gameMode = AhaGameMode.GAMEMODE_STANDARD;
				isRushMode = false;
				engineMode = AhaEngineMode.NORMAL;
				AILevel = 1;
				break;
			case 2:
				opponentNameString = "SuperMXC";
				gameState = AhaGameState.STATE_READY;
				gameMode = AhaGameMode.GAMEMODE_STANDARD;
				isRushMode = false;
				engineMode = AhaEngineMode.NORMAL;
				AILevel = 2;
				break;
			case 3:
				opponentNameString = "Shrimpy";
				gameState = AhaGameState.STATE_READY;
				gameMode = AhaGameMode.GAMEMODE_STANDARD;
				isRushMode = false;
				engineMode = AhaEngineMode.NORMAL;
				AILevel = 3;
				break;
			case 4:
				engineMode = AhaEngineMode.MODESELECT;
				break;
			case 11:
				opponentNameString = "Leo";
				gameState = AhaGameState.STATE_READY;
				gameMode = AhaGameMode.GAMEMODE_SIMPLE;
				isRushMode = false;
				engineMode = AhaEngineMode.NORMAL;
				AILevel = 1;
				break;
			case 12:
				engineMode = AhaEngineMode.MODESELECT;
				break;
			default:
				break;		
			}
		}
	}
	
	public void DrawStage(Canvas canvas, long ElapsedTime)
	{
		// 背景
		switch(targetStage)
		{
		case 1:
			canvas.drawBitmap(story1Back, 0, 0, null);
			break;
		case 2:
			canvas.drawBitmap(story2Back, 0, 0, null);
			break;
		case 3:
			canvas.drawBitmap(story3Back, 0, 0, null);
			break;
		case 4:
			canvas.drawBitmap(story3Back, 0, 0, null);
			break;
		case 11:
			canvas.drawBitmap(story3Back, 0, 0, null);
			break;
		case 12:
			canvas.drawBitmap(story3Back, 0, 0, null);
			break;
			
		}
		
		// 透明层
		Paint paint = new Paint();
		paint.setColor(color.black);
		paint.setAlpha(140);
		canvas.drawRect(new Rect(0,0,480,800), paint);
		
		// 文字
		storyHolder.Draw(canvas, ElapsedTime);
	}
	// -------- 【公共方法】 --------

	/**
	 * 设置绘图区域的大小，这会导致背景图片被缩放。
	 * <p>
	 * 这个函数是在AhaView的SurfaceChanged事件中被自动调用。
	 * 
	 * @param width
	 * @param height
	 */
	public void SetSurfaceSize(int width, int height)
	{
		mCanvasWidth = width;
		mCanvasHeight = height;

		// don't forget to resize the background image
		mBackground.SetScale(width, height);
	}

	/**
	 * 载入游戏资源。游戏运行之初就必须调用。
	 */
	public void LoadData(LoadingDialog loadingDialog)
	{
		mBackground = new Entity(getResources(), R.drawable.background, 0, 0,
				this);
		loadingDialog.setProgress(5);
		// ------------------------------------------------------

		// 创建LoadingHero，载入扑克牌资源，并且放置到allCards中

		allCards = new ArrayList<Card>();
		LoadingHero loadingHero = new LoadingHero();
		Card tempCard;

		for (int i = 0; i <= 12; i++)
		{
			tempCard = new Card(getResources(), loadingHero.spadeArray[i], 0,
					0, 1, i + 1, this);
			tempCard.SetScale(AhaSize.SIZE_CARD_WIDTH, AhaSize.SIZE_CARD_HEIGHT);
			allCards.add(tempCard);
		}
		loadingDialog.setProgress(20);
		for (int i = 0; i <= 12; i++)
		{
			tempCard = new Card(getResources(), loadingHero.heartArray[i], 0,
					0, 2, i + 1, this);
			tempCard.SetScale(AhaSize.SIZE_CARD_WIDTH, AhaSize.SIZE_CARD_HEIGHT);
			allCards.add(tempCard);
		}
		loadingDialog.setProgress(35);
		for (int i = 0; i <= 12; i++)
		{
			tempCard = new Card(getResources(), loadingHero.clubArray[i], 0, 0,
					3, i + 1, this);
			tempCard.SetScale(AhaSize.SIZE_CARD_WIDTH, AhaSize.SIZE_CARD_HEIGHT);
			allCards.add(tempCard);
		}
		loadingDialog.setProgress(50);
		for (int i = 0; i <= 12; i++)
		{
			tempCard = new Card(getResources(), loadingHero.diamondArray[i], 0,
					0, 4, i + 1, this);
			tempCard.SetScale(AhaSize.SIZE_CARD_WIDTH, AhaSize.SIZE_CARD_HEIGHT);
			allCards.add(tempCard);
		}
		loadingDialog.setProgress(65);

		// 扑克牌载入Done.

		// 载入牌背

		mCardBackBitmap = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(getResources(), loadingHero.cardback),
				AhaSize.SIZE_CARD_WIDTH, AhaSize.SIZE_CARD_HEIGHT, true);
		loadingDialog.setProgress(80);
		// 牌背载入Done.

		// 载入胡牌区和弃牌区的背景

		mKnockBackBitmap = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(getResources(), loadingHero.knockback),
				AhaSize.SIZE_CARD_WIDTH, AhaSize.SIZE_CARD_HEIGHT, true);
		loadingDialog.setProgress(85);
		mDiscardBackBitmap = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(getResources(), loadingHero.discardback),
				AhaSize.SIZE_CARD_WIDTH, AhaSize.SIZE_CARD_HEIGHT, true);
		loadingDialog.setProgress(90);
		// Done.

		// ------------------------------------------------------

		// 载入手动排序按钮
		mSortButtonBitmap = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(getResources(), loadingHero.sortButton),
				AhaSize.SIZE_SORT_WIDTH, AhaSize.SIZE_SORT_HEIGHT, true);
		
		// 载入切换排序方式按钮
		mChangeSortBitmap = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(getResources(), loadingHero.changeSort),
				AhaSize.SIZE_CHANGESORT_WIDTH, AhaSize.SIZE_CHANGESORT_HEIGHT, true);
		
		// 创建抽牌区，不过目前里面没有任何牌
		stockPile = new StockPile(this);

		// 设置抽牌区宽度和高度
		AhaSize.SIZE_STOCK_WIDTH = mCardBackBitmap.getWidth();
		AhaSize.SIZE_STOCK_HEIGHT = mCardBackBitmap.getHeight();

		// 创建手牌区，当然里面还是没有牌
		playerHand = new Hand(Hand.TYPE_PLAYER, this);
		opponentHand = new Hand(Hand.TYPE_OPPONENT, this);

		// 创建飞牌区，当然，我就不多说了。
		flyingCard = new FlyingCard(this);

		// 创建弃牌区
		discardPile = new DiscardPile(this);

		// 设置弃牌区的宽度和高度
		AhaSize.SIZE_DISCARD_WIDTH = mCardBackBitmap.getWidth();
		AhaSize.SIZE_DISCARD_HEIGHT = mCardBackBitmap.getHeight();

		// 创建胡牌区
		knockPile = new KnockPile(this);

		// 设置胡牌区的宽度和高度
		AhaSize.SIZE_KNOCK_WIDTH = mCardBackBitmap.getWidth();
		AhaSize.SIZE_KNOCK_HEIGHT = mCardBackBitmap.getHeight();

		// 创建AI引擎
		aiEngine = new AIEngine(this);
		
		// 创建游戏当前状态提示
		stateHint = new StateHint(this,getResources(),loadingHero);
		
		// 创建文字提示信息
		textHint = new TextHint(this);
		
		Log.w("XML-test", "playername:" + playerNameString);
		Log.w("XML-test", "soundeffect:" + isSoundEffectOn);
		Log.w("XML-test", "ailevel:" + AILevel);
		Log.w("XML-test", "currentstage" + currentStage);
		Log.w("XML-test", "gamemode:" + gameMode);
		
		// 从xml文件中载入数据
		File xml = new File("/data/data/com.aha.ginrummy/files/customed.xml");
		if(xml.exists())							//如果customed.xml存在
		{
			Log.w("XML-Test", "customed!");
			InputStream input;
			try
			{
				input = openFileInput("customed.xml");
				AhaSaxParser parser=new AhaSaxParser();
				parser.parser(input, this);
			} 
			catch (Exception e)
			{
			}
		}
		else 										//如果customed.xml不存在
		{
			Log.w("XML-Test", "default!");
			InputStream input;
			try
			{
				input = getAssets().open("default.xml");
				AhaSaxParser parser=new AhaSaxParser();
				parser.parser(input, this);
			} 
			catch (Exception e)
			{
			}
			
		}
		Log.w("XML-test","----------");
		Log.w("XML-test", "playername:" + playerNameString);
		Log.w("XML-test", "soundeffect:" + isSoundEffectOn);
		Log.w("XML-test", "ailevel:" + AILevel);
		Log.w("XML-test", "currentstage" + currentStage);
		Log.w("XML-test", "gamemode:" + gameMode);
		loadingDialog.setProgress(100);
		
		// -----------------------------------------
		// 模式选择画面的相关资源
		modeSelectBackground = BitmapFactory.decodeResource(getResources(),
				loadingHero.modeselectbackground);

		tutorialModeButton = new AhaButton(10, 138, 317, 77, getResources(),
				loadingHero.tutorialmode1, loadingHero.tutorialmode2);
		
		simpleModeButton = new AhaButton(10, 243, 317, 77, getResources(),
				loadingHero.simplemode1, loadingHero.simplemode2);

		standardModeButton = new AhaButton(10,348,317,77, getResources(),
				loadingHero.standardmode1, loadingHero.standardmode2);
		
		stageModeButton = new AhaButton(10, 453, 317, 77, getResources(),
				loadingHero.stagemode1, loadingHero.stagemode2);

		rushModeButton = new AhaButton(10,558,317,77, getResources(),
				loadingHero.rushmode1, loadingHero.rushmode2);
		
		// 顶补的信息提示板
		infoBoard = new InfoBoard(getResources(),loadingHero.infoback,this);
		
		// 载入资源已经完成。将引擎设置为就绪状态
		gameState = AhaGameState.STATE_READY;
		
		// 计分对象
		scoreRecord = new ScoreRecord();
				
		// 竞速模式对象
		rushTimerHint = new RushTimerHint(rushModeTimer,60,60,210,285,getResources(),R.drawable.num,this);
		
		// 文本显示器
		storyHolder = new StoryHolder(this);
		
		// 音乐播放器
		musicPlayer = new MusicPlayer(getApplicationContext(),this);
		musicPlayer.add(R.raw.music);
		
		// 读取剧情文本
		story1 = new ArrayList<String>();
		story2 = new ArrayList<String>();
		story3 = new ArrayList<String>();
		story4 = new ArrayList<String>();
		tutorial1 = new ArrayList<String>();
		tutorial2 = new ArrayList<String>();
		
		String[] str1 = getResources().getStringArray(R.array.story1);
		for(int i = 0 ; i < str1.length; i++)
		{
			story1.add(str1[i]);
		}
		
		
		String[] str2 = getResources().getStringArray(R.array.story2);
		for(int i = 0 ; i < str2.length; i++)
		{
			story2.add(str2[i]);
		}
		
		String[] str3 = getResources().getStringArray(R.array.story3);
		for(int i = 0 ; i < str3.length; i++)
		{
			story3.add(str3[i]);
		}
		
		String[] str4 = getResources().getStringArray(R.array.story4);
		for(int i = 0 ; i < str4.length; i++)
		{
			story4.add(str4[i]);
		}
		
		String[] tur1 = getResources().getStringArray(R.array.tutorial1);
		for(int i = 0 ; i < tur1.length; i++)
		{
			tutorial1.add(tur1[i]);
		}
		
		String[] tur2 = getResources().getStringArray(R.array.tutorial2);
		for(int i = 0 ; i < tur2.length; i++)
		{
			tutorial2.add(tur2[i]);
		}
		
		// 背景
		story1Back = BitmapFactory.decodeResource(getResources(), R.drawable.story1);
		story2Back = BitmapFactory.decodeResource(getResources(), R.drawable.story2);
		story3Back = BitmapFactory.decodeResource(getResources(), R.drawable.story3);
	}

	/**
	 * 处理用户的触屏输入
	 * 
	 * @return 返回true为此处已经处理完成消息。
	 */
	public boolean onTouch(View v, MotionEvent event)
	{
		switch (engineMode)
		{
		case AhaEngineMode.NORMAL:
			return onTouchNormal(v, event);
		case AhaEngineMode.MODESELECT:
			return onTouchModeSelect(v, event);
		case AhaEngineMode.STAGE:
			return onTouchStage(v, event);
		}
		return true;
	}

	/**
	 * 引擎处于普通状态下，处理用户输入
	 */
	public boolean onTouchNormal(View v, MotionEvent event)
	{
		int rawX = (int) event.getRawX();
		int rawY = (int) event.getRawY();

		// 测试用的作弊代码，对手手牌全部朝上
		if (IsPointInRect(rawX, rawY, 400, 180, 80, 50)
				&& event.getAction() == MotionEvent.ACTION_UP
				&& debugMode)
		{
			opponentHand.CheatAllFaceUp();

		}

		// 测试用的作弊代码，对手手牌全部朝下
		if (IsPointInRect(rawX, rawY, 0, 180, 80, 50)
				&& event.getAction() == MotionEvent.ACTION_UP
				&& debugMode)
		{
			opponentHand.CheatAllFaceDown();
		}

		
//		// 如果是游戏结束的状态，不能拖牌，点击任意处显示结果
//		if (gameState == AhaGameState.STATE_END && event.getAction() == MotionEvent.ACTION_UP)
//		{
//			ShowScoreBoardDialog();
//			return true;
//		}
		
		// 不是游戏结束的情况，并且点击了上部提示信息
		if (IsPointInRect(rawX, rawY, infoBoard.x, infoBoard.y,
						infoBoard.width, infoBoard.height)
				&& !flyingCard.IsFlying()
				&& event.getAction() == MotionEvent.ACTION_UP)
		{
			ShowScoreBoardDialog();
			return true;
		}
		
		
		// 当且仅当玩家回合的时候，才能拖牌
		// 这张牌可能是从抽牌区、弃牌区、手牌区拖出来
		// 如果是抽牌阶段，可以放入手牌区
		// 如果是弃牌阶段，可以放入手牌去，弃牌区，胡牌区，当然，如果能胡的话：-）
		if (mTurnState == AhaTurnState.TURN_PLAYER)
		{		
			// 按下触摸屏
			if (event.getAction() == MotionEvent.ACTION_DOWN)
			{
				// 抽牌区 - > 飞行区 抽牌
				if (IsPointInRect(rawX, rawY, AhaCoord.COORD_STOCK_X,
						AhaCoord.COORD_STOCK_Y, AhaSize.SIZE_STOCK_WIDTH,
						AhaSize.SIZE_STOCK_HEIGHT))
				{
					// 抽牌区还有牌可抽，且当前处于抽牌阶段
					if (stockPile.GetCardCount() > 0
							&& gameState == AhaGameState.STATE_DRAWCARD)
					{
						// 将一张牌添加到飞行区中
						stockPile.DrawCardToFly(flyingCard); // state =>
																// DiscardState
						stateHint.ChangeTextAndPosition();
					}
				}

				// 弃牌区 - > 飞行区 抽牌
				else if (IsPointInRect(rawX, rawY, AhaCoord.COORD_DISCARD_X,
						AhaCoord.COORD_DISCARD_Y, AhaSize.SIZE_DISCARD_WIDTH,
						AhaSize.SIZE_DISCARD_HEIGHT))
				{
					// 当前处于抽牌抽牌阶段
					if (gameState == AhaGameState.STATE_DRAWCARD)
					{
						discardPile.DrawCardToFly(flyingCard); // 对于是否有弃牌，在这里面自动计算。state
																// =>
																// DiscardState
						stateHint.ChangeTextAndPosition();
					}
				}

				// 手牌区 - > 飞行区 拖牌
				else if (IsPointInRect(rawX, rawY, AhaCoord.COORD_CARD_X_LEFT,
						AhaCoord.COORD_CARD_Y_PLAYER,
						AhaCoord.COORD_CARD_X_RIGHT + AhaSize.SIZE_CARD_WIDTH,
						AhaCoord.COORD_CARD_Y_PLAYER + AhaSize.SIZE_CARD_HEIGHT))
				{
					playerHand.FlyCard(rawX, flyingCard);
					// 重新计算米点
					playerHand.CountMiPoints();
				}
				
			}
			// 拖动触摸屏
			else if (event.getAction() == MotionEvent.ACTION_MOVE)
			{
				if (flyingCard.IsFlying())
				{
					flyingCard.SetFlyPosition(rawX, rawY);
				}
			}
			// 松开触摸屏
			else if (event.getAction() == MotionEvent.ACTION_UP)
			{
				// 排序按钮
				if (IsPointInRect(rawX, rawY, AhaCoord.COORD_SORT_X,
						AhaCoord.COORD_SORT_Y, AhaSize.SIZE_SORT_WIDTH,
						AhaSize.SIZE_SORT_HEIGHT)
						&& !flyingCard.IsFlying())
				{
					playerHand.Sort();
					textHint.AddText(AhaTextHintString.sortString);
				}
				// 自动排序模式切换
				if (IsPointInRect(rawX, rawY, AhaCoord.COORD_CHANGESORT_X,
						AhaCoord.COORD_CHANGESORT_Y, AhaSize.SIZE_CHANGESORT_WIDTH,
						AhaSize.SIZE_CHANGESORT_HEIGHT)
						&& !flyingCard.IsFlying())
				{
					playerHand.NextSortType();
					if (playerHand.sortType == AhaHandSortType.SORT_BY_RANK)
					{
						textHint.AddText(AhaTextHintString.sortByRankString);
					}
					if (playerHand.sortType == AhaHandSortType.SORT_BY_SUIT)
					{
						textHint.AddText(AhaTextHintString.sortBySuitString);
					}
					if (playerHand.sortType == AhaHandSortType.SORT_BY_MELT)
					{
						textHint.AddText(AhaTextHintString.sortByMeltString);
					}
							
				}
				
				if (flyingCard.IsFlying())
				{
					// 拖到弃牌区
					if (flyingCard.IsIntersectWithDiscard()
							&& gameState == AhaGameState.STATE_DISCARD
							&& discardPile
									.IsCardCanDiscard(flyingCard.flyingCard))
					{
						discardPile.AddDiscard(flyingCard.Land());
						stateHint.ChangeTextAndPosition();

						// 平局
						if (stockPile.GetCardCount() == 0)
						{
							if (gameMode == AhaGameMode.GAMEMODE_SIMPLE)
							{
								CheckWinIfNoCardWhenSimple();
							}
							else 
							{
								mWhoIsWin = AhaWinFlag.DRAW;
								gameState = AhaGameState.STATE_END;
								opponentHand.CheatAllFaceUp();
								AddScoreRecord();
								ShowScoreBoardDialog();
								
							}
							stateHint.ChangeTextAndPosition();
						}
					}
					// 拖到胡牌区，并且能胡！注意这里的设计，要保证一旦不能胡，要使得最后一个else能够执行.
					// 使得飞行牌返回手牌
					// 这个是金胡
					else if (flyingCard.IsIntersectWithKnock()
							&& playerHand.miPoint == 0
							&& gameState == AhaGameState.STATE_DISCARD)
					{
						mWhoIsWin = AhaWinFlag.PLAYER_WIN;
						gameState = AhaGameState.STATE_END;
						knockPile.SetCardToKnock(flyingCard.Land());
						opponentHand.CheatAllFaceUp();
						stateHint.ChangeTextAndPosition();
						AddScoreRecord();
						ShowScoreBoardDialog();
						
					}
					// 拖到胡牌区，并且能胡！注意，这里是标准模式下的米胡判定
					// 注意要保证一旦不能胡，要使得最后一个else能执行
					else if  (flyingCard.IsIntersectWithKnock()
							&& playerHand.miPoint <= 10
							&& playerHand.miPoint > 0
							&& gameState == AhaGameState.STATE_DISCARD
							&& gameMode == AhaGameMode.GAMEMODE_STANDARD)
					{
						// 飞行牌降落
						knockPile.SetCardToKnock(flyingCard.Land());
						
						// 自动贴牌并且计算米点
						AITie aiTie = new AITie();
						aiTie.tie(opponentHand.GetCards(), playerHand.GetCards());
						
						opponentHand.RebuildHandCardFromSimCard(aiTie.tier);
						opponentHand.CountMiPoints();
						opponentHand.SortByMelt();
						
						playerHand.RebuildHandCardFromSimCard(aiTie.tied);
						playerHand.CountMiPoints();
						playerHand.SortByMelt();
						
						// 胜利判断
						if (opponentHand.miPoint < playerHand.miPoint)
						{
							mWhoIsWin = AhaWinFlag.PLAYER_CHEAT_WIN;
							gameState = AhaGameState.STATE_END;
							opponentHand.CheatAllFaceUp();
							playerHand.CheatAllFaceUp();
							AddScoreRecord();
							ShowScoreBoardDialog();
							
						}
						else
						{
							mWhoIsWin = AhaWinFlag.PLAYER_MI_WIN;
							gameState = AhaGameState.STATE_END;
							opponentHand.CheatAllFaceUp();
							playerHand.CheatAllFaceUp();
							AddScoreRecord();
							ShowScoreBoardDialog();
							
						}
						stateHint.ChangeTextAndPosition();
					}
					// 拖到其他地方，返回手牌
					else
					{
						if (flyingCard.IsIntersectWithDiscard()
								&& gameState == AhaGameState.STATE_DISCARD
								&& !discardPile
										.IsCardCanDiscard(flyingCard.flyingCard))
						{
							textHint.AddText(AhaTextHintString.nodisCardHintString);
						}
						playerHand.AddCardInPosition(flyingCard.Land());
						// 重新计算米点
						playerHand.CountMiPoints();
					}
				}
			}
		}

		return true;
	}
	
	/**
	 * 引擎处于模式选择状态下，处理用户输入
	 * @param v
	 * @param event
	 * @return
	 */
	public boolean onTouchModeSelect(View v, MotionEvent event)
	{
		int rawX = (int) event.getRawX();
		int rawY = (int) event.getRawY();
		
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			if (tutorialModeButton.CheckIntersect(rawX, rawY))
			{
				tutorialModeButton.SetPressed(true);
			}
			if (stageModeButton.CheckIntersect(rawX, rawY))
			{
				stageModeButton.SetPressed(true);
			}
			if (simpleModeButton.CheckIntersect(rawX, rawY))
			{
				simpleModeButton.SetPressed(true);
			}
			if (standardModeButton.CheckIntersect(rawX, rawY))
			{
				standardModeButton.SetPressed(true);
			}
			if (rushModeButton.CheckIntersect(rawX, rawY))
			{
				rushModeButton.SetPressed(true);
			}
		}
		else if (event.getAction() == MotionEvent.ACTION_MOVE)
		{
			
		}
		else if (event.getAction() == MotionEvent.ACTION_UP)
		{
			// 教学关卡
			if (tutorialModeButton.CheckIntersect(rawX, rawY)
					&& tutorialModeButton.isPressed)
			{
				engineMode = AhaEngineMode.STAGE;
				storyHolder.SetCurrentText(tutorial1);
				targetStage = 11;
				readyToGo = false;
			} 
			// 关卡模式
			else if (stageModeButton.CheckIntersect(rawX, rawY)
					&& stageModeButton.isPressed)
			{
				engineMode = AhaEngineMode.STAGE;
				storyHolder.SetCurrentText(story1);
				targetStage = 1;
				readyToGo = false;
			} 
			// 简单模式
			else if (simpleModeButton.CheckIntersect(rawX, rawY)
					&& simpleModeButton.isPressed)
			{
				opponentNameString = "嘉嘉萌";
				gameState = AhaGameState.STATE_READY;
				gameMode = AhaGameMode.GAMEMODE_SIMPLE;
				isRushMode = false;
				targetStage = 0;
				engineMode = AhaEngineMode.NORMAL;
			} 
			// 标准模式
			else if (standardModeButton.CheckIntersect(rawX, rawY)
					&& standardModeButton.isPressed)
			{
				opponentNameString = "嘉嘉萌";
				gameState = AhaGameState.STATE_READY;
				gameMode = AhaGameMode.GAMEMODE_STANDARD;
				isRushMode = false;
				targetStage = 0;
				engineMode = AhaEngineMode.NORMAL;
			} 
			// 竞速
			else if (rushModeButton.CheckIntersect(rawX, rawY)
					&& rushModeButton.isPressed)
			{
				opponentNameString = "嘉嘉萌";
				gameState = AhaGameState.STATE_READY;
				gameMode = AhaGameMode.GAMEMODE_STANDARD;
				isRushMode = true;
				targetStage = 0;
				engineMode = AhaEngineMode.NORMAL;
				rushTimerHint.SetTimerMax(rushModeTimer);
			}
			
			tutorialModeButton.SetPressed(false);
			stageModeButton.SetPressed(false);
			simpleModeButton.SetPressed(false);
			standardModeButton.SetPressed(false);
			rushModeButton.SetPressed(false);
		}
		
		return true;
	}
	
	/**
	 * 引擎处于文本显示状态下，处理用户输入
	 * @param v
	 * @param event
	 * @return
	 */
	public boolean onTouchStage(View v, MotionEvent event)
	{
		if (storyHolder.isRunning && event.getAction() == MotionEvent.ACTION_UP)
		{
			storyHolder.Skip();
		}
		
		if (!storyHolder.isRunning && event.getAction() == MotionEvent.ACTION_UP)
		{
			storyHolder.Run();
		}
		
		// 剧情结束，进行跳转
		if (storyHolder.isEnd && event.getAction() == MotionEvent.ACTION_UP)
		{
			readyToGo = true;
		}
		return true;
	}
	
	/**
	 * 检测目标点是否包含在指定的轴对齐矩形之中
	 * 
	 * @param x
	 *            目标点的x坐标
	 * @param y
	 *            目标点的y坐标
	 * @param RectX
	 *            指定轴对齐矩形的左上角x坐标
	 * @param RectY
	 *            指定轴对齐矩形的左上角y坐标
	 * @param RectWidth
	 *            指定轴对齐矩形的宽度
	 * @param RectHeight
	 *            指定轴对齐矩形的高度
	 * @return
	 */
	public static boolean IsPointInRect(int x, int y, int RectX, int RectY,
			int RectWidth, int RectHeight)
	{
		if (x >= RectX && x < (RectX + RectWidth) && y >= RectY
				&& y < (RectY + RectHeight))
		{
			return true;
		} else
		{
			return false;
		}
	}

	/**
	 * 检测指定的两个矩形是否相交
	 * 
	 * @param x0
	 * @param y0
	 * @param width0
	 * @param height0
	 * @param x1
	 * @param y1
	 * @param width1
	 * @param height1
	 * @return
	 */
	public boolean IsRectIntersect(int x0, int y0, int width0, int height0,
			int x1, int y1, int width1, int height1)
	{
		Rect rect0 = new Rect(x0, y0, x0 + width0, y0 + height0);
		Rect rect1 = new Rect(x1, y1, x1 + width1, y1 + height1);

		if (rect0.intersect(rect1))
		{
			return true;
		} else
		{
			return false;
		}
	}

	/**
	 * 初始化一轮游戏。 一轮游戏默认由五局构成。 在这里会重置玩家对象，重置分数对象
	 */
	public void InitRound()
	{
		// 调整玩家和分数相关的东西

		// 每一轮的第一局都由玩家先手
		mIsPlayerFirstHand = false;
		
		mTurnState = AhaTurnState.TURN_NOBODY;
		gameState = AhaGameState.STATE_INITED_ROUND;
		
		// 设置当前为第一局
		currentHand = 1;
		
		// 清空得分数据
		scoreRecord.Init();

	}

	/**
	 * 初始化一局游戏。 每局游戏会发一次牌，轮流抽牌、弃牌，直到某玩家胜利或者抽牌区抽完 重置allCard中的所有扑克牌 //
	 * 清空stockPile，并将所有的扑克随机排序，加入到stockPile中 // 清空discardPile // 清空玩家的手牌
	 */
	public void InitHand()
	{
		// 初始化抽牌堆
		stockPile.Init();

		// 初始化手牌堆
		playerHand.Init();
		opponentHand.Init();

		// 初始化飞牌
		flyingCard.Init();

		// 初始化弃牌区
		discardPile.Init();

		// 初始化胡牌堆
		knockPile.Init();

		// 发牌标记
		mDealStarted = false;
		mDealHasDone = false;

		// ai引擎初始化的标记
		mAIEngineHasInitAfterDeal = false;

		// ai特殊标记置空
		aiTempCard = null;
		
		// 切换引擎状态，开始发牌
		gameState = AhaGameState.STATE_DEALING;
		
		stateHint.ChangeTextAndPosition();
		// 初始化这局游戏的胜利标记
		mWhoIsWin = AhaWinFlag.NO_WIN;
		
		if (isRushMode)
		{
			rushTimerHint.RecountTimer();
		}
		
		// 重置标记
		hasAddScore = false;

	}

	/**
	 * 发牌线程
	 * 
	 */
	class DealCard extends TimerTask
	{
		@Override
		public void run()
		{
			int CardNum = 1; // 默认只发一张牌。如果真的发一张牌，就说明gameMode没被赋值。

			// 简单模式，发14张
			if (gameMode == AhaGameMode.GAMEMODE_SIMPLE)
			{
				CardNum = 14;
			}
			// 普通模式，发20张
			else if (gameMode == AhaGameMode.GAMEMODE_STANDARD)
			{
				CardNum = 20;
			}

			// 发牌循环
			for (int cardCount = 1; cardCount <= CardNum; cardCount++)
			{
				// 单数牌发给自己
				if (cardCount % 2 != 0)
				{
					stockPile.Deal(playerHand);
					Log.w("发牌", "发了一张~给自己~呀~  " + "第" + cardCount + "张");
				}
				// 双数牌留给他人
				else
				{
					stockPile.Deal(opponentHand);
					Log.w("发牌", "留了一张~给尼玛呀~嘿~  " + "第" + cardCount + "张");
				}
				// 发牌延时
				try
				{
					Thread.sleep(AhaTime.TIME_DEALCARD);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			// 发牌终了，切换发牌完成标记
			mDealHasDone = true;

			// 切换引擎状态
			gameState = AhaGameState.STATE_DRAWCARD;
			
			// 为双方手牌排序
			playerHand.Sort();
			opponentHand.Sort();
			
			// 改变提示文字
			stateHint.ChangeTextAndPosition();
			
			if (mIsPlayerFirstHand)
			{
				mTurnState = AhaTurnState.TURN_PLAYER;
			} else
			{
				mTurnState = AhaTurnState.TURN_OPPONENT;
			}
		}

	}

	/**
	 * 启动发牌。
	 */
	private void DealCardStart()
	{
		Timer timer = new Timer();
		timer.schedule(new DealCard(), AhaTime.TIME_DEALCARD);
	}

	/**
	 * 通过花色和牌面值的大小，获取扑克牌引用
	 * 
	 * @param _suit
	 *            花色
	 * @param _rank
	 *            牌面值
	 * @return
	 */
	public Card FindCard(int _suit, int _rank)
	{
		return allCards.get(13 * (_suit - 1) + (_rank - 1));
	}

	/**
	 * 通过指定的simcard，获取扑克牌引用
	 * @param simCard 指定的simcard
	 * @return 目标扑克牌引用
	 */
	public Card FindCard(SimCard simCard)
	{
		return allCards.get(13 * (simCard.suit - 1) + (simCard.rank - 1));
	}
	
	/**
	 * 在简单模式下，当无牌可抽时，检查是否胜利
	 */
	public void CheckWinIfNoCardWhenSimple()
	{
		opponentHand.CountMiPoints();
		playerHand.CountMiPoints();
		// 如果AI米点较少
		if (opponentHand.miPoint < playerHand.miPoint)
		{
			mWhoIsWin = AhaWinFlag.OPPONENT_WIN;
		}
		// 如果AI米点较多
		else if (opponentHand.miPoint > playerHand.miPoint)
		{
			mWhoIsWin = AhaWinFlag.PLAYER_WIN;
		}
		//平局
		else
		{
			mWhoIsWin = AhaWinFlag.DRAW;
		}
		gameState = AhaGameState.STATE_END;
		opponentHand.CheatAllFaceUp();
		AddScoreRecord();
		ShowScoreBoardDialog();
	}
	
	/**
	 * 根据当前状况，显示计分板，若是关卡模式，则在结束时进行跳转。
	 */
	public void ShowScoreBoardDialog()
	{
		if(!isScoreDialogShow)
		{
			if (mWhoIsWin == AhaWinFlag.NO_WIN)
			{
				Intent intent = new Intent(this.getApplicationContext(),MiddleActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("DIALOGTYPE", GameState.GAMING);
				startActivity(intent);
			}
			else
			{
				if (currentHand < mMaxHandInRound )
				{
					// 来自教学关，跳转到教学关结局
					if (targetStage == 11)
					{
						targetStage = 12;
						engineMode = AhaEngineMode.STAGE;
						storyHolder.SetCurrentText(tutorial2);
						readyToGo = false;
						
						Intent intent = new Intent(this.getApplicationContext(),
								MiddleActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("DIALOGTYPE", GameState.END_OF_STAGE);
						startActivity(intent);
						hasAddScore = true;
						
						return;
					}
					if (!hasAddScore)
					{
						Intent intent = new Intent(this.getApplicationContext(),
								MiddleActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("DIALOGTYPE", GameState.END_OF_HAND);
						startActivity(intent);
						hasAddScore = true;
					}
					else
					{
						Intent intent = new Intent(this.getApplicationContext(),
								MiddleActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("DIALOGTYPE", GameState.END_OF_HAND_ADDED);
						startActivity(intent);
					}
				}
				else
				{
					// 关卡&模式的跳转
					if (targetStage != 0)
					{
						switch(targetStage)
						{
						// 来自第一关，跳转到第二关
						case 1:
							targetStage = 2;
							engineMode = AhaEngineMode.STAGE;
							storyHolder.SetCurrentText(story2);
							readyToGo = false;
							break;
						// 来自第二关，跳转到第三关
						case 2:
							targetStage = 3;
							engineMode = AhaEngineMode.STAGE;
							storyHolder.SetCurrentText(story3);
							readyToGo = false;
							break;
						// 来自第三关，跳转到结局
						case 3:
							targetStage = 4;
							engineMode = AhaEngineMode.STAGE;
							storyHolder.SetCurrentText(story4);
							readyToGo = false;
							break;
						default:
							break;
						}
						
						Intent intent = new Intent(this.getApplicationContext(),
								MiddleActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("DIALOGTYPE", GameState.END_OF_STAGE);
						startActivity(intent);
						hasAddScore = true;
						
						return;
					}
					
					if (!hasAddScore)
					{
						Intent intent = new Intent(this.getApplicationContext(),MiddleActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("DIALOGTYPE", GameState.END_OF_GAME);
						startActivity(intent);
						hasAddScore = true;
					}
					else
					{
						Intent intent = new Intent(this.getApplicationContext(),MiddleActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("DIALOGTYPE", GameState.END_OF_GAME_ADDED);
						startActivity(intent);
					}
					
				}
			}
			isScoreDialogShow = true;
		}
			
	}

	/**
	 * 根据当前情况，添加一条得分记录
	 */
	public void AddScoreRecord()
	{
		playerHand.CountMiPoints();
		opponentHand.CountMiPoints();
		
		int bonus;
		switch (mWhoIsWin)
		{
		// 对手金胡
		case AhaWinFlag.OPPONENT_WIN:
			
			// 简单模式的对手金胡
			if (gameMode == AhaGameMode.GAMEMODE_SIMPLE)
			{
				// 金胡类型判断并设置奖分
				switch(AITie.checkGinType(opponentHand.GetCards()))
				{
				case 1:
					bonus = 50;
					break;
				case 2:
					bonus = 25;
					break;
				default:
					bonus = 0;
					break;
				}
				
				scoreItem = new ScoreItem(playerHand.miPoint, 0, opponentHand.miPoint, bonus, mWhoIsWin);
			}
			else // 标准模式的对手金胡
			{
				switch(AITie.checkGinType(opponentHand.GetCards()))
				{
				case 1:
					bonus = 100;
					break;
				case 2:
					bonus = 50;
					break;
				case 3:
					bonus = 25;
					break;
				default:
					bonus = 0;
					break;
				}
				scoreItem = new ScoreItem(playerHand.miPoint, 0, opponentHand.miPoint, bonus, mWhoIsWin);
			}
			break; // 对手金胡End
		
		// 玩家金胡
		case AhaWinFlag.PLAYER_WIN:
			if (gameMode == AhaGameMode.GAMEMODE_SIMPLE)
			{
				// 金胡类型判断并设置奖分
				switch(AITie.checkGinType(playerHand.GetCards()))
				{
				case 1:
					bonus = 50;
					break;
				case 2:
					bonus = 25;
					break;
				default:
					bonus = 0;
					break;
				}
				scoreItem = new ScoreItem(playerHand.miPoint, bonus, opponentHand.miPoint, 0, mWhoIsWin);
			}
			else
			{
				switch(AITie.checkGinType(playerHand.GetCards()))
				{
				case 1:
					bonus = 100;
					break;
				case 2:
					bonus = 50;
					break;
				case 3:
					bonus = 25;
					break;
				default:
					bonus = 0;
					break;
				}
				scoreItem = new ScoreItem(playerHand.miPoint, 25, opponentHand.miPoint, 0, mWhoIsWin);
			}
			break; // 玩家金胡End
		case AhaWinFlag.DRAW:
			scoreItem = new ScoreItem(0, 0, 0, 0, mWhoIsWin);
			break;
		case AhaWinFlag.PLAYER_MI_WIN://WhoWin.PLAYER_MI_WIN:
			//scoreRecord.AddRecord(playerHand.miPoint, 0, opponentHand.miPoint, 0, mWhoIsWin);
			scoreItem = new ScoreItem(playerHand.miPoint, 0, opponentHand.miPoint, 0, mWhoIsWin);
			break;
		case AhaWinFlag.OPPONENT_MI_WIN://WhoWin.OPPONENT_MI_WIN:
			//scoreRecord.AddRecord(playerHand.miPoint, 0, opponentHand.miPoint, 0, mWhoIsWin);
			scoreItem = new ScoreItem(playerHand.miPoint, 0, opponentHand.miPoint, 0, mWhoIsWin);
			break;
		case AhaWinFlag.PLAYER_CHEAT_WIN://WhoWin.PLAY_CHEAT_WIN:
			//scoreRecord.AddRecord(playerHand.miPoint, 0, opponentHand.miPoint, 0, mWhoIsWin);
			scoreItem = new ScoreItem(playerHand.miPoint, 0, opponentHand.miPoint, 60, mWhoIsWin);
			break;
		case AhaWinFlag.OPPONENT_CHEAT_WIN://WhoWin.OPPONENT_CHEAT_WIN:
			//scoreRecord.AddRecord(playerHand.miPoint, 0, opponentHand.miPoint, 0, mWhoIsWin);
			scoreItem = new ScoreItem(playerHand.miPoint, 60, opponentHand.miPoint, 0, mWhoIsWin);
			break;
		default:
			break;
		}
	}
}

class AhaSize
{
	public static final int SIZE_CARD_WIDTH = /* 60 */90; // 扑克牌的宽度
	public static final int SIZE_CARD_HEIGHT = /* 90 */130; // 扑克牌的高度

	public static int SIZE_STOCK_WIDTH; // 抽牌区宽度
	public static int SIZE_STOCK_HEIGHT; // 抽牌区高度

	public static int SIZE_DISCARD_WIDTH; // 弃牌区的宽度
	public static int SIZE_DISCARD_HEIGHT; // 弃牌区的高度

	public static int SIZE_KNOCK_WIDTH;
	public static int SIZE_KNOCK_HEIGHT;
	
	public static int SIZE_SORT_WIDTH = SIZE_CARD_WIDTH;
	public static int SIZE_SORT_HEIGHT = 50;
	
	public static int SIZE_CHANGESORT_WIDTH = SIZE_CARD_WIDTH;
	public static int SIZE_CHANGESORT_HEIGHT = 50;
	
}

class AhaGameState
{
	public static final int STATE_READY = 1; // 引擎已经就绪，等待初始化
	public static final int STATE_INITED_ROUND = 2; // 游戏轮已经初始化完毕
	public static final int STATE_INITED_HAND = 3; // 游戏局已经初始化完毕
	public static final int STATE_DEALING = 4; // 发牌中
	public static final int STATE_DRAWCARD = 5; // 抽牌阶段
	public static final int STATE_DISCARD = 6; // 弃牌阶段
	public static final int STATE_END = 7; // 某方玩家胜利后的结算画面
}

class AhaTime
{
	/**
	 * 每次发牌之间的时间间隔
	 */
	public static final int TIME_DEALCARD = 100;
}

class AhaTurnState
{
	static final int TURN_PLAYER = 1; // 现在是当前玩家的回合
	static final int TURN_OPPONENT = 2; // 现在是对手的回合
	static final int TURN_NOBODY = 3; // 现在不是操作阶段，不接受输入
}

class AhaCoord
{
	// -------- 胡牌区 --------

	public static final int COORD_KNOCK_X = 40;
	public static final int COORD_KNOCK_Y = 355;

	// -------- 抽牌区 --------

	public static final int COORD_STOCK_X = 195; // 抽牌区x坐标
	public static final int COORD_STOCK_Y = 355; // 抽牌区y坐标

	// -------- 弃牌区 --------
	public static final int COORD_DISCARD_X = 350; // 弃牌区的x轴坐标
	public static final int COORD_DISCARD_Y = 355; // 弃牌区的y轴坐标

	// -------- 手牌区 --------

	/**
	 * 手牌区，手牌坐标范围，X轴左侧（这个指左侧第一张牌的左上角）
	 */
	public static final int COORD_CARD_X_LEFT = 15;

	/**
	 * 手牌区，手牌坐标范围，X轴右侧（这个指右侧第一张牌的左上角）
	 */
	public static final int COORD_CARD_X_RIGHT = 375;

	/**
	 * 手牌区，SIMPLE模式下，手上有七张牌的时候，牌与牌之间的间隔
	 */
	public static int COORD_CARD_X_SPACE_SIMPLE_7 = (COORD_CARD_X_RIGHT - COORD_CARD_X_LEFT) / 6;

	/**
	 * 手牌区，SIMPLE模式下，手上有八张牌的时候，牌与牌之间的间隔
	 */
	public static int COORD_CARD_X_SPACE_SIMPLE_8 = (COORD_CARD_X_RIGHT - COORD_CARD_X_LEFT) / 7;

	/**
	 * 手牌区，STANDARD模式下，手上有十张牌的时候，牌与牌之间的间隔
	 */
	public static int COORD_CARD_X_SPACE_STANDARD_10 =  (COORD_CARD_X_RIGHT - COORD_CARD_X_LEFT) / 9;
	
	/**
	 * 手牌区，STANDARD模式下，手上有十一张牌的时候，牌与牌之间的间隔
	 */
	public static int COORD_CARD_X_SPACE_STANDARD_11 = (COORD_CARD_X_RIGHT - COORD_CARD_X_LEFT) / 10;
	
	/**
	 * 本地玩家手牌区，手牌坐标，Y轴
	 */
	public static final int COORD_CARD_Y_PLAYER = 655;

	/**
	 * 对手玩家手牌区，手牌坐标，Y轴
	 */
	public static final int COORD_CARD_Y_OPPONENT = 151;
	
	/**
	 * 排序按钮x轴坐标
	 */
	public static final int COORD_SORT_X = COORD_DISCARD_X;
	
	/**
	 * 排序按钮y轴坐标
	 */
	public static final int COORD_SORT_Y = 550;
	
	/**
	 * 切换排序类型的按钮的x轴坐标
	 */
	public static final int COORD_CHANGESORT_X = COORD_KNOCK_X;
	
	/**
	 * 切换排序类型的按钮的y轴坐标
	 */
	public static final int COORD_CHANGESORT_Y = 550;

}

class AhaGameMode
{
	public static final int GAMEMODE_SIMPLE = 1; // 简易模式，7张牌规则，无米胡
	public static final int GAMEMODE_STANDARD = 2; // 标准模式，10张牌规则，有米胡
}

class AhaTextHintString
{
	public static String nodisCardHintString = "提示：该牌此回合不能弃";
	public static String sortByRankString = "排序方式：牌面大小";
	public static String sortBySuitString = "排序方式：花色";
	public static String sortByMeltString = "排序方式:分组";
	public static String sortString = "自动排序";
}

class AhaEngineMode
{
	public static final int NORMAL = 1;
	public static final int MODESELECT = 2;
	public static final int STAGE = 3;
}

/**
 * 胜利标记
 */
class AhaWinFlag
{
	public static final int NO_WIN = 1; 				// 尚未决出胜负
	public static final int DRAW = 2; 				// 平局
	
	public static final int PLAYER_WIN = 11; 			// 玩家金胡
	public static final int PLAYER_MI_WIN = 12;		// 玩家米胡
	public static final int PLAYER_CHEAT_WIN = 13;	// 玩家诈和（米胡失败）
	
	public static final int OPPONENT_WIN = 21;		// 对手金胡
	public static final int OPPONENT_MI_WIN = 22; 	// 对手米胡
	public static final int OPPONENT_CHEAT_WIN = 23;	// 对手诈和（米胡失败）

}