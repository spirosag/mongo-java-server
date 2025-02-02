package de.bwaldvogel.mongo.backend.postgresql;

import static de.bwaldvogel.mongo.backend.TestUtils.json;
import static de.bwaldvogel.mongo.backend.TestUtils.toArray;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mongodb.DuplicateKeyException;
import com.mongodb.client.model.IndexOptions;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import de.bwaldvogel.mongo.MongoBackend;
import de.bwaldvogel.mongo.MongoDatabase;
import de.bwaldvogel.mongo.backend.AbstractBackendTest;

public class PostgresqlBackendTest extends AbstractBackendTest {

    private static HikariDataSource dataSource;

    @BeforeClass
    public static void initializeDataSource() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:postgresql://localhost/mongo-java-server-test");
        config.setUsername("mongo-java-server-test");
        config.setPassword("mongo-java-server-test");
        config.setMaximumPoolSize(5);

        dataSource = new HikariDataSource(config);
    }

    @AfterClass
    public static void closeDataSource() {
        dataSource.close();
    }

    @Override
    protected MongoBackend createBackend() throws Exception {
        PostgresqlBackend backend = new PostgresqlBackend(dataSource);
        for (String db : Arrays.asList(TEST_DATABASE_NAME, OTHER_TEST_DATABASE_NAME)) {
            MongoDatabase mongoDatabase = backend.openOrCreateDatabase(db);
            mongoDatabase.drop();
        }
        return backend;
    }

    @Override
    public void testCompoundSparseUniqueIndex() throws Exception {
        assumeStrictTests();
        super.testCompoundSparseUniqueIndex();
    }

    @Override
    public void testCompoundSparseUniqueIndexOnEmbeddedDocuments() throws Exception {
        assumeStrictTests();
        super.testCompoundSparseUniqueIndexOnEmbeddedDocuments();
    }

    @Override
    public void testSparseUniqueIndexOnEmbeddedDocument() throws Exception {
        assumeStrictTests();
        super.testSparseUniqueIndexOnEmbeddedDocument();
    }

    @Override
    public void testSecondarySparseUniqueIndex() throws Exception {
        assumeStrictTests();
        super.testSecondarySparseUniqueIndex();
    }

    @Override
    public void testUniqueIndexWithDeepDocuments() throws Exception {
        assumeStrictTests();
        super.testUniqueIndexWithDeepDocuments();
    }

    @Override
    public void testOrderByEmbeddedDocument() throws Exception {
        assumeStrictTests();
        super.testOrderByEmbeddedDocument();
    }

    @Override
    public void testOrderByMissingAndNull() throws Exception {
        assumeStrictTests();
        super.testOrderByMissingAndNull();
    }

    @Override
    public void testSortDocuments() throws Exception {
        assumeStrictTests();
        super.testSortDocuments();
    }

    @Override
    public void testFindAndOrderByWithListValues() throws Exception {
        assumeStrictTests();
        super.testFindAndOrderByWithListValues();
    }

    @Override
    public void testSort() {
        assumeStrictTests();
        super.testSort();
    }

    @Override
    public void testInsertQueryAndSortBinaryTypes() throws Exception {
        assumeStrictTests();
        super.testInsertQueryAndSortBinaryTypes();
    }

    @Override
    public void testUuidAsId() throws Exception {
        assumeStrictTests();
        super.testUuidAsId();
    }

    @Override
    public void testTypeMatching() throws Exception {
        assumeStrictTests();
        super.testTypeMatching();
    }

    @Override
    public void testDecimal128() throws Exception {
        assumeStrictTests();
        super.testDecimal128();
    }

    @Override
    public void testArrayNe() throws Exception {
        assumeStrictTests();
        super.testArrayNe();
    }

    @Override
    @Test
    public void testRegExQuery() throws Exception {
        assumeStrictTests();
        super.testRegExQuery();
    }

    @Override
    public void testInsertAndFindJavaScriptContent() throws Exception {
        assumeStrictTests();
        super.testInsertAndFindJavaScriptContent();
    }

    @Override
    public void testMultikeyIndex_simpleArrayValues() throws Exception {
        assumeStrictTests();
        super.testMultikeyIndex_simpleArrayValues();
    }

    @Override
    public void testCompoundMultikeyIndex_simpleArrayValues() throws Exception {
        assumeStrictTests();
        super.testCompoundMultikeyIndex_simpleArrayValues();
    }

    @Override
    public void testCompoundMultikeyIndex_documents() throws Exception {
        assumeStrictTests();
        super.testCompoundMultikeyIndex_documents();
    }

    @Override
    public void testCompoundMultikeyIndex_deepDocuments() throws Exception {
        assumeStrictTests();
        super.testCompoundMultikeyIndex_deepDocuments();
    }

    @Override
    public void testCompoundMultikeyIndex_threeKeys() throws Exception {
        assumeStrictTests();
        super.testCompoundMultikeyIndex_threeKeys();
    }

    @Override
    public void testEmbeddedSort_arrayOfDocuments() {
        assumeStrictTests();
        super.testEmbeddedSort_arrayOfDocuments();
    }

    @Override
    public void testAddUniqueIndexOnExistingDocuments_violatingUniqueness() throws Exception {
        collection.insertOne(json("_id: 1, value: 'a'"));
        collection.insertOne(json("_id: 2, value: 'b'"));
        collection.insertOne(json("_id: 3, value: 'c'"));
        collection.insertOne(json("_id: 4, value: 'b'"));

        assertThatExceptionOfType(DuplicateKeyException.class)
            .isThrownBy(() -> collection.createIndex(json("value: 1"), new IndexOptions().unique(true)))
            .withMessage("Write failed with error code 11000 and error message " +
                "'E11000 duplicate key error collection: testdb.testcoll index: " +
                "ERROR: could not create unique index \"testcoll_value_ASC\"\n  Detail: Key ((data ->> 'value'::text))=(b) is duplicated.'");

        assertThat(toArray(collection.listIndexes()))
            .containsExactly(json("name: '_id_', ns: 'testdb.testcoll', key: {_id: 1}, v: 2"));

        collection.insertOne(json("_id: 5, value: 'a'"));
    }

    private void assumeStrictTests() {
        Assume.assumeTrue(Boolean.getBoolean(PostgresqlBackend.class.getSimpleName() + ".strictTest"));
    }

}
