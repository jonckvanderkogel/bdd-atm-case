package com.ing.bdd.integration;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    plugin = {"pretty", "html:target/cucumber.html", "json:target/cucumber.json"},
    glue = {"com.ing.bdd.integration"},
    tags = "not @ignore"
)
public class CucumberIT {
}
