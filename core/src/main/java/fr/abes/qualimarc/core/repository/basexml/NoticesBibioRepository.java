package fr.abes.qualimarc.core.repository.basexml;

import fr.abes.qualimarc.core.configuration.BaseXMLConfiguration;
import fr.abes.qualimarc.core.model.entity.basexml.NoticesBibio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@BaseXMLConfiguration
@Repository
public interface NoticesBibioRepository extends JpaRepository<NoticesBibio, Integer> {
    Optional<NoticesBibio> getByPpn(String ppn);

    @Query(
            value = """
                    SELECT DATE_ETAT
                    FROM (
                        SELECT /*+ INDEX_DESC(NB INDEX_DATE_ETAT_BIBIO) */ NB.DATE_ETAT
                        FROM AUTORITES.NOTICESBIBIO NB
                        WHERE NB.DATE_ETAT < SYSDATE
                        ORDER BY NB.DATE_ETAT DESC
                    )
                    WHERE ROWNUM = 1
                    """,
            nativeQuery = true
    )
    Date findLatestDateEtat();
}
