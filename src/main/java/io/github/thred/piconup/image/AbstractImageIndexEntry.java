package io.github.thred.piconup.image;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import io.github.thred.piconup.PiconUpTarget;
import io.github.thred.piconup.util.PiconUpUtil;

public abstract class AbstractImageIndexEntry implements ImageIndexEntry
{

    private static class Key
    {
        private final int width;
        private final int height;
        private final double scale;
        private final double border;
        private final double transparency;

        public Key(int width, int height, double scale, double border, double transparency)
        {
            super();
            this.width = width;
            this.height = height;
            this.scale = scale;
            this.border = border;
            this.transparency = transparency;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            long temp;

            temp = Double.doubleToLongBits(border);
            result = (prime * result) + (int) (temp ^ (temp >>> 32));
            result = (prime * result) + height;
            temp = Double.doubleToLongBits(scale);
            result = (prime * result) + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(transparency);
            result = (prime * result) + (int) (temp ^ (temp >>> 32));
            result = (prime * result) + width;

            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }

            if (obj == null)
            {
                return false;
            }

            if (getClass() != obj.getClass())
            {
                return false;
            }

            Key other = (Key) obj;

            if (Double.doubleToLongBits(border) != Double.doubleToLongBits(other.border))
            {
                return false;
            }

            if (height != other.height)
            {
                return false;
            }

            if (Double.doubleToLongBits(scale) != Double.doubleToLongBits(other.scale))
            {
                return false;
            }

            if (Double.doubleToLongBits(transparency) != Double.doubleToLongBits(other.transparency))
            {
                return false;
            }

            if (width != other.width)
            {
                return false;
            }

            return true;
        }

    }

    private final Map<Key, BufferedImage> cachedImages = new HashMap<>();

    private final String filename;
    private final String name;
    private final String simplifiedName;

    private BufferedImage originalImage;

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
        cachedImages.clear();
    }

    @Override
    public void write(PiconUpTarget target, File file, double scale, double border, double transparency)
        throws IOException
    {
        ImageIO.write(get(target, scale, border, transparency, true), "png", file);
    }

    @Override
    public void write(PiconUpTarget target, OutputStream out, double scale, double border, double transparency)
        throws IOException
    {
        ImageIO.write(get(target, scale, border, transparency, true), "png", out);
    }

    @Override
    public double estimateScale(double expectedCoverage) throws IOException
    {
        double scale = 1;
        double coverage = estimateCoverage(scale);
        double initial = coverage;

        if (coverage > expectedCoverage)
        {
            double max = 1;
            double min = 0;

            while (Math.abs(coverage - expectedCoverage) > 0.01)
            {
                if (coverage > expectedCoverage)
                {
                    max = scale;
                    scale = (scale + min) * 0.5;
                }
                else
                {
                    min = scale;
                    scale = (scale + max) * 0.5;
                }

                coverage = estimateCoverage(scale);
            }
        }

        System.out.printf("  Coverage (target / initial / estimated): %3.1f %% / %3.1f %% / %3.1f %%\n",
            expectedCoverage * 100, initial * 100, coverage * 100);

        return scale;
    }

    protected double estimateCoverage(double scale) throws IOException
    {
        BufferedImage image = get(100, 60, scale, 0, 0, false);

        fillUp(image);

        int width = image.getWidth();
        int height = image.getHeight();
        int covered = 0;

        for (int y = 0; y < height; y += 1)
        {
            for (int x = 0; x < width; x += 1)
            {
                if (((image.getRGB(x, y) >> 24) & 0xff) >= 128)
                {
                    covered += 1;
                }
            }
        }

        return (double) covered / (width * height);
    }

    protected BufferedImage get(PiconUpTarget target, double scale, double border, double transparency,
        boolean useCache) throws IOException
    {
        return get(target.getWidth(), target.getHeight(), scale, border, transparency, useCache);
    }

    private BufferedImage get(int width, int height, double scale, double border, double transparency, boolean useCache)
        throws IOException
    {
        Key key = null;
        BufferedImage result = null;

        if (useCache)
        {
            key = new Key(width, height, scale, border, transparency);
            result = cachedImages.get(key);

            if (result != null)
            {
                return result;
            }
        }

        result = createImage(width, height, scale, border, transparency);

        if (useCache)
        {
            cachedImages.put(key, result);
        }

        return result;
    }

    BufferedImage createImage(int width, int height, double scale, double border, double transparency)
        throws IOException
    {
        BufferedImage originalImage = getOriginalImage();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g.translate(width / 2, height / 2);

        double finalScale = Math.min((width * (1 - border)) / originalImage.getWidth(),
            (height * (1 - border)) / originalImage.getHeight()) * scale;

        g.scale(finalScale, finalScale);
        g.translate(-originalImage.getWidth() / 2, -originalImage.getHeight() / 2);

        if (transparency > 0)
        {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (1 - transparency)));
        }

        g.drawImage(originalImage, 0, 0, null);

        return image;
    }

    private static BufferedImage fillUp(BufferedImage image)
    {
        int width = image.getWidth();
        int height = image.getHeight();

        int[] minX = new int[height];
        int[] maxX = new int[height];
        int[] minY = new int[width];
        int[] maxY = new int[width];

        Arrays.fill(minX, Integer.MAX_VALUE);
        Arrays.fill(maxX, Integer.MIN_VALUE);
        Arrays.fill(minY, Integer.MAX_VALUE);
        Arrays.fill(maxY, Integer.MIN_VALUE);

        for (int y = 0; y < height; y += 1)
        {
            for (int x = 0; x < width; x += 1)
            {
                if (((image.getRGB(x, y) >> 24) & 0xff) >= 128)
                {
                    minX[y] = Math.min(minX[y], x);
                    maxX[y] = Math.max(maxX[y], x);
                    minY[x] = Math.min(minY[x], y);
                    maxY[x] = Math.max(maxY[x], y);
                }
            }
        }

        for (int y = 0; y < height; y += 1)
        {
            for (int x = 0; x < width; x += 1)
            {
                if (((x > minX[y]) && (x < maxX[y])) || ((y > minY[x]) && (y < maxY[x])))
                {
                    image.setRGB(x, y, 0xff000000);
                }
            }
        }

        return image;
    }

}
