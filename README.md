# XPrefs

###Usage
----

#### Gradle

```groovy
dependencies {
   compile 'com.huxq17.xprefs:xprefs:0.0.2'
}
```

##### 混淆时注意事项：
* 不要混淆XPrefs中的注解类型，添加混淆配置：
```
      -keep class * extends java.lang.annotation.Annotation { *; }
```
* 对于用于持久化的实体类不要混淆，包含javaBean和接口，在demo中是这样的：
```
      -keep interface com.huxq17.xprefs.example.interfaces.** { *; }
      -keepclasseswithmembers class com.huxq17.xprefs.example.UserBean {
        <fields>;
        <methods>;
      }
```

具体根据自己项目情况而定。
### 关于我
QQ：1491359569
QQ群：537610843 (加群备注：XPrefs)
邮箱：huxq17@163.com

### License

    Copyright (C) 2016 huxq17

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

