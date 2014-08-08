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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.lang.CloneNotSupportedException;

public class Frame {
	// identity
	private int id;
	//private Map<Integer, Startype> frame = new HashMap<Integer, Startype>(); 
	private int numberOfNominals;
	//a set of rays to match for a startype
	private Map<Integer, Set<Integer>> startypesToMatch;
	private Set<Integer> startypesToSaturate;
	private Map<Integer, Startype> stars;
	//for debug
	private int coreRuleApplications=0;
	private int coreCheckApplications=0;
	private int tipRuleApplications=0;
	private int tipCheckApplications=0;
	private int ruleApplications=0;
	private int checkApplications=0;
	private int clash=0;
	private int newSomes=0;
	private int newMin=0;
	/**
	 * Creation wof the frame.
	 * 
	 * @param id
	 *            The id for the frame.
	 */
	public Frame(int id) {
		this.id = id;
		//this.lastIdOfStartype = 0;
		//stars = new HashSet<Startype>();
		startypesToSaturate = new HashSet<Integer>();
		startypesToMatch = new HashMap<Integer, Set<Integer>>();
		stars = new HashMap<Integer, Startype>();
	}
	 
	public int getCoreRuleApplications(){
		return coreRuleApplications;
	}

	public int getCoreCheckApplications(){
		return coreCheckApplications;
	}

	public int getTipRuleApplications(){
		return tipRuleApplications;
	}

	public int getTipCheckApplications(){
		return tipCheckApplications;
	}

	 
	public Startype init(ReasonerData data)   {
		Startype st = new Startype( data.getNNFConceptLabel() );
		st = data.addStartype(st);
		//stars.put(st.getIdentifier(), st);
		//frame.put(st.getIdentifier(), st);
		//st = applyRules(st, data);
		return st;
	}
	//It applies rules to saturate each startype of the frame
	//A saturated startype may become unsaturated by expanding  
	public void applyRulesToFrame(ReasonerData data) {
		Startype sat = null;
		while( startypesToSaturate.size() > 0 ) {
		      Startype st = data.getStartypes().get(startypesToSaturate.toArray()[0]);
		      Set<Integer> concepts = new HashSet<Integer>(data.getCores().get(st.getCoreId()).getConceptIds());
		      boolean saturated = false;
                      while(! saturated ) {
		      //"for" terminates when a new startype is created, or no new startype is created  for all concepts in the core visited 
		      for(Integer concept :  concepts) {
			  Set<Integer> changedRays = null;
			  Integer changedCore;
			  boolean changed = false;
			  if (! data.getConcepts().get(concept).isTerminal() ) {
			  switch (data.getConcepts().get(concept).getOperator()) {
			  case INTERSECTION:
				sat = st.intersectionRule(concept,  changed, startypesToMatch, data);
				break;
			  case UNION:
				sat = st.unionRule(concept, changed, startypesToMatch, data);
				break;
			  case SOME:
				sat = st.someRule(concept, changed, startypesToMatch, data);
				break;
			  case ALL:
				sat = st.allRule(concept, changed,  startypesToMatch, data);
				sat = st.transRule(concept, changed,  startypesToMatch, data);
				break;
			  case MIN:
				sat = st.minRule(concept, changed,  startypesToMatch, data);
				break;
			  case MAX:
				sat = st.maxRule(concept, changed,  startypesToMatch, data);
				sat = st.choiceRule(concept, changed,  startypesToMatch,data);
				break;
			  default:
				break;
			  }
		          }
			  if(! st.equals(sat)) {
			     //remove "st" that was expanded : rethink
		             data.getStartypes().remove(st.getIdentifier());
			     st = sat;
			     concepts = new HashSet<Integer>(data.getCores().get(st.getCoreId()).getConceptIds());
			     saturated = false;	
			     ruleApplications++;
			     break;	
			  } else {
			     //"saturated = true" only if "saturated = true" for each iteration of for 
                             saturated = true;
			  }
		     }//for
	           }//while(! saturated ) {
		   sat.setSaturated(true);
		}//while( startypesToExpand.size() > 0 ) 
	}
	 
