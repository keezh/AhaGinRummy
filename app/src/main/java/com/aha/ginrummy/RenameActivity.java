package com.aha.ginrummy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent.CanceledException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RenameActivity extends Activity
{
	private Button confirm;
	private Button cancel;
	private EditText etName;
	private String name;
	
	public static final int RENAMERESULT = 0X1001;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.rename);
		
		Bundle oldName = this.getIntent().getExtras();
		name = oldName.getString("CURRENTNAME");
		
		confirm = (Button) findViewById(R.id.confirm);
		cancel = (Button) findViewById(R.id.cancel);
		etName = (EditText) findViewById(R.id.etname);
		etName.setText(name);
		confirm.setOnClickListener(clickListener);
		cancel.setOnClickListener(clickListener);
		
		
		
	}

	private OnClickListener clickListener = new OnClickListener()
	{

		@Override
		public void onClick(View v)
		{
			String rename=etName.getText().toString();
			if((confirm.getId()==v.getId())&&(!rename.trim().equals("")))
			{				
				Intent intent = new Intent();
				
				intent.putExtra("NAME", rename);
				setResult(RENAMERESULT, intent);
				finish();
			}
			else if((confirm.getId()==v.getId())&&(rename.trim().equals("")))
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(RenameActivity.this);
				builder.setPositiveButton("确定",new DialogInterface.OnClickListener()
				{
					
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
				builder.setMessage("请输入用户名！");
				builder.create().show();
			}
			else if (cancel.getId()==v.getId()) 
			{				
				finish();
			}

		}
	};
}
