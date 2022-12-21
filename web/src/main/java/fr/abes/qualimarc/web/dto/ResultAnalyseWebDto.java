package fr.abes.qualimarc.web.dto;

import fr.abes.qualimarc.web.dto.reference.AnalyseWebDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResultAnalyseWebDto {

    private AnalyseWebDto quickAnalyse;

    private AnalyseWebDto completeAnalyse;

    public AnalyseWebDto focusAnalyse;
}
