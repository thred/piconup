package io.github.thred.piconup.image;

import io.github.thred.piconup.util.PiconUpUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ImageIndex
{

    private static final String[] PREDEFINED_NAMES = {"13th_street.png", "1tvrus.png", "3sat.png", "al_jazeera.png",
        "alpendorf_tv.png", "anixe.png", "ard_alpha.png", "arte.png", "astro_tv.png", "atv.png", "atv2.png",
        "atv_avrupa.png", "axn.png", "bbc_entertainment.png", "bbc_world.png", "beate_uhse_tv.png", "bibel_tv.png",
        "bloomberg_tv.png", "blue_movie.png", "bn_music.png", "bn_sat.png", "br.png", "bvn.png", "cnn_int.png",
        "das_erste.png", "deluxe_music.png", "discovery_channel.png", "disney_channel.png", "disney_cinematic.png",
        "disney_junior.png", "disney_xd.png", "dm_sat.png", "dmax.png", "eins_festival.png", "einsplus.png", "hr.png",
        "kabel_1.png", "kika.png", "mdr.png", "ndr.png", "orf_1.png", "orf_2.png", "orf_3.png", "orf_sport.png",
        "phoenix.png", "pro7.png", "pro7maxx.png", "puls4.png", "rbb.png", "rtl.png", "rtl2.png", "rtlnitro.png",
        "sat1.png", "sat1gold.png", "servustv.png", "sixx.png", "srf1.png", "srf2.png", "super_rtl.png", "swr.png",
        "tele5.png", "viva.png", "vox.png", "wdr.png", "zdf.png", "zdf_info.png", "zdf_kultur.png", "zdf_neo.png"};

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

        ImageIndexEntry result =
            entries
            .stream()
            .max(
                Comparator.comparingDouble((entry) -> PiconUpUtil.computeMatchFactor(entry.getSimplifiedName(),
                    simplifiedName))).get();

        if ((result != null) && (PiconUpUtil.computeMatchFactor(result.getSimplifiedName(), simplifiedName) < 0.333))
        {
            result = null;
        }

        return result;
    }
}
