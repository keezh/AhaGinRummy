package com.aha.ginrummy;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

public class OptionsActivity extends ListActivity
{

	OptionBool isSoundEffectOn;
	OptionBool isDebugModeOn;
	String playerName;
	String optionTextList[]; // ����ѡ������ı�
	String optionTipList[]; // ����ѡ��ĸ��ı�
	String AILevelStrs[];
	String speedLevelStrs[];

	boolean aiLevelBool[];
	AhaGameEngine engine;
	int AILevel;
	int speedLevel;
	private final int RENAME = 0x0001;

	@Override
	protected void onDestroy()
	{
		engine.isSoundEffectOn = isSoundEffectOn.getBool();
		engine.debugMode = isDebugModeOn.getBool();
		engine.rushModeTimer = speedLevel;
		engine.AILevel = this.AILevel + 1;
		AhaSavior.SaveAll(engine, this);
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		engine = ((AhaGameEngine) getApplicationContext());

		AILevelStrs = getResources().getStringArray(R.array.ailevel);
		playerName = engine.playerNameString;
		AILevel = engine.AILevel - 1;

		speedLevelStrs = getResources().getStringArray(R.array.speedlevel);
		speedLevel = (int) engine.rushModeTimer;

		isSoundEffectOn = new OptionBool(engine.isSoundEffectOn);
		isDebugModeOn = new OptionBool(engine.debugMode);

		optionTextList = getResources().getStringArray(R.array.OptionsList);
		optionTipList = getResources().getStringArray(R.array.OptionsTips);
		setListAdapter(optionsAdapter);
		getListView().setOnItemClickListener(optionListener);

	}

	BaseAdapter optionsAdapter = new BaseAdapter()
	{

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			switch (position)
			{
			case 0:
				OptionItem nameView = new OptionItem(OptionsActivity.this,
						optionTextList[position], optionTipList[position]+playerName);
				return nameView;
			case 1:
				OptionItem checkView = new OptionItem(OptionsActivity.this,
						optionTextList[position], optionTipList[position]+isSoundEffectOn.getString(),
						true, OptionsActivity.this.isSoundEffectOn);
				return checkView;
			case 2:
				OptionItem aiView = new OptionItem(OptionsActivity.this,
						optionTextList[position], optionTipList[position]+(AILevel+1));
				return aiView;
			case 3:
				OptionItem timerView = new OptionItem(OptionsActivity.this,
						optionTextList[position], optionTipList[position]+speedLevel/1000);
				return timerView;
			case 4:
				OptionItem debugView = new OptionItem(OptionsActivity.this,
						optionTextList[position], optionTipList[position]+isDebugModeOn.getString(),
						true, OptionsActivity.this.isDebugModeOn);
				return debugView;
			case 5:
				OptionItem resetView = new OptionItem(OptionsActivity.this,
						optionTextList[position], optionTipList[position]);
				return resetView;
			default:
				return null;
			}
		}

		@Override
		public long getItemId(int position)
		{
			return 0;
		}

