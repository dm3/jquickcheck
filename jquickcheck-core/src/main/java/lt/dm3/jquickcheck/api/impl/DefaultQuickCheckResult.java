package lt.dm3.jquickcheck.api.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lt.dm3.jquickcheck.api.QuickCheckResult;

public final class DefaultQuickCheckResult implements QuickCheckResult {
    private final Status status;
    private final Throwable throwable;
    private final List<?> arguments;

    public enum Status {
        Passed, Proven, Falsified, Exhausted
    }

    DefaultQuickCheckResult(Status status) {
        this(status, (Throwable) null);
    }

    DefaultQuickCheckResult(Status status, Throwable throwable) {
        this(status, throwable, Collections.emptyList());
    }

    DefaultQuickCheckResult(Status status, List<?> arguments) {
        this(status, null, arguments);
    }

    DefaultQuickCheckResult(Status status, Throwable throwable, List<?> arguments) {
        this.status = status;
        this.throwable = throwable;
        this.arguments = arguments;
    }

    @Override
    public boolean isPassed() {
        return status == Status.Passed;
    }

    @Override
    public boolean isProven() {
        return status == Status.Proven;
    }

    @Override
    public boolean isFalsified() {
        return status == Status.Falsified;
    }

    @Override
    public boolean isExhausted() {
        return status == Status.Exhausted;
    }

    @Override
    public Throwable exception() {
        return throwable;
    }

    @Override
    public List<?> arguments() {
        return arguments;
    }

    public static QuickCheckResult falsified(Object... args) {
        return new DefaultQuickCheckResult(Status.Falsified, Arrays.asList(args));
    }

    public static QuickCheckResult falsified(Throwable t) {
        return new DefaultQuickCheckResult(Status.Falsified, t);
    }

    public static QuickCheckResult falsified(Throwable t, Object... args) {
        return new DefaultQuickCheckResult(Status.Falsified, t, Arrays.asList(args));
    }

    public static QuickCheckResult proven() {
        return new DefaultQuickCheckResult(Status.Proven);
    }

    public static QuickCheckResult exhausted() {
        return new DefaultQuickCheckResult(Status.Exhausted);
    }

}
