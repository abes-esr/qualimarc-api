package fr.abes.qualimarc.batch.webstats.statanalyses;

import com.opencsv.CSVWriter;
import fr.abes.qualimarc.batch.webstats.Export;
import fr.abes.qualimarc.core.model.entity.qualimarc.journal.JournalRuleSet;
import fr.abes.qualimarc.core.repository.qualimarc.JournalRuleSetRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RuleSetStat extends Export<JournalRuleSet> {
    private final JournalRuleSetRepository journalRuleSetRepository;
    private Date dateDebut;
    private Date dateFin;

    public RuleSetStat(JournalRuleSetRepository journalRuleSetRepository, Date date) {
        this.journalRuleSetRepository = journalRuleSetRepository;
        this.dateDebut = date;
        Calendar calendarDeFin = Calendar.getInstance();
        calendarDeFin.setTime(date);
        calendarDeFin.add(Calendar.MONTH, 1); // Je rajoute un mois pour arriver au mois suivant (en effet, comme je fais 01, je suis au début du mois hors je dois passer à mes stats la date max de recherche
        this.dateFin = calendarDeFin.getTime();
    }

    @Override
    protected void headerToCsv(CSVWriter writer) {
        writer.writeNext(new String[] {"id", "date", "idRuleSet"});
    }

    @Override
    protected void lineToCsv(CSVWriter writer, JournalRuleSet dto) {
        writer.writeNext(new String[] {dto.getId().toString(), dto.getDateTime().toString(), dto.getRuleSetId().toString()});
    }

    @Override
    protected List<JournalRuleSet> getTuples() {
        return journalRuleSetRepository.findAllByDateTimeBetween(dateDebut, dateFin);
    }
}
