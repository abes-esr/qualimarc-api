package fr.abes.qualimarc.core.model.entity.qualimarc.rules.dependance;

import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Reciprocite extends SimpleRule {
    private String zoneCible;
    private String sousZoneCible;

    public Reciprocite(String zoneCible, String sousZoneCible) {
        this.zoneCible = zoneCible;
        this.sousZoneCible = sousZoneCible;
    }

    @Override
    public boolean isValid(NoticeXml notice) {
        return false;
    }

    @Override
    public String getZones() {
        return null;
    }
}
