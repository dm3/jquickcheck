package lt.dm3.jquickcheck.api;

public interface PropertyParameter<GEN> {

    GEN getGeneratorFrom(GeneratorRepository<GEN> repo);

}
