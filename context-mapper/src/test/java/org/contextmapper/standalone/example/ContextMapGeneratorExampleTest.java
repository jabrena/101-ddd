package org.contextmapper.standalone.example;

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.generator.ContextMapGenerator;
import org.contextmapper.dsl.standalone.ContextMapperStandaloneSetup;
import org.contextmapper.dsl.standalone.StandaloneContextMapperAPI;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ContextMapGeneratorExampleTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "./src/main/cml/my-examples/Event-Storming-Stage-1.cml",
            "./src/main/cml/my-examples/Event-Storming-Stage-2.cml",
            "./src/main/cml/my-examples/Event-Storming-Stage-3.cml",
            "./src/main/cml/my-examples/Event-Storming-Stage-4.cml",
            "./src/main/cml/my-examples/Event-Storming-Stage-5.cml"
    })
    public void given_cml1_when_execute_then_Ok(String DSL_EXAMPLE_FILE) {

        // Setup and loading CML file:
        StandaloneContextMapperAPI contextMapper = ContextMapperStandaloneSetup.getStandaloneAPI();
        //CMLResource resource = contextMapper.loadCML(INSURANCE_EXAMPLE_URI);
        CMLResource resource = contextMapper.loadCML(DSL_EXAMPLE_FILE);

        // Create the PlantUML generator
        ContextMapGenerator generator = new ContextMapGenerator();

        // Generate the diagrams into 'src-gen'
        contextMapper.callGenerator(resource, generator);
    }
}
