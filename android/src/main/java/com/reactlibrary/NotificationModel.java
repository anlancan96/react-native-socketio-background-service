package com.reactlibrary;

import com.google.gson.annotations.SerializedName;

public class NotificationModel {
    @SerializedName("to")
    private String ToUserId;

    @SerializedName("from")
    private PersonModel FromUser;

    @SerializedName("type")
    private String RequestType;

    @SerializedName("channel")
    private String Channel;

    public  NotificationModel() {}

    public String getToUserId() {
        return ToUserId;
    }

    public void setToUserId(String toUserId) {
        ToUserId = toUserId;
    }

    public PersonModel getFromUser() {
        return FromUser;
    }

    public void setFromUser(PersonModel fromUser) {
        FromUser = fromUser;
    }

    public String getRequestType() {
        return RequestType;
    }

    public void setRequestType(String requestType) {
        RequestType = requestType;
    }

    public String getChannel() {
        return Channel;
    }

    public void setChannel(String channel) {
        Channel = channel;
    }
}
