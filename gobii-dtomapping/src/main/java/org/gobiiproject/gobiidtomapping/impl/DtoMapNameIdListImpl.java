package org.gobiiproject.gobiidtomapping.impl;

import org.apache.commons.lang.math.NumberUtils;
import org.gobiiproject.gobiidao.resultset.access.*;
import org.gobiiproject.gobiidtomapping.DtoMapNameIdList;
import org.gobiiproject.gobiimodel.dto.container.NameIdDTO;
import org.gobiiproject.gobiimodel.dto.container.NameIdListDTO;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Phil on 4/6/2016.
 */
public class DtoMapNameIdListImpl implements DtoMapNameIdList {

    Logger LOGGER = LoggerFactory.getLogger(DtoMapNameIdListImpl.class);


    @Autowired
    private RsAnalysisDao rsAnalysisDao = null;

    @Autowired
    private RsContactDao rsContactDao = null;

    @Autowired
    private RsProjectDao rsProjectDao = null;

    @Autowired
    private RsPlatformDao rsPlatformDao = null;

    @Autowired
    private RsReferenceDao rsReferenceDao = null;

    @Autowired
    private RsMapSetDao rsMapSetDao = null;

    @Autowired
    private RsOrganizationDao rsOrganizationDao = null;

    @Autowired
    private RsMarkerGroupDao rsMarkerGroupDao = null;

    @Autowired
    private RsCvDao rsCvDao = null;

    @Autowired
    private RsExperimentDao rsExperimentDao = null;

    @Autowired
    private RsManifestDao rsManifestDao = null;

    @Autowired
    private RsDataSetDao rsDataSetDao = null;

    @Autowired
    private RsRoleDao rsRoleDao = null;

    private NameIdListDTO getNameIdListForAnalysis(NameIdListDTO nameIdListDTO) {

        NameIdListDTO returnVal = new NameIdListDTO();

        try {

            ResultSet resultSet = rsAnalysisDao.getAnalysisNames();
            List<NameIdDTO> listDTO = new ArrayList<>();

            NameIdDTO nameIdDTO;
            while (resultSet.next()) {
                nameIdDTO = new NameIdDTO();
                nameIdDTO.setId(resultSet.getInt("analysis_id"));
                nameIdDTO.setName(resultSet.getString("name"));
                listDTO.add(nameIdDTO);
            }


            returnVal.setNamesById(listDTO);


        } catch (Exception e) {
            returnVal.getStatus().addException(e);
            LOGGER.error("Gobii Maping Error", e);
        }

        return returnVal;
    }

    private NameIdListDTO getNameIdListForAnalysisNameByTypeId(NameIdListDTO nameIdListDTO) {

        NameIdListDTO returnVal = new NameIdListDTO();

        try {

            ResultSet resultSet = rsAnalysisDao.getAnalysisNamesByTypeId(Integer.parseInt(nameIdListDTO.getFilter()));
            List<NameIdDTO> listDTO = new ArrayList<>();

            NameIdDTO nameIdDTO;
            while (resultSet.next()) {
                nameIdDTO = new NameIdDTO();
                nameIdDTO.setId(resultSet.getInt("analysis_id"));
                nameIdDTO.setName(resultSet.getString("name"));
                listDTO.add(nameIdDTO);
            }


            returnVal.setNamesById(listDTO);


        } catch (Exception e) {
            returnVal.getStatus().addException(e);
            LOGGER.error("Gobii Maping Error", e);
        }

        return returnVal;
    }


    private NameIdListDTO getNameIdListForContacts(NameIdListDTO nameIdListDTO) {
        NameIdListDTO returnVal = nameIdListDTO;

        try {

            ResultSet resultSet = rsContactDao.getContactNamesForRoleName(nameIdListDTO.getFilter());
            List<NameIdDTO> listDTO = new ArrayList<>();

            NameIdDTO nameIdDTO;
            while (resultSet.next()) {
                nameIdDTO = new NameIdDTO();
                nameIdDTO.setId(resultSet.getInt("contact_id"));
                String lastName = resultSet.getString("lastname");
                String firstName = resultSet.getString("firstname");
                String name = lastName + ", " + firstName;
                nameIdDTO.setName(name);
                listDTO.add(nameIdDTO);
            }

            returnVal.setNamesById(listDTO);

        } catch (Exception e) {
            returnVal.getStatus().addException(e);
            LOGGER.error("Gobii Maping Error", e);
        }


        return (returnVal);

    } // getNameIdListForContacts()

