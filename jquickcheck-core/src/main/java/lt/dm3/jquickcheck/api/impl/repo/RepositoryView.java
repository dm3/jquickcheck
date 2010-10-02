package lt.dm3.jquickcheck.api.impl.repo;

import java.lang.reflect.Type;
import java.util.EnumSet;
import java.util.Set;

import lt.dm3.jquickcheck.QuickCheck;
import lt.dm3.jquickcheck.api.GeneratorRepository;
import lt.dm3.jquickcheck.api.LookupDefaultByType;
import lt.dm3.jquickcheck.api.PropertyInvocation.Settings;
import lt.dm3.jquickcheck.api.impl.DefaultInvocationSettings;

public class RepositoryView<GEN> implements GeneratorRepository<GEN> {

    public static <T> GeneratorRepository<T> constrain(GeneratorRepository<T> repo, Settings settings) {
        Set<ViewStrategy> constraints = EnumSet.allOf(ViewStrategy.class);
        if (!settings.useDefaults()) {
            constraints.remove(ViewStrategy.DEFAULTS);
        }
        return new RepositoryView<T>(repo, constraints);
    }

    public static <T> LookupDefaultByType<T> constrain(final LookupDefaultByType<T> repo, Object testCase) {
        final Set<ViewStrategy> constraints = EnumSet.allOf(ViewStrategy.class);
        QuickCheck ann = testCase.getClass().getAnnotation(QuickCheck.class);
        if (ann != null) {
            Settings settings = new DefaultInvocationSettings(ann);
            if (!settings.useDefaults()) {
                constraints.remove(ViewStrategy.DEFAULTS);
            }
        }
        return new LookupDefaultByType<T>() {
            @Override
            public boolean hasDefault(Type t) {
                return constraints.contains(ViewStrategy.DEFAULTS) && repo.hasDefault(t);
            }

            @Override
            public T getDefault(Type t) {
                if (hasDefault(t)) {
                    return repo.getDefault(t);
                }
                throw new IllegalArgumentException("Default of type " + t
                        + " is either not permitted or doesn't exist.");
            }
        };
    }

    public enum ViewStrategy {
        DEFAULTS, NORMAL
    }

    private final GeneratorRepository<GEN> underlying;
    private final Set<ViewStrategy> views;

    public RepositoryView(GeneratorRepository<GEN> underlying, Set<ViewStrategy> views) {
        this.underlying = underlying;
        this.views = views;
    }

    public boolean has(Type by) {
        return underlying.has(by);
    }

    public boolean has(String name) {
        return underlying.has(name);
    }

    public GEN get(Type t) {
        return underlying.get(t);
    }

    public boolean hasDefault(Type t) {
        if (views.contains(ViewStrategy.DEFAULTS)) {
            return underlying.hasDefault(t);
        }
        return false;
    }

    public GEN get(String name) {
        return underlying.get(name);
    }

    public boolean hasSynthetic(Type t) {
        return underlying.hasSynthetic(t);
    }

    public GEN getDefault(Type t) {
        if (views.contains(ViewStrategy.DEFAULTS)) {
            return underlying.getDefault(t);
        }
        throw new IllegalStateException("Defaults are restricted");
    }

    public GEN getSynthetic(Type type) {
        return underlying.getSynthetic(type);
    }

    /**
     * @return the underlying repository
     */
    protected final GeneratorRepository<GEN> unwrap() {
        return underlying;
    }
}
