package automation.api.regression.tests;

import automation.api.regression.messageBuilder.ErrorPartBuilder;
import com.google.common.collect.ImmutableMap;
import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.github.automatedowl.tools.AllureEnvironmentWriter.allureEnvironmentWriter;

public class SuiteSetup {

    private static final Logger logger = LogManager.getLogger(SuiteSetup.class);
    private final File resultsInput = new File("src/test/resources/results.json");
    private final File resultsOutput = new File("target/results.json");
    private final File restAssuredConfigFile = new File("src/test/resources/config.json");

    public String readRestAssuredBaseURL(){
        JSONObject jsonObject = new JSONObject(jsonToString(restAssuredConfigFile));
        return jsonObject.getString("url");
    }

    @Description("Setting up Base URL and Allure Environment Information")
    @BeforeSuite
    void config() {
        RestAssured.baseURI = readRestAssuredBaseURL();
        logger.info("RestAssured baseURL set to: {}", RestAssured.baseURI);
        //
        RestAssured.config= RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.socket.timeout",20000)
                        .setParam("http.connection.timeout", 20000));
        logger.info("RestAssured socket and connection timeout set to 20000ms");
        //
        allureEnvironmentInformation();
    }

    public void allureEnvironmentInformation(){
        allureEnvironmentWriter(
                ImmutableMap.<String, String>builder()
                        .put("Point", "Alpha")
                        .put("OS", "Ubuntu 20.04.3 LTS")
                        .put("URL", "http://10.2.12.240")
                        .build());
    }

    @AfterSuite(alwaysRun = true)
    public void postConfig(ITestContext context){
        int successCount = context.getPassedTests().size();
        int failedCount = context.getFailedTests().size();
        int skipCount = context.getSkippedTests().size();
        //CHOOSE CHAT ENV
        String channelEnv = "test";
        provideResults(successCount, failedCount, skipCount, channelEnv);
    }

    public void provideResults(int successCount, int failedCount, int skipCount, String channel){
        JSONObject jsonObject = new JSONObject(jsonToString(resultsInput));

        jsonObject.put("successCount", successCount);
        jsonObject.put("failedCount", failedCount);
        jsonObject.put("skippedCount", skipCount);

        //CHOOSE CHAT ENV
        jsonObject.put("channelEnv", channel);

        ErrorPartBuilder errorPartBuilder = ErrorPartBuilder.getInstance();
        if(!errorPartBuilder.getErrorMessages().isEmpty()){
            jsonObject.put("errors", Arrays.toString(errorPartBuilder.getErrorMessages().toArray())
                    .replace("[", "")
                    .replace("]", ""));
            //SEND MESSAGE IF THERE IS AN ERROR
            stringToJson(resultsOutput, jsonObject);
            logger.info("Json to send to msgcontroller: {}", jsonObject.toString());
        } else {
            //CHOOSE SILENT OR NOT, IF THERE IS NO ERRORS
            jsonObject.put("errors", "");
            //UNCOMMENT THE LINE TO DISABLE SILENT MODE
            stringToJson(resultsOutput, jsonObject);
        }
    }

    public String jsonToString(File inputFile){
        try {
            return FileUtils.readFileToString(inputFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("Failed to read file: {}", inputFile);
            throw new RuntimeException(e);
        }
    }

    public void stringToJson(File outputFile, JSONObject jsonObject){
        try {
            FileUtils.writeStringToFile(outputFile, jsonObject.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("Failed to write file: {}", outputFile);
            throw new RuntimeException(e);
        }
    }

}
