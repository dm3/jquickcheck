package lt.dm3.jquickcheck.api.impl;

public interface NamedAndTypedGenerator<GEN> extends NamedGenerator, TypedGenerator {

    GEN getGenerator();

}
