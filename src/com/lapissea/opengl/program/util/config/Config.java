package com.lapissea.opengl.program.util.config;

import static com.lapissea.util.UtilL.*;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import com.lapissea.opengl.program.util.Objholder;
import com.lapissea.opengl.program.util.OperatingSystem;
import com.lapissea.opengl.program.util.math.vec.Vec2f;
import com.lapissea.opengl.program.util.math.vec.Vec2i;
import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.api.util.IVec2i;
import com.lapissea.opengl.window.api.util.IVec3f;
import com.lapissea.util.UtilL;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

@SuppressWarnings("unchecked")
public class Config{
	
	public static abstract class CSVTypeParser<T>{
		
		public final String		typeName;
		public final Class<T>	type;
		
		public CSVTypeParser(String typeName, Class<T> type){
			this.typeName=Objects.requireNonNull(typeName);
			this.type=Objects.requireNonNull(type);
		}
		
		public abstract void read(Consumer<T> add, int size, IntFunction<String> get);
		
		public void write(Consumer<String> add, T data){
			add.accept(data.toString());
		}
		
	}
	
	private static final Map<String,Config> CACHE=new HashMap<>();
	
	private static final List<CSVTypeParser<?>> PARSERS=new ArrayList<>();
	
	public static void registerParser(CSVTypeParser<?> newParser){
		if(PARSERS.stream().anyMatch(p->p.typeName.equals(newParser.typeName))) throw new IllegalArgumentException("Type with name "+newParser.typeName+" already exists!");
		if(PARSERS.stream().anyMatch(p->p.type.equals(newParser.type))) throw new IllegalArgumentException("type "+newParser.type+" already exists!");
		PARSERS.add(newParser);
	}
	
	static{
		
		
		
		registerParser(new CSVTypeParser<Boolean>("b", Boolean.class){
			
			@Override
			public void read(Consumer<Boolean> add, int size, IntFunction<String> get){
				if(size==0) return;
				String s=get.apply(0);
				if(s.equalsIgnoreCase("true")) add.accept(true);
				else if(s.equalsIgnoreCase("false")) add.accept(false);
			}
		});
		
		registerParser(new CSVTypeParser<Integer>("i", Integer.class){
			
			@Override
			public void read(Consumer<Integer> add, int size, IntFunction<String> get){
				if(size>0) add.accept((int)Float.parseFloat(get.apply(0)));
			}
		});
		
		registerParser(new CSVTypeParser<Float>("f", Float.class){
			
			@Override
			public void read(Consumer<Float> add, int size, IntFunction<String> get){
				if(size>0) add.accept(Float.parseFloat(get.apply(0)));
			}
		});
		
		registerParser(new CSVTypeParser<CharSequence>("s", CharSequence.class){
			
			@Override
			public void read(Consumer<CharSequence> add, int size, IntFunction<String> get){
				if(size>0) add.accept(get.apply(0));
			}
		});
		
		registerParser(new CSVTypeParser<IVec2i>("i2", IVec2i.class){
			
			@Override
			public void read(Consumer<IVec2i> add, int size, IntFunction<String> get){
				if(size>=2) add.accept(new Vec2i((int)Float.parseFloat(get.apply(0)), (int)Float.parseFloat(get.apply(1))));
			}
			
			@Override
			public void write(Consumer<String> add, IVec2i data){
				add.accept(Integer.toString(data.x()));
				add.accept(Integer.toString(data.y()));
			}
		});
		registerParser(new CSVTypeParser<Vec2f>("f2", Vec2f.class){
			
			@Override
			public void read(Consumer<Vec2f> add, int size, IntFunction<String> get){
				if(size>=2) add.accept(new Vec2f(Float.parseFloat(get.apply(0)), Float.parseFloat(get.apply(1))));
			}
			
			@Override
			public void write(Consumer<String> add, Vec2f data){
				add.accept(Float.toString(data.x()));
				add.accept(Float.toString(data.y()));
			}
		});
		registerParser(new CSVTypeParser<IVec3f>("f3", IVec3f.class){
			
			@Override
			public void read(Consumer<IVec3f> add, int size, IntFunction<String> get){
				if(size>=3) add.accept(new Vec3f(Float.parseFloat(get.apply(0)), Float.parseFloat(get.apply(1)), Float.parseFloat(get.apply(2))));
			}
			
			@Override
			public void write(Consumer<String> add, IVec3f data){
				add.accept(Float.toString(data.x()));
				add.accept(Float.toString(data.y()));
				add.accept(Float.toString(data.z()));
			}
		});
		
		registerParser(new CSVTypeParser<Dimension>("dim", Dimension.class){
			
			@Override
			public void read(Consumer<Dimension> add, int size, IntFunction<String> get){
				if(size>=3) add.accept(new Dimension(Integer.parseInt(get.apply(0)), Integer.parseInt(get.apply(1))));
			}
			
			@Override
			public void write(Consumer<String> add, Dimension data){
				add.accept(Integer.toString(data.width));
				add.accept(Integer.toString(data.height));
			}
		});
		registerParser(new CSVTypeParser<Point>("pnt", Point.class){
			
			@Override
			public void read(Consumer<Point> add, int size, IntFunction<String> get){
				if(size>=3) add.accept(new Point(Integer.parseInt(get.apply(0)), Integer.parseInt(get.apply(1))));
			}
			
			@Override
			public void write(Consumer<String> add, Point data){
				add.accept(Integer.toString(data.x));
				add.accept(Integer.toString(data.y));
			}
		});
	}
	
