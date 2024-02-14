package io.bdrc.lib.OcflClient;

import io.ocfl.api.OcflOption;
import io.ocfl.api.OcflRepository;
import io.ocfl.api.model.ObjectVersionId;
import io.ocfl.api.model.OcflObjectVersion;
import io.ocfl.api.model.VersionInfo;
import io.ocfl.core.OcflRepositoryBuilder;
import io.ocfl.core.extension.storage.layout.config.NTupleOmitPrefixStorageLayoutConfig;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * prototype ocfl client
 *
 */

public class RepoNTupleOmitPrefix
{
    // Try 1: standard HashNTuple layout
    private static final String OCFL_ROOT = "/Users/jimk/dev/tmp/Projects/OCFL/Hash_ext007";

    private static final String OCFL_SRC_ROOT = "/Users/jimk/dev/tmp/Projects/OCFL" ;

    private static final String O_PREFIX=  "bdr:";
    public static void main( String[] args )
    {

        var repoDir = Paths.get(OCFL_ROOT,"ocfl-repo"); // This directory contains the OCFL storage root.
        var workDir = Paths.get(OCFL_ROOT,"ocfl-work"); // This directory is used to assemble OCFL versions. It cannot
        // be within the OCFL storage root.

        var repo = new OcflRepositoryBuilder()
                .defaultLayoutConfig(new NTupleOmitPrefixStorageLayoutConfig().setDelimiter(O_PREFIX))
                .storage(storage -> storage.fileSystem(repoDir))
                .workDir(workDir)
                .build();

//        populate_work(repo, "bdr:W1PD177582", Paths.get(OCFL_SRC_ROOT,"object-out-dir","bdr:W1PD177852"));
        // populate_manual(repo);
        OcflObjectVersion headObjectVersion = repo.getObject(ObjectVersionId.head("bdr:W1PD177582"));
        var frelm = repo.getObject(headObjectVersion.getObjectVersionId());
//        repo.getObject(headObjectVersion.getObjectVersionId(), Paths.get(OCFL_SRC_ROOT, "W1PD177852"));



    }

    /**
     * Populate the repository with a work
     * @param repo
     */
    private static void populate_work(final OcflRepository repo, String objectId, Path sourcePath) {


                repo.putObject(ObjectVersionId.head(objectId), sourcePath,
                new VersionInfo().setMessage(
                "initial commit"));

                        // Haha - what an ulatramaroon - you have to populate a version before you can update it. Getting the resuts
                        // of this hoerks.
//        repo.updateObject(ObjectVersionId.head(objectId), new VersionInfo().setMessage("load archives"), updater -> {
//            updater.addPath(sourcePath, "", OcflOption.OVERWRITE);
//        });
    }

    private static void populate_manual(final OcflRepository repo) {
        String o1Id = O_PREFIX+"o1";
        repo.putObject(ObjectVersionId.head(o1Id), Paths.get(OCFL_SRC_ROOT, "object-out-dir"),
                new VersionInfo().setMessage(
                "initial commit"));

        // This works!
        repo.getObject(ObjectVersionId.head(o1Id), Paths.get(OCFL_SRC_ROOT, "object-in-dir"));


        repo.updateObject(ObjectVersionId.head(o1Id), new VersionInfo().setMessage("update"), updater -> {
            updater.addPath(Paths.get(OCFL_SRC_ROOT,"updates","I1KG183680008.tif"), "file2", OcflOption.OVERWRITE)
                    .removeFile("file1")
                    .addPath(Paths.get(OCFL_SRC_ROOT,"updates","I1KG183680007.tif"), "dir1/file3", OcflOption.OVERWRITE);
        });

// Contains object details and lazy-load resource handles
//        OcflObjectVersion objectVersion = repo.getObject(ObjectVersionId.version("o1", "v1"));
//
//
//        System.out.println( objectVersion.toString() );

        //Does this get the head object version?
        // OcflObjectVersion headObjectVersion = repo.getObject(ObjectVersionId.head("o1"));
        // Get the files
        repo.getObject(ObjectVersionId.head(o1Id), Paths.get(OCFL_SRC_ROOT, "object-in-dir-head"));

        // Get the v2 files
        repo.getObject(ObjectVersionId.version(o1Id,"v2"), Paths.get(OCFL_SRC_ROOT, "object-in-dir-v2"));


//        OcflObjectVersion v2ObjectVersion = repo.getObject(ObjectVersionId.version("o1", "v2"));
//        System.out.println( v2ObjectVersion.toString() );
//
//        Collection< OcflObjectVersionFile> headObjectFiles = v2ObjectVersion.getFiles();
//        System.out.println( headObjectFiles.toString() );
    }
}
