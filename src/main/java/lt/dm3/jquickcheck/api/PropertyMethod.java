package lt.dm3.jquickcheck.api;

public interface PropertyMethod<GEN> {

    PropertyInvocation<GEN> createInvocationWith(GeneratorRepository<GEN> repo);

}
