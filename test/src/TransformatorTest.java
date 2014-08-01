
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

import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
 
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Configuration;
import org.testng.annotations.Test;

import fr.paris8.iut.info.stare.Concept;
import fr.paris8.iut.info.stare.ReasonerData;
import fr.paris8.iut.info.stare.Role;
import fr.paris8.iut.info.stare.RoleAxiom;
import fr.paris8.iut.info.stare.StringToConcept;
import fr.paris8.iut.info.stare.TransitiveClosureOfRoleHierarchy;

 
public class TransformatorTest {
 
 @BeforeClass
 public void setUp() {
   // code that will be invoked when this test is instantiated
 }
 

 @Test(groups = { "raw" })
 public void conceptTest() {
   ReasonerData data = new ReasonerData();
   String line  = "A";
   System.out.println("Tested Concept : " + line); 
   Concept c1 = StringToConcept.stringToConcept( line, data );

   System.out.println("Obtained Concept : " + c1.toString(data) );
   
   line  = "(A AND B)";
   System.out.println("Tested Concept : " + line); 
   c1 = StringToConcept.stringToConcept( line , data );
   System.out.println("Obtained Concept : " + c1.toString(data) +", id="+c1.getIdentifier() );
   
   String line2  = "(B AND A)";
   System.out.println("Tested Concept : " + line2); 
   Concept c2 = StringToConcept.stringToConcept( line2 , data);
   System.out.println("Obtained Concept : " + c1.toString(data) +", id="+c2.getIdentifier() );

   if( c1.equals(c2) )
       System.out.println("Result  :" + c1.toString(data) + "=" + c2.toString(data));
   else 
       System.out.println("Result : " + c1.toString(data) + "<>" + c2.toString(data));

   line  = "(A OR B)";
   System.out.println("Tested Concept : " + line); 
   c1 = StringToConcept.stringToConcept( line , data );
   System.out.println("Obtained Concept : " + c1.toString(data) );

   line  = "(NOT B)";
   System.out.println("Tested Concept : " + line); 
   c1 = StringToConcept.stringToConcept( line , data );
   System.out.println("Obtained Concept : " + c1.toString(data) );

   line  = "(A AND ((NOT B) OR B))";
   System.out.println("Tested Concept : " + line); 
   c1 = StringToConcept.stringToConcept( line , data);
   System.out.println("Obtained Concept : " + c1.toString(data) +", id="+c1.getIdentifier());

   line2  = "((B OR (NOT B)) AND A)";
   System.out.println("Tested Concept : " + line); 
   c2 = StringToConcept.stringToConcept( line2 , data);
   System.out.println("Obtained Concept : " + c2.toString(data) +", id="+c2.getIdentifier());


   line  = "(MIN 2 R (A AND ((NOT B) OR B)))";
   System.out.println("Tested Concept : " + line); 
   c1 = StringToConcept.stringToConcept( line , data );
   System.out.println("Obtained Concept : " + c1.toString(data) +", id="+c1.getIdentifier() );

   line2  = "(MIN 2 R (((NOT B) OR B) AND A))";

   System.out.println("Tested Concept : " + line2); 
   c2 = StringToConcept.stringToConcept( line2 , data );
   System.out.println("Obtained Concept : " + c2.toString(data) +", id="+c2.getIdentifier() );

   if( c1.equals(c2) )
       System.out.println("Result :" + c1.toString(data) + "=" + c2.toString(data));
   else 
       System.out.println("Result : " + c1.toString(data) + "<>" + c2.toString(data));

   line  = "(SOME R (((NOT B) OR B) AND (MIN 2 R (((NOT B) OR B) AND A))))";
   System.out.println("Tested Concept : " + line); 
   c1 = StringToConcept.stringToConcept( line , data);
   System.out.println("Obtained Concept : " + c1.toString(data) +", id="+c1.getIdentifier());

   line2  = "(SOME R ((MIN 2 R (A AND ((NOT B) OR B))) AND ((NOT B) OR B)))";
   System.out.println("Tested Concept : " + line2); 
   c2 = StringToConcept.stringToConcept( line2 , data);
   System.out.println("Obtained Concept : " + c2.toString(data) +", id="+c2.getIdentifier() );

   if( c1.equals(c2) )
       System.out.println("Result :" + c1.toString(data) + "=" + c2.toString(data));
   else 
       System.out.println("Result : " + c1.toString(data) + "<>" + c2.toString(data));
 
   OWLOntologyManager manager = OWLManager.createOWLOntologyManager( );
   OWLDataFactory owlfactory = manager.getOWLDataFactory();
   
   OWLOntology onto1 = null;

   try {
           onto1 = manager.loadOntology( IRI.create( "file:/Users/leduc/Ontologies/Azziz/Training.owl"));
	 } catch (OWLOntologyCreationException ex) { 
					ex.printStackTrace();   
   }

   String path = onto1.getOntologyID().getOntologyIRI().toString();
   OWLNamedIndividual ind1 = owlfactory.getOWLNamedIndividual (IRI.create(path + "#o1"));
   OWLNamedIndividual ind2 = owlfactory.getOWLNamedIndividual (IRI.create(path + "#o2"));

   OWLClassExpression cls = owlfactory.getOWLObjectOneOf(  ind1, ind2 );

   ClassExpressionType type =  cls.getClassExpressionType();

   if( type.equals(ClassExpressionType.OBJECT_ONE_OF )) 
      System.out.println(cls.toString() + "is nominal");
   else
      System.out.println(cls.toString() + "is not nominal");
   
   
 }


