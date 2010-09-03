package lt.dm3.jquickcheck;

public class Configuration<G> {

    private QuickCheckAdapter adapter;
    private GeneratorResolutionStrategy<G> strategy;

    public Configuration<G> withResolutionStrategy(GeneratorResolutionStrategy<G> strategy) {
        this.strategy = strategy;
        return this;
    }

    public Configuration<G> withAdapter(QuickCheckAdapter adapter) {
        this.adapter = adapter;
        return this;
    }

    public GeneratorResolutionStrategy<G> generatorResolutionStrategy() {
        return strategy;
    }

    public QuickCheckAdapter adapter() {
        return adapter;
    }

}
