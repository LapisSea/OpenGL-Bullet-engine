package com.lapissea.opengl.program.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.opengl.OpenGLException;

import com.lapissea.opengl.launch.GameStart;
import com.lapissea.opengl.program.game.world.World;
import com.lapissea.opengl.program.rendering.GLUtil;
import com.lapissea.opengl.program.rendering.gl.Renderer;
import com.lapissea.opengl.program.rendering.gl.gui.Gui;
import com.lapissea.opengl.program.rendering.gl.gui.SplashScreen;
import com.lapissea.opengl.program.rendering.gl.shader.Shaders;
import com.lapissea.opengl.program.rendering.gl.shader.modules.ShaderModule;
import com.lapissea.opengl.program.util.PairM;
import com.lapissea.opengl.program.util.UtilM;
import com.lapissea.opengl.program.util.config.Config;
import com.lapissea.opengl.program.util.config.configs.WindowConfig;
import com.lapissea.opengl.window.api.IGLWindow;
import com.lapissea.opengl.window.api.ILWJGLCtx;
import com.lapissea.splashscreen.SplashScreenHost;
import com.lapissea.util.LogUtil;

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
	
	private boolean first=true;
	
	private Game(ILWJGLCtx glCtx){
		this.glCtx=glCtx;
	}
	
	public void start(){
		win().setEventHooks(registry);
		
		SplashScreenHost.sendMsg("Loading assets...!");
		SplashScreenHost.sendPercent(0.8F);
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
			SplashScreenHost.sendPercent(0.9F);
			Shaders.load();
			screen.end();
			SplashScreenHost.sendPercent(0.9999F);
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
		if(!isPaused()) world.update();
		registry.update();
	}
	
	private void render(){
		if(win().isClosed()){
			timer.end();
			return;
		}
		loadGLData();
		if(first){
			first=false;
			LogUtil.printWrapped("LOADED IN: "+(System.nanoTime()-GameStart.START_TIME)/1000_000_000D);
			WindowConfig winCfg=Config.getConfig(WindowConfig.class, "win_startup");
			win().setSize(winCfg.size);
			win().setPos(winCfg.position);
			new Thread(()->{
				UtilM.sleep(100);
				SplashScreenHost.close();
			}).start();
		}
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
	
	public static boolean isPaused(){
		Gui g=get().renderer.guiHandler.getOpenGui();
		return g!=null&&g.pausesGame();
	}
}
