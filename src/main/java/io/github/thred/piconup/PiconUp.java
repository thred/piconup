package io.github.thred.piconup;

import io.github.thred.piconup.image.ImageIndexEntry;
import io.github.thred.piconup.openwebif.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PiconUp
{

    public static void main(String... args)
    {
        PiconUpOptions options = PiconUpOptions.create(args);
        PiconUpState state = new PiconUpState(options);

        try
        {
            if (options.isList())
            {
                list(state);
            }

            if ((options.getDir() != null) || (options.isSsl()) || (options.getZip() != null))
            {
                write(state);
            }
        }
        catch (PiconUpException e)
        {
            e.printStackTrace();
            System.exit(1);
        }

    }

    private static void list(PiconUpState state) throws PiconUpException
    {
        List<Service> services = state.getServices();

        System.out.println();
        System.out.println("Service List");
        System.out.println("============");
        System.out.println();

        services.stream().sorted().forEach(System.out::println);
    }

    private static void write(PiconUpState state) throws PiconUpException
    {
        List<Service> services = state.getServices();

        services.stream().sorted().forEach((service) -> write(state, service));
    }

    private static void write(PiconUpState state, Service service)
    {
        ImageIndexEntry entry = service.getImageIndexEntry();

        if (entry == null)
        {
            System.out.println("No matching image for " + service.getName());
            return;
        }

        System.out.println("Processing " + service.getName());

        PiconUpOptions options = state.getOptions();

        if (options.getDir() != null)
        {
            try
            {
                write(state, service, entry, options.getDir());
            }
            catch (PiconUpException e)
            {
                e.printStackTrace();
            }
        }

        entry.cleanup();
    }

    private static void write(PiconUpState state, Service service, ImageIndexEntry entry, File dir)
        throws PiconUpException
    {
        String filename = service.getTargetFilename();

        for (PiconUpTarget target : PiconUpTarget.values())
        {
            File file = new File(dir, target.getPath() + filename);

            System.out.printf("  Writing %s ...\n", file);
            
            file.getParentFile().mkdirs();

            try
            {
                entry.write(target, file);
            }
            catch (IOException e)
            {
                throw new PiconUpException("Failed to write " + file, e);
            }
        }
    }
}
