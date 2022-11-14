package fr.abes.qualimarc.core.model.entity.basexml;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Clob;

@Entity
@NoArgsConstructor
@Setter
@Getter
@Table(name = "NOTICES", schema = "AUTORITES")
public class NoticesAutorite implements Serializable {
    @Id
    @Column(name = "ID")
    private Integer id;

    @Column(name = "PPN")
    private String ppn;

    @Column(name = "DATA_XML")
    @ColumnTransformer(read = "XMLSERIALIZE (CONTENT data_xml as CLOB)", write = "NULLSAFE_XMLTYPE(?)")
    @Lob
    //Type Clob pour pouvoir récupérer les notices de plus de 4000 caractères
    private Clob dataXml;
}
