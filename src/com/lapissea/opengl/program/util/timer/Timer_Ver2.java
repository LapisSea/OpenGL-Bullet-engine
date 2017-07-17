package com.lapissea.opengl.program.util.timer;

import com.lapissea.util.LogUtil;

public class Timer_Ver2 extends GameTimer{
	
	private int			framesPerSecond;
	private int			updatesPerSecond;
	/***/
	private double		nanosecondsPerFrame;
	private double		nanosecondsPerUpdate;
	private double		nanosecondsPerUpdateWSpeed;
	/***/
	private long		lastUpdate	=time();
	private long		lastRender	=time();
	/***/
	private float		speed		=1;
	private float		partialTicks=0;
	private boolean		infiniteFps	=false;
	/***/
	private Runnable	update		=null;
	private Runnable	render		=null;
	
	public Timer_Ver2(int ups, int fps, Runnable update, Runnable render){
		super(ups, fps, update, render);
	}
	
	public Timer_Ver2(int ups, int fps){
		super(ups, fps);
	}
	
	@Override
	public synchronized int getFps(){
		return framesPerSecond;
	}
	
	@Override
	public synchronized GameTimer setFps(int fps){
		this.framesPerSecond=fps;
		nanosecondsPerFrame=SECOND/(double)fps;
		return this;
	}
	
	private void calcNspu(){
		nanosecondsPerUpdateWSpeed=nanosecondsPerUpdate*speed;
	}
	
	@Override
	public synchronized int getUps(){
		return updatesPerSecond;
	}
	
	@Override
	public synchronized GameTimer setUps(int ups){
		this.updatesPerSecond=ups;
		nanosecondsPerUpdate=SECOND/(double)ups;
		calcNspu();
		return null;
	}
	
	@Override
	public synchronized float getSpeed(){
		return speed;
	}
	
	@Override
	public synchronized GameTimer setSpeed(float speed){
		this.speed=Math.max(speed, 0);
		calcNspu();
		return this;
	}
	
	@Override
	public synchronized GameTimer setInfiniteFps(boolean flag){
		infiniteFps=flag;
		return this;
	}
	
	@Override
	public synchronized boolean getInfiniteFps(){
		return infiniteFps;
	}
	
	@Override
	public float getPartialTick(){
		return partialTicks;
	}
	
	@Override
	public synchronized GameTimer setRender(Runnable hook){
		render=hook;
		return this;
	}
	
	@Override
	public synchronized GameTimer runRender(){
		if(!isRunning()||render==null) return this;
		if(infiniteFps) render.run();
		else{
			long t=time();
			if(t>=lastRender+nanosecondsPerFrame){
				partialTicks=(float)((t-lastUpdate)/nanosecondsPerUpdateWSpeed);
				lastRender=t;
				render.run();
			}
		}
		
		return this;
	}
	
	@Override
	public synchronized GameTimer setUpdate(Runnable hook){
		update=hook;
		return this;
	}
	
	@Override
	public synchronized GameTimer runUpdate(){
		if(!isRunning()||update==null) return this;
		long t=time();
		int count=(int)Math.floor((t-lastUpdate)/(nanosecondsPerUpdateWSpeed=nanosecondsPerUpdate/speed));
		if(count>30){
			LogUtil.println("Game is severely lagging or paused or system time changed! Skipping "+(count-1F)/updatesPerSecond+" seconds!");
			count=1;
		}
		if(count>0){
			lastUpdate=t;
			while((count--)>0){
				update.run();
			}
		}
		
		return this;
	}
	
}
