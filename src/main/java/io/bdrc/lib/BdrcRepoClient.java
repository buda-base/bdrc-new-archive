package io.bdrc.lib;

import org.fcrepo.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;


// Facade for FcrepoClient, uses the builder.
public class BdrcRepoClient {

    // Java 11 doesn't like support this structure
    //    private final static String DirectContainerTtl =
//            """
//                    "@prefix ldp: <http://www.w3.org/ns/ldp#> .
//                    @prefix pcdm: <http://pcdm.org/models#> .
//                    @prefix dc <http://purl.org/dc/elements/1.1/>
//                    <> a ldp:DirectContainer, pcdm:Object;
//                       ldp:membershipResource <>;
//                       ldp:hasMemberRelation pcdm:hasMember;
//                       ldp:isMemberOfRelation pcdm:isMemberOf;
//                       ldp:insertedContentRelation pcdm:hasFile .""";

    private final FcrepoClient _fcrepoClient;

    private final Logger _logger = LoggerFactory.getLogger("client");

    private final BdrcRepoMediaValidator _mediaValidator;

    // what do we want from the server?
    private String _acceptMedia = BdrcRepoMediaValidator.DefaultMediaType();

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

    private String _sendMedia = BdrcRepoMediaValidator.DefaultMediaType();

    public String getSendMedia() {
        return _sendMedia;
    }

