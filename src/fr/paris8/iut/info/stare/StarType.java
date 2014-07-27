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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.lang.CloneNotSupportedException;
import java.lang.Cloneable;

import fr.paris8.iut.info.stare.Concept.Type;

public class Startype implements Cloneable {
	// identity
	private int id = -1;
	private int counter = 1;
	// core identifier
	private Integer coreId;
	// set of rays and how many times each of which is tiled  
	private HashMap<Integer, Integer> rays;
	// distinct rays of the startype (for the MIN rule)
	private Map<Integer, Set<Integer>> distinctRays;
	// state of the startype
	private boolean isSaturated, isNominal, isValid;
	// set of startypes tiled with a ray
	private HashMap<Integer, Set<Integer>> tiledStartypes;
	// startype dependence (for the UNION, CH or MAX rule)
	private Set<Integer> progeny;
	//identifier of origine startype 
	private Integer ancestorId;
	
	/**
	 * Creation with an id.
	 * 
	 * @param id
	 *            Identifier of the startype.
	 */
	public Startype() {		 
		this.isNominal = false;
		this.isSaturated = false;
		this.isValid = false;
		coreId = null;  
		rays = new HashMap<Integer, Integer>();
		progeny = new HashSet<Integer>();
		ancestorId = null;
		tiledStartypes = new HashMap<Integer, Set<Integer>>();
		distinctRays = new HashMap<Integer, Set<Integer>>();
	}
	/**
	 * Creation of a nominal startype, with a core label.
	 * 
	 * @param id
	 *            Identifier of the startype.
	 * @param cb
	 *            Core of the startype.
	 */
	public Startype(Integer clId) {
		this.isNominal = false;
		this.isSaturated = false;
		this.isValid = false;
		coreId = new Integer(clId);
		rays = new HashMap<Integer, Integer>();
		progeny = new HashSet<Integer>();
		ancestorId = null;
		tiledStartypes = new HashMap<Integer, Set<Integer>>();
		distinctRays = new HashMap<Integer, Set<Integer>>();
	}

	public Startype(Integer clId, Integer rlId) {
		this.isNominal = false;
		this.isSaturated = false;
		this.isValid = false;
		coreId = new Integer(clId);
		rays = new HashMap<Integer, Integer>();
		rays.put(rlId, new Integer(0));
		progeny = new HashSet<Integer>();
		ancestorId = null;
		tiledStartypes = new HashMap<Integer, Set<Integer>>();
		distinctRays = new HashMap<Integer, Set<Integer>>();
	}

	/**
	 * Clones a startype.
	 * 
	 * @param st2
	 *            The startype to be cloned.
	 * @param id2
	 *            The identifier of the clone.
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	/*
	public Startype(Startype st2) {
		this.id = id2;
		this.counter = 1;
		this.core = new ConceptLabel(st2.core);
		this.rays = new HashSet<Ray>(st2.rays);
		this.isNominal = false;
		//this.isValid = false;
		progeny = new HashSet<Startype>();
		this.isSaturated = st2.isSaturated;
		ancestor = null;
		this.distinctRays = new ArrayList<Set<Integer>>(st2.distinctRays);
		paved = new HashMap<Integer, Integer>();
	}
	*/
	/**
	 * Add a new ray : ensure that all rays are different.
	 * 
	 * @param ray
	 *            Ray to add to the startype.
	 * @return true if the ray had been added, false otherwise.
	 */
	public Startype addRay(Integer ray, ReasonerData data) throws CloneNotSupportedException {
		//global ray id is also local ray id in a startype
		//since all rays of a startype are different
		Startype st = (Startype)this.clone();
		if( ! st.getRays().containsKey( ray ) )
		    st.getRays().put( ray, new Integer(0) );
		return st;
	}

