package io.bdrc.lib;

import org.fcrepo.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Stream;

// Facade for FcrepoClient, uses the builder.
public class BdrcRepoClient {

private final static String DirectContainerTtl =
        "@prefix ldp: <http://www.w3.org/ns/ldp#> .\n" +
                "@prefix pcdm: <http://pcdm.org/models#> .\n" +
                "@prefix dc <http://purl.org/dc/elements/1.1/>\n" +
            "<> a ldp:DirectContainer, pcdm:Object;\n" +
            "   ldp:membershipResource <>;\n" +
            "   ldp:hasMemberRelation pcdm:hasMember;\n" +
            "   ldp:isMemberOfRelation pcdm:isMemberOf;\n" +
            "   ldp:insertedContentRelation pcdm:hasFile .";

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
    public  String AddContainer(String parent, String ContainerName) throws RuntimeException {

        // Set the content type for the direct container
        String contentType = "text/turtle";

        URI containerUri = getEndpoint().resolve(parent +"/" );
        String titleTTL =
                String.format("@prefix dc: <http://purl.org/dc/elements/1.1/> <> dc:title \"%s\" .", ContainerName);

        InputStream containerBodyStream =  getInputStream(titleTTL);

        String respbody ;

        try (FcrepoResponse top_level =_fcrepoClient
                .post(containerUri)

                // UIse the header to define the content type, not the ttl - the sample
                // from the FcrepoClient documentation is wrong - gives a 400
                .body(containerBodyStream, contentType)

                // Take 1aProblem with a slug is that fcrepo will create a new resource if it has the Slug
                // as an existing one.
                .addHeader("Slug", ContainerName)

                // Take 1 - add type as header
                .addHeader("Link", "<http://www.w3.org/ns/ldp#DirectContainer>; rel=\"type\"")
                .perform()) {
            if (top_level.getStatusCode() != 201) {
              throw new RuntimeException(String.format("Add Container  %s/%s failed: %d",containerUri,ContainerName,
                      top_level.getStatusCode()));
            }
            respbody = new String(top_level.getBody().readAllBytes());
            _logger.info(respbody);
        } catch (IOException | FcrepoOperationFailedException e) {
            throw new RuntimeException(e);
        }
        return respbody;

    }

    private static InputStream getInputStream(String content) {

        // +1 OpenAI
        InputStream  stream = new ByteArrayInputStream(content.getBytes()) ;
        return stream;
    }

}
