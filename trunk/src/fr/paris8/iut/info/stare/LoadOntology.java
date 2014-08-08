/*
 * $Id$
 *
 * Copyright (C) Paris8-IUT de Montreuil, 2013-2014
 * Copyright (C) Marne-La Valee, 2013-2014
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA
 */

package fr.paris8.iut.info.stare;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.SWRLPredicate;

import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
import fr.paris8.iut.info.stare.Concept.Type;

/**
 * Loads all the data from an ontology. Those data can be accessed later for
 * further treatment.
 * 
 * @author Jérémy Lhez
 * 
 */
public class LoadOntology {
	/** The ontology extracted from the document */
	private OWLOntology ontology;
	/**
	 * The concepts (classes) in the ontology. Thing is always at the position 0
	 */
	private Map<OWLClass, Integer> classes;
	/** The data types in the ontology */
	private Map<OWLDatatype, Integer> datatypes;
	/** The roles (properties) in the ontology, with their characteristics */
	private Map<SWRLPredicate, Role> ontologyRoles;
	private ReasonerData reasonerData;
	private int increment;

	/**
	 * The main constructor.
	 * 
	 * @param ontology
	 *            The ontology the class will use.
	 */
	public LoadOntology(OWLOntology ontology) {
		reasonerData = new ReasonerData();
		classes = new HashMap<OWLClass, Integer>();
		datatypes = new HashMap<OWLDatatype, Integer>();
		ontologyRoles = new HashMap<SWRLPredicate, Role>();

		this.ontology = ontology;

		//increment = 1;
		//this.computeClasses();
		//this.computeDatatypes();
		//reasonerData.initConceptMap(classes);
		//increment = 0;
		//this.computeProperties();
		//increment = 0;
		this.makeConceptsFromSubClasses();
		//this.makeConceptsFromPropertyDomains();
		//this.makeConceptsFromPropertyRanges();
		//this.makeNominalConcepts();
		//this.makeConceptFromFunctional();
		//increment = 0;
		//this.makeAssertions();
		//increment = 0;
		this.makeRoleAxioms();
		 
		ConceptLabel allNNF = new ConceptLabel();
		for(ConceptAxiom i : reasonerData.getConceptAxioms().values() ) {
		    //each NNF is identified. This may be already done
		    //Concept c = i.getNNF();
		    Concept c = i.getRightMember();
		    c = reasonerData.addConcept( c );
		    reasonerData.getAxiomNNFs().add( new Integer(c.getIdentifier()) );	
		    reasonerData.getAxiomNNFs().add( new Integer(c.getIdentifier()) );	
		    allNNF.add( new Integer(c.getIdentifier()) ); 
		}
		allNNF = reasonerData.addCore(allNNF);
		reasonerData.setNNFConceptLabel(allNNF.getIdentifier());

		reasonerData.setTransitiveClosureOfRoleHierarchy(new TransitiveClosureOfRoleHierarchy(
				reasonerData.getRoleAxioms().values(), reasonerData.getRoles().values() ));
		reasonerData.setTerminalConceptNames();
	}

