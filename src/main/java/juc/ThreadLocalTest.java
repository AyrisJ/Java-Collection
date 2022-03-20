package juc;

import cn.hutool.core.util.ReflectUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadLocalTest {

    Logger logger = LoggerFactory.getLogger(ThreadLocalTest.class);

    /**
     * 子线程获取不到父线程值模拟
     */
    @Test
    public void testSubThread() {
        ThreadLocal<Integer> count = ThreadLocal.withInitial(() -> 1);

        logger.info("主线程-{} count={}", Thread.currentThread().getId(), count.get());
        count.set(count.get() + 1);

        new Thread(() -> {
            logger.info("子线程-{} count={}", Thread.currentThread().getId(), count.get());
        }).start();

        logger.info("主线程-{} count={}", Thread.currentThread().getId(), count.get());
    }

    @Test
    public void testSubThread2() throws InterruptedException {
        ThreadLocal<Integer> count = new ThreadLocal<>();
        count.set(1);

        logger.info("主线程-{} count={}", Thread.currentThread().getId(), count.get());
        new Thread(() -> {
            logger.info("子线程-{} count={}", Thread.currentThread().getId(), count.get());
        }).start();

        System.gc();
        logger.info("主线程-{} count={}", Thread.currentThread().getId(), count.get());

        count.set(2);
        logger.info("主线程-{} count={}", Thread.currentThread().getId(), count.get());

        count.remove();
        logger.info("主线程-{} count={}", Thread.currentThread().getId(), count.get());

    }

    /**
     * 父子线程传值问题
     */
    @Test
    public void testInheritableThread() {
        ThreadLocal<Integer> count = new InheritableThreadLocal<>();
        count.set(1);

        logger.info("主线程-{} count={}", Thread.currentThread().getId(), count.get());
        new Thread(() -> {
            logger.info("子线程-{} count={}", Thread.currentThread().getId(), count.get());
        }).start();
    }

    @Test
    public void testSet() {
        ThreadLocal<Integer> count = new ThreadLocal<>();
        count.set(1);

        ThreadLocal<String> name = new ThreadLocal<>();
        name.set("hello");

    }

    @Test
    public void testGet() {
        ThreadLocal<Integer> count = new ThreadLocal<>();
        count.set(1);
        int result = count.get();
        count = null;
        logger.info("主线程-{} count={}", Thread.currentThread().getId(), result);
    }

    @Test
    public void testRemove() {
        ThreadLocal<Integer> count = new ThreadLocal<>();
        count.set(1);
        count.remove();
        logger.info("主线程-{} count={}", Thread.currentThread().getId(), count.get());
    }

    @Test
    public void testGcWeakRef() {
        ThreadLocal<Integer> count = new ThreadLocal<>();
        count.set(1);
        int result = count.get();
        count = null;
        logger.info("主线程-{} count={}", Thread.currentThread().getId(), result);

        // 模拟内存泄漏，gc将会回收弱引用
        System.gc();

        ThreadLocal<String> name = new ThreadLocal<>();
        name.set("hello");
        String n1 = name.get();
        logger.info("主线程-{} name={}", Thread.currentThread().getId(), n1);
    }

    @Test
    public void testGcWeakRef2() {
        new ThreadLocal<>().set(1);
        printThreadLocalMapDetail();
        // 模拟内存泄漏，gc将会回收弱引用
        System.gc();

        logger.info("------------------------------after gc-----------");
        printThreadLocalMapDetail();
    }

    public void printThreadLocalMapDetail() {
        Object threadLocalMap = ReflectUtil.getFieldValue(Thread.currentThread(), "threadLocals");
        Object[] tables = (Object[]) ReflectUtil.getFieldValue(threadLocalMap, "table");
        for (Object object : tables) {
            Object referent=ReflectUtil.getFieldValue(object, "referent");
            Object value=ReflectUtil.getFieldValue(object, "value");
            logger.info("referent={},value={}", referent, value);
        }
    }

    public static void main(String[] args) {

    }
}
