
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
           //onto1 = manager.loadOntology( IRI.create( "file:/usr/local/apps/homesvn/projects/stare/trunk/onto/union-some-all-inv-trans.owl"));
	   //onto1 = manager.loadOntology( IRI.create( "file:/Users/leduc/Ontologies/pizza-no-nominal.owl"));
	   onto1 = manager.loadOntology( IRI.create( "file:/Users/leduc/Ontologies/galen.owl"));
	 } catch (OWLOntologyCreationException ex) { 
					ex.printStackTrace();   
      }

      LoadOntology onto = new LoadOntology(onto1); 
      System.out.println("Loading OK "   );
      ReasonerData data = onto.getData();
	
      //for(Integer i : data.getRoles().keySet())
      //    System.out.println("Loaded Roles ="+ data.getRoles().get(i).toString() );
      //for(Integer i : data.getConcepts().keySet())
      //    System.out.println("Loaded Concepts ="+ data.getConcepts().get(i).toString(data) );
      //for(Integer i : data.getRidges().keySet())
      //    System.out.println("Ridges ="+ data.getRidges().get(i).toString(data) );

      //for(Integer i : data.getRoles().keySet() ) {
      //	   System.out.println("Subsumers of ="+ data.getRoles().get(i).toString() + ":" );
      //	   for(Integer j : data.getSubsumers(i) ) 
      //         System.out.println("subsumer ="+ data.getRoles().get(j).toString() );
      //}
      Frame frame = new Frame(0);
      Startype star1 = frame.init(data);

      //System.out.println("star1 = "+ star1.toString(data) );
      //System.out.println("Saturated star0 = " + star1.isValidForAll(data) );   
   
      star1 = frame.applyRules(star1, data);

      System.out.println("Saturated star1 = " + star1.toShortString(data) );

      //for(Integer i : data.getCores().keySet())
      //    System.out.println("View Cores ID ="+ i +", =" + data.getCores().get(i).toString(data) );
      /*
      int valid=0;
      System.out.println("************** LIST OF ALL STARTYPES ("+ data.getStartypes().values().size() +")*******\\n");
      for(Startype st : data.getStartypes().values() ) {
      	  if(st.isValid()==null&&st.isSaturated()) 
		valid++;
          //System.out.println( st.toString(data) );
      }
      */
      //System.out.println("*******************LIST OF VALID STARTYPES  ("+valid+")**************\\n");
      //for(Startype st : data.getStartypes().values() ) {
	//  if(st.isValid()==null&&st.isSaturated() )
          //System.out.println( st.toString(data) );
      //}
     
      System.out.println("THE NUMBER OF CORE CHANGING   = "+frame.getCoreRuleApplications());
      System.out.println("THE NUMBER OF CORE CHECK   = "+frame.getCoreCheckApplications());
      System.out.println("THE NUMBER OF TIP CHANGING   = "+frame.getTipRuleApplications());
      System.out.println("THE NUMBER OF TIP CHECK   = "+frame.getTipCheckApplications());
     // System.out.println("THE NUMBER OF CLASHES = "+frame.getNbClashes());
      

      //for(Integer ray : data.getStartypesByRay().keySet()) {
     //		   for(Integer st : data.getStartypesByRay().get(ray)) {
     //		       System.out.println("=============Startype id "+  st + ", ray =" + ray + ", =" + data.getStartypes().get(st).toString(data));
     //	           }
     //}
      
 } 
}
 
