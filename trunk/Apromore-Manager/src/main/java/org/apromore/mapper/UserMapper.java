package org.apromore.mapper;

import org.apromore.dao.model.Membership;
import org.apromore.dao.model.Permission;
import org.apromore.dao.model.Role;
import org.apromore.dao.model.User;
import org.apromore.model.PermissionType;
import org.apromore.model.RoleType;
import org.apromore.model.UserType;
import org.apromore.model.UsernamesType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Mapper helper class to convert from the DAO Model to the Webservice Model.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class UserMapper {

    /**
     * Convert the List of Users to a UserNamesType Webservice object.
     * @param users the list of Users
     * @return the UsernameType object
     */
    public static UsernamesType convertUsernameTypes(List<User> users) {
        UsernamesType userNames = new UsernamesType();
        for (User usr : users) {
            userNames.getUsername().add(usr.getUsername());
        }
        return userNames;
    }

    /**
     * Convert a user object to a UserType Webservice object.
     * @param user the DB User Model
     * @return the Webservice UserType
     */
    public static UserType convertUserTypes(User user) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        
        UserType userType = new UserType();
        userType.setId(user.getRowGuid());
        userType.setLastName(user.getLastName());
        userType.setFirstName(user.getFirstName());
        userType.setUsername(user.getUsername());
        if (user.getLastActivityDate() != null) {
            userType.setLastActivityDate(formatter.format(user.getLastActivityDate()));
        }

        for (Role role : user.getRoles()) {
            RoleType newRole = new RoleType();
            newRole.setId(role.getRowGuid());
            newRole.setName(role.getName());            
            userType.getRoles().add(newRole);

            for (Permission permission : role.getPermissions()) {
                PermissionType permissionType = new PermissionType();
                permissionType.setId(permission.getRowGuid());
                permissionType.setName(permission.getName());
                
                if (!userType.getPermissions().contains(permissionType)){
                    userType.getPermissions().add(permissionType);
                }
            }
        }
        
        Membership membership = user.getMembership();
        if (membership != null){
            userType.setEmail(membership.getEmail());    
        }
        
        return userType;
    }
    
    

    /**
     * Convert from the WS (UserType) to the DB model (User).
     * @param userType the userType from the WebService
     * @return the User dao model populated.
     */
    public static User convertFromUserType(UserType userType) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        if (userType.getLastActivityDate() != null && !userType.getLastActivityDate().equals("")) {
            try {
                date = formatter.parse(userType.getLastActivityDate());
            } catch (Exception ex){}
        }

        User user = new User();
        user.setLastName(userType.getLastName());
        user.setFirstName(userType.getFirstName());
        user.setUsername(userType.getUsername());
        user.setRowGuid(userType.getId());

        if (date != null){
            user.setLastActivityDate(date);
        }
        
        Membership membership = new Membership();
        membership.setEmail(userType.getEmail());
        
        for (RoleType roleType : userType.getRoles()){
            Role role = new Role();
            role.setRowGuid(roleType.getId());
            role.setName(roleType.getName());
            user.getRoles().add(role);
        }

        return user;
    }

}