    private NameIdListDTO getNameIdListForAllContacts(NameIdListDTO nameIdListDTO) {
        NameIdListDTO returnVal = new NameIdListDTO();

        try {

            ResultSet resultSet = rsContactDao.getAllContactNames();

            List<NameIdDTO> listDTO = new ArrayList<>();

            NameIdDTO nameIdDTO;
            while (resultSet.next()) {
                nameIdDTO = new NameIdDTO();
                nameIdDTO.setId(resultSet.getInt("contact_id"));
                String lastName = resultSet.getString("lastname");
                String firstName = resultSet.getString("firstname");
                String name = lastName + ", " + firstName;
                nameIdDTO.setName(name);
                listDTO.add(nameIdDTO);
            }

            returnVal.setNamesById(listDTO);

        } catch (Exception e) {
            returnVal.getStatus().addException(e);
            LOGGER.error("Gobii Maping Error", e);
        }


        return (returnVal);

    } // getNameIdListForContacts()

    private NameIdListDTO getNameIdListForMarkerGroups(NameIdListDTO nameIdListDTO) {

        NameIdListDTO returnVal = new NameIdListDTO();

        try {

            ResultSet resultSet = rsMarkerGroupDao.getMarkerGroupNames();
            List<NameIdDTO> listDTO = new ArrayList<>();

            NameIdDTO nameIdDTO;
            while (resultSet.next()) {
                nameIdDTO = new NameIdDTO();
                nameIdDTO.setId(resultSet.getInt("marker_group_id"));
                nameIdDTO.setName(resultSet.getString("name"));
                listDTO.add(nameIdDTO);
            }


            returnVal.setNamesById(listDTO);


        } catch (Exception e) {
            returnVal.getStatus().addException(e);
            LOGGER.error("Gobii Maping Error", e);
        }

        return returnVal;

    }//getNameIdListForMarkerGroups

    private NameIdListDTO getNameIdListForExperiment(NameIdListDTO nameIdListDTO) {

        NameIdListDTO returnVal = new NameIdListDTO();

        try {

            ResultSet resultSet = rsExperimentDao.getExperimentNames();
            List<NameIdDTO> listDTO = new ArrayList<>();

            NameIdDTO nameIdDTO;
            while (resultSet.next()) {
                nameIdDTO = new NameIdDTO();
                nameIdDTO.setId(resultSet.getInt("experiment_id"));
                nameIdDTO.setName(resultSet.getString("name"));
                listDTO.add(nameIdDTO);
            }


            returnVal.setNamesById(listDTO);


        } catch (Exception e) {
            returnVal.getStatus().addException(e);
            LOGGER.error("Gobii Maping Error", e);
        }

        return returnVal;
    }

    private NameIdListDTO getNameIdListForDataSet(NameIdListDTO nameIdListDTO) {

        NameIdListDTO returnVal = nameIdListDTO;

        try {

            ResultSet resultSet = rsDataSetDao.getDatasetNames();

            List<NameIdDTO> listDTO = makeMapOfDataSetNames(resultSet);

            returnVal.setNamesById(listDTO);


        } catch (Exception e) {
            returnVal.getStatus().addException(e);
            LOGGER.error(e.getMessage());
        }

        return returnVal;
    }

    private NameIdListDTO getNameIdListForPlatforms(NameIdListDTO nameIdListDTO) {

        NameIdListDTO returnVal = new NameIdListDTO();

        try {

            ResultSet resultSet = rsPlatformDao.getPlatformNames();
            List<NameIdDTO> listDTO = new ArrayList<>();

            NameIdDTO nameIdDTO;
            while (resultSet.next()) {
                nameIdDTO = new NameIdDTO();
                nameIdDTO.setId(resultSet.getInt("platform_id"));
                nameIdDTO.setName(resultSet.getString("name"));
                listDTO.add(nameIdDTO);
            }


            returnVal.setNamesById(listDTO);

        } catch (Exception e) {
            returnVal.getStatus().addException(e);
            LOGGER.error("Gobii Maping Error", e);
        }

        return returnVal;
    }

