package io.github.thred.piconup.util;

import java.util.HashMap;
import java.util.Map;

public class PiconUpUtil
{

    private static final Map<String, String[]> SYNONYMS = new HashMap<>();

    static
    {
        synonym("9", "NEUN\\b", "NINE\\b", "\\bIX\\b");
        synonym("8", "ACHT\\b", "EIGHT\\b", "\\bVIII\\b");
        synonym("7", "SIEBEN\\b", "SEVEN\\b", "\\bVII\\b");
        synonym("6", "SECHS\\b", "SIX\\b", "\\bVI\\b");
        synonym("5", "FUENF\\b", "FIVE\\b", "\\bV\\b");
        synonym("4", "VIER\\b", "FOUR\\b", "\\bIV\\b");
        synonym("3", "DREI\\b", "THREE\\b", "\\bIII\\b");
        synonym("2", "ZWEI\\b", "TWO\\b", "\\bII\\b");
        synonym("1", "EINS\\b", "ONE\\b");
    }

    private static void synonym(String value, String... synonyms)
    {
        SYNONYMS.put(value, synonyms);
    }

    public static String simplify(String name)
    {
        name = name.toUpperCase();

        for (Map.Entry<String, String[]> entry : SYNONYMS.entrySet())
        {
            for (String synonym : entry.getValue())
            {
                name = name.replaceAll(synonym, entry.getKey());
            }
        }

        name = name.replaceAll("[^\\p{L}\\p{Nd}]+", "");

        return name;
    }

    public static double computeMatchFactor(String left, String right)
    {
        double result = 1;

        if (left.equalsIgnoreCase(right))
        {
            return result;
        }

        int length = Math.min(left.length(), right.length());

        do
        {
            result *= 0.9;

            left = left.substring(0, length);
            right = right.substring(0, length);

            if (left.equalsIgnoreCase(right))
            {
                return result;
            }

            length -= 1;
        } while (length > 1);

        return 0;
    }
}
