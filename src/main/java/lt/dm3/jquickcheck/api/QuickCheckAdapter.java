package lt.dm3.jquickcheck.api;

import java.util.List;

public interface QuickCheckAdapter<GEN> {

    /**
     * @param generators
     * @param invocation
     * @return
     */
    QuickCheckResult check(List<GEN> generators, PropertyInvocation invocation);

}
