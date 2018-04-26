package create;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

 
//Class used to get the database properties
public class applicationProperties {
    private Properties prop = null;
     
    public applicationProperties(){
        InputStream is = null;
        try {
            this.prop = new Properties();
            is = this.getClass().getResourceAsStream("application.properties");
            prop.load(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String getPropertyValue(String key){
        return this.prop.getProperty(key);
    } 
}
