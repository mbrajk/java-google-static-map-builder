package google.staticmaps.builder;

/**
 * Class containing the public enumerators for the URL builder
 */
public class PublicEnums 
{
    /**
     * The available sizes for Markers placed on the map
     */
    public enum MarkerSize
    {
        /** Smallest Marker Size */
        TINY,
        /** Mid-way between TINY and SMALL Marker Size */
        MID,
        /** Small Marker Size */
        SMALL,
        /** Largest (default) Marker Size */
        NORMAL;
        
        /** Returns the enumerator value as a lowercase String*/
        public String toLowercaseString()
        {
            return this.toString().toLowerCase();
        }
    };

    /**
     * The types of map layouts available
     */
    public enum MapType
    {
        /** Default Google Maps Road Map View*/
        ROADMAP,
        /** Satellite view with no road information*/
        SATELLITE, 
        /** Shows features such as elevation of terrain*/
        TERRAIN,
        /** Satellite view + road map*/
        HYBRID;
        
        /** Returns the enumerator value as a lowercase String*/
        public String toLowercaseString()
        {
            return this.toString().toLowerCase();
        }
    };
}
