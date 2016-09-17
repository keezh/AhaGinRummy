package com.aha.ginrummy;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class StoryHolder
{
	/**
	 * 当前读取的文本数据
	 */
	ArrayList<String> currentText;

	/**
	 * 屏幕缓冲区字符串。实际显示的内容都在这里
	 */
	StringBuffer screenStringBuffer;

	/**
	 * 用以输出文字的Paint
	 */
	Paint paint;

	/**
	 * 文字输出区域的左上角x轴坐标
	 */
	int holderX = 20;

	/**
	 * 文字输出区域的左上角y轴坐标
	 */
	int holderY = 20;

	/**
	 * 定义每行显示的字符数量
	 */
	int charNumPerLine;

	/**
	 * 单个字符高度
	 */
	int charHeight;

	/**
	 * 行间距
	 */
	int blankHeight = 10;
	// ----------------

	/**
	 * 当前读取的文本标号
	 */
	int stringIndex;

	/**
	 * 当前读取的字符标号
	 */
	int charIndex;

	/**
	 * 运行标记 <br>
	 * 如果为True则持续读取当前String直到读取完毕 <br>
	 * 如果为False则停止读取，直到标记被改变
	 */
	boolean isRunning;

	/**
	 * 结束标记 <br>
	 * 如果为True则说明当前Text已经读取完毕。 <br>
	 * 如果为false则说明当前Text尚未读取完毕。
	 */
	boolean isEnd;

	/**
	 * 引擎的引用
	 */
	AhaGameEngine engine;
	
	/**
	 * 跳过标记
	 */
	boolean isSkip;
	
	/**
	 * 构造函数
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
	 * 清除文字绘图缓冲区
	 */
	public void ClearBuffer()
	{
		screenStringBuffer.delete(0, screenStringBuffer.length());
	}

	/**
	 * 重启，继续读取文本并且显示
	 */
	public void Run()
	{		
		// 尚未结束
		if (!isEnd)
		{
			isRunning = true;
		}
	}

	/**
	 * 跳过这句话
	 */
	public void Skip()
	{
		if (isRunning)
		{
			isSkip = true;
		}
	}
	/**
	 * 设置当前的剧情文本
	 */
	public void SetCurrentText(ArrayList<String> _currentText)
	{
		currentText = _currentText;

		// 重置文本标号
		stringIndex = 0;

		// 重置字符标号
		charIndex = 0;

		// 重置运行标记
		isRunning = true;

		// 重置结束标记
		isEnd = false;

		// 跳过标记
		isSkip = false;
		
		// 清空buffer
		ClearBuffer();
	}

	public void Update(long ElapsedTime)
	{
		if (isRunning)
		{
			// 检测，如果下一个字符是'#'，则清屏
			if (currentText.get(stringIndex).charAt(charIndex) == '#')
			{
				charIndex++;
				ClearBuffer();
			}

			// 检测，如果下一个字符是'@'，添加一个玩家姓名
			if (currentText.get(stringIndex).charAt(charIndex) == '$')
			{
				charIndex++;
				screenStringBuffer.append(engine.playerNameString);
			}
			
			// 检测，如果下一个字符是'%'，添加一行空格
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
					// 如果是@则置换
					if (currentText.get(stringIndex).charAt(a) == '$')
					{
						screenStringBuffer.append(engine.playerNameString);
					}
					// 否则添加这个字符
					else
					{
						screenStringBuffer.append(currentText.get(stringIndex).charAt(a));
					}
				}
				
				// 添加空格
				int extraBlankNum = charNumPerLine
						- screenStringBuffer.length() % charNumPerLine;
				for (int t = 1; t <= extraBlankNum; t++)
				{
					screenStringBuffer.append(' ');
				}
				// 移动到下一个string
				stringIndex++;

				// 重置字符标记
				charIndex = 0;

				// 如果string超过的最大限度，说明该文本已经读取结束
				if (stringIndex >= currentText.size())
				{
					isEnd = true;
				}

				// 停止继续运行。等待手动切换到下一个String
				isRunning = false;
				isSkip = false;
			}
			else
			{
				// 读取下一个字符，并且添加到缓冲区之中
				screenStringBuffer.append(currentText.get(stringIndex).charAt(
						charIndex));
	
				// 添加字符后的停顿
				try
				{
					Thread.sleep(20);
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
				// 字符标记向下移动
				charIndex++;
	
				// 这个String已经读取完成
				if (charIndex >= currentText.get(stringIndex).length())
				{
					int extraBlankNum = charNumPerLine
							- screenStringBuffer.length() % charNumPerLine;
					for (int t = 1; t <= extraBlankNum; t++)
					{
						screenStringBuffer.append(' ');
					}
					// 移动到下一个string
					stringIndex++;
	
					// 重置字符标记
					charIndex = 0;
	
					// 如果string超过的最大限度，说明该文本已经读取结束
					if (stringIndex >= currentText.size())
					{
						isEnd = true;
					}
	
					// 停止继续运行。等待手动切换到下一个String
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
