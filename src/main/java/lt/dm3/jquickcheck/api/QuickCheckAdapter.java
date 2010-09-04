package lt.dm3.jquickcheck.api;

import lt.dm3.jquickcheck.junit4.Generator;

public interface QuickCheckAdapter {

    QuickCheckResult check(Generator<?>[] generators, Invocation invocation);

}
