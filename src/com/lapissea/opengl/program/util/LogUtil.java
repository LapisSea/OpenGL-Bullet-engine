package com.lapissea.opengl.program.util;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LogUtil{

	public static void println(){
		normal("");
	}
	
	public static void println(Object obj){
		normal(UtilM.toString(obj));
	}
	
	public static void println(Object...objs){
		normal(UtilM.toString(objs));
	}
	
	//================================================
	
	public static void printlnEr(){
		error("");
	}
	
	public static void printlnEr(Object obj){
		error(UtilM.toString(obj));
	}
	
	public static void printlnEr(Object...objs){
		error(UtilM.toString(objs));
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
				if(diff==0)continue;
				
				for(int j=0;j<diff;j++){
					if(j%2==0)lin+="=";
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
		
		normal(lineS);
		for(String lin:data)
			normal("<<=="+lin+"==>>");
		normal(lineS);
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
	
	private static void error(String data){
		for(String line:data.split("\n")){
			StackTraceElement stack=Thread.currentThread().getStackTrace()[3];
			String className=stack.getClassName();
			_print(System.err,"["+Thread.currentThread().getName()+"]["+className.substring(className.lastIndexOf('.')+1)+":"+stack.getLineNumber()+"]: "+line);
		}
	}
	private static void normal(String data){
		for(String line:data.split("\n")){
			StackTraceElement stack=Thread.currentThread().getStackTrace()[3];
			String className=stack.getClassName();
			_print(System.out,"["+Thread.currentThread().getName()+"]["+className.substring(className.lastIndexOf('.')+1)+":"+stack.getLineNumber()+"]: "+line);
		}
	}
	private static void _print(PrintStream stream, String data){
		stream.println(data);
	}
	
}
