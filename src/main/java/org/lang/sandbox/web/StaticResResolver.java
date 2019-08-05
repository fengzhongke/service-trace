package org.lang.sandbox.web;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

import org.lang.sandbox.io.AgentResourceLoader;

public class StaticResResolver {

  public static void resolve(String path, Writer writer) throws Exception {
    InputStream in = AgentResourceLoader.getResourceAsStream(path);
    InputStreamReader reader = new InputStreamReader(in);
    char[] buf = new char[1024];
    int len = 0;
    while((len = reader.read(buf)) > 0){
      writer.write(buf, 0, len);
    }

  }
}
