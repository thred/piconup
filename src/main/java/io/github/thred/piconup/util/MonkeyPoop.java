package io.github.thred.piconup.util;

/**
 * Because it is much more fun to throw monkey poop!
 */
public class MonkeyPoop extends RuntimeException
{

    private static final long serialVersionUID = 757793807132316489L;

    public MonkeyPoop(String message, Throwable cause, Object... args)
    {
        super(String.format(message, args), cause);
    }

    public MonkeyPoop(String message, Object... args)
    {
        super(String.format(message, args));
    }

}