	//compute all subconcepts of "concept"
	//
	public Set<Integer> subconcepts(Integer concept, ReasonerData data){
	       Set<Integer> closure = new HashSet<Integer>();

	       if ( data.getConcepts().get(concept).isTerminal() ) {
		    closure.add(concept);
		    return closure;
	       } else {
			closure.add( concept );
			switch (data.getConcepts().get(concept).getOperator()) {
			case INTERSECTION:
				for (Integer child : data.getConcepts().get(concept).getChildren()) { 
			             closure.addAll( subconcepts (child, data) );
				}
				return closure;  
			case UNION:
				closure.addAll( subconcepts (data.getConcepts().get(concept).getChildren().get(0), data) );
				closure.addAll( subconcepts (data.getConcepts().get(concept).getChildren().get(1), data) );
				return closure; 
			case SOME:
				closure.addAll( subconcepts (data.getConcepts().get(concept).getChildren().get(0), data));
				return closure; 
			case ALL:
				closure.addAll( subconcepts ( data.getConcepts().get(concept).getChildren().get(0), data));
				return closure; 
			case MIN:
				closure.addAll( subconcepts ( data.getConcepts().get(concept).getChildren().get(0), data) );
				return closure;
			case MAX:
				closure.addAll( subconcepts ( data.getConcepts().get(concept).getChildren().get(0), data) ) ;
				return closure;
			case COMPLEMENT:
				closure.addAll( subconcepts ( data.getConcepts().get(concept).getChildren().get(0), data) );
				return closure;
			default:
				break;
			}
			return closure;  
		}  
    
	}
	//compute all possible subconcepts of ontology in NNF (i.e. no negation is in front of), 
	//including negated concept, closure propagation, cardinality 
	//
	/*
 	public void computeFischerLadnerClosure(){
		//get all nnfs coming from axioms
	       Set<Concept> closure = new HashSet<Concept>();
	       Set<Concept> nnfs = new HashSet<Concept>();
	       for(Integer i :  reasonerData.getAxiomNNFs()){
		   nnfs.add(reasonerData.getConcepts().get(i));
	       }
	       
	       //we must initialise "concepts"
	       reasonerData = new HashMap<Integer, Concept>();
	       
	}
	*/
	/**
	 * Stores the classes of the ontology, then adds the Thing and Nothing
	 * classes if they are not present. Used by the constructor.
	 */
	private void computeClasses() {
		for (OWLClass owlClass : ontology.getClassesInSignature()) {
			if (owlClass.isBottomEntity())
				this.classes.put(owlClass, 1);
			else if (owlClass.isTopEntity())
				this.classes.put(owlClass, 0);
			else {
				this.classes.put(owlClass, increment);
				increment++;
			}
			if( ! owlClass.isAnonymous()) {
		              String name = owlClass.getIRI().toString();
			      Concept c  = new Concept(name.substring(name.indexOf("#") + 1), -1, false, false);
			      c = reasonerData.addConcept(c);
			}
		}
		this.classes.put(new OWLClassImpl(IRI.create("Thing")), 0);
	}

	/**
	 * Stores the datatypes of the ontology. Used by the constructor.
	 */
	private void computeDatatypes() {
		Set<OWLDatatype> data = ontology.getDatatypesInSignature();

		for (OWLDatatype type : data) {
			this.datatypes.put(type, increment);
			increment++;
		}
	}

	/**
	 * Stores the properties of the ontology, and their characteristics in
	 * axioms. Used by the constructor. Also, gives each property an inverse and
	 * a transitive closure.
	 */
	private void computeProperties() {
		Role role;
		String propertyName;

		/* attribution of identifiers & storage */
		/* ObjectProperties */
		for (OWLObjectProperty property : ontology
				.getObjectPropertiesInSignature()) {
			propertyName = property.getIRI().toString();
			role = new ObjectRole(propertyName.substring(propertyName
					.indexOf("#") + 1), increment,
					property.isTransitive(ontology),
					property.isFunctional(ontology), false, false);
			ontologyRoles.put(property, role);
			role = reasonerData.addRole(role);
			increment++;
		}

		/* DataProperties */
		for (OWLDataProperty property : ontology.getDataPropertiesInSignature()) {
			propertyName = property.getIRI().toString();
			role = new DataRole(propertyName.substring(propertyName
					.indexOf("#") + 1), increment, false,
					property.isFunctional(ontology), false, false);
			ontologyRoles.put(property, role);
			role = reasonerData.addRole(role);
			increment++;
		}

		this.addInverseProperties();
		//this.addTransitiveClosures();
	}

	/**
	 * Creates inverses as roles and stores them (only for the roles that have
	 * inverses).<br/>
	 * The created inverses can be transitive or functional, depending of the
	 * basic role.<br/>
	 * Used by computeProperties.
	 */
	private void addInverseProperties() {

		for (OWLObjectProperty property : ontology
				.getObjectPropertiesInSignature()) {
			Role role = new Role(property.toString().substring(
					property.toString().indexOf("#") + 1,
					property.toString().length() - 1), increment, property
					.isTransitive(ontology), property.isFunctional(ontology),
					true, false);
			role = reasonerData.addRole(role);
			increment++;
		}
	}

