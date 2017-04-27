package com.lapissea.opengl.program.util;

import java.util.Objects;
import java.util.function.Consumer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.lapissea.opengl.program.events.KeyEvent;
import com.lapissea.opengl.program.events.MouseKeyEvent;
import com.lapissea.opengl.program.events.MouseMoveEvent;
import com.lapissea.opengl.program.events.MouseScrollEvent;

import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;

public class WindowInput{
	
	private static final BooleanList PREV_KB_KEYS=new BooleanArrayList(),PREV_MS_KEYS=new BooleanArrayList();
	
	private static int PREV_MOUSE_X=-1,PREV_MOUSE_Y=-1,PREV_SIZE_W,PREV_SIZE_H;
	
	private static boolean PREV_FOCUS;
	
	private static Runnable						RESIZE		=()->{};
	private static Runnable						FOCUS		=()->{};
	private static Consumer<KeyEvent>			KEY_KB		=e->{};
	private static Consumer<MouseKeyEvent>		KEY_MS		=e->{};
	private static Consumer<MouseMoveEvent>		MOVE_MS		=e->{};
	private static Consumer<MouseScrollEvent>	SCROLL_MS	=e->{};
	
	public static void update(){
		int x=Mouse.getX();
		int y=Mouse.getY();
		
		if(PREV_MOUSE_X!=x||PREV_MOUSE_Y!=y){
			MOVE_MS.accept(new MouseMoveEvent(Mouse.getEventDX(), Mouse.getEventDY(),0));
			PREV_MOUSE_X=x;
			PREV_MOUSE_Y=y;
		}
		
		while(Mouse.next()){
			int key=Mouse.getEventButton();
			if(key!=-1){
				boolean state=Mouse.getEventButtonState();
				set(PREV_MS_KEYS, key, state);
				KEY_MS.accept(new MouseKeyEvent(key, state));
			}
		}
		while(Keyboard.next()){
			int key=Keyboard.getEventKey();
			if(key!=-1){
				boolean state=Keyboard.getEventKeyState();
				set(PREV_KB_KEYS, key, state);
				KEY_KB.accept(new KeyEvent(key, state));
			}
		}
		x=Display.getWidth();
		y=Display.getHeight();
		
		if(PREV_SIZE_W!=x||PREV_SIZE_H!=y){
			RESIZE.run();
			PREV_SIZE_W=x;
			PREV_SIZE_H=y;
		}
		boolean focus=Display.isActive();
		if(PREV_FOCUS!=focus){
			FOCUS.run();
			PREV_FOCUS=focus;
		}
		if(Mouse.hasWheel()){
			int moved=Mouse.getDWheel();
			if(moved!=0){
				SCROLL_MS.accept(new MouseScrollEvent());
			}
		}
	}
	
	private static void set(BooleanList list, int id, boolean value){
		while(list.size()<=id)
			list.add(false);
		list.set(id, value);
	}
	
	public static void windowSizeCallback(Runnable callback){
		RESIZE=Objects.requireNonNull(callback);
	}
	
	public static void windowFocusCallback(Runnable callback){
		FOCUS=Objects.requireNonNull(callback);
	}
	
	public static void keyboardKeyCallback(Consumer<KeyEvent> callback){
		KEY_KB=Objects.requireNonNull(callback);
	}
	
	public static void mouseKeyCallback(Consumer<MouseKeyEvent> callback){
		KEY_MS=Objects.requireNonNull(callback);
	}
	
	public static void mouseMoveCallback(Consumer<MouseMoveEvent> callback){
		MOVE_MS=Objects.requireNonNull(callback);
	}
	
	public static void mouseScrollCallback(Consumer<MouseScrollEvent> callback){
		SCROLL_MS=Objects.requireNonNull(callback);
	}
}
