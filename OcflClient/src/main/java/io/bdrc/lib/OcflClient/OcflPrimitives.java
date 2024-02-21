package io.bdrc.lib.OcflClient;

import io.ocfl.api.OcflOption;
import io.ocfl.api.OcflRepository;
import io.ocfl.api.model.ObjectVersionId;
import io.ocfl.api.model.OcflObjectVersion;
import io.ocfl.api.model.VersionInfo;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OcflPrimitives {
    public static void downloadContent(final OcflRepository repo, final ObjectVersionId objectVersionId,
                                       Path destPath)
    {
        // delete and recreate the destination directory. Don't delete any parents
        try {
            rebuildPath(destPath);

            // Now delete the destination directory
            File destDir = destPath.toFile();
            FileUtils.deleteDirectory(destDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        repo.getObject(objectVersionId, destPath);
    }

    public static Path rebuildPath(final Path thePath) throws IOException {
        File thePathFile = thePath.toFile();
        try {
            if (thePathFile.exists()) {
                FileUtils.deleteDirectory(thePathFile);
            }
            Files.createDirectories(thePath);
            return thePath;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }


    /**
     * Populate the repository with a directory
     *
     * @param repo       - repo object
     * @param objectId   - repo identifier
     * @param sourcePath - path to original sources
     */
    public static void populate_work(final OcflRepository repo, String objectId, Path sourcePath) {


        repo.putObject(ObjectVersionId.head(objectId), sourcePath,
                new VersionInfo().setMessage(
                        "initial commit"));

        // Haha - what an ulatramaroon - you have to populate a version before you can update it. Getting the resuts
        // of this hoerks.
//        repo.updateObject(ObjectVersionId.head(objectId), new VersionInfo().setMessage("load archives"), updater -> {
//            updater.addPath(sourcePath, "", OcflOption.OVERWRITE);
//        });
    }

    /**
     * Populate the repository with a work, update the work, and download some test versions
     *
     * @param repo       the repository object
     * @param objectId   repo identifier
     * @param sourcePath - path to original sources
     * @param dumpParent - parent directory name where source and updates reside
     */
    public static void populate_manual(final OcflRepository repo, String objectId, Path sourcePath,
                                       Path updatesSource,
                                       Path dumpParent)
    {
        String o1Id = objectId;
        repo.putObject(ObjectVersionId.head(o1Id), sourcePath,
                new VersionInfo().setMessage(
                        "initial commit"));

        // This works!
        downloadContent(repo, ObjectVersionId.head(o1Id), dumpParent.resolve("object-in-dir"));

        repo.updateObject(ObjectVersionId.head(o1Id), new VersionInfo().setMessage("update"), updater -> {
            updater.addPath(updatesSource.resolve("I1KG183680008.tif"), "file2", OcflOption.OVERWRITE)
                    .removeFile("file1")
                    .addPath(updatesSource.resolve("I1KG183680007.tif"), "dir1/file3", OcflOption.OVERWRITE);
        });

// Contains object details and lazy-load resource handles
//        OcflObjectVersion objectVersion = repo.getObject(ObjectVersionId.version("o1", "v1"));
//
//
//        System.out.println( objectVersion.toString() );

        //Does this get the head object version?
        // OcflObjectVersion headObjectVersion = repo.getObject(ObjectVersionId.head("o1"));

        // Get the files
        // Note that this will overwrite the previous download
        downloadContent(repo, ObjectVersionId.head(o1Id), dumpParent.resolve("object-in-dir-head"));
        // repo.getObject(ObjectVersionId.head(o1Id), Paths.get(dumpParent, "object-in-dir-head"));

        // Get the v2 files
        downloadContent(repo, ObjectVersionId.version(o1Id, "v2"), dumpParent.resolve("object-in-dir-v2"));


//        OcflObjectVersion v2ObjectVersion = repo.getObject(ObjectVersionId.version("o1", "v2"));
//        System.out.println( v2ObjectVersion.toString() );
//
//        Collection< OcflObjectVersionFile> headObjectFiles = v2ObjectVersion.getFiles();
//        System.out.println( headObjectFiles.toString() );
    }
}
