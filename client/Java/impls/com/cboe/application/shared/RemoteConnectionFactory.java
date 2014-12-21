package com.cboe.application.shared;

/**
 * This type was created in VisualAge.
 */
public class RemoteConnectionFactory {
    static RemoteConnection connection = null;
/**
 * SBTConnectionFactory constructor comment.
 */
public RemoteConnectionFactory() {
    super();
}
/**
 * This method was created in VisualAge.
 */
public static RemoteConnection create(String[] args) {
//  connection = new RemoteConnectionJava2(args);
    connection = new RemoteConnectionCBOEOrb(args);
    return connection;
}
/**
 *
 * @author Jeff Illian
 *
 */
public static RemoteConnection find() {
    return connection;
}
}
