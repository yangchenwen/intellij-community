/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.openapi.application;

import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.PlatformUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * @author nik
 */
public class ApplicationNamesInfo {
  private static final String COMPONENT_NAME = "ApplicationInfo";

  public static String getComponentName() {
    String prefix = System.getProperty(PlatformUtils.PLATFORM_PREFIX_KEY);
    return prefix != null ? prefix + COMPONENT_NAME : COMPONENT_NAME;
  }

  private final String myProductName;
  private final String myFullProductName;
  private final String myScriptName;
  private final String myDefaultLauncherName;

  private static class ApplicationNamesInfoHolder {
    private static final ApplicationNamesInfo ourInstance = new ApplicationNamesInfo();
    private ApplicationNamesInfoHolder() { }
  }

  @NotNull
  public static ApplicationNamesInfo getInstance() {
    return ApplicationNamesInfoHolder.ourInstance;
  }

  private ApplicationNamesInfo() {
    String resource = "/idea/" + getComponentName() + ".xml";
    try {
      Document doc = JDOMUtil.loadDocument(ApplicationNamesInfo.class, resource);
      Element rootElement = doc.getRootElement();
      Element names = rootElement.getChild("names", rootElement.getNamespace());
      myProductName = names.getAttributeValue("product");
      myFullProductName = names.getAttributeValue("fullname");
      myScriptName = names.getAttributeValue("script");
      myDefaultLauncherName = names.getAttributeValue("default-launcher-name", myScriptName);
    }
    catch (Exception e) {
      throw new RuntimeException("Cannot load resource: " + resource, e);
    }
  }

  /**
   * For multi-word product names, returns a short variant (e.g. {@code "IDEA"} for "IntelliJ IDEA"),
   * otherwise returns the same value as {@link #getFullProductName()}.
   * <strong>Consider using {@link #getFullProductName()} instead.</strong>
   */
  public String getProductName() {
    return myProductName;
  }

  /**
   * Returns a product name without a vendor prefix ({@code "IntelliJ IDEA"} for IntelliJ IDEA, {@code "WebStorm"} for WebStorm, etc).
   */
  public String getFullProductName() {
    return myFullProductName;
  }

  /**
   * Returns a sentence-cased version of {@link #getProductName()} ({@code "Idea"} for IntelliJ IDEA, {@code "Webstorm"} for WebStorm, etc).
   * <strong>Kept for compatibility; use {@link #getFullProductName()} instead.</strong>
   */
  public String getLowercaseProductName() {
    return StringUtil.capitalize(myProductName.toLowerCase(Locale.US));
  }

  /**
   * Returns the base name of the launcher file (*.exe, *.bat, *.sh) located in the product home's 'bin/' directory
   * ({@code "idea"} for IntelliJ IDEA, {@code "webstorm"} for WebStorm etc).
   */
  public String getScriptName() {
    return myScriptName;
  }

  /**
   * Returns the default name of the command-line launcher to be suggested in 'Create Launcher Script' dialog.
   */
  public String getDefaultLauncherName() {
    return myDefaultLauncherName;
  }
}