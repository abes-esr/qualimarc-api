package fr.abes.qualimarc.core.utils;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class QualimarcInitializer {
    @EventListener
    public void afterPropertiesSet(ContextRefreshedEvent event){
        Map<String, String> tempMap = new HashMap<>();
        tempMap.put("B", "AUDIOVISUEL");
        tempMap.put("K", "CARTE");
        tempMap.put("O", "DOC ELEC");
        tempMap.put("N", "ENREGISTREMENT");
        tempMap.put("I", "IMAGE");
        tempMap.put("F", "MANUSCRIT");
        tempMap.put("Z", "MULTIMEDIA");
        tempMap.put("V", "OBJET");
        tempMap.put("G", "MUSIQUE");
        tempMap.put("M", "PARTITION");
        tempMap.put("BD", "RESSOURCE CONTINUE");
        tempMap.put("A", "MONOGRAPHIE");
        tempMap.put("TS", "THESE SOUTENANCE");
        tempMap.put("TR", "THESE REPRODUCTION");
        TypeDocument.TYPE_DOCUMENT.putAll(tempMap);
    }
}
