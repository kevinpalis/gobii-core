package org.gobiiproject.gobiidomain.services.gdmv3;

import lombok.extern.slf4j.Slf4j;
import org.gobiiproject.gobiidomain.GobiiDomainException;
import org.gobiiproject.gobiimodel.config.GobiiException;
import org.gobiiproject.gobiimodel.dto.gdmv3.ProtocolDTO;
import org.gobiiproject.gobiimodel.dto.system.PagedResult;
import org.gobiiproject.gobiimodel.entity.Contact;
import org.gobiiproject.gobiimodel.entity.Platform;
import org.gobiiproject.gobiimodel.entity.Protocol;
import org.gobiiproject.gobiimodel.modelmapper.ModelMapper;
import org.gobiiproject.gobiisampletrackingdao.ContactDao;
import org.gobiiproject.gobiisampletrackingdao.GobiiDaoException;
import org.gobiiproject.gobiisampletrackingdao.PlatformDao;
import org.gobiiproject.gobiisampletrackingdao.ProtocolDao;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.*;

@Slf4j
@Transactional(rollbackOn = GobiiDaoException.class)
public class ProtocolServiceImpl implements ProtocolService {

    @Autowired
    private ProtocolDao protocolDao;

    @Autowired
    private ContactDao contactDao;

    @Autowired
    private PlatformDao platformDao;

    @Override
    public ProtocolDTO getProtocolById(Integer protocolId) throws GobiiDomainException {
        ProtocolDTO returnVal = new ProtocolDTO();
        try {
            Protocol protocol = protocolDao.getProtocolById(protocolId);
            ModelMapper.mapEntityToDto(protocol, returnVal);
            return returnVal;
        }
        catch (GobiiException gE) {
            throw gE;
        } catch (Exception e) {
            log.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }
    }

    @Override
    public PagedResult<ProtocolDTO> getProtocols(Integer pageSize, Integer pageNum,
                                                 Integer platformId) {
        List<ProtocolDTO> protocolDTOs = new ArrayList<>();
        try {

            Objects.requireNonNull(pageSize, "pageSize: Required non null");
            Objects.requireNonNull(pageNum, "pageNum: Required non null");

            List<Protocol> protocols =
                protocolDao.getProtocols(pageSize, pageSize*pageNum, platformId);

            protocols.forEach(protocol -> {
                ProtocolDTO protocolDTO = new ProtocolDTO();
                ModelMapper.mapEntityToDto(protocol, protocolDTO);
                protocolDTOs.add(protocolDTO);
            });

            return PagedResult.createFrom(pageNum, protocolDTOs);
        }
        catch (GobiiException gE) {
            throw gE;
        } catch (Exception e) {
            log.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }
    }

    @Override
    public ProtocolDTO createProtocol(ProtocolDTO protocolDTO) {

        ProtocolDTO protocolDTOCreated = new ProtocolDTO();

        try {
            Protocol protocolToCreate = new Protocol();

            //Map entity values from dto
            ModelMapper.mapDtoToEntity(protocolDTO, protocolToCreate);

            //Set CreatedBy and CreatedDate
            String creatorUserName = ContactService.getCurrentUserName();
            Contact creator = contactDao.getContactByUsername(creatorUserName);
            protocolToCreate.setCreatedBy(
                Optional.ofNullable(creator)
                    .map(v -> creator.getContactId())
                    .orElse(null));
            protocolToCreate.setCreatedDate(new Date());

            Protocol protocol = protocolDao.createProtocol(protocolToCreate);

            ModelMapper.mapEntityToDto(protocol, protocolDTOCreated);

            return protocolDTOCreated;
        }
        catch (GobiiException gE) {
            throw gE;
        } catch (Exception e) {
            log.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }
    }

    @Override
    public ProtocolDTO patchProtocol(Integer protocolId, ProtocolDTO protocolDTO) {
        ProtocolDTO protocolDTOUpdtaed = new ProtocolDTO();
        try {

            Protocol protocolToBeUpdated = protocolDao.getProtocolById(protocolId);

            //Update Platform
            if(protocolDTO.getPlatformId() != null) {
                Platform platform = platformDao.getPlatform(protocolDTO.getPlatformId());
                protocolToBeUpdated.setPlatform(platform);
            }

            // Map the values to be updated. Ignore null values.
            ModelMapper.mapDtoToEntity(protocolDTO, protocolToBeUpdated, true);

            //Set ModifiedBy and ModifiedDate
            String modifierUserName = ContactService.getCurrentUserName();
            Contact creator = contactDao.getContactByUsername(modifierUserName);
            protocolToBeUpdated.setModifiedBy(
                Optional.ofNullable(creator)
                    .map(v -> creator.getContactId())
                    .orElse(null));
            protocolToBeUpdated.setModifiedDate(new Date());

            Protocol protocolUpdated = protocolDao.patchProtocol(protocolToBeUpdated);

            //Map Entity back to return DTO.
            ModelMapper.mapEntityToDto(protocolUpdated, protocolDTOUpdtaed);

            return protocolDTOUpdtaed;
        }
        catch (GobiiException gE) {
            throw gE;
        } catch (Exception e) {
            log.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }
    }

    @Override
    public void deleteProtocol(Integer protocolId) {
        try {
            Protocol protocol = protocolDao.getProtocolById(protocolId);
            protocolDao.deleteProtocol(protocol);
        }
        catch (GobiiException gE) {
            throw gE;
        } catch (Exception e) {
            log.error("Gobii service error", e);
            throw new GobiiDomainException(e);
        }
    }

}
