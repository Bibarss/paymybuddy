package com.paymybuddy;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test") // Utilise le profil de test avec H2
@SpringBootTest
class PaymybuddyApplicationTests {

	@Test
	void contextLoads() {
	}

}