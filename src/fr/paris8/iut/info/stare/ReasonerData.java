package fr.paris8.iut.info.stare;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.semanticweb.owlapi.model.OWLClass;

import fr.paris8.iut.info.stare.Concept.Type;

public class ReasonerData {
	/** The roles in the ontology AND those we created */
	private Map<Integer, Role> roles;
	/** The concepts of the ontology, the terminals, and those created */
	private Map<Integer, Concept> concepts;
	/** The concept axioms, once they have been transformed */
	private Map<Integer, ConceptAxiom> conceptAxioms;
	/** The role axioms, once they have been transformed */
	private Map<Integer, RoleAxiom> roleAxioms;
	/** Representation of the transitive closure of the ontology */
	private TransitiveClosureOfRoleHierarchy transitiveClosure;
	/**
	 * Map containing the different cores created, with their identifier as a
	 * key
	 */
	private static Map<Integer, ConceptLabel> cores;
	/**
	 * Map containing the different edges of ray created, with their identifier
	 * as a key
	 */
	private static Map<Integer, RoleLabel> edges;

	/**
	 * The default constructor.
	 */
	public ReasonerData() {
		transitiveClosure = null;
		roles = new HashMap<Integer, Role>();
		edges = new HashMap<Integer, RoleLabel>();
		concepts = new HashMap<Integer, Concept>();
		cores = new HashMap<Integer, ConceptLabel>();
		roleAxioms = new HashMap<Integer, RoleAxiom>();
		conceptAxioms = new HashMap<Integer, ConceptAxiom>();
	}

	/**
	 * Initialization of the map of concepts.
	 * 
	 * @param classes
	 * 
	 */
	public void initConceptMap(Map<OWLClass, Integer> classes) {
		String className;
		int nextIndex = classes.size();

		for (OWLClass owlClass : classes.keySet()) {
			className = owlClass.getIRI().toString();
			concepts.put(classes.get(owlClass),
					new Concept(
							className.substring(className.indexOf("#") + 1),
							classes.get(owlClass), false));
		}

		for (ConceptAxiom axiom : conceptAxioms.values())
			concepts.put(nextIndex, axiom.getNNF());
	}

	/**
	 * Trans function.
	 * 
	 * @param role
	 *            The role to be tested.
	 * @return true if the role has a transitive sub-role.
	 */
	public boolean Trans(Role role) {
		for (RoleAxiom axiom : transitiveClosure.getTransitiveClosure())
			if (axiom.getRightRole().equals(role))
				if (axiom.getLeftRole().isTransitive())
					return true;
		return false;
	}

	/**
	 * A setter for the transitive closure.
	 * 
	 * @param transitiveClosure
	 *            The transitive closure of the ontology.
	 */
	public void setTransitiveClosure(
			TransitiveClosureOfRoleHierarchy transitiveClosure) {
		this.transitiveClosure = transitiveClosure;
	}

	/**
	 * A getter for the transitive closure.
	 * 
	 * @return the transitive closure of the ontology, or null if it has not
	 *         been set.
	 */
	public Collection<RoleAxiom> getTransitiveClosure() {
		return transitiveClosure.getTransitiveClosure();
	}

	/**
	 * Adds a role axiom to the data of the ontology.
	 * 
	 * @param roleAxiom
	 *            The role axiom to be added.
	 */
	public void addRoleAxiom(RoleAxiom roleAxiom) {
		roleAxioms.put(roleAxiom.getIdentifier(), roleAxiom);
	}

	/**
	 * A getter for the role axioms.
	 * 
	 * @return The list of the role axioms of the ontology.
	 */
	public Map<Integer, RoleAxiom> getRoleAxioms() {
		return roleAxioms;
	}

	/**
	 * Adds a role to the map of roles.
	 * 
	 * @param role
	 *            The role to be added.
	 * @return true if the role has been added, false if it was already in the
	 *         map.
	 */
	public boolean addRole(Role role) {
		if (!roles.containsValue(role)) {
			roles.put(role.getIdentifier(), role);
			return true;
		} else
			return false;
	}

