package lt.dm3.jquickcheck.api.impl.resolution;

public interface NamedAndTypedGenerator<GEN> extends NamedGenerator<GEN>, TypedGenerator<GEN> {

    boolean isDefault();

}