	public Startype updateRidge(Integer role, Integer ray, ReasonerData data) throws CloneNotSupportedException {
		Startype st = (Startype)this.clone();
 		Ray ry = data.getRays().get(ray);
		Ray newRay = ry.getNewRayByRole(role, data); 
		st.getRays().put(newRay.getIdentifier(), new Integer(0) );
		st.getRays().remove(ry.getIdentifier());
		return st;
	}

	public Startype updateRidge(Set<Integer> roles, Integer ray, ReasonerData data) throws CloneNotSupportedException {
		Startype st = (Startype)this.clone();
		Ray ry = data.getRays().get(ray);
		Ray newRay = ry.getNewRayByRole(roles, data); 
		st.getRays().put(newRay.getIdentifier(), new Integer(0) );
		st.getRays().remove(ry.getIdentifier());	 
		return st;
	}


	public Startype updateTip(Integer concept, Integer ray, ReasonerData data) throws CloneNotSupportedException {
		Startype st = (Startype)this.clone();
		Ray ry = data.getRays().get(ray);
		Ray newRay = ry.getNewRayByTip(concept, data); 
		st.getRays().put(newRay.getIdentifier(), new Integer(0) );
		st.getRays().remove(ry.getIdentifier()); 
		return st;
	}

	public Startype updateTip(Set<Integer> concepts, Integer ray, ReasonerData data) throws CloneNotSupportedException {
		Startype st = (Startype)this.clone();
		Ray ry = data.getRays().get(ray);
		Ray newRay = ry.getNewRayByTip(concepts, data); 
		st.getRays().put(newRay.getIdentifier(), new Integer(0) );
		st.getRays().remove(ry.getIdentifier()); 
		return st;
	}

	/**
	 * Gives the core of the startype.
	 * 
	 * @return The core of the startype.
	 */
	public Integer getCoreId() {
		return coreId;
	}

	public void setCoreId(Integer id) {
		this.coreId = id;
	}

	/**
	 * Gives the rays of the startype.
	 * 
	 * @return The set of rays for this startype.
	 */
	public HashMap<Integer, Integer> getRays() {
		return rays;
	}
	
	public int getCounter() {
		return counter;
	}

	public void incCounter() {
		++counter;
	}
	public void decCounter() {
		--counter;
	}	
	public void setCounter(int c) {
		counter = c;
	}

	/**
	 * Check if the startype is semantically valid.<br/>
	 * 
	 * @return true if the startype is valid, false otherwise.
	 */
	/*
	public boolean isValid(ReasonerData data) {
		if (isSaturated) {
			Concept c1, c2;
			for (Integer i1 : core ) {
				c1 = data.getConcepts().get(i1);
				for (Integer i2 : core) {
				     c2 = data.getConcepts().get(i2);	 
				     if (Concept.negate(c1, data).equals(c2))
						return false;

					if ((c1.getOperator() == Type.MAX)
							&& (c2.getOperator() == Type.MIN))
						if ((c1.getCardinality() < c2.getCardinality())
								&& (c1.getRole().equals(c2.getRole()))
								&& c1.getChildren().equals(c2.getChildren()))
							return false;
				}
			}
			return true;
		} else
			return false;
		
	}
	*/
	/**
	 * Sets the isSaturated attribute of the startype at true.
	 */
	public void setSaturated() {
		this.isSaturated = true;
	}

	/**
	 * Check if the startype is saturated.
	 * 
	 * @return true if the startype is saturated, false otherwise.
	 */
	public boolean isSaturated() {
		return isSaturated;
	}

	/**
	 * Adds a list of concepts to the core of the startype.
	 * 
	 * @param concepts
	 *            The list of concepts to be added.
	 */
	//
	
	public Startype updateCore(Set<Integer> concepts, ReasonerData data) throws CloneNotSupportedException {
		Startype st = (Startype)this.clone();
		ConceptLabel cl = data.getCores().get( coreId );
		cl = cl.getNewConceptLabel(concepts, data); 
		if(cl.getIdentifier() == coreId.intValue() )
		   return this;
		//update each ray since startype core is a component of each ray
		for(Integer i : this.getRays().keySet() ) {
		     Ray ray = data.getRays().get(i.intValue()).getNewRayByCore(i, data);
		     data.addRay(ray);
		     st.getRays().remove( i);
		     st.getRays().put( ray.getIdentifier(), new Integer(0) );
		}
		data.addStartype(st);
		return st;
	}

