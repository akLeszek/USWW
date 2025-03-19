package adrianles.usww.utils;

import adrianles.usww.domain.entity.dictionary.UserGroup;

public class UserGroupUtils {

    public static boolean isAdmin(UserGroup userGroup) {
        return userGroup != null && "ADMIN".equals(userGroup.getIdn());
    }

    public static boolean isOperator(UserGroup userGroup) {
        return userGroup != null && "OPERATOR".equals(userGroup.getIdn());
    }

    public static boolean isStudent(UserGroup userGroup) {
        return userGroup != null && "STUDENT".equals(userGroup.getIdn());
    }

    public static boolean requiresOrganizationUnit(UserGroup userGroup) {
        return userGroup != null && !isAdmin(userGroup);
    }
}
