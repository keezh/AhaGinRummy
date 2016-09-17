package com.aha.ginrummy;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class ScoreBoardDialog extends Dialog
{
	private int gameState = 0;
	private ScoreRecord scoreRecord = null;
	private TextView thisHandScore = null;

	private Button goHome = null;
	private Button nextHand = null;
	private Button newGame = null;
	private Button continueGame = null;
	private Button examine = null;
	private TextView gamingTitle = null;

	private TableLayout scoreTable = null;
	private TableRow sumPart = null;
	private LinearLayout scoreMainBoard = null;
	private LinearLayout finalHandBtns = null;
	private ScrollView scoreChildBoard = null;

	private LinearLayout topPart = null;
	private LinearLayout bottomPart = null;
	private AhaGameEngine engine;
	private MiddleActivity owner;

	public ScoreBoardDialog(Context context, int theme,
			ScoreRecord scoreRecord, AhaGameEngine _engine)
	{
		super(context, theme);
		this.scoreRecord = scoreRecord;
		engine = _engine;
		importScore();
	}

	public ScoreBoardDialog(Context context, ScoreRecord scoreRecord,
			AhaGameEngine _engine)
	{
		super(context);
		this.scoreRecord = scoreRecord;
		engine = _engine;
		importScore();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	public void show(int gameState)
	{

		WindowManager m = this.getWindow().getWindowManager();
		Display d = m.getDefaultDisplay();

		WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值

		p.height = d.getHeight() * 3 / 4;
		p.width = d.getWidth() * 5 / 6;

		scoreMainBoard.setBackgroundResource(R.drawable.scoreboardbackground);

		scoreMainBoard.setPadding(p.height / 30, p.height / 20, p.height / 30,
				p.height / 30);
		topPart.getLayoutParams().height = p.height * 4 / 13;
		scoreChildBoard.getLayoutParams().height = p.height * 11 / 31;

		p.alpha = 1.0f;// 设置对话框透明度
		p.dimAmount = 0.8f; // 设置下一层Activity的黑暗度

		this.gameState = gameState;
		if (this.gameState == GameState.GAMING)
		{
			topPart.addView(gamingTitle,0);
			bottomPart.addView(continueGame, bottomPart.getChildCount());
		} else if (this.gameState == GameState.END_OF_HAND)
		{
			setCurrentHandScore(this.scoreRecord);
			topPart.addView(thisHandScore, 0);
			topPart.addView(examine,1);
			bottomPart.addView(nextHand, bottomPart.getChildCount());
		} else if (this.gameState == GameState.END_OF_GAME)
		{
			setCurrentHandScore(this.scoreRecord);
			topPart.addView(thisHandScore, 0);
			topPart.addView(examine,1);
			bottomPart.addView(finalHandBtns, bottomPart.getChildCount());
		}
		else if(this.gameState == GameState.END_OF_STAGE)
		{
			setCurrentHandScore(this.scoreRecord);
			topPart.addView(thisHandScore, 0);
			topPart.addView(examine,1);
			bottomPart.addView(continueGame, bottomPart.getChildCount());
		}
		show();
	}

	@Override
	protected void onStop()
	{
		if (gameState == GameState.END_OF_GAME)
		{
			topPart.removeView(thisHandScore);
			topPart.removeView(examine);
			bottomPart.removeView(finalHandBtns);
		} else if (gameState == GameState.END_OF_HAND)
		{
			topPart.removeView(thisHandScore);
			topPart.removeView(examine);
			bottomPart.removeView(nextHand);
		} else if (gameState == GameState.GAMING)
		{
			topPart.removeView(gamingTitle);
			bottomPart.removeView(continueGame);
		} else if(gameState==GameState.END_OF_STAGE)
		{
			topPart.removeView(thisHandScore);
			topPart.removeView(examine);
			bottomPart.removeView(continueGame);
		}
		gameState = 0;
		super.onStop();
	}

	private void setCurrentHandScore(ScoreRecord record)
	{
		ScoreItem latestHand = record.getLastHand();

		StringBuilder thisHandScoreStr = new StringBuilder();

		switch (latestHand.whoWin)
		{
		case AhaWinFlag.OPPONENT_WIN:
			thisHandScoreStr.append(engine.opponentNameString + " 金胡：\n得到了：");
			break;
		case AhaWinFlag.PLAYER_WIN:
			thisHandScoreStr.append(engine.playerNameString + " 金胡:\n得到了：");
			break;
		case AhaWinFlag.DRAW:
			thisHandScoreStr.append("平局，真是遗憾");
			break;
		case AhaWinFlag.PLAYER_MI_WIN:
			thisHandScoreStr.append(engine.playerNameString + " 米胡:\n得到了：");
			break;
		case AhaWinFlag.OPPONENT_MI_WIN:
			thisHandScoreStr.append(engine.opponentNameString + " 米胡：\n得到了：");
			break;
		case AhaWinFlag.PLAYER_CHEAT_WIN:
			thisHandScoreStr.append(engine.playerNameString + " 诈胡:\n嘉嘉萌得到了：");
			break;
		case AhaWinFlag.OPPONENT_CHEAT_WIN:
			thisHandScoreStr.append(engine.opponentNameString + " 诈胡:\n" + engine.playerNameString
					+ "得到了：");
			break;
		default:
			break;
		}

		switch (latestHand.whoWin)
		{
		case AhaWinFlag.OPPONENT_WIN:
			thisHandScoreStr
					.append((latestHand.playerMi - latestHand.opponentMi)
							+ " + " + latestHand.opponentBonus);
			thisHandScoreStr.append(" 分。");
			break;
		case AhaWinFlag.PLAYER_WIN:
			thisHandScoreStr
					.append((latestHand.opponentMi - latestHand.playerMi)
							+ " + " + latestHand.playerBonus);
			thisHandScoreStr.append(" 分。");
			break;
		case AhaWinFlag.PLAYER_MI_WIN:
			thisHandScoreStr
					.append(latestHand.opponentMi - latestHand.playerMi);
			thisHandScoreStr.append(" 分。");
			break;
		case AhaWinFlag.OPPONENT_MI_WIN:
			thisHandScoreStr
					.append(latestHand.playerMi - latestHand.opponentMi);
			thisHandScoreStr.append(" 分。");
			break;
		case AhaWinFlag.PLAYER_CHEAT_WIN:
			thisHandScoreStr
					.append((latestHand.playerMi - latestHand.opponentMi)
							+ " + " + latestHand.opponentBonus);
			thisHandScoreStr.append(" 分。");
			break;
		case AhaWinFlag.OPPONENT_CHEAT_WIN:
			thisHandScoreStr
					.append((latestHand.opponentMi - latestHand.playerMi)
							+ " + " + latestHand.playerBonus);
			thisHandScoreStr.append(" 分。");
			break;

		default:
			break;
		}
		
		thisHandScore.setText(thisHandScoreStr);

	}

	public void addScore(int hand, int scorePlayer, int scoreCom)
	{
		TableRow child = new TableRow(super.getContext());
		TextView tvChild = new TextView(super.getContext());

		tvChild.setText("" + hand);
		tvChild.setTextColor(Color.BLACK);
		tvChild.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
		child.addView(tvChild, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		tvChild = new TextView(super.getContext());
		tvChild.setTextColor(Color.BLACK);
		tvChild.setText("" + scorePlayer);
		tvChild.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
		child.addView(tvChild, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		tvChild = new TextView(super.getContext());
		tvChild.setTextColor(Color.BLACK);
		tvChild.setText("" + scoreCom);
		tvChild.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
		child.addView(tvChild, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		int sumPlayer = 0;
		int sumCom = 0;

		sumPlayer = Integer.parseInt(((TextView) sumPart.getChildAt(1))
				.getText().toString());
		sumCom = Integer.parseInt(((TextView) sumPart.getChildAt(2)).getText()
				.toString());
		sumPlayer += scorePlayer;
		engine.scoreRecord.playerTotalScore = sumPlayer;
		sumCom += scoreCom;
		engine.scoreRecord.opponentTotalSocre = sumCom;
		((TextView) sumPart.getChildAt(1)).setText(sumPlayer + "");
		((TextView) sumPart.getChildAt(2)).setText(sumCom + "");

		scoreTable.addView(child);

	}

	private void importScore()
	{

		for (int i = 0; i < this.scoreRecord.record.size(); ++i)
		{
			ScoreItem item = scoreRecord.record.get(i);
			addScore(
					i+1,
					((item.opponentMi - item.playerMi) > 0 ? (item.opponentMi - item.playerMi)
							: 0)
							+ item.playerBonus,
					((item.playerMi - item.opponentMi) > 0 ? (item.playerMi - item.opponentMi)
							: 0)
							+ item.opponentBonus);
		}
	}

	public void setOnClickListener(View.OnClickListener listener)
	{
			goHome.setOnClickListener(listener);
			nextHand.setOnClickListener(listener);
			continueGame.setOnClickListener(listener);
			newGame.setOnClickListener(listener);	
			examine.setOnClickListener(listener);
	}

	public void setOwner(MiddleActivity owner)
	{
		this.owner = owner;
	}
	
	@Override
	public void dismiss()
	{
		owner.finish();
		super.dismiss();
	}

	{

		setContentView(R.layout.scoreboardlinear);
		thisHandScore = (TextView) findViewById(R.id.scoreboard_tv_currentscore);
		goHome = (Button) findViewById(R.id.scoreboard_btn_returnhome);
		nextHand = (Button) findViewById(R.id.scoreboard_btn_nexthand);
		newGame = (Button) findViewById(R.id.scoreboard_btn_newgame);
		continueGame = (Button) findViewById(R.id.scoreboard_btn_continue);
		examine = (Button)findViewById(R.id.scoreboard_btn_examine);
		gamingTitle = (TextView)findViewById(R.id.scoreboard_tv_gaming_title);
		
		scoreMainBoard = (LinearLayout) findViewById(R.id.scoreboard_ll_scoreboard);
		finalHandBtns = (LinearLayout) findViewById(R.id.scoreboard_ll_finalhandbtn);
		scoreChildBoard = (ScrollView) findViewById(R.id.scoreboard_sv_scoreboard);

		scoreTable = (TableLayout) findViewById(R.id.scoreboard_tl_scoretable);
		sumPart = (TableRow) findViewById(R.id.scoreboard_tr_sumpart);

		topPart = (LinearLayout) findViewById(R.id.scoreboard_ll_toppart);
		bottomPart = (LinearLayout) findViewById(R.id.scoreboard_ll_bottompart);

		topPart.removeView(thisHandScore);
		topPart.removeView(examine);
		topPart.removeView(gamingTitle);
		
		bottomPart.removeView(finalHandBtns);
		bottomPart.removeView(nextHand);
		bottomPart.removeView(continueGame);
	}
}
