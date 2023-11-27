package io.bdrc.lib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;


public class Main {
    public Main() {
    }

    private static final Logger _logger = LoggerFactory.getLogger(BdrcRepoClient.class);

    public static ArchivalGroup AddArchiveGroup(URI fcRestEndpoint) {
        // Pause this for a while, see ArchivalGroupFactory.java for sample output
        // ArchivalGroup  = ArchivalGroupFactory.get(fcRestEndpoint, "Volumes");
        return ArchivalGroupFactory.create(fcRestEndpoint, "Volumes", "Volumes");
    }
    
    public static void CreateVolumes(BdrcRepoClient client) {
        // Add a direct container to the Volumes archival group
        String volumes1 = client.AddContainer("Volumes", "Volume1");
        _logger.error(volumes1);
        String volumes2 = client.AddContainer("Volumes", "Volume2");
        _logger.error(volumes2);
        // Add a direct containedr to the Volumes archival group
        String volumes3 = client.AddContainer("Volumes", "Volume3");
        _logger.error(volumes3);
    }



    public static void main(String[] args) throws URISyntaxException {

        // Add an Archival group to the repository

        // Add three containers to the "Volumes" repository
        // In each container, add three image files.

        URI fcRestEndpoint = new URI("http://sattva:8080/rest/");
        // BdrcRepoClient client = new BdrcRepoClient(fcRestEndpoint,"application/n-triples");
        BdrcRepoClient client = new BdrcRepoClient(fcRestEndpoint,"text/turtle");

        String VolsArchiveGroup = client.GetResource("Volumes");
        _logger.info(VolsArchiveGroup);
        for (int i = 1 ; i <= 3; i++) {
            String volumei = client.GetResource(String.format("Volumes/Volume%d", i ));
            _logger.info(volumei);
        }

        
    }
}