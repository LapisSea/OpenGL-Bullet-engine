package com.lapissea.opengl.program.util.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lapissea.opengl.program.util.OperatingSystem;

public class Config{
	
	private static final String CONFIG_ROOT=OperatingSystem.APP_DATA+"/OpenGL engine/config";
	
	private static final List<Config> CONFIG_CASH=new ArrayList<>();
	
	private static final ObjectMapper MAPPER=new ObjectMapper();
	static{
		MAPPER.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		MAPPER.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
	}
	
	public static <T extends Config> T getConfig(Class<T> type, String name){
		return getConfig(type, name, t->{});
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Config> T getConfig(Class<T> type, String name, Consumer<T> onCreate){
		Objects.requireNonNull(name);
		return (T)CONFIG_CASH.stream().filter(cf->cf.name.equals(name)).findFirst().orElseGet(()->{
			byte[] src=null;
			try{
				src=Files.readAllBytes(path(name));
			}catch(IOException e3){}
			
			
			T config;
			if(src==null){
				try{
					config=type.getConstructor(String.class).newInstance(name);
					onCreate.accept(config);
				}catch(Exception e1){
					throw new RuntimeException(e1);
				}
			}
			else{
				try{
					config=MAPPER.readValue(src, type);
				}catch(Exception e){
					e.printStackTrace();
					try{
						config=type.getConstructor(String.class).newInstance(name);
					}catch(Exception e1){
						throw new RuntimeException(e1);
					}
				}
			}
			
			CONFIG_CASH.add(config);
			
			return config;
		});
	}
	
	
	public final String name;
	
	protected Config(String name){
		this.name=name;
	}
	
	public void save(){
		new File(CONFIG_ROOT).mkdir();
		try{
			Files.write(path(name), MAPPER.writerWithDefaultPrettyPrinter().writeValueAsBytes(this));
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private static File file(String name){
		return new File(CONFIG_ROOT+"/"+name+".cf");
	}
	
	private static Path path(String name){
		return file(name).toPath();
	}
	
}
