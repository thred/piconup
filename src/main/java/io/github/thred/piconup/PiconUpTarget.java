package io.github.thred.piconup;

import java.awt.Dimension;

public enum PiconUpTarget
{

    DEFAULT("picon/", new Dimension(220, 132)),
    SMALL("picon_50x30/", new Dimension(50, 30)),
    MEDIUM("picon_100x60/", new Dimension(100, 60)),
    LARGE("picon_220x132/", new Dimension(220, 132)),
    MYMETRIX("XPicons/picon/", new Dimension(220, 132));

    private final String path;
    private final Dimension dimension;

    private PiconUpTarget(String path, Dimension dimension)
    {
        this.path = path;
        this.dimension = dimension;
    }

    public String getPath()
    {
        return path;
    }

    public Dimension getDimension()
    {
        return dimension;
    }

    public int getWidth()
    {
        return dimension.width;
    }

    public int getHeight()
    {
        return dimension.height;
    }

}
