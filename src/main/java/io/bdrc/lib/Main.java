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

    public static ArchivalGroup AddArchiveGroup(URI fcRestEndpoint) {
        // Pause this for a while, see ArchivalGroupFactory.java for sample output
        // ArchivalGroup  = ArchivalGroupFactory.get(fcRestEndpoint, "Volumes");
        return ArchivalGroupFactory.create(fcRestEndpoint, "Volumes", "Volumes");
    }

    public static void CreateVolumes(BdrcRepoClient client) {
        // Add a direct container to the Volumes archival group
        // String volumes1 = client.AddContainer("Volumes", LdpContainer.DirectContainer,null,"Volume 1");
//        _logger.error(volumes1);
//        String volumes2 = client.AddContainer("Volumes", "Volume2");
//        _logger.error(volumes2);
//        // Add a direct container to the Volumes archival group
//        String volumes3 = client.AddContainer("Volumes", "Volume3");
//        _logger.error(volumes3);
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

        URI fcRestEndpoint = new URI("http://localhost:8080/fcrepo/rest/");
        // BdrcRepoClient client = new BdrcRepoClient(fcRestEndpoint,"application/n-triples");
        BdrcRepoClient client = new BdrcRepoClient(fcRestEndpoint, "text/turtle");

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
        String new_container = client.AddContainer(null, LdpContainer.DirectContainer, "urn:bdrc:arc:bdr:W1345:v1" +
                ":W12345-I54321", null);
        // _logger.debug("Created {} ", new_container);

        _logger.debug("Adding images to {} ", new_container);
        Path source_dir = Paths.get("/Users/jimk/dev/tmp/Projects/fcrepo-sandbox/sample-images");
        AddImages(new_container, client, source_dir);


    }
}
