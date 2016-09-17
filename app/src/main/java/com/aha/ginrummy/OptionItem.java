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
 * ����һ���Զ��������,�������������TextView���,��ʵ�ֵ���������
 * CheckBoxΪ��ѡ���
 */

public class OptionItem extends RelativeLayout
{
	public static final int MAINTEXT = 1;
	public static final int SUBTEXT = 2;
	
	public OptionItem owner = this;
	private TextView mainTextView = null;
	private TextView subTextView = null;
	private CheckBox checkBox = null;
	
	private OptionBool isChecked ;		//������¼CheckBox�ĵ�ǰ״̬
	private boolean hasCheckBox = false;	//�Ƿ���CheckBox��ʶ
	
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
		
		textViewInit();		//������TextView���ֵĳ�ʼ��
		
		checkBoxInit(isChecked);		//������CheckBox�ĳ�ʼ��
	}
	public OptionItem(Context context,String mainTxt,String subTxt)
	{
		super(context);
		mainTextView.setText(mainTxt);
		subTextView.setText(subTxt);
		textViewInit();
	}
	/*
	 * txtTypeֵΪMAINTEXT��SUBTEXT,�����������ı����в���
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
	 * ��ȡCheckBox��ǰ״̬
	 */
	public boolean isChecked()
	{
		return isChecked.getBool();
	}
	/*
	 * �ú������������������ɺ����CheckBox
	 */
	public void addCheckBox(OptionBool isChecked)
	{
		hasCheckBox = true;
		checkBoxInit(isChecked);
	}
	/*
	 * �ú������������������ɺ��Ƴ�CheckBox
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
		 * ��ȡһ��Ӧ����ListView�����Լ�,����̬���View������
		 */
		android.widget.AbsListView.LayoutParams params = new android.widget.AbsListView.LayoutParams(android.widget.AbsListView.LayoutParams.FILL_PARENT,android.widget.AbsListView.LayoutParams.WRAP_CONTENT);
		this.setLayoutParams(params);
		/*
		 * Ϊ��TextView���Ӧ����RelativeLayout�����Լ�
		 * ���layout_width��layout_height����
		 */
		LayoutParams mainParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		/*
		 * ���ӶԸ����ڵĶ��뷽ʽ��Ӧlayout_alignParentLeft��layout_alignParentTop
		 */
		mainParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		mainParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		/*
		 * ������TextView������
		 * ����	��ɫ
		 * 		�����С
		 * 		ID
		 */
		mainTextView.setTextColor(Color.WHITE);
		mainTextView.setTextSize(22.0f);
		mainTextView.setLayoutParams(mainParams);
		mainTextView.setId(0x000001);
		/*
		 * Ϊ��TextView���Ӧ����RelativeLayout�����Լ�
		 * ���layout_width��layout_height����
		 * ������ɫ������
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
		 * Ϊ��CheckBox���Ӧ����RelativeLayout�����Լ�
		 * ���layout_width��layout_height����
		 * ������Ը������λ��
		 * layout_alignParentRight��layout_alignParentTop
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
				 * �������CheckBox�ĵ�ǰ״̬
				 */		
				String subString =	OptionItem.this.subTextView.getText().toString();
				if(OptionItem.this.isChecked.getBool())
					subString = subString.replaceFirst("�ѿ���", "�ѹر�");
				else 
					subString = subString.replaceFirst("�ѹر�", "�ѿ���");					
				OptionItem.this.subTextView.setText(subString);
				OptionItem.this.isChecked.setOpposite();
				
			}
		});
		checkBox.setFocusable(false);
		this.addView(checkBox);
		
	}

	{
		/*
		 * ΪTextView�����ڴ�
		 * ��ִ������Ȼ��ִ�й��캯��
		 */
		mainTextView = new TextView(super.getContext());
		subTextView = new TextView(super.getContext());		
	}
	
}

//Version 0.2