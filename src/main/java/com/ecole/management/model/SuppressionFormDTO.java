package com.ecole.management.model;

import jakarta.persistence.Temporal;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuppressionFormDTO {

    @NotNull(message = "L'équipement est requis")
    private Integer equipmentId;

    @Temporal(jakarta.persistence.TemporalType.DATE)
    @NotNull(message = "La date de suppression est requise")
    private Date dateSuppression;

    @NotBlank(message = "Le motif de suppression est requis")
    @Size(max = 500, message = "Le motif ne doit pas dépasser 500 caractères")
    private String motifSuppression;

    @Size(max = 800, message = "Les observations ne doivent pas dépasser 800 caractères")
    private String observations;

    @Size(max = 200, message = "Le responsable ne doit pas dépasser 200 caractères")
    private String responsable;
}