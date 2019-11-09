package com.reactlibrary;

import com.google.gson.annotations.SerializedName;

public class PersonModel {
    @SerializedName("_id")
    private String PersonId;

    @SerializedName("name")
    private String Name;

    public String getPersonId() {
        return PersonId;
    }

    public void setPersonId(String personId) {
        PersonId = personId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
