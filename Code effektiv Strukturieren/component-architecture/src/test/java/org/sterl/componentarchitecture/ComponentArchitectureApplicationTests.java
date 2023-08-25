package org.sterl.componentarchitecture;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

@SpringBootTest
class ComponentArchitectureApplicationTests {

    @Test
    void contextLoads() {
        var modules = ApplicationModules.of(ComponentArchitectureApplication.class);

        new Documenter(modules).writeModulesAsPlantUml().writeIndividualModulesAsPlantUml();
        
        modules.verify();
    }

}