	//It returns (startype, ray2) where "startype" contains "ray2" which can connect to "ray"
	public Map<Integer, Integer> getMatch(Integer ray, ReasonerData data) {
	       for(Integer st : data.getStartypes().keySet()){
		   for(Integer r : data.getStartypes().get(st).getRays().keySet()){
		       if(data.getRays().get(r).isInverseOf(ray, data)) {
			  Map<Integer, Integer> str = new HashMap();
		          str.put(st, r);
                          return str;
		       }
		   }
	       }
	       return null;
	}
	// toPave=(startype, ray)
	// It matches "toMatch" to "matchable" 
	public void matching(Map<Integer,Integer> toMatch, Map<Integer,Integer> matchable, ReasonerData data) {
		  Integer st1 = (Integer)toMatch.keySet().toArray()[0];
		  Integer st2 = (Integer)matchable.keySet().toArray()[0];
		  Integer ray1 = toMatch.get(st1);
		  Integer ray2 = matchable.get(st2);
		  int nb1 = ((Integer)data.getStartypes().get(st1).getRays().get(ray1)).intValue();
		  int nb2 = ((Integer)data.getStartypes().get(st2).getRays().get(ray2)).intValue();
		  data.getStartypes().get(st1).getRays().put(ray1, new Integer(nb1+1));
		  Map<Integer,Integer> matcheds = data.getStartypes().get(st1).getStartypesMatched().get(ray1);
		  //add pair (startype,ray)
		  matcheds.put(st2, ray2);  
		  data.getStartypes().get(st1).getStartypesMatched().put(ray1, matcheds);
		  data.getStartypes().get(st2).getRays().put(ray2, new Integer(nb2+1));
		  matcheds = data.getStartypes().get(st2).getStartypesMatched().get(ray2);
		  //add pair (startype,ray)
		  matcheds.put(st1,ray1); 
		  data.getStartypes().get(st2).getStartypesMatched().put(ray2, matcheds);
	}

	/**
	 * Paving a frame from created startypes and generating new ones
	 * Nominals are not taken into account
	 */

	//The method starts with a startype that is not matched.
	//That startype will match another one via each ray
	//Saturation by rules makes startypes saturated and propagates changes  
	//It returns "true" if every startype matched another one and is saturated
	//It returns "false" when every startype is saturated but there is a startype that cannot match any startype 
	public boolean buildFrame(ReasonerData data) {
	       boolean matched = false;
	       Set<Integer> matchedRays = new HashSet<Integer>();
	       while ( ! startypesToMatch.isEmpty() ) {
		 Integer sId = (Integer)startypesToMatch.keySet().toArray()[0];
		 Integer rId = (Integer)startypesToMatch.get(sId).toArray()[0];
		 Map<Integer, Integer> toMatch = new HashMap<Integer, Integer>();
		 toMatch.put(sId, rId);
		 Map<Integer, Integer> matchable = getMatch(rId, data);
		 //If the method has already tried to match the ray in "toMatch" but is is always not matched
		 //then this frame is not a model
		 if(matchable == null && matchedRays.contains(rId)) 
                    return false;
                 matchedRays.add(rId);
		 if(matchable!=null) 
			matching(toMatch, matchable, data);
		 else {
                    	//create a new startype corresponding to the ray "r" in "toPave=(s,r)" 
		    	Integer stId =  (Integer)toMatch.keySet().toArray()[0];
		    	Integer rayId =  toMatch.get(stId);
		    	Startype st = new Startype( data.getRays().get(rayId).getTipId() );
		    	RoleLabel rl = data.getRidges().get(data.getRays().get(rayId).getRidgeId());
	            	RoleLabel inv = rl.getInverseOf(data);
		    	Ray ray = new Ray(inv.getIdentifier(),  data.getRays().get(rayId).getTipId(), data.getRays().get(rayId).getCoreId(), data);
		    	ray = data.addRay(ray);
		    	st = st.addRay(ray.getIdentifier(), null, data);
		    	st = data.addStartype(st);
		    	startypesToSaturate.add(st.getIdentifier());
		    	applyRulesToFrame(data);
		 }
	      }
	      return true;
	}

