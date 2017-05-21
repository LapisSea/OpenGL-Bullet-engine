package com.lapissea.opengl.program.util.log;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.lapissea.opengl.program.rendering.swing.ScrollUtil;
import com.lapissea.opengl.program.util.UtilM;
import com.lapissea.opengl.program.util.config.Config;
import com.lapissea.opengl.program.util.config.configs.SwingWindowConfig;

import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.chars.CharList;

public class ExternalLogWindow extends JFrame{
	
	private static final long serialVersionUID=3441837513436601144L;
	
	private final Style				outStyle,errStyle;
	private final StyledDocument	doc;
	private final JTextPane			text;
	private boolean					scrollUpdate;
	private BooleanList				bufferFlags	=new BooleanArrayList();
	private CharList				buffer		=new CharArrayList();
	
	public ExternalLogWindow(String configLocation){
		super("DebugLog");
		SwingWindowConfig config=Config.getConfig(SwingWindowConfig.class, configLocation);
		
		getContentPane().setLayout(new BorderLayout());
		//		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		text=new JTextPane();
		
		text.setFont(new Font("Consolas", Font.PLAIN, 13));
		
		JScrollPane scrollE=new JScrollPane(text){
			
			private static final long serialVersionUID=-756340247250959182L;
			
			@Override
			public void paint(Graphics g){
				if(scrollUpdate){
					ScrollUtil.scroll(text, ScrollUtil.BOTTOM);
					scrollUpdate=false;
				}
				super.paint(g);
			}
		};
		scrollE.setBorder(new EmptyBorder(0, 0, 0, 0));
		doc=text.getStyledDocument();
		
		outStyle=text.addStyle("out_style", null);
		StyleConstants.setForeground(outStyle, new Color(0xD0D0D0));
		errStyle=text.addStyle("out_style", outStyle);
		StyleConstants.setForeground(errStyle, Color.green);
		text.setBackground(new Color(0x101020));
		
		setAlwaysOnTop(true);
		setSize(config.size);
		setLocation(config.position);
		addWindowListener(new WindowAdapter(){
			
			private void save(){
				config.size=getSize();
				config.position=getLocation();
				config.save();
			}
			
			@Override
			public void windowDeactivated(WindowEvent e){
				if(!e.getWindow().isVisible()) return;
				save();
			}
			
			@Override
			public void windowClosed(WindowEvent e){
				save();
			}
		});
		getContentPane().add(scrollE);
		
		setVisible(true);
		createBufferStrategy(2);
		
		new Thread(()->{
			
			StringBuilder builder=new StringBuilder();
			
			while(true){
				if(buffer.isEmpty()){
					UtilM.sleep(1);
					continue;
				}
				try{
					synchronized(buffer){
						int i=0;
						while(bufferFlags.size()>i){
							boolean b;
							b=bufferFlags.get(i);
							builder.append(buffer.getChar(i));
							i++;
							while(bufferFlags.size()>i&&b==bufferFlags.get(i)){
								char c=buffer.getChar(i++);
								if(c=='\n') scrollUpdate=true;
								builder.append(c);
							}
							try{
								doc.insertString(doc.getLength(), builder.toString(), b?outStyle:errStyle);
							}catch(BadLocationException e1){
								e1.printStackTrace();
							}
							builder.setLength(0);
						}
						bufferFlags.clear();
						buffer.clear();
					}
				}catch(Exception e){}
			}
		}, "log-updater").start();
		
	}
	
	public void out(int b){
		if(!isVisible()) return;
		bufferFlags.add(true);
		buffer.add((char)b);
	}
	
	public void err(int b){
		if(!isVisible()) return;
		bufferFlags.add(false);
		buffer.add((char)b);
	}
	
	public void clear(){
		if(!isVisible()) return;
		bufferFlags.clear();
		buffer.clear();
		text.setText("");
	}
}
