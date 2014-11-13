package io.github.thred.piconup.openwebif;

import io.github.thred.piconup.PiconUpException;
import io.github.thred.piconup.PiconUpOptions;
import io.github.thred.piconup.util.DOMUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class GetAllServicesRequest extends AbstractRequest
{

    public GetAllServicesRequest(PiconUpOptions options)
    {
        super(options);
    }

    @Override
    protected String getName()
    {
        return "web/getallservices";
    }

    public List<Service> execute() throws PiconUpException
    {
        String response = executeRequest();
        Document document = DOMUtils.read(response);

        return new ArrayList<>(DOMUtils.findAll(document, "//e2service").stream().map(GetAllServicesRequest::parseServiceNode)
            .collect(Collectors.toSet()));
    }

    protected static Service parseServiceNode(Node node)
    {
        String reference = DOMUtils.getText(DOMUtils.element(node, "e2servicereference"), null);
        String name = DOMUtils.getText(DOMUtils.element(node, "e2servicename"), null);

        return new Service(reference, name);
    }
}