	public boolean buildDeterministicFrame(ReasonerData data) {
	       boolean matched = false;
	       Set<Integer> matchedRays = new HashSet<Integer>();
	       while ( ! startypesToMatch.isEmpty() ) {
		 Integer sId = (Integer)startypesToMatch.keySet().toArray()[0];
		 Integer rId = (Integer)startypesToMatch.get(sId).toArray()[0];
		 Map<Integer, Integer> toMatch = new HashMap<Integer, Integer>();
		 toMatch.put(sId, rId);
		 Map<Integer, Integer> matchable = getMatch(rId, data);
		 //If the method has already tried to match the ray in "toMatch" but is is always not matched
		 //then this frame is not a model
		 if(matchable == null && matchedRays.contains(rId)) 
                    return false;
                 matchedRays.add(rId);
		 if(matchable!=null) 
			matching(toMatch, matchable, data);
		 else {
                    	//create a new startype corresponding to the ray "r" in "toPave=(s,r)" 
		    	Integer stId =  (Integer)toMatch.keySet().toArray()[0];
		    	Integer rayId =  toMatch.get(stId);
		    	Startype st = new Startype( data.getRays().get(rayId).getTipId() );
		    	RoleLabel rl = data.getRidges().get(data.getRays().get(rayId).getRidgeId());
	            	RoleLabel inv = rl.getInverseOf(data);
		    	Ray ray = new Ray(inv.getIdentifier(),  data.getRays().get(rayId).getTipId(), data.getRays().get(rayId).getCoreId(), data);
		    	ray = data.addRay(ray);
		    	st = st.addRay(ray.getIdentifier(), null, data);
		    	st = data.addStartype(st);
		    	startypesToSaturate.add(st.getIdentifier());
		    	applyRulesToFrame(data);
		 }
	      }
	      return true;
	}

	//This method visits each ray of "st" and applies rules to its tip. 
	public Startype applyRulesToTips(Startype st,  StartypeEvolution ste, ReasonerData data) {
		   Startype sat = st;
		   boolean changed = false;
	           for(Integer  ray : ste.getIdEvolution().keySet() ) {	
		     //System.out.println("Tip Rays dealt initId= " + ray );
		     //System.out.println("Tip Rays dealt currId= " + ste.getIdEvolution().get(ray) );
		     //System.out.println("Tip ste init= " + ste.getIdEvolution().keySet().toString() );
		     //System.out.println("Tip ste values= " + ste.getIdEvolution().values().toString() );
	 	  
		     Set<Integer> concepts = new HashSet<Integer>( ste.getFreshConceptsForRay().get(ray) );
                     while(! concepts.isEmpty() ) {
			  //System.out.println("Tip Non changed Applications= " + tipCheckApplications );
			  //System.out.println("Tip Changed Applications= " + tipRuleApplications );
		          //System.out.println(" nRay size= "+nRay.size()+", RayId=" +(Integer)nRay.toArray()[0] );
	                  //System.out.println(" Ray ="+ data.getRays().get( (Integer)nRay.toArray()[0] ).getIdentifier() );
			  //System.out.println("Concepts dealt = "+ concepts.toString() );	

			  //System.out.println("Rays = "+st.getRays().keySet());
		          //System.out.println("Ray init evol = "+ ste.getIdEvolution().keySet() );
			  // System.out.println("Ray values evol = "+ ste.getIdEvolution().values() );
			  Integer concept = (Integer)concepts.toArray()[0];
			  if (! data.getConcepts().get(concept).isTerminal()   ) {
			  switch (data.getConcepts().get(concept).getOperator()) {
			  case INTERSECTION:
				System.out.println("Tip INTERSECTION Rule ");
				sat = st.intersectionRuleForTip(concept, ray, ste, changed,  data);
			        //if(sat!=st) 
			        //   oldNews.put(ray, (Integer)newConcepts.toArray()[0] );
				break;
			  case UNION:
				System.out.println("Tip UNION Rule ");				
				sat = st.unionRuleForTip(concept, ray, ste, changed, data);
				break;
			  case ALL:
				System.out.println("Tip ALL Rule ");
				sat = st.allRuleForTip(concept, ray, ste, changed,  data);
				sat = sat.transRuleForTip(concept, ray, ste, changed,   data);
				break;
			  case MAX:
				System.out.println("Tip MAX Rule ");
				sat = st.choiceRuleForTip(concept, ray, ste, changed, data);
				break;
			  default:
				//System.out.println("Tip NO Rule ");
				break;
			  }
		          }
			  if(! st.equals(sat)) {
			     //when tip saturation changes something, this may lead to need to saturate core
			     tipRuleApplications++;
			     
		             if(sat==null) {
				return null;
			     }
			     data.flushRayAndCore(sat);
			    
			     st = sat;
			  } else {
			     //"saturated = true" only if "saturated = true" for each iteration of for 
			     tipCheckApplications++;
			  }
			   
			  ste.addProcessedConceptForRay(concept, ray, data);
			  concepts = ste.getFreshConceptsForRay().get(ray);
			  //System.out.println("Tip concepts fresh = ");
			  //for(Integer i: concepts){System.out.println(data.getConcepts().get(i).toString(data) ); }
		     } //while(! concepts.isEmpty() )
	           } //for
		return st;
	}

