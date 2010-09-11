package lt.dm3.jquickcheck.test.builder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

import javassist.bytecode.Descriptor;
import lt.dm3.jquickcheck.test.builder.ClassUtils;

import org.junit.Test;

public class ClassUtilsTest {

    @Test
    public void shouldDescribeNonParameterizedClass() {
        String result = ClassUtils.describe(Integer.class);

        assertThat(result, equalTo(Descriptor.of(Integer.class.getName())));
    }

    public void withOneTypeParam(List<Integer> one) {

    }

    @Test
    public void shouldDescribeClassParameterizedWithOneParameter() throws SecurityException, NoSuchMethodException {
        Method oneParam = this.getClass().getMethod("withOneTypeParam", List.class);
        ParameterizedType paramType = (ParameterizedType) oneParam.getGenericParameterTypes()[0];
        String result = ClassUtils.describe(paramType);

        assertThat(result, equalTo("Ljava/util/List<Ljava/lang/Integer;>;"));
    }

    public void withTwoTypeParams(Map<Integer, Long> two) {

    }

    @Test
    public void shouldDescribeClassParameterizedWithTwoParameters() throws SecurityException, NoSuchMethodException {
        Method twoParams = this.getClass().getMethod("withTwoTypeParams", Map.class);
        ParameterizedType paramType = (ParameterizedType) twoParams.getGenericParameterTypes()[0];
        String result = ClassUtils.describe(paramType);

        assertThat(result, equalTo("Ljava/util/Map<Ljava/lang/Integer;Ljava/lang/Long;>;"));
    }

    public void withTwoLevelDeepTypeParam(List<List<Integer>> two) {

    }

    @Test
    public void shouldDescribeClassParameterizedWithTwoLevelDeepParameters() throws SecurityException,
        NoSuchMethodException {
        Method twoParams = this.getClass().getMethod("withTwoLevelDeepTypeParam", List.class);
        ParameterizedType paramType = (ParameterizedType) twoParams.getGenericParameterTypes()[0];
        String result = ClassUtils.describe(paramType);

        assertThat(result, equalTo("Ljava/util/List<Ljava/util/List<Ljava/lang/Integer;>;>;"));
    }
}