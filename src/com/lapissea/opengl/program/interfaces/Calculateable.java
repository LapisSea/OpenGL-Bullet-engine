package com.lapissea.opengl.program.interfaces;


public interface Calculateable<Type extends Calculateable<Type>>{

	Type add(Type c);
	Type sub(Type c);
	Type subRev(Type c);
	Type mul(Type c);
	Type mul(float f);
	Type div(Type c);
	Type abs();
	Type sqrt();
	Type sq();
	Type clone();
	Type set(Type src);
}
