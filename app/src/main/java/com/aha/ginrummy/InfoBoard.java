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
		// ���Ʊ���
		canvas.drawBitmap(boardBackImage, x, y, null);

		// ��ʾ�������
		canvas.drawText("���������" + engine.playerNameString, x + 30, y + 25,
				paint);

		// ��ʾ��ǰ�÷�
		canvas.drawText("��ǰ�ܷ֣�" + engine.scoreRecord.playerTotalScore, x + 30, y + 45, paint);

		// ��ʾ��ǰ����
		canvas.drawText("��ǰ������" + engine.currentHand, x + 30, y + 65, paint);

		// ��ʾ��ǰ�׵�
		canvas.drawText("��ǰ�׵㣺" + engine.playerHand.miPoint, x + 30, y + 85,
				paint);

		// ��ʾ��ǰ����ʽ
		switch (engine.playerHand.sortType) {
		case AhaHandSortType.SORT_BY_MELT:
		{
			canvas.drawText("����ʽ������", x + 30, y + 105, paint);
		}
			break;
		case AhaHandSortType.SORT_BY_RANK:
		{
			canvas.drawText("����ʽ�������С", x + 30, y + 105, paint);
		}
			break;
		case AhaHandSortType.SORT_BY_SUIT:
		{
			canvas.drawText("����ʽ����ɫ", x + 30, y + 105, paint);
		}
			break;
		}

		// ��������
		canvas.drawText("����������" + engine.opponentNameString,
				x + boardBackImage.getWidth() / 2 + 30, y + 25, paint);

		// ���ֵ÷�
		canvas.drawText("�����ܷ֣�" + engine.scoreRecord.opponentTotalSocre, x + boardBackImage.getWidth() / 2 + 30,
				y + 45, paint);

		// ���ֵȼ�
		canvas.drawText("���ֵȼ���" + engine.AILevel, x + boardBackImage.getWidth()
				/ 2 + 30, y + 65, paint);

		// ��ʾʣ������
		canvas.drawText("������ʣ��������" + engine.stockPile.GetCardCount(), x
				+ boardBackImage.getWidth() / 2 + 30, y + 85, paint);

	}
}
