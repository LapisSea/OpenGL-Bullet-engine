package com.lapissea.opengl.core.asm.poll;

import static org.objectweb.asm.Opcodes.*;

import java.util.List;
import java.util.ListIterator;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.lapissea.opengl.core.asm.ClassTransformer;
import com.lapissea.opengl.core.asm.LapisClassLoader;

public class TransformerAsmPoll implements ClassTransformer{
	
	protected static final String ASM_POLL_NAME=AsmPoll.class.getName().replace('.', '/');
	
	public static void register(LapisClassLoader loader){
		loader.registerTransformer("com.lapissea.opengl.launch", new TransformerAsmPoll());
	}
	
	private TransformerAsmPoll(){}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean transform(String name, ClassNode node){
		boolean dirty=false;
		List<MethodNode> methods=node.methods;
		MethodNode staticInit=null;
		for(MethodNode funct:methods){
			if(funct.name.equals("<clinit>")){
				staticInit=funct;
				break;
			}
		}
		if(staticInit==null){
			MethodVisitor mv=node.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
			mv.visitCode();
			Label l0=new Label();
			mv.visitLabel(l0);
			mv.visitInsn(RETURN);
			mv.visitMaxs(2, 0);
			mv.visitEnd();
			for(MethodNode funct:methods){
				if(funct.name.equals("<clinit>")){
					staticInit=funct;
					break;
				}
			}
		}
		for(MethodNode funct:methods){
//			LogUtil.printlnEr(funct.name);
			ListIterator<AbstractInsnNode> iter=funct.instructions.iterator();
			int i=0;
			while(iter.hasNext()){
				AbstractInsnNode insn=iter.next();
//				LogUtil.println(insn);
				if(insn.getType()==AbstractInsnNode.METHOD_INSN){
					MethodInsnNode method=(MethodInsnNode)insn;
					if(method.getType()==H_INVOKEVIRTUAL&&method.owner.equals(ASM_POLL_NAME)){
						AbstractInsnNode type=method.getPrevious();
						if(type!=null&&type.getType()==AbstractInsnNode.LDC_INSN){
							String classType=((Type)((LdcInsnNode)type).cst).getInternalName();
							String classTypeDesc=((Type)((LdcInsnNode)type).cst).toString();
							FieldNode field=new FieldNode(ACC_PRIVATE|ACC_STATIC|ACC_FINAL, "__ASM_POOL_VALUE_"+(i++), classTypeDesc, null, null);
							node.fields.add(field);
							
							staticInit.visitCode();
							staticInit.visitTypeInsn(NEW, classType);
							staticInit.visitInsn(DUP);
							staticInit.visitMethodInsn(INVOKESPECIAL, classType, "<init>", "()V", false);
							staticInit.visitFieldInsn(PUTSTATIC, node.name, field.name, classTypeDesc);
							staticInit.visitEnd();
							
							funct.instructions.insertBefore(type, new FieldInsnNode(Opcodes.ICONST_0, node.name, field.name, classTypeDesc));
							
							//mv.visitFieldInsn(GETSTATIC, "com/lapissea/opengl/launch/Test", "lel", "Lcom/lapissea/opengl/program/util/math/vec/Vec3f;");
//							funct.instructions.remove(method);
							funct.instructions.remove(method.getNext());
							funct.instructions.remove(method);
							funct.instructions.remove(type);
							
							dirty=true;
							
							//							node.fields.add(e)
						}
						
					}
				}
			}
			
		}
		
		return dirty;
	}
	
}
