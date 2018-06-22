package org.doremus.isnimatcher;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@XmlRootElement(name = "externalInformation")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExternalInformation {
  private static final String WIKIPEDIA_REGEX = "https?:\\/\\/([a-z]{1,3})\\.wikipedia\\.org\\/wiki\\/(.+)";
  private static final String VIAF_REGEX = "https?:\\/\\/viaf\\.org\\/viaf\\/(.+)";
  private static final String DISCOGS_REGEX = "https?:\\/\\/www\\.discogs\\.com\\/artist\\/(.+)";
  private static final String WIKIDATA_REGEX = "https?:\\/\\/www\\.wikidata\\.org\\/wiki\\/(.+)";
  private static final String MUZIEKWEB_REGEX = "https?://www.muziekweb.nl/Link/M00000238711";

  private static final Pattern WIKIPEDIA_PATTERN = Pattern.compile(WIKIPEDIA_REGEX);

  @XmlElement(name = "information")
  public String information;

  @XmlElement(name = "URI")
  public String URI;


  public String getLang() {
    // e.g. https://es.wikipedia.org/wiki/Wolfgang_Amadeus_Mozart => es
    Matcher m = WIKIPEDIA_PATTERN.matcher(this.URI);
    if (!m.find()) return null;
    else return m.group(1);
  }

  public String toDBpedia() {
    // e.g. https://es.wikipedia.org/wiki/Wolfgang_Amadeus_Mozart => es
    Matcher m = WIKIPEDIA_PATTERN.matcher(this.URI);
    if (!m.find()) return null;

    String lang = m.group(1);
    if ("en".equals(lang)) lang = "";
    else lang += ".";

    return "http://" + lang + "dbpedia.org/resource/" + m.group(2);
  }

  public boolean isType(String type) {
    return this.getType().equalsIgnoreCase(type);
  }

  public String getType() {
    if (URI.matches(WIKIPEDIA_REGEX))
      return "wikipedia";
    if (URI.matches(VIAF_REGEX))
      return "viaf";
    if (URI.matches(DISCOGS_REGEX))
      return "discogs";
    if (URI.matches(WIKIDATA_REGEX))
      return "wikidata";
    if (URI.matches(MUZIEKWEB_REGEX))
      return "muziekweb";
    return "unknown";
  }
}
