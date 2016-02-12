package at.ftec.aerogear.model.result;

import java.util.List;

/**
 * {
    status:OK | WARN | CRIT
    summary:string
    details:[
         {
         runtime:number
         result:string
         description:string
         test_status:OK | WARN | CRIT
         }
    ]
}
 *
 * there are a HealthStatus class in den AeroGear Server.
 * But not in the model API
 * So i have to write a own one
 *
 * @author Michael Fischelmayer
 */

public class HealthStatus {
    private String status;
    private String summary;
    private List<HealthStatusDetails> details;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<HealthStatusDetails> getDetails() {
        return details;
    }

    public void setDetails(List<HealthStatusDetails> details) {
        this.details = details;
    }
}
