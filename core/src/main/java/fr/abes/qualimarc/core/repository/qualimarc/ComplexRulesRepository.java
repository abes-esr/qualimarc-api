package fr.abes.qualimarc.core.repository.qualimarc;

import fr.abes.qualimarc.core.configuration.QualimarcConfiguration;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.RuleSet;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.ComplexRule;
import fr.abes.qualimarc.core.utils.Priority;
import fr.abes.qualimarc.core.utils.TypeThese;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@QualimarcConfiguration
@Repository
public interface ComplexRulesRepository extends JpaRepository<ComplexRule, Integer> {
    Set<ComplexRule> findByPriority(Priority priority);

    Set<ComplexRule> findByFamillesDocuments(FamilleDocument familleDocument);

    Set<ComplexRule> findByRuleSet(RuleSet ruleSet);

    Set<ComplexRule> findByTypesThese(TypeThese typeThese);

}