    private NameIdListDTO getNameIdListForPlatformsByTypeId(NameIdListDTO nameIdListDTO) {

        NameIdListDTO returnVal = new NameIdListDTO();

        try {

            ResultSet resultSet = rsPlatformDao.getPlatformNamesByTypeId(Integer.parseInt(nameIdListDTO.getFilter()));
            List<NameIdDTO> listDTO = new ArrayList<>();

            NameIdDTO nameIdDTO;
            while (resultSet.next()) {
                nameIdDTO = new NameIdDTO();
                nameIdDTO.setId(resultSet.getInt("platform_id"));
                nameIdDTO.setName(resultSet.getString("name"));
                listDTO.add(nameIdDTO);
            }


            returnVal.setNamesById(listDTO);

        } catch (Exception e) {
            returnVal.getStatus().addException(e);
            LOGGER.error("Gobii Maping Error", e);
        }

        return returnVal;
    }

    private NameIdListDTO getNameIdListForProjects(NameIdListDTO nameIdListDTO) {

        NameIdListDTO returnVal = new NameIdListDTO();

        try {

            ResultSet resultSet = rsProjectDao.getProjectNames();
            List<NameIdDTO> listDTO = new ArrayList<>();

            NameIdDTO nameIdDTO;
            while (resultSet.next()) {
                nameIdDTO = new NameIdDTO();
                nameIdDTO.setId(resultSet.getInt("project_id"));
                nameIdDTO.setName(resultSet.getString("name"));
                listDTO.add(nameIdDTO);
            }


            returnVal.setNamesById(listDTO);

        } catch (Exception e) {
            returnVal.getStatus().addException(e);
            LOGGER.error("Gobii Maping Error", e);
        }

        return returnVal;
    }

    private NameIdListDTO getNameIdListForReference(NameIdListDTO nameIdListDTO) {

        NameIdListDTO returnVal = new NameIdListDTO();

        try {

            ResultSet resultSet = rsReferenceDao.getReferenceNames();
            List<NameIdDTO> listDTO = new ArrayList<>();

            NameIdDTO nameIdDTO;
            while (resultSet.next()) {
                nameIdDTO = new NameIdDTO();
                nameIdDTO.setId(resultSet.getInt("reference_id"));
                nameIdDTO.setName(resultSet.getString("name"));
                listDTO.add(nameIdDTO);
            }


            returnVal.setNamesById(listDTO);


        } catch (Exception e) {
            returnVal.getStatus().addException(e);
            LOGGER.error("Gobii Maping Error", e);
        }

        return returnVal;
    }

    private NameIdListDTO getNameIdListForRole(NameIdListDTO nameIdListDTO) {

        NameIdListDTO returnVal = new NameIdListDTO();

        try {

            ResultSet resultSet = rsRoleDao.getContactRoleNames();
            List<NameIdDTO> listDTO = new ArrayList<>();

            NameIdDTO nameIdDTO;
            while (resultSet.next()) {
                nameIdDTO = new NameIdDTO();
                nameIdDTO.setId(resultSet.getInt("role_id"));
                nameIdDTO.setName(resultSet.getString("role_name"));
                listDTO.add(nameIdDTO);
            }


            returnVal.setNamesById(listDTO);


        } catch (Exception e) {
            returnVal.getStatus().addException(e);
            LOGGER.error("Gobii Maping Error", e);
        }

        return returnVal;
    }