	public Startype applyRulesToCore(Startype st,  StartypeEvolution ste, ReasonerData data) {
	       Set<Integer> concepts = new HashSet<Integer>(ste.getFreshCoreConcepts());
	       Startype sat = st;
	       //Map<Integer, Integer> allRuleVisited = new HashMap<Integer, Integer>(); 
		      while( !concepts.isEmpty() ) {
		          //System.out.println(" WHILE Core ");
		      //for(Integer concept :  concepts) {
			  Integer concept = (Integer)concepts.toArray()[0];
			  boolean changed = false;
		          //System.out.println("Core ste key= " + ste.getIdEvolution().keySet().toString() );
		          //System.out.println("Core ste  values= " + ste.getIdEvolution().values().toString() );
			   //System.out.println("Rays= " + st.getRays().keySet().toString() );
			  //System.out.println("Non changed Applications= " + coreCheckApplications );
			  //System.out.println("Changed Applications= " + coreRuleApplications + ", " +data.getConcepts().get(concept).toString(data));
			  //System.out.println("Startypes= " + data.getStartypes().keySet().size() + ",  Rays =" + data.getRays().keySet().size() + ", Concepts = " + data.getConcepts().keySet().size()  + ", Cores = " + data.getCores().keySet().size() + ", Ridges = " + data.getRidges().keySet().size()  );


			  //System.out.println("Core Rays = "+st.getRays().keySet());
		          //System.out.println("Core Ray init evol = "+ ste.getIdEvolution().keySet() );
			  // System.out.println("Core Ray values evol = "+ ste.getIdEvolution().values() );
			  if (! data.getConcepts().get(concept).isTerminal()  ) {
			   
			  switch (data.getConcepts().get(concept).getOperator()) {
			  case INTERSECTION:
				System.out.println("core INTERSECTION Rule ");
				sat = st.intersectionRule(concept, ste, changed,  data);
			        ste.addCoreProcessed(concept, data);
				break;
			  case UNION:
			        System.out.println("core UNION Rule ");
				sat = st.unionRule(concept, ste, changed,  data);
				ste.addCoreProcessed(concept, data);
				break;
			  case SOME:
			        System.out.println("core SOME Rule on"+ data.getConcepts().get(concept).toString(data));
				sat = st.someRule(concept, ste,  changed, data);
			        ste.addCoreProcessed(concept, data);
				 
				break;
			  case ALL:
				System.out.println("core ALL Rule on "+ data.getConcepts().get(concept).toString(data));
				 
				sat = st.allRule(concept, ste, changed, data);
				sat = sat.transRule(concept, ste, changed, data);
				 
				break;
			  case MIN:
				System.out.println("core MIN Rule ");
				sat = st.minRule(concept, ste, changed, data);
				ste.addCoreProcessed(concept, data);
				break;
			  case MAX:
				System.out.println("core MAX Rule on "+ data.getConcepts().get(concept).toString(data));
				sat = st.maxRule(concept, ste, changed,  data);
				sat = sat.choiceRule(concept, ste,  changed,  data);
			    	
				break;
			  default: 
				//System.out.println("core NO Rule ");
				//System.out.println("concept="+data.getConcepts().get(concept).toString(data));
				break;
			  }
		          }
			  if(! st.equals(sat)) {
			     //when core saturation changes something, this may lead to need to saturate tips 
			     coreRuleApplications++; 
		             if(sat==null) {
				return null;			        
			     }
			     data.flushRayAndCore(sat);  
			     st = sat;
			  } else {
			     //"saturated = true" only if "saturated = true" for each iteration of for 
			     coreCheckApplications++;
			  }
			  //if(changed)
			  //System.out.println("concepts fresh = ");
			  //for(Integer i: concepts){System.out.println(data.getConcepts().get(i).toString(data) ); }
		          ste.addCoreProcessed(concept, data);
			  concepts = ste.getFreshCoreConcepts();
		   } //while( !concepts.isEmpty() ) 
                    
		   for(Integer concept : ste.getAllMaxConcepts()) {
                          //System.out.println(" FOR Core ");
			  boolean changed = false;
			  //System.out.println("Non changed Applications= " + coreCheckApplications );
			  //System.out.println("Changed Applications= " + coreRuleApplications + ", " +data.getConcepts().get(concept).toString(data));
			  //System.out.println("Startypes= " + data.getStartypes().keySet().size() + ",  Rays =" + data.getRays().keySet().size() + ", Concepts = " + data.getConcepts().keySet().size()  + ", Cores = " + data.getCores().keySet().size() + ", Ridges = " + data.getRidges().keySet().size()  );
			   
			  switch (data.getConcepts().get(concept).getOperator()) {
			   
			  case ALL:
				System.out.println("core FOR ALL Rule on "+ data.getConcepts().get(concept).toString(data));
				sat = st.allRule(concept, ste, changed, data);
				sat = sat.transRule(concept, ste, changed, data);
				break;
			  case MAX:
				System.out.println("core FOR MAX Rule on "+ data.getConcepts().get(concept).toString(data));
				sat = st.maxRule(concept, ste, changed,  data);
				sat = sat.choiceRule(concept, ste,  changed,  data);
				break;
			  default: 
				//System.out.println("core NO Rule ");
				//System.out.println("concept="+data.getConcepts().get(concept).toString(data));
				break;
			  }
			  if(! st.equals(sat)) {
			     //when core saturation changes something, this may lead to need to saturate tips 
			     coreRuleApplications++; 
		             if(sat==null) {
				return null;			        
			     }
			     data.flushRayAndCore(sat);  
			     st = sat;
			  } else {
			     //"saturated = true" only if "saturated = true" for each iteration of for 
			     coreCheckApplications++;
			  }
		   }
	       return st;
	}
	
