package org.gobiiproject.gobiidomain.services.gdmv3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.transaction.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.EntityDoesNotExistException;
import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.InvalidMarkersException;
import org.gobiiproject.gobiidomain.services.gdmv3.exceptions.MarkerStatus;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.gdmv3.MarkerDTO;
import org.gobiiproject.gobiimodel.dto.gdmv3.MarkerGroupDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Contact;
import org.gobiiproject.gobiimodel.entity.Cv;
import org.gobiiproject.gobiimodel.entity.Marker;
import org.gobiiproject.gobiimodel.entity.MarkerGroup;
import org.gobiiproject.gobiimodel.modelmapper.ModelMapper;
import org.gobiiproject.gobiimodel.types.GobiiStatusLevel;
import org.gobiiproject.gobiimodel.types.GobiiValidationStatusType;
import org.gobiiproject.gobiimodel.utils.LineUtils;
import org.gobiiproject.gobiisampletrackingdao.ContactDao;
import org.gobiiproject.gobiisampletrackingdao.CvDao;
import org.gobiiproject.gobiisampletrackingdao.MarkerDao;
import org.gobiiproject.gobiisampletrackingdao.MarkerGroupDao;
import org.springframework.beans.factory.annotation.Autowired;


public class MarkerGroupServiceImpl implements MarkerGroupService {

    
    @Autowired
    private CvDao cvDao;

    @Autowired
    private MarkerGroupDao markerGroupDao;

    @Autowired
    private ContactDao contactDao;

    @Autowired
    private MarkerDao markerDao;

    @Transactional
    @Override
    public MarkerGroupDTO createMarkerGroup(MarkerGroupDTO request, String createdBy) throws Exception {

        MarkerGroup markerGroup = new MarkerGroup();
        markerGroup.setName(request.getMarkerGroupName());
        markerGroup.setGermplasmGroup(request.getGermplasmGroup());

        Cv newStatus = cvDao.getNewStatus();
        markerGroup.setStatus(newStatus);

        //audit items
        Contact creator = contactDao.getContactByUsername(createdBy);
        markerGroup.setCreatedBy(Optional.ofNullable(creator).map(v -> v.getContactId()).orElse(null));
        markerGroup.setCreatedDate(new java.util.Date());
        
        markerGroup = markerGroupDao.createMarkerGroup(markerGroup);
        MarkerGroupDTO createdMarkerGroupDTO = new MarkerGroupDTO();
        ModelMapper.mapEntityToDto(markerGroup, createdMarkerGroupDTO);

        return createdMarkerGroupDTO;


    }

    @Transactional
	@Override
	public PagedResult<MarkerGroupDTO> getMarkerGroups(Integer page, Integer pageSize) {
        Integer offset = page * pageSize;
        List<MarkerGroup> markerGroups = markerGroupDao.getMarkerGroups(offset, pageSize);
        List<MarkerGroupDTO> markerGroupDTOs = new ArrayList<>();

        markerGroups.forEach(markerGroup -> {
            MarkerGroupDTO markerGroupDTO = new MarkerGroupDTO();
            ModelMapper.mapEntityToDto(markerGroup, markerGroupDTO);
            markerGroupDTOs.add(markerGroupDTO);
        });

		return PagedResult.createFrom(page, markerGroupDTOs);
	}

    @Transactional
	@Override
	public MarkerGroupDTO getMarkerGroup(Integer markerGroupId) throws Exception {
        MarkerGroup markerGroup = this.loadMarkerGroup(markerGroupId);
        MarkerGroupDTO markerGroupDTO = new MarkerGroupDTO();
        ModelMapper.mapEntityToDto(markerGroup, markerGroupDTO);
        return markerGroupDTO;
	}

    @Transactional
	@Override
	public MarkerGroupDTO updateMarkerGroup(Integer markerGroupId, MarkerGroupDTO request, String updatedBy) throws Exception {
        MarkerGroup markerGroup = this.loadMarkerGroup(markerGroupId);
        boolean modified = false;
        if (!LineUtils.isNullOrEmpty(request.getMarkerGroupName())) {
            markerGroup.setName(request.getMarkerGroupName());
            modified = true;
        }
        if (!LineUtils.isNullOrEmpty(request.getGermplasmGroup())) {
            markerGroup.setGermplasmGroup(request.getGermplasmGroup());
            modified = true;
        }

        if (modified) {
            Contact editor = contactDao.getContactByUsername(updatedBy);
            markerGroup.setModifiedBy(Optional.ofNullable(editor).map(v -> v.getContactId()).orElse(null));
            markerGroup.setModifiedDate(new java.util.Date());

            //status
            Cv modifiedStat = cvDao.getModifiedStatus();
            markerGroup.setStatus(modifiedStat);

            markerGroup = markerGroupDao.updateMarkerGroup(markerGroup);
        }

        MarkerGroupDTO markerGroupDTO = new MarkerGroupDTO();
        ModelMapper.mapEntityToDto(markerGroup, markerGroupDTO);
		return markerGroupDTO;
    }
    
    private MarkerGroup loadMarkerGroup(Integer markerGroupId) throws Exception {
        MarkerGroup markerGroup = markerGroupDao.getMarkerGroup(markerGroupId);
        if (markerGroup == null) {
            throw new EntityDoesNotExistException("marker group");
        }
        return markerGroup;
    }

    @Transactional
	@Override
	public void deleteMarkerGroup(Integer markerGroupId) throws Exception {
        MarkerGroup markerGroup = this.loadMarkerGroup(markerGroupId);
        markerGroupDao.deleteMarkerGroup(markerGroup);
	}

