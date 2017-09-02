package com.lapissea.opengl.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.lwjgl.opengl.OpenGLException;

import com.lapissea.opengl.game.world.World;
import com.lapissea.opengl.gui.Gui;
import com.lapissea.opengl.gui.SplashScreen;
import com.lapissea.opengl.launch.GameStart;
import com.lapissea.opengl.rendering.GLUtil;
import com.lapissea.opengl.rendering.Renderer;
import com.lapissea.opengl.rendering.shader.Shaders;
import com.lapissea.opengl.rendering.shader.modules.ShaderModule;
import com.lapissea.opengl.util.PairM;
import com.lapissea.opengl.util.Performance;
import com.lapissea.opengl.util.timer.GameTimer;
import com.lapissea.opengl.util.timer.Timer_Ver2;
import com.lapissea.opengl.window.api.IGLWindow;
import com.lapissea.opengl.window.api.ILWJGLCtx;
import com.lapissea.util.LogUtil;
import com.lapissea.util.UtilL;

public class Game{
	
	private static Game INSTANCE;
	
	public static Game get(){
		return INSTANCE;
	}
	
	public static void createGame(ILWJGLCtx glCtx){
		if(INSTANCE!=null) throw new IllegalStateException();
		INSTANCE=new Game(glCtx);
	}
	
	public final Registry	registry=new Registry();
	public GameTimer		timer;
	public Renderer			renderer;
	public World			world;
	public final ILWJGLCtx	glCtx;
	
	private static final ForkJoinPool LOADING_POOL=new ForkJoinPool(Performance.getMaxThread(), ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);
	
	private final List<PairM<Runnable,Exception>> openglLoadQueue=new ArrayList<>();
	
	private boolean first=true;
	
	private Game(ILWJGLCtx glCtx){
		this.glCtx=glCtx;
	}
	
	public void start(){
		win().setEventHooks(registry);
		
		ShaderModule.register();
		
		timer=new Timer_Ver2(20, 60);
		timer.setInfiniteFps(!win().getVSync());
		
		new Thread(()->{
			SplashScreen screen=new SplashScreen();
			timer.setUpdate(screen::update);
			timer.setRender(screen::render);
			
			renderer=new Renderer();
			initContent();
			registry.preInit();
			registry.init();
			registry.postInit();
			
			world=new World();
			Shaders.load();
			screen.end();
			LogUtil.printWrapped("=======GAME_RUN=======");
			timer.setUpdate(this::update);
			timer.setRender(this::render);
		}, "Loading thread").start();
		
		UtilL.runWhileThread("Updating thread", timer::isRunning, timer::runUpdate);
		UtilL.runWhile(timer::isRunning, timer::runRender);
	}
	
	private void update(){
		if(win().isClosed()){
			timer.end();
			return;
		}
		if(world==null) return;
		boolean paused=isPaused();
		timer.setPaused(paused);
		if(!paused) world.update();
		registry.update();
	}
	
	private void render(){
		
		timer.setInfiniteFps(false);
//		win().setVSync(true);
		
		if(win().isClosed()){
			timer.end();
			return;
		}
		loadGLData();
		win().updateInput();
		if(first){
			first=false;
			LogUtil.printWrapped("LOADED IN: "+(System.nanoTime()-GameStart.START_TIME)/1000_000_000D);
		}
		if(win().isVisible()){
			
			try{
				renderer.render();
			}catch(OpenGLException e){
				e.printStackTrace();
			}
			win().swapBuffers();
		}
	}
	
	public void loadGLData(){
		synchronized(this){
			if(openglLoadQueue.isEmpty()) return;
			for(PairM<Runnable,Exception> p:openglLoadQueue){
				try{
					GLUtil.checkError();
					p.obj1.run();
					GLUtil.checkError();
				}catch(Exception e){
					e.initCause(p.obj2);
					throw e;
				}
				
			}
			openglLoadQueue.clear();
		}
	}
	
	private void initContent(){
		
		registry.register(renderer);
		
	}
	
	public static float getPartialTicks(){
		return get().timer.getPartialTick();
	}
	
	public static void glCtx(Runnable runnable){
		if(get().glCtx.isGlThread()) runnable.run();
		else glCtxLater(runnable);
	}
	
	private void _glCtxLater(Runnable runnable){
		synchronized(this){
			openglLoadQueue.add(new PairM<>(runnable, new Exception()));
		}
	}
	
	public static void glCtxLater(Runnable runnable){
		get()._glCtxLater(runnable);
	}
	
	public static IGLWindow win(){
		return get().glCtx.getCtxWindow();
	}
	
	public static boolean isPaused(){
		Gui g=get().renderer.guiHandler.getOpenGui();
		return g!=null&&g.pausesGame();
	}
	
	public static <T> void load(Supplier<T> task, Consumer<T> onLoad){
		
	}
	
	public static <T> void load(Runnable task){
		LOADING_POOL.submit(task);
	}
}
