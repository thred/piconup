package io.github.thred.piconup;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class PiconUpOptions
{

    private static final String ARG_HOST = "host";
    private static final String ARG_OPEN_WEB_IF_URL = "openWebIFURL";
    private static final String ARG_USER = "user";
    private static final String ARG_PASSWORD = "password";
    private static final String ARG_IMAGES = "images";
    private static final String ARG_RECURSIVE = "recursive";
    private static final String ARG_LIST = "list";
    private static final String ARG_DIR = "dir";
    private static final String ARG_SSH = "ssh";
    private static final String ARG_ZIP = "zip";
    private static final String ARG_HELP = "help";

    public static PiconUpOptions create(String... args)
    {
        Options options = new Options();

        options.addOption("h", ARG_HOST, true,
            "The hostname or IP address of STB (used for both, the OpenWebIF and the SSH connection).");
        options.addOption(null, ARG_OPEN_WEB_IF_URL, true,
            "The URL to the OpenWebIF. Uses \"http://<host>\" if not specified.");
        options.addOption("u", ARG_USER, true, "The username for login, default is \"root\".");
        options.addOption("p", ARG_PASSWORD, true, "The password for login, default is \"\".");
        options.addOption("i", ARG_IMAGES, true, "The source directory containing all images, default is \".\".");
        options.addOption("r", ARG_RECURSIVE, false, "Search for images in subfolders.");
        options.addOption("l", ARG_LIST, false, "Lists all known services collected from the STB.");
        options.addOption("d", ARG_DIR, true, "Write the images to the specified path.");
        options.addOption(null, ARG_SSH, false, "Use an SSH connection and update all images on the STB.");
        options.addOption(null, ARG_ZIP, true, "Write the images into a ZIP file.");
        options.addOption("?", ARG_HELP, false, "Shows this help.");

        CommandLineParser parser = new BasicParser();
        CommandLine cmd = null;

        try
        {
            cmd = parser.parse(options, args);
        }
        catch (ParseException e)
        {
            System.err.println("Inavlid options: " + e.getMessage());

            printHelp(options);
            System.exit(1);
        }

        if (cmd.hasOption(ARG_HELP))
        {
            printHelp(options);
            System.exit(0);
        }

        PiconUpOptions result = new PiconUpOptions();

        if (cmd.hasOption(ARG_HOST))
        {
            result.setHost(cmd.getOptionValue(ARG_HOST));
        }

        if (cmd.hasOption(ARG_OPEN_WEB_IF_URL))
        {
            try
            {
                result.setOpenWebIFURL(new URL(cmd.getOptionValue(ARG_OPEN_WEB_IF_URL)));
            }
            catch (MalformedURLException e)
            {
                System.err.println("Invalid " + ARG_OPEN_WEB_IF_URL + ": " + e.getMessage());
                System.exit(1);
            }
        }

        if (cmd.hasOption(ARG_USER))
        {
            result.setUser(cmd.getOptionValue(ARG_USER));
        }

        if (cmd.hasOption(ARG_PASSWORD))
        {
            result.setUser(cmd.getOptionValue(ARG_PASSWORD));
        }

        if (cmd.hasOption(ARG_PASSWORD))
        {
            result.setUser(cmd.getOptionValue(ARG_PASSWORD));
        }

        if (cmd.hasOption(ARG_IMAGES))
        {
            File imagePath = new File(cmd.getOptionValue(ARG_IMAGES));

            if (!imagePath.exists())
            {
                System.err.println("Invalid image path: " + imagePath.getAbsolutePath());
                System.exit(1);
            }

            if (!imagePath.isDirectory())
            {
                System.err.println("Image path must be a directory: " + imagePath.getAbsolutePath());
                System.exit(1);
            }

            result.setImagePath(imagePath);
        }

        result.setRecursive(cmd.hasOption(ARG_RECURSIVE));

        result.setList(cmd.hasOption(ARG_LIST));

        if (cmd.hasOption(ARG_DIR))
        {
            File dir = new File(cmd.getOptionValue(ARG_DIR));

            if ((!dir.mkdirs()) && (!dir.canWrite()))
            {
                System.err.println("Cannot write to path: " + dir.getAbsolutePath());
                System.exit(1);
            }

            result.setDir(dir);
        }

        result.setSsh(cmd.hasOption(ARG_SSH));

        if (cmd.hasOption(ARG_ZIP))
        {
            File zip = new File(cmd.getOptionValue(ARG_ZIP));

            if (!zip.canWrite())
            {
                System.err.println("Cannot write to ZIP file: " + zip.getAbsolutePath());
                System.exit(1);
            }

            result.setZip(zip);
        }

        List<String> patterns = new ArrayList<>();

        for (String arg : cmd.getArgs())
        {
            patterns.add(arg.toUpperCase());
        }

        result.setPatterns(patterns.toArray(new String[patterns.size()]));

        return result;
    }

    private String host;
    private URL openWebIFURL;
    private String user = "root";
    private String password = "";
    private File imagePath = new File(".");
    private boolean recursive = false;
    private boolean list = false;
    private File dir;
    private boolean ssh = false;
    private File zip;
    private String[] patterns;

    public PiconUpOptions()
    {
        super();
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public URL getOpenWebIFURL()
    {
        if (openWebIFURL == null)
        {
            String url = "http://" + host;

            try
            {
                return new URL(url);
            }
            catch (MalformedURLException e)
            {
                System.err.println("Invalid " + ARG_OPEN_WEB_IF_URL + ": " + e.getMessage());
            }
        }

        return openWebIFURL;
    }

    public boolean isOpenWebIFURL()
    {
        return (host != null) || (openWebIFURL != null);
    }

    public void setOpenWebIFURL(URL openWebIFURL)
    {
        this.openWebIFURL = openWebIFURL;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public File getImagePath()
    {
        return imagePath;
    }

    public void setImagePath(File imagePath)
    {
        this.imagePath = imagePath;
    }

    public boolean isRecursive()
    {
        return recursive;
    }

    public void setRecursive(boolean recursive)
    {
        this.recursive = recursive;
    }

    public boolean isList()
    {
        return list;
    }

    public void setList(boolean list)
    {
        this.list = list;
    }

    public File getDir()
    {
        return dir;
    }

    public void setDir(File dir)
    {
        this.dir = dir;
    }

    public boolean isSsh()
    {
        return ssh;
    }

    public void setSsh(boolean ssh)
    {
        this.ssh = ssh;
    }

    public File getZip()
    {
        return zip;
    }

    public void setZip(File zip)
    {
        this.zip = zip;
    }

    public String[] getPatterns()
    {
        return patterns;
    }

    public void setPatterns(String[] patterns)
    {
        this.patterns = patterns;
    }

    public static void printHelp(Options options)
    {
        System.out.println("PiconUp");
        System.out.println("=======");
        System.out.println();
        System.out.println("Create and update picons for your STB.");
        System.out.println();
        System.out.println("PiconUp connects to your STB and grabs the names of all defined channels.");
        System.out.println("Using this name, it uses fully logic to search for images in the specified");
        System.out.println("directory. If it findes an image, it scales it accoringly and sends it to");
        System.out.println("the STB or puts it into an archive.");
        System.out.println();

        HelpFormatter formatter = new HelpFormatter();

        formatter.printHelp("java -jar piconUp.jar", options);
    }

}
