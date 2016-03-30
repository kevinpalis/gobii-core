package org.gobiiproject.gobiidao.generated.entities;
// Generated Mar 30, 2016 11:56:04 AM by Hibernate Tools 3.2.2.GA


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Platform generated by hbm2java
 */
@Entity
@Table(name="platform"
    ,schema="public"
)
public class Platform  implements java.io.Serializable {


     private int platformId;
     private String platformName;
     private String platformCode;
     private String platformVendor;
     private String platformDescription;
     private String createdBy;
     private Date createdDate;
     private String modifiedBy;
     private Date modifiedDate;
     private int status;

    public Platform() {
    }

	
    public Platform(int platformId, String platformName, String platformCode, String platformVendor, String createdBy, Date createdDate, String modifiedBy, int status) {
        this.platformId = platformId;
        this.platformName = platformName;
        this.platformCode = platformCode;
        this.platformVendor = platformVendor;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.modifiedBy = modifiedBy;
        this.status = status;
    }
    public Platform(int platformId, String platformName, String platformCode, String platformVendor, String platformDescription, String createdBy, Date createdDate, String modifiedBy, Date modifiedDate, int status) {
       this.platformId = platformId;
       this.platformName = platformName;
       this.platformCode = platformCode;
       this.platformVendor = platformVendor;
       this.platformDescription = platformDescription;
       this.createdBy = createdBy;
       this.createdDate = createdDate;
       this.modifiedBy = modifiedBy;
       this.modifiedDate = modifiedDate;
       this.status = status;
    }
   
     @Id 
    
    @Column(name="platform_id", unique=true, nullable=false)
    public int getPlatformId() {
        return this.platformId;
    }
    
    public void setPlatformId(int platformId) {
        this.platformId = platformId;
    }
    
    @Column(name="platform_name", nullable=false)
    public String getPlatformName() {
        return this.platformName;
    }
    
    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }
    
    @Column(name="platform_code", nullable=false)
    public String getPlatformCode() {
        return this.platformCode;
    }
    
    public void setPlatformCode(String platformCode) {
        this.platformCode = platformCode;
    }
    
    @Column(name="platform_vendor", nullable=false)
    public String getPlatformVendor() {
        return this.platformVendor;
    }
    
    public void setPlatformVendor(String platformVendor) {
        this.platformVendor = platformVendor;
    }
    
    @Column(name="platform_description")
    public String getPlatformDescription() {
        return this.platformDescription;
    }
    
    public void setPlatformDescription(String platformDescription) {
        this.platformDescription = platformDescription;
    }
    
    @Column(name="created_by", nullable=false)
    public String getCreatedBy() {
        return this.createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    @Temporal(TemporalType.DATE)
    @Column(name="created_date", nullable=false, length=13)
    public Date getCreatedDate() {
        return this.createdDate;
    }
    
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    
    @Column(name="modified_by", nullable=false)
    public String getModifiedBy() {
        return this.modifiedBy;
    }
    
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
    @Temporal(TemporalType.DATE)
    @Column(name="modified_date", length=13)
    public Date getModifiedDate() {
        return this.modifiedDate;
    }
    
    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
    
    @Column(name="status", nullable=false)
    public int getStatus() {
        return this.status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }




}


