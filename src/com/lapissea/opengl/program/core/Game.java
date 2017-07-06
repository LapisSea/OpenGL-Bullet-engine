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
import com.lapissea.opengl.program.util.config.Config;
import com.lapissea.opengl.program.util.timer.GameTimer;
import com.lapissea.opengl.program.util.timer.Timer_Ver2;
import com.lapissea.opengl.window.api.IGLWindow;
import com.lapissea.opengl.window.api.ILWJGLCtx;
import com.lapissea.splashscreen.SplashScreenHost;
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
			SplashScreenHost.sendPercent(0.9F);
			Shaders.load();
			screen.end();
			SplashScreenHost.sendPercent(0.9999F);
			LogUtil.printWrapped("=======GAME_RUN=======");
			timer.setUpdate(this::update);
			timer.setRender(this::render);
		}, "Loading thread").start();
		
		while(timer.isRunning()){
			timer.runUpdate();
			timer.runRender();
			UtilL.sleep(1);
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
		//		Display.sync(60);
		timer.setInfiniteFps(false);
		if(win().isClosed()){
			timer.end();
			return;
		}
		loadGLData();
		win().updateInput();
		if(first){
			first=false;
			LogUtil.printWrapped("LOADED IN: "+(System.nanoTime()-GameStart.START_TIME)/1000_000_000D);
			win().setPos(Config.getInt("win_startup:pos.x", -1), Config.getInt("win_startup:pos.y", -1));
			win().setResizable(true);
			new Thread(()->{
				UtilL.sleep(100);
				SplashScreenHost.close();
			}).start();
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
	
	public synchronized void loadGLData(){
		if(openglLoadQueue.isEmpty()) return;
		UtilL.doAndClear(openglLoadQueue, p->{
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
