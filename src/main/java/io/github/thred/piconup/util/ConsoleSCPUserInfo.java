package io.github.thred.piconup.util;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

import com.jcraft.jsch.UserInfo;

public class ConsoleSCPUserInfo implements UserInfo
{

    private final Console console;

    private boolean quiet = false;
    private boolean yes = false;
    private String passphrase;
    private String password;

    public ConsoleSCPUserInfo()
    {
        this(System.console());
    }

    public ConsoleSCPUserInfo(Console console)
    {
        super();

        this.console = console;
    }

    public boolean isQuiet()
    {
        return quiet;
    }

    public void setQuiet(boolean quiet)
    {
        this.quiet = quiet;
    }

    public boolean isYes()
    {
        return yes;
    }

    public void setYes(boolean yes)
    {
        this.yes = yes;
    }

    public void setPassphrase(String passphrase)
    {
        this.passphrase = passphrase;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    @Override
    public String getPassphrase()
    {
        return (passphrase != null) ? passphrase : readPassword("");
    }

    @Override
    public String getPassword()
    {
        return (password != null) ? password : readPassword("");
    }

    @Override
    public boolean promptPassphrase(String s)
    {
        if (passphrase != null)
        {
            return true;
        }

        write("%s: ", s);

        return true;
    }

    @Override
    public boolean promptPassword(String s)
    {
        if (password != null)
        {
            return true;
        }

        write("%s: ", s);

        return true;
    }

    @Override
    public boolean promptYesNo(String s)
    {
        if (yes)
        {
            return true;
        }

        String line;

        while ((line = readLine("%s (y/n) ", s)) != null)
        {
            line = line.trim();

            if ("y".equals(line))
            {
                return true;
            }

            if ("n".equals(line))
            {
                return false;
            }
        }

        return false;
    }

    @Override
    public void showMessage(String s)
    {
        write("%s\n", s);
    }

    protected String readLine(String fmt, Object... args)
    {
        if (console != null)
        {
            return console.readLine(fmt, args);
        }

        System.out.printf(fmt, args);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try
        {
            return reader.readLine();
        }
        catch (IOException e)
        {
            return null;
        }
    }

    protected String readPassword(String fmt, Object... args)
    {
        if (console != null)
        {
            return String.valueOf(console.readPassword(fmt, args));
        }

        return readLine(fmt, args);
    }

    protected void write(String fmt, Object... args)
    {
        if (quiet)
        {
            return;
        }

        if (console != null)
        {
            console.format(fmt, args);

            return;
        }

        System.out.printf(fmt, args);
    }
}