package com.ambergarden.orderprocessor.localization;

import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.context.i18n.LocaleContextHolder;

public class Localizer {
   private final static String RESOURCE_BUNDLE = "com.ambergarden.orderprocessor.messages";
   private Locale locale = null;

   private Localizer(Locale locale) { this.locale = locale; }

   public String getLocalizedText(String key) {
      try {
         ResourceBundle bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE,
               locale, this.getClass().getClassLoader());

         if (bundle.keySet().contains(key)) {
            return bundle.getString(key);
         } else {
            return key + "(No localization entry found)";
         }
      } catch (Exception e) {
         return "LOCALIZATION FAILED: " + e.toString();
      }
   }

   public static Localizer getLocalizer() {
      return new Localizer(LocaleContextHolder.getLocale());
   }
}