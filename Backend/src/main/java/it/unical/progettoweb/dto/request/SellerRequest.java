package it.unical.progettoweb.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class SellerRequest {

    private String name;
    private String surname;
    private String email;
    private String vatNumber;
    private Date birthDate;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String otp;
}