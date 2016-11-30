package org.thoughtcrime.securesms;

public class BillingInfo {

    private String id;
    private String name;
    private long created;
    private String stripe_user_id;
    private String token_type;
    private String stripe_publishable_key;
    private String scope;
    private boolean livemode;
    private String refresh_token;
    private String access_token;

    // necessary no-arg constructor (for GSON)
    BillingInfo() {}

    public String getTokenType() {
        return token_type;
    }

    public void setTokenType(String tokenType) {
        this.token_type = tokenType;
    }

    public String getPublishableKey() {
        return stripe_publishable_key;
    }

    public void setPublishableKey(String publishableKey) {
        this.stripe_publishable_key = publishableKey;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public boolean isLiveMode() {
        return livemode;
    }

    public void setLiveMode(boolean liveMode) {
        this.livemode = livemode;
    }

    public String getRefreshToken() {
        return refresh_token;
    }

    public void setRefreshToken(String refreshToken) {
        this.refresh_token = refreshToken;
    }

    public String getAccessToken() {
        return access_token;
    }

    public void setAccessToken(String accessToken) {
        this.access_token = accessToken;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getUserId() {
        return stripe_user_id;
    }

    public void setUserId(String userId) {
        this.stripe_user_id = userId;
    }
}