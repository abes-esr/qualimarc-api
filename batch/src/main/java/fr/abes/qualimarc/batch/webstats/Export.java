package fr.abes.qualimarc.batch.webstats;

import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.util.Date;
import java.util.List;

@Slf4j
public abstract class Export<T> {

    public void generate(String destination, Date date) {
        log.info(destination + " [" + date + "]: Génération");
        try (CSVWriter writer = new CSVWriter(new FileWriter(destination), ';', CSVWriter.NO_QUOTE_CHARACTER)) {
            List<T> tuples = this.getTuples();
            this.headerToCsv(writer);
            tuples.forEach(t -> this.lineToCsv(writer, t));

        } catch (Exception e) {
            log.error(destination + " [" + date + "]: Erreur dans la création du fichier", e);
        }
    }

    protected abstract void headerToCsv(CSVWriter writer);

    protected abstract void lineToCsv(CSVWriter writer, T dto);

    protected abstract List<T> getTuples();
}
