package com.lapissea.opengl.rendering;

import org.lwjgl.util.vector.Matrix4f;

import com.lapissea.opengl.window.assets.IModel;

public interface ModelTransformed{
	
	IModel getModel();
	
	Matrix4f getTransform();
	
}