    private NameIdListDTO getNameIdListForMapByTypeId(NameIdListDTO nameIdListDTO) {

        NameIdListDTO returnVal = new NameIdListDTO();

        try {

            ResultSet resultSet = rsMapSetDao.getMapNamesByTypeId(Integer.parseInt(nameIdListDTO.getFilter()));
            List<NameIdDTO> listDTO = new ArrayList<>();

            NameIdDTO nameIdDTO;
            while (resultSet.next()) {
                nameIdDTO = new NameIdDTO();
                nameIdDTO.setId(resultSet.getInt("mapset_id"));
                nameIdDTO.setName(resultSet.getString("name"));
                listDTO.add(nameIdDTO);
            }


            returnVal.setNamesById(listDTO);


        } catch (Exception e) {
            returnVal.getStatus().addException(e);
            LOGGER.error("Gobii Maping Error", e);
        }

        return returnVal;
    }

    private NameIdListDTO getNameIdListForMap(NameIdListDTO nameIdListDTO) {

        NameIdListDTO returnVal = new NameIdListDTO();

        try {

            ResultSet resultSet = rsMapSetDao.getAllMapsetNames();
            List<NameIdDTO> listDTO = new ArrayList<>();

            NameIdDTO nameIdDTO;
            while (resultSet.next()) {
                nameIdDTO = new NameIdDTO();
                nameIdDTO.setId(resultSet.getInt("mapset_id"));
                nameIdDTO.setName(resultSet.getString("name"));
                listDTO.add(nameIdDTO);
            }


            returnVal.setNamesById(listDTO);

        } catch (Exception e) {
            returnVal.getStatus().addException(e);
            LOGGER.error("Gobii Maping Error", e);
        }

        return returnVal;
    }

    private NameIdListDTO getNameIdListForOrganizationById(NameIdListDTO nameIdListDTO) {

        NameIdListDTO returnVal = new NameIdListDTO();

        try {

            ResultSet resultSet = rsOrganizationDao.getOrganizationNames();
            List<NameIdDTO> listDTO = new ArrayList<>();

            NameIdDTO nameIdDTO;
            while (resultSet.next()) {
                nameIdDTO = new NameIdDTO();
                nameIdDTO.setId(resultSet.getInt("organization_id"));
                nameIdDTO.setName(resultSet.getString("name"));
                listDTO.add(nameIdDTO);
            }


            returnVal.setNamesById(listDTO);


        } catch (Exception e) {
            returnVal.getStatus().addException(e);
            LOGGER.error("Gobii Maping Error", e);
        }

        return returnVal;
    }

    private NameIdListDTO getNameIdListForManifest(NameIdListDTO nameIdListDTO) {

        NameIdListDTO returnVal = new NameIdListDTO();

        try {

            ResultSet resultSet = rsManifestDao.getManifestNames();
            List<NameIdDTO> listDTO = new ArrayList<>();

            NameIdDTO nameIdDTO;
            while (resultSet.next()) {
                nameIdDTO = new NameIdDTO();
                nameIdDTO.setId(resultSet.getInt("manifest_id"));
                nameIdDTO.setName(resultSet.getString("name"));
                listDTO.add(nameIdDTO);
            }


            returnVal.setNamesById(listDTO);

        } catch (Exception e) {
            returnVal.getStatus().addException(e);
            LOGGER.error("Gobii Maping Error", e);
        }

        return returnVal;
    }//getNameIdListForManifest

    private NameIdListDTO getNameIdListForProjectNameByContact(NameIdListDTO nameIdListDTO) {

        NameIdListDTO returnVal = nameIdListDTO;

        try {

            String filter = nameIdListDTO.getFilter();
            if (NumberUtils.isNumber(filter)) {
                ResultSet resultSet = rsProjectDao.getProjectNamesForContactId(Integer.parseInt(filter));

                List<NameIdDTO> listDTO = new ArrayList<>();
                NameIdDTO nameIdDTO;

                while (resultSet.next()) {
                    nameIdDTO = new NameIdDTO();
                    nameIdDTO.setId(resultSet.getInt("project_id"));
                    nameIdDTO.setName(resultSet.getString("name"));
                    listDTO.add(nameIdDTO);
                }

                returnVal.setNamesById(listDTO);
            } else {
                nameIdListDTO.getStatus()
                        .addStatusMessage(GobiiStatusLevel.ERROR,
                        "Filter value is not numeric: " + filter);
            }
        } catch (Exception e) {
            returnVal.getStatus().addException(e);
            LOGGER.error("Gobii Maping Error", e);
        }

        return returnVal;

    } // getNameIdListForContacts()

