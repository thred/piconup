package io.github.thred.piconup.image;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ImageIndexResourceEntry extends AbstractImageIndexEntry
{

    private final URL url;

    public ImageIndexResourceEntry(URL url)
    {
        super(extractFile(url));

        this.url = url;
    }

    @Override
    protected InputStream openStream() throws IOException
    {
        return url.openStream();
    }

    private static String extractFile(URL url)
    {
        String path = url.getPath();
        int index = path.lastIndexOf("/");

        if (index >= 0)
        {
            path = path.substring(index + 1);
        }

        return path;
    }

}
