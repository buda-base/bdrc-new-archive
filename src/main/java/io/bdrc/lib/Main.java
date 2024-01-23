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

    public static void CreateVolumes( BdrcRepoClient client, ArchivalGroup archivalGroup, String[] volumeNames) {
        // Add a direct container to the Volumes archival group

        for (String volumeName : volumeNames) {
            String volume = client.AddContainer(archivalGroup.getName(), LdpContainer.DirectContainer, volumeName,
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
                            _logger.debug(" contentType {} " , contentType);
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


    public static void main(String[] args) throws URISyntaxException {

        // Add an Archival group to the repository

        // Add three containers to the "Volumes" repository
        // In each container, add three image files.

        URI fcRestEndpoint = new URI("http://localhost:8088/fcrepo/rest/");
        // BdrcRepoClient client = new BdrcRepoClient(fcRestEndpoint,"application/n-triples");
        BdrcRepoClient client = new BdrcRepoClient(fcRestEndpoint, "text/turtle");

        // Scenario 2: Add an Archival group, named "volumes"
        // Add three containers
        // Add three images to each container.
        // In between, monitor the repository with the Fedora UI
        _logger.info("Howdy");
        _logger.error("Howdy");
        _logger.debug("Howdy");
        ArchivalGroup volumesAG = AddArchiveGroup(fcRestEndpoint, "Volumes","Archival Group of Image Volumes. Code " +
                "created");
        _logger.debug("Created {} ", volumesAG);
        CreateVolumes(client, volumesAG, new String[] {"Volume1", "Volume2", "Volume3"});



//        String VolsArchiveGroup = client.GetResource("Volumes");
//        _logger.info(VolsArchiveGroup);
//        for (int i = 1 ; i <= 3; i++) {
//            String volumei = client.GetResource(String.format("Volumes/Volume%d", i ));
//            _logger.info(volumei);
//        }

        // This is the URL of an anonymous container
	//  String imagesContainer = "http://sattva:8080/rest/76369565-4478-40a6-9e64-d236590a27b7";
        // _logger.debug("Using existing {} ", imagesContainer);

        // Add an unnamed container to the Volumes archival group
        // client.AddContainer("Volumes", LdpContainer.DirectContainer,null,"W12345-I54321");

        // Add an unnamed container to the basic container:
//        String new_container = client.AddContainer(null, LdpContainer.DirectContainer, "urn:bdrc:arc:bdr:W1345:v1" +
//                ":W12345-I54321", null);
        // _logger.debug("Created {} ", new_container);

//        _logger.debug("Adding images to {} ", new_container);
//        Path source_dir = Paths.get("/Users/jimk/dev/tmp/Projects/fcrepo-sandbox/sample-images");
//        AddImages(new_container, client, source_dir);


    }
}
