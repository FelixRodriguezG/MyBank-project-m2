package io.github.felix.bank_back.model.embedded;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Embeddable
public class Address {
    @NotBlank
    private String street;
    @NotBlank
    private String city;
    @NotBlank
    @Pattern(regexp = "[A-Za-z0-9]{5,10}", message = "El código postal debe tener entre 5 y 10 caracteres alfanuméricos")
    private String zipCode;
    @NotBlank
    private String country;
}
