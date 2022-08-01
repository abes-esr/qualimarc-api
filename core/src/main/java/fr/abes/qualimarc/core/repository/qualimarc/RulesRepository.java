package fr.abes.qualimarc.core.repository.qualimarc;

import fr.abes.qualimarc.core.configuration.QualimarcConfiguration;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.RuleSet;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.Rule;
import fr.abes.qualimarc.core.utils.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@QualimarcConfiguration
@Repository
public interface RulesRepository extends JpaRepository<Rule, Integer> {
    Set<Rule> findByPriority(Priority priority);

    Set<Rule> findByFamillesDocuments(FamilleDocument familleDocument);

    Set<Rule> findByRuleSet(RuleSet ruleSet);
}
