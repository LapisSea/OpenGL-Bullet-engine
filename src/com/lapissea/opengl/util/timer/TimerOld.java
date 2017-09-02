package com.lapissea.opengl.util.timer;

import java.util.Objects;

import com.lapissea.opengl.util.UtilM;

public class TimerOld extends GameTimer{
	
	private long		lastTimeUpdated;
	private long		lastTimeRendered;
	/** update period */
	private long		up;
	/** frame period */
	private long		fp;
	private long		lastSec;
	private int			updateQueue;
	private int			updatesCount;
	private int			framesCount;
	private int			ups;
	private int			fps;
	private int			ctxUpdates;
	private int			ctxFrames;
	private boolean		render;
	private boolean		running		=true;
	private boolean		infiniteFps	=true;
	private float		partialUpdate;
	private Runnable	updateHook;
	private Runnable	renderHook;
	
	public TimerOld(int ups, int fps, Runnable update, Runnable render){
		super(ups, fps, update, render);
	}
	
	private void update(){
		
		long time=time();
		if(time-lastTimeUpdated>=up){
			lastTimeUpdated=time;
			updateQueue++;
		}
		time=time();
		if(!infiniteFps||time-lastTimeRendered>=fp){
			render=true;
		}
		time=time();
		if(time-lastSec>=SECOND){
			lastSec=time;
			ctxUpdates=updatesCount;
			ctxFrames=framesCount;
			//LogUtil.println("fps", getFps(), "ups", getUps());
			updatesCount=0;
			framesCount=0;
		}
		//UtilM.sleep(0, 500000);
	}
	
	private void render(){
		partialUpdate=(float)(((double)(time())-lastTimeUpdated)/SECOND*ups);
		renderHook.run();
		framesCount++;
	}
	
	public GameTimer run(){
		running=true;
		while(running){
			update();
			throttleLooping();
		}
		return this;
	}
	
	/**
	 * Save CPU from checking if anything should update many times in a
	 * millisecond
	 */
	private void throttleLooping(){
		if(infiniteFps&&fps<ctxFrames*1.2&&ups<ctxUpdates*1.2) UtilM.sleep(0, 500000);
	}
	
	@Override
	public void end(){
		running=false;
	}
	
	@Override
	public GameTimer setUps(int ups){
		this.ups=ups;
		up=SECOND/ups;
		return this;
	}
	
	@Override
	public GameTimer setFps(int fps){
		this.fps=fps;
		fp=SECOND/fps;
		return this;
	}
	
	@Override
	public int getFps(){
		return ctxFrames;
	}
	
	@Override
	public int getUps(){
		return ctxUpdates;
	}
	
	@Override
	public float getPartialTick(){
		return partialUpdate;
	}
	
	public int getTargetedFps(){
		return infiniteFps?fps:(int)Double.POSITIVE_INFINITY;
	}
	
	public int getTargetedUps(){
		return ups;
	}
	
	public void setFpsRestriction(boolean restrict){
		infiniteFps=restrict;
	}
	
	@Override
	public boolean isRunning(){
		return running;
	}
	
	@Override
	public GameTimer setUpdate(Runnable updateHook){
		this.updateHook=Objects.requireNonNull(updateHook);
		return this;
	}
	
	@Override
	public GameTimer setRender(Runnable renderHook){
		this.renderHook=Objects.requireNonNull(renderHook);
		return this;
	}
	
	@Override
	public float getSpeed(){
		return 1;
	}
	
	@Override
	public GameTimer setSpeed(float fps){
		return this;
	}
	
	@Override
	public GameTimer runUpdate(){
		while(updateQueue>0){
			updateHook.run();
			updateQueue--;
			updatesCount++;
		}
		return this;
	}
	
	@Override
	public GameTimer runRender(){
		if(infiniteFps) render();
		else if(render){
			render=false;
			render();
			lastTimeRendered=time();
			throttleLooping();
		}
		return this;
		
	}
	
	@Override
	public GameTimer setInfiniteFps(boolean flag){
		infiniteFps=flag;
		return this;
	}
	
	@Override
	public boolean getInfiniteFps(){
		return infiniteFps;
	}
	
}