	//This method is used for avoiding nondeterminism
	//Its tries to saturate "st" and returns null if the result is invalid
	public Startype applyRules(Startype st, ReasonerData data) {
		Startype sat = st;
		boolean saturatedTip = false;
		//boolean saturatedAllTips = false;
		boolean saturatedCore = false;
		StartypeEvolution ste = new StartypeEvolution(st, data);
                while(! saturatedCore || ! saturatedTip ) {
		      sat = applyRulesToCore(st, ste, data);
		      //for terminates when a new startype is created, or no new startype is created  for all concepts visited 
		      if(sat==st) 
		         saturatedCore = true;
                      st = sat;
		      sat = applyRulesToTips(st, ste, data);
		      if(sat==st) 
		         saturatedTip = true;
                      st = sat;
	        }//while(! saturatedCore || ! saturatedTip )
	        if(st==null)
		  return null;
	        else { 
		  System.out.println("Checking for clashes ................................................");
		  st.setSaturated(true);
		  if(st.isValidForAll(data) )
	             st.setValid(new Boolean(true));
		  else
		     st.setValid(new Boolean(false));
		  return st; 
	        }
	}

	 
	/**
	 * Adds all nominal startypes to the list of the frame. Added startypes HAVE
	 * applied the rules.
	 * 
	 * @param data
	 *            The data of the ontology.
	 */
	/*
	public void initNominals(ReasonerData data) {
		ArrayList<Concept> result = new ArrayList<Concept>();
		boolean clash = false;
		List<Concept> cs;

		for (Concept concept : data.getConcepts().values())
			if (concept.isNominal()) {
				Startype s = new Startype(lastIdOfStartype, new ConceptLabel(
						concept));
				lastIdOfStartype++;

				// apply standard rules
				cs = this.applyStandardRules(s, data, clash);
				if( clash ) {
				   continue;
				}
			
				result.addAll(cs);

				// apply rule for conceptAssertions
				for (ConceptAssertion assertion : data.getConceptAssertions()
						.values())
					result.addAll(s.conceptAssertionRule(assertion));

				// apply rule for roleAssertions
				for (RoleAssertion assertion : data.getRoleAssertions()
						.values())
					s.roleAssertionRule(assertion, data);

				// apply the rules to the concepts generated previously
				// (recursive)
				if (result.size() != 0)
					s.addToCore(this.applyRulesRecursive(s, data, result));

				// apply the rules to the alternatives of the startype
				// (non-determinism)
				this.applyRulesToDeterminist(s, data);

				data.addCore(data.giveCoreIdentifier(s.getCore()));
				for (Ray ray : s.getRays())
					data.addRidge(data.giveRidgeIdentifier(ray.getRidge()));
				s.setSaturated();
				stars.add(s);
			}

	}
	*/
	/**
	 * Recursive function. Applies the rules of the startypes to a list of
	 * concepts, and loops until all the concepts (even the generated ones) have
	 * been treated.
	 * 
	 * @param s
	 *            The startype of origin of the concepts.
	 * @param data
	 *            The data of the ontology.
	 * @param concepts
	 *            The concepts to be treated.
	 * @return The list of all the generated concepts.
	 */
	/*
	private List<Concept> applyRulesRecursive(Startype s, ReasonerData data,
			List<Concept> concepts) {
		List<Concept> res, tmp;

		res = new ArrayList<Concept>();
		for (Concept concept : concepts) {
			tmp = applyConceptRule(s, concept, data);
			if (tmp != null)
				res.addAll(tmp);
		}

		if (res.size() != 0)
			res.addAll(this.applyRulesRecursive(s, data, res));

		return res;
	}
	*/
	/**
	 * Apply all the rules to the standard concepts of the ontology.
	 * 
	 * @param startype
	 *            The startype that contains the rules.
	 * @param data
	 *            The data of the ontology.
	 * @return The concepts generated by the rules.
	 */
	/*
	public List<Concept> applyStandardRules(Startype startype, ReasonerData data, boolean clash) {
		ArrayList<Concept> result = new ArrayList<Concept>();
		List<Concept> tmp;

		//Check if startype is valid
                if( ! startype.isValid(data) ) {
                    clash  = true;
System.out.println("clash = "+startype.toString() );
                    return result;
		}
		for (Concept c : startype.getCore()) {
			tmp = this.applyConceptRule(startype, c, data);
			if (tmp != null)
				result.addAll(tmp);
		}

		// concepts
		for (Concept c : data.getAxiomNNFs()) {
			tmp = this.applyConceptRule(startype, c, data);
			if (tmp != null)
				result.addAll(tmp);
		}

		// transitive closure
		startype.closureRule(data.getTransitiveClosure());

		return result;
	}
	*/
	/**
	 * Apply the rules adapted to the given concept.
	 * 
	 * @param s
	 *            The startype of origin of the concept.
	 * @param concept
	 *            The concept to be treated.
	 * @param data
	 *            The data of the ontology.
	 * @return The concepts generated by the rules.
	 */
	/*
	private List<Concept> applyConceptRule(Startype s, Concept concept,
			ReasonerData data) {
		ArrayList<Concept> res = new ArrayList<Concept>();
		Concept c;

		if (!concept.isTerminal()) {
			switch (concept.getOperator()) {
			case INTERSECTION:
System.out.println("Rule INTER for startype "+ s.getIdentifier() + " on "+  concept.toString() );
				return s.intersectionRule(concept);
			case UNION:
System.out.println("Rule UNION for startype "+ s.getIdentifier() + " on "+  concept.toString() );
				c = s.unionRule(concept, data, this);
				if (c == null)
					break;
				else {
					res.add(c);
					return res;
				}

			case SOME:
System.out.println("Rule SOME for startype "+ s.getIdentifier() + " on "+  concept.toString() );
				s.someRule(concept, data);
				if (concept.getRole().isTransitiveClosure()) {
					res.add(s.someTransitiveRule(concept, data));
					return res;
				}
				break;
			case ALL:
System.out.println("Rule ALL for startype "+ s.getIdentifier() + " on "+ concept.toString() );
				s.allRule(concept);
				s.allTransitiveRule(concept, data);
				break;
			case MIN:
System.out.println("Rule MIN for startype "+ s.getIdentifier() + " on "+ concept.toString() );
				s.minRule(concept, data);
				break;
			case MAX:
System.out.println("Rule MAX and CH for startype "+ s.getIdentifier() + " on "+ concept.toString() );
				s.maxRule(concept, data, this);
				s.chRule(concept, data, this);
				break;
			default:
				break;
			}
		}

		return null;
	}
	*/
	/**
	 * Apply the rules to the different alternatives of a startype, in case of a
	 * non-determinist rule.
	 * 
	 * @param startype
	 *            The startype to be checked.
	 * @param data
	 *            The data of the ontology.
	 */
	/*
	public void applyRulesToDeterminist(Startype startype, ReasonerData data) {
		Set<Startype> progeny = startype.getProgeny();
		ArrayList<Concept> result = new ArrayList<Concept>();
		boolean clash = false;
		List<Concept> cs;
		// if the startype has alternatives
		if (!progeny.isEmpty()) {
			// for each alternative
			for (Startype s : progeny) {
				// if it is not nominal, re-apply the standard rules
				if (!s.isNominal()) {
					cs = this.applyStandardRules(s, data, clash);
					if( clash ) {
					    continue;
					}
					s.addToCore(cs);
				}
				else {
					// apply the rules for the nominals
					cs = this.applyStandardRules(s, data, clash);
					if( clash ) {
					    continue;
					}
					result.addAll(cs);

					for (ConceptAssertion assertion : data
							.getConceptAssertions().values())
						result.addAll(s.conceptAssertionRule(assertion));

					for (RoleAssertion assertion : data.getRoleAssertions()
							.values())
						s.roleAssertionRule(assertion, data);

					if (result.size() != 0)
						s.addToCore(this.applyRulesRecursive(s, data, result));
				}

				// register the data
				data.addCore(data.giveCoreIdentifier(s.getCore()));
				for (Ray ray : s.getRays())
					data.addRidge(data.giveRidgeIdentifier(ray.getRidge()));
				s.setSaturated();
				stars.add(s);
			}
		}
	}
	*/
	/**
	 * Gives the identifier of the frame.
	 * 
	 * @return The identifier of the frame.
	 */
	public int getIdentifier() {
		return id;
	}
	
	 
	
	/**
	 * Gives the last identifier available to create a new startype.
	 * 
	 * @return The last identifier valid for a startype. If the created startype
	 *         is stored, this value must be incremented.
	 */
	/*
	public int getLastIDOfStartype() {
		return lastIdOfStartype;
	}
	*/
	/**
	 * Updates the last identifier to be used for the creation of a startype.
	 * Must be used each time a startype is created THEN STORED.
	 */
	/*
	public void incrementLastIDOfStartype() {
		lastIdOfStartype++;
	}
	*/
}
