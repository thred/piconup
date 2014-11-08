package io.github.thred.piconup.image;

import java.io.File;
import java.io.IOException;

public interface ImageIndexEntry
{

    public String getFilename();

    public String getName();

    public String getSimplifiedName();

    public void writeXPicon(File file) throws IOException;

    public void writePicon(File file) throws IOException;

    public void cleanup();

}
