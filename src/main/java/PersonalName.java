import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "personalName")
@XmlAccessorType(XmlAccessType.FIELD)
public class PersonalName {

  @XmlElement(name = "forename")
  public String forename;

  @XmlElement(name = "surname")
  public String surname;

  @XmlElement(name = "nameUse")
  public String nameUse;

  @XmlElement(name = "marcDate")
  public String marcDate;

  @XmlElement(name = "source")
  public List<String> sources;

  @XmlElement(name = "subsourceIdentifier")
  public String subsourceIdentifier;


}
