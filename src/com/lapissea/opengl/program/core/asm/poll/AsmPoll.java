package com.lapissea.opengl.program.core.asm.poll;

public class AsmPoll{
	
	/**
	 * <b>NOT MULTI THREAD SAFE</b> <br>
	 * Example:<br>
	 * <code>FooBar fobr=AsmPoll.get(FooBar.class);</code>
	 */
	public static <T> T get(Class<T> c){
		throw new RuntimeException("ASM FAILED TO REPLACE! (did you add @Asmfied to class and used a compile know type?)");
	}
	
}
