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
import java.lang.CloneNotSupportedException;

public class Frame {
	// identity
	private int id;
	private int lastIdOfStartype;
	private int numberOfNominals;
	private Set<Startype> stars = new HashSet<Startype>();
	//for debug
	private int ruleApplications=0;
	/**
	 * Creation wof the frame.
	 * 
	 * @param id
	 *            The id for the frame.
	 */
	public Frame(int id) {
		this.id = id;
		this.lastIdOfStartype = 0;
		stars = new HashSet<Startype>();
	}

	/**
	 * Creates the startypes and paves them
	 * 
	 * @param data
	 *            The data already gathered by the reasoner.
	 */
	/*
	public void pave(ReasonerData data) {
		this.initNominals(data);
		numberOfNominals = stars.size();
		this.paving(data, new ArrayList<Startype>(stars));
	}
	*/
	/**
	 * Recursive function to pave the startypes of a list.
	 * 
	 * @param data
	 *            The data already gathered by the reasoner.
	 * @param list
	 *            The list of startypes to be paved.
	 */
	/*
	private void paving(ReasonerData data, List<Startype> list) {
		List<Startype> createdStartypes = new ArrayList<Startype>();
		boolean paved = false;

		// initialization
		this.initNominals(data);

		// pave each startype
		for (Startype startype : list) {
			// for each ray
			for (Ray ray : startype.getRays()) {

				// if the ray has not been paved yet...
				if (ray.getCounter() == 0) {
					// ...check in each startype...
					for (Startype s : stars) {

						// must not pave the startypes reflexively
						if (startype.getIdentifier() != s.getIdentifier()) {
							// ...for each ray...
							for (Ray r : s.getRays()) {
								// ...if the second ray is an inverse of the
								// first:
								if (ray.isInverseOf(r, startype.getCore(),
										s.getCore(), data)) {
									// signal both startype they have been paved
									// on
									// the
									// ray
									startype.addPavedStartypeToRay(ray, s);
									s.addPavedStartypeToRay(r, startype);

									// increment the counters
									ray.incrementCounter();
									r.incrementCounter();
									s.incrementCounter();

									// signal the startype has been paved
									paved = true;
								}
							}
						}
					}

					// if the current startype cannot be paved...
					if (!paved) {
						// ...create a new startype, with the standard
						// concepts...
						Startype s = init(data, ray.getTip());
						// ...and one ray that is the inverse of the one checked
						Ray r = new Ray(ray.getRidge().getInverseOf(data),
								startype.getCore());

						s.addRay(r);
						// signal both startype they have been paved on the
						// ray
						startype.addPavedStartypeToRay(ray, s);
						s.addPavedStartypeToRay(r, startype);

						// increment the counters
						ray.incrementCounter();
						r.incrementCounter();
						s.incrementCounter();

						// add the startype to the list of newly created
						// startypes
						createdStartypes.add(s);
					}
				}
			}
		}

		if (!createdStartypes.isEmpty()) {
			// add all the newly created startypes to the frame, then loop to
			// pave them
			stars.addAll(createdStartypes);
			this.paving(data, createdStartypes);
		}
	}
	*/

	/**
	 * Initialization of a standard startype, with only the concepts of the
	 * ontology in the core (rules have not been applied). WARNING: the
	 * generated startype truly contains the NNF, they are notstored somewhere
	 * else like for the other initializers. For this reason, this startype has
	 * no identifier and is not stored in the frame.
	 * 
	 * @param data
	 *            The data of the ontology.
	 * @return The startype initialized.
	 */
	public int getRuleApplications(){
		return ruleApplications;
	}
	public Startype init(ReasonerData data)   {
		Startype st = new Startype( data.getNNFConceptLabel() );
		st = data.addStartype(st);
		return st;
	}

	public void applyNonGeneratingRules(ReasonerData data)   {
		Integer notSaturated = null;
		while((notSaturated = data.getStartypeToExpand()) != null ) {
		      Startype st = data.getStartypes().get(notSaturated);
		      Startype sat = st;
		      Set<Integer> concepts = new HashSet<Integer>(data.getCores().get(st.getCoreId()).getConceptIds());
		      boolean saturated = false;
                      while(! saturated ) {
		      //for terminates when a new startype is created, or no new startype is created  for all concepts visited 
		      for(Integer concept :  concepts) {
			   boolean changed = false; // not used yet
			  if (! data.getConcepts().get(concept).isTerminal() ) {
			  switch (data.getConcepts().get(concept).getOperator()) {
			  case INTERSECTION:
				System.out.println("Rule INTER for startype "+ getIdentifier() + " on "+  data.getConcepts().get(concept).toString(data) );
				sat = st.intersectionRule(concept, changed, data);
				break;
			  case UNION:
				System.out.println("Rule UNION for startype "+ getIdentifier() + " on "+  data.getConcepts().get(concept).toString(data) );
				sat = st.unionRule(concept, changed, data);
				break;
			  case ALL:
				System.out.println("Rule ALL for startype "+  getIdentifier() + " on "+ data.getConcepts().get(concept).toString(data) );
				sat = st.allRule(concept, changed, data);
				sat = st.transRule(concept, changed, data);
				break;
			 case  MAX: //CHOICE
				System.out.println("Rule CHOICE for startype "+  getIdentifier() + " on "+ data.getConcepts().get(concept).toString(data) );
				sat = st.choiceRule(concept, changed, data);
				break;
			  default:
				break;
			  }
		          }
			  if(! st.equals(sat)) {
		             st.setExpanded(sat.getIdentifier());
			     st = sat;
			     concepts = new HashSet<Integer>(data.getCores().get(st.getCoreId()).getConceptIds());
			     saturated = false;	
			     break;	
			  } else
                             saturated = true;
		     }//for
	           }
		}
	}

