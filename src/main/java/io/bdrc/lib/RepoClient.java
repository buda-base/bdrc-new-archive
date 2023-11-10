package io.bdrc.lib;

import org.fcrepo.client.*;
public class RepoClient extends FcrepoClient {

    protected RepoClient(final String username, final String password, final String host, final Boolean throwExceptionOnFailure) {
        super(username, password, host, throwExceptionOnFailure);
    }
}
