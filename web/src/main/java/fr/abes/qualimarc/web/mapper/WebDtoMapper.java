package fr.abes.qualimarc.web.mapper;

import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.NombreSousZones;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.NombreZone;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.PresenceSousZone;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.PresenceZone;
import fr.abes.qualimarc.core.model.resultats.ResultAnalyse;
import fr.abes.qualimarc.core.utils.UtilsMapper;
import fr.abes.qualimarc.web.dto.ResultAnalyseResponseDto;
import fr.abes.qualimarc.web.dto.ResultRulesResponseDto;
import fr.abes.qualimarc.web.dto.indexrules.NombreSousZoneWebDto;
import fr.abes.qualimarc.web.dto.indexrules.NombreZoneWebDto;
import fr.abes.qualimarc.web.dto.indexrules.PresenceSousZoneWebDto;
import fr.abes.qualimarc.web.dto.indexrules.PresenceZoneWebDto;
import lombok.SneakyThrows;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Component
public class WebDtoMapper {

    private final UtilsMapper mapper;

    public WebDtoMapper(UtilsMapper mapper) {
        this.mapper = mapper;
    }

    @Bean
    public void converterPresenceZone() {
        Converter<PresenceZoneWebDto, PresenceZone> myConverter = new Converter<PresenceZoneWebDto, PresenceZone>() {
            @SneakyThrows
            public PresenceZone convert(MappingContext<PresenceZoneWebDto, PresenceZone> context) {
                PresenceZoneWebDto source = context.getSource();

                PresenceZone presenceZone = new PresenceZone();
                presenceZone.setId(source.getId());
                presenceZone.setMessage(source.getMessage());
                presenceZone.setZone(source.getZone());
                presenceZone.setPriority(source.getPriority());
                Set<FamilleDocument> familleDocumentSet = new HashSet<>();
                for (String typeDocument: source.getTypesDoc()
                     ) {
                    FamilleDocument familleDocument = new FamilleDocument();
                    familleDocument.setId(typeDocument);
                    familleDocumentSet.add(familleDocument);
                }
                presenceZone.setFamillesDocuments(familleDocumentSet);
                presenceZone.setPresent(source.isPresent());

                return presenceZone;
            }
        };
        mapper.addConverter(myConverter);
    }

    @Bean
    public void converterPresenceSousZone() {
        Converter<PresenceSousZoneWebDto, PresenceSousZone> myConverter = new Converter<PresenceSousZoneWebDto, PresenceSousZone>() {
            @SneakyThrows
            public PresenceSousZone convert(MappingContext<PresenceSousZoneWebDto, PresenceSousZone> context) {
                PresenceSousZoneWebDto source = context.getSource();

                PresenceSousZone presenceSousZone = new PresenceSousZone();
                presenceSousZone.setId(source.getId());
                presenceSousZone.setMessage(source.getMessage());
                presenceSousZone.setZone(source.getZone());
                presenceSousZone.setPriority(source.getPriority());
                Set<FamilleDocument> familleDocumentSet = new HashSet<>();
                for (String typeDocument: source.getTypesDoc()
                ) {
                    FamilleDocument familleDocument = new FamilleDocument();
                    familleDocument.setId(typeDocument);
                    familleDocumentSet.add(familleDocument);
                }
                presenceSousZone.setFamillesDocuments(familleDocumentSet);
                presenceSousZone.setSousZone(source.getSousZone());
                presenceSousZone.setPresent(source.isPresent());

                return presenceSousZone;
            }
        };
        mapper.addConverter(myConverter);
    }

    @Bean
    public void converterNombreZone() {
        Converter<NombreZoneWebDto, NombreZone> myConverter = new Converter<NombreZoneWebDto, NombreZone>() {
            @SneakyThrows
            public NombreZone convert(MappingContext<NombreZoneWebDto, NombreZone> context) {
                NombreZoneWebDto source = context.getSource();

                NombreZone nombreZone = new NombreZone();
                nombreZone.setId(source.getId());
                nombreZone.setMessage(source.getMessage());
                nombreZone.setZone(source.getZone());
                nombreZone.setPriority(source.getPriority());
                Set<FamilleDocument> familleDocumentSet = new HashSet<>();
                for (String typeDocument: source.getTypesDoc()
                ) {
                    FamilleDocument familleDocument = new FamilleDocument();
                    familleDocument.setId(typeDocument);
                    familleDocumentSet.add(familleDocument);
                }
                nombreZone.setFamillesDocuments(familleDocumentSet);

                nombreZone.setOperateur(source.getOperateur());
                nombreZone.setOccurrences(source.getOccurences());

                return nombreZone;
            }
        };
        mapper.addConverter(myConverter);
    }

    @Bean
    public void converterNombreSousZone() {
        Converter<NombreSousZoneWebDto, NombreSousZones> myConverter = new Converter<NombreSousZoneWebDto, NombreSousZones>() {
            @SneakyThrows
            public NombreSousZones convert(MappingContext<NombreSousZoneWebDto, NombreSousZones> context) {
                NombreSousZoneWebDto source = context.getSource();

                NombreSousZones NombreSousZones = new NombreSousZones();
                NombreSousZones.setId(source.getId());
                NombreSousZones.setMessage(source.getMessage());
                NombreSousZones.setZone(source.getZone());
                NombreSousZones.setPriority(source.getPriority());
                Set<FamilleDocument> familleDocumentSet = new HashSet<>();
                for (String typeDocument: source.getTypesDoc()
                ) {
                    FamilleDocument familleDocument = new FamilleDocument();
                    familleDocument.setId(typeDocument);
                    familleDocumentSet.add(familleDocument);
                }
                NombreSousZones.setFamillesDocuments(familleDocumentSet);

                NombreSousZones.setSousZone(source.getSousZone());
                NombreSousZones.setZoneCible(source.getZoneCible());
                NombreSousZones.setSousZoneCible(source.getSousZoneCible());

                return NombreSousZones;
            }
        };
        mapper.addConverter(myConverter);
    }

    @Bean
    public void converterEditeurCreeWebDto() {
        Converter<ResultAnalyse, ResultAnalyseResponseDto> myConverter = new Converter<ResultAnalyse, ResultAnalyseResponseDto>() {
            @SneakyThrows
            public ResultAnalyseResponseDto convert(MappingContext<ResultAnalyse, ResultAnalyseResponseDto> context) {
                ResultAnalyse source = context.getSource();

                ResultAnalyseResponseDto responseDto = new ResultAnalyseResponseDto();
                responseDto.setPpnAnalyses(new ArrayList<>(source.getPpnAnalyses()));
                responseDto.setPpnErrones(new ArrayList<>(source.getPpnErrones()));
                responseDto.setPpnOk(new ArrayList<>(source.getPpnOk()));
                responseDto.setPpnInconnus(new ArrayList<>(source.getPpnInconnus()));

                responseDto.setNbPpnAnalyses(source.getPpnAnalyses().size());
                responseDto.setNbPpnErrones(source.getPpnErrones().size());
                responseDto.setNbPpnOk(source.getPpnOk().size());
                responseDto.setNbPpnInconnus(source.getPpnInconnus().size());

                source.getResultRules().forEach(r -> responseDto.addResultRule(new ResultRulesResponseDto(r.getPpn(), r.getMessages())));

                return responseDto;
            }
        };
        mapper.addConverter(myConverter);
    }
}
