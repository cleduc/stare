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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
 

// a set of concepts
//public class ConceptLabel extends HashSet<Concept> {

public class ConceptLabel  {
	private static final long serialVersionUID = 1L;
	private int identifier = -1;
	private Set<Integer> conceptIds;

	public ConceptLabel() {
		conceptIds = new HashSet<Integer>();
	}

	public ConceptLabel(Set<Integer> s) {
		conceptIds = new HashSet<Integer>(s);
	}

	//create a ConceptLabel from a single concept that must be identified 
	public ConceptLabel(Concept c) {
		conceptIds = new HashSet<Integer>(c.getIdentifier());
		conceptIds.add(c.getIdentifier());
	}
	
	//If concept is already identifed
	public ConceptLabel(Integer id) {
		conceptIds = new HashSet<Integer>();
		conceptIds.add(id);
	}

	public void add(Concept c) {
		conceptIds.add(c.getIdentifier());
	}

	public void add(Integer id) {
		conceptIds.add(id);
	}

	public void addAll(Set<Integer> ids) {
		conceptIds.addAll(ids);
	}

	//If "c" is not in "this", a new Conceptlabel is created 
	public  ConceptLabel getNewConceptLabel(Integer c, ReasonerData data) {
		if( ! this.contains(c) ) {
		    ConceptLabel cl = new ConceptLabel();
		    for(Integer concept : conceptIds){ 
		        cl.add(concept);
                    }
		    cl.add( c );
		    cl = data.addCore(cl);
		    return cl;
                } else
		  return this;
	}

	 
	public  ConceptLabel getNewConceptLabel(Set<Integer> lc, ReasonerData data) {
		ConceptLabel cl = new ConceptLabel(this.conceptIds);
		cl.addAll(lc);
		//System.out.println("content core="+ cl.getConceptIds().toString() );
		cl = data.addCore(cl);
		
		return cl;
 	}


	public  boolean contains(Concept concept) {
		return this.conceptIds.contains(concept.getIdentifier());
	}

	public  boolean contains(Integer concept) {
		return this.conceptIds.contains(concept);
	}

	public void setIdentifier(int id) {
		this.identifier = id;
	}

	public int getIdentifier() {
		return this.identifier;
	}

	public void setConceptIds(Set<Integer> s) {
		this.conceptIds = s;
	}

	public Set<Integer> getConceptIds() {
		return this.conceptIds;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null )
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConceptLabel other = (ConceptLabel) obj;
		if (this.getIdentifier() >= 0 && other.getIdentifier() >= 0)
			if (this.getIdentifier() == other.getIdentifier())
				return true;
			else
				return false;
		if( conceptIds.equals(other.getConceptIds() )  ) 
		     return true;
		return false;
	}

	
	public String toString(ReasonerData data) {
		StringBuilder sb = new StringBuilder();
		
		for (Integer i : conceptIds) {
			sb.append(data.getConcepts().get( i ).toString(data));
			sb.append(System.getProperty("line.separator"));
		}

		return sb.toString();
	}
}
