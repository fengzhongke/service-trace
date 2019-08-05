package org.lang.sandbox.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;


public class AgentResourceLoader {

  private static Map<String, byte[]> resMap = new HashMap<String, byte[]>();

  static {
    String staticName = "static";
    String pageName = "page";
    String staticPath = "/" + staticName;
    String path = AgentResourceLoader.class.getResource(staticPath)
        .getFile();
    path = path.substring(0, path.length() - staticPath.length() - 1);
    JarInputStream jarInput = null;
    try {
      jarInput = new JarInputStream(new URL(path).openStream());
      JarEntry entry = null;
      while ((entry = jarInput.getNextJarEntry()) != null) {
        String name = entry.getName();
        if (name.startsWith(staticName) || name.startsWith(pageName)) {
          ByteArrayOutputStream bytes = new ByteArrayOutputStream();
          int chunk = 0;
          byte[] data = new byte[256];
          while (-1 != (chunk = jarInput.read(data))) {
            bytes.write(data, 0, chunk);
          }
          data = bytes.toByteArray();
          resMap.put(name, data);
        }
      }
    } catch (Exception e) {
      //e.printStackTrace();
    } finally {
      if (jarInput != null) {
        try {
          jarInput.close();
        } catch (IOException e1) {
          //e1.printStackTrace();
        }
      }
    }
  }

  public static InputStream getResourceAsStream(String name) {
    byte[] data = resMap.get(name);
    InputStream in = null;
    if(data != null){
      in = new ByteArrayInputStream(data, 0, data.length);
    }
    return in;
  }
}
