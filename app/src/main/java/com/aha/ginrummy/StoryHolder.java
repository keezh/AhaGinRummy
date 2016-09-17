package com.aha.ginrummy;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class StoryHolder
{
	/**
	 * ��ǰ��ȡ���ı�����
	 */
	ArrayList<String> currentText;

	/**
	 * ��Ļ�������ַ�����ʵ����ʾ�����ݶ�������
	 */
	StringBuffer screenStringBuffer;

	/**
	 * ����������ֵ�Paint
	 */
	Paint paint;

	/**
	 * ���������������Ͻ�x������
	 */
	int holderX = 20;

	/**
	 * ���������������Ͻ�y������
	 */
	int holderY = 20;

	/**
	 * ����ÿ����ʾ���ַ�����
	 */
	int charNumPerLine;

	/**
	 * �����ַ��߶�
	 */
	int charHeight;

	/**
	 * �м��
	 */
	int blankHeight = 10;
	// ----------------

	/**
	 * ��ǰ��ȡ���ı����
	 */
	int stringIndex;

	/**
	 * ��ǰ��ȡ���ַ����
	 */
	int charIndex;

	/**
	 * ���б�� <br>
	 * ���ΪTrue�������ȡ��ǰStringֱ����ȡ��� <br>
	 * ���ΪFalse��ֹͣ��ȡ��ֱ����Ǳ��ı�
	 */
	boolean isRunning;

	/**
	 * ������� <br>
	 * ���ΪTrue��˵����ǰText�Ѿ���ȡ��ϡ� <br>
	 * ���Ϊfalse��˵����ǰText��δ��ȡ��ϡ�
	 */
	boolean isEnd;

	/**
	 * ���������
	 */
	AhaGameEngine engine;
	
	/**
	 * �������
	 */
	boolean isSkip;
	
	/**
	 * ���캯��
	 */
	public StoryHolder(AhaGameEngine _engine)
	{
		paint = new Paint();
		paint.setTextSize(22);
		paint.setARGB(255, 255, 255, 255);
		screenStringBuffer = new StringBuffer();
		isRunning = false;
		isEnd = true;
		charNumPerLine = 20;
		Rect tempRect = new Rect();
		paint.getTextBounds("A", 0, 1, tempRect);

		charHeight = tempRect.height();
		engine = _engine;

	}

	/**
	 * ������ֻ�ͼ������
	 */
	public void ClearBuffer()
	{
		screenStringBuffer.delete(0, screenStringBuffer.length());
	}

	/**
	 * ������������ȡ�ı�������ʾ
	 */
	public void Run()
	{		
		// ��δ����
		if (!isEnd)
		{
			isRunning = true;
		}
	}

	/**
	 * ������仰
	 */
	public void Skip()
	{
		if (isRunning)
		{
			isSkip = true;
		}
	}
	/**
	 * ���õ�ǰ�ľ����ı�
	 */
	public void SetCurrentText(ArrayList<String> _currentText)
	{
		currentText = _currentText;

		// �����ı����
		stringIndex = 0;

		// �����ַ����
		charIndex = 0;

		// �������б��
		isRunning = true;

		// ���ý������
		isEnd = false;

		// �������
		isSkip = false;
		
		// ���buffer
		ClearBuffer();
	}

	public void Update(long ElapsedTime)
	{
		if (isRunning)
		{
			// ��⣬�����һ���ַ���'#'��������
			if (currentText.get(stringIndex).charAt(charIndex) == '#')
			{
				charIndex++;
				ClearBuffer();
			}

			// ��⣬�����һ���ַ���'@'�����һ���������
			if (currentText.get(stringIndex).charAt(charIndex) == '$')
			{
				charIndex++;
				screenStringBuffer.append(engine.playerNameString);
			}
			
			// ��⣬�����һ���ַ���'%'�����һ�пո�
			if (currentText.get(stringIndex).charAt(charIndex) == '%')
			{
				for (int t = 1; t <= charNumPerLine; t++)
				{
					screenStringBuffer.append(' ');
				}
				stringIndex++;
				charIndex = 0;
			}
			if (isSkip)
			{
				for (int a = charIndex; a < currentText.get(stringIndex).length(); a++)
				{
					// �����@���û�
					if (currentText.get(stringIndex).charAt(a) == '$')
					{
						screenStringBuffer.append(engine.playerNameString);
					}
					// �����������ַ�
					else
					{
						screenStringBuffer.append(currentText.get(stringIndex).charAt(a));
					}
				}
				
				// ��ӿո�
				int extraBlankNum = charNumPerLine
						- screenStringBuffer.length() % charNumPerLine;
				for (int t = 1; t <= extraBlankNum; t++)
				{
					screenStringBuffer.append(' ');
				}
				// �ƶ�����һ��string
				stringIndex++;

				// �����ַ����
				charIndex = 0;

				// ���string����������޶ȣ�˵�����ı��Ѿ���ȡ����
				if (stringIndex >= currentText.size())
				{
					isEnd = true;
				}

				// ֹͣ�������С��ȴ��ֶ��л�����һ��String
				isRunning = false;
				isSkip = false;
			}
			else
			{
				// ��ȡ��һ���ַ���������ӵ�������֮��
				screenStringBuffer.append(currentText.get(stringIndex).charAt(
						charIndex));
	
				// ����ַ����ͣ��
				try
				{
					Thread.sleep(20);
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
				// �ַ���������ƶ�
				charIndex++;
	
				// ���String�Ѿ���ȡ���
				if (charIndex >= currentText.get(stringIndex).length())
				{
					int extraBlankNum = charNumPerLine
							- screenStringBuffer.length() % charNumPerLine;
					for (int t = 1; t <= extraBlankNum; t++)
					{
						screenStringBuffer.append(' ');
					}
					// �ƶ�����һ��string
					stringIndex++;
	
					// �����ַ����
					charIndex = 0;
	
					// ���string����������޶ȣ�˵�����ı��Ѿ���ȡ����
					if (stringIndex >= currentText.size())
					{
						isEnd = true;
					}
	
					// ֹͣ�������С��ȴ��ֶ��л�����һ��String
					isRunning = false;
				}
			}

		}
	}

	public void Draw(Canvas canvas, long ElapsedTime)
	{
		int line = 0;

		while (line * charNumPerLine + charNumPerLine < screenStringBuffer
				.length())
		{
			canvas.drawText(screenStringBuffer.substring(line * charNumPerLine,
					(line + 1) * charNumPerLine), holderX, holderY + line
					* (charHeight + blankHeight), paint);
			line++;
		}
		canvas.drawText(screenStringBuffer.substring(line * charNumPerLine,
				screenStringBuffer.length()), holderX, holderY + line
				* (charHeight + blankHeight), paint);
	}
}
