package fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import fr.abes.qualimarc.core.utils.TypeVerification;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "ENUM_TYPE_DE_VERIFICATION")
    @NotNull
    @Enumerated(EnumType.STRING)
    private TypeVerification typeDeVerification;

    public Indicateur(Integer id, String zone, Boolean affichageEtiquette,  Integer indicateur, String valeur, TypeVerification typeDeVerification) {
        super(id, zone, affichageEtiquette);
        this.indicateur = indicateur;
        this.valeur = valeur;
        this.typeDeVerification = typeDeVerification;
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
                    datafieldList.stream().filter(df -> isValueIndicateurValidWithTypeVerification(df)).collect(Collectors.toList())
            );
            return this.getComplexRule().isSavedZoneIsNotEmpty();
        } else {
            return datafieldList.stream().anyMatch(df -> isValueIndicateurValidWithTypeVerification(df));
        }
    }

    public boolean isValueIndicateurValidWithTypeVerification(Datafield df) {
        String indicateurCible = this.indicateur == 1 ? df.getInd1() : df.getInd2();
        boolean isOk = false;
        if (this.typeDeVerification == TypeVerification.STRICTEMENT) {
            isOk = indicateurCible.equals(this.valeur) || (this.valeur.equals("#") && indicateurCible.equals(" "));
        } else if (this.typeDeVerification == TypeVerification.STRICTEMENTDIFFERENT) {
            if (this.valeur.equals("#")) {
                isOk = !indicateurCible.equals(" ");
            } else {
                isOk = !indicateurCible.equals(this.valeur);
            }
        }
        return isOk;
    }

    @Override
    public List<String> getZones() {
        List<String> listZones = new ArrayList<>();
        listZones.add(this.zone);
        return listZones;
    }
}
