package me.zayz.socialplus.config;

/**
 * Created by zayz on 11/7/17.
 * <p>
 * Template config file for own usage.
 */
public enum TemplateConfig {
    INSTAGRAM_API_CLIENT_ID("client_id", "Your_Client_Id"),
    INSTAGRAM_API_CLIENT_SECRET("client_secret", "Your_Client_Secret"),
    INSTAGRAM_API_REDIRECT_URI("redirect_uri", "Your_Redirect_Uri");

    public String key;
    public String value;

    TemplateConfig(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
