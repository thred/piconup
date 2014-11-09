package io.github.thred.piconup.util;

import java.io.IOException;
import java.io.InputStream;
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

    public static int checkAck(InputStream in) throws IOException
    {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1

        if (b == 0)
        {
            return b;
        }

        if (b == -1)
        {
            return b;
        }

        if ((b == 1) || (b == 2))
        {
            StringBuffer sb = new StringBuffer();
            int c;
            do
            {
                c = in.read();
                sb.append((char) c);
            } while (c != '\n');

            if (b == 1)
            { // error
                System.out.print(sb.toString());
            }

            if (b == 2)
            { // fatal error
                System.out.print(sb.toString());
            }
        }

        return b;
    }

}
