package lt.dm3.jquickcheck.test.builder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.Assert.assertThat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;
import lt.dm3.jquickcheck.G;
import lt.dm3.jquickcheck.Property;
import lt.dm3.jquickcheck.QuickCheck;
import lt.dm3.jquickcheck.api.impl.DefaultInvocationSettings;
import lt.dm3.jquickcheck.sample.Generator;
import lt.dm3.jquickcheck.sample.IntegerGenerator;
import lt.dm3.jquickcheck.sample.SampleRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;

public class TestClassBuilderTest {

    @Test
    public void shouldCreateASampleTestClassWithAnnotation() throws ClassNotFoundException, NotFoundException {
        SampleTestClassBuilder.forSample("lol", Generator.class).build().load();

        Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass("lol");
        RunWith runAnnotation = clazz.getAnnotation(RunWith.class);
        Class<? extends Runner> runnerClass = runAnnotation.value();

        assertThat(runnerClass, typeCompatibleWith(SampleRunner.class));
        assertThat(clazz.getAnnotation(QuickCheck.class).useDefaults(),
                   is(DefaultInvocationSettings.DEFAULT_USE_DEFAULTS));
    }

    @Test
    public void shouldCreateASampleTestClassWithOneGeneratorOfTheSpecifiedType() throws ClassNotFoundException,
                                                                               SecurityException, NoSuchFieldException,
                                                                               InstantiationException,
                                                                               IllegalAccessException {
        String fieldName = "intGen";
        SampleTestClassBuilder.forSample("lol2", Generator.class)
                .withGenerator(Modifier.PUBLIC, Descriptor.of(Integer.class.getName()), fieldName,
                               ClassUtils.newInstance(IntegerGenerator.class))
                .build().load();

        Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass("lol2");
        Object instance = clazz.newInstance();

        ParameterizedType pType = (ParameterizedType) clazz.getField(fieldName).getGenericType();
        assertThat(clazz.getField(fieldName).getType(), equalTo((Type) Generator.class));
        assertThat(pType.getActualTypeArguments()[0], equalTo((Type) Integer.class));
        assertThat(clazz.getField(fieldName).get(instance), instanceOf(IntegerGenerator.class));
    }

    @Test
    public void shouldCreateASampleTestClassWithTwoGeneratorsOfTheSpecifiedType() throws ClassNotFoundException,
                                                                               SecurityException, NoSuchFieldException,
                                                                               InstantiationException,
                                                                               IllegalAccessException {
        String fieldName1 = "intGen";
        String fieldName2 = "intListGen";
        SampleTestClassBuilder
                .forSample("lol5", Generator.class)
                .withGenerator(Modifier.PUBLIC, Descriptor.of(Integer.class.getName()), fieldName1,
                               ClassUtils.newInstance(IntegerGenerator.class))
                .withGenerator(Modifier.PUBLIC, ClassUtils.parameterized(List.class).of(Integer.class).build(),
                               fieldName2,
                               ClassUtils.newInstance(ListIntGenerator.class))
                .build().load();

        Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass("lol5");
        Object instance = clazz.newInstance();

        // Integer
        ParameterizedType pType = (ParameterizedType) clazz.getField(fieldName1).getGenericType();
        assertThat(clazz.getField(fieldName1).getType(), equalTo((Type) Generator.class));
        assertThat(pType.getActualTypeArguments()[0], equalTo((Type) Integer.class));
        assertThat(clazz.getField(fieldName1).get(instance), instanceOf(IntegerGenerator.class));

        // List<Integer>
        ParameterizedType pType2 = (ParameterizedType) clazz.getField(fieldName2).getGenericType();
        assertThat(pType2.getActualTypeArguments()[0], instanceOf(ParameterizedType.class));
        ParameterizedType pType2Type = ((ParameterizedType) pType2.getActualTypeArguments()[0]);
        assertThat(pType2Type.getActualTypeArguments()[0], equalTo((Type) Integer.class));
    }

    @Test
    public void shouldCreateASampleTestClassWithOneGeneratorOfTheSpecifiedCollectionType()
        throws ClassNotFoundException, SecurityException, NoSuchFieldException, InstantiationException,
               IllegalAccessException {

        String fieldName = "intGen";
        SampleTestClassBuilder
                .forSample("lol3", Generator.class)
                .withGenerator(Modifier.PUBLIC, ClassUtils.parameterized(List.class).of(Integer.class).build(),
                               fieldName,
                               ClassUtils.newInstance(ListIntGenerator.class))
                .build().load();

        Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass("lol3");
        Object instance = clazz.newInstance();

        assertThat(clazz.getField(fieldName).getType(), equalTo((Type) Generator.class));
        assertThat(clazz.getField(fieldName).get(instance), instanceOf(ListIntGenerator.class));

        // check the generic type of the field
        ParameterizedType pType = (ParameterizedType) clazz.getField(fieldName).getGenericType();
        assertThat(pType.getActualTypeArguments()[0], instanceOf(ParameterizedType.class));
        ParameterizedType pType2 = ((ParameterizedType) pType.getActualTypeArguments()[0]);
        assertThat(pType2.getActualTypeArguments()[0], equalTo((Type) Integer.class));
    }

    @Test
    public void shouldCreateASampleTestClassWithOnePropertyContainingSpecifiedParameters()
        throws ClassNotFoundException, SecurityException, NoSuchMethodException, InstantiationException,
        IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        String methodName = "prop1";
        SampleTestClassBuilder.forSample("lol4", Generator.class).withProperty(methodName)
                    .with(Parameter.of(Integer.class))
                    .with(Parameter.of(int.class)).and()
                .build().load();

        Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass("lol4");
        Object instance = clazz.newInstance();

        // then
        Method m = clazz.getMethod(methodName, Integer.class, int.class);
        assertThat(m.getParameterTypes().length, equalTo(2));
        assertThat(m.getAnnotation(Property.class), is(not(nullValue())));
        assertThat((Boolean) m.invoke(instance, new Object[] { 1, 1 }), is(true));
    }

    @Test
    public void shouldCreateASampleTestClassWithOnePropertyHavingAParameterWithAnAnnotation()
        throws ClassNotFoundException, InstantiationException, IllegalAccessException, SecurityException,
        NoSuchMethodException {

        String methodName = "prop1";
        String annotationValue = "lulz";
        SampleTestClassBuilder.forSample("lol7", Generator.class).withProperty(methodName)
                    .with(Parameter.of(Integer.class).annotatedBy(G.class).with("gen", annotationValue).end()).and()
                .build().load();

        Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass("lol7");

        // then
        Method m = clazz.getMethod(methodName, Integer.class);
        assertThat(m.getParameterTypes().length, equalTo(1));
        assertThat(m.getAnnotation(Property.class), is(not(nullValue())));

        G gAnnotation = (G) m.getParameterAnnotations()[0][0];
        assertThat(gAnnotation.gen(), equalTo(annotationValue));
    }

    @Test
    public void shouldCreateASampleTestClassWithUseDefaultsPropertySetToTrueOnClassLevel()
        throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        SampleTestClassBuilder.forSample("lol6", Generator.class).useDefaults().build().load();

        Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass("lol6");

        QuickCheck qc = clazz.getAnnotation(QuickCheck.class);
        assertThat(qc.useDefaults(), is(true));
    }
}
