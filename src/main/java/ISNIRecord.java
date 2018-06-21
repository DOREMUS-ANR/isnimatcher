import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@XmlRootElement(name = "ISNIAssigned")
@XmlAccessorType(XmlAccessType.FIELD)
public class ISNIRecord {
  private final static String START_TAG = "<ISNIAssigned>";
  private final static String END_TAG = "</ISNIAssigned>";

  @XmlElement(name = "isniUnformatted")
  public String id;

  @XmlElement(name = "isniURI")
  public String uri;

  @XmlElement(name = "personalName")
  public List<PersonalName> personalNames;




  public static ISNIRecord fromUri(URL uri) throws JAXBException {
    JAXBContext jaxbContext = JAXBContext.newInstance(ISNIRecord.class);
    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    return (ISNIRecord) jaxbUnmarshaller.unmarshal(uri);
  }


  public static ISNIRecord fromUri(String uri) throws MalformedURLException, JAXBException {
    return fromUri(new URL(uri));
  }

  public static ISNIRecord fromString(String str) throws JAXBException {
    JAXBContext jaxbContext = JAXBContext.newInstance(ISNIRecord.class);
    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

    int start = str.indexOf(START_TAG);
    int end = str.indexOf(END_TAG) + END_TAG.length();

    // no results
    if (start == -1) return null;

    String record = str.substring(start, end);
    // remove useless intermediate tags
    record = record.replaceAll("</?ISNIMetadata>","");
    record = record.replaceAll("</?identity>","");
    record = record.replaceAll("</?personOrFiction>","");

    StringReader reader = new StringReader(record);
    return (ISNIRecord) unmarshaller.unmarshal(reader);
  }
}
