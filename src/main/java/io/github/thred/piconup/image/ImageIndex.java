package io.github.thred.piconup.image;

import io.github.thred.piconup.util.PiconUpUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ImageIndex
{

    private static final String[] PREDEFINED_NAMES = {"3sat.png", "anixe.png", "arte.png", "atv2.png", "atv.png",
        "br.png", "das_erste.png", "disney_channel.png", "disney_junior.png", "dmax.png", "eins_festival.png", "einsplus.png", "hr.png",
        "kabel_1.png", "kika.png", "mdr.png", "ndr.png", "orf_1.png", "orf_2.png", "orf_3.png", "orf_sport.png",
        "phoenix.png", "pro7maxx.png", "pro7.png", "puls4.png", "rbb.png", "rtl2.png", "rtlnitro.png", "rtl.png",
        "sat1gold.png", "sat1.png", "servustv.png", "sixx.png", "srf1.png", "srf2.png", "super_rtl.png", "swr.png",
        "tele5.png", "viva.png", "vox.png", "wdr.png", "zdf_info.png", "zdf_kultur.png", "zdf_neo.png", "zdf.png"};

    public static ImageIndex createPredefined()
    {
        List<ImageIndexEntry> entries = new ArrayList<>();

        for (String name : PREDEFINED_NAMES)
        {
            URL url = ImageIndex.class.getResource(name);

            if (url == null)
            {
                throw new IllegalArgumentException("Invalid predefined image name: " + name);
            }

            entries.add(new ImageIndexResourceEntry(url));
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

        return entries
            .stream()
            .max(
                Comparator.comparingDouble((entry) -> PiconUpUtil.computeMatchFactor(entry.getSimplifiedName(),
                    simplifiedName))).get();
    }
}
