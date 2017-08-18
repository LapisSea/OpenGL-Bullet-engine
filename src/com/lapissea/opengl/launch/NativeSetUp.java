package com.lapissea.opengl.launch;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipException;

import javax.swing.UIManager;

import com.lapissea.opengl.launch.swing.ConsoleWindow;
import com.lapissea.opengl.program.util.OperatingSystem;

class NativeSetUp{
	
	static void haxNatives(){
		String paths=System.getProperty("java.library.path");
		
		String nativeLocation=new File(OperatingSystem.APP_DATA+"/LWJGL/2/"+OperatingSystem.ACTIVE_OS).getPath();
		checkNatives(nativeLocation);
		
		if(paths==null||paths.isEmpty()) paths=nativeLocation;
		else paths+=File.pathSeparator+nativeLocation;
		System.setProperty("java.library.path", paths);
		try{
			Field fieldSysPath=ClassLoader.class.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null, null);
		}catch(Exception e){}
	}
	
	private static ConsoleWindow cons;
	
	private static void out(String s){
		if(cons==null) create();
		cons.out(s+"\n");
		System.out.println(s);
	}
	
	private static void err(String s){
		if(cons==null) create();
		cons.err(s+"\n");
		System.err.println(s);
	}
	
	private static boolean flag=false;
	
	private static void create(){
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e1){}
		
		cons=new ConsoleWindow("Native setting up", "nativ_win", true);
		if(flag) cons.setLocationRelativeTo(null);
		cons.out("====GPU NATIVES SET UP====\n");
	}
	
	private static void close(){
		if(cons!=null){
			out("Done! Closing window in 1 sec...");
			try{
				Thread.sleep(1000);
			}catch(InterruptedException e){}
			cons.setVisible(false);
			cons.dispose();
		}
	}
	
	private static void closeErr(){
		if(cons!=null){
			err("\nClick to close...");
			cons.text.addMouseListener(new MouseAdapter(){
				
				@Override
				public void mouseClicked(MouseEvent e){
					System.exit(-1);
				}
			});
			try{
				Thread.sleep(Integer.MAX_VALUE);
			}catch(InterruptedException e1){}
		}
	}
	
	private static void checkNatives(String nativeLocation){
		File nativesDir=new File(nativeLocation),tempSave=new File(nativeLocation, "NativeSrc.jar");
		nativesDir.mkdirs();
		try{
			if(!tempSave.exists()){
				out("Downloading natives...");
				Thread.sleep(200);
				tempSave.createNewFile();
				
				String glVersion="2.9.3";
				out("Maven data:\n    LWJGL - version: "+glVersion+"\n    Operating system: "+OperatingSystem.ACTIVE_OS);
				URL url=new URL("http://repo1.maven.org/maven2/org/lwjgl/lwjgl/lwjgl-platform/"+glVersion+"/lwjgl-platform-"+glVersion+"-natives-"+OperatingSystem.ACTIVE_OS.toString().toLowerCase()+".jar");
				try(BufferedInputStream in=new BufferedInputStream(url.openStream());FileOutputStream fout=new FileOutputStream(tempSave)){
					final byte data[]=new byte[1024];
					int count;
					while((count=in.read(data, 0, 1024))!=-1){
						fout.write(data, 0, count);
					}
					out("Done!");
				}catch(Exception e){
					err("UNABLE TO DOWNLOAD NATIVES!");
					if(e instanceof UnknownHostException)err("Error:\nProblem connectiong to server: "+e.getMessage()+"\nPlease check your internet connection!");
					else err("Error:\nUnkown errer: "+e.getMessage());
					tempSave.delete();
					e.printStackTrace();
					closeErr();
				}
			}
			
			try(JarFile nativeSrc=new JarFile(tempSave)){
				Enumeration<JarEntry> i=nativeSrc.entries();
				while(i.hasMoreElements()){
					JarEntry entry=i.nextElement();
					String name=entry.getName();
					if(name.startsWith("META-INF/")) continue;
					File file=new File(nativesDir, name);
					if(file.exists()) continue;
					if(entry.isDirectory()){
						file.mkdirs();
						continue;
					}
					out("Extracting "+name);
					try(InputStream is=nativeSrc.getInputStream(entry);FileOutputStream fos=new FileOutputStream(file)){
						while(is.available()>0){
							fos.write(is.read());
						}
					}
				}
			}catch(ZipException e){
				tempSave.delete();
				err("UNABLE TO EXTRACT NATIVES!");
				e.printStackTrace();
				closeErr();
			}catch(Exception e){
				err("UNABLE TO EXTRACT NATIVES!");
				e.printStackTrace();
				closeErr();
			}
		}catch(Exception e){
			err("UNABLE TO SET UP NATIVES!");
			e.printStackTrace();
			closeErr();
		}
		close();
	}
}
