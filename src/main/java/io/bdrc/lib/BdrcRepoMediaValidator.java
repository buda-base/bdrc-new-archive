package io.bdrc.lib;


import java.util.List;

/**
 * Created by bdrc-jimk on 11/19/2023
 * Validates a given string against a set of media types
 */
public class BdrcRepoMediaValidator {

    // +1 copilot
    private final static List<String> _default_mediaTypes = List.of(
            "application/ld+json",
            "text/turtle",
            "application/n-triples",
            "application/rdf+xml");

    private final List<String> _mediaTypes;


    public BdrcRepoMediaValidator(List<String> mediaTypes) {

        _mediaTypes = mediaTypes;
    }

    public BdrcRepoMediaValidator() {

        _mediaTypes = _default_mediaTypes;
    }


    public boolean validate(String mediaType) {
        for (String mt : _mediaTypes) {
            if (mt.equals(mediaType)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getDefaultMediaTypes() {
        return _default_mediaTypes;
    }
}