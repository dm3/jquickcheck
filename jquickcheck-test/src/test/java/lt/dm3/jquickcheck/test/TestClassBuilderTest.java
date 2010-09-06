package lt.dm3.jquickcheck.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javassist.Modifier;
import javassist.NotFoundException;
import lt.dm3.jquickcheck.junit4.QuickCheckRunner;
import lt.dm3.jquickcheck.sample.Generator;
import lt.dm3.jquickcheck.sample.IntegerGenerator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;

public class TestClassBuilderTest {

    @Test
    public void shouldCreateAJunitTestClassWithAnnotation() throws ClassNotFoundException, NotFoundException {
        TestClassBuilder.forJUnit4("lol", Generator.class).build();

        Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass("lol");
        RunWith runAnnotation = clazz.getAnnotation(RunWith.class);
        Class<? extends Runner> runnerClass = runAnnotation.value();

        assertThat(runnerClass, typeCompatibleWith(QuickCheckRunner.class));
    }

    @Test
    public void shouldCreateAJunitTestClassWithOneGeneratorOfTheSpecifiedType() throws ClassNotFoundException,
                                                                               SecurityException, NoSuchFieldException,
                                                                               InstantiationException,
                                                                               IllegalAccessException {
        TestClassBuilder
                .forJUnit4("lol2", Generator.class)
                .withGenerator(Integer.class.getName(), "intGen",
                               ClassUtils.newInstance(IntegerGenerator.class),
                               Modifier.PUBLIC)
                .build();

        Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass("lol2");
        Object instance = clazz.newInstance();

        ParameterizedType pType = (ParameterizedType) clazz.getField("intGen").getGenericType();
        assertThat(clazz.getField("intGen").getType(), equalTo((Type) Generator.class));
        assertThat(pType.getActualTypeArguments()[0], equalTo((Type) Integer.class));
        assertThat(clazz.getField("intGen").get(instance), instanceOf(IntegerGenerator.class));
    }
}
