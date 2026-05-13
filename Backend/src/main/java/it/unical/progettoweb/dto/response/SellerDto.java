package it.unical.progettoweb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerDto {
    private Integer id;
    private String name;
    private String surname;
    private String email;
    private String vatNumber;
    private Date birthDate;
    private String role;
}