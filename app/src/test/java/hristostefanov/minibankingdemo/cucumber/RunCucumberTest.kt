package hristostefanov.minibankingdemo.cucumber

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

// NOTE: If this class is not in the default package, some ancestor
// directory path for feature files must be provided to @CucumberOptions.
@RunWith(Cucumber::class)
// NOTE: it's not clear what "pretty" plugin does, but normally goes together
// with the "html" plugin. Perhaps, it improves the generated html code.
@CucumberOptions( features = ["src/test"],
    glue = ["features"],
    plugin = ["pretty", "html:build/reports/tests/cucumber.html"])
class RunCucumberTest