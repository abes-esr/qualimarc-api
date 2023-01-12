package fr.abes.qualimarc.batch.webstats.statanalyses;

import com.opencsv.CSVWriter;
import fr.abes.qualimarc.batch.webstats.Export;
import fr.abes.qualimarc.core.model.entity.qualimarc.journal.JournalFamilleDocument;
import fr.abes.qualimarc.core.repository.qualimarc.JournalFamilleRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FamilleDocumentStat extends Export<JournalFamilleDocument> {
    private final JournalFamilleRepository journalFamilleRepository;
    private Date dateDebut;
    private Date dateFin;

    public FamilleDocumentStat(JournalFamilleRepository journalFamilleRepository, Date date) {
        this.journalFamilleRepository = journalFamilleRepository;
        this.dateDebut = date;
        Calendar calendarDeFin = Calendar.getInstance();
        calendarDeFin.setTime(date);
        calendarDeFin.add(Calendar.MONTH, 1); // Je rajoute un mois pour arriver au mois suivant (en effet, comme je fais 01, je suis au début du mois hors je dois passer à mes stats la date max de recherche
        this.dateFin = calendarDeFin.getTime();
    }

    @Override
    protected void headerToCsv(CSVWriter writer) {
        writer.writeNext(new String[]{"id", "date", "familleId"});
    }

    @Override
    protected void lineToCsv(CSVWriter writer, JournalFamilleDocument dto) {
        writer.writeNext(new String[]{dto.getId().toString(), dto.getDateTime().toString(), dto.getFamilleDocument()});
    }

    @Override
    protected List<JournalFamilleDocument> getTuples() {
        return journalFamilleRepository.findAllByDateTimeBetween(this.dateDebut, this.dateFin);
    }
}
