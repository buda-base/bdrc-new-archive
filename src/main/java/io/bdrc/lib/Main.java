package io.bdrc.lib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;


import org.fcrepo.client.FcrepoClient;
import org.fcrepo.client.FcrepoResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class Main {
    public Main() {
    }

    private static final Logger _logger = LoggerFactory.getLogger(BdrcRepoClient.class);

    public static void AddArchiveGroup() {
        // Pause this for a while, see ArchivalGroupFactory.java for sample output
        // ArchivalGroup  = ArchivalGroupFactory.get(fcRestEndpoint, "Volumes");
        // ArchivalGroup volumes = ArchivalGroupFactory.create(fcRestEndpoint, "Volumes", "Volumes");
        // Create a direct container
    }



    public static void main(String[] args) throws URISyntaxException {

        // Add three containers to the "Volumes" repository
        // In each container, add three image files.

        URI fcRestEndpoint = new URI("http://sattva:8080/rest/");
        BdrcRepoClient client = new BdrcRepoClient(fcRestEndpoint,"application/n-triples");
        String VolsArchiveGroup = client.GetResource("Volumes");
        _logger.error(VolsArchiveGroup);
        // Add a direct container to the Volumes archival group
        String volumes = client.CreateContainer("Volumes", "Volumes");
        _logger.error(volumes);
        // Add a direct container to the Volumes archival group
        String volumes2 = client.CreateContainer("Volumes", "Volumes2");
        _logger.error(volumes2);
        // Add a direct container to the Volumes archival group
        String volumes3 = client.CreateContainer("Volumes", "Volumes3");
        _logger.error(volumes3);
    }
}