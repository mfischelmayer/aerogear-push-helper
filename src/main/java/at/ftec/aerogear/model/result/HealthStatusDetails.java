package at.ftec.aerogear.model.result;

/**
 * @author Michael Fischelmayer
 */
public class HealthStatusDetails {
    private long runtime;
    private String result;
    private String description;
    private String test_status;

    public long getRuntime() {
        return runtime;
    }

    public void setRuntime(long runtime) {
        this.runtime = runtime;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTest_status() {
        return test_status;
    }

    public void setTest_status(String test_status) {
        this.test_status = test_status;
    }
}