	public Startype updateCore(Integer concept, ReasonerData data) throws CloneNotSupportedException {
		Startype st = (Startype)this.clone();
		ConceptLabel cl = data.getCores().get( coreId );
		cl = cl.getNewConceptLabel(concept, data);
		if(cl.getIdentifier() == coreId.intValue())
		   return this;
		//update each ray since startype core is a component of each ray
		for(Integer i : this.getRays().keySet() ) {
		     Ray ray = data.getRays().get(i).getNewRayByCore(i, data);
		     data.addRay(ray);
		     st.getRays().remove( i );
		     st.getRays().put( ray.getIdentifier(), new Integer(0) );
		}
		data.addStartype(st);
		return st;
	}
	
	/**
	 * Check if the startype matches another startype "st" over a ray "r" of
	 * "st". It returns a ray of the startype that matches "r", or null if no
	 * ray has been found.
	 * 
	 * @param st
	 * @param r
	 * @return
	 */
	/*
	public Ray match(Startype st, Ray r) {
		for (Ray ray : this.rays) {
			if (ray.getTip().equals(st.core))
				if (ray.getRidge().isInverseOf(r.getRidge()))
					return ray;
		}
		return null;
	}
	*/
	/**
	 * Check if the startype with a ray "r1" matches another startype "st" with
	 * a ray r2.
	 * 
	 * @param r1
	 * @param st
	 * @param r2
	 * @return
	 */
	/*
	public boolean match(Ray r1, Startype st, Ray r2) {
		if (this.core.equals(r2.getTip()))
			if (r1.getTip().equals(st.core))
				if (r1.getRidge().isInverseOf(r2.getRidge()))
					return true;
		return false;
	}
	*/
	/**
	 * Rule of intersection.
	 * 
	 * @param concept
	 *            Concept to apply the rule.
	 */
	/*
	public List<Concept> intersectionRule(Concept concept) {
		ArrayList<Concept> concepts = new ArrayList<Concept>();

		for (Concept child : concept.getChildren()) {
			core.add(child);
			concepts.add(child);
		}

		if (concepts.size() > 0)
			return concepts;
		else
			return null;
	}
	*/
	/**
	 * Rule about the transitive closure.
	 * 
	 * @param closure
	 *            The transitive closure of the ontology.
	 */
	/*
	public void closureRule(TransitiveClosureOfRoleHierarchy closure) {
		Role role;

		for (RoleAxiom roleAxiom : closure.getTransitiveClosure()) {
			role = roleAxiom.getRightRole();
			for (Ray ray : this.rays) {
				if (ray.getRidge().contains(roleAxiom.getLeftRole()))
					ray.addToRidge(role);
			}
		}
	}
	*/
	/**
	 * Rule of union.
	 * 
	 * @param concept
	 *            Concept to apply the rule.
	 * @param id
	 *            Identifier of the alternative startype to be created in the
	 *            rule.
	 */
	/*
	public Concept unionRule(Concept concept, ReasonerData data, Frame frame) {
		// clone the startype
		Startype st2 = new Startype(this, frame.getLastIDOfStartype());
		Concept c1, c2;

		// apply the rule
		c1 = concept.getChildren().get(0);
		c2 = concept.getChildren().get(1);

		if ((!this.core.contains(c1) && !data.getAxiomNNFs().contains(c1))
				&& (!this.core.contains(c2) && !data.getAxiomNNFs()
						.contains(c2))) {
			this.core.add(c1);
			st2.core.add(c2);

			// create a link
			st2.ancestor = this;
			this.progeny.add(st2);
			frame.incrementLastIDOfStartype();

			return c1;
		} else if ((!this.core.contains(c1) && !data.getAxiomNNFs()
				.contains(c1))
				&& (this.core.contains(c2) || data.getAxiomNNFs().contains(c2))) {
			st2.core.add(c1);

			// create a link
			st2.ancestor = this;
			this.progeny.add(st2);
			frame.incrementLastIDOfStartype();

			return null;
		} else if ((this.core.contains(c1) || data.getAxiomNNFs().contains(c1))
				&& (!this.core.contains(c2) && !data.getAxiomNNFs()
						.contains(c2))) {
			st2.core.add(c2);

			// create a link
			st2.ancestor = this;
			this.progeny.add(st2);
			frame.incrementLastIDOfStartype();

			return null;
		} else
			return null;
	}
	*/
	/**
	 * Rule of some.
	 * 
	 * @param concept
	 *            Concept to apply the rule.
	 */
	/*
	public void someRule(Concept concept, ReasonerData data) {
		Role role = concept.getRole();
		Concept child = concept.getChildren().get(0);
		Ray r = new Ray(role, child);

		for (Ray ray : this.rays)
			if ((ray.getRidge().contains(role))
					&& (ray.getTip().contains(child)))
				return;

		this.addRay(r);
	}
	*/
	/**
	 * Rule of transitive some.
	 * 
	 * @param concept
	 *            Concept to apply the rule.
	 */
	/*
	public Concept someTransitiveRule(Concept concept, ReasonerData data) {
		Role rNonTrans = data.getInverseOfRole(concept.getRole());
		Concept c2 = data.giveConceptIdentifier(new Concept(Type.SOME,
				rNonTrans, concept.getChildren().get(0)));
		Concept c1 = data.giveConceptIdentifier(new Concept(Type.UNION,
				new Concept(Type.SOME, rNonTrans, concept), c2));

		data.addConcept(c1);
		data.addConcept(c2);

		if (!core.contains(c1) && !core.contains(c2)
				&& !data.getAxiomNNFs().contains(c1)
				&& !data.getAxiomNNFs().contains(c2))
			core.add(c1);

		return c1;
	}
	*/
	/**
	 * Rule of all.
	 * 
	 * @param concept
	 *            Concept to apply the rule.
	 */
	/*
	public void allRule(Concept concept) {
		Role role = concept.getRole();
		Concept child = concept.getChildren().get(0);

		for (Ray ray : this.rays)
			if ((ray.getRidge().contains(role))
					&& (!ray.getTip().contains(child))) {
				ray.addToTip(child);
				break;
			}
	}
	*/
	/**
	 * Rule of transitive all.
	 * 
	 * @param concept
	 *            Concept to apply the rule.
	 * @param data
	 *            Data about the ontology.
	 */
	/*
	public void allTransitiveRule(Concept concept, ReasonerData data) {
		Role leftRole;

		// get Q of the axiom where the role of the concept is the S.
		for (RoleAxiom roleAxiom : data.getTransitiveClosure()
				.getTransitiveClosure()) {
			if (roleAxiom.getRightRole().equals(concept.getRole())) {
				leftRole = roleAxiom.getLeftRole();
				// there is Trans(Q)
				if (data.Trans(leftRole)) {
					Concept allConcept = new Concept(Type.ALL, leftRole,
							concept.getChildren().get(0));

					for (Ray ray : rays) {
						if (ray.getRidge().contains(leftRole)
								&& !ray.getTip().contains(allConcept)) {
							ray.addToTip(allConcept);
							break;
						}
					}
				}
			}
		}
	}
	*/
	/**
	 * Rule called ch.
	 * 
	 * @param concept
	 *            Concept to apply the rule.
	 * @param id
	 *            Identifier of the alternative startype to be created in the
	 *            rule.
	 */
	/*
	public void chRule(Concept concept, ReasonerData data, Frame frame) {
		Role role = concept.getRole();
		Concept negation = Concept.negate(concept, data);
		Startype st2;
		Ray r1, r2;

		for (Ray ray : rays)
			if (ray.getRidge().contains(role)
					&& !ray.getTip().contains(concept)
					&& !ray.getTip().contains(negation)) {
				r1 = ray;
				r2 = ray;

				r1.addNNFToTip(data.getConcepts().values());
				r2.addNNFToTip(data.getConcepts().values());

				this.rays.remove(ray);
				st2 = new Startype(this, frame.getLastIDOfStartype());
				r1.addToTip(concept);
				r2.addToTip(negation);

				this.addRay(r1);
				st2.addRay(r2);

				this.progeny.add(st2);
				st2.ancestor = this;
				frame.incrementLastIDOfStartype();
			}
	}
	*/
	/**
	 * Rule of min.
	 * 
	 * @param concept
	 *            Concept to apply the rule.
	 */
	/*
	public void minRule(Concept concept, ReasonerData data) {
		Role role = concept.getRole();
		Concept child = concept.getChildren().get(0);
		Set<Integer> newRays = new HashSet<Integer>();
		Ray newRay;

		// create the number of rays needed
		for (int i = 0; i < concept.getCardinality(); i++) {
			newRay = new Ray(role, child);
			newRay.addNNFToTip(data.getAxiomNNFs());
			this.addRay(newRay);
			newRays.add(newRay.getId());
		}

		// add the created rays to the array of distinct rays
		this.distinctRays.add(newRays);
	}
	*/

