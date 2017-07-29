package com.voicerecognition;

/**
 * Created by ghanendra on 29/07/2017.
 */

public class NewProfileModel {
    private String username,identificationProfileId;

    public NewProfileModel(String username, String identificationProfileId) {
        this.username = username;
        this.identificationProfileId = identificationProfileId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIdentificationProfileId() {
        return identificationProfileId;
    }

    public void setIdentificationProfileId(String identificationProfileId) {
        this.identificationProfileId = identificationProfileId;
    }
}
