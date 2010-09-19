package lt.dm3.jquickcheck.api.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import lt.dm3.jquickcheck.Provider;
import lt.dm3.jquickcheck.api.GeneratorResolutionStrategy;
import lt.dm3.jquickcheck.api.PropertyInvocation.Settings;
import lt.dm3.jquickcheck.api.PropertyMethodFactory;
import lt.dm3.jquickcheck.api.QuickCheckAdapter;
import lt.dm3.jquickcheck.api.QuickCheckException;

/**
 * NOTE: As we don't know what type of generators this provider will deal with, here we have two choices - either to
 * <ol>
 * <li>
 * implement the raw {@code Provider} type which will make us remove the wildcard type parameter of the
 * <code>Provider</code> in the <code>QuickCheck#provider</code> requiring a <code>suppress rawtypes</code> annotation.
 * <br />
 * We would have to go from
 * 
 * <pre>
 * Class&lt;? extends Provider&lt;?&gt;&gt;
 * </pre>
 * 
 * to
 * 
 * <pre>
 * Class&lt;? extends Provider&gt;
 * </pre>
 * 
 * </li>
 * <li>implement the Provider type bound by {@code Object} which makes the ProxyProvider type generic and suitable for
 * the default value of the <code>QuickCheck#provider</code> parameter</li>
 * </ol>
 * 
 * @author dm3
 * 
 */
public class ProxyProvider implements Provider<Object> {

    private static final Set<String> possibleProviders = new HashSet<String>();

    private final Provider<Object> realProvider;

    static {
        register("lt.dm3.jquickcheck.fj.FJ");
        register("lt.dm3.jquickcheck.qc.QC");
    }

    /**
     * Registers a class to be tried for a JQuickCheck provider if the provider isn't specified in the QuickCheck
     * annotation explicitly. Does nothing if this class was already registered before.
     * <p>
     * You should only be calling this method if you're implementing your own JQuickCheck provider.
     * 
     * @param className
     */
    public static void register(String className) {
        possibleProviders.add(className);
    }

    /**
     * Deregisters a class from a list of JQuickCheck provider candidates. Does nothing if the class wasn't registered
     * previously.
     * <p>
     * You should only be calling this method if you're implementing your own JQuickCheck provider.
     * 
     * @param className
     */
    public static void deregister(String className) {
        possibleProviders.remove(className);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ProxyProvider() {
        Class<?> provider = null;
        Iterator<String> providers = possibleProviders.iterator();
        while (providers.hasNext() && provider == null) {
            String providerName = providers.next();
            try {
                provider = Class.forName(providerName);
            } catch (ClassNotFoundException e) {
                providers.remove();
            }
        }

        try {
            realProvider = (Provider) provider.newInstance();
        } catch (Exception e) {
            throw new QuickCheckException("Could not instantiate provider: " + provider, e);
        }
    }

    @Override
    public GeneratorResolutionStrategy<Object> resolutionStrategy() {
        return realProvider.resolutionStrategy();
    }

    @Override
    public QuickCheckAdapter<Object> adapter() {
        return realProvider.adapter();
    }

    @Override
    public PropertyMethodFactory<Object> methodFactory(Settings settings) {
        return realProvider.methodFactory(settings);
    }

}
