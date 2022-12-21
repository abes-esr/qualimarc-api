package fr.abes.qualimarc.web.dto;

import fr.abes.qualimarc.web.dto.reference.AnalyseWebDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResultAnalyseWebDto {

    private List<AnalyseWebDto> analyses = new ArrayList<>();

    public void addAnalyse(AnalyseWebDto analyse){
        analyses.add(analyse);
    }
}
