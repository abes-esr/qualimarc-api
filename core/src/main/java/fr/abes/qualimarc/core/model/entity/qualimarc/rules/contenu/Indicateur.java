package fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "RULE_INDICATEUR")
@Getter @Setter
@NoArgsConstructor
public class Indicateur extends SimpleRule implements Serializable {
    @Column(name = "INDICATEUR")
    @NotNull
    private Integer indicateur; // 1 ou 2

    @Column(name = "VALEUR")
    @NotNull
    //regex 1 seul caractere
    private String valeur;


    public Indicateur(Integer id, String zone, Integer indicateur, String valeur) {
        super(id, zone);
        this.indicateur = indicateur;
        this.valeur = valeur;
    }

    @Override
    public boolean isValid(NoticeXml ... notices) {
        if (notices.length > 0) {
            NoticeXml notice = Arrays.stream(notices).findFirst().get();
            List<Datafield> zonesSource = notice.getDatafields().stream().filter(d -> d.getTag().equals(this.getZone())).collect(Collectors.toList());
            for (Datafield datafield : zonesSource) {
                String indicateurCible = this.indicateur == 1 ? datafield.getInd1() : datafield.getInd2();
                // # est different selon la base XML consulté (ex: # en prod = ' ' en test)
                if (indicateurCible.equals(this.valeur) || ("#".equals(this.valeur) && indicateurCible.equals(" ")))
                    return true;
            }
        }
        return false;
    }

    @Override
    public String getZones() {
        return this.getZone();
    }
}
