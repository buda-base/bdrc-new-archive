package io.bdrc.lib.OcflClient;

import io.ocfl.api.model.ObjectVersionId;
import io.ocfl.core.OcflRepositoryBuilder;
import io.ocfl.core.extension.storage.layout.config.HashedNTupleLayoutConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.bdrc.lib.OcflClient.Globals.*;
import static io.bdrc.lib.OcflClient.OcflPrimitives.*;

/**
 * prototype ocfl client
 */

public class RepoNHash {
    private static final String layoutName = "RepoNHash";
    private static final String sampleObjectId = "W1PD177852";

    // Just for thrills. Used BOTH to locate input and output
    private static final String sampleObjectDiskName = sampleObjectId;

    public static void main(String[] args) throws IOException
    {
        Path ocflHome = getOCFL_ROOT(layoutName);
        Path repoDir = rebuildPath(getOcflRepo(ocflHome));
        Path workDir = rebuildPath(getOcflWork(ocflHome));

        var repo = new OcflRepositoryBuilder()
                .defaultLayoutConfig(new HashedNTupleLayoutConfig())
                .storage(storage -> storage.fileSystem(repoDir))
                .workDir(workDir)
                .build();

// jimk 2024-02-21: Use standard actions
        // No prefix in this object type

        populate_work(repo, sampleObjectId, Paths.get(OCFL_PROJECT_ROOT.toString(), "sample",
                SampleWorkSource, sampleObjectDiskName));


        // dump each object into a dir specific to its layout name
        Path sampleOut = Paths.get(OCFL_PROJECT_ROOT.toString(), "sample", SampleOutput,
                layoutName, sampleObjectDiskName);

        downloadContent(repo, ObjectVersionId.head(sampleObjectId), sampleOut);


    }
}
