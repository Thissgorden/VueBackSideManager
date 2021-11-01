package com.gdlearn.backsidemanager.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class PassDto implements Serializable {

    @NotBlank
    private String currentPass;
    @NotBlank
    private String newPass;
    @NotBlank
    private String confirmPass;
}
