package fr.abes.qualimarc.core.model.entity.qualimarc.rules.dependance;

import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Reciprocite extends SimpleRule {
    private String sousZoneSource;
    private String zoneCible;
    private String sousZoneCible;

    @Override
    public boolean isValid(NoticeXml... notices) {
        return false;
    }

    @Override
    public String getZones() {
        return null;
    }
}
