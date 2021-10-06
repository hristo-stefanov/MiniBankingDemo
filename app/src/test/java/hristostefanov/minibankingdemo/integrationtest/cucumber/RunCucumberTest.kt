package hristostefanov.minibankingdemo.integrationtest.cucumber

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.ClassRule
import org.junit.Rule
import org.junit.runner.RunWith

// NOTE: If this class is not in the default package, some ancestor
// directory path for feature files must be provided to @CucumberOptions.
@RunWith(Cucumber::class)
@CucumberOptions(
    features = ["src/test"],
    glue = ["hristostefanov.minibankingdemo.integrationtest.steps"],
// NOTE: it's not clear what "pretty" plugin does, but normally goes together
// with the "html" plugin. Perhaps, it improves the generated html code.
    plugin = ["pretty", "html:build/reports/tests/cucumber.html"],
//    tags = "not @draft",
//    tags = "@debug",
)
class RunCucumberTest {


    // NOTE: needed for proper testing of Architecture Components -
// makes background tasks execute synchronously.
// More importantly, provides TaskExecutor#isMainThread implementation which always return `true`
// thus avoiding exceptions in LiveData's observe* methods.

    companion object {
        @JvmStatic
        @get:ClassRule
        val rule = InstantTaskExecutorRule()
    }
}
