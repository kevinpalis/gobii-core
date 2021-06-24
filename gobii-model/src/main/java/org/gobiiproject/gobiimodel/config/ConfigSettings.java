package org.gobiiproject.gobiimodel.config;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.gobiiproject.gobiimodel.types.GobiiAuthenticationType;
import org.gobiiproject.gobiimodel.types.GobiiFileNoticeType;
import org.gobiiproject.gobiimodel.types.GobiiFileProcessDir;
import org.gobiiproject.gobiimodel.types.ServerCapabilityType;
import org.gobiiproject.gobiimodel.types.ServerType;
import org.gobiiproject.gobiimodel.utils.email.AuthType;
import org.gobiiproject.gobiimodel.utils.error.Logger;

/**
 * This class encapsulates all the configuration data with which the rest of the system interacts.
 * It is intended to eliminate the need for other system components to interact directly with (or
 * even know the format of the) underlying configuration mechanism. At this time, that maechanism
 * happens to be a configuration file. In the future, it could be a database. As a general container
 * for this functionality, it delegates most of its functionality to component classes. In particular,
 * it consumes:
 * * A ConfigValues instance, which contains the actual configuration data;
 * * A ConfigValuesReader, which knows how to create a ConfigValues instance.
 * This form of organization enables this class to function as a dependency firewall between the actual
 * format of the data and the rest of the system.
 */
public class ConfigSettings {

    private String configFileFqpn;

    public ConfigSettings() {
        try {
            configValues = ConfigValuesReader.read(null);
        } catch (Exception e) {
            Logger.logError("Error instancing ConfigValues with null fqpn", e);
        }
    }

    ConfigValues configValues = null;

    public ConfigSettings(String configFileWebPath) {
        Logger.logDebug("ConfigSettings", "configFileWebPath: " + configFileWebPath);
        try {
            configValues = ConfigValuesReader.read(configFileWebPath);
            this.configFileFqpn = configFileWebPath;
        } catch (Exception e) {
            Logger.logError("Error instancing ConfigValues with fqpn: " + configFileWebPath, e);

        }
    } // ctor

    private ConfigSettings(ConfigValues configValues) {
        this.configValues = configValues;
    }

    public static ConfigSettings makeNew(String userFqpn) throws Exception {

        ConfigSettings returnVal = null;

        ConfigValues configValues = ConfigValuesReader.makeNew(userFqpn);
        if (configValues != null) {
            returnVal = new ConfigSettings(configValues);
            returnVal.configFileFqpn = userFqpn;
        }

        return returnVal;

    } //

    public static ConfigSettings read(String userFqpn) throws Exception {

        ConfigSettings returnVal = null;

        ConfigValues configValues = ConfigValuesReader.read(userFqpn);
        if (configValues != null) {
            returnVal = new ConfigSettings(configValues);
            returnVal.configFileFqpn = userFqpn;
        }

        return returnVal;

    } //


    /***
     * Creates a map of server capability settings that are derived from various
     * configuration values. This map could itself have been stored in the configuration.
     * However, that would have resulted in reduntant values.
     * @return
     */
    public Map<ServerCapabilityType, Boolean> getServerCapabilities() throws Exception {

        Map<ServerCapabilityType, Boolean> returnVal = new HashMap<>();

        if (this.configValues.getGlobalServer(ServerType.KDC) != null) {
            returnVal.put(ServerCapabilityType.KDC, this.configValues.getGlobalServer(ServerType.KDC).isActive());
        } else {
            returnVal.put(ServerCapabilityType.KDC, false);
        }

        returnVal.put(ServerCapabilityType.GOBII_BACKEND, this.configValues.isProvidesBackend());

        // for now we are not controlling this value though configuraiton
        returnVal.put(ServerCapabilityType.BRAPI, true);

        return returnVal;
    }


    public void commit(boolean getFqpnFromJndi) throws Exception {

        String fqpn;

        if (getFqpnFromJndi) {
            fqpn = ConfigValuesReader.getFqpnFromTomcat();
        } else {
            fqpn = this.configFileFqpn;
        }

        ConfigValuesReader.commitConfigValues(this.configValues, fqpn);
    }

    public void commit() throws Exception {
        // In the case where we are committing, we need to make sure that
        // we already have the fqpn of the config file
        this.commit(false);
    }

