package com.lapissea.opengl.launch;

import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.resources.texture.TextureLoader;
import com.lapissea.opengl.program.util.OperatingSystem;
import com.lapissea.opengl.program.util.UtilM;
import com.lapissea.opengl.program.util.config.Config;
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
//			System.exit(0);
			
			LogUtil.printWrapped("=======GAME_INIT=======");
			LogUtil.println("OS named \""+OperatingSystem.OS_NAME+"\" has been identified as", OperatingSystem.ACTIVE_OS);
			
			Thread.setDefaultUncaughtExceptionHandler((t, e)->{
				e.printStackTrace();
				System.exit(1);
			});
			
			Thread.currentThread().setName("Render");
			
			ILWJGLCtx glCtx=new LWJGL2Ctx();
			Game.createGame(glCtx);
			
			IGLWindow window=glCtx.getCtxWindow();
			SplashScreenHost.sendMsg("Initalised game base!");
			try{
				SplashScreenHost.sendMsg("Configuring window...");
				
				//window.setTitle("Genine and Lee");
				window
				.setSize(Config.getInt("win_startup:size.x", 1000), Config.getInt("win_startup:size.y", 600))
				.setPos(-10000, -10000)
				.setTitle("The abandoned")
				.setFullScreen(false)
				.setResizable(true)
				.setVSync(true);
				
				//				window.setPos(winCfg.position);
				//				window.setSize(winCfg.size);
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
			if(window.getPos().x()!=-10000){
				Config.set("win_startup:size.x", window.getSize().x());
				Config.set("win_startup:size.y", window.getSize().y());
				Config.set("win_startup:pos.x", window.getPos().x());
				Config.set("win_startup:pos.y", window.getPos().y());
			}
			glCtx.destroy();
			Config.save();
			
		}catch(Exception e){
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
}
