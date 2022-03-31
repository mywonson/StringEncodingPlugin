package com.juexingzhe.mobile

import com.wonson.Operator
import org.gradle.api.Plugin
import org.gradle.api.Project
class EncodeStringPlugin implements Plugin<Project>{
    //Configure project
    @Override
    void apply(Project project){
        // 创建了名为writeToFile的Task
        // 并依赖于WriteTask
        project.android.applicationVariants.all{ va->
            va.javaCompile.doLast{
                String path = va.javaCompile.destinationDir
                File file = project.file(path)
                Operator.run(file)
            }
        }
//        project.tasks.create("encodeStringInClassFile",WriteTask){
//            classDir = project.classDir
//            doLast {
//                println "doLast..."
//            }
//        }
    }
}

//class WriteTask extends DefaultTask{
//    String classDir
//    File getDestination(){
//        project.file(classDir)
//    }
//    @TaskAction
//    def greet(){
//        println("TaskAction:greet")
//        println("classDir:" + classDir)
//        def file = getDestination()
//    }
//}