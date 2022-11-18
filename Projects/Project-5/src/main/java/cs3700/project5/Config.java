package cs3700.project5;

/**
 * Configuration options for the application.
 */
public interface Config {
    String DEFAULT_HOST = "proj5.3700.network";
    int DEFAULT_PORT = 443;

    int FLAG_COUNT = 5;

    int HTTP_OK = 200;
    int HTTP_FOUND = 302;
    int HTTP_FORBIDDEN = 403;
    int HTTP_NOT_FOUND = 404;
    int HTTP_SERVICE_UNAVAILABLE = 503;

    String WEBSITE_LOGIN_PAGE = "/accounts/login/?next=/fakebook/";
}
