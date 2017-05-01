package com.lapissea.opengl.abstr.opengl.assets;

import com.lapissea.opengl.program.util.color.ColorM;

public interface IMaterial{
	
	ColorM getAmbient();
	
	default IMaterial setAmbient(ColorM ambient){
		return setAmbient(ambient.r(), ambient.g(), ambient.b(), ambient.a());
	}
	IMaterial setAmbient(float r, float g, float b, float a);
	
	ColorM getDiffuse();

	default IMaterial setDiffuse(ColorM diffuse){
		return setAmbient(diffuse.r(), diffuse.g(), diffuse.b(), diffuse.a());
	}
	IMaterial setDiffuse(float r, float g, float b, float a);
	
	ColorM getSpecular();
	
	default IMaterial setSpecular(ColorM specular){
		return setAmbient(specular.r(), specular.g(), specular.b(), specular.a());
	}
	IMaterial setSpecular(float r, float g, float b, float a);
	
	float getJelly();
	
	void setJelly(float jelly);
	
	float getShineDamper();
	
	IMaterial setShineDamper(float shineDamper);
	
	float getReflectivity();
	
	IMaterial setReflectivity(float reflectivity);
	
	float getLightTroughput();
	
	IMaterial setLightTroughput(float lightTroughput);
	
	String getName();

	int getId();
	
}
