# You can do it, Josh!

This is how I do logging:

package logging;

import java.util.logging.Logger;

public class SimpleLoggingExample{
    public static void main(String[] args){
        Logger logger = Logger.getLogger("myLogger");
        logger.addHandler(new FileHandler("example.log", true));
        var formatter = new SimpleFormatter();
        logger.info("main: " + String.join(", ",  args));
    }
}
