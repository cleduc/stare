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
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
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
	 * The concepts (classes) in the ontology. Thing is at the position 0,
	 * Nothing is at the position 1
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

		increment = 2;
		this.computeClasses();
		this.computeDatatypes();
		increment = 0;
		this.computeProperties();
		increment = 0;
		this.makeConceptsFromSubClasses();
		this.makeConceptsFromPropertyDomains();
		this.makeConceptsFromPropertyRanges();
		increment = 0;
		this.makeRoleAxioms();

		reasonerData.initConceptMap(classes);
		reasonerData.setTransitiveClosure(new TransitiveClosureOfRoleHierarchy(
				reasonerData.getRoleAxioms().values(), getStandardRoles()));
	}

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

		this.classes.put(new OWLClassImpl(IRI.create("owl:Thing")), 0);
		this.classes.put(new OWLClassImpl(IRI.create("owl:Nohing")), 1);
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
					.indexOf("#") + 1), increment, property
					.isTransitive(ontology), property.isFunctional(ontology),
					false, false);
			ontologyRoles.put(property, role);
			reasonerData.addRole(role);
			increment++;
		}

		/* DataProperties */
		for (OWLDataProperty property : ontology.getDataPropertiesInSignature()) {
			propertyName = property.getIRI().toString();
			role = new DataRole(propertyName.substring(propertyName
					.indexOf("#") + 1), increment, false, property
					.isFunctional(ontology), false, false);
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
		OWLObjectPropertyExpression expression;

		for (OWLObjectProperty property : ontology
				.getObjectPropertiesInSignature()) {
			// if role has inverses
			if (!property.getInverses(ontology).isEmpty()) {
				expression = property.getInverses(ontology).iterator().next();
				reasonerData.addRole(new Role(expression.toString().substring(
						expression.toString().indexOf("#") + 1,
						expression.toString().length() - 1), increment, expression
						.isTransitive(ontology), expression
						.isFunctional(ontology), true, false));
			}
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
			if (role.isTransitive) {
				toAdd.add(new Role(role.getName(), increment, true, false, role
						.isInverse(), true));
				increment++;
			}
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

		/* OBJECT */
		for (OWLObjectProperty property : ontology
				.getObjectPropertiesInSignature()) {
			for (OWLObjectPropertyDomainAxiom axiom : ontology
					.getObjectPropertyDomainAxioms(property)) {
				left = new Concept(Type.SOME, ontologyRoles.get(axiom
						.getProperty()), new Concept("owl:Thing", 0, false));
				right = getConceptFromClassRecursive(axiom.getDomain().getNNF());
				nnf = new Concept(Type.UNION, Concept.negate(left), right);
				reasonerData.addConceptAxiom(new ConceptAxiom(increment, left,
						right, nnf));
				increment++;
			}
		}
		/* DATA */
		for (OWLDataProperty property : ontology.getDataPropertiesInSignature()) {
			for (OWLDataPropertyDomainAxiom axiom : ontology
					.getDataPropertyDomainAxioms(property)) {
				left = new Concept(Type.SOME, ontologyRoles.get(axiom
						.getProperty()), new Concept("owl:Thing", 0, false));
				right = getConceptFromClassRecursive(axiom.getDomain().getNNF());
				nnf = new Concept(Type.UNION, Concept.negate(left), right);
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
				if (axiom.getProperty().getInverses(ontology).size() == 0)
					System.out.println("Can't find an inverse for "
							+ axiom.getProperty()
							+ ", concept from range cannot be created.");
				else {
					left = new Concept(Type.SOME, ontologyRoles.get(axiom
							.getProperty().getInverseProperty()), new Concept(
							"owl:Thing", 0, false));
					right = getConceptFromClassRecursive(axiom.getRange()
							.getNNF());
					nnf = new Concept(Type.UNION, Concept.negate(left), right);
					reasonerData.addConceptAxiom(new ConceptAxiom(increment,
							left, right, nnf));
					increment++;
				}
			}
		}

		/* DATA */
		/*
		 * for (OWLDataProperty property :
		 * ontology.getDataPropertiesInSignature()) { for
		 * (OWLDataPropertyRangeAxiom axiom : ontology
		 * .getDataPropertyRangeAxioms(property)) { left = new
		 * Concept(Type.SOME, roles.get(axiom.getProperty()
		 * .getInverseProperty()), new Concept("owl:Thing", 0, false)); right =
		 * makeConceptFromRange(axiom.getRange()); nnf = new Concept(Type.UNION,
		 * Concept.negate(left), right); dataOntology.addConceptAxiom(new
		 * ConceptAxiom(increment, left, right, nnf)); increment++; } }
		 */
	}

	/**
	 * Stores the axioms dealing with concepts.<br/>
	 * Used by the constructor.
	 */
	private void makeConceptsFromSubClasses() {
		OWLDataFactory factory = new OWLDataFactoryImpl();
		OWLClassExpression expression, subClass, superClass;

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

			/* dealing with the classAssertion axioms */
			for (OWLClassAssertionAxiom classAssertionAxiom : ontology
					.getClassAssertionAxioms(owlClass)) {
				subClass = classAssertionAxiom.asOWLSubClassOfAxiom()
						.getSubClass();
				superClass = owlClass;
				expression = factory.getOWLObjectUnionOf(
						factory.getOWLObjectComplementOf(subClass), superClass)
						.getNNF();
				reasonerData.addConceptAxiom(new ConceptAxiom(increment,
						getConceptFromClassRecursive(subClass),
						getConceptFromClassRecursive(superClass),
						getConceptFromClassRecursive(expression)));
				increment++;
			}

			/* dealing with the propertyAssertion axioms */
			// WARNING: CANNOT BE TESTED WITH THE DEFAULT IRI TEST
			for (OWLIndividual individual : owlClass.getIndividuals(ontology)) {
				/* the individuals contain several properties */
				for (OWLObjectPropertyAssertionAxiom propertyAssertionAxiom : ontology
						.getObjectPropertyAssertionAxioms(individual)) {
					subClass = propertyAssertionAxiom.asOWLSubClassOfAxiom()
							.getSubClass();
					superClass = propertyAssertionAxiom.asOWLSubClassOfAxiom()
							.getSuperClass();
					expression = factory.getOWLObjectUnionOf(
							factory.getOWLObjectComplementOf(subClass),
							superClass).getNNF();
					reasonerData.addConceptAxiom(new ConceptAxiom(increment,
							getConceptFromClassRecursive(subClass),
							getConceptFromClassRecursive(superClass),
							getConceptFromClassRecursive(expression)));
					increment++;
				}
				for (OWLDataPropertyAssertionAxiom propertyAssertionAxiom : ontology
						.getDataPropertyAssertionAxioms(individual)) {
					subClass = propertyAssertionAxiom.asOWLSubClassOfAxiom()
							.getSubClass();
					superClass = propertyAssertionAxiom.asOWLSubClassOfAxiom()
							.getSuperClass();
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
					classes.get(expression), false);
		}
		/* otherwise... */
		else {
			Concept formulas[];
			ClassExpressionType expressionType = expression
					.getClassExpressionType();
			Role role;
			Concept formula;
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
				formula = getConceptFromDataRange(((OWLDataAllValuesFrom) expression)
						.getFiller());

				return new Concept(Type.ALL, role, formula);
			case OBJECT_ALL_VALUES_FROM:
				role = ontologyRoles.get(((OWLObjectAllValuesFrom) expression)
						.getProperty());
				formula = getConceptFromClassRecursive(((OWLObjectAllValuesFrom) expression)
						.getFiller());
				return new Concept(Type.ALL, role, formula);
				/* COMPLEMENT operator */
			case OBJECT_COMPLEMENT_OF:
				formula = getConceptFromClassRecursive(((OWLObjectComplementOf) expression)
						.getOperand());
				return new Concept(Type.COMPLEMENT, formula);
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
				return new Concept(Type.INTERSECTION, formulas);
				/* MAX operator */
			case DATA_MAX_CARDINALITY:
				role = ontologyRoles.get(((OWLDataMaxCardinality) expression)
						.getProperty());
				formula = getConceptFromDataRange(((OWLDataMaxCardinality) expression)
						.getFiller());
				return new Concept(Type.MAX,
						((OWLDataMaxCardinality) expression).getCardinality(),
						role, formula);
			case OBJECT_MAX_CARDINALITY:
				role = ontologyRoles.get(((OWLObjectMaxCardinality) expression)
						.getProperty());
				formula = getConceptFromClassRecursive(((OWLObjectMaxCardinality) expression)
						.getFiller());
				return new Concept(
						Type.MAX,
						((OWLObjectMaxCardinality) expression).getCardinality(),
						role, formula);
				/* MIN operator */
			case DATA_MIN_CARDINALITY:
				role = ontologyRoles.get(((OWLDataMinCardinality) expression)
						.getProperty());
				formula = getConceptFromDataRange(((OWLDataMinCardinality) expression)
						.getFiller());
				return new Concept(Type.MIN,
						((OWLDataMinCardinality) expression).getCardinality(),
						role, formula);
			case OBJECT_MIN_CARDINALITY:
				role = ontologyRoles.get(((OWLObjectMinCardinality) expression)
						.getProperty());
				formula = getConceptFromClassRecursive(((OWLObjectMinCardinality) expression)
						.getFiller());
				return new Concept(
						Type.MIN,
						((OWLObjectMinCardinality) expression).getCardinality(),
						role, formula);
				/* SOME operator */
			case DATA_SOME_VALUES_FROM:
				role = ontologyRoles.get(((OWLDataSomeValuesFrom) expression)
						.getProperty());
				formula = getConceptFromDataRange(((OWLDataSomeValuesFrom) expression)
						.getFiller());
				return new Concept(Type.SOME, role, formula);
			case OBJECT_SOME_VALUES_FROM:
				role = ontologyRoles.get(((OWLObjectSomeValuesFrom) expression)
						.getProperty());
				formula = getConceptFromClassRecursive(((OWLObjectSomeValuesFrom) expression)
						.getFiller());
				return new Concept(Type.SOME, role, formula);
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
				return new Concept(Type.UNION, formulas);
				/* treated for the default example */
			case DATA_HAS_VALUE:
				return getConceptFromClassRecursive(((OWLDataHasValue) expression)
						.asSomeValuesFrom());
			case OBJECT_HAS_VALUE:
				return getConceptFromClassRecursive(((OWLObjectHasValue) expression)
						.asSomeValuesFrom());
				/* WARNING: should be treated as an OBJECT_COMPLEMENT_OF */
			case OBJECT_ONE_OF:
				/* all other operators are not treated */
			default:
				// System.out.println(expressionType
				// + " not managed by the program.");
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
				datatypes.get(range.asOWLDatatype()), true);
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
