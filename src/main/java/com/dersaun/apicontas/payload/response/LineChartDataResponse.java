package com.dersaun.apicontas.payload.response;

import com.dersaun.apicontas.dto.DatasetDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.eclipse.collections.api.list.MutableList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class LineChartDataResponse extends ChartDataResponse{

    private MutableList<String> legend;

    public LineChartDataResponse(MutableList<String> legends, MutableList<String> labels, MutableList<DatasetDTO> datasets) {
        super(labels, datasets);
        this.legend = legends;
    }

}
