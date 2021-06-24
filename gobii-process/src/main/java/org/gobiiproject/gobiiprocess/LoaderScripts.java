package org.gobiiproject.gobiiprocess;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LoaderScripts {

    private String rootDir;

    public LoaderScripts(String rootDir) {
        this.rootDir = rootDir;
    }


    public String getHdf5BinariesPath() {
        return Paths.get(getPath(), "hdf5", "bin").toString();
    }

    public String getPath() {
        return Paths.get(rootDir, "loaders").toString();
    }

    public String getIfl() {
        return Paths.get(
            getPath(),
            "postgres",
            "gobii_ifl",
            "gobii_ifl.py").toString();

    }

    public String getLgDuplicatesScript() {
        return Paths.get(getPath(), "LGduplicates.py").toString();
    }

    public String getPathToHdf5Files(String cropType) {
        Path cropPath = Paths.get(rootDir + "crops/" + cropType.toLowerCase());
        return cropPath.toString() + "/hdf5/";
    }
}
