package co.edu.login.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokerResponse {
    @JsonProperty("acceso_token")
    String acceso_token;
    @JsonProperty("refrescar_token")
    String refrescar_token;
}
