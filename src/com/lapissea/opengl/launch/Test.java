package com.lapissea.opengl.launch;

import com.lapissea.opengl.program.core.asm.poll.AsmPoll;
import com.lapissea.opengl.program.core.asm.poll.AsmPoll.AsmPolling;
import com.lapissea.opengl.program.util.LogUtil;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

@AsmPolling
public class Test{
	
	public void lel(){
		Vec3f vec=AsmPoll.get(Vec3f.class);
		
		LogUtil.println(vec.hashCode());
	}
	
}
