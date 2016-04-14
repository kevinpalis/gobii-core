package org.gobiiproject.gobiidao.entity.pojos;
// Generated Mar 31, 2016 1:44:38 PM by Hibernate Tools 3.2.2.GA


import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Role generated by hbm2java
 */
@Entity
@Table(name="role"
    ,schema="public"
)
public class Role  implements java.io.Serializable {


     private int roleId;
     private String roleName;
     private String roleCode;
     private Serializable readTables;
     private Serializable writeTables;

    public Role() {
    }

	
    public Role(int roleId, String roleName, String roleCode) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.roleCode = roleCode;
    }

    public Role(int roleId, String roleName, String roleCode, Serializable readTables, Serializable writeTables) {
       this.roleId = roleId;
       this.roleName = roleName;
       this.roleCode = roleCode;
       this.readTables = readTables;
       this.writeTables = writeTables;
    }
   
     @Id 
    
    @Column(name="role_id", unique=true, nullable=false)
    public int getRoleId() {
        return this.roleId;
    }
    
    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }
    
    @Column(name="role_name", nullable=false)
    public String getRoleName() {
        return this.roleName;
    }
    
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    
    @Column(name="role_code", nullable=false)
    public String getRoleCode() {
        return this.roleCode;
    }
    
    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }
    
    @Column(name="read_tables")
    public Serializable getReadTables() {
        return this.readTables;
    }
    
    public void setReadTables(Serializable readTables) {
        this.readTables = readTables;
    }
    
    @Column(name="write_tables")
    public Serializable getWriteTables() {
        return this.writeTables;
    }
    
    public void setWriteTables(Serializable writeTables) {
        this.writeTables = writeTables;
    }




}

