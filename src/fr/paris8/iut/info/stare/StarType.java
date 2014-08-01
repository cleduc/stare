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
	//private Map<Integer, Set<Integer>> distinctRays;
	// state of the startype
	private boolean isSaturated, isNominal, isValid;
	// set of startypes tiled with a ray
	private HashMap<Integer, Set<Integer>> tiledStartypes;
	// startype dependence (for the UNION, CH or MAX rule)
	private Set<Integer> progeny;
	//identifier of origine startype 
	private Integer ancestorId, expandedId;
	
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
		expandedId = null;
		tiledStartypes = new HashMap<Integer, Set<Integer>>();
		//distinctRays = new HashMap<Integer, Set<Integer>>();
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
		expandedId = null;
		tiledStartypes = new HashMap<Integer, Set<Integer>>();
		//distinctRays = new HashMap<Integer, Set<Integer>>();
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
		expandedId = null;
		tiledStartypes = new HashMap<Integer, Set<Integer>>();
		//distinctRays = new HashMap<Integer, Set<Integer>>();
	}

	/**
	 * Clones a startype.
	 * 
	 * @param st2
	 *            The startype to be cloned.
	 * @param id2
	 *            The identifier of the clone.
	 */
	//It creates an exact copy of "st" except for "id=-1"
	protected Startype(Startype st )   {		
		this.setIdentifier(-1);
		this.setCounter(st.getCounter());
		this.setCoreId (new Integer(st.getCoreId()));
		this.setRays(new HashMap<Integer, Integer>());
		for(Integer r : st.getRays().keySet()) {
		   Integer nb = new Integer(st.getRays().get(r));
		   this.rays.put(r, nb );
		}
		this.setNominal(st.isNominal());
		this.setValid(st.isValid());
		this.setSaturated(st.isSaturated());
		//this.setDistinctRays(new HashMap<Integer, Set<Integer>>());
		//for(Integer r : st.getDistinctRays().keySet()) {
		//   Set<Integer> rays = new HashSet<Integer>(st.getDistinctRays().get(r) );
		//   this.getDistinctRays().put(r, rays);
		//}
		if(st.getAncestor()==null)
		    this.setAncestor(null);
		else 
		    this.setAncestor(new Integer(st.getAncestor()));
		if(st.getExpanded()==null)
		    this.setExpanded(null);
		else 
		    this.setExpanded(new Integer(st.getExpanded()));
		this.setProgeny(new HashSet<Integer>(st.getProgeny()));
		this.setTiledStartypes(new HashMap<Integer, Set<Integer>>());
		for(Integer r : st.getTiledStartypes().keySet() ) {
		   Set<Integer> ss = new HashSet<Integer>(st.getTiledStartypes().get(r));
		   this.getTiledStartypes().put(r, ss);
		}
	}
	 
	/**
	 * Add a new ray : ensure that all rays are different.
	 * 
	 * @param ray
	 *            Ray to add to the startype.
	 * @return true if the ray had been added, false otherwise.
	 */

	public Startype addRay(Integer ray, ReasonerData data)   {
		//global ray id is also local ray id in a startype
		//since all rays of a startype are different
		Startype st = new Startype(this);
		if( ! st.getRays().containsKey( ray ) )
		    st.getRays().put( ray, new Integer(0) );
		return st;
	}


	public Startype addRays(Set<Integer> rs, ReasonerData data)   {
		Startype st = new Startype(this);
		for(Integer r : rs) {
		    if( ! st.getRays().containsKey( r ) )
		        st.getRays().put(r, new Integer(0) );
		}
		return st;
	}

	public Startype replaceRay(Integer or, Integer nr, ReasonerData data)   {
		Startype st = new Startype(this);
		st.getRays().remove(or);			
		st.getRays().put( nr, new Integer(0) );
		return st;
	}

	public Startype replaceRay(Integer or1, Integer or2, Integer nr, ReasonerData data)   {
		Startype st = new Startype(this);
		st.getRays().remove(or1);
		st.getRays().remove(or2);			
		st.getRays().put( nr, new Integer(0) );
		return st;
	}

	public Startype getStartypeByRidge(List<Integer> roles, Integer ray, ReasonerData data)   {
		Set<Integer> rSet = new HashSet();
		rSet.addAll(roles);
		Startype st = new Startype(this);
		Ray ry = data.getRays().get(ray);
		Ray newRay = ry.getNewRayByRole(rSet, data); 
		st.getRays().put(newRay.getIdentifier(), new Integer(0) );
		st.getRays().remove(ry.getIdentifier());
		st = data.addStartype(st);	 
		return st;
	}


	public Startype getStartypeByTip(List<Integer> concepts, Integer ray, ReasonerData data)   {
		Set<Integer> cSet = new HashSet();
		cSet.addAll(concepts);
		Startype st = new Startype(this);
		Ray ry = data.getRays().get(ray);
		Ray newRay = ry.getNewRayByTip(cSet, data); 
		st.getRays().put(newRay.getIdentifier(), new Integer(0) );
		st.getRays().remove(ry.getIdentifier()); 
		st = data.addStartype(st);
		return st;
	}

	

	/**
	 * Check if the startype is semantically valid.<br/>
	 * 
	 * @return true if the startype is valid, false otherwise.
	 */
	
	public boolean isValid(ReasonerData data) {
		//A startype may be invalid even if it is not saturated
		if (isSaturated) {
			for (Integer i1 : data.getCores().get(getCoreId()).getConceptIds() ) {
				Concept c1 = data.getConcepts().get(i1);
				for (Integer i2 : data.getCores().get(getCoreId()).getConceptIds() ) {
				     if (Concept.negate(i1, data).equals(i2))
						return false;
				}
				if (c1.getOperator() == Type.MAX) {
				     Integer r = c1.getRoleId();
				     Integer c = c1.getChildren().get(0);
				     int nb =  c1.getCardinality();
				     int nbRays=0;
				     for (Integer ray : getRays().keySet()) {
					  if(data.getRidges().get(data.getRays().get(ray).getRidgeId()).contains(r) && 
					     data.getCores().get(data.getRays().get(ray).getTipId()).contains(c))
					  nbRays++;
				     } 
				     if(nbRays > nb)
					return false;
				}
			}
			return true;
		} else
		  return false;
	}
	
	/**
	 * Sets the isSaturated attribute of the startype at true.
	 */
	public void setSaturated() {
		this.isSaturated = true;
	}
	public void setSaturated(boolean b) {
		this.isSaturated = b;
	}

	/**
	 * Check if the startype is saturated.
	 * 
	 * @return true if the startype is saturated, false otherwise.
	 */
	public boolean isSaturated() {
		return isSaturated;
	}

	public void setNominal(boolean b) {
		this.isNominal = b;
	}

	public boolean isNominal() {
		return this.isNominal ;
	}

	public void setValid(boolean b) {
		this.isValid = b;
	}

	public boolean isValid() {
		return this.isValid ;
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
	public Map<Integer, Integer> getRays() {
		return rays;
	}
	
	public void setRays(HashMap<Integer, Integer> r) {
		rays = r;
	}

	public Map<Integer, Set<Integer>> getTiledStartypes() {
		return tiledStartypes;
	}

	public void setTiledStartypes(HashMap<Integer, Set<Integer>>  m) {
		tiledStartypes = m;
	}

	public Set<Integer> getProgeny() {
		return progeny;
	}

	public void setProgeny(Set<Integer> p) {
		progeny = p;
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
	 * Adds a list of concepts to the core of the startype.
	 * 
	 * @param concepts
	 *            The list of concepts to be added.
	 */
	//

	public Startype getStartypeByCore(List<Integer> concepts, ReasonerData data)   {
		Startype st = new Startype(this);
		ConceptLabel cl = data.getCores().get( coreId );
		Set<Integer> cSet = new HashSet();
		cSet.addAll(concepts);
		cl = cl.getNewConceptLabel(cSet, data); 
		if( coreId.equals(new Integer(cl.getIdentifier())) )
		   return this;		 
		st.setCoreId( cl.getIdentifier() );
		//update each ray since startype core is a component of each ray
		for(Integer i : this.getRays().keySet() ) {
		     Ray ray = data.getRays().get(i.intValue()).getNewRayByCore(i, data);
		     ray = data.addRay(ray);
		     st.getRays().remove( i);
		     st.getRays().put( ray.getIdentifier(), new Integer(0) );
		}
		st = data.addStartype(st);
		//System.out.println("id core Result = " +st.getCoreId() +", start type id= " + st.getIdentifier() );
		return st;
	}
	
	/**
	 * Check if the startype matches another startype "startype2" over its ray "r2" 
	 * via "ray2" of this startype. This may lead to apply rules to "startype2"  and this startype.
	 * This method requires that "startype2" and this startype must be valid 
	 * @param startype
	 * @param ray of 
	 * @return
	 */
	/*
	public Ray match(Integer ray1, Integer startype2, Integer ray2) {
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

	//This method allows to expand the startype progressively 
	public Startype intersectionRule(Integer concept, boolean changed, ReasonerData data)   { 
		    List<Integer> l = new ArrayList(0);
		    boolean applied = false;
		    for(Integer c : data.getConcepts().get(concept).getChildren()) {
			if( ! data.getCores().get(coreId).contains(c) ) {
			    l.add(c);
			    applied = true;
			}
		    }
		    if(! applied ) 
			return this;
		    Startype st = getStartypeByCore(l, data);
		    st = data.addStartype(st);
		    st.setValid(st.isValid(data));
		    changed = true;
		    return st;
	}
	
	 
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
	public Startype unionRule(Integer concept, boolean changed, ReasonerData data)   {
		List<Integer> l1 = new ArrayList<Integer>(0);
		List<Integer> l2 = new ArrayList<Integer>(0);
		Integer i1 = data.getConcepts().get(concept).getChildren().get(0);
		Integer i2 = data.getConcepts().get(concept).getChildren().get(1);
		if( data.getCores().get(coreId).contains(i1) || data.getCores().get(coreId).contains(i2)) 
		    return this;
		l1.add(i1);
		l2.add(i2);
		Startype st1 = getStartypeByCore(l1, data);
		Startype st2 = getStartypeByCore(l2, data);
		st2.setAncestor(st1.getIdentifier());
		st1.getProgeny().add(st2.getIdentifier());
		st1.setValid(st1.isValid(data));
		st2.setValid(st2.isValid(data));
		changed = true;
		return st1;
	}
	 
        
	
	/**
	 * Rule of some.
	 * 
	 * @param concept
	 *            Concept to apply the rule.
	 */
	
	public Startype someRule(Integer concept, boolean changed, ReasonerData data)   {
		Integer role = data.getConcepts().get(concept).getRoleId();
		Integer child = data.getConcepts().get(concept).getChildren().get(0);
		Set<Integer> names = data.getSomeNames(concept);
		ConceptLabel currentCore = data.getCores().get(coreId); 
		ConceptLabel newCore = currentCore.getNewConceptLabel((Integer)names.toArray()[0], data);
		//System.out.println("nnF = "+data.getAxiomNNFs());
		ConceptLabel tl = new ConceptLabel(child);
		tl = tl.getNewConceptLabel((Integer)names.toArray()[1], data);
		tl = tl.getNewConceptLabel(data.getAxiomNNFs(), data);
		//System.out.println("new tip = "+ tl.toString(data)+ " id new tip = "+tl.getIdentifier());
		for (Integer ray : this.getRays().keySet()) {
			//System.out.println("some role="+ data.getRoles().get(role).toString());
			Integer ridgeId = data.getRays().get(ray).getRidgeId();
			RoleLabel rl2 = data.getRidges().get(ridgeId);
			//System.out.println("some ridge="+ ridgeId);
			//System.out.println("some ridge content ="+ rl2.toString(data) );
			Integer tipId = data.getRays().get(ray).getTipId();
			Integer coreId = data.getRays().get(ray).getCoreId();
			ConceptLabel cl = data.getCores().get(tipId);
			//System.out.println("tip = "+ cl.toString(data)+ " id, "+tipId);
			if ( cl.contains( child )  &&  rl2.contains( role )) {	 
				return this;
			}
		}
		RoleLabel rl = new RoleLabel(role, data);
		for(Integer i : data.getSubsumers( role ))
		        rl.add(i);
		rl = data.addRidge(rl);
		Ray r = new Ray(rl.getIdentifier(), newCore.getIdentifier(), tl.getIdentifier(), data);
		r = data.addRay(r);
		//System.out.println("Ray added = "+ r.toString(data));
		Startype st = this.addRay(r.getIdentifier(), data);
		st = this.addRay(r.getIdentifier(), data);
		st.setCoreId(newCore.getIdentifier());	
		for (Integer ray : st.getRays().keySet()) {
		     Ray or = data.getRays().get(ray);
		     Ray r2 = or.getNewRayByCore((Integer)names.toArray()[0], data);
		     r2 = data.addRay(r2);
		     st=st.replaceRay(or.getIdentifier(), r2.getIdentifier(), data);
		}
		st = data.addStartype(st);
		st.setValid(st.isValid(data));
		changed = true;
		return st;
	}

	/**
	 * Rule of min.
	 * 
	 * @param concept
	 *            Concept to apply the rule.
	 */

	public boolean checkForMinRays(Integer role, Integer concept, int N, ReasonerData data) {
		int c = 0;
	        for (Integer ray : this.getRays().keySet()) {
		     //System.out.println("some role="+ data.getRoles().get(role).toString());
		     Integer ridgeId = data.getRays().get(ray).getRidgeId();
		     Integer tipId = data.getRays().get(ray).getTipId();
		     if ( data.getCores().get(tipId).contains( concept )  &&  
			     data.getRidges().get(ridgeId).contains( role )) {	 
			  c++;
		          if(c>=N) return true;	
			}
	        }
		return false;
	}
	
	

	public Startype minRule(Integer concept, boolean changed, ReasonerData data) {
		Integer role = data.getConcepts().get(concept).getRoleId();
		Integer child = data.getConcepts().get(concept).getChildren().get(0);
		Integer card = data.getConcepts().get(concept).getCardinality();
		if(checkForMinRays(role, child, card, data))
		   return this;
		Set<Integer> names = data.getMinNames(concept);
		ConceptLabel currentCore = data.getCores().get(coreId); 
		ConceptLabel newCore = currentCore.getNewConceptLabel((Integer)names.toArray()[0], data);		
		Set<Integer> rays = new HashSet();
		for (int i=0; i< card; i++) {
		     ConceptLabel tl = new ConceptLabel(child);		
		     tl = tl.getNewConceptLabel((Integer)names.toArray()[i+1], data);
		     tl = tl.getNewConceptLabel(data.getAxiomNNFs(), data);
		     RoleLabel rl = new RoleLabel(role, data);
		     for(Integer j : data.getSubsumers( role ))
		        rl.add(j);
		     rl = data.addRidge(rl);
		     Ray r = new Ray(rl.getIdentifier(), newCore.getIdentifier(), tl.getIdentifier(), data);
		     r = data.addRay(r);
		     // add the created rays to the array of distinct rays
		     rays.add(r.getIdentifier());
		     //System.out.println("Ray added = "+ r.toString(data));
	        }
		Startype st = this.addRays(rays, data);	
		st.setCoreId(newCore.getIdentifier());	
		for (Integer ray : st.getRays().keySet()) {
		     Ray or = data.getRays().get(ray);
		     Ray r = or.getNewRayByCore((Integer)names.toArray()[0], data);
		     r = data.addRay(r);
		     st=st.replaceRay(or.getIdentifier(), r.getIdentifier(), data);
		}
		st = data.addStartype(st);
		st.setValid(st.isValid(data));
		changed = true;
		return st;
	}
	
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
	public Startype allRule(Integer concept, boolean changed, ReasonerData data) {
		Integer role = data.getConcepts().get(concept).getRoleId();
		Integer child = data.getConcepts().get(concept).getChildren().get(0);
		for (Integer ray : this.getRays().keySet()) {
			Integer ridgeId = data.getRays().get(ray).getRidgeId();
			RoleLabel rl2 = data.getRidges().get(ridgeId);
			//System.out.println("some ridge="+ ridgeId);
			//System.out.println("some ridge content ="+ rl2.toString(data) );
			Integer tipId = data.getRays().get(ray).getTipId();
			ConceptLabel cl = data.getCores().get(tipId);
			//System.out.println("tip = "+ cl.toString(data)+ " id, "+tipId);
			if ( rl2.contains( role )) {
				if (cl.contains( child )) 	 
				    return this;
				else {
				    ConceptLabel tl = cl.getNewConceptLabel(child, data);
				     //System.out.println("Child = "+ data.getConcepts().get(child).toString(data));
				     //System.out.println("Old tip = "+ cl.toString(data));	
				     //System.out.println("New tip = "+ tl.toString(data));
				    Ray r = new Ray(rl2.getIdentifier(), coreId, tl.getIdentifier(), data);
				    r = data.addRay(r);
				    //System.out.println("Ray added = "+ r.toString(data));
				    Startype st = replaceRay(ray, r.getIdentifier(), data); 	
				    st = data.addStartype(st);
				    st.setValid(st.isValid(data));
				    changed = true;
				    return st;
				}
			}
		}
		return this;
	}

	/**
	 * TRANS Rule. The rule terminates when it is applied to a concept 
	 * 
	 * @param concept
	 *            Concept to apply the rule.
	 */
	public Startype transRule(Integer concept, boolean changed, ReasonerData data) {
		Integer role = data.getConcepts().get(concept).getRoleId();
		Integer child = data.getConcepts().get(concept).getChildren().get(0);
		for (Integer ray : this.getRays().keySet()) {
			Integer ridgeId = data.getRays().get(ray).getRidgeId();
			RoleLabel rl2 = data.getRidges().get(ridgeId);
			//System.out.println("trans ridge="+ ridgeId);
			//System.out.println("trans ridge content ="+ rl2.toString(data) );
			Integer tipId = data.getRays().get(ray).getTipId();
			ConceptLabel cl = data.getCores().get(tipId);
			//System.out.println("trans tip ="+ cl.toString(data) );
			//System.out.println("Role for trans role= "+ data.getRoles().get(role).toString() + " id, "+ role+", trans =" + data.getRolesForTransRule(role).toString());
			for(Integer trans : data.getRolesForTransRule(role)) {
			    Concept transAllConcept =  new Concept(trans, Type.ALL, child);
			    transAllConcept = data.addConcept(transAllConcept);
			    //System.out.println("trans concept id ="+ transAllConcept.getIdentifier()+", content= "+ transAllConcept.toString(data) );
			    if ( rl2.contains( data.getRoles().get(trans) )) {
				if ( cl.contains( transAllConcept.getIdentifier() )) 
			              return this;
				else {
				    cl = cl.getNewConceptLabel(transAllConcept.getIdentifier(), data);
				    Ray r = new Ray(rl2.getIdentifier(), coreId, cl.getIdentifier(), data);
				    r = data.addRay(r);
				    //System.out.println("Ray added = "+ r.toString(data));
				    Startype st = replaceRay(ray, r.getIdentifier(), data); 	
				    st = data.addStartype(st);
			            st.setValid(st.isValid(data));
			    	    changed = true;
		                    return st;
				} 
			    }
			}
		}
		return this;
	}

	/**
	 * CHOICE Rule. The rule terminates when it is applied to a concept 
	 * 
	 * @param concept
	 *            Concept to apply the rule.
	 */
	public Startype choiceRule(Integer concept, boolean changed, ReasonerData data) {
		Integer role = data.getConcepts().get(concept).getRoleId();
		Integer child = data.getConcepts().get(concept).getChildren().get(0);
		Integer notChild = Concept.negate(child, data);
		for (Integer ray : this.getRays().keySet()) {
			Integer ridgeId = data.getRays().get(ray).getRidgeId();
			RoleLabel rl2 = data.getRidges().get(ridgeId);
			//System.out.println("some ridge="+ ridgeId);
			//System.out.println("some ridge content ="+ rl2.toString(data) );
			Integer tipId = data.getRays().get(ray).getTipId();
			ConceptLabel cl = data.getCores().get(tipId);
			//System.out.println("tip = "+ cl.toString(data)+ " id, "+tipId);
			if ( rl2.contains( data.getRoles().get(role) )) {
				if (cl.contains( child ) || cl.contains( notChild )) 	 
				    return this;
				else {
				    ConceptLabel tl1 = cl.getNewConceptLabel(child, data);
				    ConceptLabel tl2 = cl.getNewConceptLabel(notChild, data);
				    Ray r1 = new Ray(rl2.getIdentifier(), coreId, tl1.getIdentifier(), data);
				    r1 = data.addRay(r1);
			            Ray r2 = new Ray(rl2.getIdentifier(), coreId, tl2.getIdentifier(), data);
				    r2 = data.addRay(r2);
 			            Startype st1 = replaceRay(ray, r1.getIdentifier(), data); 	
				    st1 = data.addStartype(st1);
				    Startype st2 = replaceRay(ray, r2.getIdentifier(), data); 	
				    st2 = data.addStartype(st2);
				    st2.setAncestor(st1.getIdentifier());
				    st1.getProgeny().add(st2.getIdentifier());
				    st1.setValid(st1.isValid(data));
				    st2.setValid(st2.isValid(data));
				    changed = true;
				    return st1;
				}
			}
		}
		return this;
	}
	
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
	//"role = R" and "concept = C" in <= N R C  
	public boolean checkForMaxRays(Integer role, Integer concept, int N, ReasonerData data) {
		int c = 0;
	        for (Integer ray : this.getRays().keySet()) {
		     //System.out.println("some role="+ data.getRoles().get(role).toString());
		     Integer ridgeId = data.getRays().get(ray).getRidgeId();
		     Integer tipId = data.getRays().get(ray).getTipId();
		     if ( data.getCores().get(tipId).contains( concept )  &&  
			     data.getRidges().get(ridgeId).contains( role )) {	 
			  c++;
		          if(c>N) return false;	
			}
	        }
		return true;
	}

	public boolean checkForDistinctRays(Integer ray1, Integer ray2, ReasonerData data) {
	       for(Integer i : data.getMinNames().keySet()){
		   if(data.getCores().get(this.coreId).contains(i)) {
		      for(Integer n1 : data.getMinNames().get(i)){
			  for(Integer n2 : data.getMinNames().get(i)){
			      if(data.getRays().get(ray1).tipContains(n1,data) && data.getRays().get(ray2).tipContains(n2, data))
			         return false;
		          }
		      }
		   }
	       }
	       return true;
	}
	//"role = R" and "concept = C" in <= N R C  
	public Set<Integer> selectTwoRaysForMerge(Integer role, Integer concept, ReasonerData data) {
		Set<Integer> twoRays = new HashSet<Integer>();
	        for (Integer ray1 : this.getRays().keySet()) {
		     Integer ridgeId = data.getRays().get(ray1).getRidgeId();
		     Integer tipId = data.getRays().get(ray1).getTipId();
		     if ( data.getCores().get(tipId).contains( concept )  &&  
			     data.getRidges().get(ridgeId).contains( role )) {
		        for (Integer ray2 : this.getRays().keySet()) { 
			     Integer ridgeId2 = data.getRays().get(ray2).getRidgeId();
		             Integer tipId2 = data.getRays().get(ray2).getTipId();
		             if ( data.getCores().get(tipId2).contains( concept )  &&  
			          data.getRidges().get(ridgeId2).contains( role )) {	   
			          if(checkForDistinctRays(ray1, ray2, data)) {
			             twoRays.add(ray1);
			             twoRays.add(ray2);
			             return twoRays;
			          }
			     } 	
			}
		     }
	        }
		return null;
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
	
	public Startype maxRule(Integer concept, boolean changed, ReasonerData data) {
		Integer role = data.getConcepts().get(concept).getRoleId();
		Integer child = data.getConcepts().get(concept).getChildren().get(0);
		Integer card = data.getConcepts().get(concept).getCardinality();
		if(checkForMaxRays(role, child, card, data))
		   return this;
		Set<Integer> names = data.getMinNames(concept);
		Set<Integer> twoRays = selectTwoRaysForMerge(role, child, data);
		if(twoRays==null) 
		   return this;
		Ray merged = data.getRays().get((Integer)twoRays.toArray()[0]).fusion((Integer)twoRays.toArray()[1],data);
		merged = data.addRay(merged);
		Startype st = this.replaceRay((Integer)twoRays.toArray()[0], (Integer)twoRays.toArray()[1], merged.getIdentifier(), data);
		st.setAncestor(this.getIdentifier());
		this.getProgeny().add(st.getIdentifier());	
		st = data.addStartype(st);
		st.setValid(st.isValid(data)); 
		changed = true;
		return st;		
	}
	
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
	 

	public void setAncestor(Integer s) {
		this.ancestorId = s;
	}

	public Integer getAncestor() {
		return ancestorId;
	}

	public void setExpanded(Integer s) {
		this.expandedId = s;
	}

	public Integer getExpanded() {
		return expandedId;
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
		if (this.getIdentifier() >= 0 && other.getIdentifier() >= 0) {
			if (this.getIdentifier() == other.getIdentifier() )
			   return true;
			else 
			   return false;
		}

		//System.out.println("eq 1=" + this.coreId);
		//System.out.println("eq 2=" + other.getCoreId() );

		if (! this.coreId.equals(other.getCoreId() )) 
			return false;
		//System.out.println("eq OK core");
		if( ! rays.keySet().equals(other.getRays().keySet()))
			return false;
		return true;
	}

	//@Override
	public String toString(ReasonerData data) {
		StringBuilder sb = new StringBuilder();

		sb.append("Startype " + id + System.getProperty("line.separator"));
		sb.append("Saturated: " + isSaturated() + System.getProperty("line.separator"));
		sb.append("Valid: " + isValid() + System.getProperty("line.separator"));
		sb.append("ExpandedId: " + getExpanded() + System.getProperty("line.separator"));
		sb.append("Core:" + System.getProperty("line.separator"));
		sb.append( data.getCores().get( coreId ).toString(data) );
		sb.append(System.getProperty("line.separator"));
		sb.append("Rays:" + System.getProperty("line.separator"));
		for (Integer rayId : rays.keySet() ) {
			sb.append(data.getRays().get( rayId ).toString(data) );
			sb.append(System.getProperty("line.separator"));
		}

		return sb.toString();
	}
}
