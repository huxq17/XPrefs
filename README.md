# XPrefs

###依赖
----

#### Gradle

```groovy
dependencies {
   compile 'com.huxq17.xprefs:xprefs:1.1.0'
}
```
### **用法**

####*初始化*

```
  XPrefs.bind(this);
```
初始化是绑定了contenxt，之后的操作就不需要传入context参数了，如果你传入activity，会自动转成application context，这样就避免了内存泄漏的问题，当然你也可以在application的类里进行绑定操作。

####*整存整取*

```
 /**
     * 整存整取，把javabean类中的所有有效的字段都写入sharedpreferences中
     */
    private void saveAll() {
        //如果想切换写入的sharedpreferences文件，可以调用
//        XPrefs.changeFileName("your custom sp file's name");
        //如果设置Mode，如果调用了changeFileName方法，则必须在changeFileName之后调用
//        XPrefs.changeFileMode(Context.MODE_PRIVATE);
        //或者直接调用
//        XPrefs.changeFileNameAndMode("your custom sp file's name", Context.MODE_PRIVATE);

        XPrefs.changeFileName(spFile1);
        UserBean userBean = new UserBean();
        userBean.setAge(21);
        userBean.setFuns(1000);
        userBean.setMoney(100);
        userBean.setName("韩梅梅");
        userBean.setVIP(true);
        XPrefs.saveAll(userBean);
        //读取，查看存入的数据
        userBean = XPrefs.get(UserBean.class);
        LogUtils.i("saveAll " + userBean);
    }
```
上面代码中的changeFileName和changeFileMode方法分别是设置Sharedpreferences文件的name和mode的，要注意的是，如果调用了changeFileName，那么需要调用changeFileMode的话就必须要在changeFileName之后调用。如果不设置name，默认操作的文件名是XPrefs，默认的mode是Context.MODE_PRIVATE。

接着new了一个UserBean的实例，给需要保存的属性设置了对应的值，然后调用saveAll就把UserBean中所有的属性都保存进了SharedPreferences文件里，其中key是属性名，value是属性的值。让我们接着看看UserBean这个类，
```
    /**
     * final 修饰的字段不会被存储
     */
    private final String InvalidField="InvalidField";
    /**
     * 添加了XIgnore注解的字段会被忽视，也不会被存储
     */
    @XIgnore
    private String IgnoreField="IgnoreField";
    //目前支持以下几种类型的数据存储
    private String name;
    private float money;
    private int age;
    private boolean isVIP;
    private long funs;

   ...省略一些getter、setter方法和toString（）...
```
用final修饰的属性和加上@XIgnore注解的属性都是被忽略的，不会被存储的。存储的时候，key是属性名（如"name","money","age"...），value是属性的值。最后通过下面这行代码读取所有存入的数据，直接调用userbean的getter方法，就可以使用这些数据。
 `userBean = XPrefs.get(UserBean.class);`
 整存整取的好处除了方便以外，就是效率高，无论存储了多少属性，都只操作了一次文件，有点类似于数据库中的事物。
    
既然可以保存和读取整个javaBean，那么也应该可以对javaBean中的单个属性进行存储和读取。

####*单个字段的存储和读取*

```
 /**
     * 单个字段的存储和读取
     */
    public void save() {
        //修改存储的sharedpreferences文件，mode默认为Context.MODE_PRIVATE
        XPrefs.changeFileName(spFile2);
        UserBean userBean = new UserBean();
        userBean.setAge(42);
        userBean.setFuns(2000);
        userBean.setMoney(200);
        userBean.setName("李雷");
        userBean.setVIP(false);
        XPrefs.save(userBean, "name");
        XPrefs.save(userBean, "age");
        XPrefs.save(userBean, "funs");
        XPrefs.save(userBean, "money");
        XPrefs.save(userBean, "isVIP");
        //读取，查看存入的数据
        Class cls = UserBean.class;
        boolean isVIP = XPrefs.getBoolean(cls, "isVIP");
        String name = XPrefs.getString(cls, "name");
        int age = XPrefs.getInt(cls, "age");
        long funs = XPrefs.getLong(cls, "funs");
        float money = XPrefs.getFloat(cls, "money");
        LogUtils.i("save name=" + name + ";money=" + money + ";age=" + age + ";funs=" + funs + ";isVIP=" + isVIP);
        //整取
        userBean = XPrefs.get(UserBean.class);
        LogUtils.i("save " + userBean.toString());
    }
```
上面的代码演示了对UserBean中的字段单独进行读写操作。
```
XPrefs.save(userBean, "name");
String name = XPrefs.getString(cls, "name");
```
要注意的save和getString方法传入的第二个参数得和属性名相同，才能达到想要的效果。也就是如果定义了一个属性private String name;那么传入的就得是“name”。看上去有点麻烦，确实挺麻烦的，但这只是一种用法，更方便的用法后面会介绍。

####*使用接口和注解*

