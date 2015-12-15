package io.github.thred.piconup.util;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class SCP implements Closeable
{

    private final String host;
    private final int port;
    private final String user;
    private final UserInfo userInfo;
    private final String basePath;

    private Session session;
    private OutputStream out;
    private InputStream in;

    public SCP(String host, int port, String user, UserInfo userInfo, String basePath) throws IOException
    {
        super();

        this.host = host;
        this.port = port;
        this.user = user;
        this.userInfo = userInfo;
        this.basePath = basePath;

        JSch jsch = new JSch();

        try
        {
            session = jsch.getSession(user, host, port);
            session.setUserInfo(userInfo);
            session.connect();

            ChannelExec channel = (ChannelExec) session.openChannel("exec");

            channel.setCommand(String.format("scp -rt %s", basePath));

            out = channel.getOutputStream();
            in = channel.getInputStream();

            channel.connect();

            waitForAck();
        }
        catch (JSchException | IOException e)
        {
            close();

            throw new IOException("SCP failed", e);
        }
    }

    @Override
    public void close()
    {
        if (in != null)
        {
            try
            {
                in.close();
            }
            catch (IOException e)
            {
                // ignore
            }
        }

        if (out != null)
        {
            try
            {
                out.close();
            }
            catch (IOException e)
            {
                // ignore
            }
        }

        if (session != null)
        {
            session.disconnect();
            session = null;
        }
    }

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }

    public String getUser()
    {
        return user;
    }

    public UserInfo getUserInfo()
    {
        return userInfo;
    }

    public String getBasePath()
    {
        return basePath;
    }

    public void setSession(Session session)
    {
        this.session = session;
    }

    public Session getSession()
    {
        return session;
    }

    public void write(String path, byte[] content) throws IOException
    {
        try (InputStream contentStream = new ByteArrayInputStream(content))
        {
            write(path, contentStream, content.length);
        }
    }

    public void write(String path, InputStream contentStream, int contentLength) throws IOException
    {
        String[] split = path.split("(?<!\\\\)/", 2); // it's ok for now, but foo\\/bar does not work
        String filename = split[0];

        if (split.length > 1)
        {
            session.getUserInfo().showMessage("Creating directory " + filename + " ...");

            out.write(String.format("D0755 0 %s\n", filename).getBytes());
            out.flush();
            waitForAck();

            try
            {
                write(split[1], contentStream, contentLength);
            }
            finally
            {
                out.write("E\n".getBytes());
                out.flush();
                waitForAck();
            }
        }
        else
        {
            session.getUserInfo().showMessage("Sending file " + filename + " ...");

            out.write(String.format("C0644 %d %s\n", contentLength, filename).getBytes());
            out.flush();
            waitForAck();

            byte[] buffer = new byte[1024];
            int totalLength = 0;
            int length;

            while ((length = contentStream.read(buffer)) >= 0)
            {
                out.write(buffer, 0, length);

                totalLength += length;
            }

            if (totalLength != contentLength)
            {
                throw new IOException(
                    String.format("Content lenght does not match real length: %d != %d", contentLength, totalLength));
            }

            sendAck();
            waitForAck();
        }
    }

    protected void sendAck() throws IOException
    {
        out.write(new byte[]{0});
        out.flush();
    }

    protected void waitForAck() throws IOException
    {
        int b = in.read();

        if (b == 0)
        {
            return;
        }

        if ((b == 1) || (b == 2))
        {
            StringBuffer sb = new StringBuffer();
            int c;
            do
            {
                c = in.read();
                sb.append((char) c);
            } while (c != '\n');

            if (b == 1)
            {
                throw new IOException("Error - " + sb);
            }

            if (b == 2)
            {
                throw new IOException("Fatal error - " + sb);
            }
        }

        throw new IOException("Error " + b);
    }

    public static void main(String[] args) throws IOException
    {
        ConsoleSCPUserInfo userInfo = new ConsoleSCPUserInfo();

        userInfo.setYes(true);

        try (SCP scp = new SCP("localhost", 22, "ham", userInfo, "."))
        {
            scp.write("foo", "this is a test".getBytes());
        }
    }
}
