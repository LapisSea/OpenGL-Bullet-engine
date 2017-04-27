package com.lapissea.opengl.program.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.OpenGLException;

import com.lapissea.opengl.program.game.world.World;
import com.lapissea.opengl.program.rendering.GLUtil;
import com.lapissea.opengl.program.rendering.gl.Renderer;
import com.lapissea.opengl.program.rendering.gl.Window;
import com.lapissea.opengl.program.rendering.gl.guis.SplashScreen;
import com.lapissea.opengl.program.rendering.gl.shader.Shaders;
import com.lapissea.opengl.program.rendering.gl.shader.modules.ShaderModule;
import com.lapissea.opengl.program.util.LogUtil;
import com.lapissea.opengl.program.util.PairM;
import com.lapissea.opengl.program.util.UtilM;
import com.lapissea.opengl.program.util.WindowInput;

public class Game{
	
	private static final Game INSTANCE=new Game();
	
	public static Game get(){
		return INSTANCE;
	}
	
	public final Registry	registry=new Registry();
	public Timer			timer;
	public Renderer			renderer;
	public World			world;
	private static Thread	GL_THREAD;
	
	
	private final List<PairM<Runnable,Exception>> openglLoadQueue=Collections.synchronizedList(new ArrayList<>());
	
	private static final class AsyncLoadWorker extends Thread{
		
		LinkedList<Runnable> queue=new LinkedList<>();
		
		public AsyncLoadWorker(){
			super("Loading");
			setDaemon(false);
		}
		
		@Override
		public void run(){
			while(true){
				try{
					synchronized(this){
						wait();
					}
				}catch(InterruptedException e){
					throw new RuntimeException(e);
				}
			}
		}
		
	}
	
	private Game(){}
	
	public void start(){
		GL_THREAD=Thread.currentThread();
		ShaderModule.register();
		SplashScreen screen=new SplashScreen();
		timer=new Timer(20, 4000, screen::update, screen::render);
		
		UtilM.startDaemonThread(timer, "Timer thread");
		new Thread(()->{
			Window.EVENT_HOOK=registry;
			renderer=new Renderer();
			initContent();
			registry.preInit();
			registry.init();
			registry.postInit();
			
			world=new World();
			Shaders.load();
			screen.end();
			LogUtil.printWrapped("=======GAME_RUN=======");
			timer.setUpdateHook(this::update);
			timer.setRenderHook(this::render);
		}, "Loading thread").start();
		
		while(timer.isRunning()){
			timer.runUpdateRender();
		}
		
	}
	
	private void update(){
		if(Window.isClosed()){
			timer.end();
			return;
		}
		WindowInput.update();
		if(world==null) return;
		world.update();
		registry.update();
	}
	
	private void render(){
		if(Window.isClosed()){
			timer.end();
			return;
		}
		loadGLData();
		try{
			renderer.render();
		}catch(OpenGLException e){
			e.printStackTrace();
		}
		Window.swapBuffers(timer.getTargetedFps());
	}
	
	public synchronized void loadGLData(){
		if(openglLoadQueue.isEmpty()) return;
		UtilM.doAndClear(openglLoadQueue, p->{
			try{
				GLUtil.checkError();
				p.obj1.run();
				GLUtil.checkError();
			}catch(Exception e){
				e.initCause(p.obj2);
				throw e;
			}
		});
	}
	
	private void initContent(){
		
		registry.register(renderer);
		
	}
	
	public static float getPartialTicks(){
		return get().timer.getPartialTick();
	}
	
	public static boolean isThisOpenGLThread(){
		return Thread.currentThread()==GL_THREAD;
	}
	
	public static void loadThread(Runnable runnable){
		
	}
	public static void glCtx(Runnable runnable){
		if(isThisOpenGLThread()) runnable.run();
		else get().openglLoadQueue.add(new PairM<>(runnable, new Exception()));
	}
	public static void glCtxLatter(Runnable runnable){
		get().openglLoadQueue.add(new PairM<>(runnable, new Exception()));
	}
}