	/**
	 * Creates transitive closures as roles and stores them (only for the roles
	 * that are transitive).<br/>
	 * The created closures can be inverse depending of the basic role, but they
	 * will never be functional and always be transitive.<br/>
	 * Used by computeProperties.
	 */
	/*
	private void addTransitiveClosures() {
		List<Role> toAdd = new ArrayList<Role>();
		for (Role role : reasonerData.getRoles().values() ) {
			Role r = new Role(role.getName(), increment, true, false, role.isInverse(), true);
			r = reasonerData.addRole(r);
			toAdd.add(r);
			increment++;
		}

		reasonerData.addRoles(toAdd);
	}
	*/
	/**
	 * Converts the axioms of the ontology dealing with domains of properties.<br/>
	 * Used by the constructor.
	 * 
	 * WARNING: using owlapi 3.4.10, the DataProperty class is considered having
	 * no inverse. For this reason, it is not possible to extract concepts from
	 * its domains.
	 */
	private void makeConceptsFromPropertyDomains() {
		Concept left, right, nnf;
		Concept top = new Concept("Thing", 0, false, false);
		top = reasonerData.addConcept(top);
		/* OBJECT */
		for (OWLObjectProperty property : ontology
				.getObjectPropertiesInSignature()) {
			for (OWLObjectPropertyDomainAxiom axiom : ontology
					.getObjectPropertyDomainAxioms(property)) {
				left =  new Concept(ontologyRoles.get(axiom.getProperty()).getIdentifier(), Type.SOME, top.getIdentifier());
				left = reasonerData.addConcept(left);
				right = getConceptFromClassRecursive(axiom.getDomain().getNNF());
				nnf = new Concept(Type.UNION, Concept.negate(left.getIdentifier(), reasonerData), right.getIdentifier());
				nnf = reasonerData.addConcept(nnf);
				reasonerData.addConceptAxiom(new ConceptAxiom(increment, left,
						right, nnf));
				increment++;
			}
		}
		/* DATA */
		for (OWLDataProperty property : ontology.getDataPropertiesInSignature()) {
			for (OWLDataPropertyDomainAxiom axiom : ontology
					.getDataPropertyDomainAxioms(property)) {
				left = new Concept(ontologyRoles.get(axiom.getProperty()).getIdentifier(), Type.SOME, top.getIdentifier());
				left = reasonerData.addConcept(left);
				right = getConceptFromClassRecursive(axiom.getDomain().getNNF());
				nnf = new Concept(
						Type.UNION, Concept.negate(left.getIdentifier(), reasonerData), right.getIdentifier());
				nnf = reasonerData.addConcept(nnf);
				reasonerData.addConceptAxiom(new ConceptAxiom(increment, left,
						right, nnf));
				increment++;
			}
		}
	}

	/**
	 * Converts the axioms of the ontology dealing with ranges of properties.<br/>
	 * Used by the constructor.
	 * 
	 * WARNING: using owlapi 3.4.10, the DataProperty class is considered having
	 * no inverse. For this reason, it is not possible to extract concepts from
	 * its ranges.
	 * 
	 * WARNING: the concept will be created with the inverse of the property. If
	 * the inverse is not declared precisely in the ontology, no concept will be
	 * created and the range axiom will be ignored.
	 * 
	 * @return The list of concepts created from the axioms.
	 */
	private void makeConceptsFromPropertyRanges() {
		Concept left, right, nnf;
		Concept top = new Concept("Thing", 0, false, false);
		reasonerData.addConcept(top);
		/* OBJECT */
		for (OWLObjectProperty property : ontology
				.getObjectPropertiesInSignature()) {
			for (OWLObjectPropertyRangeAxiom axiom : ontology
					.getObjectPropertyRangeAxioms(property)) {
				Role role = reasonerData.getInverseOfRole(ontologyRoles.get(axiom.getProperty()), reasonerData);
				left = new Concept(role.getIdentifier(), Type.SOME , 
  						           top.getIdentifier());
				left = reasonerData.addConcept(left);
				right = getConceptFromClassRecursive(axiom.getRange().getNNF());
				nnf = new Concept(Concept.negate(left.getIdentifier(), reasonerData), Type.UNION, right.getIdentifier());
				nnf = reasonerData.addConcept(nnf);
				reasonerData.addConceptAxiom(new ConceptAxiom(increment, left,
						right, nnf));
				increment++;
			}
		}
	}

