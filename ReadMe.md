# StringEncodePlugin
## 使用方法
note:仅适用于安卓项目
1.打开项目根目录的`build.gradle` 在 `buildscript`中添加仓库及依赖
```groovy
buildscript {
    repositories {
        ...
        //add repository
        maven { url 'https://www.jitpack.io' }
    }
    dependencies {
        ...
        //add dependency
        classpath 'com.github.mywonson:StringEncodingPlugin:main-SNAPSHOT'
    }
}
```
2.在项目模块的`build.gradle`使用插件:
```groovy
...
// use plugin
apply plugin:'com.wonson.encodeString'
...
```
3.效果:打包生成的apk中字符串明文将被简单加密