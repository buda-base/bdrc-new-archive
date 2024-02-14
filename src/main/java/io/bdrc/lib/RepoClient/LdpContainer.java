package io.bdrc.lib.RepoClient;

// create an enum of strings that represeent <http://www.w3.org/ns/ldp# types
public enum LdpContainer {
    DirectContainer("<http://www.w3.org/ns/ldp#DirectContainer>"),
    IndirectContainer("<http://www.w3.org/ns/ldp#IndirectContainer>"),
    BasicContainer("<http://www.w3.org/ns/ldp#BasicContainer>"),
    Resource("<http://www.w3.org/ns/ldp#Resource>");

    private final String _repr;

    LdpContainer(String representation) {
        this._repr = representation;
    }

    @Override
    public String toString() {
        return _repr;
    }
}
