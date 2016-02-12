package at.ftec.aerogear.model;

import org.jboss.aerogear.unifiedpush.api.PushApplication;

import java.util.List;

/**
 * @author Michael Fischelmayer
 */
public class PushApplicationsResult {

    private int totalCount;
    private List<PushApplication> pushApplications;

    /**
     *
     * @return the total count of all installed applications
     */
    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    /**
     *
     * @return a list of push applications
     */
    public List<PushApplication> getPushApplications() {
        return pushApplications;
    }

    public void setPushApplications(List<PushApplication> pushApplications) {
        this.pushApplications = pushApplications;
    }
}