	//================================================================
	
	public static Config getConfig(String name){
		Config conf=CACHE.get(name);
		if(conf==null){
			CACHE.put(name, conf=new Config(name));
		}
		return conf;
	}
	
	private static final Float		F	=new Float(-1);
	private static final Integer	I	=new Integer(-1);
	private static final Boolean	B	=new Boolean(false);
	
	private static final String BASE=OperatingSystem.APP_DATA+"/OpenGL engine/config/";
	
	public final File file;
	
	private Map<String,Object> data;
	
	private Config(String path){
		file=new File(BASE, new File(path+".csv").getPath());
	}
	
	public boolean getBoolean(String key, boolean def){
		Boolean n=(Boolean)data().get(key);
		if(n==null) return def;
		return n;
	}
	
	public boolean getBoolean(String key){
		return (Boolean)data().getOrDefault(key, B);
	}
	
	public int getInt(String key, int def){
		Number n=(Number)data().get(key);
		if(n==null) return def;
		return n.intValue();
	}
	
	public int getInt(String key){
		return ((Number)data().getOrDefault(key, I)).intValue();
	}
	
	public float getFloat(String key, float def){
		Number n=(Number)data().get(key);
		if(n==null) return def;
		return n.floatValue();
	}
	
	public float getFloat(String key){
		return ((Number)data().getOrDefault(key, F)).floatValue();
	}
	
	public <T> T get(String key, Supplier<T> def){
		T t=(T)data().get(key);
		if(t==null) return def.get();
		return t;
	}
	
	public <T> T get(String key, T def){
		return (T)data().getOrDefault(key, def);
	}
	
	public <T> T get(T key){
		return (T)data().get(key);
	}
	/**
	 * If value exists it will be returned but if it does not exist the result of obj argument set to key and will be returned.<br>
	 * Example: <code><br>
	 * class Foo{<br>
	 * 	Foo(){<br>
	 * 		System.out.println("foo created!")<br>
	 * 	}<br>
	 * }<br>
	 * emptyConfig.fill("foo1", Foo::new); \\ foo created<br>
	 * emptyConfig.fill("foo1", Foo::new);<br>
	 * </code>
	 */
	public <T> T fill(String key, Supplier<T> obj){
		T t=(T)data().get(key);
		if(t==null)data.put(key, t=obj.get());
		return t;
	}
	
	public void set(String key, Object obj){
		data().put(key, obj);
	}
	
	@SuppressWarnings("rawtypes")
	public void save(){
		if(data==null){
			file.getParentFile().mkdirs();
		}
		try(CSVWriter csv=new CSVWriter(new FileWriter(file))){
			List<String> build=new ArrayList<>();
			Objholder<String[]> line=new Objholder(new String[0]);
			
			data.entrySet().stream()
			.filter(e->e.getValue()!=null)
			.forEach(e->{
				Object val=e.getValue();
				Class cls=val.getClass();
				CSVTypeParser parser=PARSERS.stream()
						.filter(p->p.type.equals(cls))
						.findAny()
						.orElseGet(()->PARSERS.stream()
								.filter(p->instanceOf(cls, p.type))
								.findAny().orElse(null));
				
				if(parser==null) return;
				build.clear();
				
				build.add(e.getKey());
				build.add(parser.typeName);
				parser.write((Consumer<String>)build::add, val);
				
				csv.writeNext(line.obj=UtilL.array(build, line.obj), false);
			});
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private Map<String,Object> data(){
		if(data==null) read();
		return data;
	}
	
	private void read(){
		data=new HashMap<>();
		try(CSVReader csv=new CSVReader(new FileReader(file))){
			csv.readAll().stream()
			.filter(line->line.length>1)
			.forEach(arr->{
				PARSERS.stream()
				.filter(p->p.typeName.equals(arr[1]))
				.findAny().ifPresent(parser->{
					parser.read(o->data.put(arr[0], o), arr.length-2, i->arr[Math.max(0, i)+2]);
				});
			});
		}catch(FileNotFoundException e){}catch(IOException e){
			e.printStackTrace();
		}
	}
	
}
