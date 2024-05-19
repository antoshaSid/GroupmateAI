package com.asid.groupmateai.storage;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(StorageModuleConfiguration.class)
@EnableAutoConfiguration
public class TestStorageModuleConfiguration {
}
