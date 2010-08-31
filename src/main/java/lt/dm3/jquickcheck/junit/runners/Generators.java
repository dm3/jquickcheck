package lt.dm3.jquickcheck.junit.runners;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import fj.data.Option;

public final class Generators {

    public interface GeneratorsForTestCase {
        boolean hasGeneratorFor(Type t);

        Generator<?> getGeneratorFor(Type t);
    }

    private static class AllGeneratorsForTestCase implements GeneratorsForTestCase {
        private final List<Generator<?>> generators = new ArrayList<Generator<?>>();

        AllGeneratorsForTestCase(Iterable<Field> fields, Object test) {
            for (Field field : fields) {
                try {
                    generators.add((Generator<?>) field.get(test));
                } catch (IllegalArgumentException e) {
                    System.err.println(e);
                } catch (IllegalAccessException e) {
                    System.err.println(e);
                }
            }
        }

        @Override
        public boolean hasGeneratorFor(Type t) {
            return findGeneratorFor(t).isSome();
        }

        @Override
        public Generator<?> getGeneratorFor(Type t) {
            return findGeneratorFor(t).some();
        }

        private Option<Generator<?>> findGeneratorFor(Type t) {
            for (Generator<?> g : generators) {
                Type[] interfaces = g.getClass().getGenericInterfaces();
                for (Type i : interfaces) {
                    if (i instanceof ParameterizedType) { // i.equals(Generator.class) or Generator.class.isAssignableFrom(i) doesn't work
                        ParameterizedType pType = (ParameterizedType) i;
                        if (pType.getRawType() == Generator.class) {
                            Type[] args = pType.getActualTypeArguments();
                            if (args.length != 1) {
                                throw new IllegalArgumentException("Cannot determine the type of the generator: " + g + " for argument of type " + t);
                            }
                            if (suitableFor(t, args[0])) {
                                return Option.<Generator<?>> some(g);
                            }
                        }
                    }
                }
            }
            return Option.none();
        }

        private boolean suitableFor(Type required, Type candidate) {
            return Primitives.equalIgnoreWrapping(required, candidate);
        }
    }

    private final List<Field> generatorFields = new ArrayList<Field>();

    public GeneratorsForTestCase forTest(Object test) {
        return new AllGeneratorsForTestCase(generatorFields, test);
    }

    public void add(Field field) {
        generatorFields.add(field);
    }

}
