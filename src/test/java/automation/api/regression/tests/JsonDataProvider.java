package automation.api.regression.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class JsonDataProvider {

    private static final String JSON_FILE_PATH = "src/test/resources/config.json";

    public static ServiceData getServiceData(String serviceName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(new File(JSON_FILE_PATH));

        JsonNode serviceNode = jsonNode.at("/services/" + serviceName);
        String servicePath = serviceNode.at("/service_path").asText();
        String filePath = serviceNode.at("/file_path").asText();
        int status = serviceNode.at("/status").asInt();
        String bodyKeyToValidate = serviceNode.at("/body/0").asText();
        String bodyValueToValidate = serviceNode.at("/body/1").asText();
        int executionTime = serviceNode.at("/execution_time").asInt();

        return new ServiceData(servicePath, filePath, status, bodyKeyToValidate, bodyValueToValidate, executionTime);
    }

    public static class ServiceData {
        private final String servicePath;
        private final String filePath;
        private final int status;
        private final String bodyKeyToValidate;
        private final String bodyValueToValidate;
        private final int executionTime;

        public ServiceData(String servicePath, String filePath, int status, String bodyKeyToValidate, String bodyValueToValidate, int executionTime) {
            this.servicePath = servicePath;
            this.filePath = filePath;
            this.status = status;
            this.bodyKeyToValidate = bodyKeyToValidate;
            this.bodyValueToValidate = bodyValueToValidate;
            this.executionTime = executionTime;
        }

        public String getServicePath() {
            return servicePath;
        }

        public String getFilePath() {
            return filePath;
        }

        public int getStatus() {
            return status;
        }

        public String getBodyKeyToValidate() {
            return bodyKeyToValidate;
        }

        public String getBodyValueToValidate() {
            return bodyValueToValidate;
        }

        public int getExecutionTime() {
            return executionTime;
        }
    }

}
