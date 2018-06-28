/** URLBuilder.java
 * 
 * @description Library to generate a valid Google Static Maps API URL. 
 * This URL can be used to generate a Google Static Map.
 * 
 * Supports most of the basic features of the Google Static Maps API, such as 
 * color, size, and labels of markers as well as width and color of paths 
 * (does not currently support path opacity).
 * 
 * If intend to create an optimized version of the Google Static Maps API URL
 * (for example in large complex maps) it is not recommended to use this library
 * in its current form. This library uses redundancy in creating the URL, mostly
 * when creating multiple markers. 
 * 
 * @version 0.9.20110828
 *
 * @author Mike B
 */
package google.staticmaps.builder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.*;
import java.util.LinkedList;
import java.awt.*;
import google.staticmaps.builder.PublicEnums.*;

public class URLBuilder 
{
    //constant values which should not require changes unless Google's API is modified.
    private static final int MAX_X_SIZE = 640; //max size of map x axis in pixels
    private static final int MAX_Y_SIZE = 640; //max size of map y axis in pixels
    private static final int MAX_URL_LENGTH = 2048; //max URL length in characters 
    private static final int MAX_LAT_LONG_DECIMAL = 2; /*maximum number of decimal places 
                                                *allowed in lat/long values */
            
    //the following are required prefix strings for parameters in the URL/API
    private static final String pipe = "%7C"; //expanded representation of | character
    private static final String mapSizePrefix = "&size="; //prefix for size paramter
    private static final String markerPrefix = "&markers="; //prefix for marker locations
    private static final String pathPrefix = "&path="; //prefix for paths
    private static final String mapTypePrefix = "&maptype="; //prefix for map type
    private static final String urlPrefix = 
            "https://maps.googleapis.com/maps/api/staticmap?&sensor=false";
    
    //set defaults, can be changed via appropriate method calls
    private String mapSize = MAX_X_SIZE + "x" + MAX_Y_SIZE;
    private MapType mapType = MapType.HYBRID;
    
