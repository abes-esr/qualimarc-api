package fr.abes.qualimarc.core.repository.rules;

import fr.abes.qualimarc.core.model.entity.rules.Rule;
import fr.abes.qualimarc.core.utils.Priority;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Set;

@NoRepositoryBean
public interface RulesRepository {
    Set<Rule> findByPriority(Priority priority);

    Set<Rule> findAll();

    Set<Rule> findByTypeDocument(String typeDocument);

    Set<Rule> findByRuleSet(Integer ruleSet);
}
