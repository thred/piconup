package io.github.thred.piconup.image;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import io.github.thred.piconup.util.MonkeyPoop;
import io.github.thred.piconup.util.PiconUpUtil;

public class ImageIndex
{

    private static final String INDEX_FILE_NAME = ".index";

    public static ImageIndex createPredefined()
    {
        List<ImageIndexEntry> entries = new ArrayList<>();

        try (BufferedReader reader =
            new BufferedReader(new InputStreamReader(ImageIndex.class.getResourceAsStream(INDEX_FILE_NAME))))
        {
            while (true)
            {
                String name = reader.readLine();

                if (name == null)
                {
                    break;
                }

                if (INDEX_FILE_NAME.equals(name))
                {
                    continue;
                }

                URL url = ImageIndex.class.getResource(name);

                if (url == null)
                {
                    throw new MonkeyPoop("Invalid predefined image name: %s", name);
                }

                entries.add(new ImageIndexResourceEntry(url));
            }
        }
        catch (IOException e)
        {
            throw new MonkeyPoop("Failed to read %s file from resources", e, INDEX_FILE_NAME);
        }

        return new ImageIndex(entries);
    }

    private final List<ImageIndexEntry> entries;

    private ImageIndex(List<ImageIndexEntry> entries)
    {
        super();

        this.entries = entries;
    }

    public ImageIndexEntry find(String name)
    {
        String simplifiedName = PiconUpUtil.simplify(name);

        ImageIndexEntry result = entries.stream()
            .max(Comparator
                .comparingDouble((entry) -> PiconUpUtil.computeMatchFactor(entry.getSimplifiedName(), simplifiedName)))
            .get();

        if ((result != null) && (PiconUpUtil.computeMatchFactor(result.getSimplifiedName(), simplifiedName) < 0.333))
        {
            result = null;
        }

        return result;
    }
}