    public void setSendMedia(final String sendMedia) {
        if (_mediaValidator.validate(sendMedia)) {
            _sendMedia = sendMedia;
        } else {
            throw new IllegalArgumentException("Invalid media type: " + sendMedia);
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


        _logger.debug("BdrcRepoClient constructor {}", endpoint);
        _mediaValidator = new BdrcRepoMediaValidator();
        setAcceptMedia(acceptMedia);

        _fcrepoClient = new FcrepoClient.FcrepoClientBuilder()
                .credentials("fedoraAdmin", "fedoraAdmin")
                .build();
        setEndpoint(endpoint);
    }

    /**
     * @param endpoint The URI of the Fedora Commons repository (site + port + /rest)
     */
    public BdrcRepoClient(URI endpoint) {
        this(endpoint, BdrcRepoMediaValidator.DefaultMediaType());

    }

    //</editor-fold>

    public String GetResource(String resourcePath) throws RuntimeException {

        String respbody;
        GetBuilder builder = _fcrepoClient.get(_endpoint.resolve(resourcePath));
        try (FcrepoResponse top_level = builder.accept(_acceptMedia).perform()) {
            if (top_level.getStatusCode() != 200) {
                throw new RuntimeException("GetResource failed: " + top_level.getStatusCode());
            }

            respbody = new String(top_level.getBody().readAllBytes());
            // _logger.info(respbody);
        } catch (IOException | FcrepoOperationFailedException e) {
            throw new RuntimeException(e);
        }
        return respbody;
    }


    /**
     * @param parent      parent container
     * @param container   container type
     * @param title       Dublin Core title - recommend to not use in FcRepo environment
     * @param description - free text description of object
     * @return URI of newly created container
     * @throws RuntimeException on any exception
     */
    public String AddContainerDCTitleDesc(String parent, LdpContainer container, String title,
                                          String description) throws RuntimeException
    {

        // Set the content type for the direct container
        String contentType = "text/turtle";

        // Create the container
        URI containerUri = getEndpoint();
        if (parent != null) {
            containerUri = getEndpoint().resolve(parent + "/");
        }

        // Add dublin core title and description to the container
        // Callers should not, in general, use dc:title
        // See https://www.dublincore.org/specifications/dublin-core/dcmi-terms/#section-3
        StringBuilder dcTTL = new StringBuilder();
        if (title != null) {
            dcTTL.append(String.format("@prefix dc: <http://purl.org/dc/elements/1.1/> <> dc:title \"%s\" .\n",
                    title));
        }
        if (description != null) {
            dcTTL.append(String.format("@prefix dc: <http://purl.org/dc/elements/1.1/> <> dc:description \"%s\" .\n",
                    description));
        }
        if ((description == null) && (title == null)) {
            dcTTL.append("a <> .\n");
        }

        InputStream containerBodyStream = getInputStream(dcTTL.toString());

        String respbody;

        _logger.info(container.toString() + "; rel=\"type\"");
        try (FcrepoResponse top_level = _fcrepoClient
                .post(containerUri)

                // UIse the header to define the content type, not the ttl - the sample
                // from the FcrepoClient documentation is wrong - gives a 400
                .body(containerBodyStream, contentType)

                // Take 1aProblem with a slug is that fcrepo will create a new resource if it has the Slug
                // as an existing one.
                // jimk: Don't add "slug" header, let fcrepo name its resource
                // .addHeader("Slug", ContainerName)

                // Take 1 - add type as header
                // .addHeader("Link", "<http://www.w3.org/ns/ldp#DirectContainer>; rel=\"type\"")
                // Take 2 - use the passed in container type
                .addHeader("Link", container + "; rel=\"type\"")
                .perform()) {
            if (top_level.getStatusCode() != 201) {
                throw new RuntimeException(String.format("Add Container  %s/%s failed: %d", containerUri, (title == null) ? "" : title,
                        top_level.getStatusCode()));
            }
            respbody = new String(top_level.getBody().readAllBytes());
            _logger.info(respbody);
        } catch (IOException | FcrepoOperationFailedException e) {
            throw new RuntimeException(e);
        }
        return respbody;

    }

    public String AddContainerSlug(String parent, LdpContainer container, String title,
                                   String description) throws RuntimeException
    {

        // Set the content type for the direct container
        String contentType = "text/turtle";

        // Create the container
        URI containerUri = getEndpoint();
        if (parent != null) {
            containerUri = getEndpoint().resolve(parent + "/");
        }

        String respbody;
        String containerType =container.toString() + "; rel=\"type\"";
        _logger.debug("Container type: {}  URI: {}", containerType, containerUri);

        try (FcrepoResponse top_level = _fcrepoClient
                .post(containerUri)

                // Take 1aProblem with a slug is that fcrepo will create a new resource if it has the Slug
                // as an existing one.
                //
                .addHeader("Slug", title)

                // Take 1 - add type as header
                // .addHeader("Link", "<http://www.w3.org/ns/ldp#DirectContainer>; rel=\"type\"")
                // Take 2 - use the passed in container type
                .addHeader("Link", containerType)
                .perform()) {
            if (top_level.getStatusCode() != 201) {
                throw new RuntimeException(String.format("Add Container  %s/%s failed: %d", containerUri, (title == null) ? "" : title,
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
        return new ByteArrayInputStream(content.getBytes());
    }

    public String AddBinary(final String parentUrl, final java.nio.file.Path input_file,
                            final String file_content_type,
                            final String md5sum)
    {
        String inputFileName = input_file.getFileName().toString();
        // Maybe this was the error:
        // URI containerUri = getEndpoint().resolve(parentUrl + "/" + inputFileName);
        URI containerUri = getEndpoint().resolve(parentUrl);
        _logger.info("Adding {} to {}", inputFileName, containerUri);
        String respbody;
        // FcrepoClient _fcr = new FcrepoClient.FcrepoClientBuilder().build();
        try (FcrepoResponse top_level = _fcrepoClient
                .post(containerUri)
                .body(input_file.toFile(), file_content_type)
                .filename(inputFileName)
                .addHeader("Digest", "md5=" + md5sum)
                .perform()) {
            if (top_level.getStatusCode() != 201) {
                throw new RuntimeException(String.format("Add Binary  %s/%s failed: %d", containerUri, inputFileName,
                        top_level.getStatusCode()));
            }
            respbody = new String(top_level.getBody().readAllBytes());
            _logger.info(respbody);
        } catch (IOException | FcrepoOperationFailedException e) {
            throw new RuntimeException(e);
        }
        return respbody;
    }

    /**
     * Increment
     * @param resourceName
     * How would I add the "message" key, below?
     * @return
     *    "v2": {
     *       "created": "2018-02-02T02:02:02Z",
     *       "message": "Fix bar.xml, remove image.tiff, add empty2.txt, rename file1.txt to file2.txt, add updated file1.txt",
     *       "state": {
     *         "9bb43j...n3a": [ "file2.txt" ],
     *         "u8b99v...7b2": [ "file1.txt" ],
     *         "4d27c8...b53": [ "foo/bar.xml" ],
     *         "cf83e1...a3e": [ "empty.txt", "empty2.txt" ]
     *       },
     */
    public URI AddVersion(final String resourceName, String messageSlug) throws BdrcRepoException  {
        // https://github.com/fcrepo-exts/fcrepo-java-client
        // https://wiki.lyrasis.org/display/FEDORA4x/Versioning

        URI versionLocation ;
        String _resourceName = resourceName;
        try {
            if (!_resourceName.endsWith("/")) {
                _resourceName += "/";
            }
        URI versionURI = addChild(_endpoint, _resourceName);
        versionURI = addChild(versionURI, "fcr:versions");
        FcrepoResponse response = new PostBuilder(versionURI, _fcrepoClient)
                .perform();
            versionLocation = response.getLocation();
            _logger.debug("Version creation input: {}  status and location: {}, {}", versionURI, response.getStatusCode(),
                    versionLocation);
        }
        catch (URISyntaxException | FcrepoOperationFailedException e ){
            throw new BdrcRepoException(e.getMessage());
        }

        return versionLocation ;

    }

    /**
     * Work through examples in https://github.com/fcrepo-exts/fcrepo-java-client
     * That doc is a loser. The Lyrasis Wiki shows how to use transactions:
     * https://wiki.lyrasis.org/display/FEDORA4x/Transactions
     */

    public URI txnBegin() throws BdrcRepoException {
        URI txnLocation;
        try {
            URI _tmp = addChild(_endpoint, "fcr:tx");
            FcrepoResponse response = _fcrepoClient.post(_tmp).perform();
            txnLocation = response.getLocation();
            _logger.debug("Transaction creation status and location: {}, {}", response.getStatusCode(), txnLocation);
        } catch (Exception e) {
            throw (BdrcRepoException) (e);
        }
        return txnLocation;
    }

    /**
     * Roll back and fcRepo Transction
     *
     * @param txnLocation fcRepo transactions are identified by an URI
     */
    public void txnRollback(URI txnLocation) throws BdrcRepoException {
        txnOp(txnLocation, "fcr:rollback");
    }

    /**
     * Commit an fcRepo transaction
     *
     * @param txnLocation fcRepo transactions are identified by an URI
     */
    public void txnCommit(URI txnLocation) throws BdrcRepoException {
        txnOp(txnLocation, "fcr:commit");
    }

    private void txnOp(URI txnLocation, String op) throws BdrcRepoException {
        try {
            URI commitURI = addChild(txnLocation, "./" + op);
            FcrepoResponse response = new PostBuilder(commitURI, _fcrepoClient).perform();
            _logger.debug("Transaction commit status: {}", response.getStatusCode());
        }
        catch (FcrepoOperationFailedException | URISyntaxException e ) {
            throw new BdrcRepoException(e.getMessage());
        }
    }


    public URI addChild(URI baseURI, String extension) throws URISyntaxException {
        // Extract existing components

        return baseURI.resolve("./" + extension);
       //  return URI.create(baseURI.toString()  + extension + "/") ;
        // Create a new URI with the extended components
        // return new URI(scheme, null, host, port, path, extension, null);
    }

    public URI addChild(URI baseURI, String paramName, String paramValue) throws URISyntaxException {
        // Extract existing components
        String scheme = baseURI.getScheme();
        String host = baseURI.getHost();
        int port = baseURI.getPort();
        String path = baseURI.getPath();

        // Create a query string with the new parameter
        String query = _endpoint.getQuery();
        if (query == null) {
            query = paramName + "=" + paramValue;
        } else {
            query += "&" + paramName + "=" + paramValue;
        }

        // Create a new URI with the extended components
        return new URI(scheme, null, host, port, path, query, null);
    }
}
