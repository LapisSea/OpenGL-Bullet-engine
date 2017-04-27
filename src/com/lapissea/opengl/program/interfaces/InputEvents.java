package com.lapissea.opengl.program.interfaces;

import com.lapissea.opengl.program.events.KeyEvent;
import com.lapissea.opengl.program.events.MouseKeyEvent;

public interface InputEvents{
	
	default void onKey(KeyEvent event){}
	
	default void onClick(MouseKeyEvent event){}
}
