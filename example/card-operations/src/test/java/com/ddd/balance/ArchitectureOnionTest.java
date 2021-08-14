package com.ddd.balance;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.onionArchitecture;

@AnalyzeClasses(packages = "com.ddd.balance", importOptions = {ImportOption.DoNotIncludeTests.class})
public class ArchitectureOnionTest {

    @ArchTest
    static final ArchRule follow_onion_architecture_structure =
            onionArchitecture()
                    .domainModels("..domain.model..")
                    .domainServices("..domain.service..")
                    .applicationServices("..application..")
                    .adapter("rest", "..infrastructure.rest..");

}
