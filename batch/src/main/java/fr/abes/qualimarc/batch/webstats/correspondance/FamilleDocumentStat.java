package fr.abes.qualimarc.batch.webstats.correspondance;

import com.opencsv.CSVWriter;
import fr.abes.qualimarc.batch.webstats.Export;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.repository.qualimarc.FamilleDocumentRepository;

import java.util.List;

public class FamilleDocumentStat extends Export<FamilleDocument> {
    private final FamilleDocumentRepository familleDocumentRepository;

    public FamilleDocumentStat(FamilleDocumentRepository familleDocumentRepository) {
        this.familleDocumentRepository = familleDocumentRepository;
    }

    @Override
    protected void lineToCsv(CSVWriter writer, FamilleDocument dto) {
        writer.writeNext(new String[]{dto.getId(), dto.getLibelle()});
    }

    @Override
    protected List<FamilleDocument> getTuples() {
        return familleDocumentRepository.findAll();
    }
}