	/**
	 * Converts the functional properties into axioms.
	 */
	private void makeConceptFromFunctional() {
		Concept left, right, nnf;

		for (OWLObjectProperty property : ontology
				.getObjectPropertiesInSignature())
			if (property.isFunctional(ontology)) {
				Concept top = new Concept("Thing", 0, false, false);
				top = reasonerData.addConcept(top);
				left =  new Concept(ontologyRoles.get(property).getIdentifier(),Type.SOME, top.getIdentifier());
				right = new Concept(1, ontologyRoles.get(property).getIdentifier(), Type.MAX, top.getIdentifier());
				nnf =  new Concept(Concept.negate(left.getIdentifier(), reasonerData),
						Type.UNION, right.getIdentifier());
				left = reasonerData.addConcept(left);
				right = reasonerData.addConcept(right);
				nnf = reasonerData.addConcept(nnf);
				reasonerData.addConceptAxiom(new ConceptAxiom(increment, left,
						right, nnf));
				increment++;
			}
	}
	
	 

	/**
	 * Stores the axioms dealing with concepts.<br/>
	 * Used by the constructor.
	 */
	private void makeConceptsFromSubClasses() {
		OWLDataFactory factory = new OWLDataFactoryImpl();
		OWLClassExpression expression, subClass = null, superClass = null, subClass2 = null, superClass2 = null;
		//for (OWLClass owlClass : classes.keySet()) {
			/* dealing with subClass axioms */
			//for (OWLSubClassOfAxiom subClassOfAxiom : ontology
			//		.getSubClassAxiomsForSubClass(owlClass)) {

			for (OWLAxiom classAxiom : ontology.getAxioms()) {

				//System.out.println("type="+ classAxiom.getAxiomType().toString());

				if(classAxiom.getAxiomType().equals(AxiomType.SUBCLASS_OF) ) {	
					subClass = ((OWLSubClassOfAxiom)classAxiom).getSubClass();
					superClass =  ((OWLSubClassOfAxiom)classAxiom).getSuperClass();
					expression = factory.getOWLObjectUnionOf(
						factory.getOWLObjectComplementOf(subClass), superClass).getNNF();
					Concept c1 = getConceptFromClassRecursive(subClass); 
				//System.out.println("c1="+ c1.toString(reasonerData) + ", id=" +c1.getIdentifier());
					Concept c2 = getConceptFromClassRecursive(superClass);
				//System.out.println("c2="+ c2.toString(reasonerData) + ", id=" +c2.getIdentifier());
					Concept c3 = getConceptFromClassRecursive(expression);
				//System.out.println("c3="+ c3.toString(reasonerData) + ", id=" +c3.getIdentifier());
					reasonerData.addConceptAxiom(new ConceptAxiom(increment,c1,c2,c3));
					increment++;
			        }

			/* dealing with the disjoint axioms */
			/*
				 * disjunction can have many members; they have to be treated
				 * separately.
				 */
			//for (OWLDisjointClassesAxiom disjointClassesAxiom : ontology.getDisjointClassesAxioms(owlClass)) {
			
			        if(classAxiom.getAxiomType().equals(AxiomType.DISJOINT_CLASSES) ) {	
					Set<OWLSubClassOfAxiom> subClassesAxioms = ((OWLDisjointClassesAxiom)classAxiom).asOWLSubClassOfAxioms();
					for(OWLSubClassOfAxiom ax : subClassesAxioms){
					subClass = ax.getSubClass();
					superClass = ax.getSuperClass();
					expression = factory.getOWLObjectUnionOf(
							factory.getOWLObjectComplementOf(subClass),
							superClass).getNNF();
					Concept c1 = getConceptFromClassRecursive(subClass); 
					Concept c2 = getConceptFromClassRecursive(superClass);
					Concept c3 = getConceptFromClassRecursive(expression);
				        reasonerData.addConceptAxiom(new ConceptAxiom(increment,c1,c2,c3));
					//reasonerData.addConceptAxiom(new ConceptAxiom(increment,
					//		getConceptFromClassRecursive(subClass),
					//		getConceptFromClassRecursive(superClass),
					//		getConceptFromClassRecursive(expression)));
					increment++;
					}
				}

				/* dealing with equivalence axioms */
			
			        if(classAxiom.getAxiomType().equals(AxiomType.EQUIVALENT_CLASSES) ) {	
				Set<OWLSubClassOfAxiom> subClassOfAxioms =  ((OWLEquivalentClassesAxiom)classAxiom).asOWLSubClassOfAxioms();
				for(OWLSubClassOfAxiom ax : subClassOfAxioms) {
				subClass = ax.getSubClass();
				superClass = ax.getSuperClass();
				 
				expression = factory.getOWLObjectUnionOf(
						factory.getOWLObjectComplementOf(superClass), subClass)
						.getNNF();
			        Concept c1 = getConceptFromClassRecursive(subClass); 
				Concept c2 = getConceptFromClassRecursive(superClass);
				Concept c3 = getConceptFromClassRecursive(expression);
				reasonerData.addConceptAxiom(new ConceptAxiom(increment,c1,c2,c3));
                                }
				 
			        }
			}
	}

