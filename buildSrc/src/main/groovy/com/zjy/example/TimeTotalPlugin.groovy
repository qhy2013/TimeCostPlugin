package com.zjy.example

import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.zjy.cost.MethodTotal
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

public class TimeTotalPlugin extends Transform implements Plugin<Project> {
    @Override
    void apply(Project project) {
        //注册transform
        project.extensions.getByType(AppExtension).registerTransform(this)
    }

    @Override
    String getName() {
        //task名字
        //在控制台打印的transform名字（只是把这个名字拼接上去而已，例如：transformClassesWith+name+ForDebug）
        //Task :app:transformClassesWithAsm_demoForDebug
        return "asm_demo"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        //限定输入文件的类型（例如：class,jar,dex等）
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        //限定文件所在的区域（例如：所有project，只有主工程等）
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        //是否增量更新
        return false
    }

    /**
     * 这方法才是真正的插件实现
     *
     * @param transformInvocation
     * @throws TransformException* @throws InterruptedException* @throws IOException
     */
    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        transformInvocation.inputs.each {
            it.directoryInputs.each {
                if (it.file.isDirectory()) {
                    it.file.eachFileRecurse {
                        def fileName = it.name
                        if (fileName.endsWith(".class") && !fileName.startsWith("R\$")
                                && fileName != "BuildConfig.class" && fileName != "R.class") {
                            //各种过滤类，关联classVisitor
                            handleFile(it)
                        }
                    }
                }
                def dest = transformInvocation.outputProvider.getContentLocation(it.name, it.contentTypes, it.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(it.file, dest)
            }
            it.jarInputs.each { jarInput ->
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                def dest = transformInvocation.outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                FileUtils.copyFile(jarInput.file, dest)
            }
        }
    }

    private void handleFile(File file) {
        def cr = new ClassReader(file.bytes)
        def cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
        def classVisitor = new MethodTotal(Opcodes.ASM5, cw)
        cr.accept(classVisitor, ClassReader.EXPAND_FRAMES)
        def bytes = cw.toByteArray()
        //写回原来这个类所在的路径
        FileOutputStream fos = new FileOutputStream(file.getParentFile().getAbsolutePath() + File.separator + file.name)
        fos.write(bytes)
        fos.close()
    }
}