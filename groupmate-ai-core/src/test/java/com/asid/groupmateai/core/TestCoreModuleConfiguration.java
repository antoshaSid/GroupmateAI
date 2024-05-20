package com.asid.groupmateai.core;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(CoreModuleConfiguration.class)
@EnableAutoConfiguration
public class TestCoreModuleConfiguration {
}
