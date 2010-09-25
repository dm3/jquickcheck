package lt.dm3.jquickcheck.api.impl.resolution;

public interface NamedGenerator<GEN> {

    String getName();

    GEN getGenerator();

}
