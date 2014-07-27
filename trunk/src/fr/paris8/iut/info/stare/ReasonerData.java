package fr.paris8.iut.info.stare;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

import org.semanticweb.owlapi.model.OWLClass;

import fr.paris8.iut.info.stare.Concept.Type;

public class ReasonerData {
	private Map<Integer, ConceptAssertion> conceptAssertions;
	private Map<Integer, RoleAssertion> roleAssertions;
	/** The concept axioms, once they have been transformed */
	private Map<Integer, ConceptAxiom> conceptAxioms;
	/** NNF of axioms */
	private Set<Integer> axiomNNFs;
	/** ConceptLabel of all NNFs*/
	private Integer NNFConceptLabel;
	/** The role axioms, once they have been transformed */
	private Map<Integer, RoleAxiom> roleAxioms;
	/** Representation of the transitive closure of the ontology */
	private TransitiveClosureOfRoleHierarchy transitiveClosure;

	/** The roles in the ontology AND those we created */
	private Map<Integer, Role> roles;

	/** The concepts of the ontology, the terminals, and those created */
	private Map<Integer, Concept> concepts;

	/**
	 * Map containing the different cores created, with their identifier as a
	 * key
	 */
	private Map<Integer, ConceptLabel> cores;
	/**
	 * Map containing the different ridges of ray created, with their identifier
	 * as a key
	 */
	private Map<Integer, RoleLabel> ridges;

	/**
	 * Map containing the different rays, with their identifier
	 * as a key
	 */
	private Map<Integer, Ray> rays;

	/**
	 * Map containing the different startypes created, with their identifier
	 * as a key
	 */
	private Map<Integer, Startype> starsByInt;

	/**
	 * Map containing the different startypes created, and they are grouped in rays
	 * 
	 */
	private Map<Integer, Set<Startype>> starsByRay;
 
	/**
	 * The default constructor.
	 */
	public ReasonerData() {
		transitiveClosure = null;
		roles = new HashMap<Integer, Role>();
		ridges = new HashMap<Integer, RoleLabel>();
		rays = new HashMap<Integer, Ray>();
		concepts = new HashMap<Integer, Concept>();
		cores = new HashMap<Integer, ConceptLabel>();
		starsByInt = new HashMap<Integer, Startype>();
		starsByRay = new HashMap<Integer, Set<Startype>>();
		roleAxioms = new HashMap<Integer, RoleAxiom>();
		conceptAxioms = new HashMap<Integer, ConceptAxiom>();
		roleAssertions = new HashMap<Integer, RoleAssertion>();
		conceptAssertions = new HashMap<Integer, ConceptAssertion>();
		axiomNNFs = new HashSet<Integer>();
	}

