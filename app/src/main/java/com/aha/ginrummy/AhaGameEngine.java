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
	long startTime; // ����֡��
	long endTime;

	/**
	 * ����ģʽ����
	 */
	boolean debugMode = false;
	
	/**
	 * AhaView���ڵ�Activity������
	 */
	SecondActivity sbActivity;
	
	boolean isUpdateLocked = false;
	
	// -------- ģʽѡ������� --------
	Bitmap modeSelectBackground;
	AhaButton tutorialModeButton;
	AhaButton stageModeButton;
	AhaButton simpleModeButton;
	AhaButton standardModeButton;
	AhaButton rushModeButton;
	
	// -------- ��ͼ��صĳ�Ա���� --------

	int mCanvasWidth; // ��ͼ����Ŀ��
	int mCanvasHeight; // ��ͼ����ĸ߶�

	Entity mBackground; // ����ͼƬ
	Bitmap mCardBackBitmap; // �˿��Ʊ������ԴͼƬ����ĳЩ��������»�ʹ�õ���
	Bitmap mKnockBackBitmap; // �������������ԴͼƬ
	Bitmap mDiscardBackBitmap;		// �������������ԴͼƬ
	Bitmap mSortButtonBitmap;		// ����ť����ԴͼƬ
	Bitmap mChangeSortBitmap;		// �ı�����ʽ��ť����ԴͼƬ
	Bitmap story1Back;
	Bitmap story2Back;
	Bitmap story3Back;
	
	// -------- �߼�������صĳ�Ա���� --------

	String playerNameString;
	
	boolean isSoundEffectOn = true;
	int AILevel;
	int currentStage;
	
	String opponentNameString = "�μ���";
	
	/**
	 * ����ģʽ���ȴ�ʱ��
	 */
	long rushModeTimer = 7000;
	
	/**
	 * ��ǵ�ǰ����ģʽ��
	 * <p>AhaEngineMode.NORMAL��ͨ��ģʽ
	 * <p>AhaEngineMode.MODESELECT����Ϸģʽѡ��
	 * <p>AhaEngineMode.STORY��������ʾ
	 */
	int engineMode;
	
	/**
	 * ��ǰ��Ϸ״̬
	 */
	int gameState;

	/**
	 * ��ǣ��Ƿ�Ϊ����ģʽ
	 */
	boolean isRushMode = false;
	
	/**
	 * ��Ϸģʽ
	 */
	int gameMode = AhaGameMode.GAMEMODE_STANDARD;

	/**
	 * ��ǰ�غ�״̬
	 */
	public int mTurnState;

	/**
	 * һ���е���Ϸ����
	 */
	int mMaxHandInRound = 5;

	/**
	 * ��ǰ����Ϸ�ڼ���
	 */
	int currentHand = 0;
	
	/**
	 * ���Ʊ�ǣ��ͷ���ʱ�ļ�ʱ����ء�
	 * <p>
	 * ��Ϊtrue����ʾ�����Ѿ�����
	 * <p>
	 * ��Ϊfalse����ʾ������δ����
	 */
	private boolean mDealStarted;

	/**
	 * ������ɱ�ǣ��ͷ����߳����
	 * <p>
	 * ��Ϊtrue�����ʾ�����Ѿ����
	 * <p>
	 * ��Ϊfalse�����ʾ������δ���
	 */
	public boolean mDealHasDone;

	/**
	 * �����Ƿ��ڷ�����֮������˳�ʼ��
	 * <p>
	 * ��Ϊtrue������˳�ʼ��
	 * <p>
	 * ��Ϊfalse��û�н��г�ʼ��
	 */
	private boolean mAIEngineHasInitAfterDeal;

	/**
	 * ���ֱ��
	 * <p>
	 * ��Ϊtrue���򱾵�����ȳ��һ���ơ�
	 * <p>
	 * ��Ϊfalse�����������ȳ��һ���ơ�
	 */
	private boolean mIsPlayerFirstHand;

	/**
	 * ��������Ϸ˭Ӯ��
	 */
	public int mWhoIsWin;
	
	/**
	 * ����ģʽ��ʱ��&��ʾ��Ϣ
	 */
	public RushTimerHint rushTimerHint;
	
	/**
	 * ��ǣ������Ƿ�ӹ���
	 */
	boolean hasAddScore;
	
	/**
	 * �ؿ�&��ѧģʽ��ǣ�����������ת
	 */
	boolean readyToGo;
	
	/**
	 * Ŀ��ؿ����
	 */
	int targetStage;
	
	/**
	 * �ƷֶԻ�����
	 * <br>��Ϊtrue���ʾ�ƷֶԻ�������ʾ
	 * <br>��Ϊfalse���ʾ�ƷֶԻ���δ��ʾ 
	 */
	boolean isScoreDialogShow;
	// -------- ��Ϸ������صĳ�Ա���� --------

	/**
	 * ���������˿��Ƶ�������
	 * <p>
	 * ������˿˶�����ԭʼ����������ʹ�����ط������,Ҳ����ͨ����������ҵ�����Ҫ���˿˶���
	 * <p>
	 * ͨ��FindCard������ȡָ�����ơ�
	 */
	ArrayList<Card> allCards;

	/**
	 * ��������������װ�ã��ں��˿����������Լ�һЩ�����ķ�����
	 */
	StockPile stockPile;

	/**
	 * ���汾����ҵ����ƣ��ں��˿����������Լ�һЩ�����ķ�����
	 */
	Hand playerHand;

	/**
	 * ������ֵ����ƣ��ں��˿����������Լ�һЩ�����ķ�����
	 */
	Hand opponentHand;

	/**
	 * ���浱ǰ���϶��ŵ�ĳ���˿�
	 * <p>
	 * ���ǵģ����ǹ����С����ơ����ѵ��������Ԥ����ʲô����
	 */
	FlyingCard flyingCard;

	/**
	 * �����ŷ������ƣ��ں��˿����������Լ�һЩ�����ķ�����
	 */
	DiscardPile discardPile;

	/**
	 * �����ź��ƣ��ں�һ���˿������ã��Լ�һЩ�����ķ�����
	 */
	KnockPile knockPile;
	
	/**
	 * ������AI��صĺ����㷨����ƻ���
	 */
	AIEngine aiEngine;

	/**
	 * ����ai��Ϊ��ص�ĳ��card�����á����Է�ֹAI���ٹ���
	 */
	Card aiTempCard = null;
	
	/**
	 * ��ʾ��ǰ��Ϸ״̬����Ϣ
	 */
	StateHint stateHint;
	
	/**
	 * ��ʾ������Ϣ����ʾ��
	 */
	TextHint textHint;
	
	/**
	 * ������Ϣ��ʾ��
	 */
	InfoBoard infoBoard;
	
	/**
	 * �Ʒֶ���
	 */
	ScoreRecord scoreRecord;
	
	/**
	 * �ӷֶ���
	 */
	ScoreItem scoreItem;
	
	/**
	 * �ı���ʾ��
	 */
	StoryHolder storyHolder;
	
	/**
	 * ���ֲ�����
	 */
	MusicPlayer musicPlayer;
	
	/**
	 * �ı�������
	 */
	ArrayList<String> story1;
	ArrayList<String> story2;
	ArrayList<String> story3;
	ArrayList<String> story4;
	
	ArrayList<String> tutorial1;
	ArrayList<String> tutorial2;
	
	// -------- ����ϵͳ��صĳ�Ա���� --------

	public static final int SPEED_CARD = 50; // �˿��Ƶ��ƶ��ٶ�

	// -------- �����캯���� --------

	public AhaGameEngine()
	{
		mCanvasWidth = 0;
		mCanvasHeight = 0;

		// ��ʼ������ԾǨ��λͼ��������װ�á�����������Դ�����һ��
		BitmapFliter.SetMode(BitmapFliter.MODE_800_480,
				BitmapFliter.MODE_800_480);
		
	}

	// -------- ����������¡� --------

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

		// �����������ʼ�����ֳ�ʼ��
		if (gameState == AhaGameState.STATE_READY)
		{
			// ��ʼ��һ����Ϸ
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

		// ����ֳ�ʼ����ϣ���ʼ���оֳ�ʼ��
		if (gameState == AhaGameState.STATE_INITED_ROUND)
		{
			// ��ʼ��һ����Ϸ
			InitHand();
		}

		// ���ƽ׶Σ���ÿ����ҷ�ʮ���ƣ�ȷ����һ�����Ƶ��ˣ�Ȼ���л�״̬
		if (gameState == AhaGameState.STATE_DEALING)
		{
			if (!mDealStarted)
			{
				// ������������
				DealCardStart();

				// ��ǣ����������Ѿ�����
				mDealStarted = true;
			}

		}

		if (mDealHasDone == true && mAIEngineHasInitAfterDeal == false)
		{
			aiEngine.InitAfterDeal(opponentHand.GetCards());
			mAIEngineHasInitAfterDeal = true;
			// ���¼����׵�
			playerHand.CountMiPoints();
			//sbActivity.finish();
		}

		// ai˼����· --------

		if (mTurnState == AhaTurnState.TURN_OPPONENT
				&& (gameState == AhaGameState.STATE_DRAWCARD || gameState == AhaGameState.STATE_DISCARD))
		{
			// ��������ʼ˼�����ж�
			if (aiEngine.aiState == AIEngine.STATE_READY)
			{
				// ������ƶ����ƣ���Ҫ�ȿ����Ƿ���Ҫ������
				if (discardPile.GetCardCount() != 0)
				{
					aiEngine.StartTask(AIEngine.TASK_IsDiscardNeed,
							discardPile.GetTopCard());
				}
				// ������ƶ����ƣ������ǡ�����Ҫ���ơ���ֱ����ת״̬
				else
				{
					aiEngine.aiState = AIEngine.STATE_RESULT_IsDiscardNeed_FALSE;
				}
			} else if (aiEngine.aiState == AIEngine.STATE_THINKING_IsDiscardNeed)
			{
				// ˼���У�what can i do��
			} else if (aiEngine.aiState == AIEngine.STATE_RESULT_IsDiscardNeed_TRUE)
			{
				// ��Ҫ���ƣ�����result���Ѿ���������Ҫ�����ƣ�what can i do��
			} else if (aiEngine.aiState == AIEngine.STATE_RESULT_IsDiscardNeed_FALSE)
			{
				// ���������ƣ�������result�з���������
				aiEngine.StartTask(AIEngine.TASK_DrawFromStockAndDiscard,
						stockPile.GetTopCard());
			} else if (aiEngine.aiState == AIEngine.STATE_THINKING_DrawFromStockAndDiscard)
			{
				// ��result���Ѿ������˷��������ƣ�what can i do��
			}

		}
		// ai˼����· done.
		
		// ����ģʽ�Ĵ���
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
		
		
		
		// ���ƽ׶�
		if (gameState == AhaGameState.STATE_DRAWCARD)
		{
			// Player�غϵ�״̬���л���StockPile.DrawCardToFly�н���
			if (mTurnState == AhaTurnState.TURN_PLAYER)
			{

			}

			// Opponent�غϵ�״̬
			else if (mTurnState == AhaTurnState.TURN_OPPONENT)
			{
				// ��Ҫ�������ƣ�
				if (aiEngine.aiState == AIEngine.STATE_RESULT_IsDiscardNeed_TRUE)
				{
					// ���������Ķ�����ֱ�ӷ�������������
					aiTempCard = discardPile.DrawCardToHand(opponentHand);
					stateHint.ChangeTextAndPosition();
				} else if (aiEngine.aiState == AIEngine.STATE_RESULT_DrawFromStockAndDiscard)
				{
					// ���������Ķ�����ֱ�ӷ�������������
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
		// ���ƽ׶�
		if (gameState == AhaGameState.STATE_DISCARD)
		{

			if (mTurnState == AhaTurnState.TURN_PLAYER)
			{
				// �ȴ���Ҷ�״̬�����л�
			} 
			else if (mTurnState == AhaTurnState.TURN_OPPONENT
					&& aiTempCard != null
					&& aiTempCard.mX == aiTempCard.targetX
					&& aiTempCard.mY == aiTempCard.targetY)
			{
				aiTempCard = null;
				// ץ����֮������ƣ�δ���ƣ�
				if (aiEngine.aiState == AIEngine.STATE_RESULT_IsDiscardNeed_TRUE
						&& !aiEngine.gin && !aiEngine.knock)
				{
					// ���ƣ�ע�Ᵽ�棡
					discardPile.AddDiscard(opponentHand
							.Discard(aiEngine.result));
					aiEngine.aiState = AIEngine.STATE_DONE;
				}
				// ץ����֮������ƣ�δ���ƣ�
				else if (aiEngine.aiState == AIEngine.STATE_RESULT_DrawFromStockAndDiscard
						&& !aiEngine.gin && !aiEngine.knock)
				{
					// ���ƣ�ע�Ᵽ�棡
					discardPile.AddDiscard(opponentHand
							.Discard(aiEngine.result));
					aiEngine.aiState = AIEngine.STATE_DONE;
				}
				// ʤ���жϣ��������
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
				// �׺��жϣ��Ǽ�ģʽ�£�
				else if ( gameMode == AhaGameMode.GAMEMODE_STANDARD && aiEngine.knock)
				{
					// �Ȱ�Ҫ�ӵ����ӳ�ȥ
					knockPile.SetCardToKnock(opponentHand
							.Discard(aiEngine.result));
					
					// �Զ����Ʋ��Ҽ����׵�
					AITie aiTie = new AITie();
					aiTie.tie(playerHand.GetCards(), opponentHand.GetCards());
					opponentHand.RebuildHandCardFromSimCard(aiTie.tied);
					opponentHand.CountMiPoints();
					opponentHand.SortByMelt();
					playerHand.RebuildHandCardFromSimCard(aiTie.tier);
					playerHand.CountMiPoints();
					playerHand.SortByMelt();
					
					// ʤ���ж�
					// �����׺�
					if (opponentHand.miPoint <= playerHand.miPoint)
					{
						mWhoIsWin = AhaWinFlag.OPPONENT_MI_WIN;
						gameState = AhaGameState.STATE_END;
						opponentHand.CheatAllFaceUp();
						playerHand.CheatAllFaceUp();
						AddScoreRecord();
						ShowScoreBoardDialog();
					}
					// ����թ��
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
				// �����ж�
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
		// ����׶�
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
		// ���Ʊ���
		// canvas.drawBitmap(mBackground,0,0,null);
		
		Paint paint = new Paint();
		paint.setARGB(255, 0, 255, 0);
		paint.setTextSize(15);
		// mBackground.draw(canvas);
		Paint testPaint = new Paint();
		testPaint.setARGB(255, 0, 255, 0);
		testPaint.setTextSize(20);
		
		// ===============================
		// ��ʽ��ͼ
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
		// ��ʾ����
		
		if (debugMode)
		{
			// ֡��
			endTime = System.currentTimeMillis();

			canvas.drawText("Update&Draw Time Elapsed:" + (endTime - startTime)
					+ "ms", 10, 10, paint);
			canvas.drawText("Frame Time Elapsed:" + ElapsedTime + "ms", 10, 30,
					paint);

			// ��Ϸ״̬
			switch (gameState)
			{
			case AhaGameState.STATE_DEALING:
				canvas.drawText("GameState: ���ƽ׶�", 10, 50, paint);
				break;
			case AhaGameState.STATE_DRAWCARD:
				canvas.drawText("GameState: ���ƽ׶�", 10, 50, paint);
				break;
			case AhaGameState.STATE_DISCARD:
				canvas.drawText("GameState: ���ƽ׶�", 10, 50, paint);
			case AhaGameState.STATE_END:
				canvas.drawText("GameState: ����", 10, 50, paint);
			default:
				canvas.drawText("GameState: ", 10, 50, paint);
				break;
			}

			// �غ�״̬
			switch (mTurnState)
			{
			case AhaTurnState.TURN_PLAYER:
				canvas.drawText("TurnState�� �Լ�", 10, 70, paint);
				break;
			case AhaTurnState.TURN_OPPONENT:
				canvas.drawText("TurnState�� ����", 10, 70, paint);
			default:
				canvas.drawText("TurnState�� ", 10, 70, paint);
				break;
			}

			// ai״̬
			switch (aiEngine.aiState)
			{
			case AIEngine.STATE_READY:
				canvas.drawText("AIEngine�� SYATE_READY", 10, 90, paint);
				break;
			case AIEngine.STATE_THINKING_IsDiscardNeed:
				canvas.drawText("AIEngine�� STATE_THINKING_IsDiscardNeed", 10, 90,
						paint);
				break;
			case AIEngine.STATE_RESULT_IsDiscardNeed_TRUE:
				canvas.drawText("AIEngine�� STATE_RESULT_IsDiscardNeed_TRUE", 10,
						90, paint);
				break;
			case AIEngine.STATE_RESULT_IsDiscardNeed_FALSE:
				canvas.drawText("AIEngine�� STATE_RESULT_IsDiscardNeed_FALSE", 10,
						90, paint);
				break;
			case AIEngine.STATE_THINKING_DrawFromStockAndDiscard:
				canvas.drawText("AIEngine�� STATE_THINKING_DrawFromStockAndDiscard",
						10, 90, paint);
				break;
			case AIEngine.STATE_RESULT_DrawFromStockAndDiscard:
				canvas.drawText("AIEngine�� STATE_RESULT_DrawFromStockAndDiscard",
						10, 90, paint);
				break;
			case AIEngine.STATE_DONE:
				canvas.drawText("AIEngine�� STATE_DONE", 10, 90, paint);
				break;
			default:
				canvas.drawText("AIEngine�� ", 10, 90, paint);
				break;
			}

			// ai˼�����
			if (aiEngine.result != null)
			{
				canvas.drawText("AI Result: suit:" + aiEngine.result.suit
						+ " rank:" + aiEngine.result.rank, 10, 110, paint);

			} else
			{
				canvas.drawText("AI Result: Null", 10, 110, paint);
			}

			// ��ʾ�ҷ��׵�
			canvas.drawText("�ҷ��׵㣺" + playerHand.miPoint + "����ʽ��" + playerHand.sortType, 10, 130, paint);
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
			// ����Ŀ��ؿ���ת
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
		// ����
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
		
		// ͸����
		Paint paint = new Paint();
		paint.setColor(color.black);
		paint.setAlpha(140);
		canvas.drawRect(new Rect(0,0,480,800), paint);
		
		// ����
		storyHolder.Draw(canvas, ElapsedTime);
	}
	// -------- ������������ --------

	/**
	 * ���û�ͼ����Ĵ�С����ᵼ�±���ͼƬ�����š�
	 * <p>
	 * �����������AhaView��SurfaceChanged�¼��б��Զ����á�
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
	 * ������Ϸ��Դ����Ϸ����֮���ͱ�����á�
	 */
	public void LoadData(LoadingDialog loadingDialog)
	{
		mBackground = new Entity(getResources(), R.drawable.background, 0, 0,
				this);
		loadingDialog.setProgress(5);
		// ------------------------------------------------------

		// ����LoadingHero�������˿�����Դ�����ҷ��õ�allCards��

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

		// �˿�������Done.

		// �����Ʊ�

		mCardBackBitmap = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(getResources(), loadingHero.cardback),
				AhaSize.SIZE_CARD_WIDTH, AhaSize.SIZE_CARD_HEIGHT, true);
		loadingDialog.setProgress(80);
		// �Ʊ�����Done.

		// ������������������ı���

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

		// �����ֶ�����ť
		mSortButtonBitmap = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(getResources(), loadingHero.sortButton),
				AhaSize.SIZE_SORT_WIDTH, AhaSize.SIZE_SORT_HEIGHT, true);
		
		// �����л�����ʽ��ť
		mChangeSortBitmap = Bitmap.createScaledBitmap(BitmapFactory
				.decodeResource(getResources(), loadingHero.changeSort),
				AhaSize.SIZE_CHANGESORT_WIDTH, AhaSize.SIZE_CHANGESORT_HEIGHT, true);
		
		// ����������������Ŀǰ����û���κ���
		stockPile = new StockPile(this);

		// ���ó�������Ⱥ͸߶�
		AhaSize.SIZE_STOCK_WIDTH = mCardBackBitmap.getWidth();
		AhaSize.SIZE_STOCK_HEIGHT = mCardBackBitmap.getHeight();

		// ��������������Ȼ���滹��û����
		playerHand = new Hand(Hand.TYPE_PLAYER, this);
		opponentHand = new Hand(Hand.TYPE_OPPONENT, this);

		// ��������������Ȼ���ҾͲ���˵�ˡ�
		flyingCard = new FlyingCard(this);

		// ����������
		discardPile = new DiscardPile(this);

		// �����������Ŀ�Ⱥ͸߶�
		AhaSize.SIZE_DISCARD_WIDTH = mCardBackBitmap.getWidth();
		AhaSize.SIZE_DISCARD_HEIGHT = mCardBackBitmap.getHeight();

		// ����������
		knockPile = new KnockPile(this);

		// ���ú������Ŀ�Ⱥ͸߶�
		AhaSize.SIZE_KNOCK_WIDTH = mCardBackBitmap.getWidth();
		AhaSize.SIZE_KNOCK_HEIGHT = mCardBackBitmap.getHeight();

		// ����AI����
		aiEngine = new AIEngine(this);
		
		// ������Ϸ��ǰ״̬��ʾ
		stateHint = new StateHint(this,getResources(),loadingHero);
		
		// ����������ʾ��Ϣ
		textHint = new TextHint(this);
		
		Log.w("XML-test", "playername:" + playerNameString);
		Log.w("XML-test", "soundeffect:" + isSoundEffectOn);
		Log.w("XML-test", "ailevel:" + AILevel);
		Log.w("XML-test", "currentstage" + currentStage);
		Log.w("XML-test", "gamemode:" + gameMode);
		
		// ��xml�ļ�����������
		File xml = new File("/data/data/com.aha.ginrummy/files/customed.xml");
		if(xml.exists())							//���customed.xml����
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
		else 										//���customed.xml������
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
		// ģʽѡ����������Դ
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
		
		// ��������Ϣ��ʾ��
		infoBoard = new InfoBoard(getResources(),loadingHero.infoback,this);
		
		// ������Դ�Ѿ���ɡ�����������Ϊ����״̬
		gameState = AhaGameState.STATE_READY;
		
		// �Ʒֶ���
		scoreRecord = new ScoreRecord();
				
		// ����ģʽ����
		rushTimerHint = new RushTimerHint(rushModeTimer,60,60,210,285,getResources(),R.drawable.num,this);
		
		// �ı���ʾ��
		storyHolder = new StoryHolder(this);
		
		// ���ֲ�����
		musicPlayer = new MusicPlayer(getApplicationContext(),this);
		musicPlayer.add(R.raw.music);
		
		// ��ȡ�����ı�
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
		
		// ����
		story1Back = BitmapFactory.decodeResource(getResources(), R.drawable.story1);
		story2Back = BitmapFactory.decodeResource(getResources(), R.drawable.story2);
		story3Back = BitmapFactory.decodeResource(getResources(), R.drawable.story3);
	}

	/**
	 * �����û��Ĵ�������
	 * 
	 * @return ����trueΪ�˴��Ѿ����������Ϣ��
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
	 * ���洦����ͨ״̬�£������û�����
	 */
	public boolean onTouchNormal(View v, MotionEvent event)
	{
		int rawX = (int) event.getRawX();
		int rawY = (int) event.getRawY();

		// �����õ����״��룬��������ȫ������
		if (IsPointInRect(rawX, rawY, 400, 180, 80, 50)
				&& event.getAction() == MotionEvent.ACTION_UP
				&& debugMode)
		{
			opponentHand.CheatAllFaceUp();

		}

		// �����õ����״��룬��������ȫ������
		if (IsPointInRect(rawX, rawY, 0, 180, 80, 50)
				&& event.getAction() == MotionEvent.ACTION_UP
				&& debugMode)
		{
			opponentHand.CheatAllFaceDown();
		}

		
//		// �������Ϸ������״̬���������ƣ�������⴦��ʾ���
//		if (gameState == AhaGameState.STATE_END && event.getAction() == MotionEvent.ACTION_UP)
//		{
//			ShowScoreBoardDialog();
//			return true;
//		}
		
		// ������Ϸ��������������ҵ�����ϲ���ʾ��Ϣ
		if (IsPointInRect(rawX, rawY, infoBoard.x, infoBoard.y,
						infoBoard.width, infoBoard.height)
				&& !flyingCard.IsFlying()
				&& event.getAction() == MotionEvent.ACTION_UP)
		{
			ShowScoreBoardDialog();
			return true;
		}
		
		
		// ���ҽ�����һغϵ�ʱ�򣬲�������
		// �����ƿ����Ǵӳ����������������������ϳ���
		// ����ǳ��ƽ׶Σ����Է���������
		// ��������ƽ׶Σ����Է�������ȥ��������������������Ȼ������ܺ��Ļ���-��
		if (mTurnState == AhaTurnState.TURN_PLAYER)
		{		
			// ���´�����
			if (event.getAction() == MotionEvent.ACTION_DOWN)
			{
				// ������ - > ������ ����
				if (IsPointInRect(rawX, rawY, AhaCoord.COORD_STOCK_X,
						AhaCoord.COORD_STOCK_Y, AhaSize.SIZE_STOCK_WIDTH,
						AhaSize.SIZE_STOCK_HEIGHT))
				{
					// �����������ƿɳ飬�ҵ�ǰ���ڳ��ƽ׶�
					if (stockPile.GetCardCount() > 0
							&& gameState == AhaGameState.STATE_DRAWCARD)
					{
						// ��һ������ӵ���������
						stockPile.DrawCardToFly(flyingCard); // state =>
																// DiscardState
						stateHint.ChangeTextAndPosition();
					}
				}

				// ������ - > ������ ����
				else if (IsPointInRect(rawX, rawY, AhaCoord.COORD_DISCARD_X,
						AhaCoord.COORD_DISCARD_Y, AhaSize.SIZE_DISCARD_WIDTH,
						AhaSize.SIZE_DISCARD_HEIGHT))
				{
					// ��ǰ���ڳ��Ƴ��ƽ׶�
					if (gameState == AhaGameState.STATE_DRAWCARD)
					{
						discardPile.DrawCardToFly(flyingCard); // �����Ƿ������ƣ����������Զ����㡣state
																// =>
																// DiscardState
						stateHint.ChangeTextAndPosition();
					}
				}

				// ������ - > ������ ����
				else if (IsPointInRect(rawX, rawY, AhaCoord.COORD_CARD_X_LEFT,
						AhaCoord.COORD_CARD_Y_PLAYER,
						AhaCoord.COORD_CARD_X_RIGHT + AhaSize.SIZE_CARD_WIDTH,
						AhaCoord.COORD_CARD_Y_PLAYER + AhaSize.SIZE_CARD_HEIGHT))
				{
					playerHand.FlyCard(rawX, flyingCard);
					// ���¼����׵�
					playerHand.CountMiPoints();
				}
				
			}
			// �϶�������
			else if (event.getAction() == MotionEvent.ACTION_MOVE)
			{
				if (flyingCard.IsFlying())
				{
					flyingCard.SetFlyPosition(rawX, rawY);
				}
			}
			// �ɿ�������
			else if (event.getAction() == MotionEvent.ACTION_UP)
			{
				// ����ť
				if (IsPointInRect(rawX, rawY, AhaCoord.COORD_SORT_X,
						AhaCoord.COORD_SORT_Y, AhaSize.SIZE_SORT_WIDTH,
						AhaSize.SIZE_SORT_HEIGHT)
						&& !flyingCard.IsFlying())
				{
					playerHand.Sort();
					textHint.AddText(AhaTextHintString.sortString);
				}
				// �Զ�����ģʽ�л�
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
					// �ϵ�������
					if (flyingCard.IsIntersectWithDiscard()
							&& gameState == AhaGameState.STATE_DISCARD
							&& discardPile
									.IsCardCanDiscard(flyingCard.flyingCard))
					{
						discardPile.AddDiscard(flyingCard.Land());
						stateHint.ChangeTextAndPosition();

						// ƽ��
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
					// �ϵ��������������ܺ���ע���������ƣ�Ҫ��֤һ�����ܺ���Ҫʹ�����һ��else�ܹ�ִ��.
					// ʹ�÷����Ʒ�������
					// ����ǽ��
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
					// �ϵ��������������ܺ���ע�⣬�����Ǳ�׼ģʽ�µ��׺��ж�
					// ע��Ҫ��֤һ�����ܺ���Ҫʹ�����һ��else��ִ��
					else if  (flyingCard.IsIntersectWithKnock()
							&& playerHand.miPoint <= 10
							&& playerHand.miPoint > 0
							&& gameState == AhaGameState.STATE_DISCARD
							&& gameMode == AhaGameMode.GAMEMODE_STANDARD)
					{
						// �����ƽ���
						knockPile.SetCardToKnock(flyingCard.Land());
						
						// �Զ����Ʋ��Ҽ����׵�
						AITie aiTie = new AITie();
						aiTie.tie(opponentHand.GetCards(), playerHand.GetCards());
						
						opponentHand.RebuildHandCardFromSimCard(aiTie.tier);
						opponentHand.CountMiPoints();
						opponentHand.SortByMelt();
						
						playerHand.RebuildHandCardFromSimCard(aiTie.tied);
						playerHand.CountMiPoints();
						playerHand.SortByMelt();
						
						// ʤ���ж�
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
					// �ϵ������ط�����������
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
						// ���¼����׵�
						playerHand.CountMiPoints();
					}
				}
			}
		}

		return true;
	}
	
	/**
	 * ���洦��ģʽѡ��״̬�£������û�����
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
			// ��ѧ�ؿ�
			if (tutorialModeButton.CheckIntersect(rawX, rawY)
					&& tutorialModeButton.isPressed)
			{
				engineMode = AhaEngineMode.STAGE;
				storyHolder.SetCurrentText(tutorial1);
				targetStage = 11;
				readyToGo = false;
			} 
			// �ؿ�ģʽ
			else if (stageModeButton.CheckIntersect(rawX, rawY)
					&& stageModeButton.isPressed)
			{
				engineMode = AhaEngineMode.STAGE;
				storyHolder.SetCurrentText(story1);
				targetStage = 1;
				readyToGo = false;
			} 
			// ��ģʽ
			else if (simpleModeButton.CheckIntersect(rawX, rawY)
					&& simpleModeButton.isPressed)
			{
				opponentNameString = "�μ���";
				gameState = AhaGameState.STATE_READY;
				gameMode = AhaGameMode.GAMEMODE_SIMPLE;
				isRushMode = false;
				targetStage = 0;
				engineMode = AhaEngineMode.NORMAL;
			} 
			// ��׼ģʽ
			else if (standardModeButton.CheckIntersect(rawX, rawY)
					&& standardModeButton.isPressed)
			{
				opponentNameString = "�μ���";
				gameState = AhaGameState.STATE_READY;
				gameMode = AhaGameMode.GAMEMODE_STANDARD;
				isRushMode = false;
				targetStage = 0;
				engineMode = AhaEngineMode.NORMAL;
			} 
			// ����
			else if (rushModeButton.CheckIntersect(rawX, rawY)
					&& rushModeButton.isPressed)
			{
				opponentNameString = "�μ���";
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
	 * ���洦���ı���ʾ״̬�£������û�����
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
		
		// ���������������ת
		if (storyHolder.isEnd && event.getAction() == MotionEvent.ACTION_UP)
		{
			readyToGo = true;
		}
		return true;
	}
	
	/**
	 * ���Ŀ����Ƿ������ָ������������֮��
	 * 
	 * @param x
	 *            Ŀ����x����
	 * @param y
	 *            Ŀ����y����
	 * @param RectX
	 *            ָ���������ε����Ͻ�x����
	 * @param RectY
	 *            ָ���������ε����Ͻ�y����
	 * @param RectWidth
	 *            ָ���������εĿ��
	 * @param RectHeight
	 *            ָ���������εĸ߶�
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
	 * ���ָ�������������Ƿ��ཻ
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
	 * ��ʼ��һ����Ϸ�� һ����ϷĬ������ֹ��ɡ� �������������Ҷ������÷�������
	 */
	public void InitRound()
	{
		// ������Һͷ�����صĶ���

		// ÿһ�ֵĵ�һ�ֶ����������
		mIsPlayerFirstHand = false;
		
		mTurnState = AhaTurnState.TURN_NOBODY;
		gameState = AhaGameState.STATE_INITED_ROUND;
		
		// ���õ�ǰΪ��һ��
		currentHand = 1;
		
		// ��յ÷�����
		scoreRecord.Init();

	}

	/**
	 * ��ʼ��һ����Ϸ�� ÿ����Ϸ�ᷢһ���ƣ��������ơ����ƣ�ֱ��ĳ���ʤ�����߳��������� ����allCard�е������˿��� //
	 * ���stockPile���������е��˿�������򣬼��뵽stockPile�� // ���discardPile // �����ҵ�����
	 */
	public void InitHand()
	{
		// ��ʼ�����ƶ�
		stockPile.Init();

		// ��ʼ�����ƶ�
		playerHand.Init();
		opponentHand.Init();

		// ��ʼ������
		flyingCard.Init();

		// ��ʼ��������
		discardPile.Init();

		// ��ʼ�����ƶ�
		knockPile.Init();

		// ���Ʊ��
		mDealStarted = false;
		mDealHasDone = false;

		// ai�����ʼ���ı��
		mAIEngineHasInitAfterDeal = false;

		// ai�������ÿ�
		aiTempCard = null;
		
		// �л�����״̬����ʼ����
		gameState = AhaGameState.STATE_DEALING;
		
		stateHint.ChangeTextAndPosition();
		// ��ʼ�������Ϸ��ʤ�����
		mWhoIsWin = AhaWinFlag.NO_WIN;
		
		if (isRushMode)
		{
			rushTimerHint.RecountTimer();
		}
		
		// ���ñ��
		hasAddScore = false;

	}

	/**
	 * �����߳�
	 * 
	 */
	class DealCard extends TimerTask
	{
		@Override
		public void run()
		{
			int CardNum = 1; // Ĭ��ֻ��һ���ơ������ķ�һ���ƣ���˵��gameModeû����ֵ��

			// ��ģʽ����14��
			if (gameMode == AhaGameMode.GAMEMODE_SIMPLE)
			{
				CardNum = 14;
			}
			// ��ͨģʽ����20��
			else if (gameMode == AhaGameMode.GAMEMODE_STANDARD)
			{
				CardNum = 20;
			}

			// ����ѭ��
			for (int cardCount = 1; cardCount <= CardNum; cardCount++)
			{
				// �����Ʒ����Լ�
				if (cardCount % 2 != 0)
				{
					stockPile.Deal(playerHand);
					Log.w("����", "����һ��~���Լ�~ѽ~  " + "��" + cardCount + "��");
				}
				// ˫������������
				else
				{
					stockPile.Deal(opponentHand);
					Log.w("����", "����һ��~������ѽ~��~  " + "��" + cardCount + "��");
				}
				// ������ʱ
				try
				{
					Thread.sleep(AhaTime.TIME_DEALCARD);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			// �������ˣ��л�������ɱ��
			mDealHasDone = true;

			// �л�����״̬
			gameState = AhaGameState.STATE_DRAWCARD;
			
			// Ϊ˫����������
			playerHand.Sort();
			opponentHand.Sort();
			
			// �ı���ʾ����
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
	 * �������ơ�
	 */
	private void DealCardStart()
	{
		Timer timer = new Timer();
		timer.schedule(new DealCard(), AhaTime.TIME_DEALCARD);
	}

	/**
	 * ͨ����ɫ������ֵ�Ĵ�С����ȡ�˿�������
	 * 
	 * @param _suit
	 *            ��ɫ
	 * @param _rank
	 *            ����ֵ
	 * @return
	 */
	public Card FindCard(int _suit, int _rank)
	{
		return allCards.get(13 * (_suit - 1) + (_rank - 1));
	}

	/**
	 * ͨ��ָ����simcard����ȡ�˿�������
	 * @param simCard ָ����simcard
	 * @return Ŀ���˿�������
	 */
	public Card FindCard(SimCard simCard)
	{
		return allCards.get(13 * (simCard.suit - 1) + (simCard.rank - 1));
	}
	
	/**
	 * �ڼ�ģʽ�£������ƿɳ�ʱ������Ƿ�ʤ��
	 */
	public void CheckWinIfNoCardWhenSimple()
	{
		opponentHand.CountMiPoints();
		playerHand.CountMiPoints();
		// ���AI�׵����
		if (opponentHand.miPoint < playerHand.miPoint)
		{
			mWhoIsWin = AhaWinFlag.OPPONENT_WIN;
		}
		// ���AI�׵�϶�
		else if (opponentHand.miPoint > playerHand.miPoint)
		{
			mWhoIsWin = AhaWinFlag.PLAYER_WIN;
		}
		//ƽ��
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
	 * ���ݵ�ǰ״������ʾ�Ʒְ壬���ǹؿ�ģʽ�����ڽ���ʱ������ת��
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
					// ���Խ�ѧ�أ���ת����ѧ�ؽ��
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
					// �ؿ�&ģʽ����ת
					if (targetStage != 0)
					{
						switch(targetStage)
						{
						// ���Ե�һ�أ���ת���ڶ���
						case 1:
							targetStage = 2;
							engineMode = AhaEngineMode.STAGE;
							storyHolder.SetCurrentText(story2);
							readyToGo = false;
							break;
						// ���Եڶ��أ���ת��������
						case 2:
							targetStage = 3;
							engineMode = AhaEngineMode.STAGE;
							storyHolder.SetCurrentText(story3);
							readyToGo = false;
							break;
						// ���Ե����أ���ת�����
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
	 * ���ݵ�ǰ��������һ���÷ּ�¼
	 */
	public void AddScoreRecord()
	{
		playerHand.CountMiPoints();
		opponentHand.CountMiPoints();
		
		int bonus;
		switch (mWhoIsWin)
		{
		// ���ֽ��
		case AhaWinFlag.OPPONENT_WIN:
			
			// ��ģʽ�Ķ��ֽ��
			if (gameMode == AhaGameMode.GAMEMODE_SIMPLE)
			{
				// ��������жϲ����ý���
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
			else // ��׼ģʽ�Ķ��ֽ��
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
			break; // ���ֽ��End
		
		// ��ҽ��
		case AhaWinFlag.PLAYER_WIN:
			if (gameMode == AhaGameMode.GAMEMODE_SIMPLE)
			{
				// ��������жϲ����ý���
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
			break; // ��ҽ��End
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
	public static final int SIZE_CARD_WIDTH = /* 60 */90; // �˿��ƵĿ��
	public static final int SIZE_CARD_HEIGHT = /* 90 */130; // �˿��Ƶĸ߶�

	public static int SIZE_STOCK_WIDTH; // ���������
	public static int SIZE_STOCK_HEIGHT; // �������߶�

	public static int SIZE_DISCARD_WIDTH; // �������Ŀ��
	public static int SIZE_DISCARD_HEIGHT; // �������ĸ߶�

	public static int SIZE_KNOCK_WIDTH;
	public static int SIZE_KNOCK_HEIGHT;
	
	public static int SIZE_SORT_WIDTH = SIZE_CARD_WIDTH;
	public static int SIZE_SORT_HEIGHT = 50;
	
	public static int SIZE_CHANGESORT_WIDTH = SIZE_CARD_WIDTH;
	public static int SIZE_CHANGESORT_HEIGHT = 50;
	
}

class AhaGameState
{
	public static final int STATE_READY = 1; // �����Ѿ��������ȴ���ʼ��
	public static final int STATE_INITED_ROUND = 2; // ��Ϸ���Ѿ���ʼ�����
	public static final int STATE_INITED_HAND = 3; // ��Ϸ���Ѿ���ʼ�����
	public static final int STATE_DEALING = 4; // ������
	public static final int STATE_DRAWCARD = 5; // ���ƽ׶�
	public static final int STATE_DISCARD = 6; // ���ƽ׶�
	public static final int STATE_END = 7; // ĳ�����ʤ����Ľ��㻭��
}

class AhaTime
{
	/**
	 * ÿ�η���֮���ʱ����
	 */
	public static final int TIME_DEALCARD = 100;
}

class AhaTurnState
{
	static final int TURN_PLAYER = 1; // �����ǵ�ǰ��ҵĻغ�
	static final int TURN_OPPONENT = 2; // �����Ƕ��ֵĻغ�
	static final int TURN_NOBODY = 3; // ���ڲ��ǲ����׶Σ�����������
}

class AhaCoord
{
	// -------- ������ --------

	public static final int COORD_KNOCK_X = 40;
	public static final int COORD_KNOCK_Y = 355;

	// -------- ������ --------

	public static final int COORD_STOCK_X = 195; // ������x����
	public static final int COORD_STOCK_Y = 355; // ������y����

	// -------- ������ --------
	public static final int COORD_DISCARD_X = 350; // ��������x������
	public static final int COORD_DISCARD_Y = 355; // ��������y������

	// -------- ������ --------

	/**
	 * ���������������귶Χ��X����ࣨ���ָ����һ���Ƶ����Ͻǣ�
	 */
	public static final int COORD_CARD_X_LEFT = 15;

	/**
	 * ���������������귶Χ��X���Ҳࣨ���ָ�Ҳ��һ���Ƶ����Ͻǣ�
	 */
	public static final int COORD_CARD_X_RIGHT = 375;

	/**
	 * ��������SIMPLEģʽ�£������������Ƶ�ʱ��������֮��ļ��
	 */
	public static int COORD_CARD_X_SPACE_SIMPLE_7 = (COORD_CARD_X_RIGHT - COORD_CARD_X_LEFT) / 6;

	/**
	 * ��������SIMPLEģʽ�£������а����Ƶ�ʱ��������֮��ļ��
	 */
	public static int COORD_CARD_X_SPACE_SIMPLE_8 = (COORD_CARD_X_RIGHT - COORD_CARD_X_LEFT) / 7;

	/**
	 * ��������STANDARDģʽ�£�������ʮ���Ƶ�ʱ��������֮��ļ��
	 */
	public static int COORD_CARD_X_SPACE_STANDARD_10 =  (COORD_CARD_X_RIGHT - COORD_CARD_X_LEFT) / 9;
	
	/**
	 * ��������STANDARDģʽ�£�������ʮһ���Ƶ�ʱ��������֮��ļ��
	 */
	public static int COORD_CARD_X_SPACE_STANDARD_11 = (COORD_CARD_X_RIGHT - COORD_CARD_X_LEFT) / 10;
	
	/**
	 * ����������������������꣬Y��
	 */
	public static final int COORD_CARD_Y_PLAYER = 655;

	/**
	 * ����������������������꣬Y��
	 */
	public static final int COORD_CARD_Y_OPPONENT = 151;
	
	/**
	 * ����ťx������
	 */
	public static final int COORD_SORT_X = COORD_DISCARD_X;
	
	/**
	 * ����ťy������
	 */
	public static final int COORD_SORT_Y = 550;
	
	/**
	 * �л��������͵İ�ť��x������
	 */
	public static final int COORD_CHANGESORT_X = COORD_KNOCK_X;
	
	/**
	 * �л��������͵İ�ť��y������
	 */
	public static final int COORD_CHANGESORT_Y = 550;

}

class AhaGameMode
{
	public static final int GAMEMODE_SIMPLE = 1; // ����ģʽ��7���ƹ������׺�
	public static final int GAMEMODE_STANDARD = 2; // ��׼ģʽ��10���ƹ������׺�
}

class AhaTextHintString
{
	public static String nodisCardHintString = "��ʾ�����ƴ˻غϲ�����";
	public static String sortByRankString = "����ʽ�������С";
	public static String sortBySuitString = "����ʽ����ɫ";
	public static String sortByMeltString = "����ʽ:����";
	public static String sortString = "�Զ�����";
}

class AhaEngineMode
{
	public static final int NORMAL = 1;
	public static final int MODESELECT = 2;
	public static final int STAGE = 3;
}

/**
 * ʤ�����
 */
class AhaWinFlag
{
	public static final int NO_WIN = 1; 				// ��δ����ʤ��
	public static final int DRAW = 2; 				// ƽ��
	
	public static final int PLAYER_WIN = 11; 			// ��ҽ��
	public static final int PLAYER_MI_WIN = 12;		// ����׺�
	public static final int PLAYER_CHEAT_WIN = 13;	// ���թ�ͣ��׺�ʧ�ܣ�
	
	public static final int OPPONENT_WIN = 21;		// ���ֽ��
	public static final int OPPONENT_MI_WIN = 22; 	// �����׺�
	public static final int OPPONENT_CHEAT_WIN = 23;	// ����թ�ͣ��׺�ʧ�ܣ�

}