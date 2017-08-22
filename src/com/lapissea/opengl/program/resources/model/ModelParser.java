package com.lapissea.opengl.program.resources.model;

import java.io.InputStream;

public abstract class ModelParser{
	
	private static interface SimpleExtensionCheck{
		
		boolean match(String extension);
	}
	
	private static final SimpleExtensionCheck DEFAULT=extension->{
		throw new IllegalStateException("Extension checking not initialised!");
	};
	
	private SimpleExtensionCheck defaultCheck=DEFAULT;
	
	public ModelParser(){}
	
	public ModelParser(String...extensions){
		setSimpleExtensionCheck(extensions);
	}
	
	protected void setSimpleExtensionCheck(String...extensions){
		if(extensions.length==0) throw new IllegalStateException("No extensions defined!");
		
		if(extensions.length==1){
			String extensionMatch=extensions[0];
			defaultCheck=extension->extensionMatch.equalsIgnoreCase(extension);
		}else{
			defaultCheck=extension->{
				for(String extensionMatch:extensions){
					if(extensionMatch.equalsIgnoreCase(extension)) return true;
				}
				return false;
			};
		}
		
	}
	
	public abstract ModelBuilder load(String location, InputStream modelStream);
	
	/**
	 * returns if this parser is able to read data of a model with an extension
	 * 
	 * @param extension
	 *            = string of the model file extension (Without a dot)
	 * @return true if parser supports extension
	 */
	public boolean extensionSupported(String extension){
		return defaultCheck.match(extension);
	}
}
