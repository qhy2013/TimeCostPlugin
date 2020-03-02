package com.example.asmdemo;


/**
 * Created by zjy on 2019-05-05
 * 只是为了方便使用asm生成添加字节码的代码
 *
 * https://www.jianshu.com/p/83a360c9a3d7
 *
 * ASM Bytecode Outline
 *
 * System.nanoTime()
 * https://www.cnblogs.com/jpfss/p/9674054.html
 *
 * TODO: 可能由于kotlin的版本和ASM Bytecode Outline不兼容，不能查看Bytecode
 */
public class Test {

    public void handle() {
        String methodName = new Throwable().getStackTrace()[1].getMethodName();
        System.out.println(methodName);
        System.out.println("new add log");
        TimeManager.addStartTime(methodName, System.nanoTime());
        TimeManager.addEndTime(methodName, System.nanoTime());
        TimeManager.calcuteTime(methodName);
    }
}
