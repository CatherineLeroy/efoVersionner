package uk.ac.ebi.spot;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.spot.efoVersionner.VersionUpdater;

/**
 * Unit test for simple App.
 */
public class VersionUpdaterTest
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public VersionUpdaterTest(String testName)
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( VersionUpdaterTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {

        try {
            VersionUpdater vu = new VersionUpdater();


            String versionNumber = "2.61";
            String expectedVersionNumber = "3.00";
            vu.setTypeEnum("mj");
            assertEquals(expectedVersionNumber, vu.versionNumberIncrementer(versionNumber));


            versionNumber = "2.61";
            expectedVersionNumber = "2.62";
            vu.setTypeEnum("mn");
            assertEquals(expectedVersionNumber, vu.versionNumberIncrementer(versionNumber));

            versionNumber = "2.61";
            expectedVersionNumber = "2.61.1";
            vu.setTypeEnum("mm");
            assertEquals(expectedVersionNumber, vu.versionNumberIncrementer(versionNumber));


            versionNumber = "2.61.1";
            expectedVersionNumber = "2.61.2";
            vu.setTypeEnum("mm");
            assertEquals(expectedVersionNumber, vu.versionNumberIncrementer(versionNumber));






        }catch(Exception e){
            fail("An excpetion was sent when creating the VersionUpdater object.");
        }
        assertTrue( true );
    }
}
