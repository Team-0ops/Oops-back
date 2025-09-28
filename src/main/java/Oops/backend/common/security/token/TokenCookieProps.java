package Oops.backend.common.security.token;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.auth.token-cookie")
class TokenCookieProps {
    private String accessCookieName = "AccessToken";
    private String refreshCookieName = "RefreshToken";
    private String sameSite = "None";
    private boolean secure = true;
    private String path = "/";

    private long accessMaxAgeSec = 30 * 60;            // 30분
    private long refreshMaxAgeSec = 14 * 24 * 60 * 60; // 14일


    public String getAccessCookieName() { return accessCookieName; }
    public void setAccessCookieName(String v) { this.accessCookieName = v; }
    public String getRefreshCookieName() { return refreshCookieName; }
    public void setRefreshCookieName(String v) { this.refreshCookieName = v; }
    public String getSameSite() { return sameSite; }
    public void setSameSite(String v) { this.sameSite = v; }
    public boolean isSecure() { return secure; }
    public void setSecure(boolean v) { this.secure = v; }
    public String getPath() { return path; }
    public void setPath(String v) { this.path = v; }
    public long getAccessMaxAgeSec() { return accessMaxAgeSec; }
    public void setAccessMaxAgeSec(long v) { this.accessMaxAgeSec = v; }
    public long getRefreshMaxAgeSec() { return refreshMaxAgeSec; }
    public void setRefreshMaxAgeSec(long v) { this.refreshMaxAgeSec = v; }
}
