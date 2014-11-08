package io.github.thred.piconup.image;

import io.github.thred.piconup.util.PiconUpUtil;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public abstract class AbstractImageIndexEntry implements ImageIndexEntry
{

    private final String filename;
    private final String name;
    private final String simplifiedName;

    private BufferedImage originalImage;
    private BufferedImage xPicon;
    private BufferedImage picon;

    public AbstractImageIndexEntry(String filename)
    {
        super();

        this.filename = filename;

        int index = filename.lastIndexOf(".");

        if (index >= 0)
        {
            filename = filename.substring(0, index);
        }

        name = filename;
        simplifiedName = PiconUpUtil.simplify(name);
    }

    @Override
    public String getFilename()
    {
        return filename;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getSimplifiedName()
    {
        return simplifiedName;
    }

    protected abstract InputStream openStream() throws IOException;

    public BufferedImage getOriginalImage() throws IOException
    {
        if (originalImage != null)
        {
            return originalImage;
        }

        try (InputStream openStream = openStream())
        {
            originalImage = ImageIO.read(openStream);
        }

        return originalImage;
    }

    @Override
    public void cleanup()
    {
        originalImage = null;
    }

    @Override
    public void writeXPicon(File file) throws IOException
    {
        ImageIO.write(getXPicon(), "png", file);
    }

    @Override
    public void writePicon(File file) throws IOException
    {
        ImageIO.write(getPicon(), "png", file);
    }

    protected BufferedImage getXPicon() throws IOException
    {
        if (xPicon != null)
        {
            return xPicon;
        }

        return xPicon = createImage(220, 132);
    }

    protected BufferedImage getPicon() throws IOException
    {
        if (picon != null)
        {
            return picon;
        }

        return picon = createImage(100, 60);
    }

    protected BufferedImage createImage(int width, int height) throws IOException
    {
        BufferedImage originalImage = getOriginalImage();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g.translate(width / 2, height / 2);

        double scale = Math.min((double) width / originalImage.getWidth(), (double) height / originalImage.getHeight());

        g.scale(scale, scale);
        g.translate(-originalImage.getWidth() / 2, -originalImage.getHeight() / 2);
        g.drawImage(originalImage, 0, 0, null);

        return image;
    }
}
