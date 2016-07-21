/* Marker.java
 * 
 * @description package private class definition for markers in Google Static 
 * Maps API URL builder
 * 
 * @version N/A
 *
 * @author Mike B
 */
package google.staticmaps.builder;

import google.staticmaps.builder.PublicEnums.*;
import java.awt.Color;

class Marker 
{    
    private static final String pipe = "%7C";
    private static final String comma = ",";
    private static final String sizePrefix = "size:";
    private static final String colorPrefix = "color:";
    private static final String labelPrefix = "label:";
    
    private String markerString;
    
    private double lat;
    private double longt;
    private MarkerSize size;
    private Color color = null;
    private Character label = null;
    
    Marker(double lat, double longt)
    {
        this.lat = lat;
        this.longt = longt;                
    }
    
    Marker(double lat, double longt, Color color, MarkerSize size, char label)
    {
        this.lat = lat;
        this.longt = longt;
        this.color = color;
        this.label = label;
        this.size = size;
    }
    
    Marker(double lat, double longt, Color color, MarkerSize size)
    {
        this.lat = lat;
        this.longt = longt;
        this.color = color;
        this.size = size;
    }
    
    Marker(double lat, double longt, Color color, char label)
    {
        this.lat = lat;
        this.longt = longt;
        this.color = color;
        this.label = label;
    }
    
    Marker(double lat, double longt, Color color)
    {
        this.lat = lat;
        this.longt = longt;
        this.color = color;
    }
    
    Marker(double lat, double longt, char label)
    {
        this.lat = lat;
        this.longt = longt;
        this.label = label;
    }
    
    @Override
    public String toString()
    {
        markerString = "";
        
        if(color != null)
        {
            markerString = markerString + colorPrefix + getHexColor24(color) + pipe;
        
            if(size != null)
            {
                switch(size)
                {
                    case TINY: markerString = markerString + sizePrefix + size.toLowercaseString() + pipe ; 
                        break;
                    case MID: markerString = markerString + sizePrefix + size.toLowercaseString() + pipe ; 
                        break;
                    case SMALL: markerString = markerString + sizePrefix + size.toLowercaseString() + pipe ; 
                        break;
                    case NORMAL: break;
                    default:break;
                }                      
            }
            if(label != null)
                {markerString = markerString + labelPrefix + Character.toString(label) + pipe;}
        }
        
        else if(label != null)
            {markerString = markerString + labelPrefix + Character.toString(label) + pipe;}
        
        markerString = markerString + Double.toString(lat) + comma + Double.toString(longt);
       
        return markerString;        
    }

    //gets 24 bit hex color for string building
    private String getHexColor24(Color color)
    {
        String hex = Integer.toHexString(color.getRGB());
        return "0x" + hex.substring(2,hex.length());
    }
}
