package co.edu.login.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor

public class TokenResponse {
    @JsonProperty("acceso_token")
    String acceso_token;
    @JsonProperty("refrescar_token")
    String refrescar_token;
}
