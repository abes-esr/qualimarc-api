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
import java.util.ArrayList;
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
        NoticeXml notice = notices[0];
        List<Datafield> datafieldList = notice.getDatafields().stream().filter(d -> d.getTag().equals(this.getZone())).collect(Collectors.toList());
        if(datafieldList.isEmpty()) {
            return false;
        }
        if (this.getComplexRule() != null && this.getComplexRule().isMemeZone()) {
            this.getComplexRule().setSavedZone(
                    // Collecte dans une liste toutes les zones qui CONTIENNENT l'indicateur cible
                    datafieldList.stream().filter(df -> {
                        String indicateurCible = this.indicateur == 1 ? df.getInd1() : df.getInd2();
                        return indicateurCible.equals(this.valeur) || (this.valeur.equals("#") && indicateurCible.equals(" "));
                    }).collect(Collectors.toList())
            );
            return this.getComplexRule().isSavedZoneIsNotEmpty();
        } else {
            return datafieldList.stream().anyMatch(df -> {
                String indicateurCible = this.indicateur == 1 ? df.getInd1() : df.getInd2();
                return indicateurCible.equals(this.valeur) || (this.valeur.equals("#") && indicateurCible.equals(" "));
            });
        }
    }

    @Override
    public List<String> getZones() {
        List<String> listZones = new ArrayList<>();
        listZones.add(this.zone);
        return listZones;
    }
}
