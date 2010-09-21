package lt.dm3.jquickcheck.api.impl;

public interface NamedGenerator<GEN> {

    String getName();

    GEN getGenerator();

}