    public String getProcessingPath(String cropType, GobiiFileProcessDir gobiiFileProcessDir) throws Exception {
        return this.configValues.getProcessingPath(cropType, gobiiFileProcessDir);
    }

    public String getFileNoticePath(String cropType, GobiiFileNoticeType gobiiFileNoticeType) throws Exception {

        return this.configValues.getFileNoticePath(cropType, gobiiFileNoticeType);
    }

    public void setCrop(String gobiiCropType,
                        boolean isActive,
                        String serviceDomain,
                        String serviceAppRoot,
                        Integer servicePort) throws Exception {

        this.configValues.setCrop(gobiiCropType, isActive, serviceDomain, serviceAppRoot, servicePort);
    }

    public void removeCrop(String cropId) throws Exception {
        this.configValues.removeCrop(cropId);
    }

    public boolean isCropDefined(String gobiiCropType) {
        return this.configValues.isCropDefined(gobiiCropType);
    }

    public ServerConfig getGlobalServer(ServerType serverType) throws Exception {
        return this.configValues.getGlobalServer(serverType);
    }

    public GobiiCropConfig getCropConfig(String gobiiCropType) throws Exception {

        return (this.configValues.getCropConfig(gobiiCropType));
    }

    public List<GobiiCropConfig> getActiveCropConfigs() throws Exception {

        return (this.configValues.getActiveCropConfigs());
    }

    public List<GobiiCropConfig> getAllCropConfigs() throws Exception {

        return (new ArrayList<>(this.configValues
                .getCropConfigs()
                .values()));
    }

    public TestExecConfig getTestExecConfig() {

        return this.configValues.getTestExecConfig();
    }

    public void setTestExecConfig(TestExecConfig testExecConfig) {
        this.configValues.setTestExecConfig(testExecConfig);
    }

    public List<String> getActiveCropTypes() throws Exception {
        return this
                .configValues
                .getActiveCropConfigs()
                .stream()
                .filter(c -> c.isActive() == true)
                .map(GobiiCropConfig::getGobiiCropType)
                .collect(Collectors.toList());
    }

    public GobiiCropConfig getCurrentCropConfig() throws Exception {

        return this.configValues.getCurrentCropConfig();
    }


    public void setCurrentGobiiCropType(String currentGobiiCropType) {
        this.configValues.setCurrentGobiiCropType(currentGobiiCropType);

    }

    public String getCurrentGobiiCropType() {
        return this.configValues.getCurrentGobiiCropType();
    }

    public String getEmailSvrPassword() {

        return this.configValues.getEmailSvrPassword();
    }

    public String getEmailSvrHashType() {
        return this.configValues.getEmailSvrHashType();
    }

    public String getEmailSvrUser() {

        return this.configValues.getEmailSvrUser();
    }

    public String getEmailSvrDomain() {

        return this.configValues.getEmailSvrDomain();
    }

    public String getEmailSvrType() {

        return this.configValues.getEmailSvrType();
    }

    public Integer getEmailServerPort() {

        return this.configValues.getEmailServerPort();
    }

    public void setEmailSvrType(String emailSvrType) {
        this.configValues.setEmailSvrType(emailSvrType);
    }

    public void setEmailSvrDomain(String emailSvrDomain) {
        this.configValues.setEmailSvrDomain(emailSvrDomain);
    }

    public void setEmailSvrUser(String emailSvrUser) {
        this.configValues.setEmailSvrUser(emailSvrUser);
    }

    public void setEmailSvrHashType(String emailSvrHashType) {
        this.configValues.setEmailSvrHashType(emailSvrHashType);
    }

    public void setEmailSvrPassword(String emailSvrPassword) {
        this.configValues.setEmailSvrPassword(emailSvrPassword);
    }

    public void setEmailSvrPort(Integer emailServerPort) {
        this.configValues.setEmailSvrPort(emailServerPort);
    }

    public boolean isIflIntegrityCheck() {
        return this.configValues.isIflIntegrityCheck();
    }

    public void setIflIntegrityCheck(boolean iflIntegrityCheck) {
        this.configValues.setIflIntegrityCheck(iflIntegrityCheck);
    }

    public GobiiAuthenticationType getGobiiAuthenticationType() {
        return this.configValues.getGobiiAuthenticationType();
    }

