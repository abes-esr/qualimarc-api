package fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure;

import fr.abes.qualimarc.core.model.entity.notice.NoticeXml;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.ComplexRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.LinkedRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import fr.abes.qualimarc.core.utils.BooleanOperateur;
import fr.abes.qualimarc.core.utils.Priority;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "RULE_GROUPEMEMEZONE")
public class GroupeMemeZone extends SimpleRule implements Serializable {
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_FIRST_RULE")
    @NotNull
    private SimpleRule firstRule;

    @OneToMany(mappedBy = "groupeMemeZone", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    private List<GroupeMemeZoneRegle> regles = new LinkedList<>();

    public GroupeMemeZone(Integer id, String zone, Boolean affichageEtiquette, SimpleRule firstRule) {
        super(id, zone, affichageEtiquette);
        this.firstRule = firstRule;
    }

    public void addRegle(SimpleRule rule, BooleanOperateur operateur, Integer position) {
        this.regles.add(new GroupeMemeZoneRegle(rule, operateur, position, this));
    }

    @Override
    public boolean isValid(NoticeXml... notices) {
        if (notices.length == 0 || this.firstRule == null) {
            return false;
        }

        ComplexRule internalRule = new ComplexRule(this.id, "groupe-meme-zone", Priority.P1, this.firstRule);
        internalRule.setMemeZone(true);

        this.regles.stream()
                .sorted(Comparator.comparing(GroupeMemeZoneRegle::getPosition))
                .forEach(regle -> internalRule.addOtherRule(new LinkedRule(regle.getRule(), regle.getOperateur(), regle.getPosition(), internalRule)));

        return internalRule.isValid(notices[0]);
    }

    @Override
    public List<String> getZones() {
        List<String> zones = new ArrayList<>(this.firstRule.getZones());
        this.regles.forEach(regle -> zones.addAll(regle.getRule().getZones()));
        return zones.stream().distinct().toList();
    }
}
