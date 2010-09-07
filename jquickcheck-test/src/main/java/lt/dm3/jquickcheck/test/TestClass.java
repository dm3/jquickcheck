package lt.dm3.jquickcheck.test;

import javassist.CannotCompileException;
import javassist.CtClass;

public class TestClass {

    private final CtClass clazz;

    public TestClass(CtClass clazz) {
        this.clazz = clazz;
    }

    public Class<?> load() {
        try {
            return clazz.toClass();
        } catch (CannotCompileException e) {
            throw new RuntimeException("Cannot load class: " + clazz, e);
        }
    }

    public String getName() {
        return clazz.getName();
    }

}
