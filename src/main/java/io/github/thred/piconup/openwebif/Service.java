package io.github.thred.piconup.openwebif;

import io.github.thred.piconup.image.ImageIndexEntry;
import io.github.thred.piconup.util.CompareUtils;
import io.github.thred.piconup.util.PiconUpUtil;

public class Service implements Comparable<Service>
{

    private final String reference;
    private final String name;
    private final String simplifiedName;

    private ImageIndexEntry imageIndexEntry;

    public Service(String reference, String name)
    {
        super();

        this.reference = reference;
        this.name = name;

        simplifiedName = PiconUpUtil.simplify(name);
    }

    public String getReference()
    {
        return reference;
    }

    public String getTargetFilename()
    {
        String result = reference.replace(':', '_');

        if (result.endsWith("_"))
        {
            result = result.substring(0, result.length() - 1);
        }

        return result + ".png";
    }

    public String getName()
    {
        return name;
    }

    public String getSimplifiedName()
    {
        return simplifiedName;
    }

    public ImageIndexEntry getImageIndexEntry()
    {
        return imageIndexEntry;
    }

    public void setImageIndexEntry(ImageIndexEntry imageIndexEntry)
    {
        this.imageIndexEntry = imageIndexEntry;
    }

    public String getSourceFilename()
    {
        return (imageIndexEntry != null) ? imageIndexEntry.getFilename() : "";
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = (prime * result) + ((name == null) ? 0 : name.hashCode());
        result = (prime * result) + ((reference == null) ? 0 : reference.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj == null)
        {
            return false;
        }

        if (getClass() != obj.getClass())
        {
            return false;
        }

        Service other = (Service) obj;

        if (name == null)
        {
            if (other.name != null)
            {
                return false;
            }
        }
        else if (!name.equals(other.name))
        {
            return false;
        }

        if (reference == null)
        {
            if (other.reference != null)
            {
                return false;
            }
        }
        else if (!reference.equals(other.reference))
        {
            return false;
        }

        return true;
    }

    @Override
    public int compareTo(Service o)
    {
        return CompareUtils.dictionaryCompare(getName(), o.getName());
    }

    @Override
    public String toString()
    {
        return String.format("%-24s | %-24s | %-34s | %s", getName(), getSimplifiedName(), getReference(),
            getSourceFilename());
    }

}
