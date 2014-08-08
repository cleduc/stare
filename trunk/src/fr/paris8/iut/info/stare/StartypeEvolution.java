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

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
 

//Managing evolution of a startype
public class StartypeEvolution { 
	private Integer coreId;
	private int inc=0;
	private Set<Integer> processedCoreConcepts;
	private Set<Integer> freshCoreConcepts;	
	private Set<Integer> allMaxConcepts;	
	private Map<Integer, Set<Integer>> processedConceptsForRay;
	private Map<Integer, Set<Integer>> freshConceptsForRay;
	//mapping between initial indexes and last ones
	private Map<Integer, Integer> idEvolution;
	 
	public  StartypeEvolution(Startype st, ReasonerData data) {
		processedCoreConcepts = new HashSet<Integer>();
		allMaxConcepts = new HashSet<Integer>();
		coreId = st.getCoreId();
		processedConceptsForRay = new HashMap<Integer, Set<Integer>>();
		freshConceptsForRay = new HashMap<Integer, Set<Integer>>();
		idEvolution = new HashMap<Integer, Integer>();
		freshCoreConcepts = new HashSet<Integer>();
		for(Integer i: data.getCores().get(st.getCoreId()).getConceptIds()){
		     if (! data.getConcepts().get(i).isTerminal()  ) 
		     switch(data.getConcepts().get(i).getOperator()) {
		         case ALL:
			 case MAX:
			      allMaxConcepts.add(i);
			      break;
			 default:
			      if(! data.getConcepts().get(i).isTerminal())
  		                    freshCoreConcepts.add( i);
		     }
		     
		}
		for(Integer ray : st.getRays().keySet()){
		    Integer tipId = data.getRays().get(ray).getTipId();
		    Set<Integer> ids = new HashSet<Integer>();
		    for(Integer i: data.getCores().get(tipId).getConceptIds()){
		        if(! data.getConcepts().get(i).isTerminal())
		           ids.add(i);
		    }
		    freshConceptsForRay.put(ray, ids);
		}
		 
		for(Integer ray : st.getRays().keySet()){
		    idEvolution.put(new Integer(inc++), ray);
		}
	}

	public StartypeEvolution(StartypeEvolution st){
               processedCoreConcepts = new HashSet<Integer>(st.getProcessedCoreConcepts());
	       freshCoreConcepts = new HashSet<Integer>(st.getFreshCoreConcepts());
	       allMaxConcepts = new HashSet<Integer>(st.getAllMaxConcepts());
	       coreId = st.getCoreId();
	       processedConceptsForRay = new HashMap<Integer, Set<Integer>>( st.getProcessedConceptsForRay());
	       freshConceptsForRay = new HashMap<Integer, Set<Integer>>( st.getFreshConceptsForRay());
	       freshConceptsForRay = new HashMap<Integer, Set<Integer>>();
	       idEvolution = new HashMap<Integer, Integer>(st.getIdEvolution());
	}
	public void extend(Integer ray, ReasonerData data){
		//System.out.println("Extend  = "+ ray+ ", inc="+inc);
		idEvolution.put(new Integer(inc), ray);
		processedConceptsForRay.put(new Integer(inc), new HashSet<Integer>());
		Integer tipId = data.getRays().get(ray).getTipId();
		Set<Integer> ids = new HashSet<Integer>();
		for(Integer i: data.getCores().get(tipId).getConceptIds()){
			if(! data.getConcepts().get(i).isTerminal())
		           ids.add(i);
		}
		freshConceptsForRay.put(new Integer(inc),  ids);
		inc++;
	}

	public void extend(Set<Integer> p, Set<Integer> f, Integer ray, ReasonerData data){
		    idEvolution.put(inc, ray);
		    processedConceptsForRay.put(inc, p);
		    Set<Integer> ids = new HashSet<Integer>();
		    for(Integer i : f)
	            if(! data.getConcepts().get(i).isTerminal() )
			ids.add(i);
		    freshConceptsForRay.put(inc, ids);
		    inc++;
	}