```
    private void saveAllAndFollowYourHeart() {
        IUser user = XPrefs.getObject(IUser.class);
        user.setName("Tom");
        user.setAge(18);
        user.setFuns(4000);
        user.setMoney(40000);
        user.setVip(true);
        LogUtils.i("IUser name=" + user.getName() + ";money=" + user.getMoney() + ";age=" + user.getAge()+ ";funs=" + user.getFuns() + ";isVIP=" + user.getVip());
    }
```
首先，调用了XPrefs.getObject(IUser.class)拿到了接口IUser的一个实例对象user，接着分别调用了user的set和get方法，看上去没什么，但是，在执行set方法的时候就已经把数据 存了起来，执行get方法就是把数据读取出来。
具体让我们看看IUser接口，

```
@XSPFile(fileName = "IUser", fileMode = Context.MODE_PRIVATE)
public interface IUser {
    @XSet(key = "name", fileName = "IUser", fileMode = Context.MODE_PRIVATE)
    void setName(String name);

    @XGet(key = "name")
    String getName();

    @XSet(key = "age")
    void setAge(int age);

    @XGet(key = "age")
    int getAge();

    @XSet(key = "funs")
    void setFuns(int funs);

    @XGet(key = "funs")
    int getFuns();

    @XSet(key = "vip")
    void setVip(boolean vip);

    @XGet(key = "vip")
    boolean getVip();

    @XSet(key = "money")
    void setMoney(float money);

    @XGet(key = "money")
    float getMoney();
}
```
有三个注解XSPFile,XSet和XGet，这三个注解都不是必需的。其中XSPFile是用来指定file name和mode的，用来标记接口，作用域是类。

```
@XSPFile(fileName = "IUser", fileMode = Context.MODE_PRIVATE)
```
XSet和XGet则是来标记方法的作用的，一个方法用XSet标记了，那么这个方法的作用就是写入数据到指定的SharedPreferences文件中的，其中key就是写入时的key。XSet也能指定写入的文件 name和mode，如果XSPFile和XSet同时指定了文件的name和mode，那么以XSet指定的为准。
```
@XSet(key = "name", fileName = "IUser", fileMode = Context.MODE_PRIVATE)
void setName(String name);
```
如果一个方法用XGet标记了，那么这个方法的作用就是从SharedPreferences文件中读取数据，XGet中的key就是读取时的key，其他的和XSet一样。

```
 @XGet(key = "name")
 String getName();
```
这样也不是很好，每写一个方法就得加一个注解，好麻烦的说，所以还可以更简单点。

####*使用接口不用注解*

```
private void saveAllAndFollowYourHeartToo() {
        IEmployee employee = XPrefs.getObject(IEmployee.class);
        employee.setName("员工");
        employee.setAge(22);
        employee.setSalary(3000);
        LogUtils.i("IEmployee name=" + employee.getName() + ";age=" + employee.getAge() + ";salary=" + employee.getSalary());
    }
```
这里的用法和用注解的是一样的，主要还是IEmployee接口上的区别，

```
@XSPFile(fileName = "IEmployee", fileMode = Context.MODE_PRIVATE)
public interface IEmployee {
    /**
     * 存储字段 name
     *
     * @param name value
     */
    void setName(String name);

    /**
     * 读取字段 name
     *
     * @return
     */
    String getName();

    void setAge(int age);

    int getAge();

    void setSalary(float salary);

    float getSalary();
}
```
可以看到，不用注解以后，代码少了将近一半。

这个时候key主要是通过解析方法名来得到的，所以一个方法以set开头的方法的作用就是写入数据到指定的SharedPreferences文件中，其中key就是方法名除去set的后半部分，且首字母小写，举个例子，方法setName的key就是name；

那么一个以get开头的方法的作用也是很明显了，就是从SharedPreferences文件中读取数据，其中key就是方法名出去get的后半部分，首字母也是小写，举个例子，方法getName的key就是name。看上去已经很方便了，但是这都不算什么，更骚的在后面。

####*最后一种用法*

```
 private void saveAllAndFollowYourHeartThree() {
        IStudent student = XPrefs.getObject(IStudent.class);
        student.name("学生3号");
        student.score(100);
        student.sex("女");
        LogUtils.i("IStudent name=" + student.name() + ";score=" + student.score() + ";sex=" + student.sex());
    }
```
这里的用法和上面两种依然没有什么区别，主要的区别还是在IStudent接口上，

```
@XSPFile(fileName = "IStudent")
public interface IStudent {
    /**
     * 存储字段 name
     *
     * @param name value
     */
    void name(String name);

    /**
     * 读取字段 name
     *
     * @return
     */
    String name();

    void score(int score);

    int score();

    void sex(String sex);

    String sex();
}
```
和上种用法相比，区别就是方法名中没有了set和get，那么这是怎么判断方法的作用和要存入数据的key的呢？首先，key就是方法名，完完全全的懒人的写法；其次，方法的作用是通过方法参数和方法返回值来判断的，

 1. 如果一个方法有一个参数，那么这个方法的作用就是写入数据到指定的SharedPreferences文件中，key是方法名，值是传入的参数；
 2. 如果一个方法没有参数并且方法的返回值不是void，那么这个方法的作用就是从指定的SharedPreferences文件中读取数据，key是方法名。

### 混淆时注意事项：
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
### 鸣谢
* [androidInject](https://github.com/wangjiegulu/androidInject)

### 关于我
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

