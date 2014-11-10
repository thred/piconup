package io.github.thred.piconup.image;

import io.github.thred.piconup.PiconUpTarget;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public interface ImageIndexEntry
{

    public String getFilename();

    public String getName();

    public String getSimplifiedName();

    public double estimateCoverage() throws IOException;

    public void write(PiconUpTarget target, File file, double scale) throws IOException;

    public void write(PiconUpTarget target, OutputStream out, double scale) throws IOException;

    public void cleanup();

}
