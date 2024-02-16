package automation.api.regression.tests;

import io.qameta.allure.*;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.hasItem;

@Epic("API Sanity Check")
public class GetInfo {

    private static final Logger logger = LogManager.getLogger(GetInfo.class);
    private ValidatableResponse validatableResponse = null;
    private JsonDataProvider.ServiceData serviceData;

    @Story("POST /api/v2/getInfo")
    @BeforeClass
    public void testSetUp() {
        try {
            serviceData = JsonDataProvider.getServiceData("GetInfo");
            String jsonBody = Files.readString(Path.of(serviceData.getFilePath()));
            RequestSpecification request = RestAssured.given()
                    .filter(new AllureRestAssured())
                    .contentType(ContentType.JSON)
                    .body(jsonBody);
            logger.info("Request body: {}", jsonBody);
            Response response = request.post(serviceData.getServicePath());
            validatableResponse = response.then();
            logger.info("Response body: {}", response.getBody().asString());
        } catch (IOException e) {
            logger.error("Exception thrown while setting up test: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Description("Should get status as indicated in the config file")
    @Severity(SeverityLevel.BLOCKER)
    @Test(priority = 0)
    public void getResponseStatusAsIndicated() {
        validatableResponse.assertThat().statusCode(serviceData.getStatus());
    }

    @Description("Validate response body as indicated in the config file")
    @Severity(SeverityLevel.NORMAL)
    @Test(priority = 1)
    public void getResponseValidate() {
        validatableResponse.assertThat().body(serviceData.getBodyKeyToValidate(), hasItem(serviceData.getBodyValueToValidate()));
    }

    @Description("Should get response less than indicated in the config file")
    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 2)
    public void getResponseLessThanIndicated() {
        validatableResponse.time(Matchers.lessThan((long) serviceData.getExecutionTime()));
    }

}
