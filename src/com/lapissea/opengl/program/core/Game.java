package com.lapissea.opengl.program.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.opengl.OpenGLException;

import com.lapissea.opengl.abstr.opengl.IGLWindow;
import com.lapissea.opengl.abstr.opengl.ILWJGLCtx;
import com.lapissea.opengl.program.game.world.World;
import com.lapissea.opengl.program.rendering.GLUtil;
import com.lapissea.opengl.program.rendering.gl.Renderer;
import com.lapissea.opengl.program.rendering.gl.guis.SplashScreen;
import com.lapissea.opengl.program.rendering.gl.shader.Shaders;
import com.lapissea.opengl.program.rendering.gl.shader.modules.ShaderModule;
import com.lapissea.opengl.program.util.LogUtil;
import com.lapissea.opengl.program.util.PairM;
import com.lapissea.opengl.program.util.UtilM;

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
	public Timer			timer;
	public Renderer			renderer;
	public World			world;
	public final ILWJGLCtx	glCtx;
	
	private final List<PairM<Runnable,Exception>> openglLoadQueue=Collections.synchronizedList(new ArrayList<>());
	
	private Game(ILWJGLCtx glCtx){
		this.glCtx=glCtx;
	}
	
	public void start(){
		glCtx.init();
		win().setEventHooks(registry);
		
		ShaderModule.register();
		SplashScreen screen=new SplashScreen();
		timer=new Timer(20, 4000, screen::update, screen::render);
		
		UtilM.startDaemonThread(timer, "Timer thread");
		new Thread(()->{
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
		if(win().isClosed()){
			timer.end();
			return;
		}
		win().updateInput();
		if(world==null) return;
		world.update();
		registry.update();
	}
	
	private void render(){
		if(win().isClosed()){
			timer.end();
			return;
		}
		loadGLData();
		try{
			renderer.render();
		}catch(OpenGLException e){
			e.printStackTrace();
		}
		win().swapBuffers(timer.getTargetedFps());
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
	
	public static void glCtx(Runnable runnable){
		if(get().glCtx.isGlThread()) runnable.run();
		else get().openglLoadQueue.add(new PairM<>(runnable, new Exception()));
	}
	
	public static void glCtxLatter(Runnable runnable){
		get().openglLoadQueue.add(new PairM<>(runnable, new Exception()));
	}
	
	public static IGLWindow win(){
		return get().glCtx.getCtxWindow();
	}
}
