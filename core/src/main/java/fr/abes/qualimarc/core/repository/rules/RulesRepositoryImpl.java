package fr.abes.qualimarc.core.repository.rules;

import fr.abes.qualimarc.core.model.entity.rules.Rule;
import fr.abes.qualimarc.core.model.entity.rules.structure.PresenceZone;
import fr.abes.qualimarc.core.utils.Priority;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
public class RulesRepositoryImpl implements RulesRepository {
    @Override
    public Set<Rule> findByPriority(Priority priority) {
        Set<Rule> ruleList = new HashSet<>();
        //  First rule of the advanced rule set
        ruleList.add(new PresenceZone(1, "La zone 010 doit être présente", "010", Priority.P2, true));
        return ruleList;
    }

    @Override
    public Set<Rule> findAll() {
        Set<Rule> ruleList = new HashSet<>();
        //  First rule of the advanced rule set
        ruleList.add(new PresenceZone(1, "La zone 010 doit être présente", "010", Priority.P2, true));
        return ruleList;
    }

    @Override
    public Set<Rule> findByTypeDocument(String typeDocument) {
        return null;
    }

    @Override
    public Set<Rule> findByRuleSet(Integer ruleSet) {
        return null;
    }
}
