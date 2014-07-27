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
import org.semanticweb.owlapi.model.OWLClass;
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

		increment = 1;
		this.computeClasses();
		this.computeDatatypes();
		//reasonerData.initConceptMap(classes);
		increment = 0;
		this.computeProperties();
		increment = 0;
		this.makeConceptsFromSubClasses();
		this.makeConceptsFromPropertyDomains();
		this.makeConceptsFromPropertyRanges();
		this.makeNominalConcepts();
		this.makeConceptFromFunctional();
		increment = 0;
		this.makeAssertions();
		increment = 0;
		this.makeRoleAxioms();
		 
		ConceptLabel allNNF = new ConceptLabel();
		for(ConceptAxiom i : reasonerData.getConceptAxioms().values() ) {
		    //each NNF is identified. This may be already done
		    Concept c = i.getNNF();
		    reasonerData.addConcept( c );
		    reasonerData.getAxiomNNFs().add( new Integer(c.getIdentifier()) );	
		    allNNF.add( new Integer(c.getIdentifier()) ); 
		}
		reasonerData.addCore(allNNF);
		reasonerData.setNNFConceptLabel(allNNF.getIdentifier());
		reasonerData.setTransitiveClosure(new TransitiveClosureOfRoleHierarchy(
				reasonerData.getRoleAxioms().values(), getStandardRoles()));
	}

	//compute all subconcepts of "concept"
	//
	public Set<Concept> subconcepts(Concept concept){
	       Set<Concept> closure = new HashSet<Concept>();

	       if ( concept.isTerminal() ) {
		    closure.add(concept);
		    return closure;
	       } else {
			closure.add( concept );
			switch (concept.getOperator()) {
			case INTERSECTION:
				for (Concept child : concept.getChildren()) { 
			             closure.addAll( subconcepts (child) );
				}
				return closure;  
			case UNION:
				closure.addAll( subconcepts (concept.getChildren().get(0)) );
				closure.addAll( subconcepts (concept.getChildren().get(1)) );
				return closure; 
			case SOME:
				closure.addAll( subconcepts ( concept.getChildren().get(0)) );
				return closure; 
			case ALL:
				closure.addAll( subconcepts ( concept.getChildren().get(0) ) );
				return closure; 
			case MIN:
				closure.addAll( subconcepts ( concept.getChildren().get(0) ) );
				return closure;
			case MAX:
				closure.addAll( subconcepts ( concept.getChildren().get(0) ) ) ;
				return closure;
			case COMPLEMENT:
				closure.addAll( subconcepts ( concept.getChildren().get(0) ) );
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
			reasonerData.addRole(role);
			increment++;
		}

		/* DataProperties */
		for (OWLDataProperty property : ontology.getDataPropertiesInSignature()) {
			propertyName = property.getIRI().toString();
			role = new DataRole(propertyName.substring(propertyName
					.indexOf("#") + 1), increment, false,
					property.isFunctional(ontology), false, false);
			ontologyRoles.put(property, role);
			reasonerData.addRole(role);
			increment++;
		}

		this.addInverseProperties();
		this.addTransitiveClosures();
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
			reasonerData.addRole(new Role(property.toString().substring(
					property.toString().indexOf("#") + 1,
					property.toString().length() - 1), increment, property
					.isTransitive(ontology), property.isFunctional(ontology),
					true, false));
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
	private void addTransitiveClosures() {
		List<Role> toAdd = new ArrayList<Role>();

		for (Role role : reasonerData.getRoles().values()) {
			toAdd.add(new Role(role.getName(), increment, true, false, role
					.isInverse(), true));
			increment++;
		}

		reasonerData.addRoles(toAdd);
	}

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

		/* OBJECT */
		for (OWLObjectProperty property : ontology
				.getObjectPropertiesInSignature()) {
			for (OWLObjectPropertyDomainAxiom axiom : ontology
					.getObjectPropertyDomainAxioms(property)) {
				left = reasonerData
						.giveConceptIdentifier(new Concept(Type.SOME,
								ontologyRoles.get(axiom.getProperty()), top));
				right = getConceptFromClassRecursive(axiom.getDomain().getNNF());
				nnf = reasonerData.giveConceptIdentifier(new Concept(
						Type.UNION, Concept.negate(left, reasonerData), right));
				reasonerData.addConcept(left);
				reasonerData.addConceptAxiom(new ConceptAxiom(increment, left,
						right, nnf));
				increment++;
			}
		}
		/* DATA */
		for (OWLDataProperty property : ontology.getDataPropertiesInSignature()) {
			for (OWLDataPropertyDomainAxiom axiom : ontology
					.getDataPropertyDomainAxioms(property)) {
				left = reasonerData
						.giveConceptIdentifier(new Concept(Type.SOME,
								ontologyRoles.get(axiom.getProperty()), top));
				right = getConceptFromClassRecursive(axiom.getDomain().getNNF());
				nnf = reasonerData.giveConceptIdentifier(new Concept(
						Type.UNION, Concept.negate(left, reasonerData), right));
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

		/* OBJECT */
		for (OWLObjectProperty property : ontology
				.getObjectPropertiesInSignature()) {
			for (OWLObjectPropertyRangeAxiom axiom : ontology
					.getObjectPropertyRangeAxioms(property)) {
				left = reasonerData.giveConceptIdentifier(new Concept(
						Type.SOME, reasonerData.getInverseOfRole(ontologyRoles.get(axiom.getProperty()), reasonerData), 
  						           new Concept("Thing", 0, false, false)));
				right = getConceptFromClassRecursive(axiom.getRange().getNNF());
				nnf = reasonerData.giveConceptIdentifier(new Concept(
						Type.UNION, Concept.negate(left, reasonerData), right));
				reasonerData.addConcept(left);
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
				left = reasonerData.giveConceptIdentifier(new Concept(
						Type.SOME, ontologyRoles.get(property), top));
				right = reasonerData.giveConceptIdentifier(new Concept(
						Type.MAX, 1, ontologyRoles.get(property), top));
				nnf = reasonerData.giveConceptIdentifier(new Concept(
						Type.UNION, Concept.negate(left, reasonerData), right));
				reasonerData.addConcept(left);
				reasonerData.addConcept(right);
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
		OWLClassExpression expression, subClass = null, superClass = null;

		for (OWLClass owlClass : classes.keySet()) {
			/* dealing with subClass axioms */
			for (OWLSubClassOfAxiom subClassOfAxiom : ontology
					.getSubClassAxiomsForSubClass(owlClass)) {
				subClass = subClassOfAxiom.getSubClass();
				superClass = subClassOfAxiom.getSuperClass();
				expression = factory.getOWLObjectUnionOf(
						factory.getOWLObjectComplementOf(subClass), superClass)
						.getNNF();
				reasonerData.addConceptAxiom(new ConceptAxiom(increment,
						getConceptFromClassRecursive(subClass),
						getConceptFromClassRecursive(superClass),
						getConceptFromClassRecursive(expression)));
				increment++;
			}

			/* dealing with the disjoint axioms */
			for (OWLDisjointClassesAxiom disjointClassesAxiom : ontology
					.getDisjointClassesAxioms(owlClass)) {
				/*
				 * disjunction can have many members; they have to be treated
				 * separately.
				 */
				for (OWLSubClassOfAxiom subClassOfAxiom : disjointClassesAxiom
						.asOWLSubClassOfAxioms()) {
					subClass = subClassOfAxiom.getSubClass();
					superClass = subClassOfAxiom.getSuperClass();
					expression = factory.getOWLObjectUnionOf(
							factory.getOWLObjectComplementOf(subClass),
							superClass).getNNF();
					reasonerData.addConceptAxiom(new ConceptAxiom(increment,
							getConceptFromClassRecursive(subClass),
							getConceptFromClassRecursive(superClass),
							getConceptFromClassRecursive(expression)));
					increment++;
				}
			}

			/* dealing with equivalence axioms */
			for (OWLEquivalentClassesAxiom equivalentClassesAxiom : ontology
					.getEquivalentClassesAxioms(owlClass)) {
				int i = 0;

				// get the classes in the axiom (only 2)
				for (OWLClass c : equivalentClassesAxiom.getNamedClasses()) {
					if (i == 0)
						subClass = c;
					else
						superClass = c;
					i++;
					if (i == 2)
						break;
				}

				expression = factory.getOWLObjectUnionOf(
						factory.getOWLObjectComplementOf(subClass), superClass)
						.getNNF();
				reasonerData.addConceptAxiom(new ConceptAxiom(increment,
						getConceptFromClassRecursive(subClass),
						getConceptFromClassRecursive(superClass),
						getConceptFromClassRecursive(expression)));

				expression = factory.getOWLObjectUnionOf(
						factory.getOWLObjectComplementOf(superClass), subClass)
						.getNNF();
				reasonerData.addConceptAxiom(new ConceptAxiom(increment,
						getConceptFromClassRecursive(superClass),
						getConceptFromClassRecursive(subClass),
						getConceptFromClassRecursive(expression)));
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
					c1 = reasonerData.giveConceptIdentifier(new Concept(n1
							.substring(n1.indexOf("#") + 1, n1.length() - 1),
							-1, false, true));
				n2 = classAssertionAxiom.getIndividual().toStringID();
				c2 = reasonerData.giveConceptIdentifier(new Concept(n2
						.substring(n2.indexOf("#") + 1), -1, false, true));
				reasonerData.addConcept(c1);
				reasonerData.addConcept(c2);
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
				c1 = reasonerData.giveConceptIdentifier(new Concept(n1
						.substring(n1.indexOf("#") + 1), -1, false, true));
				n2 = propertyAssertionAxiom.getObject().toStringID();
				c2 = reasonerData.giveConceptIdentifier(new Concept(n2
						.substring(n2.indexOf("#") + 1), -1, false, true));
				reasonerData.addConcept(c1);
				reasonerData.addConcept(c2);
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
			reasonerData.addConcept(reasonerData
					.giveConceptIdentifier(new Concept(name.substring(name
							.indexOf("#") + 1), -2, false, true)));
		}
	}

	/**
	 * Stores the axioms dealing with roles.<br/>
	 * Used by the constructor.
	 */
	private void makeRoleAxioms() {

		for (OWLObjectProperty property : ontology
				.getObjectPropertiesInSignature()) {
			for (OWLSubObjectPropertyOfAxiom axiom : ontology
					.getObjectSubPropertyAxiomsForSubProperty(property)) {
				reasonerData.addRoleAxiom(new RoleAxiom(increment,
						ontologyRoles.get(axiom.getSubProperty()),
						ontologyRoles.get(axiom.getSuperProperty())));
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
			return new Concept(className.substring(className.indexOf("#") + 1),
					classes.get(expression), false, false);
		}
		/* otherwise... */
		else {
			Concept formulas[];
			ClassExpressionType expressionType = expression
					.getClassExpressionType();
			Role role;
			Concept concept;
			int i;

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
				concept = reasonerData.giveConceptIdentifier(new Concept(
						Type.ALL, role, concept));
				reasonerData.addConcept(concept);
				return concept;
			case OBJECT_ALL_VALUES_FROM:
				role = ontologyRoles.get(((OWLObjectAllValuesFrom) expression)
						.getProperty());
				concept = getConceptFromClassRecursive(((OWLObjectAllValuesFrom) expression)
						.getFiller());
				concept = reasonerData.giveConceptIdentifier(new Concept(
						Type.ALL, role, concept));
				reasonerData.addConcept(concept);
				return concept;
				/* COMPLEMENT operator */
			case OBJECT_COMPLEMENT_OF:
				concept = getConceptFromClassRecursive(((OWLObjectComplementOf) expression)
						.getOperand());
				concept = reasonerData.giveConceptIdentifier(new Concept(
						Type.COMPLEMENT, concept));
				reasonerData.addConcept(concept);
				return concept;
				/* INTERSECTION operator */
			case OBJECT_INTERSECTION_OF:
				formulas = new Concept[((OWLObjectIntersectionOf) expression)
						.getOperands().size()];
				i = 0;
				for (OWLClassExpression operand : ((OWLObjectIntersectionOf) expression)
						.getOperands()) {
					formulas[i] = getConceptFromClassRecursive(operand);
					i++;
				}
				concept = reasonerData.giveConceptIdentifier(new Concept(
						Type.INTERSECTION, formulas));
				reasonerData.addConcept(concept);
				return concept;
				/* MAX operator */
			case DATA_MAX_CARDINALITY:
				role = ontologyRoles.get(((OWLDataMaxCardinality) expression)
						.getProperty());
				concept = getConceptFromDataRange(((OWLDataMaxCardinality) expression)
						.getFiller());
				concept = reasonerData.giveConceptIdentifier(new Concept(
						Type.MAX, ((OWLDataMaxCardinality) expression)
								.getCardinality(), role, concept));
				reasonerData.addConcept(concept);
				return concept;
			case OBJECT_MAX_CARDINALITY:
				role = ontologyRoles.get(((OWLObjectMaxCardinality) expression)
						.getProperty());
				concept = getConceptFromClassRecursive(((OWLObjectMaxCardinality) expression)
						.getFiller());
				concept = reasonerData.giveConceptIdentifier(new Concept(
						Type.MAX, ((OWLObjectMaxCardinality) expression)
								.getCardinality(), role, concept));
				reasonerData.addConcept(concept);
				return concept;
				/* MIN operator */
			case DATA_MIN_CARDINALITY:
				role = ontologyRoles.get(((OWLDataMinCardinality) expression)
						.getProperty());
				concept = getConceptFromDataRange(((OWLDataMinCardinality) expression)
						.getFiller());
				concept = reasonerData.giveConceptIdentifier(new Concept(
						Type.MIN, ((OWLDataMinCardinality) expression)
								.getCardinality(), role, concept));
				reasonerData.addConcept(concept);
				return concept;
			case OBJECT_MIN_CARDINALITY:
				role = ontologyRoles.get(((OWLObjectMinCardinality) expression)
						.getProperty());
				concept = getConceptFromClassRecursive(((OWLObjectMinCardinality) expression)
						.getFiller());
				concept = reasonerData.giveConceptIdentifier(new Concept(
						Type.MIN, ((OWLObjectMinCardinality) expression)
								.getCardinality(), role, concept));
				reasonerData.addConcept(concept);
				return concept;
				/* SOME operator */
			case DATA_SOME_VALUES_FROM:
				role = ontologyRoles.get(((OWLDataSomeValuesFrom) expression)
						.getProperty());
				concept = getConceptFromDataRange(((OWLDataSomeValuesFrom) expression)
						.getFiller());
				concept = reasonerData.giveConceptIdentifier(new Concept(
						Type.SOME, role, concept));
				reasonerData.addConcept(concept);
				return concept;
			case OBJECT_SOME_VALUES_FROM:
				role = ontologyRoles.get(((OWLObjectSomeValuesFrom) expression)
						.getProperty());
				concept = getConceptFromClassRecursive(((OWLObjectSomeValuesFrom) expression)
						.getFiller());
				concept = reasonerData.giveConceptIdentifier(new Concept(
						Type.SOME, role, concept));
				reasonerData.addConcept(concept);
				return concept;
				/* UNION operator */
			case OBJECT_UNION_OF:
				formulas = new Concept[((OWLObjectUnionOf) expression)
						.getOperands().size()];
				i = 0;
				for (OWLClassExpression operand : ((OWLObjectUnionOf) expression)
						.getOperands()) {
					formulas[i] = getConceptFromClassRecursive(operand);
					i++;
				}
				concept = reasonerData.giveConceptIdentifier(new Concept(
						Type.UNION, formulas));
				reasonerData.addConcept(concept);
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

				for (OWLIndividual individual : ((OWLObjectOneOf) expression)
						.getIndividuals()) {
					name = individual.asOWLNamedIndividual().getIRI()
							.toString();
					concept = reasonerData.giveConceptIdentifier(new Concept(
							name.substring(name.indexOf("#") + 1), -2, false,
							true));
					reasonerData.addConcept(concept);
					formulas[i] = concept;
					i++;
				}

				concept = reasonerData.giveConceptIdentifier(new Concept(
						formulas));
				reasonerData.addConcept(concept);
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

		return new Concept(typeName.substring(typeName.indexOf("#") + 1),
				datatypes.get(range.asOWLDatatype()), true, false);
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