    @Transactional
	@Override
    public PagedResult<MarkerDTO> mapMarkers(Integer markerGroupId, List<MarkerDTO> markers, String editedBy) throws Exception {
        Objects.requireNonNull(markers, "No markers to process");
        //check data size
        if (markers.size() > 1000) {
            throw new GobiiException(GobiiStatusLevel.ERROR, GobiiValidationStatusType.BAD_REQUEST,
                "Maximum number of allowed markers in list for a single request is 1000");
        }
        
        MarkerGroup markerGroup = this.loadMarkerGroup(markerGroupId);

        //get platform_name, marker name combo from list
        List<List<String>> markerTuples = new java.util.ArrayList<>();
        markers.forEach( markerDTO -> {
            List<String> tuple = new ArrayList<>();
            tuple.add(markerDTO.getPlatformName());
            tuple.add(markerDTO.getMarkerName());
            markerTuples.add(tuple);
        });

        List<Marker> existingMarkers = markerDao.getMarkersByPlatformMarkerNameTuples(markerTuples);


        //check the markers one by one
        //create copy of existingMarkers
        List<MarkerStatus> statusList = new ArrayList<>();
        //convert to map
        Map<String, Boolean> lookup = new HashMap<>();
        existingMarkers.forEach(marker -> {
            lookup.put( marker.getPlatform().getPlatformName() + marker.getMarkerName(), true);
        });
        boolean errorsFound = false;
        for (MarkerDTO markerDTO: markers) {
            if (lookup.get(markerDTO.getPlatformName() + markerDTO.getMarkerName()) != null) {
                //check alleles
                MarkerStatus markerStatus = this.checkAlleles(
                    markerDTO.getFavorableAlleles(),
                    markerDTO.getPlatformName(),
                    markerDTO.getMarkerName());
                statusList.add(markerStatus);
                if (markerStatus.getError() != null) errorsFound = true;
            } else {
                statusList.add(
                    new MarkerStatus(
                        false,
                        String.format("Bad Request. Marker: %s, %s is invalid", markerDTO.getPlatformName(), markerDTO.getMarkerName())
                    )
                );
                errorsFound = true;
            }   
        }

        if (errorsFound) {
            throw new InvalidMarkersException(statusList);
        }

        statusList = null; //delete this moving since no errors;
            
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode currentNode = Optional.ofNullable((ObjectNode) markerGroup.getMarkers())
                                         .orElse(objectMapper.createObjectNode());

        Integer counter = 0;
        List<MarkerDTO> markerDTOs = new ArrayList<>();
        for (Marker marker: existingMarkers) {
            ArrayNode arrayNode = currentNode.putArray(marker.getMarkerId().toString());
            String[] favorableAlleles = markers.get(counter).getFavorableAlleles();
            for (String allele: favorableAlleles) {
                arrayNode.add(allele);
            }
            counter++;

            //map to DTO
            MarkerDTO markerDTO = new MarkerDTO();
            ModelMapper.mapEntityToDto(marker, markerDTO);
            //set the favorableAllele
            markerDTO.setFavorableAlleles(favorableAlleles);
            markerDTOs.add(markerDTO);
        }

        markerGroup.setMarkers(currentNode);
        markerGroup = markerGroupDao.updateMarkerGroup(markerGroup);

		return PagedResult.createFrom(0, markerDTOs);
	}

	@Override
	public PagedResult<MarkerDTO> getMarkerGroupMarkers(Integer markerGroupId, Integer page, Integer pageSize) throws Exception {
        MarkerGroup markerGroup = this.loadMarkerGroup(markerGroupId);
        //get ids

        ObjectNode markers = (ObjectNode) markerGroup.getMarkers();
        List<Integer> markerIds = new ArrayList<>();
        Iterator<String> iterator =  markers.fieldNames();
        while (iterator.hasNext()) {
            markerIds.add(new Integer(iterator.next()));
        }
        Collections.sort(markerIds);
        //handle paging here
        Integer offset = page * pageSize;
        Integer max = markerIds.size();
        List<Integer> subset = markerIds.subList(Math.max(0, offset), Math.min(offset + pageSize, max));
       
        List<Marker> results = markerDao.getMarkersByMarkerIds(new HashSet<Integer>(subset));

        List<MarkerDTO> markerDTOs = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();

        results.forEach( marker -> {
            MarkerDTO markerDTO = new MarkerDTO();
            ModelMapper.mapEntityToDto(marker, markerDTO);
            JsonNode node = markers.get(marker.getMarkerId().toString());
            ArrayList<?> array = objectMapper.convertValue(node, ArrayList.class);
            markerDTO.setFavorableAlleles(array.toArray(new String[0]));
            markerDTOs.add(markerDTO);
        });
        

		return PagedResult.createFrom(page, markerDTOs);
    }
    
    private static final String PATTERN_STRING = "^(A|C|G|T|\\-|\\+|0|1|2|0[0-9]{3}|1000)$";
    private static final Pattern ALLELE_PATTERN = Pattern.compile(PATTERN_STRING);
   
    private MarkerStatus checkAlleles(String[] alleles, String platformName, String markerName) {
        List<String> invalidAlleles = new ArrayList<>();
        for (String allele: alleles) {
            Matcher matcher = ALLELE_PATTERN.matcher(allele);
            if (!matcher.matches()) invalidAlleles.add(allele); 
        }

        if (invalidAlleles.size() > 0) {
            return new MarkerStatus(
                false,
                String.format(
                    "Bad Request. Invalid allele value(s) for %s, %s: %s",
                    platformName,
                    markerName,
                    String.join(", ", invalidAlleles)
                )
            );
        }


        return new MarkerStatus(true);
    }
    
}