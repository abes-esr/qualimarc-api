package fr.abes.qualimarc.batch.webstats.statmessages;

import com.opencsv.CSVWriter;
import fr.abes.qualimarc.batch.webstats.Export;
import fr.abes.qualimarc.core.model.entity.qualimarc.journal.JournalMessages;
import fr.abes.qualimarc.core.repository.qualimarc.JournalMessagesRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MessagesStats extends Export<JournalMessages> {

    private final JournalMessagesRepository journalMessagesRepository;
    private Date dateDebut;
    private Date dateFin;

    public MessagesStats(JournalMessagesRepository journalMessagesRepository, Date date) {
        this.journalMessagesRepository = journalMessagesRepository;
        this.dateDebut = date;
        Calendar calendarDeFin = Calendar.getInstance();
        calendarDeFin.setTime(date);
        calendarDeFin.add(Calendar.MONTH, 1); // Je rajoute un mois pour arriver au mois suivant (en effet, comme je fais 01, je suis au début du mois hors je dois passer à mes stats la date max de recherche
        calendarDeFin.add(Calendar.DAY_OF_MONTH, -1);
        this.dateFin = calendarDeFin.getTime();
    }

    @Override
    protected void headerToCsv(CSVWriter writer) {
        writer.writeNext(new String[]{"id", "date", "message", "occurrences"});
    }

    @Override
    protected void lineToCsv(CSVWriter writer, JournalMessages dto) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        writer.writeNext(new String[]{dto.getId().toString(), format.format(dateFin), dto.getMessage(), dto.getOccurrences().toString()});
    }

    @Override
    protected List<JournalMessages> getTuples() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.dateDebut);
        return journalMessagesRepository.findByAnneeAndMois(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) +1);
    }
}
