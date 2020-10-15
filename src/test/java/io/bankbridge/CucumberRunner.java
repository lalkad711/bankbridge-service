package io.bankbridge;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
        stepNotifications = true,
        features = "src/test/resources/features/bankridgeservice.feature",
        plugin = {
                "pretty",
                "json:target/cucumber.json"
        },
        glue = {
                "io.bankbridge"
        },
        monochrome = true,
        strict = true
)
public class CucumberRunner {
}
