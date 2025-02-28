package tools.java_api.java_test;

import tools.java_api.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.Instant;
import java.time.Duration;


@TestMethodOrder(MethodOrderer.MethodName.class)
public class ConnectionTest extends TestBase {

    @Test
    void ConnCreationAndDestroy() {
        try{
            KuzuConnection conn = new KuzuConnection(db);
            conn.destroy();
        }catch(AssertionError e) {
            fail("ConnCreationAndDestroy failed: ");
            System.out.println(e.toString());
        }catch(KuzuObjectRefDestroyedException e) {
            fail("ConnCreationAndDestroy failed: ");
            System.out.println(e.toString());
        }
        System.out.println("ConnCreationAndDestroy passed");
    }

    @Test 
    void ConnInvalidDB() {
        try{
            KuzuConnection conn = new KuzuConnection(null);
            fail("DBInvalidPath did not throw KuzuObjectRefDestroyedException as expected.");
        }catch(AssertionError e) {
        }
        System.out.println("ConnInvalidDB passed");
    }

    @Test
    void ConnQuery() throws KuzuObjectRefDestroyedException {
        String query = "MATCH (a:person) RETURN a.fName;";
        KuzuQueryResult result = conn.query(query);
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.hasNext());
        assertTrue(result.getErrorMessage().equals(""));
        assertEquals(result.getNumTuples(), 8);
        assertEquals(result.getNumColumns(), 1);
        assertTrue(result.getColumnName(0).equals("a.fName"));
        result.destroy();
        System.out.println("ConnQuery passed");
    }

    @Test
    void ConnSetGetMaxNumThreadForExec() throws KuzuObjectRefDestroyedException {
        conn.setMaxNumThreadForExec(4);
        assertEquals(conn.getMaxNumThreadForExec(), 4);
        conn.setMaxNumThreadForExec(8);
        assertEquals(conn.getMaxNumThreadForExec(), 8);
        System.out.println("ConnSetGetMaxNumThreadForExec passed");
    }

    @Test
    void ConnPrepareBool() throws KuzuObjectRefDestroyedException {
        String query = "MATCH (a:person) WHERE a.isStudent = $1 RETURN COUNT(*)";
        Map<String, KuzuValue> m = new HashMap<String, KuzuValue>();
        m.put("1", new KuzuValue(true));
        KuzuPreparedStatement statement = conn.prepare(query);
        assertNotNull(statement);
        KuzuQueryResult result = conn.execute(statement, m);
        assertTrue(result.isSuccess());
        assertTrue(result.hasNext());
        assertTrue(result.getErrorMessage().equals(""));
        assertEquals(result.getNumTuples(), 1);
        assertEquals(result.getNumColumns(), 1);
        KuzuFlatTuple tuple = result.getNext();
        assertEquals(((long)tuple.getValue(0).getValue()), 3);
        statement.destroy();
        result.destroy();
        System.out.println("ConnPrepare passed");
    }

    @Test
    void ConnPrepareInt64() throws KuzuObjectRefDestroyedException {
        String query = "MATCH (a:person) WHERE a.age > $1 RETURN COUNT(*)";
        Map<String, KuzuValue> m = new HashMap<String, KuzuValue>();
        
        m.put("1", new KuzuValue((long)30));
        
        KuzuPreparedStatement statement = conn.prepare(query);
        assertNotNull(statement);
        KuzuQueryResult result = conn.execute(statement, m);
        System.out.println(result.getErrorMessage());
        assertTrue(result.isSuccess());
        assertTrue(result.hasNext());
        assertTrue(result.getErrorMessage().equals(""));
        assertEquals(result.getNumTuples(), 1);
        assertEquals(result.getNumColumns(), 1);
        KuzuFlatTuple tuple = result.getNext();
        assertEquals(((long)tuple.getValue(0).getValue()), 4);
        statement.destroy();
        result.destroy();
        System.out.println("ConnPrepareInt64 passed");
    }

    @Test
    void ConnPrepareInt32() throws KuzuObjectRefDestroyedException {
        String query = "MATCH (a:movies) WHERE a.length > $1 RETURN COUNT(*)";
        Map<String, KuzuValue> m = new HashMap<String, KuzuValue>();
        m.put("1", new KuzuValue((int)200));
        KuzuPreparedStatement statement = conn.prepare(query);
        assertNotNull(statement);
        KuzuQueryResult result = conn.execute(statement, m);
        System.out.println(result.getErrorMessage());
        assertTrue(result.isSuccess());
        assertTrue(result.hasNext());
        assertTrue(result.getErrorMessage().equals(""));
        assertEquals(result.getNumTuples(), 1);
        assertEquals(result.getNumColumns(), 1);
        KuzuFlatTuple tuple = result.getNext();
        assertEquals(((long)tuple.getValue(0).getValue()), 2);
        statement.destroy();
        result.destroy();
        System.out.println("ConnPrepareInt32 passed");
    }

    @Test
    void ConnPrepareInt16() throws KuzuObjectRefDestroyedException {
        String query = "MATCH (a:person) -[s:studyAt]-> (b:organisation) WHERE s.length > $1 RETURN COUNT(*)";
        Map<String, KuzuValue> m = new HashMap<String, KuzuValue>();
        m.put("1", new KuzuValue((short)10));
        KuzuPreparedStatement statement = conn.prepare(query);
        assertNotNull(statement);
        KuzuQueryResult result = conn.execute(statement, m);
        System.out.println(result.getErrorMessage());
        assertTrue(result.isSuccess());
        assertTrue(result.hasNext());
        assertTrue(result.getErrorMessage().equals(""));
        assertEquals(result.getNumTuples(), 1);
        assertEquals(result.getNumColumns(), 1);
        KuzuFlatTuple tuple = result.getNext();
        assertEquals(((long)tuple.getValue(0).getValue()), 2);
        statement.destroy();
        result.destroy();
        System.out.println("ConnPrepareInt16 passed");
    }

    @Test
    void ConnPrepareDouble() throws KuzuObjectRefDestroyedException {
        String query = "MATCH (a:person) WHERE a.eyeSight > $1 RETURN COUNT(*)";
        Map<String, KuzuValue> m = new HashMap<String, KuzuValue>();
        m.put("1", new KuzuValue((double)4.5));
        KuzuPreparedStatement statement = conn.prepare(query);
        assertNotNull(statement);
        KuzuQueryResult result = conn.execute(statement, m);
        System.out.println(result.getErrorMessage());
        assertTrue(result.isSuccess());
        assertTrue(result.hasNext());
        assertTrue(result.getErrorMessage().equals(""));
        assertEquals(result.getNumTuples(), 1);
        assertEquals(result.getNumColumns(), 1);
        KuzuFlatTuple tuple = result.getNext();
        assertEquals(((long)tuple.getValue(0).getValue()), 7);
        statement.destroy();
        result.destroy();
        System.out.println("ConnPrepareDouble passed");
    }

    @Test
    void ConnPrepareFloat() throws KuzuObjectRefDestroyedException {
        String query = "MATCH (a:person) WHERE a.height < $1 RETURN COUNT(*)";
        Map<String, KuzuValue> m = new HashMap<String, KuzuValue>();
        m.put("1", new KuzuValue((float)1.0));
        KuzuPreparedStatement statement = conn.prepare(query);
        assertNotNull(statement);
        KuzuQueryResult result = conn.execute(statement, m);
        System.out.println(result.getErrorMessage());
        assertTrue(result.isSuccess());
        assertTrue(result.hasNext());
        assertTrue(result.getErrorMessage().equals(""));
        assertEquals(result.getNumTuples(), 1);
        assertEquals(result.getNumColumns(), 1);
        KuzuFlatTuple tuple = result.getNext();
        assertEquals(((long)tuple.getValue(0).getValue()), 1);
        statement.destroy();
        result.destroy();
        System.out.println("ConnPrepareFloat passed");
    }

    @Test
    void ConnPrepareString() throws KuzuObjectRefDestroyedException {
        String query = "MATCH (a:person) WHERE a.fName = $1 RETURN COUNT(*)";
        Map<String, KuzuValue> m = new HashMap<String, KuzuValue>();
        m.put("1", new KuzuValue("Alice"));
        KuzuPreparedStatement statement = conn.prepare(query);
        assertNotNull(statement);
        KuzuQueryResult result = conn.execute(statement, m);
        System.out.println(result.getErrorMessage());
        assertTrue(result.isSuccess());
        assertTrue(result.hasNext());
        assertTrue(result.getErrorMessage().equals(""));
        assertEquals(result.getNumTuples(), 1);
        assertEquals(result.getNumColumns(), 1);
        KuzuFlatTuple tuple = result.getNext();
        assertEquals(((long)tuple.getValue(0).getValue()), 1);
        statement.destroy();
        result.destroy();
        System.out.println("ConnPrepareString passed");
    }

    @Test
    void ConnPrepareDate() throws KuzuObjectRefDestroyedException {
        String query = "MATCH (a:person) WHERE a.birthdate > $1 RETURN COUNT(*)";
        Map<String, KuzuValue> m = new HashMap<String, KuzuValue>();
        m.put("1", new KuzuValue(LocalDate.EPOCH));
        KuzuPreparedStatement statement = conn.prepare(query);
        assertNotNull(statement);
        KuzuQueryResult result = conn.execute(statement, m);
        System.out.println(result.getErrorMessage());
        assertTrue(result.isSuccess());
        assertTrue(result.hasNext());
        assertTrue(result.getErrorMessage().equals(""));
        assertEquals(result.getNumTuples(), 1);
        assertEquals(result.getNumColumns(), 1);
        KuzuFlatTuple tuple = result.getNext();
        assertEquals(((long)tuple.getValue(0).getValue()), 4);
        statement.destroy();
        result.destroy();
        System.out.println("ConnPrepareDate passed");
    }

    @Test
    void ConnPrepareTimeStamp() throws KuzuObjectRefDestroyedException {
        String query = "MATCH (a:person) WHERE a.registerTime > $1 RETURN COUNT(*)";
        Map<String, KuzuValue> m = new HashMap<String, KuzuValue>();
        m.put("1", new KuzuValue(Instant.EPOCH));
        KuzuPreparedStatement statement = conn.prepare(query);
        assertNotNull(statement);
        KuzuQueryResult result = conn.execute(statement, m);
        System.out.println(result.getErrorMessage());
        assertTrue(result.isSuccess());
        assertTrue(result.hasNext());
        assertTrue(result.getErrorMessage().equals(""));
        assertEquals(result.getNumTuples(), 1);
        assertEquals(result.getNumColumns(), 1);
        KuzuFlatTuple tuple = result.getNext();
        assertEquals(((long)tuple.getValue(0).getValue()), 7);
        statement.destroy();
        result.destroy();
        System.out.println("ConnPrepareTimeStamp passed");
    }

    @Test
    void ConnPrepareInterval() throws KuzuObjectRefDestroyedException {
        String query = "MATCH (a:person) WHERE a.lastJobDuration > $1 RETURN COUNT(*)";
        Map<String, KuzuValue> m = new HashMap<String, KuzuValue>();
        m.put("1", new KuzuValue(Duration.ofDays(3650)));
        KuzuPreparedStatement statement = conn.prepare(query);
        assertNotNull(statement);
        KuzuQueryResult result = conn.execute(statement, m);
        System.out.println(result.getErrorMessage());
        assertTrue(result.isSuccess());
        assertTrue(result.hasNext());
        assertTrue(result.getErrorMessage().equals(""));
        assertEquals(result.getNumTuples(), 1);
        assertEquals(result.getNumColumns(), 1);
        KuzuFlatTuple tuple = result.getNext();
        assertEquals(((long)tuple.getValue(0).getValue()), 3);
        statement.destroy();
        result.destroy();
        System.out.println("ConnPrepareInterval passed");
    }

    @Test
    void ConnPrepareMultiParam() throws KuzuObjectRefDestroyedException {
        String query = "MATCH (a:person) WHERE a.lastJobDuration > $1 AND a.fName = $2 RETURN COUNT(*)";
        Map<String, KuzuValue> m = new HashMap<String, KuzuValue>();
        m.put("1", new KuzuValue(Duration.ofDays(730)));
        m.put("2", new KuzuValue("Alice"));
        KuzuPreparedStatement statement = conn.prepare(query);
        assertNotNull(statement);
        KuzuQueryResult result = conn.execute(statement, m);
        System.out.println(result.getErrorMessage());
        assertTrue(result.isSuccess());
        assertTrue(result.hasNext());
        assertTrue(result.getErrorMessage().equals(""));
        assertEquals(result.getNumTuples(), 1);
        assertEquals(result.getNumColumns(), 1);
        KuzuFlatTuple tuple = result.getNext();
        assertEquals(((long)tuple.getValue(0).getValue()), 1);
        statement.destroy();
        result.destroy();
        System.out.println("ConnPrepareMultiParam passed");
    }
    
    @Test
    void ConnGetNodeTableNames() throws KuzuObjectRefDestroyedException {
        String result = conn.getNodeTableNames();
        assertNotNull(result);
        assertTrue(result.equals("Node tables: \n" + 
                                 "\torganisation\n" +
                                 "\tperson\n" +
                                 "\tmovies\n") ||
                   result.equals("Node tables: \n" + 
                                 "\tmovies\n" +
                                 "\tperson\n" +
                                 "\torganisation\n"));
        System.out.println("ConnGetNodeTableNames passed");
    }

    @Test
    void ConnGetRelTableNames() throws KuzuObjectRefDestroyedException {
        String result = conn.getRelTableNames();
        assertNotNull(result);
        assertTrue(result.equals("Rel tables: \n" +
                                "\tmeets\n" +
                                "\tstudyAt\n" +
                                "\tknows\n" + 
                                "\tworkAt\n" +
                                "\tmarries\n") || 
                   result.equals("Rel tables: \n" +
                                "\tmarries\n" +
                                "\tworkAt\n" +
                                "\tknows\n" + 
                                "\tstudyAt\n" +
                                "\tmeets\n"));
        System.out.println("ConnGetRelTableNames passed");
    }

    @Test
    void ConnGetNodePropertyNames() throws KuzuObjectRefDestroyedException {
        String result = conn.getNodePropertyNames("movies");
        assertNotNull(result);
        assertTrue(result.equals("movies properties: \n" +
                                "\tname STRING(PRIMARY KEY)\n" + 
                                "\tlength INT32\n" + 
                                "\tnote STRING\n" + 
                                "\tdescription STRUCT(DOUBLE,INT64,TIMESTAMP,DATE)\n"));
        System.out.println("ConnGetNodePropertyNames passed");
    }

    @Test
    void ConnGetRelPropertyNames() throws KuzuObjectRefDestroyedException {
        String result = conn.getRelPropertyNames("meets");
        assertNotNull(result);
        assertTrue(result.equals("meets src node: person\n" + 
                                "meets dst node: person\n" + 
                                "meets properties: \n" + 
                                "\tlocation FLOAT[2]\n" + 
                                "\ttimes INT32\n"));
        System.out.println("ConnGetRelPropertyNames passed");
    }

    @Test
    void ConnQueryTimeout() throws KuzuObjectRefDestroyedException {
        conn.setQueryTimeout(1);
        KuzuQueryResult result = conn.query("MATCH (a:person)-[:knows*1..28]->(b:person) RETURN COUNT(*);");
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().equals("Interrupted."));
        result.destroy();
        System.out.println("ConnQueryTimeout passed");
    }

}
