package be.florens.expandability;

import be.florens.expandability.api.EventResult;
import java.util.function.Supplier;

public class Util {
   public static boolean processEventResult(EventResult result, boolean defaultValue) {
      return processEventResult(result, true, false, defaultValue);
   }

   public static boolean processEventResult(EventResult result, Supplier<Boolean> defaultSupplier) {
      return processEventResult(result, true, false, defaultSupplier);
   }

   public static <T> T processEventResult(EventResult result, T success, T fail, Supplier<T> defaultSupplier) {
      return processEventResult(result, success, fail, defaultSupplier.get());
   }

   public static <T> T processEventResult(EventResult result, T success, T fail, T defaultValue) {
      return (T)(switch (result) {
         case SUCCESS -> (Object)success;
         case FAIL -> (Object)fail;
         case PASS -> (Object)defaultValue;
      });
   }
}