	/**
	 * Initialization of the map of concepts.
	 * 
	 * @param classes
	 * 
	 */
	//Rethink
	public void initConceptMap(Map<OWLClass, Integer> classes) {
		String className;

		for (OWLClass owlClass : classes.keySet()) {
			className = owlClass.getIRI().toString();
			concepts.put(classes.get(owlClass),
					new Concept(
							className.substring(className.indexOf("#") + 1),
							classes.get(owlClass), false, false));
		}
                
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
	public TransitiveClosureOfRoleHierarchy getTransitiveClosure() {
		return transitiveClosure;
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
	 * Adds a ConceptAxiom to the map of ConceptAxiom. Also adds the NNF of the
	 * axiom to the list of concepts.
	 * 
	 * @param conceptAxiom
	 *            The ConceptAxiom to be added.
	 */
	//This fonction is called from LoadOntology. Thus, "axiomNNFs" is filled
	public void addConceptAxiom(ConceptAxiom conceptAxiom) {
		//Concept nnf = conceptAxiom.getNNF();
		conceptAxioms.put(conceptAxiom.getIdentifier(), conceptAxiom);
		//this.addConcept( nnf );
		//Concept c = this.giveConceptIdentifier( nnf ) ;
		//axiomNNFs.add( c.getIdentifier() );
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
	 * Seeks if a Concept is in the map; if it is, it returns it, otherwise it
	 * returns null. This function should only be used with concept that have
	 * not been identified yet.
	 * 
	 * @param c
	 *            The concept to be sought.
	 * @return The identified concept, or null if it had not been found.
	 */
	public Concept getConcept(Concept c) {
		//If concept is identified
		if (c.getIdentifier() >= 0)
			return concepts.get( new Integer(c.getIdentifier()) );

		for (Concept concept : concepts.values())
			if (c.equals(concept))
				return concept;
		return null;

	}

	public Concept giveConceptIdentifier(Concept c) {
		Concept concept = this.getConcept(c);

		if (concept == null) {
			c.setIdentifier(concepts.size());
			return c;
		} else {
			return concept;
		}
	}

	/**
	 * Adds a Concept to the map of Concept. Checks if its identifier has
	 * already been used before.
	 * 
	 * @param concept
	 *            The Concept to be added.
	 * @return true if the concept has been added, false if it was already in
	 *         the map.
	 */
	public boolean addConcept(Concept concept) {
		if (concepts.containsKey( new Integer(concept.getIdentifier()) ))
			return false;
		Concept c = this.giveConceptIdentifier(concept);
		concepts.put(new Integer(c.getIdentifier()), c);
		return true;
	}

	/**
	 * A getter for the concepts.
	 * 
	 * @return The map of concepts of the ontology.
	 */
	public Map<Integer, Concept> getConcepts() {
		return concepts;
	}

	public Map<Integer, ConceptLabel> getCores() {
		return cores;
	}

	public Map<Integer, RoleLabel> getRidges() {
		return ridges;
	}

	/**
	 * A getter for the rays
	 * 
	 * @return The map of  rays
	 */
	public Map<Integer, Ray> getRays() {
		return rays;
	}

	public Set<Integer> getAxiomNNFs() {
		return axiomNNFs;
	}

	public Integer getNNFConceptLabel() {
		return NNFConceptLabel;
	}

	public void setNNFConceptLabel(Integer id) {
	       NNFConceptLabel = id;
	}

	public Role getRole(Role r) {
		//If role is identified
		if (r.getIdentifier() >= 0)
			return roles.get(r.getIdentifier());
		//"equals" manipulates two sets of Integer
		for (Role role : roles.values())
			if (r.equals(role))
				return role;
		return null;

	}

	public Role giveRoleIdentifier(Role r) {
		Role role = this.getRole(r);
		if (role == null) {
			r.setIdentifier(roles.size());
			return r;
		} else {
			return role;
		}
	}
	 
	public boolean addRole(Role role) {
		if (roles.containsKey( new Integer(role.getIdentifier())))
			return false;
		Role r = this.giveRoleIdentifier(role);
		roles.put(new Integer(r.getIdentifier()), r);
		return true;
	}

	/**
	 * Seeks if a core is in the map; if it is, it returns it, otherwise it
	 * returns null. This function should only be used with a core that have not
	 * been identified yet.
	 * 
	 * @param cl
	 *            The core to be sought.
	 * If cl.getIdentifier() is negative, it must search in the list
	 * 
	 * @return The identified core, or null if it had not been found.
	 */

	public ConceptLabel getCore(ConceptLabel cl) {
		//if core is idenfified
		if (cl.getIdentifier() >= 0)
			return cores.get(cl.getIdentifier());
		//"equals" manipulates two sets of Integer
		for (ConceptLabel conceptLabel : cores.values() )
			if (cl.equals(conceptLabel))
				return conceptLabel;
		return null;
	}

	/**
	 * Gives an identifier to a newly created core.
	 * 
	 * @param core
	 *            The core to be identified.
	 * @return The core, with a valid identifier.
	 */
	public ConceptLabel giveCoreIdentifier(ConceptLabel core) {
		ConceptLabel cl = this.getCore(core);

		if (cl == null) {
			core.setIdentifier(cores.size());
			return core;
		} else {
			return cl;
		}
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
		ConceptLabel core = this.giveCoreIdentifier(cl);
		cores.put(core.getIdentifier(), core);
		return true;
	}

	/**
	 * Seeks if a ridge is in the map; if it is, it returns it, otherwise it
	 * returns null. This function should only be used with a ridge that have
	 * not been identified yet.
	 * 
	 * @param rl
	 *            The ridge to be sought.
	 * @return The identified ridge, or null if it had not been found.
	 */
	public RoleLabel getRidge(RoleLabel rl) {
		//if ridge is idenfified
		if (rl.getIdentifier() >= 0)
			return ridges.get(rl.getIdentifier());
		//"equals" manipulates two sets of Integer
		for (RoleLabel roleLabel : ridges.values())
			if (rl.equals(roleLabel))
				return roleLabel;
		return null;

	}

	/**
	 * Gives an identifier to a newly created Ridge.
	 * 
	 * @param ridge
	 *            The ridge to be identified.
	 * @return The ridge, with a valid identifier.
	 */
	public RoleLabel giveRidgeIdentifier(RoleLabel ridge) {
		RoleLabel rl = getRidge(ridge);
		if (rl == null) {
			ridge.setIdentifier(ridges.size());
			return ridge;
		} else {
			return rl;
		}
	}

	/**
	 * Try to add a new edge to the list of edges.
	 * 
	 * @param rl
	 *            The RoleLabel to be added.
	 * @return true if it has been added, false if its identifier was already
	 *         registered.
	 */
	public boolean addRidge(RoleLabel rl) {
		if (ridges.containsKey(rl.getIdentifier()))
			return false;
		RoleLabel ray = this.giveRidgeIdentifier(rl);
		ridges.put(ray.getIdentifier(), ray);
		return true;
	}


	public Ray getRay(Ray r) {
		//if ray is idenfified
		if (r.getIdentifier() >= 0)
			return rays.get(r.getIdentifier());
		//"equals" manipulates two sets of triples of Integer
		for (Ray ray : rays.values() )
			if (r.equals(ray))
				return ray;
		return null;

	}

	public Ray giveRayIdentifier(Ray r) {
		Ray ray = this.getRay(r);
		if (ray == null) {
			r.setIdentifier(rays.size());
			return r;
		} else {
			return ray;
		}
	}

	 
	 
	public boolean addRay(Ray ray) {
		if (rays.containsKey(ray.getIdentifier()))
			return false;
		Ray r = this.giveRayIdentifier(ray);
		rays.put(r.getIdentifier(), r);
		return true;
	}

	public Startype getStartype(Startype st) {
		//if startype is idenfified
		if (st.getIdentifier() >= 0)
			return starsByInt.get(st.getIdentifier());
		//"equals" manipulates two sets of 3n-tuples of Integer 
		//where n is the number of rays 
		for (Startype s : starsByInt.values())
			if (st.equals(s))
				return s;
		return null;

	}

	 
	public Startype giveStartypeIdentifier(Startype st) {
		Startype s = getStartype(st);

		if (s == null) {
			st.setIdentifier(starsByInt.size());
			return st;
		} else {
			return s;
		}
	}

	 
	public boolean addStartype(Startype st) {
		if (starsByInt.containsKey(st.getIdentifier()))
			return false;

		starsByInt.put(st.getIdentifier(), st);

		for (Integer r : st.getRays().keySet() ) {    
	             if( starsByRay.containsKey(r) )
 		         starsByRay.get(r).add(st);
		     else {
			 Set ss = new HashSet<Startype>();
			 ss.add(st); 
			 starsByRay.put(r, ss);
		     }
		}
		return true;
	}

	/**
	 * Gives the inverse of a role, if it exists in the list.
	 * 
	 * @param role
	 *            The role which inverse must be found.
	 * @return Its inverse. null if no inverse has been found.
	 */
	public Role getInverseOfRole(Role r, ReasonerData data) {
		Role rInverse = new Role( r.getName(), -1, r.isTransitive(), 
					  r.isFunctional(), r.isInverse(), r.isTransitiveClosure());
		if(r.isInverse()){
		   	rInverse = data.giveRoleIdentifier(rInverse);
		        rInverse.setInverse(false);
		} else {
		        rInverse = data.giveRoleIdentifier(rInverse);
		        rInverse.setInverse(true);
		}
		data.addRole(rInverse);
		return rInverse;
	}

	/**
	 * Adds a ConceptAssertion to the list.
	 * 
	 * @param ca
	 *            The ConceptAssertion to be added.
	 */
	public void addConceptAssertion(ConceptAssertion ca) {
		conceptAssertions.put(ca.getIdentifier(), ca);
	}

	/**
	 * A getter for the conceptAssertions.
	 * 
	 * @return The map of conceptAssertions of the ontology.
	 */
	public Map<Integer, ConceptAssertion> getConceptAssertions() {
		return conceptAssertions;
	}

	/**
	 * Adds a RoleAssertion to the list.
	 * 
	 * @param ra
	 *            The RoleAssertion to be added.
	 */
	public void addRoleAssertion(RoleAssertion ra) {
		roleAssertions.put(ra.getIdentifier(), ra);
	}

	/**
	 * A getter for the roleAssertions.
	 * 
	 * @return The map of roleAssertions of the ontology.
	 */
	public Map<Integer, RoleAssertion> getRoleAssertions() {
		return roleAssertions;
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
		for (RoleAxiom axiom : this.getTransitiveClosure()
				.getTransitiveClosure())
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

		for (ConceptAxiom conceptAxiom : this.getConceptAxioms().values()) {
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