    private NameIdListDTO getNameIdListForExperimentByProjectId(NameIdListDTO nameIdListDTO) {

        NameIdListDTO returnVal = new NameIdListDTO();

        try {

            ResultSet resultSet = rsExperimentDao.getExperimentNamesByProjectId(Integer.parseInt(nameIdListDTO.getFilter()));

            List<NameIdDTO> listDTO = new ArrayList<>();

            NameIdDTO nameIdDTO;
            while (resultSet.next()) {
                nameIdDTO = new NameIdDTO();

                nameIdDTO.setId(resultSet.getInt("experiment_id"));
                nameIdDTO.setName(resultSet.getString("name"));
                listDTO.add(nameIdDTO);
            }

            returnVal.setNamesById(listDTO);
        } catch (Exception e) {
            returnVal.getStatus().addException(e);
            LOGGER.error("Gobii Maping Error", e);
        }

        return returnVal;

    } // getNameIdListForContacts()

    private NameIdListDTO getNameIdListForCvGroups(NameIdListDTO nameIdListDTO) {

        NameIdListDTO returnVal = new NameIdListDTO();

        try {

            ResultSet resultSet = rsCvDao.getCvGroups();
            List<NameIdDTO> listDTO = new ArrayList<>();

            NameIdDTO nameIdDTO;
            while (resultSet.next()) {
                nameIdDTO = new NameIdDTO();
                nameIdDTO.setId(resultSet.getInt("cv_id"));
                nameIdDTO.setName(resultSet.getString("lower").toString());
                listDTO.add(nameIdDTO);
            }


            returnVal.setNamesById(listDTO);


        } catch (Exception e) {
            returnVal.getStatus().addException(e);
            LOGGER.error("Gobii Maping Error", e);
        }

        return returnVal;

    } // getNameIdListForCvTypes


    private NameIdListDTO getNameIdListForCv(NameIdListDTO nameIdListDTO) {

        NameIdListDTO returnVal = new NameIdListDTO();

        try {

            ResultSet resultSet = rsCvDao.getCvNames();

            List<NameIdDTO> listDTO = new ArrayList<>();

            NameIdDTO nameIdDTO;
            while (resultSet.next()) {
                nameIdDTO = new NameIdDTO();
                nameIdDTO.setId(resultSet.getInt("cv_id"));
                nameIdDTO.setName(resultSet.getString("term").toString());
                listDTO.add(nameIdDTO);
            }


            returnVal.setNamesById(listDTO);

        } catch (Exception e) {
            returnVal.getStatus().addException(e);
            LOGGER.error("Gobii Maping Error", e);
        }

        return returnVal;

    }

    private NameIdListDTO getNameIdListForCvGroupTerms(NameIdListDTO nameIdListDTO) {

        NameIdListDTO returnVal = new NameIdListDTO();

        try {

            ResultSet resultSet = rsCvDao.getCvTermsByGroup(nameIdListDTO.getFilter());

            List<NameIdDTO> listDTO = new ArrayList<>();

            NameIdDTO nameIdDTO;
            while (resultSet.next()) {
                nameIdDTO = new NameIdDTO();
                nameIdDTO.setId(resultSet.getInt("cv_id"));
                nameIdDTO.setName(resultSet.getString("term").toString());
                listDTO.add(nameIdDTO);
            }


            returnVal.setNamesById(listDTO);

        } catch (Exception e) {
            returnVal.getStatus().addException(e);
            LOGGER.error("Gobii Maping Error", e);
        }

        return returnVal;

    } // getNameIdListForCvGroupTerms()

    private List makeMapOfDataSetNames(ResultSet resultSet) throws Exception {

        List<NameIdDTO> returnVal = new ArrayList<>();

        NameIdDTO nameIdDTO;
        while (resultSet.next()) {
            nameIdDTO = new NameIdDTO();
            nameIdDTO.setId(resultSet.getInt("dataset_id"));
            nameIdDTO.setName(resultSet.getString("name"));

            if (resultSet.wasNull()) {
                nameIdDTO.setName("<no name>");
            }
            returnVal.add(nameIdDTO);
        }

        return returnVal;
    }

