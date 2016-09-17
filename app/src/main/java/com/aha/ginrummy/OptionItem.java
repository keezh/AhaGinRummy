package com.aha.ginrummy;

import android.R.bool;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TwoLineListItem;
import com.aha.ginrummy.OptionBool;
/*
 * 这是一个自定义组件类,组件包含了两个TextView组件,来实现单独的字体
 * CheckBox为可选组件
 */

public class OptionItem extends RelativeLayout
{
	public static final int MAINTEXT = 1;
	public static final int SUBTEXT = 2;
	
	public OptionItem owner = this;
	private TextView mainTextView = null;
	private TextView subTextView = null;
	private CheckBox checkBox = null;
	
	private OptionBool isChecked ;		//用来记录CheckBox的当前状态
	private boolean hasCheckBox = false;	//是否有CheckBox标识
	
	public OptionItem(Context context)
	{
		super(context);
	}
	public OptionItem(Context context,String mainTxt,String subTxt,boolean hasCheckBox,OptionBool isChecked)
	{
		super(context);
		this.hasCheckBox = hasCheckBox;
		mainTextView.setText(mainTxt);
		subTextView.setText(subTxt);
		
		textViewInit();		//用来对TextView部分的初始化
		
		checkBoxInit(isChecked);		//用来对CheckBox的初始化
	}
	public OptionItem(Context context,String mainTxt,String subTxt)
	{
		super(context);
		mainTextView.setText(mainTxt);
		subTextView.setText(subTxt);
		textViewInit();
	}
	/*
	 * txtType值为MAINTEXT或SUBTEXT,用来对两行文本进行操作
	 */
	public void setText(int txtType,CharSequence text)
	{
		if(MAINTEXT == txtType)
			mainTextView.setText(text);
		else if(SUBTEXT == txtType)
			subTextView.setText(text);
	}
	public CharSequence getText(int txtType)
	{
		if(MAINTEXT == txtType)
			return mainTextView.getText();
		else if(SUBTEXT == txtType)
			return subTextView.getText();
		else 
			return null;
	}
	/*
	 * 获取CheckBox当前状态
	 */
	public boolean isChecked()
	{
		return isChecked.getBool();
	}
	/*
	 * 该函数可以在组件创建完成后添加CheckBox
	 */
	public void addCheckBox(OptionBool isChecked)
	{
		hasCheckBox = true;
		checkBoxInit(isChecked);
	}
	/*
	 * 该函数可以在组件创建完成后移除CheckBox
	 */
	public void removeCheckBox()
	{
		hasCheckBox = false;
		this.removeView(checkBox);
		checkBox = null;
	}
	
	public void setCheckBoxStatus(Boolean isChecked)
	{		
		checkBox.setChecked(isChecked);
	}
	private void textViewInit()
	{			
		/*
		 * 获取一个应用于ListView的属性集,来动态添加View的属性
		 */
		android.widget.AbsListView.LayoutParams params = new android.widget.AbsListView.LayoutParams(android.widget.AbsListView.LayoutParams.FILL_PARENT,android.widget.AbsListView.LayoutParams.WRAP_CONTENT);
		this.setLayoutParams(params);
		/*
		 * 为主TextView添加应用于RelativeLayout的属性集
		 * 添加layout_width和layout_height属性
		 */
		LayoutParams mainParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		/*
		 * 增加对父窗口的对齐方式对应layout_alignParentLeft和layout_alignParentTop
		 */
		mainParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		mainParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		/*
		 * 更改主TextView的属性
		 * 包括	颜色
		 * 		字体大小
		 * 		ID
		 */
		mainTextView.setTextColor(Color.WHITE);
		mainTextView.setTextSize(22.0f);
		mainTextView.setLayoutParams(mainParams);
		mainTextView.setId(0x000001);
		/*
		 * 为次TextView添加应用于RelativeLayout的属性集
		 * 添加layout_width和layout_height属性
		 * 设置颜色和字体
		 */
		LayoutParams subParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		subParams.addRule(RelativeLayout.BELOW, 0x000001);
		subTextView.setLayoutParams(subParams);
		subTextView.setTextSize(15.0f);
		
		this.addView(mainTextView);
		this.addView(subTextView);
	}
	
	private void checkBoxInit(OptionBool isChecked)
	{
		this.isChecked = isChecked;
		checkBox = new CheckBox(super.getContext());
		checkBox.setChecked(this.isChecked.getBool());
		/*
		 * 为次CheckBox添加应用于RelativeLayout的属性集
		 * 添加layout_width和layout_height属性
		 * 设置相对父组件的位置
		 * layout_alignParentRight和layout_alignParentTop
		 */
		LayoutParams checkParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		checkParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
		checkParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.TRUE);
		checkBox.setLayoutParams(checkParams);
		checkBox.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				/*
				 * 用来标记CheckBox的当前状态
				 */		
				String subString =	OptionItem.this.subTextView.getText().toString();
				if(OptionItem.this.isChecked.getBool())
					subString = subString.replaceFirst("已开启", "已关闭");
				else 
					subString = subString.replaceFirst("已关闭", "已开启");					
				OptionItem.this.subTextView.setText(subString);
				OptionItem.this.isChecked.setOpposite();
				
			}
		});
		checkBox.setFocusable(false);
		this.addView(checkBox);
		
	}

	{
		/*
		 * 为TextView分配内存
		 * 先执行这里然后执行构造函数
		 */
		mainTextView = new TextView(super.getContext());
		subTextView = new TextView(super.getContext());		
	}
	
}

//Version 0.2