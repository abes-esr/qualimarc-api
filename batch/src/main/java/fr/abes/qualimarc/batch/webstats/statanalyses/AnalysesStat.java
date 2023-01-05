package fr.abes.qualimarc.batch.webstats.statanalyses;

import com.opencsv.CSVWriter;
import fr.abes.qualimarc.batch.webstats.Export;
import fr.abes.qualimarc.core.model.entity.qualimarc.statistiques.JournalAnalyse;

import java.util.List;

public class AnalysesStat extends Export<JournalAnalyse> {
    @Override
    protected void lineToCsv(CSVWriter writer, JournalAnalyse dto) {

    }

    @Override
    protected List<JournalAnalyse> getTuples() {
        return null;
    }
}
