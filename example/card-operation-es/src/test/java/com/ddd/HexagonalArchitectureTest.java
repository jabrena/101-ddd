package com.ddd;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.onionArchitecture;

//TODO: Fix Persistence needing access to domain.model classes
//@AnalyzeClasses(packages = "com.ddd.balance", importOptions = {ImportOption.DoNotIncludeTests.class})
public class HexagonalArchitectureTest {

    @ArchTest
    static final ArchRule follow_onion_architecture_structure =
            onionArchitecture()
                    .domainModels("..domain.model..")
                    .domainServices("..domain.service..")
                    .applicationServices("..application..")
                    .adapter("configuration", "..infrastructure.configuration..")
                    .adapter("persistence", "..infrastructure.persistence..")
                    .adapter("rest", "..infrastructure.rest..")
            ;
}
