package lt.dm3.jquickcheck.api;

public interface LookupSyntheticByType<GEN> {

    GEN getSynthetic(RequestToSynthesize<GEN> req);
}
