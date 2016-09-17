package com.aha.ginrummy;

import java.util.ArrayList;

/**
 * 用以记录一轮比赛得分信息的对象
 *
 */
public class ScoreRecord
{	
	public ArrayList<ScoreItem> record;
	private ScoreBoardDialog dialog;
	public int playerTotalScore;
	public int opponentTotalSocre;
	
	
	public ScoreRecord()
	{
		record = new ArrayList<ScoreItem>();
	}
	
	public void Init()
	{
		record.clear();
		playerTotalScore = 0;
		opponentTotalSocre = 0;
	}
	public void setParentDialog(ScoreBoardDialog dialog)
	{
		this.dialog = dialog;
	}
	public void AddRecord(int _playerMi, int _playerBonus, int _opponentMi, int _opponentBonus, int _whoWin)
	{
		ScoreItem tempItem = new ScoreItem(_playerMi, _playerBonus, _opponentMi, _opponentBonus, _whoWin);
		record.add(tempItem);
		ScoreItem item = this.getLastHand();
		dialog.addScore(
				record.size(),
				((item.opponentMi - item.playerMi) > 0 ? (item.opponentMi - item.playerMi)
						: 0)
						+ item.playerBonus,
				((item.playerMi - item.opponentMi) > 0 ? (item.playerMi - item.opponentMi)
						: 0)
						+ item.opponentBonus);
	}
	
	public void AddRecord(ScoreItem tempItem)
	{
		record.add(tempItem);
		ScoreItem item = this.getLastHand();
		dialog.addScore(
				record.size(),
				((item.opponentMi - item.playerMi) > 0 ? (item.opponentMi - item.playerMi)
						: 0)
						+ item.playerBonus,
				((item.playerMi - item.opponentMi) > 0 ? (item.playerMi - item.opponentMi)
						: 0)
						+ item.opponentBonus);
	}
	
	public ScoreItem getLastHand()
	{
		return record.get(record.size()-1);
	}
}

/**
 * 放置在容器中的，保存每一局得分记录的对象
 *
 */
class ScoreItem
{
	public int playerMi;
	public int playerBonus;
	
	public int opponentMi;
	public int opponentBonus;
	
	public int whoWin;
	
	public ScoreItem(int _playerMi, int _playerBonus, int _opponentMi, int _opponentBonus, int _whoWin)
	{
		playerMi = _playerMi;
		playerBonus = _playerBonus;
		opponentMi = _opponentMi;
		opponentBonus = _opponentBonus;
		whoWin = _whoWin;
	}
}