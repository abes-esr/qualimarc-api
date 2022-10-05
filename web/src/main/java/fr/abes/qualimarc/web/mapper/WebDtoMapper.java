package fr.abes.qualimarc.web.mapper;

import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.Rule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.*;
import fr.abes.qualimarc.core.model.resultats.ResultAnalyse;
import fr.abes.qualimarc.core.model.resultats.ResultRules;
import fr.abes.qualimarc.core.utils.Priority;
import fr.abes.qualimarc.core.utils.UtilsMapper;
import fr.abes.qualimarc.web.dto.ResultAnalyseResponseDto;
import fr.abes.qualimarc.web.dto.ResultRulesResponseDto;
import fr.abes.qualimarc.web.dto.RuleResponseDto;
import fr.abes.qualimarc.web.dto.indexrules.*;
import lombok.SneakyThrows;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class WebDtoMapper {

    private final UtilsMapper mapper;

    public WebDtoMapper(UtilsMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Convertion d'un modèle PresenceZoneWebDto en modèle PresenceZone
     */
    @Bean
    public void converterPresenceZone() {
        Converter<PresenceZoneWebDto, PresenceZone> myConverter = new Converter<PresenceZoneWebDto, PresenceZone>() {
            @SneakyThrows
            public PresenceZone convert(MappingContext<PresenceZoneWebDto, PresenceZone> context) {
                PresenceZoneWebDto source = context.getSource();

                PresenceZone presenceZone = new PresenceZone();
                setChamp(source.getId(), source.getMessage(), source.getZone(), source.getPriority(), source.getTypesDoc(), presenceZone);

                presenceZone.setPresent(source.isPresent());
                return presenceZone;
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle PresenceSousZoneWebDto en modèle PresenceSousZone
     */
    @Bean
    public void converterPresenceSousZone() {
        Converter<PresenceSousZoneWebDto, PresenceSousZone> myConverter = new Converter<PresenceSousZoneWebDto, PresenceSousZone>() {
            @SneakyThrows
            public PresenceSousZone convert(MappingContext<PresenceSousZoneWebDto, PresenceSousZone> context) {
                PresenceSousZoneWebDto source = context.getSource();

                PresenceSousZone presenceSousZone = new PresenceSousZone();
                setChamp(source.getId(), source.getMessage(), source.getZone(), source.getPriority(), source.getTypesDoc(), presenceSousZone);

                presenceSousZone.setSousZone(source.getSousZone());
                presenceSousZone.setPresent(source.isPresent());

                return presenceSousZone;
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle NombreZoneWebDto en modèle NombreZone
     */
    @Bean
    public void converterNombreZone() {
        Converter<NombreZoneWebDto, NombreZone> myConverter = new Converter<NombreZoneWebDto, NombreZone>() {
            @SneakyThrows
            public NombreZone convert(MappingContext<NombreZoneWebDto, NombreZone> context) {
                NombreZoneWebDto source = context.getSource();

                NombreZone nombreZone = new NombreZone();
                setChamp(source.getId(), source.getMessage(), source.getZone(), source.getPriority(), source.getTypesDoc(), nombreZone);

                nombreZone.setOperateur(source.getOperateur());
                nombreZone.setOccurrences(source.getOccurrences());

                return nombreZone;
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle NombreSousZoneWebDto en modèle NombreSousZone
     */
    @Bean
    public void converterNombreSousZone() {
        Converter<NombreSousZoneWebDto, NombreSousZone> myConverter = new Converter<NombreSousZoneWebDto, NombreSousZone>() {
            @SneakyThrows
            public NombreSousZone convert(MappingContext<NombreSousZoneWebDto, NombreSousZone> context) {
                NombreSousZoneWebDto source = context.getSource();

                NombreSousZone nombreSousZone = new NombreSousZone();
                setChamp(source.getId(), source.getMessage(), source.getZone(), source.getPriority(), source.getTypesDoc(), nombreSousZone);

                nombreSousZone.setSousZone(source.getSousZone());
                nombreSousZone.setZoneCible(source.getZoneCible());
                nombreSousZone.setSousZoneCible(source.getSousZoneCible());

                return nombreSousZone;
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle PositionSousZoneWebDto en modèle PositionSousZone
     */
    @Bean
    public void converterPositionSousZone() {
        Converter<PositionSousZoneWebDto, PositionSousZone> myConverter = new Converter<PositionSousZoneWebDto, PositionSousZone>() {
            @SneakyThrows
            public PositionSousZone convert(MappingContext<PositionSousZoneWebDto, PositionSousZone> context) {
                PositionSousZoneWebDto source = context.getSource();

                PositionSousZone positionSousZone = new PositionSousZone();
                setChamp(source.getId(), source.getMessage(), source.getZone(), source.getPriority(), source.getTypesDoc(), positionSousZone);

                positionSousZone.setSousZone(source.getSousZone());
                positionSousZone.setSousZone(source.getSousZone());
                positionSousZone.setPosition(source.getPosition());

                return positionSousZone;
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle ResultAnalyse en modèle ResultAnalyseResponseDto
     */
    @Bean
    public void converterResultAnalyse() {
        Converter<ResultAnalyse, ResultAnalyseResponseDto> myConverter = new Converter<ResultAnalyse, ResultAnalyseResponseDto>() {
            @SneakyThrows
            public ResultAnalyseResponseDto convert(MappingContext<ResultAnalyse, ResultAnalyseResponseDto> context) {
                ResultAnalyse source = context.getSource();

                ResultAnalyseResponseDto responseDto = new ResultAnalyseResponseDto();

                source.getResultRules().forEach(resultRules -> {
                    ResultRulesResponseDto resultRulesResponseDto;
                    if (resultRules.getFamilleDocument() != null) {
                        resultRulesResponseDto = new ResultRulesResponseDto(resultRules.getPpn(), resultRules.getFamilleDocument().getLibelle(), resultRules.getMessages());
                    } else {
                        resultRulesResponseDto = new ResultRulesResponseDto(resultRules.getPpn(), resultRules.getMessages());
                    }
                        resultRules.getDetailErreurs().forEach(detailErreur -> {
                            resultRulesResponseDto.addDetailErreur(new RuleResponseDto(detailErreur.getId(),detailErreur.getZoneUnm1(),detailErreur.getZoneUnm2(),detailErreur.getPriority().toString(),detailErreur.getMessage()));
                        });
                        resultRulesResponseDto.setAuteur(resultRules.getAuteur());
                        resultRulesResponseDto.setTitre(resultRules.getTitre());
                        resultRulesResponseDto.setDateModification(resultRules.getDateModification());
                        resultRulesResponseDto.setRcr(resultRules.getRcr());
                        if(resultRules.getIsbn() != null)
                            resultRulesResponseDto.setIsbn(resultRules.getIsbn());
                        if (resultRules.getOcn() != null) {
                            resultRulesResponseDto.setOcn(resultRules.getOcn());
                        }
                        responseDto.addResultRule(resultRulesResponseDto);
                });

                responseDto.setPpnAnalyses(new ArrayList<>(source.getPpnAnalyses()));
                responseDto.setPpnErrones(new ArrayList<>(source.getPpnErrones()));
                responseDto.setPpnOk(new ArrayList<>(source.getPpnOk()));
                responseDto.setPpnInconnus(new ArrayList<>(source.getPpnInconnus()));

                responseDto.setNbPpnAnalyses(source.getPpnAnalyses().size());
                responseDto.setNbPpnErrones(source.getPpnErrones().size());
                responseDto.setNbPpnOk(source.getPpnOk().size());
                responseDto.setNbPpnInconnus(source.getPpnInconnus().size());

                return responseDto;
            }
        };
        mapper.addConverter(myConverter);
    }

    private void setChamp(Integer id, String message, String zone, String priority, List<String> typeDoc, Rule rule) {
        rule.setId(id);
        rule.setMessage(message);
        rule.setZone(zone);
        if(priority.equals("P1")) {
            rule.setPriority(Priority.P1);
        } else if (priority.equals("P2")) {
            rule.setPriority(Priority.P2);
        }
        Set<FamilleDocument> familleDocumentSet = new HashSet<>();
        for (String typeDocument: typeDoc) {
            familleDocumentSet.add(new FamilleDocument(typeDocument));
        }
        rule.setFamillesDocuments(familleDocumentSet);
    }
}
