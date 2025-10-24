package com.partyguham;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PartyguhamBackendSpringApplicationTests {

    @Test
    void contextLoads() {

        Assertions.assertThat('a');
    }

    @Test
    @DisplayName("모든 빈 출력하기")
    void findAllBean() {
    }
}
