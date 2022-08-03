package fr.abes.qualimarc.web.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.qualimarc.core.model.resultats.ResultAnalyse;
import fr.abes.qualimarc.core.model.resultats.ResultRules;
import fr.abes.qualimarc.core.utils.UtilsMapper;
import fr.abes.qualimarc.web.dto.ResultAnalyseResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class})
@SpringBootTest(classes = {UtilsMapper.class, ObjectMapper.class, WebDtoMapper.class})
public class WebDtoMapperTest {
    @Autowired
    UtilsMapper mapper;

    @Test
    @DisplayName("Test Mapper resultAnalyseResponseDto")
    void testResultAnalyseResponseDtoMapper() {
        ResultAnalyse resultAnalyse = new ResultAnalyse();

        resultAnalyse.addPpnAnalyse("111111111");
        resultAnalyse.addPpnAnalyse("222222222");
        resultAnalyse.addPpnAnalyse("333333333");
        resultAnalyse.addPpnAnalyse("444444444");
        resultAnalyse.addPpnAnalyse("555555555");
        resultAnalyse.addPpnAnalyse("666666666");
        resultAnalyse.addPpnAnalyse("777777777");
        resultAnalyse.addPpnAnalyse("888888888");

        resultAnalyse.addPpnErrone("111111111");
        resultAnalyse.addPpnOk("222222222");
        resultAnalyse.addPpnOk("555555555");
        resultAnalyse.addPpnErrone("888888888");
        resultAnalyse.addPpnErrone("777777777");
        resultAnalyse.addPpnInconnu("666666666");
        resultAnalyse.addPpnInconnu("444444444");
        resultAnalyse.addPpnInconnu("333333333");

        ResultRules result1 = new ResultRules("111111111");
        result1.addMessage("Message 1");
        result1.addMessage("Message 2");

        ResultRules result2 = new ResultRules("888888888");
        result2.addMessage("Message 3");
        result2.addMessage("Message 4");
        result2.addMessage("Message 5");

        ResultRules result3 = new ResultRules("777777777");
        result3.addMessage("Message 6");

        resultAnalyse.addResultRule(result1);
        resultAnalyse.addResultRule(result2);
        resultAnalyse.addResultRule(result3);

        ResultAnalyseResponseDto responseDto = mapper.map(resultAnalyse, ResultAnalyseResponseDto.class);

        Assertions.assertEquals(3, responseDto.getResultRules().size());
        Assertions.assertEquals(2, responseDto.getResultRules().stream().filter(r -> r.getPpn().equals("111111111")).findFirst().get().getMessages().size());
        Assertions.assertEquals(3, responseDto.getResultRules().stream().filter(r -> r.getPpn().equals("888888888")).findFirst().get().getMessages().size());
        Assertions.assertEquals(1, responseDto.getResultRules().stream().filter(r -> r.getPpn().equals("777777777")).findFirst().get().getMessages().size());
        Assertions.assertEquals(3, responseDto.getNbPpnErrones());
        Assertions.assertEquals(8, responseDto.getNbPpnAnalyses());
        Assertions.assertEquals(3, responseDto.getNbPpnInconnus());
        Assertions.assertEquals(2, responseDto.getNbPpnOk());
        Assertions.assertEquals(8, responseDto.getPpnAnalyses().size());
        Assertions.assertEquals(3, responseDto.getPpnErrones().size());
        Assertions.assertEquals(3, responseDto.getPpnInconnus().size());
        Assertions.assertEquals(2, responseDto.getPpnOk().size());
    }
}
