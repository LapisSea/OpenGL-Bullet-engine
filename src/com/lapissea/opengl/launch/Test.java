package com.lapissea.opengl.launch;

import com.lapissea.opengl.program.core.asm.Asmfied;
import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.util.LogUtil;

@Asmfied
public class Test{

	private static final Vec3f lel=new Vec3f();
	private static final Vec3f lel1=new Vec3f();
	private static final Vec3f lel2=new Vec3f();
	private static final Vec3f lel3=new Vec3f();
	
	public void lel(){
		Vec3f vec=lel1;
		vec.set(0,0,0);
		LogUtil.println(vec==lel1);
	}
	
}
