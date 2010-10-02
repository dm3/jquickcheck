package examples;

import static fj.test.Arbitrary.arbitrary;
import static fj.test.Gen.choose;

import java.util.LinkedList;

import lt.dm3.jquickcheck.G;
import lt.dm3.jquickcheck.Property;
import lt.dm3.jquickcheck.QuickCheck;
import lt.dm3.jquickcheck.fj.FJ;
import lt.dm3.jquickcheck.junit4.QuickCheckRunner;

import org.junit.runner.RunWith;

import fj.test.Arbitrary;

public class FunctionalJavaAndJUnit {

    @RunWith(QuickCheckRunner.class)
    @QuickCheck(provider = FJ.class, useDefaults = true)
    public static class SimpleTestCase {
        @Property
        public boolean lengthOfConcatenatedStringsEqualToTheSumOfTheirLengths(String a, String b) {
            return a.length() + b.length() == (a + b).length();
        }

        @Property
        // just to show it really works - is the following true for empty strings?
        public boolean lengthOfConcatenatedStringsGreaterThanTheLengthOfEachStringSeparately(String a, String b) {
            int total = (a + b).length();
            return total > a.length() && total > b.length();
        }

        static class Generated {
            private final int value;

            Generated(int value) {
                this.value = value;
            }
        }

        @G
        public Generated arbGenerated(int value) {
            return new Generated(value);
        }

        @Property
        public boolean generatedValueIsNotNull(Generated generated) {
            return generated != null;
        }

    }

    // ======= Rewrites of samples provided @ functionaljava project ========

    @RunWith(QuickCheckRunner.class)
    @QuickCheck(provider = FJ.class, useDefaults = true)
    public static class AdditionCommutes {
        @Property
        public boolean additionCommutes(int a, int b) {
            return a + b == b + a;
        }
    }

    /*
     * This needs non-boolean return type for properties. 
     * A Result enum is needed (as in FJ).
     */
    @RunWith(QuickCheckRunner.class)
    @QuickCheck(provider = FJ.class, useDefaults = true)
    public static class EqualsHashCode {
        public static final class MyClass {
            private final byte b;
            private final String s;

            MyClass(final byte b, final String s) {
                this.b = b;
                this.s = s;
            }

            public byte b() {
                return b;
            }

            public String s() {
                return s;
            }

            @Override
            public boolean equals(final Object o) {
                return o != null &&
                        o.getClass() == MyClass.class &&
                        b == ((MyClass) o).b &&
                        s.equals(((MyClass) o).s);
            }

            @Override
            public int hashCode() {
                final int p = 419;
                int result = 239;
                result = p * result + b;
                result = p * result + s.hashCode();
                return result;
            }
        }

        @G
        public Byte arbByteR(Byte b) {
            return (byte) (b % 3);
        }

        @G
        // Restrictive arbitrary for String, produces from twelve (2 * 3 * 2) possible values.
        public String arbStringR(Character c1, Character c2, Character c3) {
            return new String(new char[] { (char) (c1 % 2 + 'a'), (char) (c2 % 3 + 'a'),
                    (char) (c3 % 2 + 'a') });
        }

        @G
        // Currently generators defined in "arbByteR" automatically take precedence over the default generators (a bug?)
        // TODO: @G(useDefaults = false)
        public MyClass arbMyClass(@G(gen = "arbByteR") Byte a, @G(gen = "arbStringR") String b) {
            return new MyClass(a, b);
        }

        @Property
        public boolean hashCodeMeansEqual(MyClass a, MyClass b) {
            return a.equals(b) ? a.hashCode() == b.hashCode() : true;
        }
    }

    @RunWith(QuickCheckRunner.class)
    // useDefaults = false by default
    @QuickCheck(provider = FJ.class)
    public static class IntegerOverflow {
        final Arbitrary<Integer> arbPositiveInt = arbitrary(choose(1, Integer.MAX_VALUE));

        @Property
        public boolean overflows(int a, int b) {
            return a + b > 0;
        }
    }

    @RunWith(QuickCheckRunner.class)
    @QuickCheck(provider = FJ.class, useDefaults = true)
    public static class JavaLinkedList {
        @Property
        // lists are synthesized automagically by keeping useSynthetics = true (default value)
        public boolean sizeAfterAddAllEqualToAddingSizesOfEach(LinkedList<Integer> a, LinkedList<Integer> b) {
            final LinkedList<Integer> ab = new LinkedList<Integer>(a);
            ab.addAll(b);
            return ab.size() == a.size() + b.size();
        }
    }

    @RunWith(QuickCheckRunner.class)
    // run each property 1000 times
    @QuickCheck(provider = FJ.class, useDefaults = true, minSuccessful = 1000)
    public static class StringBuilderReverse {
        @Property
        public boolean doubleReverseReturnsTheSame(StringBuilder sb) {
            return sb.reverse().reverse().toString().equals(sb.toString());
        }

        @Property
        public boolean reverseOnASingleCharacterBuilderResultsInTheSameValue(Character c) {
            return new StringBuilder().append(c).toString().equals(new StringBuilder().append(c).reverse().toString());
        }

        @Property
        public boolean appendingTwoStringBuildersAndReversingSameAsReversingTheSecondAndAppendingTheReverseOfTheFirst(
            StringBuilder a, StringBuilder b) {
            StringBuilder aa = new StringBuilder(a);
            StringBuilder bb = new StringBuilder(b);
            return a.append(b).reverse().toString().equals(bb.reverse().append(aa.reverse()).toString());
        }
    }

}
