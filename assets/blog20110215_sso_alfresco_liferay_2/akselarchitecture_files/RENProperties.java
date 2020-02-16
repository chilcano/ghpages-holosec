package no.ren.util;

import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

public class RENProperties {
	  private static String myPropertyFile = "REN"; //Navn på property-fil som det skal hentes data fra

	  private static Properties myProps = new Properties();
	  private static boolean propsLoaded = false;

	  //private static Logger log = Logger.getLogger(RENProperties.class);    

	  /**
	  * Checks to see if the properties file is loaded
	  * 
	  */
	  private static void checkProps()
	  {
	    if(!propsLoaded)
	    {
	      initProps();
	      propsLoaded = true;
	    }
	  }


	  /**
	  * Gets the property from the property-file
	  *
	  * @param  paramName the string containing the name of the property stored in the property file
	  * @return           the string containing the property stored in the property file
	  */
	  public static final synchronized String getProperty(String paramName)
	  {
	     checkProps();
	     return myProps.getProperty(paramName);
	  }


	  /**
	  * Initiates the properties
	  * 
	  */
	  private static final void initProps()
	  {
	    Enumeration bundleKeys = null;
	  
	    if(propsLoaded)
	        return;

	    //Forsøker å hente properties fra classpath
	    try
	    {
	       ResourceBundle labels = ResourceBundle.getBundle(myPropertyFile);
	       String key;
	       String value;
	       for(bundleKeys = labels.getKeys(); bundleKeys.hasMoreElements(); myProps.put(key, value))
	       {
	           key = (String)bundleKeys.nextElement();
	           value = labels.getString(key);
	       }
	    }
	    catch(Exception _ex)
	    {
	    	System.out.println("Property-filen " + myPropertyFile + " finnes ikke!!");
	    }

	    System.out.println("Data er hentet ut av følgende propertyfil: " + myPropertyFile);
	   }

}