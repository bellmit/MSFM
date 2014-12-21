package com.cboe.presentation.resources;

import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Loads resource bundles for the application.
 * @author Nick DePasquale
 */
public class Resources
{
    public static final String IMAGES = "images/";
    public static final String REPORTS = "reports/";
    public static final String PERMISSIONS = "permissions/";
    public static final String XSL = "xsl/";
    public static final char SEPARATOR = '/';
    final static String Category = Resources.class.getName();

    /**
     * Resources constructor comment.
     */
    public Resources()
    {
        super();
    }

    /**
     * Tries to load the image resource bundle specified.
     * @param name of resource
     * @return resource loaded into an ImageIcon
     */
    public static ImageIcon getImageIcon(String name)
    {
        ImageIcon anImageIcon = null;

        try
        {
            URL imgURL = getImageResource(name);
            if (imgURL == null)
            {
                GUILoggerHome.find().alarm(Category + ".getImageIcon()", "Loading image failed in getImageResource() for: " + name);
            }
            else
            {
                Toolkit tk = Toolkit.getDefaultToolkit();
                Image img = tk.getImage(imgURL);

                anImageIcon = new ImageIcon(img);

                if (anImageIcon.getImageLoadStatus() == MediaTracker.ERRORED)
                {
                    anImageIcon = null;
                    GUILoggerHome.find().alarm(Category + ".getImageIcon()", "Loading image failed in Resources.getImage() named: " + imgURL);
                }
            }
        }
        catch (Exception e)
        {
            DefaultExceptionHandlerHome.find().process(e, "Failed getImageIcon() for name : " + name);
        }

        return anImageIcon;
    }

    /**
     * Gets the image from the icon path
     *
     * @param name
     *            name of icon in icon directory
     * @return The image for the icon name if one exists, else null
     */
    public static Image getImage(String name)
    {
        BufferedImage image = null;
        try
        {
            image = ImageIO.read(getImageResource(name));
        }
        catch (IOException e)
        {
            DefaultExceptionHandlerHome.find().process(e, "Failed ImageIO.read() for name : " + name);
        }
        return image;
    }


    /**
     * Tries to resolve the location of a resource.
     * @param name of resource
     * @return resource URL with location resolved.
     */
    public static URL getResource(String name)
    {
        URL resourceURL = null;

        try
        {
            resourceURL = Resources.class.getResource(name);

            if (resourceURL == null)
            {
                GUILoggerHome.find().alarm("Resources.getResource() - Could not find resource named: " + name);
            }
        }
        catch (Exception e)
        {
            resourceURL = null;
            DefaultExceptionHandlerHome.find().process(e, "Failed getResource() for name : " + name);
        }

        return resourceURL;
    }

    /**
     * Helper method to get an URL for an image resource.
     * @param name of image resource
     * @return resource URL with location resolved.
     * @see <code>getResource()</code>
     */
    public static URL getImageResource(String name)
    {
        String imageName = name;

        if (name.charAt(0) != SEPARATOR)
        {
            imageName = IMAGES + name;
        }

        return getResource(imageName);
    }

    /**
     * Helper method to get an URL for a report resource.
     * @param name of report resource
     * @return resource URL with location resolved.
     * @see <code>getResource()</code>
     */
    public static URL getReportResource(String name)
    {
        String reportName = name;

        if (name.charAt(0) != SEPARATOR)
        {
            reportName = REPORTS + name;
        }

        return getResource(reportName);
    }

    /**
     * Helper method to get an URL for a permission resource.
     * @param name of permission resource
     * @return resource URL with location resolved.
     * @see <code>getResource()</code>
     */
    public static URL getPermissionResource(String name)
    {
        String permissionName = name;

        if (name.charAt(0) != SEPARATOR)
        {
            permissionName = PERMISSIONS + name;
        }

        return getResource(permissionName);
    }

    /**
     * Helper method to get an URL for a Xsl resource.
     * @param name of Xsl resource
     * @return resource URL with location resolved.
     * @see <code>getResource()</code>
     */
    public static URL getXslResource(String name)
    {
        String xslName = name;
        if (name.charAt(0) != SEPARATOR)
        {
            xslName = XSL + name;
        }

        return getResource(xslName );
    }
}
