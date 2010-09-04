package lt.dm3.jquickcheck.api;

import java.lang.reflect.Method;

public interface PropertyMethodFactory<GEN> {

    PropertyMethod<GEN> createMethod(Method method, Object target);

}
