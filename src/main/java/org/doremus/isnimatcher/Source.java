package org.doremus.isnimatcher;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "source")
@XmlAccessorType(XmlAccessType.FIELD)
public class Source {
  private static final String MUSICBRAINZ_BASE = "https://musicbrainz.org/artist/";
  private static final String BNF_BASE = "http://catalogue.bnf.fr/ark:/12148/cb";
  private static final String VIAF_BASE = "https://viaf.org/viaf/";

  @XmlElement(name = "codeOfSource")
  public String codeOfSource;

  @XmlElement(name = "sourceIdentifier")
  public String sourceIdentifier;

  public String asMusicBrainzURI() {
    return MUSICBRAINZ_BASE + sourceIdentifier;
  }

  public String asBNFUri() {
    return BNF_BASE + sourceIdentifier + "b";
  }

  public String asViafURI() {
    return makeViafUri(sourceIdentifier);
  }

  public static String makeViafUri(String viaf) {
    return VIAF_BASE + viaf;

  }
}
