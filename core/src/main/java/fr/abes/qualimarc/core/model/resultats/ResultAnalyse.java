package fr.abes.qualimarc.core.model.resultats;

import fr.abes.qualimarc.core.model.entity.qualimarc.statistiques.StatsMessages;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ResultAnalyse {
    private List<ResultRules> resultRules;
    private Set<String> ppnAnalyses;
    private Set<String> ppnErrones;
    private Set<String> ppnOk;
    private Set<String> ppnInconnus;
    private List<StatsMessages> statsMessagesList;

    public ResultAnalyse() {
        this.resultRules = new ArrayList<>();
        this.ppnAnalyses = new HashSet<>();
        this.ppnErrones = new HashSet<>();
        this.ppnOk = new HashSet<>();
        this.ppnInconnus = new HashSet<>();
        this.statsMessagesList = new ArrayList<>();
    }

    public void addResultRule(ResultRules rule) {
        this.resultRules.add(rule);
    }

    public void addPpnAnalyse(String ppn) {
        this.ppnAnalyses.add(ppn);
    }

    public void addPpnErrone(String ppn) {
        this.ppnErrones.add(ppn);
    }

    public void addPpnOk(String ppn) {
        this.ppnOk.add(ppn);
    }

    public void addPpnInconnu(String ppn) {
        this.ppnInconnus.add(ppn);
    }

    public void addStatsMessage(String messages) {
        this.statsMessagesList.add(new StatsMessages(messages));
    }

    public void merge(ResultAnalyse resultAnalyse) {
        this.resultRules.addAll(resultAnalyse.getResultRules());
        this.ppnAnalyses.addAll(resultAnalyse.getPpnAnalyses());
        this.ppnOk.addAll(resultAnalyse.getPpnOk());
        this.ppnInconnus.addAll(resultAnalyse.getPpnInconnus());
        this.ppnErrones.addAll(resultAnalyse.getPpnErrones());
        this.statsMessagesList.addAll(resultAnalyse.getStatsMessagesList());
    }
}
