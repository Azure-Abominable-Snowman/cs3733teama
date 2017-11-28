package entities.db;

import com.teama.mapsubsystem.data.DatabaseUUID;
import com.teama.mapsubsystem.data.NodeType;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;

public class DatabaseUUIDTest {

    @Test
    public void generateID() throws Exception {
        // Should be the 4th in the series
        Set<String> s = new HashSet<>();
        s.add("AHALL00003");
        s.add("AHALL00103");
        s.add("AHALL00203");
        s.add("AHALL00303");
        assertEquals("AHALL00403", DatabaseUUID.generateID(NodeType.HALL, "03", s));

        // Should be the 1st in the series
        s = new HashSet<>();
        assertEquals("AHALL00103", DatabaseUUID.generateID(NodeType.HALL, "03", s));

        // Should be the 2nd on floor 3
        s = new HashSet<>();
        s.add("AHALL00003");
        s.add("AHALL00103");
        s.add("AHALL00202");
        assertEquals("AHALL00203", DatabaseUUID.generateID(NodeType.HALL, "03", s));

        // Should be the 3rd on floor 3
        s = new HashSet<>();
        s.add("AHALL0000G");
        s.add("AHALL0010G");
        s.add("AHALL0020G");
        assertEquals("AHALL0030G", DatabaseUUID.generateID(NodeType.HALL, "G", s));

        // Should be the 3rd on floor 3, try with RETL
        s = new HashSet<>();
        s.add("ARETL0000G");
        s.add("ARETL0010G");
        s.add("ARETL0020G");
        assertEquals("ARETL0030G", DatabaseUUID.generateID(NodeType.RETL, "G", s));
    }

    @Test
    public void generateID1() throws Exception {
        assertEquals("1_2", DatabaseUUID.generateID("1", "2"));
        assertEquals("ARETL0030G_ARETL0030G", DatabaseUUID.generateID("ARETL0030G", "ARETL0030G"));
        assertEquals("ARETL0030G_ARETL00201", DatabaseUUID.generateID("ARETL0030G", "ARETL00201"));
    }

}