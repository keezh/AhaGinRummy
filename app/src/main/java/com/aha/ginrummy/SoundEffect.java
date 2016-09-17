package com.aha.ginrummy;

import java.util.HashMap;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

public class SoundEffect {
	private SoundPool soundPool;
	private HashMap<Integer, Integer> soundMap;
	private Context context;
	private boolean isLoaded;
	private int num;
	
	public SoundEffect(Context context,int maxStreams) 
	{
		soundPool = new SoundPool(maxStreams,AudioManager.STREAM_MUSIC,0);
		soundMap = new HashMap<Integer, Integer>();
		this.context = context;
		isLoaded = false;
		num = 0;
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {	
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				isLoaded = true;
				System.out.println("装载完毕");
			}
		});
	}
	
	public void add(int resourse)
	{
		isLoaded = false;
		soundMap.put(num++, soundPool.load(context, resourse, 1));
	}
	
	public void play(int key)
	{
		if(key>=0 && key<soundMap.size())
		{
			if(isLoaded)
			{
				soundPool.play(soundMap.get(key), 1, 1, 0, 0, 1);
				System.out.println("key:"+key);
			}
			else 
			{
				System.out.println("资源木有装载好，不能播放!");
			}
	
		}
	}
}