	/**
	 * Stores the assertions of the ontology, both for the concepts and the
	 * roles.
	 */
	private void makeAssertions() {
		OWLObjectPropertyExpression property;
		String propertyName, n1, n2;
		Concept c1, c2;
		Role r;

		for (OWLClass owlClass : classes.keySet()) {
			/* dealing with the classAssertion axioms */
			for (OWLClassAssertionAxiom classAssertionAxiom : ontology
					.getClassAssertionAxioms(owlClass)) {
				n1 = classAssertionAxiom.getClassExpression().toString();
				if (n1.equals("owl:Thing"))
					c1 = new Concept("Thing", 0, false, false);
				else
					c1 = new Concept(n1.substring(n1.indexOf("#") + 1, n1.length() - 1),
							-1, false, true);
				n2 = classAssertionAxiom.getIndividual().toStringID();
				c2 = new Concept(n2.substring(n2.indexOf("#") + 1), -1, false, true);
				c1 = reasonerData.addConcept(c1);
				c2 = reasonerData.addConcept(c2);
				reasonerData.addConceptAssertion(new ConceptAssertion(
						increment, c1, c2));
				increment++;
			}
		}

		increment = 0;
		/* dealing with the propertyAssertion axioms */
		for (OWLNamedIndividual individual : ontology
				.getIndividualsInSignature()) {
			/* the individuals contain several properties */
			for (OWLObjectPropertyAssertionAxiom propertyAssertionAxiom : ontology
					.getObjectPropertyAssertionAxioms(individual)) {
				property = propertyAssertionAxiom.getProperty();
				propertyName = property.getNamedProperty().getIRI().toString();
				r = reasonerData.giveRoleIdentifier(new ObjectRole(propertyName
						.substring(propertyName.indexOf("#") + 1), increment,
						property.isTransitive(ontology), property
								.isFunctional(ontology), false, false));
				n1 = propertyAssertionAxiom.getSubject().toStringID();
				c1 = new Concept(n1.substring(n1.indexOf("#") + 1), -1, false, true);
				n2 = propertyAssertionAxiom.getObject().toStringID();
				c2 = new Concept(n2.substring(n2.indexOf("#") + 1), -1, false, true);
				c1 = reasonerData.addConcept(c1);
				c2 = reasonerData.addConcept(c2);
				reasonerData.addRoleAssertion(new RoleAssertion(increment, r,
						c1, c2));
				increment++;
			}
		}
	}

	/**
	 * Stores the nominal singleton concepts of the ontology.
	 */
	private void makeNominalConcepts() {
		String name;
		for (OWLNamedIndividual individual : ontology
				.getIndividualsInSignature()) {
			name = individual.toStringID();
			Concept c = new Concept(name.substring(name.indexOf("#") + 1), -2, false, true);
			c = reasonerData.addConcept(c);
		}
	}

