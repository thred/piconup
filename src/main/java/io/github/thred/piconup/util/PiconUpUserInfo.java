package io.github.thred.piconup.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.jcraft.jsch.UserInfo;

public class PiconUpUserInfo implements UserInfo
{

    @Override
    public String getPassphrase()
    {
        System.out.print("Passphrase: ");
        
        return readLine();
    }

    @Override
    public String getPassword()
    {
        System.out.print("Password: ");
        
        return readLine();
    }

    @Override
    public boolean promptPassphrase(String s)
    {
        System.out.print(s);
        
        return true;
    }

    @Override
    public boolean promptPassword(String s)
    {
        System.out.print(s);
        
        return true;
    }

    @Override
    public boolean promptYesNo(String s)
    {
        System.out.print(s);
        System.out.print(" (yes/no) ");

        try
        {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)))
            {
                String line;

                while ((line = reader.readLine()) != null)
                {
                    line = line.trim();

                    if ("yes".equals(line))
                    {
                        return true;
                    }

                    if ("no".equals(line))
                    {
                        return false;
                    }

                    System.out.print("(yes/no)? ");
                }

                return false;
            }
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Failed to consume", e);
        }
    }

    @Override
    public void showMessage(String s)
    {
        System.out.println(s);
    }

    protected String readLine()
    {
        try
        {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)))
            {
                return reader.readLine();
            }
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Failed to consume", e);
        }
    }
}

