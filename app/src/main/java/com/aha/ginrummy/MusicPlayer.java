package com.aha.ginrummy;

import java.util.HashMap;
import android.content.Context;
import android.media.MediaPlayer;

public class MusicPlayer
{
	private float currentVolume;
	private int index;
	private boolean is_playing;
	private boolean is_pausing;
	private boolean is_stopped;
	private MediaPlayer mediaPlayer;
	private HashMap<Integer, Integer> musicmap;
	private Context context;
	private AhaGameEngine engine;

	public MusicPlayer(Context context, AhaGameEngine _engine)
	{
		is_playing = false;
		is_pausing = false;
		is_stopped = true;
		currentVolume = 1.0f;
		index = 0;
		musicmap = new HashMap<Integer, Integer>();
		engine = _engine;
		this.context = context;
	}

	public boolean isPausing()
	{
		return is_pausing;
	}

	public boolean isPlaying()
	{
		return is_playing;
	}

	public boolean isStopped()
	{
		return is_stopped;
	}

	public float getVolume()
	{
		return currentVolume;
	}

	public void setVolume(float volume)
	{
		if (volume < 0.0f || volume > 1.0f)
			return;
		currentVolume = volume;
		if (is_playing)
		{
			mediaPlayer.setVolume(currentVolume, currentVolume);
		}
	}

	public void add(int resource)
	{
		musicmap.put(index++, resource);
		System.out.println("key:" + (index - 1) + " resource:" + resource);
	}

	public void start(int index)
	{
		if (engine.isSoundEffectOn)
		{
			if (is_stopped || is_pausing)
			{
				mediaPlayer = MediaPlayer.create(context, musicmap.get(index)
						.intValue());
				mediaPlayer.setVolume(currentVolume, currentVolume);
				mediaPlayer.setLooping(true);
				try
				{
					mediaPlayer.prepare();
				} catch (Exception e)
				{
				}
				mediaPlayer.start();
				is_playing = true;
				is_pausing = false;
				is_stopped = false;
			}
		}
	}

	public void restart()
	{
		if (is_pausing)
		{
			try
			{
				mediaPlayer.prepare();
			} catch (Exception e)
			{
			}
			mediaPlayer.start();
			is_playing = true;
			is_pausing = false;
			is_stopped = false;
		}
	}

	public void stop()
	{
		if (is_playing || is_pausing)
		{
			mediaPlayer.stop();
			is_playing = false;
			is_pausing = false;
			is_stopped = true;
			mediaPlayer.release();
		}
	}

	public void pause()
	{
		if (is_playing)
		{
			mediaPlayer.pause();
			is_playing = false;
			is_stopped = false;
			is_pausing = true;
		}
	}
}
