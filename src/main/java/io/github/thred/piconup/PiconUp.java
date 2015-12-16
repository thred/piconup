package io.github.thred.piconup;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import io.github.thred.piconup.image.ImageIndexEntry;
import io.github.thred.piconup.openwebif.Service;
import io.github.thred.piconup.util.SCP;

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

            if ((options.getDir() != null) || (options.isSsh()) || (options.getZip() != null))
            {
                if (options.isSsh())
                {
                    state.openSCP();
                }

                try
                {
                    write(state);
                }
                finally
                {
                    state.closeSCP();
                }
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

        System.out.printf("Processing \"%s\" ...\n", service.getName());

        PiconUpOptions options = state.getOptions();

        double scale = 1;

        if (options.getOptimize() != null)
        {
            try
            {
                scale = entry.estimateScale(options.getOptimize());
            }
            catch (IOException e)
            {
                e.printStackTrace(System.err);
            }
        }

        if (options.getDir() != null)
        {
            try
            {
                writeFile(state, service, entry, scale, options.getDir());
            }
            catch (PiconUpException e)
            {
                e.printStackTrace(System.err);
            }
        }

        if (options.isSsh())
        {
            try
            {
                writeSsh(state, service, entry, scale, state.getSCP());
            }
            catch (PiconUpException e)
            {
                e.printStackTrace(System.err);
            }
        }

        entry.cleanup();
    }

    private static void writeFile(PiconUpState state, Service service, ImageIndexEntry entry, double scale, File dir)
        throws PiconUpException
    {
        PiconUpOptions options = state.getOptions();
        String filename = service.getTargetFilename();

        for (PiconUpTarget target : PiconUpTarget.values())
        {
            File file = new File(dir, target.getPath() + filename);

            System.out.printf("  Writing %s ...\n", file);

            file.getParentFile().mkdirs();

            try
            {
                entry.write(target, file, options.getBackground(), scale, options.getBorder(),
                    options.getTransparent());
            }
            catch (IOException e)
            {
                throw new PiconUpException("Failed to write " + file, e);
            }
        }
    }

    private static void writeSsh(PiconUpState state, Service service, ImageIndexEntry entry, double scale, SCP scp)
        throws PiconUpException
    {
        PiconUpOptions options = state.getOptions();
        String filename = service.getTargetFilename();

        for (PiconUpTarget target : PiconUpTarget.values())
        {
            String targetFile = target.getPath() + filename;
            String targetPath = scp.getBasePath() + "/" + targetFile;

            System.out.printf("  Sending %s ...\n", targetPath);

            try
            {
                byte[] bytes;

                try (ByteArrayOutputStream out = new ByteArrayOutputStream())
                {
                    entry.write(target, out, options.getBackground(), scale, options.getBorder(),
                        options.getTransparent());

                    bytes = out.toByteArray();
                }

                scp.write(targetFile, bytes);
            }
            catch (IOException e)
            {
                throw new PiconUpException("Failed to send " + targetPath, e);
            }
        }
    }
}
