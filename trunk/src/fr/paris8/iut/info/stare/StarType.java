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
	// set of rays and how many times each of which is used to pave
	private HashMap<Integer, Integer> rays;
	// distinct rays of the startype (for the MIN rule)
	//private Map<Integer, Set<Integer>> distinctRays;
	// state of the startype
	private boolean isSaturated, isNominal;
	private Boolean isValid = null;
	// Set of pairs (startype, ray) a ray that paves with
	// The Map contains a set of pairs (startype, ray)
	private HashMap<Integer, Map<Integer, Integer>> startypesMatched;
	// nondeterministic startypes  (for the UNION, CH or MAX rule)
	// each element has (concept, set of choices) 
	private Map<Integer, Set<Integer>> progeny;
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
		coreId = null;  
		rays = new HashMap<Integer, Integer>();
		progeny = new HashMap<Integer, Set<Integer>>();
		ancestorId = null;
		expandedId = null;
		startypesMatched = new HashMap<Integer, Map<Integer,Integer>>();
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
		coreId = new Integer(clId);
		rays = new HashMap<Integer, Integer>();
		progeny = new HashMap<Integer, Set<Integer>>();
		ancestorId = null;
		expandedId = null;
		startypesMatched = new HashMap<Integer, Map<Integer,Integer>>();
	}

	public Startype(Integer clId, Integer rlId) {
		this.isNominal = false;
		this.isSaturated = false;
		coreId = new Integer(clId);
		rays = new HashMap<Integer, Integer>();
		rays.put(rlId, new Integer(0));
		progeny = new HashMap<Integer, Set<Integer>>();
		ancestorId = null;
		expandedId = null;
		startypesMatched = new HashMap<Integer, Map<Integer,Integer>>();
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
		this.coreId = new Integer(st.getCoreId());
		this.rays = new HashMap<Integer, Integer>();
		for(Integer r : st.getRays().keySet()) {
		   Integer nb = new Integer(st.getRays().get(r));
		   this.rays.put(r, nb );
		}
		this.setNominal(st.isNominal());
		this.setValid(st.isValid());
		this.setSaturated(st.isSaturated());
		if(st.getAncestor()==null)
		    this.setAncestor(null);
		else 
		    this.setAncestor(new Integer(st.getAncestor()));
		if(st.getExpanded()==null)
		    this.setExpanded(null);
		else 
		    this.setExpanded(new Integer(st.getExpanded()));
		this.progeny = new HashMap<Integer, Set<Integer>>();
	        for(Integer r : st.getProgeny().keySet() ) {
		   Set<Integer> ss = new HashSet<Integer>(st.getProgeny().get(r));
		   this.getProgeny().put(r, ss);
		}
		this.setStartypesMatched(new HashMap<Integer, Map<Integer,Integer>>());
		for(Integer r : st.getStartypesMatched().keySet() ) {
		   Map<Integer,Integer> ss = new HashMap<Integer,Integer>(st.getStartypesMatched().get(r));
		   this.getStartypesMatched().put(r, ss);
		}
	}
	 
	/**
	 * Add a new ray : ensure that all rays are different.
	 * 
	 * @param ray
	 *            Ray to add to the startype.
	 * @return true if the ray had been added, false otherwise.
	 */

	public Startype addRay(Integer ray, StartypeEvolution ste, ReasonerData data)   {
		//global ray id is also local ray id in a startype
		//since all rays of a startype are different
		Startype st = new Startype(this);
		if( ! st.getRays().containsKey( ray ) ) {
		    st.getRays().put( ray, new Integer(0) );
		    st.getStartypesMatched().put(ray, new HashMap<Integer, Integer>());
		    ste.extend(ray, data);
		    return st;
		}
		else 
		   return this;
	}

	
	public Startype addRays(Set<Integer> rs, StartypeEvolution ste, ReasonerData data)   {
		Startype st = new Startype(this);
		boolean changed = false;
		for(Integer r : rs) {
		    if( ! st.getRays().containsKey( r ) ) {
		        st.getRays().put(r, new Integer(0) );
			st.getStartypesMatched().put(r, new HashMap<Integer, Integer>());
			ste.extend(r, data);
			changed = true;
		    } 
		}
		if(changed)
		   return st;
		else 
                   return this;		
	}

	public Startype replaceRay(Integer or, Integer nr,  ReasonerData data)   {
		Startype st = new Startype(this);
		st.getRays().remove(or);		
		st.getStartypesMatched().remove(or);
		st.getStartypesMatched().put(nr, new HashMap<Integer, Integer>());
		st.getRays().put( nr, new Integer(0) );
		//ste.getIdEvolution().put(ste.getInitId(or.getIdentifier()), nr.getIdentifier());
		return st;
	}

	public Startype replaceRay(Integer or1, Integer or2, Integer nr, ReasonerData data)   {
		Startype st = new Startype(this);
		st.getRays().remove(or1);
		st.getStartypesMatched().remove(or1);
		st.getRays().remove(or2);	
		st.getStartypesMatched().remove(or2);		
		st.getRays().put( nr, new Integer(0) );
		st.getStartypesMatched().put(nr, new HashMap<Integer, Integer>());
		return st;
	}

	/*
	public Startype getStartypeByRidge(List<Integer> roles, Integer ray, ReasonerData data)   {
		Set<Integer> rSet = new HashSet();
		rSet.addAll(roles);
		Startype st = new Startype(this);
		Ray ry = data.getRays().get(ray);
		Ray newRay = ry.getNewRayByRole(rSet, data); 
		st.getRays().put(newRay.getIdentifier(), new Integer(0) );
		st.getRays().remove(ry.getIdentifier());
		//st = data.addStartype(st);	 
		return st;
	}
	*/

	public Startype getStartypeByTip(Set<Integer> concepts, Integer ray, Set<Integer> nRay,  ReasonerData data)   {
		Set<Integer> cSet = new HashSet();
		cSet.addAll(concepts);
		Startype st = new Startype(this);
		Ray ry = data.getRays().get(ray);
		Ray newRay = ry.getNewRayByTip(cSet, data); 
		 
		Integer nb = this.getRays().get(ray); 
		st.getRays().put(newRay.getIdentifier(), new Integer(nb) );
		//st.getStartypesMatched().remove(ry.getIdentifier());
		st.getStartypesMatched().remove(ray);
                Map<Integer, Integer> m1 = new HashMap<Integer,Integer>(this.getStartypesMatched().get(ray));
		st.getStartypesMatched().put(newRay.getIdentifier(), m1);
		//st.getRays().remove(ry.getIdentifier());
		st.getRays().remove(ray); 
		nRay.clear();
		nRay.add(newRay.getIdentifier()); 
		
		//st = data.addStartype(st);
		return st;
	}

	public Startype getStartypeByTip(Set<Integer> concepts, Integer ray, ReasonerData data)   {
		Set<Integer> cSet = new HashSet();
		cSet.addAll(concepts);
		Startype st = new Startype(this);
		Ray ry = data.getRays().get(ray);
		Ray newRay = ry.getNewRayByTip(cSet, data); 
		st.getRays().put(newRay.getIdentifier(), new Integer(0) );
		Map<Integer, Integer> m1 = new HashMap<Integer,Integer>(0);
		st.getStartypesMatched().put(newRay.getIdentifier(), m1);
		st.getRays().remove(ray); 
		st.getStartypesMatched().remove(ray);
		return st;
	}
	

	/**
	 * Adds a list of concepts to the core of the startype.
	 * 
	 * @param concepts
	 *            The list of concepts to be added.
	 */
	//"concepts" is a set of concepts to add in the core
	//The method does not change "startypesMatched"
	public Startype getStartypeByCore(Set<Integer> concepts, StartypeEvolution ste, ReasonerData data)   {
		Startype st = new Startype(this);
		ConceptLabel cl = data.getCores().get( coreId );
		Set<Integer> cSet = new HashSet();
		cSet.addAll(concepts);
		for(Integer i : concepts) {
		    ste.addCoreFresh(i, data);
		}
		//System.out.println("cSet="+cSet.toString());
		cl = cl.getNewConceptLabel(cSet, data); 
		if( coreId.equals(new Integer(cl.getIdentifier())) )
		   return this;		 
		st.setCoreId( cl.getIdentifier() );
		ste.setCoreId( cl.getIdentifier() );
		//update each ray since startype core is a component of each ray
		Set<Integer> rayIds = new HashSet<Integer>(st.getRays().keySet());
		for(Integer i :  rayIds) {
		     Ray ray = data.getRays().get(i).getNewRayByCore(concepts, data);
		     st.getRays().remove(i);
		     st.getRays().put( ray.getIdentifier(), new Integer(0) );
		     ste.getIdEvolution().put(ste.getInitId(i), ray.getIdentifier());
		}
		//st = data.addStartype(st);
		//System.out.println("id core Result = " +st.getCoreId() +", start type id= " + st.getIdentifier() );
		return st;
	}

	/**
	 * Check if the startype is semantically valid.<br/>
	 * 
	 * @return true if the startype is valid, false otherwise.
	 */
	//It returns true if this startype is not invalid. It is not sure that it is valid  
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
		  return true;
	}

	//This version is used for avoiding non-determinism
	public boolean isValidForAll(ReasonerData data) {
		//A startype may be invalid even if it is not saturated
		if (isSaturated) {
			for (Integer i1 : data.getCores().get(getCoreId()).getConceptIds() ) {
				Concept c1 = data.getConcepts().get(i1);
				for (Integer i2 : data.getCores().get(getCoreId()).getConceptIds() ) {
				     if (Concept.negate(i1, data).equals(i2)) {
						System.out.println("Clash : Core i1 = "+ data.getConcepts().get(i1).toString(data) + ", i2 =" + data.getConcepts().get(i2).toString(data));
						return false;
				     }
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
				     if(nbRays > nb) {
					System.out.println("Clash : nbRays = "+ nbRays + ", nb =" +nb);
					return false;
					
				     }
				}
			}
			for(Integer ray : this.getRays().keySet()){
			    Integer tipId = data.getRays().get(ray).getTipId();
			    for (Integer i1 : data.getCores().get(tipId).getConceptIds() ) {
				Concept c1 = data.getConcepts().get(i1);
				for (Integer i2 : data.getCores().get(getCoreId()).getConceptIds() ) {
				     if (Concept.negate(i1, data).equals(i2)) {
						System.out.println("Clash: Tip i1 = "+ data.getConcepts().get(i1).toString(data) + ", i2 =" + data.getConcepts().get(i2).toString(data));
						return false;
				     }
				}
			    }
		        }
			return true;
		} else {
		  
		  return false;
		}
	}
	
	/**
	 * Rule of intersection.
	 * 
	 * @param concept
	 *            Concept to apply the rule.
	 */

	//This method allows to expand the startype progressively 
	public Startype intersectionRule(Integer concept, boolean changed, Map<Integer, Set<Integer>> toExpand, ReasonerData data)   {   
		    Set<Integer> l = new HashSet();
		    boolean applied = false;
		    for(Integer c : data.getConcepts().get(concept).getChildren()) {
			if( ! data.getCores().get(coreId).contains(c) ) {
			    l.add(c);
			    applied = true;
			}
		    }
		    if(!applied ) {
			return this;
		    }
		    //Be careful. I put null for compilation
		    Startype st = getStartypeByCore(l, null, data);
		    if( ! st.isValid(data) ) {  
		        st.setValid(new Boolean(false));
		    }
                    else { 
		        st.setValid(null);
			//st.propagatingCoreChanges(data);
		        st = data.addStartype(st);
			//propagatingCoreChanges(st, l, data);
		    }
		    changed = true;
		    //System.out.println("Trying to apply rule INTER for startype "+ getIdentifier() + " on ("+ getRays().keySet().size()+")"+  data.getConcepts().get(concept).toString(data) );
		    return st;
	}

	//This version is used for avoiding non-determinism
	public Startype intersectionRule(Integer concept, StartypeEvolution ste, boolean changed, ReasonerData data)   {   
		    Set<Integer> l = new HashSet();
		    
		    boolean applied = false;
		    for(Integer c : data.getConcepts().get(concept).getChildren()) {
			if( ! data.getCores().get(coreId).contains(c) ) {
			    l.add(c);
			    ste.addCoreFresh(c, data);
			    applied = true;
			}
		    }
		    if(!applied ) {
			return this;
		    }
		    Startype st = getStartypeByCore(l, ste, data);
		    if( ! st.isValid(data) ) {  
			changed = true;
		        return null;
		    }
                    else { 
		        st.setValid(null);
		        //st = data.addStartype(st);
			//data.flushRay();
		        changed = true;
			return st;
		    }
	}

	//This version is used for avoiding non-determinism
	public Startype intersectionRuleForTip(Integer concept, Integer initRay, StartypeEvolution ste, boolean changed,  ReasonerData data)   {   
		    Set<Integer> l = new HashSet();
		    boolean applied = false;
		    Integer tipId = data.getRays().get(ste.getIdEvolution().get(initRay)).getTipId();
		    for(Integer c : data.getConcepts().get(concept).getChildren()) {
			if( ! data.getCores().get(tipId).contains(c) ) {
			    l.add(c);
			    ste.addFreshConceptForRay(c, initRay, data);
			    applied = true;
			}
		    }
		    if(!applied ) {
			return this;
		    }
		    changed = true;
		    Set<Integer> nRay = new HashSet<Integer>();
		    Startype st = this.getStartypeByTip(l, ste.getIdEvolution().get(initRay), nRay, data);
		    ste.getIdEvolution().put(initRay, (Integer)nRay.toArray()[0]);
		    if( ! st.isValid(data) ) {  	
		        return null;
		    }
                    else { 
		        st.setValid(null);
		        //st = data.addStartype(st);
			//data.flushRay();
			//System.out.println("new ray in INTER "+  nRay.toArray()[0]);
			return st;
		    }
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
	public Startype unionRule(Integer concept, boolean changed, Map<Integer, Set<Integer>> toExpand, ReasonerData data)   {
		Integer i1 = data.getConcepts().get(concept).getChildren().get(0); 
		Integer i2 = data.getConcepts().get(concept).getChildren().get(1); 
		if( data.getCores().get(coreId).contains(i1) || data.getCores().get(coreId).contains(i2)) 
		    return this;
		Set<Integer> l1 = new HashSet<Integer>();
		Set<Integer> l2 = new HashSet<Integer>();
		l1.add(i1);
		l2.add(i2);
		Startype st1 = getStartypeByCore(l1, null, data);
		Startype st2 = getStartypeByCore(l2, null, data);
		if( st1.isValid(data) && st2.isValid(data) ) {
		    st1.setAncestor(this.getIdentifier());
		    //st2.setAncestor(this.getIdentifier());
		    this.addProgeny(concept, st1.getIdentifier());
		    //this.getProgeny().add(st2.getIdentifier());
		    st1.setValid(null);
		    st1 = data.addStartype(st1);
		    //propagatingCoreChanges(st1, i1, data);
		    //st2.setValid(null);
		    //for non-determinism
		    //st2 = data.addStartype(st2);
		    changed = true;
		   //System.out.println("Trying to apply rule UNION for startype "+ getIdentifier() + " on ("+ getRays().keySet().size()+")"+  data.getConcepts().get(concept).toString(data) );
		    return st1;		       
	        }
	       if( !st1.isValid(data) && st2.isValid(data) ) {  
		    //st1.setValid(new Boolean(false));
		    st2.setValid(null); 
		    st2.setAncestor(this.getIdentifier());
		    this.addProgeny(concept, st2.getIdentifier());
		    st2 = data.addStartype(st2);
		    //propagatingCoreChanges(st2, i2, data);
		    changed = true;
		    //System.out.println("Trying to apply rule UNION for startype "+ getIdentifier() + " on ("+ getRays().keySet().size()+")"+  data.getConcepts().get(concept).toString(data) );
		    return st2;
	       }
               if( st1.isValid(data) && ! st2.isValid(data) ) {  
		    //st2.setValid(new Boolean(false));
		    st1.setValid(null); 
		    st1.setAncestor(this.getIdentifier());
		    this.addProgeny(concept, st1.getIdentifier());
		    st1 = data.addStartype(st1);
		    //propagatingCoreChanges(st1, i1, data);
		    changed = true;
		    //System.out.println("Trying to apply rule UNION for startype "+ getIdentifier() + " on ("+ getRays().keySet().size()+")"+  data.getConcepts().get(concept).toString(data) );
		    return st1;
	       } 
	       st2.setValid(new Boolean(false));
	       st1.setValid(new Boolean(false)); 
	       changed = true;
	       //System.out.println("Trying to apply rule UNION for startype "+ getIdentifier() + " on ("+ getRays().keySet().size()+")"+ data.getConcepts().get(concept).toString(data) );
	       return st1;
	}

	public Startype unionRule(Integer concept, StartypeEvolution ste, boolean changed,   ReasonerData data)   {
		Integer i1 = data.getConcepts().get(concept).getChildren().get(0); 
		Integer i2 = data.getConcepts().get(concept).getChildren().get(1); 
		if( data.getCores().get(coreId).contains(i1) || data.getCores().get(coreId).contains(i2)) 
		    return this;
		changed = true;
		Set<Integer> l1 = new HashSet<Integer>();
		Set<Integer> l2 = new HashSet<Integer>();
		l1.add(i1);
		l2.add(i2);
		StartypeEvolution ste1 = new StartypeEvolution(ste);
		StartypeEvolution ste2 = new StartypeEvolution(ste);
		Startype st1 = getStartypeByCore(l1, ste1, data);
		Startype st2 = getStartypeByCore(l2, ste2, data);
		 
	        if( st2.isValid(data) ) {  
		    st2.setValid(null); 
		    //st2 = data.addStartype(st2);
		    //data.flushRay();
		    ste.setIdEvolution(ste2.getIdEvolution());
		    ste.addCoreFresh( i2, data );
		    return st2;
	        }
                if( st1.isValid(data) ) {  
		    st1.setValid(null); 
		    //st1 = data.addStartype(st1);
		    //data.flushRay();
		    ste.setIdEvolution(ste1.getIdEvolution());
		    ste.addCoreFresh( i1 , data);
		    return st1;
	        } 
	       return null;
	}
	//This version is used for avoiding non-determinism
	public Startype unionRuleForTip(Integer concept, Integer initRay, StartypeEvolution ste, boolean changed, ReasonerData data)   {
		Integer i1 = data.getConcepts().get(concept).getChildren().get(0); 
		Integer i2 = data.getConcepts().get(concept).getChildren().get(1); 
		Integer tipId = data.getRays().get( ste.getIdEvolution().get(initRay) ).getTipId();
		if( data.getCores().get(tipId).contains(i1) || data.getCores().get(tipId).contains(i2)) {
		    return this;
		}
		changed = true;
		Set<Integer> nRay1= new HashSet<Integer>();
		Set<Integer> nRay2= new HashSet<Integer>();
		Set<Integer> l1 = new HashSet<Integer>();
		Set<Integer> l2 = new HashSet<Integer>();
		l1.add(i1);
		l2.add(i2);
		Startype st1 = getStartypeByTip(l1, ste.getIdEvolution().get(initRay), nRay1, data);
		Startype st2 = getStartypeByTip(l2, ste.getIdEvolution().get(initRay), nRay2, data);
		Set<Integer> nRay = new HashSet<Integer>();
	        if( st2.isValid(data) ) {  
		    st2.setValid(null); 
		    //st2 = data.addStartype(st2);
		    //data.flushRay();	
		    ste.getIdEvolution().put(initRay, (Integer)nRay2.toArray()[0]);
		    //System.out.println("Added by tip UNION="+ (Integer)nRay2.toArray()[0]);	    
		    return st2;
	       }
               if( st1.isValid(data) ) {  
		    st1.setValid(null); 
		    //st1 = data.addStartype(st1);
		    //data.flushRay();
		    ste.getIdEvolution().put(initRay, (Integer)nRay1.toArray()[0]);	
		    //System.out.println("Added by tip UNION="+ (Integer)nRay1.toArray()[0]); 
		    return st1;
	       }
	       return null;
	}
	
	/**
	 * Rule of some.
	 * 
	 * @param concept
	 *            Concept to apply the rule.
	 */
	
	public Startype someRule(Integer concept, boolean changed, Map<Integer, Set<Integer>> toExpand, ReasonerData data)   {
		Integer role = data.getConcepts().get(concept).getRoleId();
		Integer child = data.getConcepts().get(concept).getChildren().get(0);
		List<Integer> names = data.getSomeNames(concept);
		ConceptLabel currentCore = data.getCores().get(coreId); 
		ConceptLabel newCore = currentCore.getNewConceptLabel(names.get(0), data);
		ConceptLabel tl = new ConceptLabel(child);
		tl = tl.getNewConceptLabel(names.get(1), data);
		tl = tl.getNewConceptLabel(data.getAxiomNNFs(), data);
		for (Integer ray : this.getRays().keySet()) {
			Integer ridgeId = data.getRays().get(ray).getRidgeId();
			RoleLabel rl2 = data.getRidges().get(ridgeId);
			Integer tipId = data.getRays().get(ray).getTipId();
			Integer coreId = data.getRays().get(ray).getCoreId();
			ConceptLabel cl = data.getCores().get(tipId);
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
		Startype st = this.addRay(r.getIdentifier(), null, data);
		//st = this.addRay(r.getIdentifier(), data);
		st.setCoreId(newCore.getIdentifier());	
		Set<Integer> rayIds = new HashSet<Integer>(st.getRays().keySet());
		for (Integer ray :  rayIds) {
		     Ray or = data.getRays().get(ray);
		     Ray r2 = or.getNewRayByCore(names.get(0), data);
		     r2 = data.addRay(r2);
		     st=st.replaceRay(or.getIdentifier(), r2.getIdentifier(),  data);
		}
		if(  st.isValid(data) )  {
	            st.setValid(null);
		    st = data.addStartype(st);
		    changed = true;
		    return st;
		}
                changed = true;
		return st;
	}

	public Startype someRule(Integer concept, StartypeEvolution ste, boolean changed,   ReasonerData data)   {
		Integer role = data.getConcepts().get(concept).getRoleId();
		Integer child = data.getConcepts().get(concept).getChildren().get(0);
		List<Integer> names = data.getSomeNames(concept);
		ConceptLabel currentCore = data.getCores().get(coreId); 
		ConceptLabel newCore = currentCore.getNewConceptLabel(names.get(0), data);
		ConceptLabel tl = new ConceptLabel(child);
		tl = tl.getNewConceptLabel(names.get(1), data);
		tl = tl.getNewConceptLabel(data.getAxiomNNFs(), data);
		for (Integer ray : this.getRays().keySet()) {
			Integer ridgeId = data.getRays().get(ray).getRidgeId();
			RoleLabel rl2 = data.getRidges().get(ridgeId);
			Integer tipId = data.getRays().get(ray).getTipId();
			Integer coreId = data.getRays().get(ray).getCoreId();
			ConceptLabel cl = data.getCores().get(tipId);
			if ( cl.contains( child )  &&  rl2.contains( role )) {	 
				return this;
			}
		}
		changed = true;
		RoleLabel rl = new RoleLabel(role, data);
		for(Integer i : data.getSubsumers( role ))
		        rl.add(i);
		rl = data.addRidge(rl);
		Ray r = new Ray(rl.getIdentifier(), newCore.getIdentifier(), tl.getIdentifier(), data);
		r = data.addRay(r);
		Startype st = this.addRay(r.getIdentifier(), ste, data);		
		st.setCoreId(newCore.getIdentifier());	
		ste.setCoreId(newCore.getIdentifier());
	        Set<Integer> rayIds = new HashSet<Integer>(st.getRays().keySet());
		//System.out.println("Added ray SOME="+ r.getIdentifier()  );
		for (Integer ray : rayIds) {
		     Ray or = data.getRays().get(ray);
		     //System.out.println("new core ray SOME="+ or.getCoreId()  );
		     Ray r2 = or.getNewRayByCore(names.get(0), data);
		     //r2 = data.addRay(r2);
		     st=st.replaceRay(or.getIdentifier(), r2.getIdentifier(),  data);
                     ste.getIdEvolution().put( ste.getInitId(ray), r2.getIdentifier());
                     //if(ray.equals(r.getIdentifier()))
		     //	ste.addFreshConceptForRay( child, ste.getInitId(ray));
		}
		if( st.isValid(data))  {
	            st.setValid(null);
		    //st = data.addStartype(st);
		    //data.flushRay(); 
		    return st;
		}
		return null;
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

	public Startype minRule(Integer concept, boolean changed, Map<Integer, Set<Integer>> toExpand, ReasonerData data) {
		Integer role = data.getConcepts().get(concept).getRoleId();
		Integer child = data.getConcepts().get(concept).getChildren().get(0);
		Integer card = data.getConcepts().get(concept).getCardinality();
		if(checkForMinRays(role, child, card, data))
		   return this;
		List<Integer> names = data.getMinNames(concept);
		ConceptLabel currentCore = data.getCores().get(coreId); 
		ConceptLabel newCore = currentCore.getNewConceptLabel(names.get(0), data);		
		Set<Integer> rays = new HashSet<Integer>();
		for (int i=0; i< card; i++) {
		     ConceptLabel tl = new ConceptLabel(child);		
		     tl = tl.getNewConceptLabel(names.get(i+1), data);
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
		Startype st = this.addRays(rays, null, data);	
		st.setCoreId(newCore.getIdentifier());	
		Set<Integer> rayIds = new HashSet<Integer>(st.getRays().keySet());
		for (Integer ray : rayIds ) {
		     Ray or = data.getRays().get(ray);
		     Ray r = or.getNewRayByCore(names.get(0), data);
		     r = data.addRay(r);
		     st=st.replaceRay(or.getIdentifier(), r.getIdentifier(),  data);
		}
		
		if( ! st.isValid(data) )  
		    st.setValid(new Boolean(false));
                else {
		    st.setValid(null);
		    st = data.addStartype(st);
		}
		System.out.println("Trying to apply rule MIN for startype "+ getIdentifier() + " on ("+ getRays().keySet().size()+")"+ data.getConcepts().get(concept).toString(data) );
		changed = true;
		return st;
	}

	public Startype minRule(Integer concept, StartypeEvolution ste, boolean changed,   ReasonerData data) {
		Integer role = data.getConcepts().get(concept).getRoleId();
		Integer child = data.getConcepts().get(concept).getChildren().get(0);
		Integer card = data.getConcepts().get(concept).getCardinality();
		if(checkForMinRays(role, child, card, data))
		   return this;
		List<Integer> names = data.getMinNames(concept);
		ConceptLabel currentCore = data.getCores().get(coreId); 
		ConceptLabel newCore = currentCore.getNewConceptLabel(names.get(0), data);		
		Set<Integer> rays = new HashSet<Integer>();
		for (int i=0; i< card; i++) {
		     ConceptLabel tl = new ConceptLabel(child);		
		     tl = tl.getNewConceptLabel(names.get(i+1), data);
		     tl = tl.getNewConceptLabel(data.getAxiomNNFs(), data);
		     RoleLabel rl = new RoleLabel(role, data);
		     for(Integer j : data.getSubsumers( role ))
		        rl.add(j);
		     rl = data.addRidge(rl);
		     Ray r = new Ray(rl.getIdentifier(), newCore.getIdentifier(), tl.getIdentifier(), data);
		     r = data.addRay(r);
		     rays.add(r.getIdentifier());
		     //this.addRay(r.getIdentifier(), ste, data);
	        }
		changed = true;
		Startype st = this.addRays(rays, ste, data);	
		st.setCoreId(newCore.getIdentifier());	
		ste.setCoreId(newCore.getIdentifier());
		Set<Integer> rayIds = new HashSet<Integer>(st.getRays().keySet());
		for (Integer ray : rayIds) {
		     Ray or = data.getRays().get(ray);
		     Ray r = or.getNewRayByCore(names.get(0), data);
		     r = data.addRay(r);
		     st=st.replaceRay(or.getIdentifier(), r.getIdentifier(), data);
		     ste.getIdEvolution().put( ste.getInitId(ray), r.getIdentifier());
		     //if(ray.equals(r.getIdentifier()))
		     //	ste.addFreshConceptForRay( child, ste.getInitId(ray));
		     //System.out.println("Min added= " + st.getRays().keySet().toString() );
		}
		if( st.isValid(data) )  {
		    st.setValid(null);
		    //st = data.addStartype(st);
	            //data.flushRay();
		    return st;
                } else {
		    //System.out.println("MIN null ");
		    return null;
		}
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
	public Startype allRule(Integer concept, boolean changed, Map<Integer, Set<Integer>> toExpand, ReasonerData data) {	
		Integer role = data.getConcepts().get(concept).getRoleId();
		Integer child = data.getConcepts().get(concept).getChildren().get(0);
		Set<Integer> rayIds = new HashSet<Integer>(this.getRays().keySet());
		for (Integer ray : rayIds) {
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
				    Startype st = replaceRay(ray, r.getIdentifier(),  data); 	
				    if( ! st.isValid(data) )  
		    			st.setValid(new Boolean(false));
                		    else {
		    			st.setValid(null);
				        st = data.addStartype(st);
					propagatingTipChanges(this.getIdentifier(), st.getIdentifier(), ray, r.getIdentifier(), data);
			            }
				    changed = true;
				System.out.println("Trying to apply rule ALL for startype "+ getIdentifier() + " on ("+ getRays().keySet().size()+")"+  data.getConcepts().get(concept).toString(data) );
				    return st;
				}
			}
		}
		return this;
	}
	//this version is used for avoiding nondeterminism
	public Startype allRule(Integer concept, StartypeEvolution ste, boolean changed,   ReasonerData data) {	
		Integer role = data.getConcepts().get(concept).getRoleId();
		Integer child = data.getConcepts().get(concept).getChildren().get(0);
		Set<Integer> rayIds = new HashSet<Integer>(this.getRays().keySet());
		for (Integer ray : rayIds) {
			Integer ridgeId = data.getRays().get(ray).getRidgeId();
			RoleLabel rl2 = data.getRidges().get(ridgeId);
			Integer tipId = data.getRays().get(ray).getTipId();
			ConceptLabel cl = data.getCores().get(tipId);
			//System.out.println("Core ALL RULE 0 ="+  data.getConcepts().get(concept).toString(data));
			//System.out.println("Core ALL RULE 0  RAY="+  data.getRays().get(ray).toString(data));
			if ( rl2.contains( role )) {
				if (cl.contains( child )) 	 
				    return this;
				else {
				    //System.out.println("Core ALL RULE concept ="+  data.getCores().get(coreId).toString(data) );
				    changed = true;
				    ConceptLabel tl = cl.getNewConceptLabel(child, data);
				    Ray r = new Ray(rl2.getIdentifier(), coreId, tl.getIdentifier(), data);
				    r = data.addRay(r);
				    Startype st = this.replaceRay(ray, r.getIdentifier(), data); 
				    //System.out.println("Ray 0= "+ ray );
				     //System.out.println("Rays= "+this.getRays().keySet());
				    //System.out.println("Init for Ray 0= "+ste.getInitId(ray));		
				    ste.getIdEvolution().put(ste.getInitId(ray), r.getIdentifier() );
			            //System.out.println("Ray = "+ ray );
				    //System.out.println("Init for Ray = "+ste.getInitId(ray));
				    //System.out.println("Ray init = "+ ste.getIdEvolution().keySet() );
				    //System.out.println("Ray values = "+ ste.getIdEvolution().values() );
				    //System.out.println("New value = "+  r.getIdentifier());
				    ste.addFreshConceptForRay(child, ste.getInitId(r.getIdentifier()), data );
				    if( st.isValid(data) )  {
		    			st.setValid(null);
				        //System.out.println("New Ray added by ALL RULE  ="+ r.toString(data));
					/*
					System.out.println("Id core =" + tl.getIdentifier()+ ", ALL RULE added : Cores  ="+ data.getCores().keySet());
					Set<Integer> h = st.getRays().keySet();
				        Integer tip=null;
					for(Integer i : h){
					   if(i.equals(r.getIdentifier())) {
					      System.out.println("Ray added ");
					      tip = data.getRays().get(i).getTipId();
					   }
					}
				        System.out.println("New tip with child :"  +  data.getConcepts().get(child).toString(data) + ", idTip=" + tip+ ", TIP =" +  data.getCores().get(tip).toString(data) );
					*/
				        return st;
                		    } else {
				        return null;
			            }
				    
				}
			}
		}
		return this;
	}

	//this version is used for avoiding nondeterminism
	public Startype allRuleForTip(Integer concept, Integer ray,  StartypeEvolution ste, boolean changed,  ReasonerData data) {	
		Integer role = data.getConcepts().get(concept).getRoleId();
		Integer child = data.getConcepts().get(concept).getChildren().get(0);
		ste.addCoreFresh(child, data);
		 //System.out.println("ray init="+ray);
		 //System.out.println("ray curr="+ste.getIdEvolution().get(ray) );
		 //System.out.println("rays="+ getRays().keySet().toString());
		 //System.out.println("ste="+ ste.getIdEvolution().values().toString());
		Integer ridgeId = data.getRays().get( ste.getIdEvolution().get(ray) ).getRidgeId();
		RoleLabel rl2 = data.getRidges().get(ridgeId);
                RoleLabel inv = rl2.getInverseOf(data);
		Integer tipId = data.getRays().get(ste.getIdEvolution().get(ray)).getTipId();
		ConceptLabel core = data.getCores().get(coreId);
		if ( inv.contains( role ) ) {
		     if (core.contains( child )) 	 
			 return this;
		     else {
			//System.out.println("Tip ALL RULE concept ="+  concept.toString());
			changed = true;
			//Integer newRay=null;
			ConceptLabel tl = core.getNewConceptLabel(child, data);
			Startype st = new Startype(this);
			st.setCoreId(tl.getIdentifier());
			ste.setCoreId(tl.getIdentifier());
			Set<Integer> rayIds = new HashSet<Integer>(st.getRays().keySet());
			for(Integer r1 :  rayIds){
			    Ray r = new Ray(rl2.getIdentifier(), core.getIdentifier(), tipId, data);
			    r = data.addRay(r);
			    st = st.replaceRay(r1, r.getIdentifier(), data); 
			    ste.getIdEvolution().put(ste.getInitId(r1), r.getIdentifier() );
		        }
			if( st.isValid(data) )  {
		    	    st.setValid(null);
			    //st = data.addStartype(st);
			    //data.flushRay();
			     
			    return st;
                        } else {
			    return null;		 
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
	public Startype transRule(Integer concept, boolean changed, Map<Integer, Set<Integer>> toExpand, ReasonerData data) {
		Integer role = data.getConcepts().get(concept).getRoleId();
		Integer child = data.getConcepts().get(concept).getChildren().get(0);
		Set<Integer> rayIds = new HashSet<Integer>(this.getRays().keySet());
		for (Integer ray : rayIds) {
			Integer ridgeId = data.getRays().get(ray).getRidgeId();
			RoleLabel rl2 = data.getRidges().get(ridgeId);
			Integer tipId = data.getRays().get(ray).getTipId();
			ConceptLabel cl = data.getCores().get(tipId);
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
				    Startype st = replaceRay(ray, r.getIdentifier(),  data); 	
			            if( ! st.isValid(data) )  
		    			st.setValid(new Boolean(false));
                		    else {
		    			st.setValid(null);
				        st = data.addStartype(st);
					propagatingTipChanges(this.getIdentifier(), st.getIdentifier(), trans, r.getIdentifier(), data);
				    }
			    	    changed = true;
			            System.out.println("Trying to apply rule TRANSALL for startype "+ getIdentifier() + " on ("+ getRays().keySet().size()+")"+  data.getConcepts().get(concept).toString(data) );
		                    return st;
				} 
			    }
			}
		}
		return this;
	}
	//this version is used for avoiding nondeterminism
	public Startype transRule(Integer concept, StartypeEvolution ste, boolean changed,    ReasonerData data) {
		Integer role = data.getConcepts().get(concept).getRoleId();
		Integer child = data.getConcepts().get(concept).getChildren().get(0);
		Set<Integer> rayIds = new HashSet<Integer>(this.getRays().keySet());
		for (Integer ray : rayIds) {
			Integer ridgeId = data.getRays().get(ray).getRidgeId();
			RoleLabel rl2 = data.getRidges().get(ridgeId);
			Integer tipId = data.getRays().get(ray).getTipId();
			ConceptLabel cl = data.getCores().get(tipId);
			for(Integer trans : data.getRolesForTransRule(role)) {
			    Concept transAllConcept =  new Concept(trans, Type.ALL, child);
			    transAllConcept = data.addConcept(transAllConcept);
			    //System.out.println("Core TRANSRULE concept id ="+ transAllConcept.getIdentifier()+", content= "+ transAllConcept.toString(data) );
			    if ( rl2.contains( data.getRoles().get(trans) )) {
				if ( cl.contains( transAllConcept.getIdentifier() )) 
			              return this;
				else {
				    changed = true;	
				    cl = cl.getNewConceptLabel(transAllConcept.getIdentifier(), data);
				    Ray r = new Ray(rl2.getIdentifier(), coreId, cl.getIdentifier(), data);
				    r = data.addRay(r);
				    Startype st = this.replaceRay(ray, r.getIdentifier(),  data); 
				    ste.getIdEvolution().put(ste.getInitId(ray), r.getIdentifier() );
				    ste.addFreshConceptForRay(child, ste.getInitId(ray), data);	
			            if( st.isValid(data) )  {
		    			st.setValid(null);
				        //st = data.addStartype(st);
					//data.flushRay();	
				                  
		                        return st;
                		    } else {			            
		                        return null;
				    }    
				} 
			    }
			}
		}
		return this;
	}
	//this version is used for avoiding nondeterminism
	public Startype transRuleForTip(Integer concept, Integer ray, StartypeEvolution ste, boolean changed,    ReasonerData data) {
		Integer role = data.getConcepts().get(concept).getRoleId();
		Integer child = data.getConcepts().get(concept).getChildren().get(0);
		ste.addCoreFresh(child, data);
	        Integer ridgeId = data.getRays().get(ste.getIdEvolution().get(ray) ).getRidgeId();
		RoleLabel rl2 = data.getRidges().get(ridgeId);
		RoleLabel inv = rl2.getInverseOf( data);
		Integer tipId = data.getRays().get( ste.getIdEvolution().get(ray) ).getTipId();
		ConceptLabel core = data.getCores().get(coreId);
		for(Integer trans : data.getRolesForTransRule(role)) {
		    Concept transAllConcept =  new Concept(trans, Type.ALL, child);
		    transAllConcept = data.addConcept(transAllConcept);    
		    if ( inv.contains( data.getRoles().get(trans) )) {
				if ( core.contains( transAllConcept.getIdentifier() )) 
			              return this;
				else {
				    changed = true;
				    Integer newRay=null;
				    ConceptLabel tl = core.getNewConceptLabel(transAllConcept.getIdentifier(), data);
				    Startype st = new Startype(this);
			            st.setCoreId(tl.getIdentifier());
				    Set<Integer> rayIds = new HashSet<Integer>(st.getRays().keySet());
				    //System.out.println("Tip TRANSRULE concept id ="+  transAllConcept.toString(data));
				    for(Integer r1 : rayIds){
			                Ray r = new Ray(rl2.getIdentifier(), core.getIdentifier(), tipId, data);
			                r = data.addRay(r);
			                st = st.replaceRay(r1, r.getIdentifier(), data); 
					ste.getIdEvolution().put(ste.getInitId(r1), r.getIdentifier() );			    
		                    }
				    if( st.isValid(data) )  {
		    			st.setValid(null);
				         
				        return st;
                        	    } else {
				        return null;		 
		                    } 
				} 
	            }
		}
		return this;
	}

	//It may transform some startypes from saturated to non-saturated 
	//"concept" is added to tip, "source" is startype whose core is the tip, 
	public Map<Integer, Integer>  changingTip(Set<Integer> concepts, Integer source, Integer des, Integer ray,   ReasonerData data){
		    Startype copiedSt = new Startype(data.getStartypes().get(des));
		    Set<Integer> nRay= new HashSet<Integer>();
		    Startype newSt = this.getStartypeByTip(concepts, ray, nRay,  data);
		    newSt.setSaturated(false);
		    newSt = data.addStartype(newSt);
		    Map<Integer, Integer> m1 =  new HashMap<Integer,Integer>();
		    m1.put(source, ray);
		    newSt.getStartypesMatched().remove((Integer)nRay.toArray()[0] );	
		    newSt.getStartypesMatched().put((Integer)nRay.toArray()[0], m1);
		    Map<Integer,Integer> changed = new HashMap<Integer,Integer>();
		    changed.put(newSt.getIdentifier(), (Integer)nRay.toArray()[0]);
		    return changed;
	}

	//It may transform some startypes from saturated to non-saturated 
	//"st" is related startype, "concept" : concept added   
	public void propagatingCoreChanges(Integer st, HashSet<Integer> concepts,  ReasonerData data){
		    Startype copiedSt = new Startype(data.getStartypes().get(st));
		    copiedSt.setSaturated(false);
		    Startype newSt = copiedSt.getStartypeByCore(concepts, null, data);
		    newSt = data.addStartype(newSt);
 		    for(Integer ray : newSt.getRays().keySet()) {
			Map<Integer, Integer> matcheds = newSt.getStartypesMatched().get(ray);
		        for(Integer j : matcheds.keySet()) {
			    Integer r = matcheds.get(j);
			    Map<Integer,Integer> changed = changingTip(concepts, newSt.getIdentifier(), j, r, data);
			    Map<Integer, Integer> m1 = data.getStartypes().get(j).getStartypesMatched().get(r);
			    m1.remove(st);
			    Integer key = (Integer)changed.keySet().toArray()[0];
			    m1.put( key, changed.get(key));
 		        }	
		    }
	}

	//It may transform some startypes from saturated to non-saturated 
	//"oSt" and "oRay" are old startype and ray; "nSt" and "nRay" are changed startype and ray
	//"concept" is the concept added to tip
	public void propagatingTipChanges(Integer nSt, Integer oRay, Integer nRay, Integer concept,  ReasonerData data){ 
		    Map<Integer, Integer> matcheds = data.getStartypes().get(this.getIdentifier()).getStartypesMatched().get(oRay);
		    //processing pairs (st, ray)
		    for(Integer j : matcheds.keySet()) {
			Integer r = matcheds.get(j);
			Map<Integer, Integer> m1 = data.getStartypes().get(j).getStartypesMatched().get(r);
			m1.remove(this.getIdentifier());
			m1.put(nSt, nRay);
			if( data.getStartypes().get(j).getCoreId().equals(data.getRays().get(nRay).getTipId()) )
			    return;
			propagatingCoreChanges(j, new HashSet<Integer>(data.getRays().get(nRay).getTipId()),  data);
 		    }	
	}

	/**
	 * CHOICE Rule. The rule terminates when it is applied to a concept 
	 * 
	 * @param concept
	 *            Concept to apply the rule.
	 */
	public Startype choiceRule(Integer concept, boolean changed, Map<Integer, Set<Integer>> toExpand, ReasonerData data) {
		Integer role = data.getConcepts().get(concept).getRoleId();
		Integer child = data.getConcepts().get(concept).getChildren().get(0);
		Integer notChild = Concept.negate(child, data);
		Set<Integer> rayIds = new HashSet<Integer>(this.getRays().keySet());
		for (Integer ray :  rayIds) {
			Integer ridgeId = data.getRays().get(ray).getRidgeId();
			RoleLabel rl2 = data.getRidges().get(ridgeId);
			Integer tipId = data.getRays().get(ray).getTipId();
			ConceptLabel cl = data.getCores().get(tipId);
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
 			            Startype st1 = replaceRay(ray, r1.getIdentifier(),  data); 	
				    Startype st2 = replaceRay(ray, r2.getIdentifier(),  data); 	
                                    if( st1.isValid(data) && st2.isValid(data) ) {
				       st1.setAncestor(this.getIdentifier());
				       //st2.setAncestor(this.getIdentifier());
				       st1.setValid(null);
				       //st2.setValid(null);	
				       this.addProgeny(concept,st1.getIdentifier());
				       //this.addProgeny(concept,st2.getIdentifier());
				       st1 = data.addStartype(st1);
				       this.propagatingTipChanges(st1.getIdentifier(), ray, r1.getIdentifier(), child,  data);
				       changed = true;
					//System.out.println("Trying to apply rule CHOICE for startype "+ getIdentifier() + " on ("+ getRays().keySet().size()+")"+ data.getConcepts().get(concept).toString(data) );
				       return st1;
				    }
				    if( ! st1.isValid(data) && st2.isValid(data) ) {  
		    			//st1.setValid(new Boolean(false));
					st2.setValid(null); 
					st2.setAncestor(this.getIdentifier());
				        this.addProgeny(concept, st2.getIdentifier());
		     			st2 = data.addStartype(st2);
					this.propagatingTipChanges( st2.getIdentifier(), ray, r2.getIdentifier(), notChild,  data);
					changed = true;
					//System.out.println("Trying to apply rule CHOICE for startype "+ getIdentifier() + " on ("+ getRays().keySet().size()+")"+ data.getConcepts().get(concept).toString(data) );
				        return st2;
				    }
                		    if( st1.isValid(data) && ! st2.isValid(data) ) {  
		    			//st2.setValid(new Boolean(false));
					st1.setValid(null); 
					st1.setAncestor(this.getIdentifier());
				        this.addProgeny(concept, st1.getIdentifier());
		     			st1 = data.addStartype(st1);
					this.propagatingTipChanges( st1.getIdentifier(), ray, r1.getIdentifier(), child,  data);
					changed = true;
					//System.out.println("Trying to apply rule CHOICE for startype "+ getIdentifier() + " on ("+ getRays().keySet().size()+")"+ data.getConcepts().get(concept).toString(data) );
				        return st1;
				    } 
				    st2.setValid(new Boolean(false));
				    st1.setValid(new Boolean(false)); 
				    changed = true;
				    //System.out.println("Trying to apply rule CHOICE for startype "+ getIdentifier() + " on ("+ getRays().keySet().size()+")"+  data.getConcepts().get(concept).toString(data) );
				    return st1;
				}
			}
		}
		return this;
	}

	//This version is used for avoiding nondeterminism
	public Startype choiceRule(Integer concept, StartypeEvolution ste, boolean changed,  ReasonerData data) {
		Integer role = data.getConcepts().get(concept).getRoleId();
		Integer child = data.getConcepts().get(concept).getChildren().get(0);
		Integer notChild = Concept.negate(child, data);
		Set<Integer> rayIds = new HashSet<Integer>(this.getRays().keySet());
		for (Integer ray :  rayIds) {
			Integer ridgeId = data.getRays().get(ray).getRidgeId();
			RoleLabel rl2 = data.getRidges().get(ridgeId);
			Integer tipId = data.getRays().get(ray).getTipId();
			ConceptLabel cl = data.getCores().get(tipId);
			if ( rl2.contains( data.getRoles().get(role) )) {
				if (cl.contains( child ) || cl.contains( notChild )) 	 
				    return this;
				else {
				    changed = true;
				    ConceptLabel tl1 = cl.getNewConceptLabel(child, data);
				    ConceptLabel tl2 = cl.getNewConceptLabel(notChild, data);
				    Ray r1 = new Ray(rl2.getIdentifier(), coreId, tl1.getIdentifier(), data);
				    r1 = data.addRay(r1);
			            Ray r2 = new Ray(rl2.getIdentifier(), coreId, tl2.getIdentifier(), data);
				    r2 = data.addRay(r2);
 			            Startype st1 = replaceRay(ray, r1.getIdentifier(),  data); 	
				    Startype st2 = replaceRay(ray, r2.getIdentifier(),  data); 	
                                    if( st1.isValid(data)   ) {
				       st1.setValid(null);
				       //st1 = data.addStartype(st1);
				       //data.flushRay();
				       ste.getIdEvolution().put(ste.getInitId(ray), r1.getIdentifier() );
				       ste.addCoreFresh(child, data);
				       return st1;
				    }
				    if( st2.isValid(data)   ) {
				       st2.setValid(null);
				       //data.flushRay();
				       //st2 = data.addStartype(st2);
				       ste.getIdEvolution().put(ste.getInitId(ray), r2.getIdentifier() );
				       ste.addCoreFresh(notChild, data);
				       return st2;
				    }
				    return null;
				}
			}
		}
		return this;
	}

	//This version is used for avoiding nondeterminism
	public Startype choiceRuleForTip(Integer concept, Integer ray,  StartypeEvolution ste,  boolean changed, ReasonerData data) {
		Integer role = data.getConcepts().get(concept).getRoleId();
		Integer child = data.getConcepts().get(concept).getChildren().get(0);
		Integer notChild = Concept.negate(child, data);
		Integer ridgeId = data.getRays().get( ste.getIdEvolution().get(ray) ).getRidgeId();
		RoleLabel rl2 = data.getRidges().get(ridgeId);
		Integer tipId = data.getRays().get( ste.getIdEvolution().get(ray) ).getTipId();
		RoleLabel inv = rl2.getInverseOf(data);
		ConceptLabel core = data.getCores().get(coreId);
		Map<Integer, Integer> m1 = new HashMap<Integer,Integer>();
		Map<Integer, Integer> m2 = new HashMap<Integer,Integer>();
		if ( inv.contains( data.getRoles().get(role) )) {
		     if (core.contains( child ) || core.contains( notChild )) 	 
			 return this;
		     else {
			 changed = true;
			 Integer newRay=null;
			 ConceptLabel tl1 = core.getNewConceptLabel(child, data);
		         ConceptLabel tl2 = core.getNewConceptLabel(notChild, data);
			 Ray r1 = new Ray(rl2.getIdentifier(), coreId, tl1.getIdentifier(), data);
			 r1 = data.addRay(r1);
			 Ray r2 = new Ray(rl2.getIdentifier(), coreId, tl2.getIdentifier(), data);
			 r2 = data.addRay(r2);
			 Startype st1 = new Startype(this);
			 Startype st2 = new Startype(this);
 			 st1 = replaceRay(ste.getIdEvolution().get(ray), r1.getIdentifier(), data);
		         st2 = replaceRay(ste.getIdEvolution().get(ray), r2.getIdentifier(), data); 
			 Set<Integer> rayIds = new HashSet<Integer>(st1.getRays().keySet());
			 for(Integer r3 : rayIds){
			    Ray r = new Ray(rl2.getIdentifier(), core.getIdentifier(), tipId, data);
			    r = data.addRay(r);
			    st1 = st1.replaceRay(r3, r.getIdentifier(), data); 	
                            st1.setCoreId(tl1.getIdentifier());	  
			    m1.put(ste.getInitId(r3), r.getIdentifier());
			     
		         }
			 rayIds = new HashSet<Integer>(st2.getRays().keySet());
			 for(Integer r3 : rayIds){
			    Ray rr = new Ray(rl2.getIdentifier(), core.getIdentifier(), tipId, data);
			    rr = data.addRay(rr);
			    st2 = st2.replaceRay(r3, rr.getIdentifier(), data); 
			    st2.setCoreId(tl2.getIdentifier());	
			    m2.put(ste.getInitId(r3), rr.getIdentifier());		    
		         }
                         if( st1.isValid(data)   ) {
			     st1.setValid(null);       
			     //st1 = data.addStartype(st1);
			     //data.flushRay();
			     ste.setCoreId(st1.getCoreId());
			     for(Integer i: m1.keySet()){
			        ste.getIdEvolution().put(i, m1.get(i) );
			     }
			     ste.addCoreFresh(child, data);				    
			     return st1;
		         }
		         if( st2.isValid(data)   ) {
			     st2.setValid(null);
			     //st2 = data.addStartype(st2);
			     //data.flushRay();
			     ste.setCoreId(st2.getCoreId());
			     for(Integer i: m2.keySet()){
			        ste.getIdEvolution().put(i, m2.get(i) );
			     }
			     ste.addCoreFresh(notChild, data);
			     return st2;
			 }
			 return null;	
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
			      if(! n1.equals(n2)) {
			         if(data.getRays().get(ray1).tipContains(n1,data) && data.getRays().get(ray2).tipContains(n2, data))
			         return false;
			      }
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
	//"role = R" and "concept = C" in <= N R C  
	//for avoiding nondeterminism
	public Map<Integer,Integer> selectTwoRaysForMerge(Integer role, Integer concept, Set<Map<Integer, Integer>> triedRays, ReasonerData data) {
		Map<Integer, Integer> twoRays = new HashMap<Integer,Integer>();
	        for (Integer ray1 : this.getRays().keySet()) {
		     Integer ridgeId = data.getRays().get(ray1).getRidgeId();
		     Integer tipId = data.getRays().get(ray1).getTipId();
		     if ( data.getCores().get(tipId).contains( concept )  &&  
			     data.getRidges().get(ridgeId).contains( role )) {
		        for (Integer ray2 : this.getRays().keySet()) { 
			     if(!ray2.equals(ray1)) {
			     Integer ridgeId2 = data.getRays().get(ray2).getRidgeId();
		             Integer tipId2 = data.getRays().get(ray2).getTipId();
		             if ( data.getCores().get(tipId2).contains( concept )  &&  
			          data.getRidges().get(ridgeId2).contains( role )) {	
				  //if(checkForDistinctRays(ray1, ray2, data))  System.out.println("Names distincts");   
				  //	else  System.out.println("Names not distincts");
			          if(checkForDistinctRays(ray1, ray2, data)) {
				     if(triedRays.size()==0) {
				         twoRays.put(ray1, ray2);
					 //System.out.println("Max ray 1 empty="+  ray1);//data.getRays().get(ray1).toString(data)
	                                 //System.out.println("Max ray 2 empty="+  ray2);
					 return twoRays;
				     }
				     for(Map<Integer,Integer> m : triedRays) {
					 Integer key = (Integer)m.keySet().toArray()[0];
					 Integer val = m.get(key);
					 if( (key.equals(ray1) &&  val.equals(ray2)) || (key.equals(ray2) &&  val.equals(ray1)) )
					     break;
			                 twoRays.put(ray1, ray2);
					 //System.out.println("Max ray 1="+  ray1);//data.getRays().get(ray1).toString(data)
	                                 //System.out.println("Max ray 2="+  ray2);
			                 return twoRays;
				     }
				     
			          }
			     } 	
                             }
			}
		     }
	        }
		//System.out.println("Max ray 1 NULL");
		return null;
	}

	//"st" and "ray" are changed startype and rays 
	public void propagatingRayChanges(Integer ray, Map<Integer, Set<Integer>> toExpand, ReasonerData data){ 
		    Map<Integer, Integer> matcheds = data.getStartypes().get(this.getIdentifier()).getStartypesMatched().get(ray);
		    //processing pairs (st, ray)
		    for(Integer j : matcheds.keySet()) {
			Integer r = matcheds.get(j);
			Map<Integer, Integer> m1 = data.getStartypes().get(j).getStartypesMatched().get(r);
			m1.remove(this.getIdentifier());
			int nb = ((Integer)data.getStartypes().get(j).getRays().get(r)).intValue();
			if(nb==1) {
			   if( toExpand.containsKey(j) )
			       toExpand.get(j).add(r);
			   else {
			       Set<Integer> ss = new HashSet<Integer>();
			       ss.add(r);
			       toExpand.put(j,ss);
			   }
			}
			data.getStartypes().get(j).getRays().put(r, new Integer(nb-1));
		    }	
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
	
	public Startype maxRule(Integer concept, boolean changed, Map<Integer, Set<Integer>> toExpand, ReasonerData data) {
		Integer role = data.getConcepts().get(concept).getRoleId();
		Integer child = data.getConcepts().get(concept).getChildren().get(0);
		Integer card = data.getConcepts().get(concept).getCardinality();
		if(checkForMaxRays(role, child, card, data))
		   return this;
		Set<Integer> twoRays = selectTwoRaysForMerge(role, child, data);
		if(twoRays==null) 
		   return this;
		this.propagatingRayChanges( (Integer)twoRays.toArray()[0], toExpand, data);
		this.propagatingRayChanges( (Integer)twoRays.toArray()[1], toExpand, data);
		Ray merged = data.getRays().get((Integer)twoRays.toArray()[0]).fusion((Integer)twoRays.toArray()[1],data);
		merged = data.addRay(merged);
		Startype st = this.replaceRay((Integer)twoRays.toArray()[0], (Integer)twoRays.toArray()[1], merged.getIdentifier(), data);
		if( ! st.isValid(data) )  
		     st.setValid(new Boolean(false));
                else { 
		     st.setValid(null); 
		     st.setAncestor(this.getIdentifier());
		     this.addProgeny(concept, st.getIdentifier());
		     st = data.addStartype(st);
		     toExpand.put(st.getIdentifier(), new HashSet<Integer>(merged.getIdentifier()));
		}
		changed = true;
		//System.out.println("Trying to apply rule MAX for startype "+ getIdentifier() + " on ("+ getRays().keySet().size()+")"+  data.getConcepts().get(concept).toString(data) );
		return st;		
	}

	//This version is used for avoiding nondeterminism
	public Startype maxRule(Integer concept, StartypeEvolution ste, boolean changed,   ReasonerData data) {
		Integer role = data.getConcepts().get(concept).getRoleId(); 
		Integer child = data.getConcepts().get(concept).getChildren().get(0);
		Integer card = data.getConcepts().get(concept).getCardinality();
		if(checkForMaxRays(role, child, card, data))
		   return this;
		//Set<Integer> names = data.getMinNames(concept);
		Set<Map<Integer, Integer>> triedRays = new HashSet<Map<Integer, Integer>>();
		Map<Integer,Integer> twoRays = null;
		changed = true;
		while( (twoRays = selectTwoRaysForMerge(role, child, triedRays, data)) != null) {
			triedRays.add(twoRays);
			Integer key = (Integer)twoRays.keySet().toArray()[0];
			Integer val = twoRays.get(key);
			 
			Ray merged = data.getRays().get(key).fusion(val,data);
			merged = data.addRay(merged);
			Startype st = this.replaceRay(key, twoRays.get(key), merged.getIdentifier(), data);
			//ste.getIdEvolution().put( ste.getInitId(ray), r2.getIdentifier());
			Integer i1 = ste.getInitId(key);
			Integer i2 = ste.getInitId(twoRays.get(key));
			Set<Integer> processedConcepts = new HashSet<Integer>(ste.getProcessedConceptsForRay().get(i1) );
			processedConcepts.addAll(ste.getProcessedConceptsForRay().get(i2) );
			Set<Integer> freshConcepts = new HashSet<Integer>(ste.getFreshConceptsForRay().get(i1) );
			freshConcepts.addAll(ste.getFreshConceptsForRay().get(i2) );
			ste.getIdEvolution().remove(i1);
			ste.getIdEvolution().remove(i2);
			ste.getProcessedConceptsForRay().remove(i1);
			ste.getProcessedConceptsForRay().remove(i2);
			ste.getFreshConceptsForRay().remove(i1);
			ste.getFreshConceptsForRay().remove(i2);
			ste.extend( processedConcepts, freshConcepts, merged.getIdentifier(), data);
			if( st.isValid(data) )  {
			     st.setValid(null); 
		     	     //st = data.addStartype(st);
			     //data.flushRay();
			     
			     return st;
			}     
		}	 
		return null;
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

	public void setValid(Boolean b) {
		this.isValid = b;
	}

	public Boolean isValid() {
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

	public Map<Integer, Map<Integer,Integer>> getStartypesMatched() {
		return startypesMatched;
	}

	public void setStartypesMatched(HashMap<Integer, Map<Integer,Integer>>  m) {
		startypesMatched = m;
	}

	public void addStartypesMatched(Integer rId, Integer st, Integer ray) {
		if(startypesMatched.containsKey(rId))
	           startypesMatched.get(rId).put(st,ray);
                else {
		   Map<Integer,Integer> ss = new HashMap<Integer,Integer>();
		   ss.put(st,ray);
		   startypesMatched.put(rId, ss);
		}
	}

	public Map<Integer, Set<Integer>> getProgeny() {
		return progeny;
	}

	public void setProgeny(Map<Integer, Set<Integer>> p) {
		progeny = p;
	}

	public void addProgeny(Integer cId, Integer stId) {
		if(progeny.containsKey(cId))
	           progeny.get(cId).add(stId);
                else {
		   Set ss = new HashSet<Integer>(stId);
		   progeny.put(cId, ss);
		}
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
		//sb.append("ExpandedId: " + getExpanded() + System.getProperty("line.separator"));
		sb.append("Core:" + coreId + System.getProperty("line.separator"));
		sb.append( data.getCores().get( coreId ).toString(data) );
		sb.append(System.getProperty("line.separator"));
		sb.append("Rays:" + "("+ rays.keySet().size()+")"+ System.getProperty("line.separator"));
		for (Integer rayId : rays.keySet() ) {
			sb.append(data.getRays().get( rayId ).toString(data) );
			sb.append(System.getProperty("line.separator"));
		}

		return sb.toString();
	}

	public String toShortString(ReasonerData data) {
		StringBuilder sb = new StringBuilder();

		sb.append("Startype " + id + System.getProperty("line.separator"));
		sb.append("Saturated: " + isSaturated() + System.getProperty("line.separator"));
		sb.append("Valid: " + isValid() + System.getProperty("line.separator"));
		//sb.append("ExpandedId: " + getExpanded() + System.getProperty("line.separator"));
		sb.append("Core:" + coreId  + System.getProperty("line.separator"));
		sb.append( data.getCores().get( coreId ).toString(data) );
		sb.append(System.getProperty("line.separator"));
		sb.append("Rays:" + "("+ rays.keySet().size()+")"+ System.getProperty("line.separator"));
		//for (Integer rayId : rays.keySet() ) {
		//	sb.append(data.getRays().get( rayId ).toString(data) );
		//	sb.append(System.getProperty("line.separator"));
		//}

		return sb.toString();
	}
}
