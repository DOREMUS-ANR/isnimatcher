import org.doremus.isnimatcher.ISNI;
import org.doremus.isnimatcher.ISNIRecord;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

public class ISNITest {
  private final static String MOZART_URI = "http://isni.org/isni/0000000121269154";
  private final static String BEETHOVEN_ID = "0000000121268987";

  @Test
  public void searchWithFullName() throws IOException {
    String str = "Mozart, Wolfgang Amadeus";
    ISNIRecord r = ISNI.getRecord(str);
    assertEquals(MOZART_URI, r.uri);
    assertEquals("Mozart", r.personalNames.get(0).surname);
    assertEquals("http://fr.dbpedia.org/resource/Wolfgang_Amadeus_Mozart", r.getDBpediaUri("fr"));
    assertEquals("http://dbpedia.org/resource/Wolfgang_Amadeus_Mozart", r.getDBpediaUri());
  }

  @Test
  public void searchWithNameAndDate() throws IOException {
    String str = "Beethoven, Ludwig van";
    ISNIRecord r = ISNI.getRecord(str, "1770");
    assertEquals(BEETHOVEN_ID, r.id);
    assertEquals("Beethoven", r.personalNames.get(0).surname);
    assertEquals("1770", r.getBirthYear());
  }


  @Test
  public void searchWithIncompleteName() throws IOException {
    String str = "Wolfgang Mozart";
    ISNIRecord r = ISNI.getRecord(str);
    String uri = r.uri;
    assertEquals(MOZART_URI, uri);
  }

  @Test
  public void searchWithAlternateName() throws IOException {
    String str = "W. A. Mozart\n";
    ISNIRecord r = ISNI.getRecord(str);
    String uri = r.uri;
    assertEquals(MOZART_URI, uri);
  }


  @Test
  public void beethovenIsNotMozart() throws IOException {
    String str = "Beethoven";
    ISNIRecord r = ISNI.getRecord(str);
    String uri = r.uri;
    assertNotEquals(MOZART_URI, uri);
  }

  @Test
  public void unexistingPerson() throws IOException {
    String str = "Brazov, Ajeje";
    ISNIRecord r = ISNI.getRecord(str);
    assertNull(r);
  }

}