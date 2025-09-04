package fr.abes.qualimarc.web.mapper;

import fr.abes.qualimarc.core.model.entity.qualimarc.reference.FamilleDocument;
import fr.abes.qualimarc.core.model.entity.qualimarc.reference.RuleSet;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.ComplexRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.DependencyRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.LinkedRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.SimpleRule;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.*;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.contenu.chainecaracteres.ChaineCaracteres;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.dependance.Reciprocite;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.*;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.positions.PositionsOperator;
import fr.abes.qualimarc.core.model.entity.qualimarc.rules.structure.souszoneoperator.SousZoneOperator;
import fr.abes.qualimarc.core.model.resultats.ResultAnalyse;
import fr.abes.qualimarc.core.utils.*;
import fr.abes.qualimarc.web.dto.ResultAnalyseResponseDto;
import fr.abes.qualimarc.web.dto.ResultRulesResponseDto;
import fr.abes.qualimarc.web.dto.RuleResponseDto;
import fr.abes.qualimarc.web.dto.RuleWebDto;
import fr.abes.qualimarc.web.dto.indexrules.ComplexRuleWebDto;
import fr.abes.qualimarc.web.dto.indexrules.DependencyWebDto;
import fr.abes.qualimarc.web.dto.indexrules.SimpleRuleWebDto;
import fr.abes.qualimarc.web.dto.indexrules.contenu.*;
import fr.abes.qualimarc.web.dto.indexrules.dependance.ReciprociteWebDto;
import fr.abes.qualimarc.web.dto.indexrules.structure.*;
import fr.abes.qualimarc.web.dto.reference.FamilleDocumentWebDto;
import fr.abes.qualimarc.web.dto.rulesets.RuleSetWebDto;
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
                checkOtherRule(source);
                return new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), getFamilleDocument(source.getTypesDoc()), getTypeThese(source.getTypesThese()), getRuleSet(source.getRuleSetList()), new PresenceZone(source.getId(), source.getZone(), source.isAffichageEtiquette(), source.isPresent()));
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
                checkOtherRule(source);
                return new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), getFamilleDocument(source.getTypesDoc()), getTypeThese(source.getTypesThese()), getRuleSet(source.getRuleSetList()), new PresenceSousZone(source.getId(), source.getZone(), source.isAffichageEtiquette(), source.getSousZone(), source.isPresent()));
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
                checkOtherRule(source);
                if (!ComparaisonOperateur.EGAL.equals(source.getComparaisonOperateur()) && !ComparaisonOperateur.SUPERIEUR.equals(source.getComparaisonOperateur()) && !ComparaisonOperateur.INFERIEUR.equals(source.getComparaisonOperateur())) {
                    throw new IllegalArgumentException("Règle " + source.getId() + " : Seuls les opérateurs INFERIEUR, SUPERIEUR ou EGAL sont autorisés sur ce type de règle");
                }
                return new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), getFamilleDocument(source.getTypesDoc()), getTypeThese(source.getTypesThese()), getRuleSet(source.getRuleSetList()), new NombreZone(source.getId(), source.getZone(), source.isAffichageEtiquette(), source.getComparaisonOperateur(), source.getOccurrences()));
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
                checkOtherRule(source);
                return new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), getFamilleDocument(source.getTypesDoc()), getTypeThese(source.getTypesThese()), getRuleSet(source.getRuleSetList()), new NombreSousZone(source.getId(), source.getZone(), source.isAffichageEtiquette(), source.getSousZone(), source.getZoneCible(), source.getSousZoneCible()));
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
                PositionSousZone target = new PositionSousZone(source.getId(), source.getZone(), source.isAffichageEtiquette(), source.getSousZone(), source.getBooleanOperateur());
                checkOtherRule(source);
                source.getPositions().forEach(pos -> target.addPositionOperator(new PositionsOperator(pos.getPosition(), pos.getComparateur(), target)));
                return new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), getFamilleDocument(source.getTypesDoc()), getTypeThese(source.getTypesThese()), getRuleSet(source.getRuleSetList()), target);
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
                checkOtherRule(source);
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
                checkOtherRule(source);
                if (source.getIndicateur() != 1 && source.getIndicateur() != 2) {
                    throw new IllegalArgumentException("Règle " + source.getId() + " : le champ indicateur peut etre soit '1', soit '2'");
                } else if (source.getTypeDeVerification() == null || source.getTypeDeVerification().isEmpty() || (!source.getTypeDeVerification().equals("STRICTEMENT") && !source.getTypeDeVerification().equals("STRICTEMENTDIFFERENT"))) {
                    throw new IllegalArgumentException("Règle " + source.getId() + " : le champ type-de-verification est obligatoire et peut etre soit 'STRICTEMENT', soit 'STRICTEMENTDIFFERENT'");
                }
                return new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), getFamilleDocument(source.getTypesDoc()), getTypeThese(source.getTypesThese()), getRuleSet(source.getRuleSetList()), new Indicateur(source.getId(), source.getZone(), source.isAffichageEtiquette(), source.getIndicateur(), source.getValeur(), getTypeDeVerification(source.getTypeDeVerification())));
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
                checkOtherRule(source);
                return new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), getFamilleDocument(source.getTypesDoc()), getTypeThese(source.getTypesThese()), getRuleSet(source.getRuleSetList()), new NombreCaracteres(source.getId(), source.getZone(), source.isAffichageEtiquette(), source.getSousZone(), source.getComparaisonOperateur(), source.getOccurrences()));
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
                checkOtherRule(source);
                return new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), getFamilleDocument(source.getTypesDoc()), getTypeThese(source.getTypesThese()), getRuleSet(source.getRuleSetList()), constructTypeCaractere(source));
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Conversion d'un modèle PresenceChaineCaracteresWebDto en modèle ComplexRule
     */
    @Bean
    public void converterPresenceChaineCaracteresToComplexRule() {
        Converter<PresenceChaineCaracteresWebDto, ComplexRule> myConverter = new Converter<PresenceChaineCaracteresWebDto, ComplexRule>() {
            public ComplexRule convert(MappingContext<PresenceChaineCaracteresWebDto, ComplexRule> context) {
                PresenceChaineCaracteresWebDto source = context.getSource();
                checkOtherRule(source);
                PresenceChaineCaracteres target = constructPresenceChaineCaracteres(source);
                return new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), getFamilleDocument(source.getTypesDoc()), getTypeThese(source.getTypesThese()), getRuleSet(source.getRuleSetList()), target);
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle ComparaisonDateWebDto en modèle ComplexRule
     */
    @Bean
    public void converterComparaisonDateToComplexRule() {
        Converter<ComparaisonDateWebDto, ComplexRule> myConverter = new Converter<ComparaisonDateWebDto, ComplexRule>() {
            public ComplexRule convert(MappingContext<ComparaisonDateWebDto, ComplexRule> context) {
                ComparaisonDateWebDto source = context.getSource();
                checkOtherRule(source);
                ComparaisonDate target = constructComparaisonDate(source);
                return new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), getFamilleDocument(source.getTypesDoc()), getTypeThese(source.getTypesThese()), getRuleSet(source.getRuleSetList()), target);
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle TypeDocumentWebDto en modèle ComplexRule
     */
    @Bean
    public void converterTypeDocumentToComplexRule() {
        Converter<TypeDocumentWebDto, ComplexRule> myConverter = new Converter<TypeDocumentWebDto, ComplexRule>() {
            public ComplexRule convert(MappingContext<TypeDocumentWebDto, ComplexRule> context) {
                TypeDocumentWebDto source = context.getSource();
                checkOtherRule(source);
                TypeDocument target = constructTypeDocument(source);
                return new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), getFamilleDocument(source.getTypesDoc()), getTypeThese(source.getTypesThese()), getRuleSet(source.getRuleSetList()), target);
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Conversion d'un modèle ComparaisonContenuSousZoneWebDto en modèle ComplexRule
     */
    @Bean
    public void converterComparaisonContenuSousZoneToComplexRule() {
        Converter<ComparaisonContenuSousZoneWebDto, ComplexRule> myConverter = new Converter<ComparaisonContenuSousZoneWebDto, ComplexRule>() {
            @Override
            public ComplexRule convert(MappingContext<ComparaisonContenuSousZoneWebDto, ComplexRule> context) {
                ComparaisonContenuSousZoneWebDto source = context.getSource();
                checkOtherRule(source);
                if((source.getPositionStart() != null || source.getPositionEnd() != null) && source.getPosition() != null) {
                    throw new IllegalArgumentException("Règle " + source.getId() + " : L'attribut position ne peut pas etre present en meme temps que positionstart ou positionend");
                }
                if((source.getPositionStartCible() != null || source.getPositionEndCible() != null) && source.getPositionCible() != null) {
                    throw new IllegalArgumentException("Règle " + source.getId() + " : L'attribut positioncible ne peut pas etre present en meme temps que positionstartcible ou positionendcible");
                }
                return new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), getFamilleDocument(source.getTypesDoc()), getTypeThese(source.getTypesThese()), getRuleSet(source.getRuleSetList()), new ComparaisonContenuSousZone(source.getId(), source.getZone(), source.isAffichageEtiquette(), source.getSousZone(), convertStringToInteger(source.getPositionStart()), convertStringToInteger(source.getPositionEnd()), convertStringToInteger(source.getPosition()), getTypeDeVerification(source.getTypeVerification()), convertStringToInteger(source.getNombreCaracteres()), source.getZoneCible(), source.getSousZoneCible(), convertStringToInteger(source.getPositionStartCible()), convertStringToInteger(source.getPositionEndCible()), convertStringToInteger(source.getPositionCible())));
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
                return new PresenceZone(source.getId(), source.getZone(), source.isAffichageEtiquette(), source.isPresent());
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
                return new PresenceSousZone(source.getId(), source.getZone(), source.isAffichageEtiquette(), source.getSousZone(), source.isPresent());
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
                if (!ComparaisonOperateur.EGAL.equals(source.getComparaisonOperateur()) && !ComparaisonOperateur.SUPERIEUR.equals(source.getComparaisonOperateur()) && !ComparaisonOperateur.INFERIEUR.equals(source.getComparaisonOperateur())) {
                    throw new IllegalArgumentException("Règle " + source.getId() + " : Seuls les opérateurs INFERIEUR, SUPERIEUR ou EGAL sont autorisés sur ce type de règle");
                }
                return new NombreZone(source.getId(), source.getZone(), source.isAffichageEtiquette(), source.getComparaisonOperateur(), source.getOccurrences());
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
                return new NombreSousZone(source.getId(), source.getZone(), source.isAffichageEtiquette(), source.getSousZone(), source.getZoneCible(), source.getSousZoneCible());
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
                PositionSousZone target = new PositionSousZone(source.getId(), source.getZone(), source.isAffichageEtiquette(), source.getSousZone(), source.getBooleanOperateur());
                source.getPositions().forEach(pos -> {
                    target.addPositionOperator(new PositionsOperator(pos.getPosition(), pos.getComparateur(), target));
                });
                return target;
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
     * Conversion d'un modèle ComparaisonContenuSousZoneWebDto en modèle ComparaisonContenuSousZone (linkedRule)
     */
    @Bean
    public void converterComparaisonContenuSousZoneToLinkedRule() {
        Converter<ComparaisonContenuSousZoneWebDto, SimpleRule> myConverter = new Converter<ComparaisonContenuSousZoneWebDto, SimpleRule>() {
            public SimpleRule convert(MappingContext<ComparaisonContenuSousZoneWebDto, SimpleRule> context) {
                ComparaisonContenuSousZoneWebDto source = context.getSource();
                if((source.getPositionStart() != null || source.getPositionEnd() != null) && source.getPosition() != null) {
                    throw new IllegalArgumentException("Règle " + source.getId() + " : L'attribut position ne peut pas etre present en meme temps que positionstart ou positionend");
                }
                if((source.getPositionStartCible() != null || source.getPositionEndCible() != null) && source.getPositionCible() != null) {
                    throw new IllegalArgumentException("Règle " + source.getId() + " : L'attribut positioncible ne peut pas etre present en meme temps que positionstartcible ou positionendcible");
                }
                return new ComparaisonContenuSousZone(source.getId(), source.getZone(), source.isAffichageEtiquette(), source.getSousZone(), convertStringToInteger(source.getPositionStart()), convertStringToInteger(source.getPositionEnd()), convertStringToInteger(source.getPosition()), getTypeDeVerification(source.getTypeVerification()), convertStringToInteger(source.getNombreCaracteres()), source.getZoneCible(), source.getSousZoneCible(), convertStringToInteger(source.getPositionStartCible()), convertStringToInteger(source.getPositionEndCible()), convertStringToInteger(source.getPositionCible()));
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
                if (source.getIndicateur() != 1 && source.getIndicateur() != 2) {
                    throw new IllegalArgumentException("Règle " + source.getId() + " : le champ indicateur peut etre soit '1', soit '2'");
                } else if (source.getTypeDeVerification() == null || source.getTypeDeVerification().isEmpty() || (!source.getTypeDeVerification().equals("STRICTEMENT") && !source.getTypeDeVerification().equals("STRICTEMENTDIFFERENT"))) {
                    throw new IllegalArgumentException("Règle " + source.getId() + " : le champ type-de-verification est obligatoire et peut etre soit 'STRICTEMENT', soit 'STRICTEMENTDIFFERENT'");
                }
                return new Indicateur(source.getId(), source.getZone(), source.isAffichageEtiquette(), source.getIndicateur(), source.getValeur(), getTypeDeVerification(source.getTypeDeVerification()));
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
                return new NombreCaracteres(source.getId(), source.getZone(), source.isAffichageEtiquette(), source.getSousZone(), source.getComparaisonOperateur(), source.getOccurrences());
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle ReciprociteWebDto en modèle SimpleRule (LinkedRule)
     */
    @Bean
    public void converterReciprociteToLinkedRule() {
        Converter<ReciprociteWebDto, SimpleRule> myConverter = new Converter<ReciprociteWebDto, SimpleRule>() {
            public SimpleRule convert(MappingContext<ReciprociteWebDto, SimpleRule> context) {
                ReciprociteWebDto source = context.getSource();
                return new Reciprocite(source.getId(), source.getZone(), source.isAffichageEtiquette(), source.getSousZone());
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle ComparaisonDateWebDto en modèle SimpleRule (LinkedRule)
     */
    @Bean
    public void converterComparaisonDateToLinkedRule() {
        Converter<ComparaisonDateWebDto, SimpleRule> myConverter = new Converter<ComparaisonDateWebDto, SimpleRule>() {
            public SimpleRule convert(MappingContext<ComparaisonDateWebDto, SimpleRule> context) {
                return constructComparaisonDate(context.getSource());
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un modèle TypeDocumentWebdto en modèle SimpleRule (LinkedRule)
     */
    @Bean
    public void converterTypeDocumentToLinkedRule() {
        Converter<TypeDocumentWebDto, SimpleRule> myConverter = new Converter<TypeDocumentWebDto, SimpleRule>() {
            public SimpleRule convert(MappingContext<TypeDocumentWebDto, SimpleRule> context) {
                TypeDocumentWebDto source = context.getSource();
                return constructTypeDocument(context.getSource());
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
                if (source.getRegles().stream().anyMatch(rule -> (rule.getZone() != null && rule.getZone().matches("\\dXX")))) {
                    throw new IllegalArgumentException("Une règle complexe ne peut pas contenir de règles simple avec des zones génériques");
                }

                if(source.getZone() != null && source.getZone().matches("\\dXX")){
                    throw new IllegalArgumentException("Une règle complexe sur une même instance de zone, ne peut pas avoir de zone générique");
                }
                Iterator<SimpleRuleWebDto> reglesIt = source.getRegles().listIterator();
                SimpleRuleWebDto firstRegle = reglesIt.next();
                if (firstRegle instanceof DependencyWebDto)
                    throw new IllegalArgumentException("La première règle d'une règle complexe ne peut pas être une règle de dépendance");
                if (firstRegle instanceof ReciprociteWebDto)
                    throw new IllegalArgumentException("La première règle d'une règle complexe ne peut pas être une règle de réciprocité");
                int i = 0;
                if (null == firstRegle.getBooleanOperator()) {
                    if(source.getZone() != null){
                        //verifier que la regle soit du bon type : [ presenceZone, presenceSousZone, presenceChaineCaracteres, indicateur, positionSousZone ]
                        checkRuleWebDtoIsAInstanseMemeZoneRules(firstRegle);
                        firstRegle.setZone(source.getZone());
                        SimpleRule firstRegleEntity = mapper.map(firstRegle, SimpleRule.class);
                        target = new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), firstRegleEntity);
                        target.setMemeZone(true);
                        firstRegleEntity.setComplexRule(target);
                    }else{
                        target = new ComplexRule(source.getId(), source.getMessage(), getPriority(source.getPriority()), mapper.map(firstRegle, SimpleRule.class));
                    }
                    if (source.getRuleSetList() != null) {
                        target.setRuleSet(getRuleSet(source.getRuleSetList()));
                    }
                    if (source.getTypesDoc() != null)
                        target.setFamillesDocuments(getFamilleDocument(source.getTypesDoc()));
                    checkTypeThese(source.getTypesThese());
                    if (source.getTypesThese() != null && !source.getTypesThese().isEmpty()) {
                        target.setTypesThese(getTypeThese(source.getTypesThese()));
                    }
                    boolean isPreviousRegleDependency = false;
                    boolean isDependencyRuleCreated = false;
                    while (reglesIt.hasNext()) {
                        SimpleRuleWebDto otherRegle = reglesIt.next();
                        checkTypeThese(otherRegle.getTypesThese());
                        if (otherRegle instanceof ReciprociteWebDto && !isDependencyRuleCreated)
                            throw new IllegalArgumentException("Une règle de dépendance doit être créée avant de créer une règle de réciprocité");
                        if (otherRegle.getBooleanOperator() == null && !(otherRegle instanceof DependencyWebDto) && !isPreviousRegleDependency && (source.getZone() == null))
                            throw new IllegalArgumentException("Les règles autres que la première d'une règle complexe doivent avoir un opérateur");
                        //si la règle précédente est de type dépendance, la règle en cours ne doit pas avoir d'opérateur
                        if (isPreviousRegleDependency && otherRegle.getBooleanOperator() != null)
                            throw new IllegalArgumentException("Une règle simple suivant une règle de dépendance ne doit pas avoir d'opérateur");

                        //Si la règle en cours est une règle de dépendance
                        if (otherRegle instanceof DependencyWebDto) {
                            //si on est en fin de liste de règle, il manque une règle simple
                            if (i == (source.getRegles().size() - 2))
                                throw new IllegalArgumentException("Une règle de dépendance doit toujours être suivie d'une règle simple");
                            checkDependencyRule((DependencyWebDto) otherRegle);
                            target.addOtherRule(new DependencyRule(otherRegle.getId(), otherRegle.getZone(), ((DependencyWebDto) otherRegle).getSousZone(), getTypeNoticeLiee(((DependencyWebDto) otherRegle).getTypeNoticeLiee()), i++, target));
                            isDependencyRuleCreated = true;
                        }
                        else {
                            if(source.getZone() != null) {
                                //verifier que la regle soit du bon type : [ presenceZone, presenceSousZone, presenceChaineCaracteres, indicateur, positionSousZone ]
                                checkRuleWebDtoIsAInstanseMemeZoneRules(otherRegle);
                                otherRegle.setZone(source.getZone());
                                SimpleRule simpleRule = mapper.map(otherRegle, SimpleRule.class);
                                simpleRule.setComplexRule(target);
                                target.addOtherRule(new LinkedRule(simpleRule,  BooleanOperateur.ET, i++, target));
                            } else {
                                target.addOtherRule(new LinkedRule(mapper.map(otherRegle, SimpleRule.class), isPreviousRegleDependency ? BooleanOperateur.ET : getOperateur(otherRegle.getBooleanOperator()), i++, target));
                            }
                        }
                        isPreviousRegleDependency = otherRegle instanceof DependencyWebDto;
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
                            resultRulesResponseDto = new ResultRulesResponseDto(resultRules.getPpn(), (resultRules.getTypeThese().equals(TypeThese.REPRO) ? "Thèse de reproduction" : "Thèse de soutenance"), resultRules.getMessages());
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
                    source.getTypesThese().forEach(tt -> typesDoc.append((tt.equals(TypeThese.REPRO) ? "Thèse de reproduction, " : "Thèse de soutenance, ")));
                }
                if (source.getFamillesDocuments().isEmpty() && source.getTypesThese().isEmpty()) {
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
     * Convertion d'un object FamilleDocument en FamilleDocumentWebDto
     */
    @Bean
    public void converterFamilleDocument() {
        Converter<FamilleDocument, FamilleDocumentWebDto> myConverter = new Converter<FamilleDocument, FamilleDocumentWebDto>() {
            public FamilleDocumentWebDto convert(MappingContext<FamilleDocument, FamilleDocumentWebDto> context) {
                FamilleDocument source = context.getSource();
                return new FamilleDocumentWebDto(source.getId(), source.getLibelle());
            }
        };
        mapper.addConverter(myConverter);
    }

    /**
     * Convertion d'un objet RuleSetWebDto en RuleSet
     */
    @Bean
    public void converterRuleSetWebDtoToRuleSet() {
        Converter<RuleSetWebDto, RuleSet> myConverter = new Converter<RuleSetWebDto, RuleSet>() {
            public RuleSet convert(MappingContext<RuleSetWebDto, RuleSet> context) {
                RuleSetWebDto source = context.getSource();
                if(source.getId() == null){
                    throw new IllegalArgumentException("L'identifiant du jeu de règles est obligatoire");
                }
                if(source.getLibelle() == null){
                    throw new IllegalArgumentException("Le libellé du jeu de règles est obligatoire");
                }
                if(source.getPosition() == null){
                    throw new IllegalArgumentException("La position du jeu de règles est obligatoire");
                }
                return new RuleSet(source.getId(), source.getLibelle(), source.getDescription(), source.getPosition());
            }
        };
        mapper.addConverter(myConverter);
    }

    // -------------------------------------- Constructeur simplerule --------------------------------------

    /**
     * Création d'un objet PresenceChaineCaracteres à partir des données issues d'un objet PresenceChaineCaracteresWebDto
     * @param source PresenceChaineCaracteresWebDto
     * @return PresenceChaineCaracteres
     */
    private PresenceChaineCaracteres constructPresenceChaineCaracteres(PresenceChaineCaracteresWebDto source) {
        PresenceChaineCaracteres target = new PresenceChaineCaracteres(source.getId(), source.getZone(), source.isAffichageEtiquette(), source.getSousZone(), getTypeDeVerification(source.getTypeDeVerification()));
        if (source.getListChaineCaracteres() != null && !source.getListChaineCaracteres().isEmpty()) {
            int i = 0;
            for (PresenceChaineCaracteresWebDto.ChaineCaracteresWebDto chaine : source.getListChaineCaracteres()) {
                if (chaine.getOperateur() == null || chaine.getOperateur().isEmpty()) {
                    target.addChaineCaracteres(new ChaineCaracteres(i, chaine.getChaineCaracteres(), target));
                    i++;
                } else {
                    target.addChaineCaracteres(new ChaineCaracteres(i, getOperateur(chaine.getOperateur()), chaine.getChaineCaracteres(), target));
                    i++;
                }
            }
        }
        return target;
    }

    private PresenceSousZonesMemeZone constructPresenceSousZonesMemeZone(PresenceSousZonesMemeZoneWebDto source) {
        PresenceSousZonesMemeZone target = new PresenceSousZonesMemeZone(source.getId(), source.getZone(), source.isAffichageEtiquette());
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

    private ComparaisonDate constructComparaisonDate(ComparaisonDateWebDto source) {
        ComparaisonDate target = new ComparaisonDate(source.getId(), source.getZone(), source.isAffichageEtiquette(), source.getSousZone(), source.getZoneCible(), source.getSousZoneCible(), getComparaisonOperateur(source.getComparateur()));
        if(source.getPositionStart() != null && source.getPositionEnd() != null) {
            if(source.getPositionStart() > source.getPositionEnd()) {
                throw new IllegalArgumentException("Règle " + source.getId() + " : la position de début doit être inférieure à la position de fin");
            } else {
                target.setPositionStart(source.getPositionStart());
                target.setPositionEnd(source.getPositionEnd());
            }
        }
        if(source.getPositionStartCible() != null && source.getPositionEndCible() != null){
            if(source.getPositionStartCible() > source.getPositionEndCible()) {
                throw new IllegalArgumentException("Règle " + source.getId() + " : la position de début de la cible doit être inférieure à la position de fin de la cible");
            } else {
                target.setPositionStartCible(source.getPositionStartCible());
                target.setPositionEndCible(source.getPositionEndCible());
            }
        }
        return target;
    }
    private TypeCaractere constructTypeCaractere(TypeCaractereWebDto source) {
        if(source.getTypeCaracteres().isEmpty()){
            throw new IllegalArgumentException("Règle " + source.getId() + " : Le champ type-caracteres est obligatoire");
        }
        TypeCaractere target = new TypeCaractere(source.getId(), source.getZone(), source.isAffichageEtiquette(), source.getSousZone());
        for(String typeCaracteresString : source.getTypeCaracteres()){
            target.addTypeCaractere(getTypeCaracteres(typeCaracteresString));
        }
        return target;
    }

    private TypeDocument constructTypeDocument(TypeDocumentWebDto source) {
        if (source.getValeur() == null || source.getValeur().isEmpty())
            throw new IllegalArgumentException("Règle " + source.getId() + " : le champ valeur est obligatoire");
        if (source.getTypeDeVerification() == null || source.getTypeDeVerification().isEmpty())
            throw new IllegalArgumentException("Règle " + source.getId() + " : le champ type-de-verification est obligatoire");
        if (source.getPosition() == null)
            throw new IllegalArgumentException("Règle " + source.getId() + " : le champ position est obligatoire");
        if (source.getPosition() == 0 || source.getPosition() > 4)
            throw new IllegalArgumentException("Règle " + source.getId() + " : le champ position ne peut être compris qu'entre 1 et 4");
        return new TypeDocument(source.getId(), source.isAffichageEtiquette(), getTypeDeVerification(source.getTypeDeVerification()), source.getPosition(), source.getValeur());
    }

    private Integer convertStringToInteger(String myIntegerInString) {
        if(myIntegerInString == null)
            return null;
        return Integer.valueOf(myIntegerInString);
    }

    // ---------------------------------------- Get Enum From String ----------------------------------------

    private Priority getPriority(String priority) {
        return EnumUtils.getEnum(Priority.class, priority);
    }

    private TypeVerification getTypeDeVerification(String typeDeVerification) {
        return EnumUtils.getEnum(TypeVerification.class, typeDeVerification);
    }

    private ComparaisonOperateur getComparaisonOperateur(String comparaisonOperateur){
        return EnumUtils.getEnum(ComparaisonOperateur.class, comparaisonOperateur);
    }

    private BooleanOperateur getOperateur(String operateur) {
        return EnumUtils.getEnum(BooleanOperateur.class, operateur);
    }

    private TypeCaracteres getTypeCaracteres(String type){
        return EnumUtils.getEnum(TypeCaracteres.class, type);
    }

    private TypeNoticeLiee getTypeNoticeLiee(String type){
        return EnumUtils.getEnum(TypeNoticeLiee.class, type);
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
        if(types == null)
            return null;
        for (String type : types) {
            typeTheseSet.add(EnumUtils.getEnum(TypeThese.class, type));
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

    //---------------------------------------------------------- Checkers ----------------------------------------------------------

    private void checkDependencyRule(DependencyWebDto regle) throws IllegalArgumentException {
        if (regle.getPriority() != null)
            throw new IllegalArgumentException("Une règle de dépendance ne peut pas avoir de priorité");
        if (regle.getMessage() != null)
            throw new IllegalArgumentException("Une règle de dépendance ne peut pas avoir de message");
        if (regle.getBooleanOperator() != null)
            throw new IllegalArgumentException("Une règle de dépendance ne peut pas avoir d'opérateur");
        if (!regle.getRuleSetList().isEmpty())
            throw new IllegalArgumentException("Une règle de dépendance ne peut pas avoir de jeu de règles personnalisé");
        if (!regle.getTypesDoc().isEmpty())
            throw new IllegalArgumentException("Une règle de dépendance ne peut pas avoir de famille de documents");
        if (!regle.getTypesThese().isEmpty())
            throw new IllegalArgumentException("Une règle de dépendance ne peut pas avoir de type thèse");
    }

    private void checkOtherRule(SimpleRuleWebDto source) {
        if (source.getBooleanOperator() != null) {
            throw new IllegalArgumentException("Règle " + source.getId() + " : L'opérateur est interdit lors de la création d'une seule règle");
        }
        if (source.getMessage() == null || source.getPriority() == null) {
            throw new IllegalArgumentException("Règle " + source.getId() + " : Le message et / ou la priorité est obligatoire lors de la création d'une règle simple");
        }
        // typeDoc est sur la zone 008
        if (source.getZone() == null && !(source instanceof TypeDocumentWebDto)) {
            throw new IllegalArgumentException("Règle " + source.getId() + " : La zone est obligatoire lors de la création d'une règle simple");
        }
        checkTypeThese(source.getTypesThese());
    }

    private void checkTypeThese(List<String> typesThese) {
        if (typesThese != null && !typesThese.isEmpty()) {
            if (typesThese.stream().noneMatch(tt -> EnumUtils.isValidEnum(TypeThese.class, tt))) {
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

    private void checkRuleWebDtoIsAInstanseMemeZoneRules(SimpleRuleWebDto rule){
        if(!(rule instanceof PresenceZoneWebDto) && !(rule instanceof PresenceSousZoneWebDto) && !(rule instanceof PositionSousZoneWebDto) && !(rule instanceof PresenceChaineCaracteresWebDto) && !(rule instanceof IndicateurWebDto)){
            throw new IllegalArgumentException("Règle " + rule.getId() + " : La règle n'est pas une règle qui peut s'appliquer sur une même instance de zone");
        }
        if(rule.getZone() != null){
            throw new IllegalArgumentException("Les règles complexe ne peuvent pas contenir de règles simples avec des zones différentes");
        }
    }
}
