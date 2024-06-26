/* Generated by camel build tools - do NOT edit this file! */
package org.apache.camel.component.snakeyaml;

import javax.annotation.processing.Generated;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.spi.GeneratedPropertyConfigurer;
import org.apache.camel.support.component.PropertyConfigurerSupport;

/**
 * Generated by camel build tools - do NOT edit this file!
 */
@Generated("org.apache.camel.maven.packaging.PackageDataFormatMojo")
@SuppressWarnings("unchecked")
public class SnakeYAMLDataFormatConfigurer extends PropertyConfigurerSupport implements GeneratedPropertyConfigurer {

    @Override
    public boolean configure(CamelContext camelContext, Object target, String name, Object value, boolean ignoreCase) {
        SnakeYAMLDataFormat dataformat = (SnakeYAMLDataFormat) target;
        switch (ignoreCase ? name.toLowerCase() : name) {
        case "constructor": dataformat.setConstructor(property(camelContext, java.util.function.Function.class, value)); return true;
        case "representer": dataformat.setRepresenter(property(camelContext, java.util.function.Function.class, value)); return true;
        case "dumperoptions":
        case "dumperOptions": dataformat.setDumperOptions(property(camelContext, java.util.function.Function.class, value)); return true;
        case "resolver": dataformat.setResolver(property(camelContext, java.util.function.Function.class, value)); return true;
        case "unmarshaltype":
        case "unmarshalType": dataformat.setUnmarshalType(property(camelContext, java.lang.Class.class, value)); return true;
        case "useapplicationcontextclassloader":
        case "useApplicationContextClassLoader": dataformat.setUseApplicationContextClassLoader(property(camelContext, boolean.class, value)); return true;
        case "prettyflow":
        case "prettyFlow": dataformat.setPrettyFlow(property(camelContext, boolean.class, value)); return true;
        case "allowanytype":
        case "allowAnyType": dataformat.setAllowAnyType(property(camelContext, boolean.class, value)); return true;
        case "maxaliasesforcollections":
        case "maxAliasesForCollections": dataformat.setMaxAliasesForCollections(property(camelContext, int.class, value)); return true;
        case "allowrecursivekeys":
        case "allowRecursiveKeys": dataformat.setAllowRecursiveKeys(property(camelContext, boolean.class, value)); return true;
        default: return false;
        }
    }

}

