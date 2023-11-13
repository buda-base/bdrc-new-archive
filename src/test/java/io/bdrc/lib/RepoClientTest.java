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
    private Logger _logger = LoggerFactory.getLogger(RepoClientTest.class);

    @BeforeEach
    private void createClient() {
        FcrepoClient.FcrepoClientBuilder fcb = new FcrepoClient.FcrepoClientBuilder();
        _fcrepoClient = fcb.build();

        // This gnarl from stackoverflow. Slf4j doesn't seem to havea set level, but the underlying
        // log4j does

        _logger.error("diagnose {}","_logger");
    }


    @Test
    public void TestGet() throws URISyntaxException, RuntimeException {
        GetBuilder blarg = _fcrepoClient.get(new URI("http://sattva:8080/rest"));

        // Get json
        try (FcrepoResponse top_level = blarg.accept("application/ld+json").perform()) {
            String respbody = new String(top_level.getBody().readAllBytes());
            _logger.info(respbody);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (FcrepoOperationFailedException e) {
            throw new RuntimeException(e);
        }

        // Repeat previous request, but get turtle
        try (FcrepoResponse top_level = blarg.accept("text/turtle").perform()) {
            String respbody = new String(top_level.getBody().readAllBytes());
            _logger.info(respbody);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (FcrepoOperationFailedException e) {
            throw new RuntimeException(e);
        }
    }

    @Disabled
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (FcrepoOperationFailedException e) {
            throw new RuntimeException(e);
        }
    }

}