package io.github.thred.piconup;

public class PiconUpException extends Exception
{

    /**
     *
     */
    private static final long serialVersionUID = 2578243598551102280L;

    public PiconUpException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public PiconUpException(String message)
    {
        super(message);
    }

}
