package com.lapissea.opengl.program.gui;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.util.math.vec.Vec2f;
import com.lapissea.opengl.window.api.events.KeyEvent;
import com.lapissea.opengl.window.api.events.KeyEvent.KeyAction;
import com.lapissea.opengl.window.api.util.IVec2i;

public class Gui extends IngameDisplay{
	
	protected static final Vec2f SCREEN_SIZE_F=new Vec2f(){
		
		IVec2i src=Game.win().getSize();
		
		@Override
		public float x(){
			return src.x();
		}
		
		@Override
		public float y(){
			return src.y();
		}
	};
	
	@Override
	public Vec2f getPos(){
		return Vec2f.ZERO;
	}
	
	@Override
	public Vec2f getSize(){
		return SCREEN_SIZE_F;
	}
	
	@Override
	public Vec2f getElementSize(){
		return SCREEN_SIZE_F;
	}
	
	public boolean pausesGame(){
		return false;
	}
	
	@Override
	public void onKey(KeyEvent e){
		super.onKey(e);
		if(e.code==1&&e.action==KeyAction.RELEASE) close();
	}
	
	public void close(){
		Game.get().renderer.guiHandler.closeOpenGui();
	}
	
	@Override
	public void update(){
		super.update();
		updateFlow();
	}
}
