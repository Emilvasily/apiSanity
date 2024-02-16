package automation.api.regression.messageBuilder;

import java.util.ArrayList;

public class ErrorPartBuilder {

    /**
     * This singleton class is used by the Listener to store messages
     * about failed and skipped tests in the ArrayList
     * for further manipulations
     */

    private static ErrorPartBuilder errorPartBuilder = null;
    private final ArrayList<String> errorMessages;

    private ErrorPartBuilder() {
        errorMessages = new ArrayList<>();
    }

    public void addErrorMessage(String message){
        this.errorMessages.add(message
                .replace("\n", " ")
                .replace("'", "")
                );
    }

    public ArrayList<String> getErrorMessages() {
        return errorMessages;
    }

    public static ErrorPartBuilder getInstance() {
        if (errorPartBuilder == null)
            errorPartBuilder = new ErrorPartBuilder();
        return errorPartBuilder;
    }

}