	public void applyRules(ReasonerData data) {
		Integer notSaturated = null;
		while((notSaturated = data.getStartypeToExpand()) != null ) {
		      Startype st = data.getStartypes().get(notSaturated);
		      Startype sat = st;
		      Set<Integer> concepts = new HashSet<Integer>(data.getCores().get(st.getCoreId()).getConceptIds());
		      boolean saturated = false;
                      while(! saturated ) {
		      //for terminates when a new startype is created, or no new startype is created  for all concepts visited 
		      for(Integer concept :  concepts) {
			  boolean changed = false;// not used yet
			  if (! data.getConcepts().get(concept).isTerminal() ) {
			  switch (data.getConcepts().get(concept).getOperator()) {
			  case INTERSECTION:
				System.out.println("Rule INTER for startype "+ getIdentifier() + " on "+  data.getConcepts().get(concept).toString(data) );
				sat = st.intersectionRule(concept, changed, data);
				break;
			  case UNION:
				System.out.println("Rule UNION for startype "+ getIdentifier() + " on "+  data.getConcepts().get(concept).toString(data) );
				sat = st.unionRule(concept, changed, data);
				break;
			  case SOME:
				System.out.println("Rule SOME for startype "+ getIdentifier() + " on "+  data.getConcepts().get(concept).toString(data) );
				sat = st.someRule(concept, changed, data);
				break;
			  case ALL:
				System.out.println("Rule ALL and TRANSRULE for startype "+  getIdentifier() + " on "+ data.getConcepts().get(concept).toString(data) );
				sat = st.allRule(concept, changed, data);
				sat = st.transRule(concept, changed, data);
				break;
			  case MIN:
				System.out.println("Rule MIN for startype "+ getIdentifier() + " on "+ data.getConcepts().get(concept).toString(data)  );
				sat = st.minRule(concept, changed, data);
				break;
 
			case MAX:
				System.out.println("Rule MAX and CH for startype "+ getIdentifier() + " on "+ data.getConcepts().get(concept).toString(data) );
				sat = st.maxRule(concept, changed, data);
				sat = st.choiceRule(concept, changed, data);
				break;
			  default:
				break;
			  }
		          }

			  if(! st.equals(sat)) {
		             st.setExpanded(sat.getIdentifier());
			     st = sat;
			     concepts = new HashSet<Integer>(data.getCores().get(st.getCoreId()).getConceptIds());
			     saturated = false;	
			     ruleApplications++;
			     break;	
			  } else
			     //"saturated = true" only if "saturated = true" for each iteration of for 
                             saturated = true;
		     }//for
	           }
		   sat.setSaturated(true);
		}
	}
	
	/**
	 * Initialization of a standard startype, with only the concepts of the
	 * ontology in the core (rules have been applied), plus those the user wants
	 * to add.
	 * 
	 * @param data
	 *            The data of the ontology.
	 * @param concepts
	 *            The concepts to be added in the core of the startype.
	 * @return The startype initialized.
	 */
	/*
	public Startype init(ReasonerData data, ConceptLabel concepts) {
		Startype base = new Startype(lastIdOfStartype);
		List<Concept> res;
		boolean clash = false;

		lastIdOfStartype++;
		if (concepts != null)
			base.addToCore(new ArrayList<Concept>(concepts));

		res = this.applyStandardRules(base, data, clash);
		//there is a clash (invalid) in the core
		if( clash ) 
		    return null;

		if (res.size() != 0)
			base.addToCore(this.applyRulesRecursive(base, data, res));

		// apply the rules to the alternatives of the startype
		// (non-determinism)
		this.applyRulesToDeterminist(base, data);

		data.addCore(data.giveCoreIdentifier(base.getCore()));
		for (Ray ray : base.getRays())
			data.addRidge(data.giveRidgeIdentifier(ray.getRidge()));
		base.setSaturated();

		stars.add(base);

		return base;
	}
	*/
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
	
	public Set<Startype> getStartypes() {
		return stars;
	}
	
	/**
	 * Gives the last identifier available to create a new startype.
	 * 
	 * @return The last identifier valid for a startype. If the created startype
	 *         is stored, this value must be incremented.
	 */
	public int getLastIDOfStartype() {
		return lastIdOfStartype;
	}

	/**
	 * Updates the last identifier to be used for the creation of a startype.
	 * Must be used each time a startype is created THEN STORED.
	 */
	public void incrementLastIDOfStartype() {
		lastIdOfStartype++;
	}

}
