# synchronized

`synchronized` 翻译过来就是 `同步` 的意思, 在 java 中也被成为同步锁, 其作用是保证在同一时刻, 被修饰的代码块和方法只会有一个线程执行,
以达到并发安全的效果.

## 类型

* 类锁: 锁的作用域是当前类, 即同一个类的所有对象都使用同一把锁
* 对象锁: 锁的作用域是当前对象, 即同一个对象使用同一把锁, 不同的对象使用不同的锁

## 使用方式

* 修饰实例方法: 作用域为当前实例
* 修饰静态方法: 作用域为当前类
* 修饰代码块: 指定加锁对象, 作用域视加锁对象而定

## 特性

* 互斥
* 可重入

## 指令

在字节码指令中

* `monitorenter`: 使用该指令进行加锁
* `monitorexit`: 使用该指令进行解锁

如果存在多个锁, 那么编译器会进行优化, 只需要加锁一次即可, 但需要解锁多次, 即`monitorenter`只会出现一次,
但会在后面出现多次`monitorexit`, 这也是可重入锁的一种体现

## 修饰实例方法

```text
public synchronized void test() {
}
```

**作用域: 当前类的对象**

* 同一个对象 test 方法是同步的, 是同一把锁
* 不同的对象 test 方法是不同的锁

## 修饰静态方法

```text
public static synchronized void test() {
}
```

**作用域: 当前类**

* 全局 test 方法都使用同一把锁, 因为是静态方法, 他是属于类的, 不涉及到对象的概念

## 同步代码块

同步代码块的作用域取决于加锁对象的作用于, 有以下几种情况

```text
synchronized (this) {
}
```

**this: 作用域为当前对象实例, 因为使用了 this, this 代表当前对象实例的意思**

```text
synchronized (Test.class) {
}
```   

**class: 作用域为当前类, 对类的所有实例生效**

```text
private Object obj;         // 针对这个对象进行加锁, 该类每一个实例都有一个 obj 对象, 所以导致该方式加锁的范围是对象
public void test() {
    synchronized (obj) {

    }
}
```

**obj: 对象锁**

```text
public static Object obj;       // 针对 obj 进行加锁, 因为 obj 是静态的, 全局只有一个, 也就导致了同步代码块的作用范围是全局的
public void test() {
    synchronized (obj) {

    }
}
```

**static obj: 类锁**
        
