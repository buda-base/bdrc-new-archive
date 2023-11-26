package io.bdrc.lib;

import org.fcrepo.client.FcrepoClient;
import org.fcrepo.client.FcrepoOperationFailedException;
import org.fcrepo.client.FcrepoResponse;
import org.fcrepo.client.GetBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.List;

// Facade for FcrepoClient, uses the builder.
public class BdrcRepoClient {


    private final FcrepoClient _fcrepoClient;

    private Logger _logger = LoggerFactory.getLogger(BdrcRepoClient.class);

    private final BdrcRepoMediaValidator _mediaValidator;

    // what do we want from the server?
    private String _acceptMedia = "application/ld+json";

    public String getAcceptMedia() {
        return _acceptMedia;
    }

    public void setAcceptMedia(String acceptMedia) {
        if (_mediaValidator.validate(acceptMedia)) {
            _acceptMedia = acceptMedia;
        } else {
            throw new IllegalArgumentException("Invalid media type: " + acceptMedia);
        }
    }

    private URI _endpoint;

    public void setEndpoint(URI endpoint) {
        _endpoint = endpoint;
    }

    public URI getEndpoint() {
        return _endpoint;
    }


    //<editor-fold desc="Constructors">
    public BdrcRepoClient(URI endpoint, String acceptMedia) {
        _mediaValidator = new BdrcRepoMediaValidator();
        _fcrepoClient = new FcrepoClient.FcrepoClientBuilder().build();
        setAcceptMedia(acceptMedia);
        setEndpoint(endpoint);
    }

    public BdrcRepoClient(URI endpoint) {
        _mediaValidator = new BdrcRepoMediaValidator();
        _fcrepoClient = new FcrepoClient.FcrepoClientBuilder().build();
        setEndpoint(endpoint);
    }

    // Overload to select media on a get-by-get basis
    public BdrcRepoClient(URI endpoint, List<String> acceptMedias) {
        _mediaValidator = new BdrcRepoMediaValidator(acceptMedias);
        _fcrepoClient = new FcrepoClient.FcrepoClientBuilder().build();
        setEndpoint(endpoint);
    }
    //</editor-fold>

    public String GetResource(String resourcePath) throws RuntimeException {

        String respbody ;
        GetBuilder builder = _fcrepoClient.get(_endpoint.resolve(resourcePath));
        try (FcrepoResponse top_level = builder.accept(_acceptMedia).perform()) {
            if (top_level.getStatusCode() != 200) {
                throw new RuntimeException("GetResource failed: " + top_level.getStatusCode());
            }
            respbody = new String(top_level.getBody().readAllBytes());
            _logger.info(respbody);
        } catch (IOException | FcrepoOperationFailedException e) {
            throw new RuntimeException(e);
        }
        return respbody;
    }

}
