package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

import fr.abes.qualimarc.core.model.entity.notice.Datafield;
import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.notice.SubField;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.positions.PositionsOperator;
import fr.abes.qualimarc.core.utils.BooleanOperateur;
import fr.abes.qualimarc.core.utils.ComparaisonOperateur;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "RULE_POSITIONSOUSZONE")
public class PositionSousZone extends SimpleRule implements Serializable {
    @Column(name = "SOUS_ZONE")
    private String sousZone;
    @Column(name = "POSITION")
    @OneToMany(mappedBy = "positionSousZone", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PositionsOperator> positions;
    @Column(name = "OPERATEUR")
    @Enumerated(EnumType.STRING)
    private BooleanOperateur operateur;

    public PositionSousZone(Integer id, String zone, String sousZone, List<PositionsOperator> positions, BooleanOperateur operateur) {
        super(id, zone);
        this.sousZone = sousZone;
        this.positions = positions;
        this.operateur = operateur;
    }

    @Override
    public boolean isValid(NoticeXml... notices) {
        NoticeXml notice = notices[0];
        //récupération de toutes les zones définies dans la règle
        List<Datafield> datafieldList = notice.getDatafields().stream().filter(
                datafield -> datafield.getTag().equals(this.getZone())
        ).collect(Collectors.toList());

        if (datafieldList.isEmpty())
            return false;

        if (positions == null || positions.isEmpty()) return false;

        // Cas simple : une seule position
        if (positions.size() == 1) {
            Predicate<Datafield> predicate = buildPredicate(positions.get(0));
            List<Datafield> datafieldsValid = datafieldList.stream()
                    .filter(predicate)
                    .collect(Collectors.toList());

            if (this.getComplexRule() != null && this.getComplexRule().isMemeZone()) {
                this.getComplexRule().setSavedZone(datafieldsValid);
                return this.getComplexRule().isSavedZoneIsNotEmpty();
            }

            return !datafieldsValid.isEmpty();
        }

        // Cas multiple : plusieurs positions
        List<Predicate<Datafield>> predicates = positions.stream()
                .map(this::buildPredicate)
                .collect(Collectors.toList());

        boolean valid;

        if (operateur == BooleanOperateur.OU) {
            valid = datafieldList.stream().anyMatch(
                    df -> predicates.stream().anyMatch(pred -> pred.test(df))
            );
        } else { // ET
            valid = datafieldList.stream().anyMatch(
                    df -> predicates.stream().allMatch(pred -> pred.test(df))
            );
        }

        if (this.getComplexRule() != null && this.getComplexRule().isMemeZone()) {
            // facultatif : on peut sauvegarder ceux qui matchent tous les critères
            List<Datafield> datafieldsValid = datafieldList.stream()
                    .filter(df -> predicates.stream().allMatch(pred -> pred.test(df)))
                    .collect(Collectors.toList());

            this.getComplexRule().setSavedZone(datafieldsValid);
            return this.getComplexRule().isSavedZoneIsNotEmpty();
        }

        return valid;
    }

    private Predicate<Datafield> buildPredicate(PositionsOperator posOp) {
        return df -> {
            List<String> codes = df.getSubFields().stream()
                    .map(SubField::getCode)
                    .collect(Collectors.toList());

            int size = codes.size();
            int expectedIndex = (posOp.getPosition() == -1) ? (size - 1) : (posOp.getPosition() - 1);

            // Trouver toutes les positions de la sous-zone
            List<Integer> occurrences = new ArrayList<>();
            for (int i = 0; i < codes.size(); i++) {
                if (Objects.equals(codes.get(i), sousZone)) {
                    occurrences.add(i);
                }
            }

            if (occurrences.isEmpty()) {
                return false;
            }

            // Vérifie si au moins une occurrence valide la condition
            return occurrences.stream().anyMatch(index -> compare(index, expectedIndex, posOp.getComparaisonOperateur()));
        };
    }

    private boolean compare(Integer index, int expectedIndex, ComparaisonOperateur comparaisonOperateur) {
        switch (comparaisonOperateur) {
            case EGAL: return index == expectedIndex;
            case DIFFERENT: return index != expectedIndex;
            case INFERIEUR: return index < expectedIndex;
            case SUPERIEUR: return index > expectedIndex;
            case INFERIEUR_EGAL: return index <= expectedIndex;
            case SUPERIEUR_EGAL: return index >= expectedIndex;
            default: return false;
        }
    }

    @Override
    public List<String> getZones() {
        List<String> listZones = new ArrayList<>();
        listZones.add(this.zone + "$" + this.sousZone);
        return listZones;
    }
}