		@Override
		public Object getItem(int position)
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return optionTextList.length;
		}
	};

	OnClickListener resetConfirm = new OnClickListener()
	{

		@Override
		public void onClick(DialogInterface dialog, int which)
		{
			switch (which)
			{
			case DialogInterface.BUTTON_POSITIVE:
				Toast.makeText(OptionsActivity.this, "ResetOptions",
						Toast.LENGTH_SHORT).show();
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				dialog.dismiss();
				break;
			default:
				break;
			}
		}
	};

	OnClickListener levelListener = new OnClickListener()
	{

		@Override
		public void onClick(DialogInterface dialog, int which)
		{
			OptionsActivity.this.AILevel = which;
			((OptionItem)OptionsActivity.this.getListView().getChildAt(2)).setText(OptionItem.SUBTEXT, optionTipList[2]+(AILevel+1));
			dialog.dismiss();
		}
	};

	OnClickListener rushListener = new OnClickListener()
	{

		@Override
		public void onClick(DialogInterface dialog, int which)
		{
			switch (which)
			{
			case 0:
				speedLevel = 5000;
				break;
			case 1:
				speedLevel = 7000;
				break;
			case 2:
				speedLevel = 9000;
				break;

			default:
				break;
			}
			((OptionItem)OptionsActivity.this.getListView().getChildAt(3)).setText(OptionItem.SUBTEXT, optionTipList[3]+speedLevel/1000);
			dialog.dismiss();
		}
	};

	OnItemClickListener optionListener = new OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id)
		{
			switch (position)
			{
			case 0:
				/*
				 * ����Activity,�����л�ý��
				 */
				Intent intent = new Intent(OptionsActivity.this,
						RenameActivity.class);
				Bundle nameExtra = new Bundle();

				nameExtra.putString("CURRENTNAME",
						OptionsActivity.this.playerName);
				intent.putExtras(nameExtra);
				startActivityForResult(intent, RENAME);
				break;
			case 1:
				OptionsActivity.this.isSoundEffectOn.setOpposite();
				((OptionItem) view)
						.setCheckBoxStatus(isSoundEffectOn.getBool());
				((OptionItem)OptionsActivity.this.getListView().getChildAt(1)).setText(OptionItem.SUBTEXT, optionTipList[1]+isSoundEffectOn.getString());
				break;
			case 2:
				AlertDialog.Builder builder = new AlertDialog.Builder(
						OptionsActivity.this);
				builder.setTitle("AI�趨");
				builder.setSingleChoiceItems(AILevelStrs,
						OptionsActivity.this.AILevel, levelListener);
				AlertDialog dialog = builder.create();
				dialog.show();
				break;
			case 3:
				AlertDialog.Builder rushbuilder = new AlertDialog.Builder(
						OptionsActivity.this);
				rushbuilder.setTitle("����ģʽ�ٶ�ѡ��");
				rushbuilder.setSingleChoiceItems(speedLevelStrs,
						OptionsActivity.this.speedLevel/1000 / 3 - 1, rushListener);
				AlertDialog rushDialog = rushbuilder.create();
				rushDialog.show();
				break;
			case 4:
				OptionsActivity.this.isDebugModeOn.setOpposite();
				((OptionItem) view).setCheckBoxStatus(isDebugModeOn.getBool());
				((OptionItem)OptionsActivity.this.getListView().getChildAt(4)).setText(OptionItem.SUBTEXT, optionTipList[4]+isDebugModeOn.getString());
				break;
			case 5:
				// GenericDialog confirm=new
				// GenericDialog(OptionsActivity.this,R.style.genericdialog);
				// ͨ��GenericDialog��showDialog������ʾ�Ի���,����ȡ�ĸ���ť������
				// int isOk = confirm.showDialog();
				// if(GenericDialog.BTN_OK==isOk)
				// //�ô���Ӷ�ȷ������Ӧ����
				// Toast.makeText(OptionsActivity.this, "�ҿ���������",
				// Toast.LENGTH_SHORT).show();
				// else if(GenericDialog.BTN_CANCEL==isOk)
				// Toast.makeText(OptionsActivity.this, "�ҿ�����û�µ��Ҹ���",
				// Toast.LENGTH_SHORT).show();
				AlertDialog.Builder alterBuilder = new AlertDialog.Builder(
						OptionsActivity.this);
				alterBuilder.setNegativeButton("ȡ��", resetConfirm);
				alterBuilder.setPositiveButton("ȷ��", resetConfirm);
				alterBuilder.setMessage("ȷ��Ҫ����ô������֮�󲻿���أ�\n�����޼ۣ���������");

				AlertDialog resetConfirmDialog = alterBuilder.create();
				resetConfirmDialog.show();

				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode)
		// ����Request��Flag���ж�Activity
		{
		case RENAME: // ����Result��Flag���жϷ��ص�����
			if (resultCode == RenameActivity.RENAMERESULT)
			{
				Bundle result = data.getExtras();
				String rename = result.getString("NAME");
				engine.playerNameString = rename;
				OptionsActivity.this.playerName = rename;
				((OptionItem)this.getListView().getChildAt(0)).setText(OptionItem.SUBTEXT, optionTipList[0]+rename);
			}
			break;

		default:
			break;
		}
	}
}

class OptionBool
{
	boolean item;

	public OptionBool(boolean item)
	{
		this.item = item;
	}

	public OptionBool()
	{
		this.item = false;
	}

	public void setBoolean(boolean item)
	{
		this.item = item;
	}

	public void setOpposite()
	{
		this.item = !item;
	}

	public boolean getBool()
	{
		return item;
	}
	public String getString()
	{
		if(item)
			return "�ѿ���";
		else 
			return "�ѹر�";
	}
}

// Version 0.2