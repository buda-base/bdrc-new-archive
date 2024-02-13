package io.bdrc.lib;

import org.fcrepo.client.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


class RepoClientTest {

    // create JUnit TestFixture to create a FcrepoClient
    private FcrepoClient _fcrepoClient;
    private final Logger _logger = LoggerFactory.getLogger(RepoClientTest.class);

    @BeforeEach
    public void createClient() {
        FcrepoClient.FcrepoClientBuilder fcb = new FcrepoClient.FcrepoClientBuilder();
        _fcrepoClient = fcb.build();

    }


    @Test
    public void TestGet() throws URISyntaxException, RuntimeException {
        GetBuilder blarg = _fcrepoClient.get(new URI("http://sattva:8080/rest/Volumes"));

        // Get json
        try (FcrepoResponse top_level = blarg.accept("application/ld+json").perform()) {
            String respbody = new String(top_level.getBody().readAllBytes());
            _logger.info(respbody);
        } catch (IOException | FcrepoOperationFailedException e) {
            throw new RuntimeException(e);
        }

        // Repeat previous request, but get turtle
        try (FcrepoResponse top_level = blarg.accept("text/turtle").perform()) {
            String respbody = new String(top_level.getBody().readAllBytes());
            _logger.info(respbody);
        } catch (IOException | FcrepoOperationFailedException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void TestAddArchivalGroup() throws URISyntaxException , RuntimeException{
        // Blending the RESTFul HTTP API @ https://wiki.lyrasis.org/display/FEDORA6x/RESTful+HTTP+API+-+Containers#RESTfulHTTPAPIContainers-BluePOSTCreatenewresourceswithinaLDPcontainer
        // and the FcrepoClient API @https://github.com/fcrepo-exts/fcrepo-java-client
        // curl -X POST -u fedoraAdmin:fedoraAdmin -H "Slug: my-archival-group" -H "Link: <http://fedora.info/definitions/v4/repository#ArchivalGroup>;rel=\"type\"" http://localhost:8080/rest
        PostBuilder postBuilder = _fcrepoClient.post(new URI("http://sattva:8080/rest"));
        postBuilder.addHeader("Slug", "Volumes");
        postBuilder.addHeader("Link", "<http://fedora.info/definitions/v4/repository#ArchivalGroup>;rel=\"type\"");

        try (FcrepoResponse response = postBuilder.perform()) {
            URI location = response.getLocation();
            _logger.info("Container creation status and location: {}, {}", response.getStatusCode(), location);
        } catch (IOException | FcrepoOperationFailedException e) {
            throw new RuntimeException(e);
        }
    }



    @Test
    public void DeleteArchivalGroup() throws URISyntaxException, RuntimeException {
        /* curl -X DELETE -u fedoraAdmin:fedoraAdmin http://localhost:8080/rest/my-archival-group
        http://sattva:8080/rest/dce02103-0d8d-469b-81a1-4a737889b53d
        http://sattva:8080/rest/10462c77-a5a7-4ff7-90c2-3c4784ffba69
        http://sattva:8080/rest/05847b10-aaa9-48f1-84e1-79395e56795b
        http://sattva:8080/rest/5532a50a-8cfc-4ec0-8eaf-5943ed76856e
        http://sattva:8080/rest/dd42d0b7-6ef1-44b9-912e-4286823e291b
        http://sattva:8080/rest/b4421460-9548-4506-a324-2508a37bc014
        http://sattva:8080/rest/Volumes
        http://sattva:8080/rest/86fa8fb9-a806-4a42-a833-c911fd8987b6
        http://sattva:8080/rest/11b93b45-55b7-4a73-bf8c-7a5c1be42a8a
        http://sattva:8080/rest/7b91dfbc-b7e0-4ef6-abda-5eeadb2d394b
        http://sattva:8080/rest/d3086d5f-f1dc-41c3-b63e-464150d79304
        http://sattva:8080/rest/583311f7-1f5f-4bf8-b3f8-c51aca27abb4

         */
        DeleteBuilder deleteBuilder = _fcrepoClient.delete(new URI("http://sattva:8080/rest/Volumes"));
        try (FcrepoResponse response = deleteBuilder.perform()) {
            _logger.info("Container deletion status: {}", response.getStatusCode());
        } catch (IOException | FcrepoOperationFailedException e) {
            throw new RuntimeException(e);
        }
    }

}