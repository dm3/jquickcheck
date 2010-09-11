package lt.dm3.jquickcheck.api;

public interface PropertyParameter<GEN> {

    /**
     * @param repo
     *            to search generators for
     * @return a generator suitable to generate values for this property
     * @throws QuickCheckException
     *             if a generator could not be found
     */
    GEN getGeneratorFrom(GeneratorRepository<GEN> repo);

}
