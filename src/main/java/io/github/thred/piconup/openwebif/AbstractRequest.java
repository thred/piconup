package io.github.thred.piconup.openwebif;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import io.github.thred.piconup.PiconUpException;
import io.github.thred.piconup.PiconUpOptions;

public abstract class AbstractRequest
{

    private final PiconUpOptions options;

    public AbstractRequest(PiconUpOptions options)
    {
        super();

        this.options = options;
    }

    protected abstract String getName();

    protected String executeRequest() throws PiconUpException
    {
        String url = options.getOpenWebIFURL().toExternalForm();
        String name = getName();

        while (url.endsWith("/"))
        {
            url = url.substring(0, url.length() - 1);
        }

        while (name.startsWith("/"))
        {
            name = name.substring(1);
        }

        URI uri = URI.create(url + "/" + name);

        System.out.printf("Calling %s ... ", uri.toASCIIString());

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(uri);

        try
        {
            CloseableHttpResponse response = httpClient.execute(httpGet);

            try
            {
                System.out.println(response.getStatusLine());

                HttpEntity entity = response.getEntity();

                return EntityUtils.toString(entity, "UTF-8");
            }
            finally
            {
                response.close();
            }
        }
        catch (ClientProtocolException e)
        {
            throw new PiconUpException("Request failed", e);
        }
        catch (IOException e)
        {
            throw new PiconUpException("Request failed", e);
        }
    }
}
