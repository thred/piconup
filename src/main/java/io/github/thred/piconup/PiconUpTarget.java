package io.github.thred.piconup;

public enum PiconUpTarget
{

    DEFAULT("picon/", 100, 60),
    SMALL("picon_50x30/", 50, 30),
    MEDIUM("picon_100x60/", 100, 60),
    LARGE("picon_220x132/", 220, 132),
    MYMETRIX("XPicons/picon/", 220, 132);

    private final String path;
    private final int width;
    private final int height;

    private PiconUpTarget(String path, int width, int height)
    {
        this.path = path;
        this.width = width;
        this.height = height;
    }

    public String getPath()
    {
        return path;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

}
