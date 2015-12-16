package io.github.thred.piconup.image;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import io.github.thred.piconup.PiconUpTarget;

public interface ImageIndexEntry
{

    public String getFilename();

    public String getName();

    public String getSimplifiedName();

    public double estimateScale(double expectedCoverage) throws IOException;

    public void write(PiconUpTarget target, File file, Color background, double scale, double border, double transparency)
        throws IOException;

    public void write(PiconUpTarget target, OutputStream out, Color background, double scale, double border, double transparency)
        throws IOException;

    public void cleanup();

}
