package com.aha.ginrummy;

import java.io.FileOutputStream;
import android.app.Activity;
import android.content.Context;

public class AhaSavior
{
	public static void SaveAll(AhaGameEngine engine, Activity activity)
	{
		try 
		{
			AhaSaxParser parser=new AhaSaxParser();
			String xml=parser.serialize(engine);
			FileOutputStream fos=activity.openFileOutput("customed.xml", Context.MODE_PRIVATE);
			fos.write(xml.getBytes("UTF-8"));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
