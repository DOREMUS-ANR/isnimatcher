package org.doremus.isnimatcher;

import java.util.regex.Pattern;

public class DateUtils {
  private final static String BC_REGEX = "(?i)(v\\.Chr|(B|av?)\\.? ?(de )?(J\\.?[ -]?)?C)\\.?$";
  private final static String AD_REGEX = "(?i)(n\\.Chr|A\\.? ?D|(d|apr?)\\.? ?(de )?((J\\.?[ -]?)?C|NSJC))\\.?$";
  private final static Pattern BC_PATTERN = Pattern.compile(BC_REGEX);

  static String cleanDate(String date) {
    assert date != null;

    date = date.trim();
    if ("...".equals(date)) date = "....";
    else {
      if(isBC(date))
        date = "-" + date.replaceAll(BC_REGEX, "").trim();
      else date = date.replaceAll(AD_REGEX, "").trim();
    }

    return date;
  }

  static boolean isBC(String date) {
    return BC_PATTERN.matcher(date).find();
  }
}
