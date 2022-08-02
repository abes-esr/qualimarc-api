package fr.abes.qualimarc.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ControllingPpnWithRuleSetsResponseDto {

    private String ppn;

    private List<String> messagesList;

    public ControllingPpnWithRuleSetsResponseDto(String ppn, List<String> messagesList) {
        this.ppn = ppn;
        this.messagesList = messagesList;
    }
}
