package fr.abes.qualimarc.web.mapper;

import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.PresenceZone;
import fr.abes.qualimarc.core.model.resultats.ResultAnalyse;
import fr.abes.qualimarc.core.utils.UtilsMapper;
import fr.abes.qualimarc.web.dto.ResultAnalyseResponseDto;
import fr.abes.qualimarc.web.dto.ResultRulesResponseDto;
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

                return presenceZone;
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
