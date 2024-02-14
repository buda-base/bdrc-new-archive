package io.bdrc.lib.RepoClient;

import org.fcrepo.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;


/**
 *
 */
public class ArchivalGroupFactory {

    private static final Logger _logger = LoggerFactory.getLogger(ArchivalGroupFactory.class);

    public static ArchivalGroup create(URI endpoint, String name, String description) {

        // Build a client
        // TODO: Why am I reproducing this code from BdrcRepoClient.java?
        FcrepoClient fc = new FcrepoClient.FcrepoClientBuilder().credentials("fedoraAdmin",
                "fedoraAdmin").throwExceptionOnFailure().build();

        // Post to the endpoint  the link header defines it as an ArchivalGroup

        PostBuilder postBuilder = fc.post(endpoint);
        postBuilder.addHeader("Slug", name);
        postBuilder.addHeader("Link", "<http://fedora.info/definitions/v4/repository#ArchivalGroup>;rel=\"type\"");

        // TODO: Add dc.title and dc.description nodes to the body


        URI location;
        try (FcrepoResponse response = postBuilder.perform()) {
            location = response.getLocation();
            _logger.debug("Container creation status and location: {}, {}", response.getStatusCode(), location);
        } catch (IOException | FcrepoOperationFailedException e) {
            _logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return new ArchivalGroup(name, description, location);
    }

    /**
     * Get a named ArchivalGroup from the endpoint
     *
     * @param client context
     * @param name   search target
     * @return ArchivalGroup
     */
    public static ArchivalGroup get(RepoClient client, String name) {

        String respbody;

        // +3 copilot


//        try (FcrepoClient fcrc = new FcrepoClient.FcrepoClientBuilder().build()) {
//            GetBuilder getter = fcrc.get(endpoint.resolve("rest"));
//            getter.accept("application/ld+json");
        String frelm = client.GetResource(name);


        // Skip Server managed attributes
        // getter.addHeader("Prefer", "return=representation; omit=\"http://fedora" +
        //        ".info/definitions/v4/repository#ServerManaged\"");
//            try (FcrepoResponse top_level = getter.perform()) {
//                 if (top_level.getStatusCode() == 200) {
//                     respbody = new String(top_level.getBody().readAllBytes());
//                     _logger.debug(respbody);
//                 }
//            } catch (IOException | FcrepoOperationFailedException e) {
//                throw new RuntimeException(e);
//            }


        // Since we asked for a json+ld, use it.
        //https://www.baeldung.com/jackson-mapping-dynamic-object

        // Deserialize a list of json dictionaries into a Java object
        // https://stackoverflow.com/questions/47508216/deserialize-json-array-of-objects-with-jackson

        String rb =
                "[" +
                        "{" +
                        "\"@id\":\"http://sattva:8080/rest/\"," +
                        "\"@type\":" +
                        "[\"http://fedora.info/definitions/v4/repository#Container\"," +
                        "\"http://fedora.info/definitions/v4/repository#Resource\"," +
                        "\"http://www.w3.org/ns/ldp#Container\"," +
                        "\"http://fedora.info/definitions/v4/repository#RepositoryRoot\"," +
                        "\"http://www.w3.org/ns/ldp#Resource\"," +
                        "\"http://www.w3.org/ns/ldp#RDFSource\"," +
                        "\"http://www.w3.org/ns/ldp#BasicContainer\"]," +
                        "\"http://www.w3.org/ns/ldp#contains\":[" +
                        "{\"@id\":\"http://sattva:8080/rest/dce02103-0d8d-469b-81a1-4a737889b53d\"}," +
                        "{\"@id\":\"http://sattva:8080/rest/10462c77-a5a7-4ff7-90c2-3c4784ffba69\"}," +
                        "{\"@id\":\"http://sattva:8080/rest/05847b10-aaa9-48f1-84e1-79395e56795b\"}," +
                        "{\"@id\":\"http://sattva:8080/rest/5532a50a-8cfc-4ec0-8eaf-5943ed76856e\"}," +
                        "{\"@id\":\"http://sattva:8080/rest/dd42d0b7-6ef1-44b9-912e-4286823e291b\"}," +
                        "{\"@id\":\"http://sattva:8080/rest/b4421460-9548-4506-a324-2508a37bc014\"}," +
                        "{\"@id\":\"http://sattva:8080/rest/Volumes\"}," +
                        "{\"@id\":\"http://sattva:8080/rest/86fa8fb9-a806-4a42-a833-c911fd8987b6\"}," +
                        "{\"@id\":\"http://sattva:8080/rest/11b93b45-55b7-4a73-bf8c-7a5c1be42a8a\"}," +
                        "{\"@id\":\"http://sattva:8080/rest/7b91dfbc-b7e0-4ef6-abda-5eeadb2d394b\"}" +
                        "{\"@id\":\"http://sattva:8080/rest/d3086d5f-f1dc-41c3-b63e-464150d79304\"}," +
                        "{\"@id\":\"http://sattva:8080/rest/583311f7-1f5f-4bf8-b3f8-c51aca27abb4\"}]," +
                        "\"http://fedora.info/definitions/v4/repository#created\":" +
                        "[" +
                        "{\"@value\":\"2023-11-01T00:01:16.230478Z\"," +
                        "\"@type\":\"http://www.w3.org/2001/XMLSchema#dateTime\"" +
                        "     }" +
                        "]," +
                        "\"http://fedora.info/definitions/v4/repository#lastModified\":" +
                        "[ " +
                        "{\"@value\":\"2023-11-01T00:01:16.230478Z\"," +
                        "\"@type\":\"http://www.w3.org/2001/XMLSchema#dateTime\"" +
                        "}" +
                        "]," +
                        "\"http://fedora.info/definitions/v4/repository#hasTransactionProvider\":" +
                        "[" +
                        "{\"@id\":\"http://sattva:8080/rest/fcr:tx\"}" +
                        "]" +
                        "}" +
                        "]";

        return new ArchivalGroup("name", "description", URI.create("location"));

    }


}
