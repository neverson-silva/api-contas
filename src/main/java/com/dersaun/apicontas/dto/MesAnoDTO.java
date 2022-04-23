package com.dersaun.apicontas.dto;

import com.dersaun.apicontas.dao.models.Mes;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class MesAnoDTO implements Serializable {

    @NonNull
    Long mesReferencia;

    @NonNull
    Integer anoReferencia;

    Mes mes;
}
