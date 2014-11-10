package io.github.thred.piconup;

import java.io.IOException;
import java.util.List;

import io.github.thred.piconup.image.ImageIndex;
import io.github.thred.piconup.openwebif.GetAllServicesRequest;
import io.github.thred.piconup.openwebif.Service;
import io.github.thred.piconup.util.ConsoleSCPUserInfo;
import io.github.thred.piconup.util.SCP;
import io.github.thred.piconup.util.Wildcard;

public class PiconUpState
{

    private final PiconUpOptions options;

    private List<Service> services = null;

    private ImageIndex imageIndex = null;

    private SCP scp = null;

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

    public void openSCP() throws PiconUpException
    {
        ConsoleSCPUserInfo userInfo = new ConsoleSCPUserInfo();

        userInfo.setYes(true);
        userInfo.setPassword(options.getPassword());

        try
        {
            scp = new SCP(options.getHost(), 22, options.getUser(), userInfo, "/usr/share/enigma2");
        }
        catch (IOException e)
        {
            throw new PiconUpException("Failed to open ssh connection", e);
        }
    }

    public void closeSCP()
    {
        if (scp != null)
        {
            scp.close();
        }
    }

    public SCP getSCP()
    {
        return scp;
    }

}
