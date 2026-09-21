package com.lectoria_api.lectoria_api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

@Disabled
class LectoriaApiApplicationTests {

    private ApplicationContext applicationContext;

    public LectoriaApiApplicationTests(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Test
	void contextLoads() {
        Assertions.assertNotNull(applicationContext);
	}

}
