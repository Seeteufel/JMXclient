package org.med;

import javax.management.*;
import java.io.IOException;


public class JMXclient {

    public static void main(String[] args) throws IOException, MalformedObjectNameException {

        JMXDataRetriever jmx = new JMXDataRetriever("0.0.0.0", 1234);
        jmx.describeSpecificMBean(new ObjectName("org.med:type=basic,name=Adam"));
    }
}
