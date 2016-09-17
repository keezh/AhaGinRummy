package com.aha.ginrummy;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class InfoBoard
{
	Bitmap boardBackImage;
	Paint paint;
	int x;
	int y;
	AhaGameEngine engine;
	
	int width;
	int height;
	
	public InfoBoard(Resources res, int id, AhaGameEngine _engine)
	{
		boardBackImage = BitmapFactory.decodeResource(res, id);
		paint = new Paint();
		paint.setARGB(255, 255, 255, 255);
		paint.setTextSize(17);
		engine = _engine;
		
		width = boardBackImage.getWidth();
		height = boardBackImage.getHeight();
		x = 0;
		y = 0;
		Log.w("X", "x:" + x);
	}

	public void Draw(Canvas canvas, long ElapsedTime)
	{
		// 绘制背景
		canvas.drawBitmap(boardBackImage, x, y, null);

		// 显示玩家姓名
		canvas.drawText("玩家姓名：" + engine.playerNameString, x + 30, y + 25,
				paint);

		// 显示当前得分
		canvas.drawText("当前总分：" + engine.scoreRecord.playerTotalScore, x + 30, y + 45, paint);

		// 显示当前局数
		canvas.drawText("当前局数：" + engine.currentHand, x + 30, y + 65, paint);

		// 显示当前米点
		canvas.drawText("当前米点：" + engine.playerHand.miPoint, x + 30, y + 85,
				paint);

		// 显示当前排序方式
		switch (engine.playerHand.sortType) {
		case AhaHandSortType.SORT_BY_MELT:
		{
			canvas.drawText("排序方式：分组", x + 30, y + 105, paint);
		}
			break;
		case AhaHandSortType.SORT_BY_RANK:
		{
			canvas.drawText("排序方式：牌面大小", x + 30, y + 105, paint);
		}
			break;
		case AhaHandSortType.SORT_BY_SUIT:
		{
			canvas.drawText("排序方式：花色", x + 30, y + 105, paint);
		}
			break;
		}

		// 对手姓名
		canvas.drawText("对手姓名：" + engine.opponentNameString,
				x + boardBackImage.getWidth() / 2 + 30, y + 25, paint);

		// 对手得分
		canvas.drawText("对手总分：" + engine.scoreRecord.opponentTotalSocre, x + boardBackImage.getWidth() / 2 + 30,
				y + 45, paint);

		// 对手等级
		canvas.drawText("对手等级：" + engine.AILevel, x + boardBackImage.getWidth()
				/ 2 + 30, y + 65, paint);

		// 显示剩余牌数
		canvas.drawText("抽牌区剩余牌数：" + engine.stockPile.GetCardCount(), x
				+ boardBackImage.getWidth() / 2 + 30, y + 85, paint);

	}
}
