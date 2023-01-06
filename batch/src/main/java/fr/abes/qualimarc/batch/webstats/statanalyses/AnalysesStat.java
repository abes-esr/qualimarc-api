package fr.abes.qualimarc.batch.webstats.statanalyses;

import com.opencsv.CSVWriter;
import fr.abes.qualimarc.batch.webstats.Export;
import fr.abes.qualimarc.core.model.entity.qualimarc.journal.JournalAnalyse;
import fr.abes.qualimarc.core.repository.qualimarc.JournalAnalyseRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AnalysesStat extends Export<JournalAnalyse> {
    private final JournalAnalyseRepository journalAnalyseRepository;
    private Date dateDebut;
    private Date dateFin;

    public AnalysesStat(JournalAnalyseRepository journalAnalyseRepository, Date date) {
        this.journalAnalyseRepository = journalAnalyseRepository;
        this.dateDebut = date;
        Calendar calendarDeFin = Calendar.getInstance();
        calendarDeFin.setTime(date);
        calendarDeFin.add(Calendar.MONTH, 1); // Je rajoute un mois pour arriver au mois suivant (en effet, comme je fais 01, je suis au début du mois hors je dois passer à mes stats la date max de recherche
        this.dateFin = calendarDeFin.getTime();
    }


    @Override
    protected void headerToCsv(CSVWriter writer) {
        writer.writeNext(new String[]{"id", "date", "isReplayed", "typeAnalyse", "typeDoc", "ruleSet", "nbPpnAnalyse","nbPpnErreur","nbPpnOk","nbPpnInconnus"});
    }

    @Override
    protected void lineToCsv(CSVWriter writer, JournalAnalyse dto) {
        writer.writeNext(new String[]{dto.getId().toString(), dto.getDateTime().toString(), dto.isReplayed() ? "1":"0", dto.getTypeAnalyse().toString(), dto.getTypeDocument(), dto.getRuleSet(), dto.getNbPpnAnalyse().toString(), dto.getNbPpnErreur().toString(), dto.getNbPpnOk().toString(), dto.getNbPpnInconnus().toString()});
    }

    @Override
    protected List<JournalAnalyse> getTuples() {
        return journalAnalyseRepository.findAllByDateTimeBetween(this.dateDebut, this.dateFin);
    }
}
