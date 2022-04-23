package com.dersaun.apicontas.dto;

import com.dersaun.apicontas.dao.models.Pessoa;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.collections.api.map.MutableMap;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MesAnoChart implements Comparable<MesAnoChart>{
    private Long id;
    private String nome;
    private Integer ano;
    private Long fechamento;
    private Integer anoFechamento;
    private MutableMap<Pessoa, BigDecimal> lancamentos;

    @Override
    public int compareTo(@NotNull MesAnoChart o) {
        if (id < o.getId() ){
            return -1;
        } else if (id.equals(o.getId())) {
            return 0;
        }
        return 1;
    }
}
