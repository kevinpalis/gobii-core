package org.gobiiproject.gobiibrapi.calls.login;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Phil on 12/15/2016.
 */
public class BrapiRequestLogin {

    private String clientId;
    private String grantType;
    private String password;
    private String userName;

    @JsonProperty("client_id")
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @JsonProperty("grant_type")
    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }


    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty("username")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