	/**
	 * Stores the axioms dealing with roles.<br/>
	 * Used by the constructor.
	 */
	private void makeRoleAxioms() {
		OWLObjectPropertyExpression subProperty=null;
		OWLObjectPropertyExpression supProperty=null;
		Role lRole=null;
		Role rRole=null;
		for (OWLObjectProperty property : ontology
				.getObjectPropertiesInSignature()) {
			for (OWLSubObjectPropertyOfAxiom axiom : ontology.getObjectSubPropertyAxiomsForSubProperty(property)) {
				
				subProperty = axiom.getSubProperty();
				supProperty = axiom.getSuperProperty();
				boolean bInv,pInv;
				if(subProperty instanceof OWLObjectInverseOf)
				   bInv = true;
			        else 
				   bInv=false;
				String name=null;
				name = subProperty.getNamedProperty().getIRI().toString();
				lRole = new Role(name.substring(name.indexOf("#") + 1), -1, 
					subProperty.isTransitive(ontology),subProperty.isFunctional(ontology), bInv, false);
				lRole=reasonerData.addRole(lRole);
				if(supProperty instanceof OWLObjectInverseOf)
				   pInv = true;
			        else 
				   pInv=false;
				name = supProperty.getNamedProperty().getIRI().toString();
				rRole = new Role(name.substring(name.indexOf("#") + 1), -1, 
			                supProperty.isTransitive(ontology), supProperty.isFunctional(ontology), pInv, false);
				rRole = reasonerData.addRole(rRole);
				reasonerData.addRoleAxiom(new RoleAxiom(increment,lRole, rRole));
						  
				increment++;
			}
		}

		for (OWLDataProperty property : ontology.getDataPropertiesInSignature()) {
			for (OWLSubDataPropertyOfAxiom axiom : ontology
					.getDataSubPropertyAxiomsForSubProperty(property)) {
				reasonerData.addRoleAxiom(new RoleAxiom(increment,
						ontologyRoles.get(axiom.getSubProperty()),
						ontologyRoles.get(axiom.getSuperProperty())));
				increment++;
			}
		}
	}

