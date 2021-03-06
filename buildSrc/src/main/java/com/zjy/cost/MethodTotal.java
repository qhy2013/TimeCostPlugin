package com.zjy.cost;


import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * Created by zjy on 2019-05-03
 */
public class MethodTotal extends ClassVisitor {
    public MethodTotal(int i, ClassVisitor classVisitor) {
        super(i, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int i, String s, String s1, String s2, String[] strings) {
        MethodVisitor methodVisitor = cv.visitMethod(i, s, s1, s2, strings);
        methodVisitor = new AdviceAdapter(Opcodes.ASM5, methodVisitor, i, s, s1) {
            boolean inject;

            @Override
            public AnnotationVisitor visitAnnotation(String s, boolean b) {
                //自定义的注解用来判断方法上的注解与TimeTotal是否为同一个注解，是否需要统计耗时
                if (Type.getDescriptor(TimeTotal.class).equals(s)) {
                    inject = true;
                }
                return super.visitAnnotation(s, b);
            }

            @Override
            protected void onMethodEnter() {
                //方法进入时期
                if (inject) {
                    //这里就是之前使用ASM插件生成的统计时间代码
                    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                    mv.visitLdcInsn("this is asm input");
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

                    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                    mv.visitLdcInsn("new add log");
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

                    mv.visitTypeInsn(NEW, "java/lang/Throwable");
                    mv.visitInsn(DUP);
                    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Throwable", "<init>", "()V", false);
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Throwable", "getStackTrace", "()[Ljava/lang/StackTraceElement;", false);
                    mv.visitInsn(ICONST_1);
                    mv.visitInsn(AALOAD);
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false);
                    mv.visitVarInsn(ASTORE, 1);

                    mv.visitVarInsn(ALOAD, 1);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
                    mv.visitMethodInsn(INVOKESTATIC, "com/example/asmdemo/TimeManager", "addStartTime", "(Ljava/lang/String;J)V", false);
                }
            }

            @Override
            protected void onMethodExit(int i) {
                //方法结束时期
                if (inject) {
                    //计算方法耗时
                    mv.visitVarInsn(ALOAD, 1);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
                    mv.visitMethodInsn(INVOKESTATIC, "com/example/asmdemo/TimeManager", "addEndTime", "(Ljava/lang/String;J)V", false);

                    mv.visitVarInsn(ALOAD, 1);
                    mv.visitMethodInsn(INVOKESTATIC, "com/example/asmdemo/TimeManager", "calcuteTime", "(Ljava/lang/String;)V", false);
                }
            }
        };
        return methodVisitor;
    }
}