	/**
	 * Rule of max. Create a list of startypes with possible fusions of rays.
	 * 
	 * @param concept
	 *            Concept to apply the rule.
	 * @param identifier
	 *            Identifier for the first startype of the list of startypes
	 *            with fusioned rays.
	 */
	/*
	public void maxRule(Concept concept, ReasonerData data, Frame frame) {
		int rayOverflow, i;
		boolean areDistinctRays;
		Map<Integer, Ray> matchingRays;
		Concept child;
		Ray ray1, ray2, fusionRay;
		List<Startype> generated;

		i = 0;
		areDistinctRays = true;
		child = concept.getChildren().get(0);
		generated = new ArrayList<Startype>();
		matchingRays = new HashMap<Integer, Ray>();

		// store the id of all the rays matching the concept
		for (Ray ray : this.rays)
			if (ray.getRidge().contains(concept.getRole())
					&& ray.getTip().contains(child)) {
				matchingRays.put(i, ray);
				i++;
			}
		rayOverflow = matchingRays.size() - concept.getCardinality();

		// if there are too much rays...
		if (rayOverflow >= 1) {
			for (i = 0; i < matchingRays.size() - 1; i++) {
				for (int k = 1; k < matchingRays.size(); k++) {
					Startype st = new Startype(this,
							frame.getLastIDOfStartype());

					// pick the rays two by two
					ray1 = matchingRays.get(i);
					ray2 = matchingRays.get(k);

					// the rays must not have both been generated by the same
					// min
					// rule
					for (Set<Integer> set : distinctRays)
						if (set.contains(ray1.getId())
								&& set.contains(ray2.getId())) {
							areDistinctRays = false;
							break;
						}
					if (!areDistinctRays) {
						areDistinctRays = true;
						continue;
					}

					// then we merge the rays, create a new startype, add it to
					// the list of generated startypes
					fusionRay = ray1.fusion(ray2);
					st.rays.add(fusionRay);
					frame.incrementLastIDOfStartype();
					st.rays.remove(ray1);
					st.rays.remove(ray2);
					generated.add(st);
				}
			}

			// set the current startype as the first startype with fusioned rays
			// of the list
			this.rays = generated.get(0).rays;
			generated.remove(0);
			for (i = 0; i < generated.size(); i++) {
				// add the progeny to the ancestor
				progeny.add(generated.get(i));
				// add the ancestor to the progeny
				generated.get(i).setAncestor(this);
			}
		}

		if (rayOverflow > 1)
			for (Startype startype : generated)
				startype.maxRule(concept, data, frame);
	}
	*/
	/**
	 * Rule about the conceptAssertions.
	 * 
	 * @param assertion
	 *            Assertion to apply the rule.
	 */
	/*
	public List<Concept> conceptAssertionRule(ConceptAssertion assertion) {
		Concept assertionConcept = assertion.getConcept();
		Concept assertionIndividual = assertion.getIndividual();
		ArrayList<Concept> concepts = new ArrayList<Concept>();

		if (this.core.contains(assertionIndividual)) {
			this.core.add(assertionConcept);
			concepts.add(assertionConcept);
		}

		for (Ray ray : this.rays)
			if (ray.getTip().contains(assertionIndividual))
				ray.addToTip(assertionConcept);

		if (concepts.size() > 0)
			return concepts;
		else
			return null;
	}
	*/
	/**
	 * Rule about the roleAssertions.
	 * 
	 * @param assertion
	 *            Assertion to apply the rule.
	 */
	/*
	public void roleAssertionRule(RoleAssertion assertion, ReasonerData data) {
		Concept assertionObject = assertion.getObject();
		Concept assertionSubject = assertion.getSubject();
		Role assertionRole = assertion.getProperty();

		if (core.contains(assertionSubject))
			for (Ray ray : rays)
				if (ray.getTip().contains(assertionObject))
					ray.addToRidge(assertionRole);

		if (core.contains(assertionObject))
			for (Ray ray : rays)
				if (ray.getTip().contains(assertionSubject))
					ray.addToRidge(data.getInverseOfRole(assertionRole));
	}
	*/
	 

