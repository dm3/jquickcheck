package lt.dm3.jquickcheck.api;

import java.lang.reflect.Type;

public interface GeneratorRepository<G> {

    boolean hasGeneratorFor(Type t);

    boolean hasGeneratorFor(String fieldName);

    G getGeneratorFor(Type t);

    G getGeneratorFor(String fieldName);

    G getDefaultGeneratorFor(Type t);
}
