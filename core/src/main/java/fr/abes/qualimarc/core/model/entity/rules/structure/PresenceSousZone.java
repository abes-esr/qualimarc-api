package fr.abes.qualimarc.core.model.entity.rules.structure;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.rules.Rule;
import fr.abes.qualimarc.core.utils.Priority;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class PresenceSousZone extends Rule {

    private String sousZone;
    private boolean isPresent;

    public PresenceSousZone(Integer id, String message, String zone, String sousZone, Priority priority, boolean isPresent) {
        super(id, message, zone, priority);
        this.sousZone = sousZone;
        this.isPresent = isPresent;
    }

    public PresenceSousZone(Integer id, String message, String zone, Priority priority, Set<String> typeDocuments, String sousZone, boolean isPresent) {
        super(id, message, zone, priority, typeDocuments);
        this.sousZone = sousZone;
        this.isPresent = isPresent;
    }

    @Override
    public boolean isValid(NoticeXml notice) {

        List<Datafield> datafields = notice.getDatafields().stream().filter(dataField -> dataField.getTag().equals(this.getZone())).collect(Collectors.toList());

        if(this.isPresent) {
            for (Datafield datafield : datafields) {
                if (datafield.getSubFields().stream().anyMatch(subField -> subField.getCode().equals(this.getSousZone()))) {
                    return true;
                }
            }
        } else {
            for (Datafield datafield : datafields) {
                if (datafield.getSubFields().stream().noneMatch(subField -> subField.getCode().equals(this.getSousZone()))) {
                    return true;
                }
            }
        }
        return false;
    }
}
