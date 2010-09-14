package lt.dm3.jquickcheck.testng;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javassist.util.proxy.ProxyFactory;

import org.testng.IObjectFactory;

public class TestNGObjectFactory<GEN> implements IObjectFactory {
    private static final long serialVersionUID = -3328994316475592517L;

    @SuppressWarnings("rawtypes")
    public Object newInstance(Constructor constructor, Object... params) {
        Class<?> clazz = constructor.getDeclaringClass();

        ProxyFactory factory = new ProxyFactory();
        factory.setFilter(new QuickCheckPropertyFilter());
        factory.setSuperclass(clazz);
        try {
            return factory.create(new Class[] {}, new Object[] {}, new QuickCheckMethodHandler(clazz));
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
