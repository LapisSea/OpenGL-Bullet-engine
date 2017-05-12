package com.lapissea.opengl.program.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.function.IntConsumer;

import javax.swing.UIManager;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.lapissea.opengl.program.util.log.ExternalLogWindow;

public class LogUtil{
	
	@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, property="__class")
	public static class Bean{
		
		public final String field;
		
		@JsonCreator
		public Bean(@JsonProperty("field") String field){
			this.field=field;
		}
		
		@Override
		public String toString(){
			return "Bean{"+
					"field='"+field+'\''+
					'}';
		}
	}
	
	
	public static final class __{
		
		protected static boolean		DEBUG_ACTIVE=true;
		protected static boolean		DEBUG_INIT;
		protected static boolean		EXTERNAL_INIT;
		protected static IntConsumer	EXTERNAL_STREAM_OUT;
		protected static IntConsumer	EXTERNAL_STREAM_ERR;
		protected static Runnable		EXTERNAL_CLEAR;
		public static PrintStream		OUT			=System.out;
		
		
		public static void INJECT_EXTERNAL_PRINT(String configLocation){
			if(EXTERNAL_INIT) return;
			EXTERNAL_INIT=true;
			
			INIT_WINDOW(configLocation);
			
			System.setOut(new PrintStream(new ExternalStream(System.out, EXTERNAL_STREAM_OUT)));
			System.setErr(new PrintStream(new ExternalStream(System.err, EXTERNAL_STREAM_ERR)));
		}
		
		private static void INIT_WINDOW(String configLocation){
			
			try{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}catch(Exception e1){}
			
			ExternalLogWindow log=new ExternalLogWindow(configLocation);
			
			EXTERNAL_STREAM_OUT=log::out;
			EXTERNAL_STREAM_ERR=log::err;
			EXTERNAL_CLEAR=log::clear;
		}
		
		private static final class ExternalStream extends OutputStream{
			
			final OutputStream	child;
			final IntConsumer	external;
			
			public ExternalStream(OutputStream child, IntConsumer external){
				this.child=child;
				this.external=external;
			}
			
			@Override
			public void write(int b) throws IOException{
				child.write(b);
				if(true) external.accept(b);
			}
			
		}
		
		public static void INJECT_DEBUG_PRINT(boolean active){
			if(DEBUG_INIT) return;
			DEBUG_INIT=true;
			DEBUG_ACTIVE=active;
			if(!(DEBUG_ACTIVE=active))return;
			
			System.setOut(new PrintStream(new DebugHeaderStream(System.out)));
			System.setErr(new PrintStream(new DebugHeaderStream(System.err)));
		}
		
		
		private static final class DebugHeaderStream extends OutputStream{
			
			final OutputStream child;
			
			static boolean				LAST_CH_ENDL=true;
			static DebugHeaderStream	LAST;
			
			static final Object LOCK=new Object();
			
			public DebugHeaderStream(OutputStream child){
				this.child=child;
			}
			
			@Override
			public void write(int b) throws IOException{
				synchronized(LOCK){
					
					if(LAST!=this){//prevent half of the line to be from other stream
						if(!LAST_CH_ENDL) LAST.write('\n');//OI! END YO FUCKEN LINES
						LAST=this;
					}
					
					if(LAST_CH_ENDL){
						LAST_CH_ENDL=false;
						debugHeader(child);
					}
					
					if(b=='\n') LAST_CH_ENDL=true;
					
					child.write((char)b);
				}
			}
			
			private static void debugHeader(OutputStream stream){
				StackTraceElement[] trace=Thread.currentThread().getStackTrace();
				int depth=12;
				while(trace[depth].getClassName().equals(LogUtil.class.getName())){
					depth++;
				}
				while(trace[depth].getLineNumber()==-1){
					depth++;
				}
				
				StackTraceElement stack=trace[depth];
				String className=stack.getClassName();
				try{
					stream.write(("["+Thread.currentThread().getName()+"]["+className.substring(className.lastIndexOf('.')+1)+":"+stack.getLineNumber()+"]: ").getBytes());
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Note that this clears only external window.
	 */
	public static void clear(){
		__.EXTERNAL_CLEAR.run();
	}
	
	//================================================
	public static void print(){
		out("");
	}
	
	public static void println(){
		out("\n");
	}
	
	public static void print(Object obj){
		out(UtilM.toString(obj));
	}
	
	public static void println(Object obj){
		out(UtilM.toString(obj)+"\n");
	}
	
	public static void print(Object...objs){
		out(UtilM.toString(objs));
	}
	
	public static void println(Object...objs){
		out(UtilM.toString(objs)+"\n");
	}
	
	//================================================
	
	public static void printlnEr(){
		err("\n");
	}
	
	public static void printEr(){
		err("");
	}
	
	public static void printEr(Object obj){
		err(UtilM.toString(obj));
	}
	
	public static void printlnEr(Object obj){
		err(UtilM.toString(obj)+"\n");
	}
	
	public static void printEr(Object...objs){
		err(UtilM.toString(objs));
	}
	
	public static void printlnEr(Object...objs){
		err(UtilM.toString(objs)+"\n");
	}
	
	//================================================
	
	public static void printFunctionTrace(int count, CharSequence splitter){
		println(getFunctionTrace(count, splitter));
	}
	
	public static String getFunctionTrace(int count, CharSequence splitter){
		StringBuilder line=new StringBuilder();
		
		StackTraceElement[] trace=Thread.currentThread().getStackTrace();
		if(count>=trace.length) count=trace.length-1;
		//		for(StackTraceElement stack:trace){
		//			stack.getMethodName()
		//		}
		for(int i=count+1;i>=2;i--){
			line.append(trace[i].getMethodName()).append('(').append(trace[i].getLineNumber()).append(')');
			if(i!=2) line.append(splitter);
		}
		return line.toString();
	}
	
	/**
	 * print fancy stuff and things
	 */
	public static void printWrapped(Object obj){
		String[] data=UtilM.toString(obj).split("\n");
		StringBuilder line=new StringBuilder();
		
		int length=0;
		for(int i=0;i<data.length;i++){
			String lin=(data[i]=data[i].replaceFirst("\\s+$", ""));
			length=Math.max(length, lin.length());
		}
		
		if(data.length>1){
			length+=4;
			for(int i=0;i<data.length;i++){
				String lin=(data[i]="| "+data[i]+" |");
				int diff=length-lin.length();
				if(diff==0) continue;
				
				for(int j=0;j<diff;j++){
					if(j%2==0) lin+="=";
					else lin="="+lin;
				}
				
				data[i]=lin;
			}
		}
		
		line.append("<<");
		for(int i=0, j=length+4;i<j;i++){
			line.append('=');
		}
		line.append(">>");
		
		String lineS=line.toString();
		
		out(lineS+"\n");
		for(String lin:data)
			out("<<=="+lin+"==>>\n");
		out(lineS+"\n");
	}
	
	public static <T> T printlnAndReturn(T obj){
		println(obj);
		return obj;
	}
	
	public static void printStackTrace(String msg){
		StringBuilder result=new StringBuilder();
		
		StackTraceElement[] a1=Thread.currentThread().getStackTrace();
		
		if(msg==null){
			DateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Calendar cal=Calendar.getInstance();
			result.append("Invoke time: ").append(dateFormat.format(cal.getTime())).append("\n");
		}
		else result.append(msg).append("\n");
		
		int length=0;
		for(int i=2;i<a1.length;i++){
			StackTraceElement a=a1[i];
			String s=a.toString();
			result.append(s).append("\n");
			length=Math.max(s.length(), length);
		}
		for(int b=0;b<length/4;b++)
			result.append("_/\\_");
		
		println(result);
	}
	
	//================================================
	
	private static void out(String s){
		if(__.DEBUG_ACTIVE&&!__.DEBUG_INIT) System.err.println("LOG UTILITY DID NOT INJECT DEBUG HEADER!");
		System.out.print(s);
	}
	
	private static void err(String s){
		if(__.DEBUG_ACTIVE&&!__.DEBUG_INIT) System.err.println("LOG UTILITY DID NOT INJECT DEBUG HEADER!");
		System.err.print(s);
	}
	
}
