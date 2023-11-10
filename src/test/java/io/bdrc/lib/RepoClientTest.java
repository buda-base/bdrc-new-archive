package io.bdrc.lib;

import org.fcrepo.client.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


class RepoClientTest {

    @Test
    public void TestCreate() throws URISyntaxException, RuntimeException {
        FcrepoClient.FcrepoClientBuilder fcb = new FcrepoClient.FcrepoClientBuilder();
        FcrepoClient rc = fcb.build();
        GetBuilder blarg = rc.get(new URI("http://sattva:8080/rest/Volumes"));

        // Get json
        try (FcrepoResponse top_level = blarg.accept("application/ld+json").perform()) {
            String respbody = new String(top_level.getBody().readAllBytes());
            System.out.println(respbody);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (FcrepoOperationFailedException e) {
            throw new RuntimeException(e);
        }

        // Repeat previous request, but get turtle
        try (FcrepoResponse top_level = blarg.accept("text/turtle").perform()) {
            String respbody = new String(top_level.getBody().readAllBytes());
            System.out.println(respbody);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (FcrepoOperationFailedException e) {
            throw new RuntimeException(e);
        }


    }


//    FcrepoClient.FcrepoClientBuilder fcb = new FcrepoClient.FcrepoClientBuilder();
//
//    fcb.  // .credentials("","").host("http://sattva:8080").throwExceptionOnFailure(true);
}