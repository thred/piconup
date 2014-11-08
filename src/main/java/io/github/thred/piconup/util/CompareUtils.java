package io.github.thred.piconup.util;

import java.text.Collator;
import java.util.Comparator;

/**
 * Utilities for comparing objects
 *
 * @author Manfred Hantschel
 */
public final class CompareUtils
{

    /**
     * A collator set to primary strength, which means 'a', 'A' and '&auml;' is the same
     */
    public static final Collator DICTIONARY_COLLATOR;

    public static final Comparator<String> DICTIONARY_COMPARATOR = new Comparator<String>()
        {

        @Override
        public int compare(String left, String right)
        {
            return dictionaryCompare(left, right);
        }

        };

        static
        {
            DICTIONARY_COLLATOR = Collator.getInstance();

            DICTIONARY_COLLATOR.setStrength(Collator.PRIMARY);
            DICTIONARY_COLLATOR.setDecomposition(Collator.CANONICAL_DECOMPOSITION);
        }

        private CompareUtils()
        {
            super();
        }

        /**
         * Compares the two objects. If one of the objects is null, it will always be greater than the other object. If both
         * objects are null, they are equal.
         *
         * @param <TYPE> the type of the object
         * @param left the first object
         * @param right the second object
         * @return the result of the compare function
         */
        public static <TYPE extends Comparable<TYPE>> int compare(final TYPE left, final TYPE right)
        {
            if (left == null)
            {
                if (right != null)
                {
                    return 1;
                }
            }
            else
            {
                if (right != null)
                {
                    return left.compareTo(right);
                }

                return -1;
            }

            return 0;
        }

        /**
         * Compares the two objects. If one of the objects is null, it will always be greater than the other object. If both
         * objects are null, they are equal. Uses the comparator to compare the objects.
         *
         * @param <TYPE> the type of the object
         * @param comparator the comparator to be used
         * @param left the first object
         * @param right the second object
         * @return the result of the compare function
         */
        public static <TYPE> int compare(final Comparator<TYPE> comparator, final TYPE left, final TYPE right)
        {
            if (left == null)
            {
                if (right != null)
                {
                    return 1;
                }
            }
            else
            {
                if (right != null)
                {
                    return comparator.compare(left, right);
                }

                return -1;
            }

            return 0;
        }

        /**
         * Compares the strings using a dictionary collator. If one of the objects is null, it will always be greater than
         * the other object. If both objects are null, they are equal.
         *
         * @param left the first string
         * @param right the second string
         * @return the result of the compare function
         */
        public static int dictionaryCompare(final String left, final String right)
        {
            return compare(DICTIONARY_COLLATOR, left, right);
        }

}
