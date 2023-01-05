package fr.abes.qualimarc.core.model.resultats;

import fr.abes.qualimarc.core.model.entity.qualimarc.statistiques.StatsMessages;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

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

    public void merge(ResultAnalyse resultAnalyse) {
        this.resultRules.addAll(resultAnalyse.getResultRules());
        this.ppnAnalyses.addAll(resultAnalyse.getPpnAnalyses());
        this.ppnOk.addAll(resultAnalyse.getPpnOk());
        this.ppnInconnus.addAll(resultAnalyse.getPpnInconnus());
        this.ppnErrones.addAll(resultAnalyse.getPpnErrones());
        resultAnalyse.getStatsMessagesList().forEach(this::mergeStatsMessages);
    }

    /**
     * Vérifie si le message est déjà présent dans l'objet courant et gère le nombre d'occurrences
     * @param statsMessages
     */
    private void mergeStatsMessages(StatsMessages statsMessages) {
        Optional<StatsMessages> statsMessagesOpt = this.statsMessagesList.stream().filter(sm -> sm.getMessage().equals(statsMessages.getMessage())).findFirst();
        if (statsMessagesOpt.isPresent()) {
            //message trouvé dans l'objet on incrémente le nombre d'occurrences
            statsMessagesOpt.get().addOccurrence(statsMessages.getOccurrences());
        } else {
            //message non trouvé, on ajoute l'objet à l'objet courant
            this.statsMessagesList.add(statsMessages);
        }
    }

    /**
     * Vérifie si un message est présent dans l'objet courant et gère le nombre d'occurrence
     * @param message
     */
    public void mergeStatsMessages(String message) {
        Optional<StatsMessages> statsMessages = this.statsMessagesList.stream().filter(sm -> sm.getMessage().equals(message)).findFirst();
        if (statsMessages.isPresent()) {
            //message trouvé, on ajoute 1 au nombre d'occurrence du message dans l'objet courant
            statsMessages.get().addOccurrence();
        } else {
            //message non trouvé, on l'ajout à la liste
            this.statsMessagesList.add(new StatsMessages(message));
        }
    }
}
