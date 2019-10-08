package org.gobiiproject.gobidomain.services.impl.sampletracking;

import org.gobiiproject.gobidomain.GobiiDomainException;
import org.gobiiproject.gobidomain.services.ContactService;
import org.gobiiproject.gobidomain.services.DnaSampleService;
import org.gobiiproject.gobidomain.services.FilesService;
import org.gobiiproject.gobidomain.services.ProjectService;
import org.gobiiproject.gobiimodel.cvnames.CvGroup;
import org.gobiiproject.gobiimodel.cvnames.JobPayloadType;
import org.gobiiproject.gobiimodel.dto.entity.auditable.sampletracking.DnaSampleDTO;
import org.gobiiproject.gobiimodel.dto.entity.auditable.sampletracking.ProjectDTO;
import org.gobiiproject.gobiimodel.dto.entity.children.PropNameId;
import org.gobiiproject.gobiimodel.dto.entity.noaudit.ProjectSamplesDTO;
import org.gobiiproject.gobiimodel.dto.entity.noaudit.SampleMetadataDTO;
import org.gobiiproject.gobiimodel.dto.instructions.loader.GobiiFileColumn;
import org.gobiiproject.gobiimodel.dto.instructions.loader.GobiiLoaderInstruction;
import org.gobiiproject.gobiimodel.dto.instructions.loader.GobiiLoaderMetadata;
import org.gobiiproject.gobiimodel.dto.instructions.loader.GobiiLoaderProcedure;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.modelmapper.EntityFieldBean;
import org.gobiiproject.gobiimodel.modelmapper.ModelMapper;
import org.gobiiproject.gobiimodel.types.GobiiColumnType;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiisampletrackingdao.CvDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DnaSampleServiceImpl implements  DnaSampleService {

    Logger LOGGER = LoggerFactory.getLogger(DnaSampleServiceImpl.class);

    @Autowired
    private CvDao cvDao;

    @Autowired
    private ProjectService sampleTrackingProjectService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private FilesService fileService = null;


    @Override
    public ProjectSamplesDTO createSamples(ProjectSamplesDTO projectSamplesDTO)  throws GobiiDomainException {

        ProjectSamplesDTO returnVal = null;

        try {

            return projectSamplesDTO;

        } catch (Exception e) {

            LOGGER.error("Gobii service error", e);
            throw new GobiiDomainException(e);

        }
    }

    public void uploadSamples(InputStream is, SampleMetadataDTO sampleMetadata, String cropType) {

        BufferedReader br;

        try {

            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            String fileHeader = br.readLine();



            GobiiLoaderProcedure gobiiLoaderProcedure = new GobiiLoaderProcedure();

            List<GobiiLoaderInstruction> gobiiLoaderInstructionList = new ArrayList<>();

            GobiiLoaderMetadata gobiiLoaderMetadata = new GobiiLoaderMetadata();

            //Set Croptype
            gobiiLoaderMetadata.setGobiiCropType(cropType);

            //Set Contact email for instruction file
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();

            gobiiLoaderMetadata.setContactEmail(this.contactService.getContactByUserName(userName).getEmail());

            //Set Job Payload Type
            gobiiLoaderMetadata.setJobPayloadType(JobPayloadType.CV_PAYLOADTYPE_SAMPLES);

            //Get project details to set Project PropName in instruction file.
            PropNameId projectPropName = new PropNameId();

            ProjectDTO projectDTO = (ProjectDTO) sampleTrackingProjectService.getProjectById(
                    sampleMetadata.getProjectId());

            projectPropName.setId(projectDTO.getProjectId());

            projectPropName.setName(projectDTO.getProjectName());

            gobiiLoaderMetadata.setProject(projectPropName);

            //Map File columns to the table columns
            Map<String, List<GobiiFileColumn>> fileColumnByTableMap = this.mapFileCoulumnToGobiiTable(
                    fileHeader,
                    sampleMetadata);

            for(String tableName : fileColumnByTableMap.keySet()) {

                GobiiLoaderInstruction gobiiLoaderInstruction = new GobiiLoaderInstruction();

                gobiiLoaderInstruction.setTable(tableName);

                gobiiLoaderInstruction.setGobiiFileColumns(fileColumnByTableMap.get(tableName));

                gobiiLoaderInstructionList.add(gobiiLoaderInstruction);

            }

            gobiiLoaderProcedure.setInstructions(gobiiLoaderInstructionList);


        } catch(Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new GobiiDomainException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN, "Server error");
        }
    }

    public Map<String, List<GobiiFileColumn>> mapFileCoulumnToGobiiTable(String fileHeader,
                                                                   SampleMetadataDTO sampleMetadata) {

        Map<String, List<GobiiFileColumn>> fileColumnByTableMap = new HashMap<>();

        try {

            String[] fileHeaderList = fileHeader.split("\t");

            Map<String, EntityFieldBean> dtoEntityMap = ModelMapper.getDtoEntityMap(DnaSampleDTO.class);

            //Get Germplasm prop cvs TODO: Below cv mapping could be moved to a generalized function.
            //TODO: Create Cv service to achieve the same.
            List<Cv> germplasmPropCvs = cvDao.getCvListByCvGroup(
                    CvGroup.CVGROUP_GERMPLASM_PROP.getCvGroupName(),
                    null);

            Map<String, Cv> germplasmPropByCvTerm = new HashMap<>();

            for(Cv germplasmPropCv : germplasmPropCvs) {
                if(germplasmPropCv.getTerm() != null) {
                    germplasmPropByCvTerm.put(germplasmPropCv.getTerm(), germplasmPropCv);
                }
            }

            //Get dnasample prop cvs
            List<Cv> dnasamplePropCvs = cvDao.getCvListByCvGroup(
                    CvGroup.CVGROUP_DNASAMPLE_PROP.getCvGroupName(),
                    null);

            Map<String, Cv> dnasamplePropByCvTerm = new HashMap<>();

            for(Cv dnasamplePropCv : dnasamplePropCvs) {
                if(dnasamplePropCv.getTerm() != null) {
                    dnasamplePropByCvTerm.put(dnasamplePropCv.getTerm(), dnasamplePropCv);
                }
            }


            for (int i = 0; i < fileHeaderList.length; i++) {

                GobiiFileColumn gobiiFileColumn = new GobiiFileColumn();

                String columnHeader = fileHeaderList[i];

                EntityFieldBean entityField = null;

                if (sampleMetadata.getMap().containsKey(columnHeader)) {

                    String dtoProp = sampleMetadata.getMap().get(columnHeader);

                    if (dtoEntityMap.containsKey(dtoProp) && dtoEntityMap.get(dtoProp) != null) {

                        entityField = dtoEntityMap.get(dtoProp);

                    } else {

                        entityField = new EntityFieldBean();

                        String propField = dtoProp.substring(0, dtoProp.lastIndexOf("."));

                        //TODO: More generalized solution where you can map properties to the instruction files
                        // needs to be figured
                        if (propField.equals("germplasm.properties")) {

                            String germplasmPropField = dtoProp.substring(dtoProp.lastIndexOf(".")+1);

                            if(germplasmPropByCvTerm.containsKey(germplasmPropField)) {

                                entityField.setColumnName(germplasmPropByCvTerm.get(germplasmPropField).getCvId().toString());

                                entityField.setTableName(CvGroup.CVGROUP_DNASAMPLE_PROP.getCvGroupName());

                            }

                        } else if (propField.equals("properties")) {

                            String dnasamplePropField = dtoProp.substring(dtoProp.lastIndexOf(".")+1);

                            if(dnasamplePropByCvTerm.containsKey(dnasamplePropField)) {

                                entityField.setColumnName(dnasamplePropByCvTerm.get(
                                        dnasamplePropField).getCvId().toString());

                                entityField.setTableName(CvGroup.CVGROUP_GERMPLASM_PROP.getCvGroupName());

                            }

                        } else if (dtoProp.equals("germplasm.germplasmSpecies")) {

                            entityField.setColumnName("species_name");

                            entityField.setTableName("germplasm");

                        } else if (dtoProp.equals("germplasm.germplasmType")) {

                            entityField.setColumnName("type_name");

                            entityField.setTableName("germplasm");

                        } else {
                            entityField = null;
                        }
                    }

                } else {
                    if (dtoEntityMap.containsKey(columnHeader)) {
                        entityField = dtoEntityMap.get(columnHeader);
                    }
                }

                if (entityField != null) {

                    gobiiFileColumn.setName(entityField.getColumnName());

                    gobiiFileColumn.setCCoord(i);

                    gobiiFileColumn.setRCoord(1);

                    gobiiFileColumn.setGobiiColumnType(GobiiColumnType.CSV_COLUMN);

                    gobiiFileColumn.setSubcolumn(false);

                    if (entityField.getTableName() != null) {

                        if (!fileColumnByTableMap.containsKey(entityField.getTableName())) {
                            fileColumnByTableMap.put(entityField.getTableName(), new ArrayList<>());
                        }

                        fileColumnByTableMap.get(entityField.getTableName()).add(gobiiFileColumn);
                    }
                }
            }

            return fileColumnByTableMap;

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new GobiiDomainException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.UNKNOWN, "Server error");
        }
    }
}
