package net.xser.ratelimiter;

import java.lang.reflect.Proxy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Hello world!
 */
public class App {
    public static long begin;
    public static AtomicLong atomicLong = new AtomicLong();

    public static void main(String[] args) throws InterruptedException {
        final ServiceImpl service = new ServiceImpl();
        IService proxyService = (IService) Proxy.newProxyInstance(ServiceImpl.class.getClassLoader(), ServiceImpl.class.getInterfaces(), (proxy, method, args1) -> {
            if (method.isAnnotationPresent(RateLimit.class)) {
                RateLimit rateLimit = method.getDeclaredAnnotation(RateLimit.class);
                int count = rateLimit.countLimit();
                if (System.currentTimeMillis() - begin < TimeUnit.SECONDS.toMillis(rateLimit.durationSeconds())) {
                    atomicLong.incrementAndGet();
                    //超过单位时间阈值,抛弃请求
                    if (atomicLong.get() > count) {
                        return null;
                    } else {
                        //单位时间阈值正常
                        return method.invoke(service, args1);
                    }
                } else {
                    //新的时间周期
                    begin = System.currentTimeMillis();
                    atomicLong.set(0);
                    return method.invoke(service, args1);
                }
            }
            return method.invoke(service, args1);
        });
        ///////run test
        while (true) {
            Thread.sleep(100);
            proxyService.run();
        }
    }
}