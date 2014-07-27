
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.lang.Runtime;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.Integer;
import java.lang.CloneNotSupportedException;
import java.lang.Cloneable;

 
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Configuration;
import org.testng.annotations.Test;


import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;


import fr.paris8.iut.info.stare.Concept;
import fr.paris8.iut.info.stare.Role;
import fr.paris8.iut.info.stare.RoleAxiom;
import fr.paris8.iut.info.stare.StringToConcept;
import fr.paris8.iut.info.stare.ReasonerData;
import fr.paris8.iut.info.stare.TransitiveClosureOfRoleHierarchy;

import fr.paris8.iut.info.stare.Startype;
import fr.paris8.iut.info.stare.LoadOntology;
import fr.paris8.iut.info.stare.ReasonerData;
import fr.paris8.iut.info.stare.ConceptLabel;
import fr.paris8.iut.info.stare.Concept;
import fr.paris8.iut.info.stare.Frame;

public class StartypeTest {
 
 @BeforeClass
 public void setUp() {
   // code that will be invoked when this test is instantiated
 }
 

 @Test(groups = { "raw" })
 public void conceptTest() throws CloneNotSupportedException {
      OWLOntologyManager manager = OWLManager.createOWLOntologyManager( );

     
      OWLOntology onto1 = null;
      try {
           onto1 = manager.loadOntology( IRI.create( "file:/usr/local/apps/homesvn/projects/stare/trunk/onto/union-some.owl"));
	 } catch (OWLOntologyCreationException ex) { 
					ex.printStackTrace();   
      }

      LoadOntology onto = new LoadOntology(onto1); 
      ReasonerData data = onto.getData();

      Frame frame = new Frame(0);
      Startype star1 = frame.init(data);

      System.out.println("star1 = "+ star1.toString(data) );

      //star1 = frame.init(data, null);

      //System.out.println("star2 = "+star1.toString() );

      //for(Startype st : star1.getProgeny()) {
         // System.out.println("stars  ="+st.toString() );
      //}
 } 
}
 