	/**
	 * Recursive function that gives a Concept based on an OWLClassExpression.
	 * Can only translate the following class expressions: SOME, ALL, UNION,
	 * INTERSECTION, COMPLEMENT, MAX, MIN and HAS_VALUE for OBJECT and DATA
	 * expressions.
	 * 
	 * @param expression
	 *            The expression to be converted.
	 * @return The conversion of the expression into a Concept.
	 */
	private Concept getConceptFromClassRecursive(OWLClassExpression expression) {
		/* expression not anonymous (terminal) */
		if (!expression.isAnonymous()) {
			String className = expression.asOWLClass().getIRI().toString();
			//Concept c  = new Concept(className.substring(className.indexOf("#") + 1),
			//		classes.get(expression), false, false);

			/*There was an BUG BUG */
			Concept c  = new Concept(className.substring(className.indexOf("#") + 1), -1, false, false);
			c = reasonerData.addConcept(c);
			return c;
		}
		/* otherwise... */
		else {
			Concept formulas[];
			ClassExpressionType expressionType = expression
					.getClassExpressionType();
			Role role=null;
			Concept concept=null;
			int i;
			OWLObjectPropertyExpression property=null;
			String propertyName=null;
			/*
			 * different members and number of members for the expression,
			 * depending of the operator
			 */
			switch (expressionType) {
			/* ALL operator */
			case DATA_ALL_VALUES_FROM:
				role = ontologyRoles.get(((OWLDataAllValuesFrom) expression)
						.getProperty());				
				concept = getConceptFromDataRange(((OWLDataAllValuesFrom) expression)
						.getFiller());

				concept = new Concept( role.getIdentifier(), Type.ALL, concept.getIdentifier());
				concept = reasonerData.addConcept(concept);
				return concept;
			case OBJECT_ALL_VALUES_FROM:
				property = ((OWLObjectAllValuesFrom) expression).getProperty();
				propertyName = property.getNamedProperty().getIRI().toString();
				if(property instanceof OWLObjectInverseOf){
				  //System.out.println("make inverse= "+ propertyName.toString() );
			          role = new Role(propertyName.substring(propertyName.indexOf("#") + 1), -1, property.isTransitive(ontology),
					property.isFunctional(ontology), true, false);
			          
				} else {
				  //System.out.println("make rec all role= "+property.toString());
		                  role = new Role(propertyName.substring(propertyName.indexOf("#") + 1), -1, property.isTransitive(ontology), property.isFunctional(ontology), false, false);
				}
				role = reasonerData.addRole(role);
				concept = getConceptFromClassRecursive(((OWLObjectAllValuesFrom) expression)
						.getFiller());
				
				concept = new Concept(role.getIdentifier(), Type.ALL, concept.getIdentifier());
				concept = reasonerData.addConcept(concept);
				return concept;
				/* COMPLEMENT operator */
			case OBJECT_COMPLEMENT_OF:
				concept = getConceptFromClassRecursive(((OWLObjectComplementOf) expression)
						.getOperand());
				concept = new Concept(Type.COMPLEMENT, concept.getIdentifier());
				concept = reasonerData.addConcept(concept);
				return concept;
				/* INTERSECTION operator */
			case OBJECT_INTERSECTION_OF:
				formulas = new Concept[((OWLObjectIntersectionOf) expression)
						.getOperands().size()];
				i = 0;
				Integer[] ids = new Integer[((OWLObjectIntersectionOf) expression)
						.getOperands().size()];
				for (OWLClassExpression operand : ((OWLObjectIntersectionOf) expression)
						.getOperands()) {
					formulas[i] = getConceptFromClassRecursive(operand);
					ids[i] = formulas[i].getIdentifier();
					i++;					
				}
				
				concept = new Concept(Type.INTERSECTION, ids);
				concept = reasonerData.addConcept(concept);
				return concept;
				/* MAX operator */
			case DATA_MAX_CARDINALITY:
				role = ontologyRoles.get(((OWLDataMaxCardinality) expression)
						.getProperty());
				concept = getConceptFromDataRange(((OWLDataMaxCardinality) expression)
						.getFiller());
				concept = new Concept( ((OWLDataMaxCardinality) expression).getCardinality(), role.getIdentifier(), Type.MAX,  concept.getIdentifier());
				concept = reasonerData.addConcept(concept);
				return concept;
			case OBJECT_MAX_CARDINALITY:
				property = ((OWLObjectMaxCardinality) expression).getProperty();
				propertyName = property.getNamedProperty().getIRI().toString();;
				if(property instanceof OWLObjectInverseOf){
			          role = new Role(propertyName.substring(propertyName.indexOf("#") + 1), -1, property.isTransitive(ontology),
					property.isFunctional(ontology), true, false);
			          
				} else 
				   role = new Role(propertyName.substring(propertyName.indexOf("#") + 1), -1, property.isTransitive(ontology),
					property.isFunctional(ontology), false, false);
				role = reasonerData.addRole(role);
				concept = getConceptFromClassRecursive(((OWLObjectMaxCardinality) expression)
						.getFiller());
				concept = new Concept( ((OWLObjectMaxCardinality) expression).getCardinality(), role.getIdentifier(),Type.MAX, concept.getIdentifier());
				concept = reasonerData.addConcept(concept);
				return concept;
				/* MIN operator */
			case DATA_MIN_CARDINALITY:
				role = ontologyRoles.get(((OWLDataMinCardinality) expression)
						.getProperty());
				concept = getConceptFromDataRange(((OWLDataMinCardinality) expression)
						.getFiller());
				concept = new Concept( ((OWLDataMinCardinality) expression).getCardinality(), role.getIdentifier(), Type.MIN, concept.getIdentifier());
				concept = reasonerData.addConcept(concept);
				return concept;
			case OBJECT_MIN_CARDINALITY:
				property = ((OWLObjectMinCardinality) expression).getProperty();
				propertyName =  property.getNamedProperty().getIRI().toString();;
				if(property instanceof OWLObjectInverseOf){
			          role = new Role(propertyName.substring(propertyName.indexOf("#") + 1), -1, property.isTransitive(ontology),
					property.isFunctional(ontology), true, false);
			          
				} else 
				  role = new Role(propertyName.substring(propertyName.indexOf("#") + 1), -1, property.isTransitive(ontology),
					property.isFunctional(ontology), false, false);
				role = reasonerData.addRole(role);
				concept = getConceptFromClassRecursive(((OWLObjectMinCardinality) expression)
						.getFiller());
				concept = new Concept( ((OWLObjectMinCardinality) expression).getCardinality(), role.getIdentifier(), Type.MIN, concept.getIdentifier());
				concept = reasonerData.addConcept(concept);
				return concept;
				/* SOME operator */
			case DATA_SOME_VALUES_FROM:
				role = ontologyRoles.get(((OWLDataSomeValuesFrom) expression)
						.getProperty());
				concept = getConceptFromDataRange(((OWLDataSomeValuesFrom) expression)
						.getFiller());
				concept = new Concept(role.getIdentifier(), Type.SOME, concept.getIdentifier());
				concept = reasonerData.addConcept(concept);
				return concept;
			case OBJECT_SOME_VALUES_FROM:
			        property = ((OWLObjectSomeValuesFrom) expression).getProperty();
				propertyName = property.getNamedProperty().getIRI().toString();;
				if(property instanceof OWLObjectInverseOf){
			          //System.out.println("make inverse = "+ propertyName);
			          role = new Role(propertyName.substring(propertyName.indexOf("#") + 1), -1, property.isTransitive(ontology),
					property.isFunctional(ontology), true, false);
			          
				} else 
				   role = new Role(propertyName.substring(propertyName.indexOf("#") + 1), -1, property.isTransitive(ontology),
					property.isFunctional(ontology), false, false);
				role = reasonerData.addRole(role);
				concept = getConceptFromClassRecursive(((OWLObjectSomeValuesFrom) expression)
						.getFiller());
				
				concept = new Concept(role.getIdentifier(), Type.SOME, concept.getIdentifier());
				concept = reasonerData.addConcept(concept);
				return concept;
				/* UNION operator */
			case OBJECT_UNION_OF:
				formulas = new Concept[((OWLObjectUnionOf) expression)
						.getOperands().size()];
				i = 0;
				Integer[] ids2 = new Integer[((OWLObjectUnionOf) expression)
						.getOperands().size()];
				for (OWLClassExpression operand : ((OWLObjectUnionOf) expression)
						.getOperands()) {
					formulas[i] = getConceptFromClassRecursive(operand);
					ids2[i] = formulas[i].getIdentifier();
					i++;
				}
				 
				concept = new Concept(Type.UNION, ids2);
				concept = reasonerData.addConcept(concept);
				return concept;
				/* treated for the default example */
			case DATA_HAS_VALUE:
				return getConceptFromClassRecursive(((OWLDataHasValue) expression)
						.asSomeValuesFrom());
			case OBJECT_HAS_VALUE:
				return getConceptFromClassRecursive(((OWLObjectHasValue) expression)
						.asSomeValuesFrom());
			case OBJECT_ONE_OF:
				String name;
				formulas = new Concept[((OWLObjectOneOf) expression)
						.getIndividuals().size()];
				i = 0;
				Integer[] ids3 = new Integer[((OWLObjectOneOf) expression)
						.getIndividuals().size()];
				for (OWLIndividual individual : ((OWLObjectOneOf) expression)
						.getIndividuals()) {
					name = individual.asOWLNamedIndividual().getIRI()
							.toString();
					concept = new Concept(name.substring(name.indexOf("#") + 1), -2, false,true);
					concept = reasonerData.addConcept(concept);
					formulas[i] = concept;
					ids3[i] = concept.getIdentifier();
					i++;
				}

				concept = new Concept(ids3);
				concept = reasonerData.addConcept(concept);
				return concept;
				/* all other operators are not treated */
			default:
				return null;
			}
		}
	}

