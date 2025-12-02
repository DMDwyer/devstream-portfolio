package com.dmdwyer.devstream;

import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Epic("Application Infrastructure")
@Feature("Spring Boot Context")
@SpringBootTest
class DevstreamPortfolioApplicationTests {

	@Test
	@Story("Application startup")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Verify that Spring Boot application context loads successfully")
	void contextLoads() {
	}

}
