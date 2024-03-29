package io.bdrc.lib.OcflClient;

import io.ocfl.api.model.ObjectVersionId;
import io.ocfl.core.OcflRepositoryBuilder;
import io.ocfl.core.extension.storage.layout.config.NTupleOmitPrefixStorageLayoutConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.bdrc.lib.OcflClient.Globals.*;
import static io.bdrc.lib.OcflClient.OcflPrimitives.*;

/**
 * prototype ocfl client
 */

public class RepoNTupleOmitPrefix {
    private static final String O_PREFIX = "bdr:";

    private static final String layoutName = "RepoNTupleOmitPrefix";
    private static final String sampleObjectId = O_PREFIX + "W1PD177852";

    // This is the ONE case where the disk copy is not the object name
    private static final String sampleObjectDiskName = "W1PD177852";

    public static void main(String[] args) throws IOException
    {

        Path ocflHome = getOCFL_ROOT(layoutName);
        Path repoDir = rebuildPath(getOcflRepo(ocflHome));
        Path workDir = rebuildPath(getOcflWork(ocflHome));


        var repo = new OcflRepositoryBuilder()
                .defaultLayoutConfig(new NTupleOmitPrefixStorageLayoutConfig().setDelimiter(O_PREFIX))
                .storage(storage -> storage.fileSystem(repoDir))
                .workDir(workDir)
                .build();

        populate_work(repo, sampleObjectId, Paths.get(OCFL_PROJECT_ROOT.toString(), "sample",
                SampleWorkSource, sampleObjectDiskName));


        // dump each object into a dir specific to its layout name
        Path sampleOut = Paths.get(OCFL_PROJECT_ROOT.toString(), "sample", SampleOutput,
                layoutName, sampleObjectDiskName);

        downloadContent(repo, ObjectVersionId.head(sampleObjectId), sampleOut);

    }


}
