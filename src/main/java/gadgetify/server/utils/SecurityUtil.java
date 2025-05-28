package gadgetify.server.utils;

import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Integer getCurrentUserId() {
        var userIdString = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userIdString == null || userIdString.equals("anonymousUser")) {
            throw new AuthorizationDeniedException("Unauthorized or Forbidden");
        }
        return Integer.parseInt(userIdString);
    }
}
