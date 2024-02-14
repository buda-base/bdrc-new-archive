package io.bdrc.lib.RepoClient;

import java.net.URI;

/**
 * Created by bdrc-jimk on 11/19/2023
 * CRUD for  FCRepo archival group
 */
public class ArchivalGroup {

        private String _name;
        private String _description;

        private URI _location;


        public ArchivalGroup(String name, String description, URI location) {
            _name = name;
            _description = description;
            _location = location;
        }

        public String getName() {
            return _name;
        }

        public String getDescription() {
            return _description;
        }

        public URI getLocation() {
            return _location;
        }

        public String toString() {
            return "ArchivalGroup: " + _name + " " + _description + " " + _location;
        }
}
