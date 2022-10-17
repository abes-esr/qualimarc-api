package fr.abes.qualimarc.web.mapper;

import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.ComplexRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.LinkedRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.*;
import fr.abes.qualimarc.core.model.resultats.ResultAnalyse;
import fr.abes.qualimarc.core.utils.BooleanOperateur;
import fr.abes.qualimarc.core.utils.Priority;
import fr.abes.qualimarc.core.utils.UtilsMapper;
import fr.abes.qualimarc.web.dto.ResultAnalyseResponseDto;
import fr.abes.qualimarc.web.dto.ResultRulesResponseDto;
import fr.abes.qualimarc.web.dto.RuleResponseDto;
import fr.abes.qualimarc.web.dto.indexrules.ComplexRuleWebDto;
import fr.abes.qualimarc.web.dto.indexrules.SimpleRuleWebDto;
import fr.abes.qualimarc.web.dto.indexrules.structure.*;
import lombok.SneakyThrows;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class WebDtoMapper {

    private final UtilsMapper mapper;

    public WebDtoMapper(UtilsMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Convertion d'un modèle PresenceZoneWebDto en modèle ComplexRule
     */
    @Bean
    public void converterPresenceZone() {
        Converter<PresenceZoneWebDto, ComplexRule> myConverter = new Converter<PresenceZoneWebDto, ComplexRule>() {
            public ComplexRule convert(MappingContext<PresenceZoneWebDto, ComplexRule> context) {
                PresenceZoneWebDto source = context.getSource();
                if (source.getBooleanOperator() != null) {
                    throw new IllegalArgumentException("L'opérateur est interdit lors de la création d'une seule règle");
                }
                if (source.getMessage() == null || source.getPriority() == null) {
                    throw new IllegalArgumentException("Le message et / ou la priorité est obligatoire lors de la création d'une règle simple");
                }
                return new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), getFamilleDocument(source.getTypesDoc()), new PresenceZone(source.getId(), source.getZone(), source.isPresent()));
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle PresenceSousZoneWebDto en modèle ComplexRule
     */
    @Bean
    public void converterPresenceSousZone() {
        Converter<PresenceSousZoneWebDto, ComplexRule> myConverter = new Converter<PresenceSousZoneWebDto, ComplexRule>() {
            public ComplexRule convert(MappingContext<PresenceSousZoneWebDto, ComplexRule> context) {
                PresenceSousZoneWebDto source = context.getSource();
                if (source.getBooleanOperator() != null) {
                    throw new IllegalArgumentException("L'opérateur est interdit lors de la création d'une seule règle");
                }
                if (source.getMessage() == null || source.getPriority() == null) {
                    throw new IllegalArgumentException("Le message et / ou la priorité est obligatoire lors de la création d'une règle simple");
                }
                return new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), getFamilleDocument(source.getTypesDoc()), new PresenceSousZone(source.getId(), source.getZone(), source.getSousZone(), source.isPresent()));
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle NombreZoneWebDto en modèle ComplexRule
     */
    @Bean
    public void converterNombreZone() {
        Converter<NombreZoneWebDto, ComplexRule> myConverter = new Converter<NombreZoneWebDto, ComplexRule>() {
            public ComplexRule convert(MappingContext<NombreZoneWebDto, ComplexRule> context) {
                NombreZoneWebDto source = context.getSource();
                if (source.getBooleanOperator() != null) {
                    throw new IllegalArgumentException("L'opérateur est interdit lors de la création d'une seule règle");
                }
                if (source.getMessage() == null || source.getPriority() == null) {
                    throw new IllegalArgumentException("Le message et / ou la priorité est obligatoire lors de la création d'une règle simple");
                }
                return new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), getFamilleDocument(source.getTypesDoc()), new NombreZone(source.getId(), source.getZone(), source.getOperateur(), source.getOccurrences()));
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle NombreSousZoneWebDto en modèle ComplexRule
     */
    @Bean
    public void converterNombreSousZone() {
        Converter<NombreSousZoneWebDto, ComplexRule> myConverter = new Converter<NombreSousZoneWebDto, ComplexRule>() {
            public ComplexRule convert(MappingContext<NombreSousZoneWebDto, ComplexRule> context) {
                NombreSousZoneWebDto source = context.getSource();
                if (source.getBooleanOperator() != null) {
                    throw new IllegalArgumentException("L'opérateur est interdit lors de la création d'une seule règle");
                }
                if (source.getMessage() == null || source.getPriority() == null) {
                    throw new IllegalArgumentException("Le message et / ou la priorité est obligatoire lors de la création d'une règle simple");
                }
                return new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), getFamilleDocument(source.getTypesDoc()), new NombreSousZone(source.getId(), source.getZone(), source.getSousZone(), source.getZoneCible(), source.getSousZoneCible()));
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle PositionSousZoneWebDto en modèle ComplexRule
     */
    @Bean
    public void converterPositionSousZone() {
        Converter<PositionSousZoneWebDto, ComplexRule> myConverter = new Converter<PositionSousZoneWebDto, ComplexRule>() {
            public ComplexRule convert(MappingContext<PositionSousZoneWebDto, ComplexRule> context) {
                PositionSousZoneWebDto source = context.getSource();
                if (source.getBooleanOperator() != null) {
                    throw new IllegalArgumentException("L'opérateur est interdit lors de la création d'une seule règle");
                }
                if (source.getMessage() == null || source.getPriority() == null) {
                    throw new IllegalArgumentException("Le message et / ou la priorité est obligatoire lors de la création d'une règle simple");
                }
                return new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), getFamilleDocument(source.getTypesDoc()), new PositionSousZone(source.getId(), source.getZone(), source.getSousZone(), source.getPosition()));
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle PresenceZoneWebDto en modèle SimpleRule
     */
    @Bean
    public void converterPresenceZoneToSimple() {
        Converter<PresenceZoneWebDto, SimpleRule> myConverter = new Converter<PresenceZoneWebDto, SimpleRule>() {
            public SimpleRule convert(MappingContext<PresenceZoneWebDto, SimpleRule> context) {
                PresenceZoneWebDto source = context.getSource();
                return new PresenceZone(source.getId(), source.getZone(), source.isPresent());
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle PresenceSousZoneWebDto en modèle SimpleRule
     */
    @Bean
    public void converterPresenceSousZoneToSimple() {
        Converter<PresenceSousZoneWebDto, SimpleRule> myConverter = new Converter<PresenceSousZoneWebDto, SimpleRule>() {
            public SimpleRule convert(MappingContext<PresenceSousZoneWebDto, SimpleRule> context) {
                PresenceSousZoneWebDto source = context.getSource();
                return new PresenceSousZone(source.getId(), source.getZone(), source.getSousZone(), source.isPresent());
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle NombreZoneWebDto en modèle SimpleRule
     */
    @Bean
    public void converterNombreZoneToSimple() {
        Converter<NombreZoneWebDto, SimpleRule> myConverter = new Converter<NombreZoneWebDto, SimpleRule>() {
            public SimpleRule convert(MappingContext<NombreZoneWebDto, SimpleRule> context) {
                NombreZoneWebDto source = context.getSource();
                return new NombreZone(source.getId(), source.getZone(), source.getOperateur(), source.getOccurrences());
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle NombreSousZoneWebDto en modèle SimpleRule
     */
    @Bean
    public void converterNombreSousZoneToSimple() {
        Converter<NombreSousZoneWebDto, SimpleRule> myConverter = new Converter<NombreSousZoneWebDto, SimpleRule>() {
            public SimpleRule convert(MappingContext<NombreSousZoneWebDto, SimpleRule> context) {
                NombreSousZoneWebDto source = context.getSource();
                return new NombreSousZone(source.getId(), source.getZone(), source.getSousZone(), source.getZoneCible(), source.getSousZoneCible());
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle PositionSousZoneWebDto en modèle SimpleRule
     */
    @Bean
    public void converterPositionSousZoneToSimple() {
        Converter<PositionSousZoneWebDto, SimpleRule> myConverter = new Converter<PositionSousZoneWebDto, SimpleRule>() {
            public SimpleRule convert(MappingContext<PositionSousZoneWebDto, SimpleRule> context) {
                PositionSousZoneWebDto source = context.getSource();
                return new PositionSousZone(source.getId(), source.getZone(), source.getSousZone(), source.getPosition());
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle ComplexRuleWebDto en ComplexRule
     */
    @Bean
    public void converterComplexRule() {
        Converter<ComplexRuleWebDto, ComplexRule> myConverter = new Converter<ComplexRuleWebDto, ComplexRule>() {
            public ComplexRule convert(MappingContext<ComplexRuleWebDto, ComplexRule> context) {
                ComplexRuleWebDto source = context.getSource();
                ComplexRule target;
                Iterator<SimpleRuleWebDto> reglesIt = source.getRegles().listIterator();
                SimpleRuleWebDto firstRegle = reglesIt.next();
                if (null == firstRegle.getBooleanOperator()) {
                    target = new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), mapper.map(firstRegle, SimpleRule.class));
                    if(source.getTypesDoc() != null)
                        target.setFamillesDocuments(getFamilleDocument(source.getTypesDoc()));

                    while (reglesIt.hasNext()) {
                        SimpleRuleWebDto otherRegle = reglesIt.next();
                        if (otherRegle.getBooleanOperator() == null) {
                            throw new IllegalArgumentException("Les règles autres que la première d'une règle complexe doivent avoir un opérateur");
                        }
                        target.addOtherRule(new LinkedRule(mapper.map(otherRegle, SimpleRule.class), getOperateur(otherRegle.getBooleanOperator())));
                    }

                } else {
                    throw new IllegalArgumentException("La première règle d'une règle complexe ne doit pas contenir d'opérateur");
                }
                return target;
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
                            RuleResponseDto responseDto1 = new RuleResponseDto(detailErreur.getId(),detailErreur.getPriority().toString(),detailErreur.getMessage());
                            detailErreur.getZonesUnm().forEach(responseDto1::addZone);
                            resultRulesResponseDto.addDetailErreur(responseDto1);
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

    private Priority getPriority(String priority) {
        if(priority.equals("P1")) {
            return Priority.P1;
        } else if (priority.equals("P2")) {
            return Priority.P2;
        }
        return Priority.P1;
    }

    private BooleanOperateur getOperateur(String operateur) {
        if (operateur.equals("ET")) {
            return BooleanOperateur.ET;
        } else if (operateur.equals("OU")) {
            return BooleanOperateur.OU;
        }
        return BooleanOperateur.ET;
    }


    private Set<FamilleDocument> getFamilleDocument(List<String> familleDoc) {
        Set<FamilleDocument> familleDocumentSet = new HashSet<>();
        for (String typeDocument: familleDoc) {
            familleDocumentSet.add(new FamilleDocument(typeDocument));
        }
        return familleDocumentSet;
    }
}