    //store markers and paths in linked lists
    private LinkedList<Marker> markerList = new LinkedList<Marker>();
    private LinkedList<Path> pathList = new LinkedList<Path>();
     
     
    /**
     * Default Constructor - Initializes  an instance of URL builder
     */
    public URLBuilder(){}
   
    
    /**
     * Sets the size of the map to be displayed/generated. Google currently has
     * a maximum size of 640x640pixels that a map can be generated. This limit 
     * is defined as static final int variables MAX_X_SIZE and MAX_Y_SIZE within
     * the library's member variables. If this method is not used the default
     * (largest) size will be assumed
     * 
     * @param x Size in pixels for the x axis of the generated map, must be less
     *          than MAX_X_SIZE
     * 
     * @param y Size in pixels for the y axis of the generated map, must be less
     *          than MAX_Y_SIZE
     * 
     * @throws MapException
     */
    public void setSize(int x, int y) throws MapException
    {
        if(x < 1 || y < 1)
        {
            throw new MapException("Negative/Zero map size invalid");
        }
        else if (x > MAX_X_SIZE || y > MAX_Y_SIZE) 
        {
            throw new MapException("Map sizes must be less than "
                    + Integer.toString(MAX_X_SIZE) + "x"
                    + Integer.toString(MAX_Y_SIZE) + " pixels");
        } 
        else 
        {
            mapSize = x + "x" + y;
        }
    }
    
    
    /**
     * Sets the style of the map to be displayed/generated. There are currently 
     * four types of maps. The types as well as the value passed for them are
     * listed in the parameter documentation. If this method is not called the 
     * default will be used.
     * 
     * @param mapType The type of map to be displayed. Uses the MapType enum
     *                values from the following list: 
     *                ROADMAP - Road Map
     *                SATELLITE - Satellite
     *                TERRAIN - Terrain
     *                HYBRID - Hybrid (Satellite + Road Map) (default)
     */
    public void setMapType(MapType mapType)
    {        
        this.mapType = mapType;
    }
    
    
    /**
     * Add a path on the map from Location A to Location B. Lat/Long values 
     * should be specified in decimal degree format. MAX_LAT_LONG_DECIMAL
     * specifies the maximum allowed decimal places for coordinates, any extra
     * decimal places will be ignored. To add additional stops along the path or
     * to specify additional modifiers such as color and opacity use the 
     * overloaded addPath method which takes additional parameters to build a 
     * path.
     * 
     * @param latA   Latitude of starting path location in decimal degree format
     * 
     * @param longtA Longitude of starting path location in decimal degree format
     * 
     * @param latB   Latitude of ending path location in decimal degree format
     * 
     * @param longtB Longitude of ending path location in decimal degree format
     */
    public void addSimplePath(double latA, double longtA, double latB, double longtB)
    {
        Path p = new Path
                (trimDeci(latA), trimDeci(longtA), trimDeci(latB), trimDeci(longtB));
        
        pathList.add(p);   
    }
    
    
    /**
     * Add a path with a variable number of stops. Lat/Long values 
     * should be specified in decimal degree format. MAX_LAT_LONG_DECIMAL
     * specifies the maximum allowed decimal places for coordinates, any extra
     * decimal places will be ignored. For additional options use the overloaded
     * addPath method which takes additional parameters to build a path.
     * 
     * @param path  An array of arrays of doubles that contain the latitude and 
     *              longitude pairs for each stop on the path. Array is assumed
     *              to be of size N (number of stops) by 2 (to accommodate each 
     *              lat/long pair). Additional values beyond this restriction 
     *              will be ignored.
     */
    public void addPath(double[][] path)
    {        
        //trim decimal places
        for(double[] coords : path)
        {
            coords[0] = trimDeci(coords[0]);
            coords[1] = trimDeci(coords[1]);
        }
        
        Path p = new Path(path);
        pathList.add(p);
    }
    
    
    /**
     * Add a path with a variable number of stops as well as specifying the 
     * width, opacity and color of the path. Lat/Long values should be specified
     * in decimal degree format. MAX_LAT_LONG_DECIMAL specifies the maximum 
     * allowed decimal places for coordinates, any extra decimal places will be 
     * ignored.
     * 
     * @param path  An array of arrays of doubles that contain the latitude and 
     *              longitude pairs for each stop on the path. Array is assumed
     *              to be of size N (number of stops) by 2 (to accommodate each 
     *              lat/long pair). Additional values beyond this restriction 
     *              will be ignored.
     * 
     * @param width An integer value specifying the width (weight) in pixels of 
     *              the path. The default value is 5 pixels.              
     * 
     * @param color Takes a color object. Refer to {@link Color} 
     *              for more information. Alpha values are allowed in paths.
     *              The default color is blue.
     * 
     * @throws MapException
     */
    public void addPath(double[][] path, int width, Color color) throws MapException
    {        
        if(width < 1)
            throw new MapException("Invalid (negative) path width specified");        
        
        //trim decimal places
        for(double[] coords : path)
        {
            coords[0] = trimDeci(coords[0]);
            coords[1] = trimDeci(coords[1]);
        }
        
        Path p = new Path(path, width, color);
        pathList.add(p);
    }
       
    
    /**
     * Add a simple marker to the map. This marker will have the default color,
     * size and no label. Lat/Long values should be specified in decimal 
     * degree format. MAX_LAT_LONG_DECIMAL specifies the maximum allowed decimal
     * places for coordinates, any extra decimal places will be ignored. To add 
     * extra features to the marker use the addMarker method.
     * 
     * @param lat   The latitude coordinates of the marker location.
     * 
     * @param longt The longitude coordinates of the marker location.
     */
    public void addSimpleMarker(double lat, double longt)
    {
        Marker m = new Marker(trimDeci(lat), trimDeci(longt));        
        markerList.add(m);
    }
    
    
     /**
     * Add a detailed marker to the map. Can specify the color and size 
     * Lat/Long values should be specified in decimal degree format. 
     * MAX_LAT_LONG_DECIMAL specifies the maximum allowed decimal places for
     * coordinates, any extra decimal places will be ignored.
     * 
     * @param lat   The latitude coordinates of the marker location.
     * 
     * @param longt The longitude coordinates of the marker location.
     * 
     * @param color Takes a color object. Refer to {@link Color} 
     *              for more information. Alpha values are not allowed for 
     *              markers and will be ignored. The default color is red.
     * 
     * @param size  Takes a value of type MarkerSize from the following list:
     *              TINY - the smallest marker size
     *              MID
     *              SMALL
     *              NORMAL - the default marker size
     */
    public void addMarker
            (double lat, double longt, Color color, MarkerSize size)
    {        
        Marker m = new Marker(trimDeci(lat), trimDeci(longt), color, size);
        markerList.add(m);
    }
    
    
    /**
     * Add a detailed marker to the map. Can specify the color size and 
     * label. Lat/Long values should be specified in decimal degree format. 
     * MAX_LAT_LONG_DECIMAL specifies the maximum allowed decimal places for
     * coordinates, any extra decimal places will be ignored.
     * 
     * @param lat   The latitude coordinates of the marker location.
     * 
     * @param longt The longitude coordinates of the marker location.
     * 
     * @param color Takes a color object. Refer to {@link Color} 
     *              for more information. Alpha values are not allowed for 
     *              markers and will be ignored. The default color is red.
     * 
     * @param size  Takes a value of type MarkerSize from the following list:
     *              TINY - the smallest marker size
     *              MID
     *              SMALL
     *              NORMAL - the default marker size
     * 
     * @param label 
     *              A single character that will be displayed within the 
     *              marker. The only legal characters are 0-9 and A-Z. Letters 
     *              will be converted to uppercase, lowercase not allowed. All 
     *              other characters will be ignored and assumed default of none. 
     *              Label will also be ignored if a size of TINY or MID since
     *              the label will not fit.
     */
    public void addMarker
            (double lat, double longt, Color color, MarkerSize size, char label) throws MapException
    {        
        /* check to see if label is a digit 0-9 or a letter A-Z and convert 
         * lowercase letters to uppercase. */
        //should do this when building string instead and ignore invalid labels 
        //instead of throwing exception
        if(!Character.isDigit(label))
        {
            if(Character.isLetter(label))
            {
                if(Character.isLowerCase(label))
                    {label = Character.toUpperCase(label);}
            }
            else
            {
                throw new MapException("Invalid label, must be a single "
                        + "uppercase letter A-Z or a digit 0-9");
            }
        }
        
        Marker m = new Marker(trimDeci(lat), trimDeci(longt), color, size, label);
        markerList.add(m);
    }
    
    
    /**
     * Generates and returns the URL.
     * 
     * @return String containing a Complete and valid Google Static Maps API URL
     */
    public String getURL() throws MapException
    {
        //the following are for storing dynamically generated sections of final URL
        String url = ""; //will store fully constructed url when completed
        String markers = ""; //to concatenate/store all marker locations
        String paths = ""; //to concatenate/store all path(s) information
        String size; //to concatenate/store size information
        String mapTypeStr = ""; //to store map type information
    
        switch(mapType)
        {
            case ROADMAP:
                 break;
            case SATELLITE:
            case TERRAIN:
            case HYBRID: 
                 mapTypeStr = mapTypePrefix + mapType.toLowercaseString();
                 break;
            default: throw new MapException("Invalid Map Type");
        }
    
        size = mapSizePrefix + mapSize;
       
        if(markerList.isEmpty() && pathList.isEmpty())
             throw new MapException("Minimum requirements not met for URL/map "
                    + "generation. Check that at least one marker or path is set");
        
        if(!markerList.isEmpty())
        {
            for (Marker m : markerList)
                {markers = markers + markerPrefix + m.toString();}
        }
            
        if(!pathList.isEmpty())
        {
            for (Path p : pathList)
                {paths = paths + pathPrefix + p.toString(); }
        }
            
        //build url
        url = urlPrefix + size + mapTypeStr + markers + paths;
            
        if(url.length() > MAX_URL_LENGTH)
            throw new MapException("Generated URL is longer than "
                    + Integer.toString(MAX_URL_LENGTH)
                    + " character maximum set by Google.");
            
        return url;
    }
    
    /**
     * Returns an Image of the generated map. Will check to ensure that the URL 
     * was successfully generated, thus all required parameters of the map must
     * be set before invoking this 
     * method. Has the potential to throw a MalformedURLException
     * although this is not likely since the URL is generated 
     * automatically.
     * 
     * @return Image of the generated map if it was successfully created,
     *         otherwise returns null
     * 
     * @throws MalformedURLException 
     */
    public Image getMap() throws MalformedURLException, MapException
    {  
        URL gmap = new URL(getURL());
           
        Image image = Toolkit.getDefaultToolkit().createImage(gmap); 
        return image;   
    }
    
    //----private methods----    
        
    /*
     * Internal method to trim Lat/Long values
     */
    private double trimDeci(double value)
    {
        BigDecimal trim = new BigDecimal(value);
        BigDecimal trimmedVal = trim.setScale(MAX_LAT_LONG_DECIMAL, RoundingMode.HALF_UP);
        return trimmedVal.doubleValue();
    }
}
