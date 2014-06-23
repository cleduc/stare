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

import fr.paris8.iut.info.stare.Concept.Type;

public class Startype {
	// identity
	private int id;
	// counter indicating the number of similar startypes
	private int counter;
	// core Concept
	private ConceptLabel core;
	// set of rays
	private HashSet<Ray> rays;
	// distinct rays of the startype (for the MIN rule)
	private List<Set<Integer>> distinctRays;
	// startype dependence (for the UNION rule)
	private Set<Startype> ancestors, progeny;
	// state of the startype
	private boolean isSaturated;
	private Boolean isValid;

	/**
	 * Creation with an id.
	 * 
	 * @param id
	 *            Identifier of the startype.
	 */
	public Startype(int id) {
		this.id = id;
		this.counter = 1;
		this.isValid = null;
		this.isSaturated = false;
		core = new ConceptLabel();
		rays = new HashSet<Ray>();
		progeny = new HashSet<Startype>();
		ancestors = new HashSet<Startype>();
		distinctRays = new ArrayList<Set<Integer>>();
	}

	/**
	 * Creation with a core label.
	 * 
	 * @param id
	 *            Identifier of the startype.
	 * @param cb
	 *            Core of the startype.
	 */
	public Startype(int id, ConceptLabel cb) {
		this.id = id;
		this.core = cb;
		this.counter = 1;
		this.isValid = null;
		this.isSaturated = false;
		rays = new HashSet<Ray>();
		progeny = new HashSet<Startype>();
		ancestors = new HashSet<Startype>();
		distinctRays = new ArrayList<Set<Integer>>();
	}

	/**
	 * Creation with a ray = (edge + tip).
	 * 
	 * @param id
	 *            Identifier of the startype.
	 * @param cb
	 *            Core of the startype.
	 * @param edge
	 *            Edge of the ray of the startype.
	 * @param tip
	 *            Tip of the ray of the startype.
	 */
	public Startype(int id, ConceptLabel cb, RoleLabel edge, ConceptLabel tip) {
		this.id = id;
		this.core = cb;
		this.counter = 1;
		this.isValid = null;
		this.isSaturated = false;
		rays = new HashSet<Ray>();
		this.rays.add(new Ray(edge, tip));
		progeny = new HashSet<Startype>();
		ancestors = new HashSet<Startype>();
		distinctRays = new ArrayList<Set<Integer>>();
	}

	/**
	 * Clones a startype.
	 * 
	 * @param st2
	 *            The startype to be cloned.
	 * @param id2
	 *            The identifier of the clone.
	 */
	private Startype(Startype st2, int id2) {
		this.id = id2;
		this.counter = 1;
		this.core = st2.core;
		this.rays = st2.rays;
		this.isValid = st2.isValid;
		this.isSaturated = st2.isSaturated;
		this.distinctRays = st2.distinctRays;
		ancestors = new HashSet<Startype>();
		progeny = new HashSet<Startype>();
	}

	/**
	 * Add a new ray : ensure that all rays are different.
	 * 
	 * @param ray
	 *            Ray to add to the startype.
	 * @return true if the ray had been added, false otherwise.
	 */
	public boolean addRay(Ray ray) {
		return this.rays.add(ray);
	}

	/**
	 * Check if the startype is semantically valid.<br/>
	 * WARNING: checking the validity of a startype is a complicated operation,
	 * therefore it is registered in a variable. This means each rule applied
	 * after the first checkValidity won't change the validity of the startype.
	 * 
	 * @return true if the startype is valid, false otherwise.
	 */
	public boolean checkValidity() {
		if (isValid != null)
			return isValid;

		if (isSaturated) {
			Iterator<Concept> it1, it2;
			Concept c1, c2;

			it1 = core.iterator();
			it2 = core.iterator();
			while (it1.hasNext()) {
				c1 = it1.next();
				while (it2.hasNext()) {
					c2 = it2.next();

					if (Concept.negate(c1).equals(c2)) {
						isValid = false;
						return false;
					}

					if ((c1.getOperator() == Type.MAX)
							&& (c2.getOperator() == Type.MIN))
						if ((c1.getCardinality() < c2.getCardinality())
								&& (c1.getRole().equals(c2.getRole()))
								&& c1.getChildren().equals(c2.getChildren())) {
							isValid = false;
							return false;
						}
				}
			}
			isValid = true;
			return true;
		} else {
			isValid = false;
			return false;
		}
	}

