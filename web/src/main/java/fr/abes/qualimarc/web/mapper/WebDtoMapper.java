package fr.abes.qualimarc.web.mapper;

import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.RuleSet;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.ComplexRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.LinkedRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.Indicateur;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.NombreCaracteres;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.PresenceChaineCaracteres;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.TypeCaractere;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.chainecaracteres.ChaineCaracteres;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.*;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.souszoneoperator.SousZoneOperator;
import fr.abes.qualimarc.core.model.resultats.ResultAnalyse;
import fr.abes.qualimarc.core.utils.*;
import fr.abes.qualimarc.web.dto.ResultAnalyseResponseDto;
import fr.abes.qualimarc.web.dto.ResultRulesResponseDto;
import fr.abes.qualimarc.web.dto.RuleResponseDto;
import fr.abes.qualimarc.web.dto.RuleWebDto;
import fr.abes.qualimarc.web.dto.indexrules.ComplexRuleWebDto;
import fr.abes.qualimarc.web.dto.indexrules.SimpleRuleWebDto;
import fr.abes.qualimarc.web.dto.indexrules.contenu.IndicateurWebDto;
import fr.abes.qualimarc.web.dto.indexrules.contenu.NombreCaracteresWebDto;
import fr.abes.qualimarc.web.dto.indexrules.contenu.PresenceChaineCaracteresWebDto;
import fr.abes.qualimarc.web.dto.indexrules.contenu.TypeCaractereWebDto;
import fr.abes.qualimarc.web.dto.indexrules.structure.*;
import lombok.SneakyThrows;
import org.apache.commons.lang3.EnumUtils;
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
    public void converterPresenceZoneToComplexRule() {
        Converter<PresenceZoneWebDto, ComplexRule> myConverter = new Converter<PresenceZoneWebDto, ComplexRule>() {
            public ComplexRule convert(MappingContext<PresenceZoneWebDto, ComplexRule> context) {
                PresenceZoneWebDto source = context.getSource();
                checkSimpleRule(source);
                return new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), getFamilleDocument(source.getTypesDoc()), getTypeThese(source.getTypesThese()), getRuleSet(source.getRuleSetList()), new PresenceZone(source.getId(), source.getZone(), source.isPresent()));
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle PresenceSousZoneWebDto en modèle ComplexRule
     */
    @Bean
    public void converterPresenceSousZoneToComplexRule() {
        Converter<PresenceSousZoneWebDto, ComplexRule> myConverter = new Converter<PresenceSousZoneWebDto, ComplexRule>() {
            public ComplexRule convert(MappingContext<PresenceSousZoneWebDto, ComplexRule> context) {
                PresenceSousZoneWebDto source = context.getSource();
                checkSimpleRule(source);
                return new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), getFamilleDocument(source.getTypesDoc()), getTypeThese(source.getTypesThese()), getRuleSet(source.getRuleSetList()), new PresenceSousZone(source.getId(), source.getZone(), source.getSousZone(), source.isPresent()));
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle NombreZoneWebDto en modèle ComplexRule
     */
    @Bean
    public void converterNombreZoneToComplexRule() {
        Converter<NombreZoneWebDto, ComplexRule> myConverter = new Converter<NombreZoneWebDto, ComplexRule>() {
            public ComplexRule convert(MappingContext<NombreZoneWebDto, ComplexRule> context) {
                NombreZoneWebDto source = context.getSource();
                checkSimpleRule(source);
                if (!Operateur.EGAL.equals(source.getOperateur()) && !Operateur.SUPERIEUR.equals(source.getOperateur()) && !Operateur.INFERIEUR.equals(source.getOperateur())) {
                    throw new IllegalArgumentException("Règle " + source.getId() + " : Seuls les opérateurs INFERIEUR, SUPERIEUR ou EGAL sont autorisés sur ce type de règle");
                }
                return new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), getFamilleDocument(source.getTypesDoc()), getTypeThese(source.getTypesThese()), getRuleSet(source.getRuleSetList()), new NombreZone(source.getId(), source.getZone(), source.getOperateur(), source.getOccurrences()));
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle NombreSousZoneWebDto en modèle ComplexRule
     */
    @Bean
    public void converterNombreSousZoneToComplexRule() {
        Converter<NombreSousZoneWebDto, ComplexRule> myConverter = new Converter<NombreSousZoneWebDto, ComplexRule>() {
            public ComplexRule convert(MappingContext<NombreSousZoneWebDto, ComplexRule> context) {
                NombreSousZoneWebDto source = context.getSource();
                checkSimpleRule(source);
                return new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), getFamilleDocument(source.getTypesDoc()), getTypeThese(source.getTypesThese()), getRuleSet(source.getRuleSetList()), new NombreSousZone(source.getId(), source.getZone(), source.getSousZone(), source.getZoneCible(), source.getSousZoneCible()));
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle PositionSousZoneWebDto en modèle ComplexRule
     */
    @Bean
    public void converterPositionSousZoneToComplexRule() {
        Converter<PositionSousZoneWebDto, ComplexRule> myConverter = new Converter<PositionSousZoneWebDto, ComplexRule>() {
            public ComplexRule convert(MappingContext<PositionSousZoneWebDto, ComplexRule> context) {
                PositionSousZoneWebDto source = context.getSource();
                checkSimpleRule(source);
                return new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), getFamilleDocument(source.getTypesDoc()), getTypeThese(source.getTypesThese()), getRuleSet(source.getRuleSetList()), new PositionSousZone(source.getId(), source.getZone(), source.getSousZone(), source.getPosition()));
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle PresenceSousZonesMemeZoneWebDto en modèle ComplexRule
     */
    @Bean
    public void converterPresenceSousZonesMemeZoneToComplexRule() {
        Converter<PresenceSousZonesMemeZoneWebDto, ComplexRule> myConverter = new Converter<PresenceSousZonesMemeZoneWebDto, ComplexRule>() {
            public ComplexRule convert(MappingContext<PresenceSousZonesMemeZoneWebDto, ComplexRule> context) {
                PresenceSousZonesMemeZoneWebDto source = context.getSource();
                checkSimpleRule(source);
                PresenceSousZonesMemeZone target = constructPresenceSousZonesMemeZone(source);
                return new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), getFamilleDocument(source.getTypesDoc()), getTypeThese(source.getTypesThese()), getRuleSet(source.getRuleSetList()), target);
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle IndicateurWebDto en modèle ComplexRule
     */
    @Bean
    public void converterIndicateurToComplexRule() {
        Converter<IndicateurWebDto, ComplexRule> myConverter = new Converter<IndicateurWebDto, ComplexRule>() {
            public ComplexRule convert(MappingContext<IndicateurWebDto, ComplexRule> context) {
                IndicateurWebDto source = context.getSource();
                checkSimpleRule(source);
                if (source.getIndicateur() != 1 && source.getIndicateur() != 2) {
                    throw new IllegalArgumentException("Règle " + source.getId() + " : le champ indicateur peut etre soit '1', soit '2'");
                }
                return new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), getFamilleDocument(source.getTypesDoc()), getTypeThese(source.getTypesThese()), getRuleSet(source.getRuleSetList()), new Indicateur(source.getId(), source.getZone(), source.getIndicateur(), source.getValeur()));
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle NombreCaracteresWebDto en modèle ComplexRule
     */
    @Bean
    public void converterNombreCaractereToComplexRule() {
        Converter<NombreCaracteresWebDto, ComplexRule> myConverter = new Converter<NombreCaracteresWebDto, ComplexRule>() {
            @Override
            public ComplexRule convert(MappingContext<NombreCaracteresWebDto, ComplexRule> context) {
                NombreCaracteresWebDto source = context.getSource();
                checkSimpleRule(source);
                return new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), getFamilleDocument(source.getTypesDoc()), getTypeThese(source.getTypesThese()), getRuleSet(source.getRuleSetList()), new NombreCaracteres(source.getId(), source.getZone(), source.getSousZone(), source.getOperateur(), source.getOccurrences()));
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle TypeCaractereWebDto en modèle ComplexRule
     */
    @Bean
    public void converterTypeCaractereToComplexRule() {
        Converter<TypeCaractereWebDto, ComplexRule> myConverter = new Converter<TypeCaractereWebDto, ComplexRule>() {
            public ComplexRule convert(MappingContext<TypeCaractereWebDto, ComplexRule> context) {
                TypeCaractereWebDto source = context.getSource();
                checkSimpleRule(source);
                return new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), getFamilleDocument(source.getTypesDoc()), getTypeThese(source.getTypesThese()), getRuleSet(source.getRuleSetList()), constructTypeCaractere(source));
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle PresenceChaineCaracteresWebDto en modèle ComplexRule
     */
    @Bean
    public void converterPresenceChaineCaracteresToComplexRule() {
        Converter<PresenceChaineCaracteresWebDto, ComplexRule> myConverter = new Converter<PresenceChaineCaracteresWebDto, ComplexRule>() {
            public ComplexRule convert(MappingContext<PresenceChaineCaracteresWebDto, ComplexRule> context) {
                PresenceChaineCaracteresWebDto source = context.getSource();
                checkSimpleRule(source);
                PresenceChaineCaracteres target = constructPresenceChaineCaracteres(source);
                return new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), getFamilleDocument(source.getTypesDoc()), getTypeThese(source.getTypesThese()), getRuleSet(source.getRuleSetList()), target);
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle PresenceZoneWebDto en modèle SimpleRule (LinkedRule)
     */
    @Bean
    public void converterPresenceZoneToLinkedRule() {
        Converter<PresenceZoneWebDto, SimpleRule> myConverter = new Converter<PresenceZoneWebDto, SimpleRule>() {
            public SimpleRule convert(MappingContext<PresenceZoneWebDto, SimpleRule> context) {
                PresenceZoneWebDto source = context.getSource();
                return new PresenceZone(source.getId(), source.getZone(), source.isPresent());
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle PresenceSousZoneWebDto en modèle SimpleRule (LinkedRule)
     */
    @Bean
    public void converterPresenceSousZoneToLinkedRule() {
        Converter<PresenceSousZoneWebDto, SimpleRule> myConverter = new Converter<PresenceSousZoneWebDto, SimpleRule>() {
            public SimpleRule convert(MappingContext<PresenceSousZoneWebDto, SimpleRule> context) {
                PresenceSousZoneWebDto source = context.getSource();
                return new PresenceSousZone(source.getId(), source.getZone(), source.getSousZone(), source.isPresent());
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle NombreZoneWebDto en modèle SimpleRule (LinkedRule)
     */
    @Bean
    public void converterNombreZoneToLinkedRule() {
        Converter<NombreZoneWebDto, SimpleRule> myConverter = new Converter<NombreZoneWebDto, SimpleRule>() {
            public SimpleRule convert(MappingContext<NombreZoneWebDto, SimpleRule> context) {
                NombreZoneWebDto source = context.getSource();
                if (!Operateur.EGAL.equals(source.getOperateur()) && !Operateur.SUPERIEUR.equals(source.getOperateur()) && !Operateur.INFERIEUR.equals(source.getOperateur())) {
                    throw new IllegalArgumentException("Règle " + source.getId() + " : Seuls les opérateurs INFERIEUR, SUPERIEUR ou EGAL sont autorisés sur ce type de règle");
                }
                return new NombreZone(source.getId(), source.getZone(), source.getOperateur(), source.getOccurrences());
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle NombreSousZoneWebDto en modèle SimpleRule (LinkedRule)
     */
    @Bean
    public void converterNombreSousZoneToLinkedRule() {
        Converter<NombreSousZoneWebDto, SimpleRule> myConverter = new Converter<NombreSousZoneWebDto, SimpleRule>() {
            public SimpleRule convert(MappingContext<NombreSousZoneWebDto, SimpleRule> context) {
                NombreSousZoneWebDto source = context.getSource();
                return new NombreSousZone(source.getId(), source.getZone(), source.getSousZone(), source.getZoneCible(), source.getSousZoneCible());
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle PositionSousZoneWebDto en modèle SimpleRule (LinkedRule)
     */
    @Bean
    public void converterPositionSousZoneToLinkedRule() {
        Converter<PositionSousZoneWebDto, SimpleRule> myConverter = new Converter<PositionSousZoneWebDto, SimpleRule>() {
            public SimpleRule convert(MappingContext<PositionSousZoneWebDto, SimpleRule> context) {
                PositionSousZoneWebDto source = context.getSource();
                return new PositionSousZone(source.getId(), source.getZone(), source.getSousZone(), source.getPosition());
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle PresenceSousZonesMemeZoneWebDto en modèle SimpleRule (LinkedRule)
     */
    @Bean
    public void converterPresenceSousZonesMemeZoneToLinkedRule() {
        Converter<PresenceSousZonesMemeZoneWebDto, SimpleRule> myConverter = new Converter<PresenceSousZonesMemeZoneWebDto, SimpleRule>() {
            public SimpleRule convert(MappingContext<PresenceSousZonesMemeZoneWebDto, SimpleRule> context) {
                PresenceSousZonesMemeZoneWebDto source = context.getSource();
                return constructPresenceSousZonesMemeZone(source);
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle TypeCaractereWebDto en modèle SimpleRule (LinkedRule)
     */
    @Bean
    public void converterTypeCaractereToLinkedRule() {
        Converter<TypeCaractereWebDto, SimpleRule> myConverter = new Converter<TypeCaractereWebDto, SimpleRule>() {
            public SimpleRule convert(MappingContext<TypeCaractereWebDto, SimpleRule> context) {
                TypeCaractereWebDto source = context.getSource();
                return constructTypeCaractere(source);
            }
        };
        mapper.addConverter(myConverter);
    }

    @Bean
    public void converterPresenceChaineCaracteresToLinkedRule() {
        Converter<PresenceChaineCaracteresWebDto, SimpleRule> myConverter = new Converter<PresenceChaineCaracteresWebDto, SimpleRule>() {
            public SimpleRule convert(MappingContext<PresenceChaineCaracteresWebDto, SimpleRule> context) {
                return constructPresenceChaineCaracteres(context.getSource());
            }
        };
        mapper.addConverter(myConverter);
    }


    /**
     * Convertion d'un modèle IndicateurWebDto en modèle SimpleRule (LinkedRule)
     */
    @Bean
    public void converterIndicateurToLinkedRule() {
        Converter<IndicateurWebDto, SimpleRule> myConverter = new Converter<IndicateurWebDto, SimpleRule>() {
            public SimpleRule convert(MappingContext<IndicateurWebDto, SimpleRule> context) {
                IndicateurWebDto source = context.getSource();
                return new Indicateur(source.getId(), source.getZone(), source.getIndicateur(), source.getValeur());
            }
        };
        mapper.addConverter(myConverter);
    }


    /**
     * Convertion d'un modèle NombreCaracteresWebDto en modèle SimpleRule (LinkedRule)
     */
    @Bean
    public void converterNombreCaractereToLinkedRule() {
        Converter<NombreCaracteresWebDto, SimpleRule> myConverter = new Converter<NombreCaracteresWebDto, SimpleRule>() {
            public SimpleRule convert(MappingContext<NombreCaracteresWebDto, SimpleRule> context) {
                NombreCaracteresWebDto source = context.getSource();
                return new NombreCaracteres(source.getId(), source.getZone(), source.getSousZone(), source.getOperateur(), source.getOccurrences());
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
                //vérification qu'aucune règle simple ne contient de zone générique
                if (source.getRegles().stream().filter(rule -> rule.getZone().matches("\\dXX")).count() > 0) {
                    throw new IllegalArgumentException("Une règle complexe ne peut pas contenir de règles simple avec des zones génériques");
                }
                Iterator<SimpleRuleWebDto> reglesIt = source.getRegles().listIterator();
                SimpleRuleWebDto firstRegle = reglesIt.next();
                int i = 0;
                if (null == firstRegle.getBooleanOperator()) {
                    target = new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), mapper.map(firstRegle, SimpleRule.class));
                    if (source.getRuleSetList() != null) {
                        target.setRuleSet(getRuleSet(source.getRuleSetList()));
                    }
                    if (source.getTypesDoc() != null)
                        target.setFamillesDocuments(getFamilleDocument(source.getTypesDoc()));
                    checkTypeThese(source.getTypesThese());
                    if (source.getTypesThese() != null && source.getTypesThese().size() != 0) {
                        target.setTypesThese(getTypeThese(source.getTypesThese()));
                    }
                    while (reglesIt.hasNext()) {
                        SimpleRuleWebDto otherRegle = reglesIt.next();
                        checkTypeThese(otherRegle.getTypesThese());
                        if (otherRegle.getBooleanOperator() == null) {
                            throw new IllegalArgumentException("Les règles autres que la première d'une règle complexe doivent avoir un opérateur");
                        }
                        target.addOtherRule(new LinkedRule(mapper.map(otherRegle, SimpleRule.class), getOperateur(otherRegle.getBooleanOperator()), i++, target));
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
                        if (resultRules.getTypeThese() != null) {
                            resultRulesResponseDto = new ResultRulesResponseDto(resultRules.getPpn(), "Thèse", resultRules.getMessages());
                        }
                        else {
                            resultRulesResponseDto = new ResultRulesResponseDto(resultRules.getPpn(), resultRules.getFamilleDocument().getLibelle(), resultRules.getMessages());
                        }
                    } else {
                        resultRulesResponseDto = new ResultRulesResponseDto(resultRules.getPpn(), resultRules.getMessages());
                    }
                    resultRules.getDetailErreurs().forEach(detailErreur -> {
                        RuleResponseDto responseDto1 = new RuleResponseDto(detailErreur.getId(), detailErreur.getPriority().toString(), detailErreur.getMessage());
                        detailErreur.getZonesUnm().forEach(responseDto1::addZone);
                        resultRulesResponseDto.addDetailErreur(responseDto1);
                    });
                    resultRulesResponseDto.setAuteur(resultRules.getAuteur());
                    resultRulesResponseDto.setTitre(resultRules.getTitre());
                    resultRulesResponseDto.setDateModification(resultRules.getDateModification());
                    resultRulesResponseDto.setRcr(resultRules.getRcr());
                    if (resultRules.getIsbn() != null)
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

    /**
     * Convertion d'un modèle ComplexRule en modèle RuleWebDto
     */
    @Bean
    public void converterComplexRuleToRuleWebDto() {
        Converter<ComplexRule, RuleWebDto> myConverter = new Converter<ComplexRule, RuleWebDto>() {
            @SneakyThrows
            public RuleWebDto convert(MappingContext<ComplexRule, RuleWebDto> context) {
                ComplexRule source = context.getSource();

                RuleWebDto ruleWebDto = new RuleWebDto();
                ruleWebDto.setId(source.getId());
                ruleWebDto.setZoneUnm1(source.getZonesFromChildren().get(0));
                if (source.getZonesFromChildren().size() > 1) {
                    ruleWebDto.setZoneUnm2(source.getZonesFromChildren().get(1));
                }
                ruleWebDto.setMessage(source.getMessage());
                StringBuilder typesDoc = new StringBuilder();
                source.getFamillesDocuments().stream().sorted(Comparator.comparing(FamilleDocument::getId)).forEach(f -> {
                    typesDoc.append(f.getLibelle());
                    typesDoc.append(", ");
                });
                if (!source.getTypesThese().isEmpty()) {
                    typesDoc.append("Thèse, ");
                }
                if (source.getFamillesDocuments().size() == 0 && source.getTypesThese().size() == 0) {
                    ruleWebDto.setTypeDoc("Tous");
                } else {
                    ruleWebDto.setTypeDoc(typesDoc.substring(0, typesDoc.length() - 2));
                }
                ruleWebDto.setPriority(source.getPriority().equals(Priority.P1) ? "Essentielle" : "Avancée");
                return ruleWebDto;
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Création d'un objet PresenceChaineCaracteres à partir des données issues d'un objet PresenceChaineCaracteresWebDto
     * @param source PresenceChaineCaracteresWebDto
     * @return PresenceChaineCaracteres
     */
    private PresenceChaineCaracteres constructPresenceChaineCaracteres(PresenceChaineCaracteresWebDto source) {
        PresenceChaineCaracteres target = new PresenceChaineCaracteres(source.getId(), source.getZone(), source.getSousZone(), getTypeDeVerification(source.getTypeDeVerification()));
        if (source.getListChaineCaracteres() != null || source.getListChaineCaracteres().size() > 0 || !source.getListChaineCaracteres().isEmpty()) {
            int i = 0;
            for (PresenceChaineCaracteresWebDto.ChaineCaracteresWebDto chaine : source.getListChaineCaracteres()) {
                if (chaine.getOperateur() == null || chaine.getOperateur().isEmpty()) {
                    target.addChaineCaracteres(new ChaineCaracteres(i, chaine.getChaineCaracteres(), target));
                    i++;
                } else if (chaine.getOperateur() != null || !chaine.getOperateur().isEmpty()) {
                    target.addChaineCaracteres(new ChaineCaracteres(i, getOperateur(chaine.getOperateur()), chaine.getChaineCaracteres(), target));
                    i++;
                }
            }
        }
        return target;
    }

    private PresenceSousZonesMemeZone constructPresenceSousZonesMemeZone(PresenceSousZonesMemeZoneWebDto source) {
        PresenceSousZonesMemeZone target = new PresenceSousZonesMemeZone(source.getId(), source.getZone());
        if (source.getSousZones().size() < 2) {
            throw new IllegalArgumentException("La règle " + source.getId() + " doit avoir au moins deux sous-zones déclarées");
        } else {
            Iterator<PresenceSousZonesMemeZoneWebDto.SousZoneOperatorWebDto> sousZoneOperatorIt = source.getSousZones().listIterator();
            PresenceSousZonesMemeZoneWebDto.SousZoneOperatorWebDto firstSousZone = sousZoneOperatorIt.next();
            if (null == firstSousZone.getOperator()) {
                target.addSousZoneOperator(new SousZoneOperator(firstSousZone.getSousZone(), firstSousZone.isPresent(), target));
                while (sousZoneOperatorIt.hasNext()) {
                    PresenceSousZonesMemeZoneWebDto.SousZoneOperatorWebDto nextSousZone = sousZoneOperatorIt.next();
                    if (null != nextSousZone.getOperator()) {
                        target.addSousZoneOperator(new SousZoneOperator(nextSousZone.getSousZone(), nextSousZone.isPresent(), nextSousZone.getOperator(), target));
                    } else {
                        throw new IllegalArgumentException("Règle " + source.getId() + " : Les sous-zones en dehors de la première doivent avoir un opérateur booléen");
                    }
                }
            } else {
                throw new IllegalArgumentException("Règle " + source.getId() + " : La première sous-zone ne doit pas avoir d'opérateur booléen");
            }
        }
        return target;
    }

    private Priority getPriority(String priority) {
        if (priority.equals("P1")) {
            return Priority.P1;
        } else if (priority.equals("P2")) {
            return Priority.P2;
        }
        return Priority.P1;
    }

    private TypeVerification getTypeDeVerification(String typeDeVerification) {
        switch (typeDeVerification) {
            case "STRICTEMENT":
                return TypeVerification.STRICTEMENT;
            case "COMMENCE":
                return TypeVerification.COMMENCE;
            case "TERMINE":
                return TypeVerification.TERMINE;
            case "CONTIENT":
                return TypeVerification.CONTIENT;
            case "NECONTIENTPAS":
                return TypeVerification.NECONTIENTPAS;
            default:
                return TypeVerification.CONTIENT;
        }
    }

    private BooleanOperateur getOperateur(String operateur) {
        if (operateur.equals("ET")) {
            return BooleanOperateur.ET;
        } else if (operateur.equals("OU")) {
            return BooleanOperateur.OU;
        }
        return BooleanOperateur.ET;
    }

    private TypeCaracteres getTypeCaracteres(String type){
        switch (type) {
            case "ALPHABETIQUE":
                return TypeCaracteres.ALPHABETIQUE;
            case "ALPHABETIQUE_MAJ":
                return TypeCaracteres.ALPHABETIQUE_MAJ;
            case "ALPHABETIQUE_MIN":
                return TypeCaracteres.ALPHABETIQUE_MIN;
            case "NUMERIQUE":
                return TypeCaracteres.NUMERIQUE;
            case "SPECIAL":
                return TypeCaracteres.SPECIAL;
        }
        return TypeCaracteres.ALPHABETIQUE;
    }

    private Set<FamilleDocument> getFamilleDocument(List<String> familleDoc) {
        Set<FamilleDocument> familleDocumentSet = new HashSet<>();
        for (String typeDocument : familleDoc) {
            familleDocumentSet.add(new FamilleDocument(typeDocument));
        }
        return familleDocumentSet;
    }

    private Set<TypeThese> getTypeThese(List<String> types) {
        Set<TypeThese> typeTheseSet = new HashSet<>();
        for (String type : types) {
            if (("REPRO").equals(type))
                typeTheseSet.add(TypeThese.REPRO);
            if (("SOUTENANCE").equals(type))
                typeTheseSet.add(TypeThese.SOUTENANCE);
        }
        return typeTheseSet;
    }

    private Set<RuleSet> getRuleSet(List<Integer> ruleSetSourceList) {
        if (ruleSetSourceList != null) {
            Set<RuleSet> ruleSetList = new HashSet<>();
            for (Integer ruleSetSource : ruleSetSourceList) {
                ruleSetList.add(new RuleSet(ruleSetSource));
            }
            return ruleSetList;
        } else
            return null;
    }

    private void checkSimpleRule(SimpleRuleWebDto source) {
        if (source.getBooleanOperator() != null) {
            throw new IllegalArgumentException("Règle " + source.getId() + " : L'opérateur est interdit lors de la création d'une seule règle");
        }
        if (source.getMessage() == null || source.getPriority() == null) {
            throw new IllegalArgumentException("Règle " + source.getId() + " : Le message et / ou la priorité est obligatoire lors de la création d'une règle simple");
        }
        checkTypeThese(source.getTypesThese());
    }

    private void checkTypeThese(List<String> typesThese) {
        if (typesThese != null && typesThese.size() != 0) {
            if (typesThese.stream().filter(tt -> EnumUtils.isValidEnum(TypeThese.class, tt)).count() == 0) {
                StringBuilder message = new StringBuilder("Les types de thèses ne peuvent prendre que les valeurs ");
                int j = 0;
                for (TypeThese tt : TypeThese.values()) {
                    message.append(tt.toString());
                    if (j++ < (TypeThese.values().length - 1))
                        message.append("|");
                }
                throw new IllegalArgumentException(message.toString());
            }
        }
    }

    private TypeCaractere constructTypeCaractere(TypeCaractereWebDto source) {
        if(source.getTypeCaracteres().isEmpty()){
            throw new IllegalArgumentException("Règle " + source.getId() + " : Le champ type-caracteres est obligatoire");
        }
        TypeCaractere target = new TypeCaractere(source.getId(), source.getZone(), source.getSousZone());
        for(String typeCaracteresString : source.getTypeCaracteres()){
            target.addTypeCaractere(getTypeCaracteres(typeCaracteresString));
        }
        return target;
    }
}