	public Integer getInitId(Integer current){
               for(Integer i : idEvolution.keySet()) {
		   if(idEvolution.get(i).equals(current))
		      return i;
	       } 
	       return null;
	}

	
	public void addCoreFresh(Integer fresh, ReasonerData data){
		if(! processedCoreConcepts.contains(fresh) )
		     if (! data.getConcepts().get(fresh).isTerminal()  ) 
		    switch(data.getConcepts().get(fresh).getOperator()) {
		         case ALL:
			 case MAX:
			      allMaxConcepts.add(fresh);
			      break;
			 default:
			      if(! data.getConcepts().get(fresh).isTerminal())
  		                     freshCoreConcepts.add(fresh);
		     }
	}

	public void addCoreProcessed(Integer processed,  ReasonerData data){
		//System.out.println("concepts processed = "+ processed);
		if(! data.getConcepts().get(processed).isTerminal() ) {
		   freshCoreConcepts.remove(processed);
		   processedCoreConcepts.add(processed);
			//System.out.println("concepts removed = "+ processed);
		}
	}

	public void addFreshConceptForRay(Integer fresh, Integer initRay, ReasonerData data){
		//System.out.println("Fresh added= "+ fresh);//data.getConcepts().get(fresh).toString() );
		//System.out.println("Init = "+ initRay);//data.getConcepts().get(fresh).toString() );
		//System.out.println("Curr = "+ idEvolution.get(initRay));//data.getConcepts().get(fresh).toString() );
		//System.out.println("Processed = "+ processedConceptsForRay.get(initRay).toString());
		if(! processedConceptsForRay.get(initRay).contains(fresh) && ! data.getConcepts().get(fresh).isTerminal())
		     freshConceptsForRay.get(initRay).add(fresh);
	}

	public void addProcessedConceptForRay(Integer processed, Integer initRay, ReasonerData data){
		    if(! data.getConcepts().get(processed).isTerminal() ) {
			//System.out.println("concepts tip removed = ");
		     freshConceptsForRay.get(initRay).remove(processed);
		     processedConceptsForRay.get(initRay).add(processed);
		    }
	}

	public void setFreshConceptForRay(Set<Integer> fresh, Integer initRay){
		     freshConceptsForRay.put(initRay,  fresh);
	}

	public void setProcessedConceptForRay(Set<Integer> processed, Integer initRay){
		     processedConceptsForRay.put(initRay,  processed);
	}

	
	
	public Integer getCoreId  () {
		return coreId;
	}

	public void setCoreId(Integer p) {
		coreId = p;
	}
	public Set<Integer> getAllMaxConcepts() {
		return allMaxConcepts;
	}

	public void setAllMaxConcepts(Set<Integer> p) {
		allMaxConcepts = p;
	}

	public Set<Integer> getProcessedCoreConcepts() {
		return processedCoreConcepts;
	}

	public void setProcessedCoreConcepts(Set<Integer> p) {
		processedCoreConcepts = p;
	}

	public void setFreshCoreConcepts(Set<Integer> p) {
		freshCoreConcepts = p;
	}

	public Set<Integer> getFreshCoreConcepts() {
		return freshCoreConcepts ;
	}

	public Map<Integer, Set<Integer>> getFreshConceptsForRay() {
		return freshConceptsForRay;
	}

	public void setFreshConceptsForRay(Map<Integer, Set<Integer>> p) {
		freshConceptsForRay = p;
	}

	public Map<Integer, Set<Integer>> getProcessedConceptsForRay() {
		return processedConceptsForRay;
	}

	public void setProcessedConceptsForRay(Map<Integer, Set<Integer>> p) {
		processedConceptsForRay = p;
	}

	public Map<Integer, Integer> getIdEvolution() {
		return idEvolution;
	}

	public void setIdEvolution(Map<Integer, Integer> p) {
		idEvolution = p;
	}
 
}
