package fr.abes.qualimarc.core.model.entity.rules.structure;

import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.rules.Rule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PresenceSousZone extends Rule {

    private String sousZone;
    private boolean isPresent;

    public PresenceSousZone(Integer id, String message, String zone, String sousZone, boolean isPresent) {
        super(id, message, zone);
        this.sousZone = sousZone;
        this.isPresent = isPresent;
    }

    public PresenceSousZone(Integer id, String message, String zone, List<String> typeDocuments, String sousZone, boolean isPresent) {
        super(id, message, zone, typeDocuments);
        this.sousZone = sousZone;
        this.isPresent = isPresent;
    }

    @Override
    public boolean isValid(NoticeXml notice) {
        if(this.isPresent) {    //  renvoyer TRUE si la sous-zone est présente et FALSE si elle ne l'est pas
            return notice.getDatafields().stream().anyMatch(dataField -> {
                if(dataField.getTag().equals(this.getZone())){
                    return dataField.getSubFields().stream().anyMatch(subField -> subField.getCode().equals(this.getSousZone()));
                }
                return dataField.getSubFields().stream().anyMatch(subField -> subField.getCode().equals(this.getSousZone()));
            });
        }
//        return notice.getDatafields().stream().noneMatch(dataField -> dataField.getTag().equals(this.getZone()));
        return notice.getDatafields().stream().anyMatch(dataField -> {  //  renvoyer TRUE si la sous-zone est absente et FALSE si elle ne l'est pas
            if(dataField.getTag().equals(this.getZone())){
                return dataField.getSubFields().stream().noneMatch(subField -> subField.getCode().equals(this.getSousZone()));
            }
            return dataField.getSubFields().stream().noneMatch(subField -> subField.getCode().equals(this.getSousZone()));
        });
    }
}
