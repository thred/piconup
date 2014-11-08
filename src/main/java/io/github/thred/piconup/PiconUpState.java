package io.github.thred.piconup;

import io.github.thred.piconup.image.ImageIndex;
import io.github.thred.piconup.openwebif.GetAllServicesRequest;
import io.github.thred.piconup.openwebif.Service;

import java.util.List;

public class PiconUpState
{

    private final PiconUpOptions options;

    private List<Service> services = null;

    private ImageIndex imageIndex = null;

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
}
