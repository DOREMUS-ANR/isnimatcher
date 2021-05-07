import org.doremus.isnimatcher.ISNI;
import org.doremus.isnimatcher.ISNIRecord;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

public class ISNITest {
  private final static String MOZART_URI = "https://isni.org/isni/0000000121269154";
  private final static String BEETHOVEN_ID = "0000000121268987";

  @Before
  public void initialize() {
    ISNI.setDebug(true);
  }

  @Test
  public void searchWithFullName() throws IOException {
    String str = "Mozart, Wolfgang Amadeus";
    ISNIRecord r = ISNI.search(str);
    assertEquals(MOZART_URI, r.uri);
    assertEquals("Mozart", r.personalNames.get(0).surname);
    assertEquals("http://fr.dbpedia.org/resource/Wolfgang_Amadeus_Mozart", r.getDBpediaUri("fr"));
    assertEquals("http://dbpedia.org/resource/Wolfgang_Amadeus_Mozart", r.getDBpediaUri());
  }

  @Test
  public void getByISNIUri() throws IOException {
    ISNIRecord r = ISNI.get(MOZART_URI);
    assert r != null;
    assertEquals("Mozart", r.personalNames.get(0).surname);
  }

  @Test
  public void getByISNICode() throws IOException {
    ISNIRecord r = ISNI.get(BEETHOVEN_ID);
    assert r != null;
    assertEquals("Beethoven", r.personalNames.get(0).surname);
  }

  @Test
  public void getViaf() throws IOException {
    ISNIRecord r = ISNI.get("000000007368351X");
    assert r != null;
    assertEquals("https://viaf.org/viaf/19620875", r.getViafURI());
  }

  @Test
  public void searchWithNameAndDate() throws IOException {
    String str = "Beethoven, Ludwig van";
    ISNIRecord r = ISNI.search(str, "1770");
    assertEquals(BEETHOVEN_ID, r.id);
    assertEquals("Beethoven", r.personalNames.get(0).surname);
    assertEquals("1770", r.getBirthYear());
  }

  @Test
  public void searchWithNameAndIncompleteDate() throws IOException {
    ISNIRecord r = ISNI.search("Robert Markham", "19..");
    assertEquals("0000000107873128", r.id);
  }

  @Test
  public void differentIdentities() throws IOException {
    ISNI.setBestViafBehavior(true);
    ISNIRecord r = ISNI.search("Guillaume Apollinaire", "1880");
    assertEquals("0000000121371423", r.id);
    ISNI.setBestViafBehavior(false);
  }


  @Test
  public void searchWithNameSurname() throws IOException {
    ISNIRecord r1 = ISNI.search("Martin", "Walter", null);
    ISNIRecord r2 = ISNI.search("Walter", "Martin", null);
    assert r1 != null;
    assert r2 != null;
    assertNotEquals(r1.id, r2.id);
  }


  @Test
  public void beforeChrist() throws IOException {
    ISNIRecord r = ISNI.get("0000000123748095"); // Aristotle
    assert r != null;
    assertEquals("-0384", r.getBirthYear());
    assertEquals("-0322", r.getDeathYear());
  }

  @Test
  public void beforeAfterChrist() throws IOException {
    ISNIRecord r = ISNI.get("0000000120370699"); // Jesus Christ
    assert r != null;
    assertEquals("-4", r.getBirthYear());
    assertEquals("29", r.getDeathYear());
  }


  @Test
  public void searchWithIncompleteName() throws IOException {
    String str = "Wolfgang Mozart";
    ISNIRecord r = ISNI.search(str);
    String uri = r.uri;
    assertEquals(MOZART_URI, uri);
  }

  @Test
  public void searchWithAlternateName() throws IOException {
    String str = "W. A. Mozart";
    ISNIRecord r = ISNI.search(str);
    String uri = r.uri;
    assertEquals(MOZART_URI, uri);

    str = "Mozart, Volfango Amedeo";
    r = ISNI.search(str);
    uri = r.uri;
    assertEquals(MOZART_URI, uri);
  }


  @Test
  public void beethovenIsNotMozart() throws IOException {
    String str = "Beethoven";
    ISNIRecord r = ISNI.search(str);
    String uri = r.uri;
    assertNotEquals(MOZART_URI, uri);
  }

  @Test
  public void unexistingPerson() throws IOException {
    String str = "Brazov, Ajeje";
    ISNIRecord r = ISNI.search(str);
    assertNull(r);
  }


  @Test
  public void writeAndLoad() throws IOException, JAXBException {
    ISNIRecord r = ISNI.get(MOZART_URI);
    String dst = "test/test.xml";
    assert r != null;
    r.save(dst);
    ISNIRecord s = ISNIRecord.fromFile(dst);
    assertEquals(r.uri, s.uri);
  }

}