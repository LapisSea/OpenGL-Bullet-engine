package com.lapissea.opengl.launch;

import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.resources.texture.TextureLoader;
import com.lapissea.opengl.program.util.Config;
import com.lapissea.opengl.program.util.OperatingSystem;
import com.lapissea.opengl.program.util.UtilM;
import com.lapissea.opengl.program.util.math.vec.Vec2i;
import com.lapissea.opengl.window.api.IGLWindow;
import com.lapissea.opengl.window.api.ILWJGLCtx;
import com.lapissea.opengl.window.impl.LWJGL2Ctx;
import com.lapissea.splashscreen.SplashScreenHost;
import com.lapissea.util.LogUtil;

public class Launch{
	
	public static void start(String[] args) throws Exception{
		SplashScreenHost.open("Splash.jar", "LWJGL 2 game", OperatingSystem.APP_DATA+"/OpenGL engine/LTD.bin");
		Thread.sleep(1);
		SplashScreenHost.sendMsg("Starting...");
		NativeSetUp.haxNatives();
		SplashScreenHost.sendMsg("Injected natves!");
		try{
			LogUtil.__.INJECT_FILE_LOG(OperatingSystem.APP_DATA+"/OpenGL engine/log.txt");
			//LogUtil.__.INJECT_EXTERNAL_PRINT("dev_log");
			LogUtil.__.INJECT_DEBUG_PRINT(true);
			
			SplashScreenHost.sendMsg("Injected logger!");
			
//			LogUtil.println(badImg.getColorModel().getm);
			
//			TransformerAsmPoll.register();
//			try{
//				new Test().lel();
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//			{
//				File f=new File("img.png");
//
//				BufferedImage img=ImageIO.read(f);
//
//				LogUtil.println(img.getRGB(0, 0, 1, 1, null, 0, 1));
//
//				for(int x=0;x<256;x++){
//					for(int y=0;y<256;y++){
//						img.setRGB(x, y, Integer.MAX_VALUE);
//					}
//				}
//				ImageIO.write(img, "png", f);
//			}
			
//			Config cfg=Config.getConfig("test");
//			for(int i=0;i<200;i++)cfg.set("vector_"+i, new Vec3f());
//			cfg.save();
//			System.exit(0);
			
			LogUtil.printWrapped("=======GAME_INIT=======");
			LogUtil.println("OS named \""+OperatingSystem.OS_NAME+"\" has been identified as", OperatingSystem.ACTIVE_OS);
			
			Thread.setDefaultUncaughtExceptionHandler((t, e)->{
				e.printStackTrace();
				System.exit(1);
			});
			
			Thread.currentThread().setName("Render");
			
			Config mainWinCfg=Config.getConfig("MainWin");
			ILWJGLCtx glCtx=new LWJGL2Ctx();
			Game.createGame(glCtx);
			
			IGLWindow window=glCtx.getCtxWindow();
			
			SplashScreenHost.sendMsg("Initalised game base!");
			try{
				SplashScreenHost.sendMsg("Configuring window...");
				
				
				//window.setTitle("Genine and Lee");
				window
				.setPos(-10000, -10000)
				.setSize(mainWinCfg.get("size",()->new Vec2i(600,400)))
				.setTitle("The abandoned")
				.setFullScreen(false)
				.setResizable(true)
				.setVSync(true);
				
				ByteBuffer[] ico=new ByteBuffer[2];
				try(InputStream img=UtilM.getResource("textures/icon/16.png")){
					ico[0]=TextureLoader.imgToBuff(ImageIO.read(img));
				}
				try(InputStream img=UtilM.getResource("textures/icon/32.png")){
					ico[1]=TextureLoader.imgToBuff(ImageIO.read(img));
				}
				window.setIcon(ico);
				
				SplashScreenHost.sendMsg("Creating window...");
				glCtx.init();
				SplashScreenHost.sendMsg("Window created!");
				
			}catch(Exception e){
				e.printStackTrace();
				System.exit(1);
			}
			Game.get().start();
			mainWinCfg.set("pos", window.getPos());
			mainWinCfg.save();
			glCtx.destroy();
			
		}catch(Exception e){
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
}