    public void setGobiiAuthenticationType(GobiiAuthenticationType gobiiAuthenticationType) {
        this.configValues.setGobiiAuthenticationType(gobiiAuthenticationType);
    }

    public String getLdapUrl() {
        return this.configValues.getLdapUrl();
    }

    public void setLdapUrl(String ldapSvrDomain) {
        this.configValues.setLdapUrl(ldapSvrDomain);
    }

    public String getLdapUserDnPattern() {
        return this.configValues.getLdapUserDnPattern();
    }

    public void setLdapUserDnPattern(String ldapUserPath) {
        this.configValues.setLdapUserDnPattern(ldapUserPath);
    }


    public String getLdapBindUser() {
        return this.configValues.getLdapBindUser();
    }

    public void setLdapBindUser(String ldapBindUser) {
        this.configValues.setLdapBindUser(ldapBindUser);
    }

    public String getLdapBindPassword() {
        return this.configValues.getLdapBindPassword();
    }

    public void setLdapBindPassword(String ldapBindPassword) {
        this.configValues.setLdapBindPassword(ldapBindPassword);
    }

    public String getFileSystemRoot() {

        return this.configValues.getFileSystemRoot();
    }

    public void setFileSystemRoot(String fileSystemRoot) {

        this.configValues.setFileSystemRoot(fileSystemRoot);
    }

    public String getFileSystemHDF5() {
        return this.configValues.getHdf5ExePath();
    }

    public void setFileSystemHDF5(String fileSystemHDF5) {
        this.configValues.sethdf5ExePath(fileSystemHDF5);
    }

    public String getFileSystemLog() {
        return this.configValues.getFileSystemLog();
    }

    public void setFileSystemLog(String fileSystemLog) {
        this.configValues.setFileSystemLog(fileSystemLog);
    }

    public String getFileSysCropsParent() {

        return this.configValues.getFileSysCropsParent();
    }

    public void setFileSysCropsParent(String fileSysCropsParent) {
        this.configValues.setFileSysCropsParent(fileSysCropsParent);
    }

    public boolean isDecrypt() {
        return this.configValues.isDecrypt();
    }

    public void setIsDecrypt(boolean isDecrypt) {
        this.configValues.setDecrypt(isDecrypt);
    }

    public void setLdapUserForBackendProcs(String ldapUserForBackendProcs) {
        this.configValues.setLdapUserForBackendProcs(ldapUserForBackendProcs);
    }


    public String getLdapUserForBackendProcs() {
        return this.configValues.getLdapUserForBackendProcs();
    }

    public String getLdapPasswordForBackendProcs() {
        return this.configValues.getLdapPasswordForBackendProcs();
    }

    public void setLdapPasswordForBackendProcs(String ldapPassworedForBackendProcs) {
        this.configValues.setLdapPasswordForBackendProcs(ldapPassworedForBackendProcs);
    }


    public boolean isAuthenticateBrapi() {
        return this.configValues.isAuthenticateBrapi();
    }

    public void setAuthenticateBrapi(boolean authenticateBrapi) {
        this.configValues.setAuthenticateBrapi(authenticateBrapi);
    }

    public Integer getMaxUploadSizeMbytes() {
        return this.configValues.getMaxUploadSizeMbytes();
    }

    public void setMaxUploadSizeMbytes(Integer maxUploadSizeMbytes) {
        this.configValues.setMaxUploadSizeMbytes(maxUploadSizeMbytes);
    }

    public boolean isProvidesBackend() {
        return this.configValues.isProvidesBackend();
    }

    public void setProvidesBackend(boolean providesBackend) {
        this.configValues.setProvidesBackend(providesBackend);
    }

    public String getHdf5ExePath() {
        return this.configValues.getHdf5ExePath();
    }
    public AuthType getEmailAuth(){return this.configValues.getEmailAuthEnum();}

    public void setEmailAuth(String auth){ this.configValues.setEmailAuthType(auth);}

    public String getEmailSvrFrom(){
        return this.configValues.getEmailSvrFrom();
    }
    public void setEmailSvrFrom(String emailSvrFrom){
        this.configValues.setEmailSvrFrom(emailSvrFrom);
    }

	public KeycloakConfig getKeycloakConfig() {
		return this.configValues.getKeycloakConfig();
    }
    
    public void setKeycloakConfig(KeycloakConfig config) {
        this.configValues.setKeycloakConfig(config);
    }

}
