package io.github.thred.piconup;

import io.github.thred.piconup.image.ImageIndexEntry;
import io.github.thred.piconup.openwebif.Service;
import io.github.thred.piconup.util.PiconUpUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

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
                    state.openSession();
                }

                try
                {
                    write(state);
                }
                finally
                {
                    state.closeSession();
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

        System.out.println("Processing " + service.getName());

        PiconUpOptions options = state.getOptions();

        if (options.getDir() != null)
        {
            try
            {
                writeFile(state, service, entry, options.getDir());
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
                writeSsh(state, service, entry, state.getSession());
            }
            catch (PiconUpException e)
            {
                e.printStackTrace(System.err);
            }
        }

        entry.cleanup();
    }

    private static void writeFile(PiconUpState state, Service service, ImageIndexEntry entry, File dir)
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

    private static void writeSsh(PiconUpState state, Service service, ImageIndexEntry entry, Session session)
        throws PiconUpException
    {
        String filename = service.getTargetFilename();

        for (PiconUpTarget target : PiconUpTarget.values())
        {
            String targetFile = "/usr/share/enigma2/" + target.getPath() + filename;

            System.out.printf("  Sending %s ...\n", targetFile);

            try
            {
                byte[] bytes;

                try (ByteArrayOutputStream out = new ByteArrayOutputStream())
                {
                    entry.write(target, out);

                    bytes = out.toByteArray();
                }
                
                

                // create the directory
//                out.write(String.format("D0755 0 %s\n", "        
//                command += directory.getDirectory().getName();
//                command += "\n";
//
//                if (this.getVerbose()) {
//                    logger.info("scp command is " + command);
//                }
//                
//                out.write(command.getBytes());
//                out.flush();
//
//                waitForAck(in);
                

                
                
                // exec 'scp -t rfile' remotely
                String command = "scp -r -d -t " + targetFile;
                Channel channel = session.openChannel("exec");
                ((ChannelExec) channel).setCommand(command);

                channel.connect();

                try
                {
                    // get I/O streams for remote scp
                    try (OutputStream out = channel.getOutputStream())
                    {
                        InputStream in = channel.getInputStream();

                        if (PiconUpUtil.checkAck(in) != 0)
                        {
                            throw new PiconUpException("Failed to connect");
                        }

                        // send "C0644 filesize filename", where filename should not include '/'
                        out.write(String.format("C0644 %d %s\n", bytes.length, filename).getBytes());
                        out.flush();

                        if (PiconUpUtil.checkAck(in) != 0)
                        {
                            throw new PiconUpException("Failed to send file");
                        }

                        out.write(bytes);
                        out.write(new byte[]{0});
                        out.flush();

                        if (PiconUpUtil.checkAck(in) != 0)
                        {
                            throw new PiconUpException("Failed to send file");
                        }
                    }
                }
                finally
                {
                    channel.disconnect();
                }
            }
            catch (IOException | JSchException e)
            {
                throw new PiconUpException("Failed to send", e);
            }
        }
    }
}
