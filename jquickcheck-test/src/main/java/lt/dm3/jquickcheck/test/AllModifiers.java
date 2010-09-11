package lt.dm3.jquickcheck.test;

import java.util.Arrays;

import javassist.Modifier;

public class AllModifiers {

    private static final int[] modifiers = new int[] { Modifier.PUBLIC, Modifier.PRIVATE, Modifier.PROTECTED,
                                                Modifier.STATIC,
                                                Modifier.FINAL,
                                                Modifier.setPackage(Modifier.STATIC),
                                                Modifier.setPackage(Modifier.FINAL),
                                                Modifier.setPrivate(Modifier.STATIC),
                                                Modifier.setPrivate(Modifier.FINAL),
                                                Modifier.setProtected(Modifier.STATIC),
                                                Modifier.setProtected(Modifier.FINAL),
                                                Modifier.setPublic(Modifier.STATIC),
                                                Modifier.setPublic(Modifier.FINAL) };

    public static int[] toArray() {
        return Arrays.copyOf(modifiers, modifiers.length);
    }
}
