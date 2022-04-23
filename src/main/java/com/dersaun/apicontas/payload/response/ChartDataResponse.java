package com.dersaun.apicontas.payload.response;

import com.dersaun.apicontas.dto.DatasetDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.collections.api.list.MutableList;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChartDataResponse implements Serializable {

    protected MutableList<String> labels;

    protected MutableList<DatasetDTO> datasets;
}
