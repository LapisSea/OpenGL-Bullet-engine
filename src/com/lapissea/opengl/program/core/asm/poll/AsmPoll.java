package com.lapissea.opengl.program.core.asm.poll;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class AsmPoll{
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public static @interface AsmPolling{}
	
	/**
	 * NOT MULTI THREAD SAFE
	 */
	public static <T> T get(Class<T> c){
		throw new RuntimeException("ASM FAILED TO REPLACE! (did you add @AsmPolling to class?)");
	}
	
	//LDC L__CLASS_NAME__.class
	//INVOKESTATIC com/lapissea/opengl/program/core/asm/AsmPoll.get (Ljava/lang/Class;)Ljava/lang/Object;
	//CHECKCAST __CLASS_NAME__
}
