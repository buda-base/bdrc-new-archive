package io.bdrc.lib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Main {
    public Main() {
    }

    private static final Logger _logger = LoggerFactory.getLogger("client");

    public static ArchivalGroup AddArchiveGroup(URI fcRestEndpoint, String agName, String agDescription) {
        // Pause this for a while, see ArchivalGroupFactory.java for sample output
        // ArchivalGroup  = ArchivalGroupFactory.get(fcRestEndpoint, "Volumes");
        return ArchivalGroupFactory.create(fcRestEndpoint, agName, agDescription);
    }

    public static void AddVolumes(BdrcRepoClient client, String resourceParent, String[] volumeNames) {
        // Add a direct container to the Volumes archival group

        for (String volumeName : volumeNames) {
            String volume = client.AddContainerSlug(resourceParent, LdpContainer.DirectContainer, volumeName,
                    "Programmatically created");
            _logger.debug(volume);
        }
    }


    private static String calculateMD5(Path filePath) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");

        try (DigestInputStream dis = new DigestInputStream(Files.newInputStream(filePath), md)) {
            // Read the file, the digest is calculated automatically
            while (dis.read() != -1) ;
        }

        // Get the MD5 hash
        byte[] digest = md.digest();

        // Convert the byte array to a hexadecimal string
        StringBuilder result = new StringBuilder();
        for (byte b : digest) {
            result.append(String.format("%02x", b));
        }

        return result.toString();
    }

    public static void AddImages(String parentUrl, BdrcRepoClient client, Path source_dir) {
        // Add three binary objects to the parentUrl Fcrepo resource
        // get md5shasum for a file

        try {
            // Walk through the directory and its subdirectories
            Files.walk(source_dir)
                    .filter(Files::isRegularFile) // Filter only regular files
                    .forEach(filePath -> {
                        // Process each file

                        try {
                            String md5sum = calculateMD5(filePath);
                            String contentType = Files.probeContentType(filePath);
                            _logger.debug(" contentType {} ", contentType);
                            String image = client.AddBinary(parentUrl, filePath, contentType, md5sum);
                            _logger.debug(image);

                        } catch (IOException | NoSuchAlgorithmException e) {
                            _logger.error(e.getLocalizedMessage());
                        }
                        // Add your custom logic here
                    });
        } catch (IOException e) {
            _logger.error(e.getLocalizedMessage());
            // e.printStackTrace();
        }

    }


    public static void main(String[] args) throws URISyntaxException, BdrcRepoException {

        // Add an Archival group to the repository

        // Add three containers to the "Volumes" repository
        // In each container, add three image files.

        URI fcRestEndpoint = new URI("http://localhost:8088/fcrepo/rest/");
        BdrcRepoClient client = new BdrcRepoClient(fcRestEndpoint, "application/n-triples");
        // BdrcRepoClient client = new BdrcRepoClient(fcRestEndpoint, "text/turtle");

        // Scenario 2: Add an Archival group, named "volumes"
        // Add three containers
        // Add three images to each container.
        // In between, monitor the repository with the Fedora UI

        // Scenario 1 - get the Volumes resource
//        String whatapp = client.GetResource("Volumes");
//        _logger.debug("whatapp {}", whatapp);

// Scenario 2 - get the root resources
//        String whatapp2 = client.GetResource("");
//        _logger.debug("whatapp2 {}", whatapp2);

// Scenario 3 - create Volumes Archive group
// ArchivalGroup volumesAG = AddArchiveGroup(fcRestEndpoint, "Volumes",
//                "Archival Group of Image Volumes. Code created");
//        _logger.debug("Created {} ", volumesAG);

// Scenario 4 - Create Volumes in the Volumes archival group
//        ArchivalGroup ag = ArchivalGroupFactory.get(client,"Volumes");
//        CreateVolumes(client, new ArchivalGroup("Volumes", null, null), new String[]{"Volume1", "Volume2",
//                "Volume3"});


// Scenario 5: Get the volumes by name - this requires  a search on dc.title, or some other way
// to add a resource by name
//        String VolsArchiveGroup = client.GetResource("Volumes");
//        _logger.info(VolsArchiveGroup);
//        for (int i = 1 ; i <= 3; i++) {
//            String volumei = client.GetResource(String.format("Volumes/Volume%d", i ));
//            _logger.info(volumei);
//        }

        // Scenario 6: Create a transaction, add some images to a Volume, commit.

        //  test 1 - running under debugger, txn timed out. But was wrong approach anyway.
        // TxnTest(client, "Volumes/Volume1", "/Users/jimk/dev/tmp/Projects/fcrepo-sandbox/sample-images"
        // TxnTest(client, "Volumes/Volume2", "/Users/jimk/dev/tmp/Projects/fcrepo-sandbox/sample-images2");


        // Add an unnamed container to the Volumes archival group
        // client.AddContainer("Volumes", LdpContainer.DirectContainer,null,"W12345-I54321");

        // Add an unnamed container to the basic container:
//        String new_container = client.AddContainer(null, LdpContainer.DirectContainer, "urn:bdrc:arc:bdr:W1345:v1" +
//                ":W12345-I54321", null);
        // _logger.debug("Created {} ", new_container);

//        _logger.debug("Adding images to {} ", new_container);
//        Path source_dir = Paths.get("/Users/jimk/dev/tmp/Projects/fcrepo-sandbox/sample-images");
//        AddImages(new_container, client, source_dir);

        // Scenario 7: runs against an fcreo-docker with autoversioning off
        // Note we don't use the same images as before, as fcrepo wants to de-duplicate.
        //AddImages("Volumes/Volume3",client, Paths.get("/Users/jimk/dev/tmp/Projects/fcrepo-sandbox/sample-images3"));

        // URI versionURI = client.AddVersion("Volumes/Volume3/", "Your Message Here - Slug");

        // ****AAARRGH
        //❯ curl -X POST -H "Accept: text/turtle" -u fedoraAdmin:fedoraAdmin http://localhost:8088/fcrepo/rest/Volumes/Volume3/fcr:versions
        //Resource <info:fedora/Volumes/Volume3> is contained in Archival Group <Optional[info:fedora/Volumes]> and cannot be versioned directly. Version the Archival Group instead.%

        // Scenario 8. Create a new Direct Container.
        // add DirectContainers to it
        // Add images to one of the direct containers
        // Version the container.
        String NagRoot = "Non_ArchivalGroup_Volumes";
        String[] NagVolumes = {"NAG_Volume1", "NAG_Volume2", "NAG_Volume3"};


        //      Add the root volume
//        client.AddContainerSlug(null, LdpContainer.DirectContainer,NagRoot,
//                "Programmatically created");
//        AddVolumes(client,NagRoot, NagVolumes);
//        String curVolume = String.format("%s/%s", NagRoot, NagVolumes[0]);
//
//        AddImages(curVolume, client, Paths.get("/Users/jimk/dev/tmp/Projects" +
//                "/fcrepo-sandbox/sample-images3"));
        URI versionURI = client.AddVersion(String.format("%s/%s", NagRoot, NagVolumes[2]), "Your Message Here - Slug");


    }

    /**
     * This approach needs redesign. The transaction id is supposed to be inserted in the
     * resource url:  http://yourHost:port/fcrepo/rest/fcr:tx/895dac11-2636-4807-b6de-f3ae5bfc6d3e/ + resource
     * And also, this was not the way to inhibit versioning. You have to run the server with
     * autoversioning disabled.
     *
     * @param client         BdrcRepoclient
     * @param parentResource String representing container
     * @param imageSource    directory containing images
     * @throws BdrcRepoException
     */
    public static <parentResource> void TxnTest(BdrcRepoClient client, String parentResource, String imageSource) throws BdrcRepoException {
        URI txnLocation = null;
        boolean txnFail = false;

        // leftover from debugging
        // client.txnRollback(URI.create("http://localhost:8088/fcrepo/rest/fcr:tx/895dac11-2636-4807-b6de-f3ae5bfc6d3e/"));
        try {
            txnLocation = client.txnBegin();
            String pc = client.getEndpoint() + parentResource;
            AddImages(pc, client, Paths.get(imageSource));
        } catch (BdrcRepoException e) {
            txnFail = true;
            _logger.error(e.getLocalizedMessage());
        } finally {
            if (txnFail) {
                // Rollback the transaction
                client.txnRollback(txnLocation);
            } else {
                // Commit the transaction
                client.txnCommit(txnLocation);
            }
        }
    }
}