	/**
	 * Check if the startype is saturated.
	 * 
	 * @return true if the startype is saturated, false otherwise.
	 */
	public boolean checkSaturation() {
		return isSaturated;
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
	public Ray match(Startype st, Ray r) {
		for(Ray ray : this.rays){
			if(ray.getTip().equals(st.core))
				if(ray.getRidge().isInverseOf(r.getRidge()))
					return ray;
		}
		return null;
	}

	/**
	 * Check if the startype with a ray "r1" matches another startype "st" with
	 * a ray r2.
	 * 
	 * @param r1
	 * @param st
	 * @param r2
	 * @return
	 */
	public boolean match(Ray r1, Startype st, Ray r2) {
		if (this.core.equals(r2.getTip()))
			if (r1.getTip().equals(st.core))
				if (r1.getRidge().isInverseOf(r2.getRidge()))
					return true;
		return false;
	}

	/**
	 * Rule of inclusion.
	 * 
	 * @param concept
	 *            Concept to apply the rule.
	 */
	public void includeRule(Concept concept) {
		if (!core.add(concept))
			isSaturated = true;
	}

	/**
	 * Rule of intersection.
	 * 
	 * @param concept
	 *            Concept to apply the rule.
	 */
	public void intersectionRule(Concept concept) {
		boolean haschanged = false;

		for (Concept child : concept.getChildren())
			haschanged = core.add(child);

		this.isSaturated = !haschanged;
	}

	/**
	 * Rule of union.
	 * 
	 * @param concept
	 *            Concept to apply the rule.
	 * @param id
	 *            Identifier of the alternative startype to be created in the
	 *            rule.
	 */
	public void unionRule(Concept concept, int id) {
		// clone the startype
		Startype st2 = new Startype(this, id);

		// apply the rule
		if (this.core.add(concept.getChildren().get(0))) {
			if (st2.core.add(concept.getChildren().get(1))) {
				// create a link
				st2.ancestors.add(this);
				this.progeny.add(st2);
			}
		} else {
			if (!this.core.add(concept.getChildren().get(1)))
				this.isSaturated = true;
		}
	}

	/**
	 * Rule of some.
	 * 
	 * @param concept
	 *            Concept to apply the rule.
	 */
	public void someRule(Concept concept) {
		Role role = concept.getRole();
		Concept child = concept.getChildren().get(0);

		for (Ray ray : this.rays)
			if ((!ray.getRidge().contains(role))
					&& (!ray.getTip().contains(child)))
				if (!this.rays.add(new Ray(role, child)))
					this.isSaturated = true;
	}

	/**
	 * Rule of transitive some.
	 * 
	 * @param concept
	 *            Concept to apply the rule.
	 */
	public void someTransitiveRule(Concept concept) {
		Role r = concept.getRole();
		Role rNonTrans = new Role(r.getName(), -2, r.isTransitive,
				r.isFunctional, r.isInverse, false);
		Concept c2 = new Concept(Type.SOME, rNonTrans, concept.getChildren()
				.get(0));
		Concept c1 = new Concept(Type.UNION, new Concept(Type.SOME, rNonTrans,
				concept), c2);

		if (!core.contains(c1) && !core.contains(c2))
			if (!core.add(c1))
				this.isSaturated = true;
	}

	/**
	 * Rule of all.
	 * 
	 * @param concept
	 *            Concept to apply the rule.
	 */
	public void allRule(Concept concept) {
		Role role = concept.getRole();
		Concept child = concept.getChildren().get(0);

		for (Ray ray : this.rays)
			if ((ray.getRidge().contains(role))
					&& (!ray.getTip().contains(child))) {
				if (!ray.addConcept(child))
					this.isSaturated = true;
				break;
			}
	}

	/**
	 * Rule of transitive all.
	 * 
	 * @param concept
	 *            Concept to apply the rule.
	 * @param data
	 *            Data about the ontology.
	 */
	public void allTransitiveRule(Concept concept, ReasonerData data) {
		Role leftRole;

		// get Q of the axiom where the role of the concept is the S.
		for (RoleAxiom roleAxiom : data.getTransitiveClosure()) {
			if (roleAxiom.getRightRole().equals(concept.getRole())) {
				leftRole = roleAxiom.getLeftRole();
				// there is Trans(Q)
				if (data.Trans(leftRole)) {
					Concept allConcept = new Concept(Type.ALL, leftRole,
							concept.getChildren().get(0));

					for (Ray ray : rays) {
						if (ray.getRidge().contains(leftRole)
								&& !ray.getTip().contains(allConcept)) {
							if (!ray.addConcept(allConcept))
								this.isSaturated = true;
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Rule called ch.
	 * 
	 * @param concept
	 *            Concept to apply the rule.
	 * @param id
	 *            Identifier of the alternative startype to be created in the
	 *            rule.
	 */
	public void chRule(Concept concept, int id) {
		Role role = concept.getRole();
		Concept negation = Concept.negate(concept);
		Startype st2;
		Ray r1, r2;

		for (Ray ray : rays) {
			if (ray.getRidge().contains(role)
					&& !ray.getTip().contains(concept)
					&& !ray.getTip().contains(negation)) {
				r1 = ray;
				r2 = ray;
				this.rays.remove(ray);
				st2 = new Startype(this, id);
				r1.addConcept(concept);
				r2.addConcept(negation);

				if (this.addRay(r1)) {
					if (st2.addRay(r2)) {

						this.progeny.add(st2);
						st2.ancestors.add(this);
					}
				} else {
					if (!this.addRay(r2))
						this.isSaturated = false;
				}
			}
		}
	}

	/**
	 * Rule of min.
	 * 
	 * @param concept
	 *            Concept to apply the rule.
	 */
	public void minRule(Concept concept) {
		int matchingRays = 0, missingRays;
		Role role = concept.getRole();
		Concept child = concept.getChildren().get(0);
		Set<Integer> newRays = new HashSet<Integer>();
		Ray newRay;

		// calculate the number of existing rays matching the concept
		for (Ray ray : this.rays) {
			if (ray.getRidge().contains(role) && ray.getTip().contains(child))
				matchingRays++;
		}
		missingRays = concept.getCardinality() - matchingRays;

		// if some rays are missing, create it
		for (int i = 0; i < missingRays; i++) {
			newRay = new Ray(role, child);
			this.addRay(newRay);
			newRays.add(newRay.getId());
		}
		// add the created rays to the array of distinct rays
		if (!this.distinctRays.add(newRays))
			this.isSaturated = true;
	}

	/**
	 * Rule of max. Create a list of startypes with possible fusions of rays.
	 * 
	 * @param concept
	 *            Concept to apply the rule.
	 * @param identifier
	 *            Identifier for the first startype of the list of startypes
	 *            with fusioned rays.
	 */
	public void maxRule(Concept concept, int identifier) {
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
					Startype st = new Startype(this, identifier);

					// pick the rays two by two
					ray1 = matchingRays.get(i);
					ray2 = matchingRays.get(k);

					// the ray must not have both been generated by the same min
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
					if (!st.rays.add(fusionRay))
						this.isSaturated = true;
					else {
						identifier++;
						st.rays.remove(ray1);
						st.rays.remove(ray2);
					}
					generated.add(st);
				}
			}

			// set the current startype as the first startype with fusioned rays
			// of the list
			this.rays = generated.get(0).rays;
			generated.remove(0);
			// add the progeny to the ancestor
			for (i = 0; i < generated.size(); i++)
				progeny.add(generated.get(i));
			// add the ancestor to the progeny
			for (i = 0; i < generated.size(); i++)
				ancestors.add(this);
		}

		if (rayOverflow > 1)
			for (Startype startype : generated)
				startype.maxRule(concept, identifier);
	}

	/**
	 * Gives the number of similar startypes created.
	 * 
	 * @return The number of similar startypes created.
	 */
	public int getCounter() {
		return counter;
	}

	/**
	 * Increments the counter of this startype.
	 */
	public void incrementCounter() {
		counter++;
	}
}

