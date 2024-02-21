package io.bdrc.lib.OcflClient;

import io.ocfl.api.model.ObjectVersionId;
import io.ocfl.core.OcflRepositoryBuilder;
import io.ocfl.core.extension.storage.layout.config.FlatLayoutConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.bdrc.lib.OcflClient.Globals.*;
import static io.bdrc.lib.OcflClient.OcflPrimitives.*;

/**
 * prototype ocfl client
 */

public class RepoFlat {
    private static final String layoutName = "FlatLayout";
    private static final String sampleObjectId = "W1PD177852";

    // Just for thrills. Used BOTH to locate input and output
    private static final String sampleObjectDiskName = sampleObjectId;


    public static void main(String[] args) throws IOException
    {

        Path ocflHome = rebuildPath( getOCFL_ROOT(layoutName));
        Path repoDir = rebuildPath(getOcflRepo(ocflHome));
        Path workDir = rebuildPath(getOcflWork(ocflHome));

        var repo = new OcflRepositoryBuilder()
                .defaultLayoutConfig(new FlatLayoutConfig())
                .storage(storage -> storage.fileSystem(repoDir))
                .workDir(workDir)
                .build();

        populate_work(repo, sampleObjectId, Paths.get(OCFL_PROJECT_ROOT.toString(), "sample",
                SampleWorkSource, sampleObjectDiskName));


        Path sampleOut = Paths.get(OCFL_PROJECT_ROOT.toString(), "sample", SampleOutput,
                layoutName, sampleObjectDiskName);

        downloadContent(repo, ObjectVersionId.head(sampleObjectId), sampleOut);
    }
}
