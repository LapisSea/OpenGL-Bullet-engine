package com.lapissea.opengl.program.util;

import java.util.function.Predicate;

public class Predicates{
	
	@SuppressWarnings("rawtypes")
	private static final Predicate			TRUE			=o->true;
	public static final Predicate<String>	FIRST_NUMERIC	=s->Character.isDigit(s.charAt(0));
	
	@SuppressWarnings("unchecked")
	public static <T> Predicate<T> TRUE(){
		return TRUE;
	}
}
