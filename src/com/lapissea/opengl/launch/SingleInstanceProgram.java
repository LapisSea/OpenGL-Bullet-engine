package com.lapissea.opengl.program.util;

import java.awt.Toolkit;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

@SuppressWarnings("resource")
public class SingleInstanceProgram{
	
	static{
		try{
			
			File f=new File(OperatingSystem.APP_DATA+"/OpenGL engine/GL.lock");//name of file here that will serve as a lock file
			f.getParentFile().mkdirs();
			f.deleteOnExit();
			FileChannel channel;
			FileLock lock;
			if(f.exists()) f.delete();
			channel=new RandomAccessFile(f, "rw").getChannel();
			lock=channel.tryLock();
			if(lock==null){
				channel.close();
				System.out.println("Already running");
				Toolkit.getDefaultToolkit().beep();
				System.exit(-2);
			}
			Runtime.getRuntime().addShutdownHook(new Thread(()->{
				try{
					if(lock!=null){
						lock.release();
						channel.close();
						f.delete();
					}
				}catch(Exception e){}
			}));
		}catch(Exception e){
			System.out.println("Already running");
			Toolkit.getDefaultToolkit().beep();
			System.exit(-2);
		}
	}
	
	public static void check(){}
}
