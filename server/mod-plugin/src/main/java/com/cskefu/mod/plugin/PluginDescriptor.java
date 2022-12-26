package com.cskefu.mod.plugin;

import java.util.Hashtable;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PluginDescriptor {

  Object implementation;
  String name;
  Hashtable<String, Object> params;

}
