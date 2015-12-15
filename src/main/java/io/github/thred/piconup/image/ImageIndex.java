package io.github.thred.piconup.image;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import io.github.thred.piconup.util.PiconUpUtil;

public class ImageIndex
{

    private static final String[] PREDEFINED_NAMES = {"13th_street.png", "1tvrus.png", "3sat.png", "al_jazeera.png",
        "alpendorf_tv.png", "anixe.png", "ard_alpha.png", "arte.png", "astro_tv.png", "atv2.png", "atv_avrupa.png",
        "atv.png", "axn.png", "bbc_entertainment.png", "bbc_world.png", "beate_uhse_tv.png", "bibel_tv.png",
        "bloomberg_tv.png", "blue_movie.png", "bn_music.png", "bn_sat.png", "br.png", "bvn.png", "cnn_int.png",
        "das_erste.png", "deluxe_music.png", "discovery_channel.png", "disney_channel.png", "disney_cinematic.png",
        "disney_junior.png", "disney_xd.png", "dmax.png", "dm_sat.png", "duna_tv.png", "dw_tv.png", "eins_festival.png",
        "einsplus.png", "euro_d.png", "euronews.png", "eurosport.png", "eurostar.png", "ewtn.png", "fashionone.png",
        "fox.png", "fox_series.png", "frace_24.png", "fs1salzburg.png", "goldstar_tv.png", "gotv.png",
        "heimatkanal.png", "history.png", "hitradio_oe3.png", "hr.png", "hse24.png", "kabel_1.png", "kika.png",
        "mdr.png", "ndr.png", "orf_1.png", "orf_2.png", "orf_3.png", "orf_sport.png", "phoenix.png", "pro7maxx.png",
        "pro7.png", "puls4.png", "rbb.png", "rtl2.png", "rtlnitro.png", "rtl.png", "sat1gold.png", "sat1.png",
        "servustv.png", "sixx.png", "srf1.png", "srf2.png", "super_rtl.png", "swr.png", "tele5.png", "viva.png",
        "vox.png", "wdr.png", "zdf_info.png", "zdf_kultur.png", "zdf_neo.png", "zdf.png"};

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
