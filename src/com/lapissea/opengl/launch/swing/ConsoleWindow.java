package com.lapissea.opengl.launch.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.lapissea.opengl.program.util.Config;
import com.lapissea.opengl.program.util.math.vec.Vec2i;

public class ConsoleWindow extends JFrame{
	
	private static final long serialVersionUID=3441837513436601144L;
	
	private final Style				outStyle,errStyle;
	private final StyledDocument	doc;
	public final JTextPane			text;
	private boolean					scrollUpdate;
	private final Vec2i				clickPos=new Vec2i();
	
	public ConsoleWindow(String title, String configLocation, boolean undecorated){
		super(title);
		setUndecorated(undecorated);
		
		getContentPane().setLayout(new BorderLayout());
		
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
		if(undecorated){
			scrollE.setBorder(new LineBorder(new Color(0x1883D7)));
			text.setEditable(false);
			text.addMouseListener(new MouseAdapter(){
				
				@Override
				public void mousePressed(MouseEvent e){
					clickPos.set(e.getX(), e.getY());
				}
			});
			text.addMouseMotionListener(new MouseMotionAdapter(){
				
				@Override
				public void mouseDragged(MouseEvent e){
					setLocation(e.getXOnScreen()-clickPos.x(), e.getYOnScreen()-clickPos.y());
				}
			});
		}else scrollE.setBorder(new EmptyBorder(0, 0, 0, 0));
		doc=text.getStyledDocument();
		
		outStyle=text.addStyle("out_style", null);
		StyleConstants.setForeground(outStyle, new Color(0xD0D0D0));
		errStyle=text.addStyle("out_style", outStyle);
		StyleConstants.setForeground(errStyle, Color.green);
		text.setBackground(new Color(0x101020));
		
		Config config=Config.getConfig("ConsoleWin");
		
		setAlwaysOnTop(true);
		setSize(config.get("size", ()->new Dimension(500, 250)));
		setLocation(config.get("pos", ()->new Point(0, 0)));
		addWindowListener(new WindowAdapter(){
			
			private void save(){
				config.set("size", getSize());
				config.set("pos", getLocation());
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
		//		createBufferStrategy(2);
	}
	
	public void out(String b){
		b.chars().forEach(this::out);
	}
	
	public void err(String b){
		b.chars().forEach(this::err);
	}
	
	public void out(int b){
		if(!isVisible()) return;
		if(b=='\n') scrollUpdate=true;
		try{
			doc.insertString(doc.getLength(), String.valueOf((char)b), outStyle);
		}catch(BadLocationException e){}
	}
	
	public void err(int b){
		if(!isVisible()) return;
		if(b=='\n') scrollUpdate=true;
		try{
			doc.insertString(doc.getLength(), String.valueOf((char)b), errStyle);
		}catch(BadLocationException e){}
	}
	
	public void clear(){
		if(!isVisible()) return;
		text.setText("");
	}
}
