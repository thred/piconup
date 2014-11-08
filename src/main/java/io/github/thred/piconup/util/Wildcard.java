package io.github.thred.piconup.util;

import java.util.regex.Pattern;

/**
 * Provides wildcard matching functionality for strings (and not just files as in commons-io)
 *
 * @author Manfred Hantschel
 */
public final class Wildcard
{

    /**
     * The wildcard for a single character (?)
     */
    public static final char SINGLE_TOKEN = '?';

    /**
     * The wildcard for multiple characters (*)
     */
    public static final char MULTIPLE_TOKEN = '*';

    /**
     * The delimiter for multiple wildcard patterns
     */
    public static final char DELIMITER_TOKEN = ',';

    private static final Pattern DELIMITER_SPLIT_PATTERN = Pattern.compile("\\s*\\" + DELIMITER_TOKEN + "\\s*");

    private Wildcard()
    {
        super();
    }

    /**
     * Returns true if the string contains wildcards (? and *)
     *
     * @param s the string, may be null
     * @return true if the string contains wildcards
     */
    public static boolean isPattern(final String s)
    {
        if (s == null)
        {
            return false;
        }

        return (s.indexOf(SINGLE_TOKEN) >= 0) || (s.indexOf(MULTIPLE_TOKEN) >= 0);
    }

    /**
     * Returns true if the string contains wildcards (? and *) or multiple patterns (,)
     *
     * @param s the string, may be null
     * @return true if the string contains wildcards
     */
    public static boolean isMultiPattern(final String s)
    {
        if (s == null)
        {
            return false;
        }

        return isPattern(s) || (s.indexOf(DELIMITER_TOKEN) >= 0);
    }

    /**
     * Matches the string using wildcards (? and *). If the string is null, this method always returns false. Returns
     * true if at least one pattern matches.
     *
     * @param s the string, may be null
     * @param patterns the patterns, must be at least one
     * @return true if the string matches the pattern
     */
    public static boolean match(final String s, final String... patterns)
    {
        if ((patterns == null) || (patterns.length < 1))
        {
            throw new IllegalArgumentException("Pattern is null or empty");
        }

        if (s == null)
        {
            return false;
        }

        for (String pattern : patterns)
        {
            if (wildcardMatch(pattern, 0, s, 0))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Matches the string using wildcards (? and *). If the string is null, this method always returns false. Splits the
     * string prior to matching (,). Returns true if at least one pattern matches
     *
     * @param s the string, may be null
     * @param pattern the pattern
     * @return true if the string matches the pattern
     */
    public static boolean multiMatch(final String s, final String pattern)
    {
        if (s == null)
        {
            return false;
        }

        return match(s, DELIMITER_SPLIT_PATTERN.split(pattern));
    }

    /**
     * Performs a match operation using wildcards.
     *
     * @param patternIndex the patternIndex
     * @param tmpIndex the current index
     * @param value the value
     * @param valueIndex the current value
     * @return true on match
     */
    //CHECKSTYLE:OFF split not possible
    private static boolean wildcardMatch(final String pattern, final int patternIndex, final String value,
        final int valueIndex)
    {
        int tmpPatternIndex = patternIndex;
        int tmpValueIndex = valueIndex;

        while (tmpPatternIndex < pattern.length())
        {
            if (SINGLE_TOKEN == pattern.charAt(tmpPatternIndex))
            {
                tmpPatternIndex += 1;

                if (tmpValueIndex < value.length())
                {
                    tmpValueIndex += 1;
                }
                else
                {
                    return false;
                }
            }
            else if (MULTIPLE_TOKEN == pattern.charAt(tmpPatternIndex))
            {
                while ((tmpPatternIndex < pattern.length()) && (MULTIPLE_TOKEN == pattern.charAt(tmpPatternIndex)))
                {
                    tmpPatternIndex += 1;
                }

                if (tmpPatternIndex >= pattern.length())
                {
                    return true;
                }

                while (tmpValueIndex < value.length())
                {
                    if (wildcardMatch(pattern, tmpPatternIndex, value, tmpValueIndex))
                    {
                        return true;
                    }

                    tmpValueIndex += 1;
                }
            }
            else if ((tmpValueIndex < value.length())
                && (pattern.charAt(tmpPatternIndex) == value.charAt(tmpValueIndex)))
            {
                tmpPatternIndex += 1;
                tmpValueIndex += 1;
            }
            else
            {
                return false;
            }
        }

        return ((tmpPatternIndex >= pattern.length()) && (tmpValueIndex >= value.length()));
    }
    //CHECKSTYLE:ON

}
