package fr.abes.qualimarc.core.configuration;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

class QualimarcDbConfigContractTest {

    @Test
    void exceptionTranslationBeanShouldBeDeclaredStatic() throws NoSuchMethodException {
        Method exceptionTranslation = QualimarcDbConfig.class.getDeclaredMethod("exceptionTranslation");

        assertThat(Modifier.isStatic(exceptionTranslation.getModifiers())).isTrue();
    }
}