 @Test(groups = { "raw" })
 public void TransitiveClosureOfRolesTest() {
   

   String line = "R1 subsoleof R2";
   System.out.println("Tested RoleAxiom : " + line); 
   RoleAxiom rax1 = new RoleAxiom(0, new Role("R1",0, false, false, false, false), new Role("R2",1, false, false, false, false) );
   System.out.println("Obtained RoleAxiom : " + rax1.toString() );
   
   line = "R2 subsoleof R3";
   System.out.println("Tested RoleAxiom : " + line); 
   RoleAxiom rax2 = new RoleAxiom(1, new Role("R2",1, true, false, false, false), new Role("R3",2, false, false, true, false) );
   System.out.println("Obtained RoleAxiom : " + rax1.toString() );

   List<RoleAxiom> axs = new  ArrayList<RoleAxiom>();
   axs.add(rax1);
   axs.add(rax2);
   List<Role> roles = new  ArrayList<Role>();
   roles.add(new Role("R1",0, false, false, false, false));   roles.add(new Role("R2", 1, false, false, false, false));
   roles.add(new Role("R3", 2, false, false, false, false));   roles.add(new Role("R4", 3, false, false, false, false));
   TransitiveClosureOfRoleHierarchy tr = new TransitiveClosureOfRoleHierarchy(axs, roles);

   for(RoleAxiom ax : tr.getTransitiveClosureOfRoleHierarchy() ) {
        System.out.println("RoleAxiom in closure: " + ax.toString() );
   }
 }
}



   //try {
   //FileReader isr = new FileReader(new File("test/test.concept"));
   //BufferedReader buff = new BufferedReader(isr);

   //Pattern pattern = Pattern.compile("^/\\*.*/\\*$");
   //Matcher matcher = null;
   //String line = buff.readLine();

   //while (line != null) {
   //    matcher = pattern.matcher(line);
   //    while ( matcher.find() ) {
   //       String newline = line.substring(0, matcher.start() );
   //       Concept c1 = StringToConcept.stringToConcept( newline );
   //       System.out.println("Concept : " + c1.toString() );
           
   //    }
   //    line = buff.readLine();
   //}
 //}
 //catch (IOException ex) {
 //    ex.printStackTrace();
 //}