	/**
	 * Gives the identifier of the startype.
	 * 
	 * @returnThe identifier of the startype.
	 */
	public int getIdentifier() {
		return this.id;
	}

	public void setIdentifier(int id) {
	       this.id = id;
	}

	/**
	 * Increments the counter of this startype.
	 */
	public void incrementCounter() {
		counter++;
	}

	/**
	 * Check if the startype is nominal.
	 * 
	 * @return true if it is, false otherwise.
	 */
	public boolean isNominal() {
		return isNominal;
	}

	/**
	 * Gives the identifier of the startype paved on the given ray.
	 * 
	 * @param ray
	 *            The ray to be checked.
	 * @return The identifier of the paved startype, or null if no startype has
	 *         been paved.
	 */
	//public int getIdentifierOfStartypePavedOn(Ray ray) {
	//	int identifier = ray.getId();

	//		return paved.get(identifier);
	//}

	/**
	 * Adds a couple of identifier to the map storing the paved rays and
	 * startypes.
	 * 
	 * @param ray
	 *            The ray that has been paved.
	 * @param startype
	 *            The startype that has been paved.
	 */
	/*
	public void addPavedStartypeToRay(Ray ray, Startype startype) {
		paved.put(ray.getId(), startype.getIdentifier());
	}
	*/
	public Set<Integer> getProgeny() {
		return progeny;
	}

	public void setAncestor(Integer s) {
		this.ancestorId = s;
	}

	public Integer getAncestor() {
		return ancestorId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		Startype other = (Startype) obj;
		if (this.id > 0 && other.id > 0)
			if (this.id == other.id)
				return true;
			else
				return false;
		if (id != other.id)
			return false;
		return false;
	}

	//@Override
	public String toString(ReasonerData data) {
		StringBuilder sb = new StringBuilder();

		sb.append("Startype " + id + System.getProperty("line.separator"));
		sb.append("Core:" + System.getProperty("line.separator"));
		sb.append( data.getCores().get( coreId.intValue() ).toString(data) );
		sb.append(System.getProperty("line.separator"));
		sb.append("Rays:" + System.getProperty("line.separator"));
		for (Integer rayId : rays.keySet() ) {
			sb.append(data.getRays().get( rayId.intValue() ).toString(data) );
			sb.append(System.getProperty("line.separator"));
		}

		return sb.toString();
	}
}
