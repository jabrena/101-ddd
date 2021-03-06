/*
 * Copyright 2019 The Context Mapper Project Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.contextmapper.standalone.example;

import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.generator.PlantUMLGenerator;
import org.contextmapper.dsl.standalone.ContextMapperStandaloneSetup;
import org.contextmapper.dsl.standalone.StandaloneContextMapperAPI;
import org.junit.jupiter.api.Test;

/**
 * This example shows how you can read your CML model and generate the PlantUML
 * component and class diagrams from it.
 *
 * @author Stefan Kapferer
 */
public class PlantUMLGeneratorExampleTest {

    @Test
    public void test() {

        String model = "./src/main/cml/my-examples/Event-Storming-Stage-4.cml";

        String DSL_EXAMPLE_FILE =
                "./src/main/cml/context-mapper-example/ContextMapper-Example-Stage-4.cml";

        // Setup and loading CML file:
        StandaloneContextMapperAPI contextMapper = ContextMapperStandaloneSetup.getStandaloneAPI();
        //CMLResource resource = contextMapper.loadCML(INSURANCE_EXAMPLE_URI);
        CMLResource resource = contextMapper.loadCML(model);

        // Generate the diagrams into 'src-gen'
        contextMapper.callGenerator(resource, new PlantUMLGenerator());
    }

}
