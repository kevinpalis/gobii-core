package org.gobiiproject.gobiiprocess.services;

import org.gobiiproject.gobiimodel.config.GobiiException;

import java.io.File;

public interface MarkerGroupService {

    public boolean addMarkerGroups(File markerGroupDigestFile) throws GobiiException;
}
