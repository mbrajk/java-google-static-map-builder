/* Path.java
 * 
 * @description package private class definition for paths in Google Static 
 * Maps API URL builder. 
 * 
 * @version N/A
 *
 * @author Mike B
 */
package google.staticmaps.builder;

import java.awt.Color;
import org.apache.commons.lang3.StringUtils;

class Path 
{
    private static final String pipe = "%7C";
    private static final String comma = ",";
    private static final String weightPrefix = "weight:";
    private static final String colorPrefix = "color:";
    
    private String pathString;
    private double[][] pathArray;
    private Integer weight = null;
    private Color color = null;
    
    Path(double lata, double longta, double latb, double longtb)
    {
        pathArray = new double[2][2];
        pathArray[0][0] = lata;
        pathArray[0][1] = longta;
        pathArray[1][0] = latb;
        pathArray[1][1] = longtb;
    }
    
    Path(double[][] path)
    {
        pathArray = path;
    }
    
    Path(double[][] path, Integer weight, Color color)
    {
        pathArray = path;
        this.weight = weight;
        this.color = color;
    }
    
    @Override
    public String toString()
    {
        pathString = "";
        
        if(weight != null)
        {
            pathString = pathString + weightPrefix + Integer.toString(weight) + pipe;
                if(color != null)
                    pathString = pathString + colorPrefix + getHexColor24(color) + pipe;
        }
        else if(color != null)
            pathString = pathString + colorPrefix + getHexColor24(color) + pipe;
        
        String[] builder = new String[pathArray.length];
        
        int i=0;
        for(double[] coords : pathArray)
        {
            builder[i++] = Double.toString(coords[0]) + comma + Double.toString(coords[1]);
        }
        
        pathString += StringUtils.join(builder, pipe);
        
        return pathString;         
    }
        
    private String getHexColor24(Color color)
    {
        String hex = Integer.toHexString(color.getRGB());
        return "0x" + hex.substring(2,hex.length());
    }
    
    //also gets alpha value, does re-arranging since google takes aplha as last hex digits
    //this method is currently not used
    private String getHexColor32(Color color)
    {
        String hex = Integer.toHexString(color.getRGB());
        return "0x" + hex.substring(2,hex.length()) + hex.substring(0, 2);
    }
}