package lt.dm3.jquickcheck;

import lt.dm3.jquickcheck.junit.runners.Generator;

public interface QuickCheckAdapter {

    QuickCheckResult check(Generator<?>[] generators, Invocation invocation);

}