	/**
	 * Gives a concept based on a data property range.<br/>
	 * Used by makeConceptFromClassRecursive.
	 * 
	 * @param range
	 *            The range of the property.
	 * @return The concept created.
	 */
	private Concept getConceptFromDataRange(OWLDataRange range) {
		String typeName = range.asOWLDatatype().getIRI().toString();
		Concept c = new Concept(typeName.substring(typeName.indexOf("#") + 1),
				datatypes.get(range.asOWLDatatype()), true, false);
		reasonerData.addConcept(c);
		return c;
	}

	/**
	 * A getter for the concepts.
	 * 
	 * @return Gives the classes of the ontology.
	 */
	public Map<OWLClass, Integer> getClasses() {
		return classes;
	}

	/**
	 * A getter for the datatypes.
	 * 
	 * @return Gives the datatypes of the ontology.
	 */
	public Map<OWLDatatype, Integer> getDatatypes() {
		return datatypes;
	}

	/**
	 * A getter for the roles of the ontology. The collection returned will
	 * contain only the roles of the ontology, without any addition.
	 * 
	 * @return A collection of the roles of the ontology.
	 */
	public Collection<Role> getStandardRoles() {
		return ontologyRoles.values();
	}

	/**
	 * A getter for the data of the ontology.
	 * 
	 * @return The data of the ontology.
	 */
	public ReasonerData getData() {
		return reasonerData;
	}
}
