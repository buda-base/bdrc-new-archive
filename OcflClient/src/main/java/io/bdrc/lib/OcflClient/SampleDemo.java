package io.bdrc.lib.OcflClient;

import io.ocfl.api.model.ObjectVersionId;
import io.ocfl.api.model.VersionInfo;
import io.ocfl.core.OcflRepositoryBuilder;
import io.ocfl.core.extension.storage.layout.config.FlatLayoutConfig;
import io.ocfl.core.extension.storage.layout.config.NTupleOmitPrefixStorageLayoutConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.bdrc.lib.OcflClient.Globals.*;
import static io.bdrc.lib.OcflClient.OcflPrimitives.populate_manual;
import static io.bdrc.lib.OcflClient.OcflPrimitives.rebuildPath;

public class SampleDemo {

    private static final String layoutName = "SampleFlatLayout";
    private static final String sampleObjectId = "o1";
    private static final String O_PREFIX = "bdr:";

    private static final Path SAMPLE_ROOT = OCFL_PROJECT_ROOT.resolve("sample");

    private static final String sampleSource = "sample-source";
    private static final String sampleUpdates = "sample-updates";
    private static final String sampledest = "sample-dest";

    public static void main(String[] args) throws IOException {
        Path ocflHome = rebuildPath(getOCFL_ROOT(layoutName));
        Path repoDir = rebuildPath(getOcflRepo(ocflHome));
        Path workDir = rebuildPath(getOcflWork(ocflHome));

        var repo = new OcflRepositoryBuilder()
                .defaultLayoutConfig(new FlatLayoutConfig())
                .storage(storage -> storage.fileSystem(repoDir))
                .workDir(workDir)
                .build();

        populate_manual(repo, sampleObjectId, SAMPLE_ROOT.resolve(sampleSource),
                SAMPLE_ROOT.resolve(sampleUpdates), SAMPLE_ROOT.resolve(sampledest));


    }

}
