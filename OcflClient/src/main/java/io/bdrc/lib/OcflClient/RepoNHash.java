package io.bdrc.lib.OcflClient;

import io.ocfl.api.model.ObjectVersionId;
import io.ocfl.api.model.OcflObjectVersion;
import io.ocfl.core.OcflRepositoryBuilder;
import io.ocfl.core.extension.storage.layout.config.HashedNTupleLayoutConfig;

import java.nio.file.Paths;

/**
 * prototype ocfl client
 *
 */

public class repo1_hash_n
{
    // Try 1: standard HashNTuple layout
    private static final String OCFL_ROOT = "/Users/jimk/dev/tmp/Projects/OCFL/HashNTuple";

    private static final String OCFL_SRC_ROOT = "/Users/jimk/dev/tmp/Projects/OCFL" ;
    public static void main( String[] args )
    {
        var repoDir = Paths.get(OCFL_ROOT,"ocfl-repo"); // This directory contains the OCFL storage root.
        var workDir = Paths.get(OCFL_ROOT,"ocfl-work"); // This directory is used to assemble OCFL versions. It cannot
        // be within the OCFL storage root.

        var repo = new OcflRepositoryBuilder()
                .defaultLayoutConfig(new HashedNTupleLayoutConfig())
                .storage(storage -> storage.fileSystem(repoDir))
                .workDir(workDir)
                .build();


//        repo.putObject(ObjectVersionId.head("o1"), Paths.get(OCFL_SRC_ROOT, "object-out-dir"),
//                new VersionInfo().setMessage(
//                "initial commit"));

        // This works!
        // repo.getObject(ObjectVersionId.head("o1"), Paths.get(OCFL_SRC_ROOT, "object-in-dir"));


//        repo.updateObject(ObjectVersionId.head("o1"), new VersionInfo().setMessage("update"), updater -> {
//            updater.addPath(Paths.get(OCFL_SRC_ROOT,"updates","I1KG183680008.tif"), "file2", OcflOption.OVERWRITE)
//                    .removeFile("file1")
//                    .addPath(Paths.get(OCFL_SRC_ROOT,"updates","I1KG183680007.tif"), "dir1/file3", OcflOption.OVERWRITE);
//        });

// Contains object details and lazy-load resource handles
//        OcflObjectVersion objectVersion = repo.getObject(ObjectVersionId.version("o1", "v1"));
//
//
//        System.out.println( objectVersion.toString() );

        //Does this get the head object version?
        OcflObjectVersion headObjectVersion = repo.getObject(ObjectVersionId.head("o1"));
        // Get the files
        repo.getObject(ObjectVersionId.head("o1"), Paths.get(OCFL_SRC_ROOT, "object-in-dir-head"));

        // Get the v2 files
        repo.getObject(ObjectVersionId.version("o1","v2"), Paths.get(OCFL_SRC_ROOT, "object-in-dir-v2"));


//        OcflObjectVersion v2ObjectVersion = repo.getObject(ObjectVersionId.version("o1", "v2"));
//        System.out.println( v2ObjectVersion.toString() );
//
//        Collection< OcflObjectVersionFile> headObjectFiles = v2ObjectVersion.getFiles();
//        System.out.println( headObjectFiles.toString() );


    }
}
