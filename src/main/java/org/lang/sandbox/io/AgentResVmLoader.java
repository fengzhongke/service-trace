package org.lang.sandbox.io;

import java.io.InputStream;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

public class AgentResVmLoader extends ResourceLoader {

  @Override
  public void init(ExtendedProperties extendedProperties) {
  }

  @Override
  public InputStream getResourceStream(String s) throws ResourceNotFoundException {
    return AgentResourceLoader.getResourceAsStream(s);
  }

  @Override
  public boolean isSourceModified(Resource resource) {
    return false;
  }

  @Override
  public long getLastModified(Resource resource) {
    return 0;
  }
}
