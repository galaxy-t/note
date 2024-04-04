# Java 中反射的方式有哪些

##### 1. 调用对象的 getClass 方法来获取该类的 Class 对象

```
Person p = new Person();
Class clazz = p.getClass();
```

##### 2. 调用类的 class 属性以获取该类对应的 Class 对象

```
Class clazz = Person.class;
```

##### 3. 调用 Class 类中的 forName 静态方法获取该类对象, 这是最安全也是性能最好的方法

```
Class clazz = Class.forName("com.user.entity.Person");      // 类的全路径, 包括包名
```



