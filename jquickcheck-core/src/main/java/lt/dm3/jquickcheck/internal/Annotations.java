package lt.dm3.jquickcheck.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Annotations {

    private Annotations() {
        // static utils
    }

    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T newInstance(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[] { clazz },
                                          new InvocationHandler() {
                                              @Override
                                              public Object invoke(Object proxy, Method method, Object[] args)
                                                  throws Throwable {
                                                  return method.getDefaultValue();
                                              }
                                          });
    }
}
