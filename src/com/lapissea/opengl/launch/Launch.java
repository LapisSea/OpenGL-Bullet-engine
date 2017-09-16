package com.lapissea.opengl.launch;

import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import com.lapissea.opengl.core.Game;
import com.lapissea.opengl.resources.texture.TextureLoader;
import com.lapissea.opengl.util.Config;
import com.lapissea.opengl.util.OperatingSystem;
import com.lapissea.opengl.util.UtilM;
import com.lapissea.opengl.util.math.vec.Vec2i;
import com.lapissea.opengl.window.api.IGLWindow;
import com.lapissea.opengl.window.api.ILWJGLCtx;
import com.lapissea.opengl.window.api.events.ResizeEvent.IResizeEventListener;
import com.lapissea.opengl.window.impl.LWJGL2Ctx;
import com.lapissea.util.LogUtil;

public class Launch{
	
	public static void start(String[] args) throws Exception{
		NativeSetUp.haxNatives();
		try{
			LogUtil.__.INJECT_FILE_LOG(OperatingSystem.APP_DATA+"/OpenGL engine/log.txt");
			//LogUtil.__.INJECT_EXTERNAL_PRINT("dev_log");
			LogUtil.__.INJECT_DEBUG_PRINT(true);
			
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
			
//			OffsetArrayList<String> l=new OffsetArrayList<>();
//			l.set(-2, "-2");
//			l.set(-1, "-1");
//			l.set(0, "0");
//			l.set(1, "1");
			
//			int[] lol=new int[7];
//			for(int i=0;i<lol.length;i++){
//				lol[i]=i;
//			}
//			LogUtil.println(lol);
//			int start=2,siz=3,swap=2;
//
//
//			int[] hold=new int[siz];
//			System.arraycopy(lol, start, hold, 0, siz);
//
//			for(int i=0;i<siz;i++){
//				lol[(i+swap)%siz+start]=hold[i];
//			}
//			LogUtil.println(lol);
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
			try{
				//window.setTitle("Genine and Lee");
				window
				.setPos(-10000, -10000)
				.setSize(mainWinCfg.get("size", ()->new Vec2i(600, 400)))
				.setTitle("The abandoned")
				.setFullScreen(false)
				.setResizable(true)
				.setVSync(true)
				.setPos(mainWinCfg.get("pos", new Vec2i(-1, -1)));
				mainWinCfg.set("pos", window.getPos());
				
				ByteBuffer[] ico=new ByteBuffer[2];
				try(InputStream img=UtilM.getResource("textures/icon/16.png")){
					ico[0]=TextureLoader.imgToBuff(ImageIO.read(img));
				}
				try(InputStream img=UtilM.getResource("textures/icon/32.png")){
					ico[1]=TextureLoader.imgToBuff(ImageIO.read(img));
				}
				window.setIcon(ico);
				
				glCtx.init();
				
			}catch(Exception e){
				e.printStackTrace();
				System.exit(1);
			}
			Game.get().registry.register((IResizeEventListener)e->mainWinCfg.fill("size", Vec2i::new).set(window.getSize()));
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
