package fr.abes.qualimarc.core.model.resultats;

import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.utils.TypeThese;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ResultRules {
    private String ppn;

    private String dateModification;
    private String rcr;
    private FamilleDocument familleDocument;
    private TypeThese typeThese;
    private String titre;
    private String auteur;
    private String isbn;
    private String ocn;
    private List<String> messages;
    private List<ResultRule> detailErreurs;

    public ResultRules(String ppn) {
        this.ppn = ppn;
        this.messages = new ArrayList<>();
        this.detailErreurs = new ArrayList<>();
    }

    public void addMessage(String message) {
        this.messages.add(message);
    }

    public void addDetailErreur(ResultRule resultRule){
        this.detailErreurs.add(resultRule);
    }
}
