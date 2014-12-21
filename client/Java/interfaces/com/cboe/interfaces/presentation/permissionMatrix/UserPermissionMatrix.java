/**
 * Created by IntelliJ IDEA.
 * User: Brazhni
 * Date: Nov 15, 2002
 * Time: 1:25:33 PM
 * To change this template use Options | File Templates.
 */
package com.cboe.interfaces.presentation.permissionMatrix;

public interface UserPermissionMatrix extends PermissionMatrix
{
    boolean isAllowed(Permission permission);
}
