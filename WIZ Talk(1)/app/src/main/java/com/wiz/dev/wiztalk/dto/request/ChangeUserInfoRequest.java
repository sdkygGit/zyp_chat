package com.wiz.dev.wiztalk.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;

public class ChangeUserInfoRequest extends Request {

    @JsonProperty(value = "uid")
    public String uid;

    @JsonProperty(value = "mobile")
    public String mobile;

    @JsonProperty(value = "tel")
    public String tel;

    @JsonProperty(value = "email")
    public String email;

    @JsonProperty(value = "password")
    public String password;
}
