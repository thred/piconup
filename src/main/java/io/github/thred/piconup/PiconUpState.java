package io.github.thred.piconup;

import io.github.thred.piconup.image.ImageIndex;
import io.github.thred.piconup.openwebif.GetAllServicesRequest;
import io.github.thred.piconup.openwebif.Service;
import io.github.thred.piconup.util.PiconUpUserInfo;
import io.github.thred.piconup.util.Wildcard;

import java.util.List;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class PiconUpState
{

    private final PiconUpOptions options;

    private List<Service> services = null;

    private ImageIndex imageIndex = null;

    private Session session = null;

    public PiconUpState(PiconUpOptions options)
    {
        super();

        this.options = options;
    }

    public PiconUpOptions getOptions()
    {
        return options;
    }

    public List<Service> getServices() throws PiconUpException
    {
        if (services != null)
        {
            return services;
        }

        if (!options.isOpenWebIFURL())
        {
            throw new PiconUpException("No host or openWebIFURL specified in options");
        }

        GetAllServicesRequest request = new GetAllServicesRequest(options);

        try
        {
            services = request.execute();
        }
        catch (PiconUpException e)
        {
            throw new PiconUpException("Failed to request services");
        }

        String[] patterns = options.getPatterns();

        if ((patterns != null) && (patterns.length > 0))
        {
            services.removeIf((service) -> !Wildcard.match(service.getSimplifiedName(), patterns));
        }

        ImageIndex imageIndex = getImageIndex();

        services.forEach((service) -> service.setImageIndexEntry(imageIndex.find(service.getName())));

        return services;
    }

    public ImageIndex getImageIndex() throws PiconUpException
    {
        if (imageIndex != null)
        {
            return null;
        }

        imageIndex = ImageIndex.createPredefined();

        return imageIndex;
    }

    public void openSession() throws PiconUpException
    {
        JSch jsch = new JSch();

        try
        {
            session = jsch.getSession(options.getUser(), options.getHost(), 22);
            session.setPassword(options.getPassword());
            session.setUserInfo(new PiconUpUserInfo());
            session.connect();
        }
        catch (JSchException e)
        {
            throw new PiconUpException("Failed to connect via SSH", e);
        }
    }

    public void closeSession()
    {
        if (session != null)
        {
            session.disconnect();
            session = null;
        }
    }

    public Session getSession()
    {
        return session;
    }

}