	/**
	 * Adds a list of Role to the map of Role.
	 * 
	 * @param roles
	 *            The list to be added.
	 */
	public void addRoles(List<Role> roles) {
		roles.addAll(roles);
	}

	/**
	 * A getter to return the roles. The list returned will contained both the
	 * roles of the ontology, and those we created (inverses and closures).
	 * 
	 * @return Gives the properties of the ontology.
	 */
	public Map<Integer, Role> getRoles() {
		return roles;
	}

	/**
	 * Gives the next index for a new Role to be inserted in the map.
	 * 
	 * @return the key for the next Role to be added.
	 */
	public int nextIndexOfRole() {
		return roles.size();
	}

	/**
	 * Adds a ConceptAxiom to the map of ConceptAxiom.
	 * 
	 * @param conceptAxiom
	 *            The ConceptAxiom to be added.
	 */
	public void addConceptAxiom(ConceptAxiom conceptAxiom) {
		conceptAxioms.put(conceptAxiom.getIdentifier(), conceptAxiom);
	}

	/**
	 * A getter for the concept axioms.
	 * 
	 * @return The map of ConceptAxiom of the ontology.
	 */
	public Map<Integer, ConceptAxiom> getConceptAxioms() {
		return conceptAxioms;
	}

	/**
	 * Adds a Concept to the map of Concept.
	 * 
	 * @param concept
	 *            The Concept to be added.
	 * @return true if the concept has been added, false if it was already in
	 *         the map.
	 */
	public boolean addConcept(Concept concept) {
		if (!concepts.containsValue(concept)) {
			concepts.put(concept.getIdentifier(), concept);
			return true;
		} else
			return false;
	}

	/**
	 * A getter for the concepts.
	 * 
	 * @return The map of Concept of the ontology.
	 */
	public Map<Integer, Concept> getConcept() {
		return concepts;
	}

	/**
	 * Gives the next index for a new Concept to be inserted in the map.
	 * 
	 * @return the key for the next Concept to be added.
	 */
	public int nextIndexOfConcept() {
		return concepts.size();
	}

	/**
	 * Try to add a new core to the list of cores.
	 * 
	 * @param cl
	 *            The core to be added.
	 * @return true if it has been added, false if its identifier was already
	 *         registered.
	 */
	public boolean addCore(ConceptLabel cl) {
		if (cores.containsKey(cl.getIdentifier()))
			return false;
		cores.put(cl.getIdentifier(), cl);
		return true;
	}

	/**
	 * Try to add a new edge to the list of edges.
	 * 
	 * @param rl
	 *            The RoleLabel to be added.
	 * @return true if it has been added, false if its identifier was already
	 *         registered.
	 */
	public boolean addEdge(RoleLabel rl) {
		if (edges.containsKey(rl.getIdentifier()))
			return false;
		edges.put(rl.getIdentifier(), rl);
		return true;
	}

	/**
	 * isSimple function.
	 * 
	 * @param role
	 *            The role to be tested.
	 * @return true if Trans of all the sub-role of the given role are false.
	 *         Otherwise, returns false.
	 */
	public boolean isSimple(Role role) {
		for (RoleAxiom axiom : this.getTransitiveClosure())
			if (axiom.getRightRole().equals(role))
				if (this.Trans(axiom.getLeftRole()))
					return false;
		return true;
	}

	/**
	 * isDecidable function.
	 * 
	 * @return true if the ontology is decidable, false otherwise.
	 */
	public boolean isDecidable() {
		Concept concept;

		for (ConceptAxiom conceptAxiom : this.getConceptAxioms()
				.values()) {
			concept = conceptAxiom.getNNF().containsOperator(Type.MAX);

			if (concept == null)
				concept = conceptAxiom.getNNF().containsOperator(Type.MIN);

			if (concept == null)
				continue;

			if (!isSimple(concept.getRole()))
				return false;
		}

		return true;
	}
}

