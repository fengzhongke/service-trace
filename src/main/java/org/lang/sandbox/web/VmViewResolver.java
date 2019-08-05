package org.lang.sandbox.web;

import java.io.Writer;
import java.util.Map;
import java.util.Properties;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.lang.sandbox.io.AgentResVmLoader;

public class VmViewResolver {
  private static VelocityEngine ve = new VelocityEngine();
  static{
    try {
      Properties p = new Properties();
      p.setProperty("resource.loader", "agent");
      p.setProperty("agent.resource.loader.class", AgentResVmLoader.class.getName());
      p.setProperty("input.encoding", "UTF-8");
      ve.init(p);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void resolve(String template, Map<String, Object> params, Writer writer) {
    System.out.println("template is : " + template);
    try {
      Template t = ve.getTemplate(template);
      t.merge(new VelocityContext(params), writer);
    } catch (Exception e) {
      //e.printStackTrace();
    }
  }
}
