import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

// Note: for some reason Gradle :test task cannot run it if located in a package
@RunWith(Cucumber::class)
// NOTE: it's not clear what "pretty" plugin does, but normally goes together
// with the "html" plugin. Perhaps, it improves the generated html code.
@CucumberOptions(plugin = ["pretty", "html:build/reports/tests/cucumber.html"])
class RunCucumberTest