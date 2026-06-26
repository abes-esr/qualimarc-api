package fr.abes.qualimarc.web.dto.indexrules;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

class DependencyWebDtoValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("DependencyWebDto accepte les index negatifs documentes")
    void validateSignedPositions() {
        DependencyWebDto dependencyWebDto = new DependencyWebDto(1, "200", "a", "AUTORITE", "-1", "-2", "-1");

        Set<ConstraintViolation<DependencyWebDto>> violations = validator.validate(dependencyWebDto);

        Assertions.assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("DependencyWebDto rejette les positions non numeriques ou trop longues")
    void rejectInvalidPositions() {
        DependencyWebDto dependencyWebDto = new DependencyWebDto(1, "200", "a", "AUTORITE", "1a", "-1000", "abc");

        Set<String> invalidProperties = validator.validate(dependencyWebDto).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());

        Assertions.assertEquals(Set.of("position", "positionStart", "positionEnd"), invalidProperties);
    }
}
