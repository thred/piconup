package io.github.thred.piconup.image;

import io.github.thred.piconup.PiconUpTarget;

import java.io.File;
import java.io.IOException;

public interface ImageIndexEntry
{

    public String getFilename();

    public String getName();

    public String getSimplifiedName();

    public void write(PiconUpTarget target, File file) throws IOException;

    public void cleanup();

}
