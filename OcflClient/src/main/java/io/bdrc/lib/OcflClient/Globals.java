package io.bdrc.lib.OcflClient;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Globals {
    public static final Path OCFL_PROJECT_ROOT =
            Paths.get(System.getProperty("user.home"),"dev","tmp","Projects","OCFL");
    public static Path getOCFL_ROOT(String layoutName) {
        return OCFL_PROJECT_ROOT.resolve(layoutName);
    }

    public static Path getOcflRepo(Path home) {
        return home.resolve("ocfl-repo");
    }

    public static Path getOcflWork(Path home) {
        return home.resolve("ocfl-work");
    }

    public static String SampleOutput = "sample-output";
    public static String SampleWorkSource = "sample-work-source";
}
