package automation.api.regression.tests.listeners;

import automation.api.regression.messageBuilder.ErrorPartBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class Listener implements ITestListener {

    private static final Logger logger = LogManager.getLogger(Listener.class);
    private final ErrorPartBuilder errorPartBuilder = ErrorPartBuilder.getInstance();

    @Override
    public void onTestFailure(ITestResult result) {
        errorPartBuilder.addErrorMessage("<b>Failed test:<*b> " + result.getInstanceName() + "." + result.getName() +
                " <b>Cause:<*b> " + result.getThrowable().getMessage());
        logger.error("Failed test added to the error stack: {}.{}", result.getInstanceName(), result.getName(), result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        errorPartBuilder.addErrorMessage("<b>Skipped test:<*b> " + result.getInstanceName() + "." + result.getName() +
                " <b>Cause:<*b> " + result.getThrowable().getMessage());
        logger.warn("Skipped test added to the error stack: {}.{}", result.getInstanceName(), result.getName(), result.getThrowable());
    }

}
