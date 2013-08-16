package org.apromore.security;

import javax.servlet.http.HttpServletRequest;

import org.apromore.model.UserType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * Used to store the extra info needed to allow Apromore to work correctly.
 *
 * @author <a href="mailto:cam.james@gmail.com">Camron James</a>
 */
public class ApromoreWebAuthenticationDetails extends WebAuthenticationDetails {

    private final UserType userDetails;

    /**
     * Records the remote address and will also set the session Id if a session
     * already exists (it won't create one).
     *
     * @param request that the authentication request was received from
     */
    public ApromoreWebAuthenticationDetails(final HttpServletRequest request, final UsernamePasswordAuthenticationToken authRequest) {
        super(request);
        this.userDetails = (UserType) authRequest.getDetails();
    }

    /**
     * Used to get the User type used by Apromore.
     * @return the UserType details
     */
    public UserType getUserDetails() {
        return userDetails;
    }

}
