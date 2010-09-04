package lt.dm3.jquickcheck.api.impl;

public interface HasGenerator<GEN> extends NamedGenerator, TypedGenerator {

    GEN getGenerator();

}
