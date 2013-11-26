package org.apromore.portal.servlet;

import org.apromore.portal.common.WebAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * The Base for all Servlet Request Handlers.
 *
 * @author Cameron James
 * @Since 1.0
 */
public abstract class BaseServletRequestHandler implements HttpRequestHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseServletRequestHandler.class);

    protected static final String FIRSTNAME = "firstname";
    protected static final String SURNAME = "surname";
    protected static final String EMAIL = "email";
    protected static final String USERNAME = "username";
    protected static final String PASSWORD = "password";
    protected static final String SECURITY_QUESTION = "securityQuestion";
    protected static final String SECURITY_ANSWER = "securityAnswer";
    protected static final String CONFIRM_PASSWORD = "confirmPassword";

    protected static final String LOGIN_PAGE = "/login.zul";
    protected static final String ERROR_EXTENSION = "?error=3";
    protected static final String MESSAGE_EXTENSION = "?success=1";

    private boolean contextRelative;


    /**
     * Removes temporary authentication-related data which may have been stored in the session
     * during the authentication process.
     */
    protected void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return;
        }

        session.removeAttribute(WebAttributes.REGISTRATION_EXCEPTION);
    }


    /**
     * Redirects the response to the supplied URL.
     * <p>
     * If <tt>contextRelative</tt> is set, the redirect value will be the value after the request context path. Note
     * that this will result in the loss of protocol information (HTTP or HTTPS), so will cause problems if a
     * redirect is being performed to change to HTTPS, for example.
     */
    protected void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        String redirectUrl = calculateRedirectUrl(request.getContextPath(), url);
        redirectUrl = response.encodeRedirectURL(redirectUrl);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Redirecting to '" + redirectUrl + "'");
        }

        response.sendRedirect(redirectUrl);
    }

    private String calculateRedirectUrl(String contextPath, String url) {
        if (!UrlUtils.isAbsoluteUrl(url)) {
            if (contextRelative) {
                return url;
            } else {
                return contextPath + url;
            }
        }

        // Full URL, including http(s)://
        if (!contextRelative) {
            return url;
        }

        // Calculate the relative URL from the fully qualified URL, minus the scheme and base context.
        url = url.substring(url.indexOf("://") + 3); // strip off scheme
        url = url.substring(url.indexOf(contextPath) + contextPath.length());

        if (url.length() > 1 && url.charAt(0) == '/') {
            url = url.substring(1);
        }

        return url;
    }

}
