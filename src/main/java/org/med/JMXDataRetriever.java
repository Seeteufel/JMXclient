package org.med;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import javax.management.remote.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JMXDataRetriever {

    private static final Logger logger = LoggerFactory.getLogger(JMXDataRetriever.class);

    private JMXServiceURL serviceUrl;
    private Map<ObjectName, String> allMBeans;


    public JMXDataRetriever(String host, int port) throws IOException {

        String url = "service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi";
        this.serviceUrl = new JMXServiceURL(url);
        this.allMBeans = prepareAllMBeans();
    }

    private Map<ObjectName, String> prepareAllMBeans() throws IOException {

        Map<ObjectName, String> beans = new HashMap<>();
        StringBuilder sb = new StringBuilder();

        JMXConnector jmxConnector = JMXConnectorFactory.connect(serviceUrl, null);
        try {
            MBeanServerConnection MBeanConnection = jmxConnector.getMBeanServerConnection();
            Set<ObjectName> beanSet = MBeanConnection.queryNames(null, null);

            for (ObjectName name : beanSet) {
                MBeanInfo mbi = MBeanConnection.getMBeanInfo(name);
                MBeanAttributeInfo[] mbais = mbi.getAttributes();
                for (MBeanAttributeInfo mbai : mbais) {
                    String attr = mbai.getName();
                    try {
                        Object value = MBeanConnection.getAttribute(name, attr);
                        sb.append(attr).append("=").append(value).append("\n");
                    }
                    catch (Exception e) {
                        logger.error(e.toString());
                    }
                }
                beans.put(name, sb.toString());
                //cleaning sb
                sb.setLength(0);
            }
        } catch (ReflectionException | InstanceNotFoundException | IntrospectionException e) {
            logger.error(e.toString());
        } finally {
            jmxConnector.close();
            return beans;
        }
    }

    public void describeSpecificMBean(ObjectName name) {

        logger.debug(allMBeans.get(name));
    }
}
