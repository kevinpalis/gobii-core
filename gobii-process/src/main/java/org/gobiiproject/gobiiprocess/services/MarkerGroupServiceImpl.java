package org.gobiiproject.gobiiprocess.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiiprocess.spring.SpringContextLoaderSingleton;
import org.gobiiproject.gobiisampletrackingdao.MarkerGroupDao;

import javax.transaction.Transactional;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;

@Transactional
public class MarkerGroupServiceImpl implements MarkerGroupService {

    public boolean addMarkerGroups(File markerGroupDigestFile) {

        BufferedReader br;
        String header;
        String line;
        Map<String, Integer> mapFieldNamePosition;

        try {



            Map<String, Map<String, List<String>>> markerGroupsTobeAdded = new HashMap<>();

            // Get Headers of digest file
            br = new BufferedReader(new FileReader(markerGroupDigestFile));
            header = br.readLine();
            mapFieldNamePosition = getFileCoulumnNamePosition(header);
            br.close();

            File digestDir = markerGroupDigestFile.getParentFile();
            String digestDirPath = digestDir.getPath();

            // File to save marker ids
            String markerGroupsMarkerIdFilePath =
                Paths.get(digestDirPath, "marker_group.marker_ids").toString();
            File markerGroupMarkerIdFile = new File(markerGroupsMarkerIdFilePath);
            markerGroupMarkerIdFile.createNewFile();
            markerGroupMarkerIdFile.setWritable(true, false);

            String markerGroupUploadFile =
                Paths.get(digestDirPath, "marker_group.upload").toString();

            MarkerGroupDao markerGroupDao =
                SpringContextLoaderSingleton.getInstance().getBean(MarkerGroupDao.class);

            Long mappedIdsCount = null;

            // check platform columns found
            if(mapFieldNamePosition.containsKey("platform_id")) {
                mappedIdsCount = markerGroupDao
                    .mapMarkerIdsForMarkerNamesAndPlatformIds(
                        markerGroupDigestFile.getAbsolutePath(),
                        markerGroupsMarkerIdFilePath);
            }
            else if(mapFieldNamePosition.containsKey("platform_name")) {
                mappedIdsCount = markerGroupDao
                    .mapMarkerIdsForMarkerNamesAndPlatformNames(
                        markerGroupDigestFile.getAbsolutePath(),
                        markerGroupsMarkerIdFilePath);
            }
            else {
                throw new GobiiException(GobiiStatusLevel.ERROR,
                    GobiiValidationStatusType.BAD_REQUEST,
                    "platform column not found");
            }

            // Map marker id and favourable alleles to marker groups
            br = new BufferedReader(new FileReader(markerGroupsMarkerIdFilePath));
            header = br.readLine();
            mapFieldNamePosition = getFileCoulumnNamePosition(header);
            String markerGroupName;
            String markerId;
            String favAlleles;
            List<String> favAllelesList;

            while ((line = br.readLine()) != null) {
                String[] values = line.split("\t");
                markerGroupName = values[mapFieldNamePosition.get("marker_group_name")];
                markerId = values[mapFieldNamePosition.get("marker_id")];
                favAlleles = values[mapFieldNamePosition.get("fav_alleles")];
                if(!markerGroupsTobeAdded.containsKey(markerGroupName)) {
                    markerGroupsTobeAdded.put(markerGroupName, new HashMap<>());
                }
                favAllelesList = new ArrayList<>();
                for(String allele : favAlleles.split(",")) {
                    favAllelesList.add("\"" + allele + "\"");
                }
                markerGroupsTobeAdded
                    .get(markerGroupName)
                    .put("\""+markerId+"\"", favAllelesList);
            }
            br.close();

            // Write marker group upload file
            FileWriter writer = new FileWriter(markerGroupUploadFile);
            ObjectMapper mapper = new ObjectMapper();

            writer.write("marker_group_name" + "\t" + "markers" + System.lineSeparator());

            for(String markerGroup : markerGroupsTobeAdded.keySet()) {
                String opLine = markerGroup +
                    "\t" + "\"" +
                    mapper.writeValueAsString(
                        markerGroupsTobeAdded.get(markerGroup)).replace("\\", "") + "\"" +
                    System.lineSeparator();
                writer.write(opLine);
            }
            writer.close();

            // Load marker groups to database.
            Long markerGroupCount = markerGroupDao
                .uploadMarkerGroupsFromFile(markerGroupUploadFile);
            System.out.println(markerGroupCount);
        }
        catch (NullPointerException | IOException e) {
            throw new GobiiException("");
        }

        return true;
    }


    private Map<String, Integer> getFileCoulumnNamePosition(String header) {
        String[] headerColumns = header.split("\t");
        Map<String, Integer> mapFieldNamePosition = new HashMap<>();
        for(int i = 0; i < headerColumns.length; i++) {
            mapFieldNamePosition.put(headerColumns[i], i);
        }
        return mapFieldNamePosition;
    }

}
