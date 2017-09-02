package com.lapissea.opengl.program.util.function;

import java.util.function.Predicate;

@SuppressWarnings({"unchecked","rawtypes"})
public class Predicates{
	
	private static final Predicate			TRUE			=o->true;
	public static final Predicate<String>	FIRST_NUMERIC	=s->Character.isDigit(s.charAt(0));
	
	public static <T> Predicate<T> TRUE(){
		return TRUE;
	}
	
}