    private NameIdListDTO getNameIdListForDataSetByExperimentId(NameIdListDTO nameIdListDTO) {

        NameIdListDTO returnVal = new NameIdListDTO();

        try {

            ResultSet resultSet = rsDataSetDao.getDatasetNamesByExperimentId(Integer.parseInt(nameIdListDTO.getFilter()));
            List<NameIdDTO> listDTO   = makeMapOfDataSetNames(resultSet);

            returnVal.setNamesById(listDTO );

        } catch (Exception e) {
            returnVal.getStatus().addException(e);
            LOGGER.error("Gobii Maping Error", e);
        }

        return returnVal;

    } // getNameIdListForDataSet()

    @Override
    public NameIdListDTO getNameIdList(NameIdListDTO nameIdListDTO) {

        NameIdListDTO returnVal = nameIdListDTO;

        if (nameIdListDTO.getEntityType() == NameIdListDTO.EntityType.DBTABLE) {

            switch (nameIdListDTO.getEntityName().toLowerCase()) {

                case "analysis":
                    returnVal = getNameIdListForAnalysis(nameIdListDTO);
                    break;

                case "analysisnamebytypeid":
                    returnVal = getNameIdListForAnalysisNameByTypeId(nameIdListDTO);
                    break;

                case "contact":
                    returnVal = getNameIdListForContacts(nameIdListDTO);
                    break;
                case "allcontacts":
                    returnVal = getNameIdListForAllContacts(nameIdListDTO);
                    break;

                case "datasetnames":
                    returnVal = getNameIdListForDataSet(nameIdListDTO);
                    break;

                case "datasetnamesbyexperimentid":
                    returnVal = getNameIdListForDataSetByExperimentId(nameIdListDTO);
                    break;

                case "cvnames":
                    returnVal = getNameIdListForCv(nameIdListDTO);
                    break;

                case "project":
                    returnVal = getNameIdListForProjectNameByContact(nameIdListDTO);
                    break;

                case "organization":
                    returnVal = getNameIdListForOrganizationById(nameIdListDTO);
                    break;

                case "projectnames":
                    returnVal = getNameIdListForProjects(nameIdListDTO);
                    break;

                case "platform":
                    returnVal = getNameIdListForPlatforms(nameIdListDTO);
                    break;

                case "platformbytypeid":
                    returnVal = getNameIdListForPlatformsByTypeId(nameIdListDTO);
                    break;

                case "manifest":
                    returnVal = getNameIdListForManifest(nameIdListDTO);
                    break;

                case "mapset":
                    returnVal = getNameIdListForMap(nameIdListDTO);
                    break;

                case "mapnamebytypeid":
                    returnVal = getNameIdListForMapByTypeId(nameIdListDTO);
                    break;

                case "markergroup":
                    returnVal = getNameIdListForMarkerGroups(nameIdListDTO);
                    break;

                case "experiment":
                    returnVal = getNameIdListForExperimentByProjectId(nameIdListDTO);
                    break;

                case "experimentnames":
                    returnVal = getNameIdListForExperiment(nameIdListDTO);
                    break;

                case "cvgroupterms":
                    returnVal = getNameIdListForCvGroupTerms(nameIdListDTO);
                    break;

                case "cvgroups":
                    returnVal = getNameIdListForCvGroups(nameIdListDTO);
                    break;

                case "reference":
                    returnVal = getNameIdListForReference(nameIdListDTO);
                    break;

                case "role":
                    returnVal = getNameIdListForRole(nameIdListDTO);
                    break;
                default:
                    returnVal.getStatus().addStatusMessage(GobiiStatusLevel.ERROR,
                            "Unsupported entity for list request: " + nameIdListDTO.getEntityName());
            }

        }

        return returnVal;

    } // getNameIdList()

} // DtoMapNameIdListImpl
