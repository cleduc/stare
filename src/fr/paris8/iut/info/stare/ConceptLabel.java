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

public class ConceptLabel extends HashSet<Integer> {
	private static final long serialVersionUID = 1L;
	private int identifier = -1;

	public ConceptLabel() {
		super();
	}

	//create a ConceptLabel from a single concept that must be identified 
	public ConceptLabel(Concept c, ReasonerData data) {
		super();
		this.add(c.getIdentifier());
		//if "this" is not identified, it is now
		data.addCore(this);
	}
	
	//If concept is already identifed
	public ConceptLabel(Integer id, ReasonerData data) {
		super();
		this.add(id);
		//if "this" is not identified, it is now
		data.addCore(this);
	}

	//If "c" is not in "this", a new Conceptlabel is created 
	public  ConceptLabel getNewConceptLabel(Integer c, ReasonerData data) {
		if( ! this.contains(c) ) {
		    ConceptLabel cl = new ConceptLabel();
		    for(Integer concept : this){ 
		        cl.add(concept);
                    }
		    cl.add( c );
		    data.addCore(cl);
		    return cl;
                } else
		  return this;
	}

	 
	public  ConceptLabel getNewConceptLabel(Set<Integer> lc, ReasonerData data) {
		ConceptLabel cl = new ConceptLabel();
		for(Integer concept : this){ 
		        cl.add(concept);
                }
		cl.addAll(lc);
		data.addCore(cl);
		return cl;
 	}


	public  boolean contains(Concept concept) {
		return this.contains(concept.getIdentifier());
	}

	public void setIdentifier(int id) {
		this.identifier = id;
	}

	public int getIdentifier() {
		return this.identifier;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		//If adresses are the same
		if (this == obj)
			return true;
		if (obj == null )
			return false;

		if (getClass() != obj.getClass())
			return false;
		 
		ConceptLabel other = (ConceptLabel) obj;
		if (this.getIdentifier() > 0 && other.getIdentifier() > 0)
			if (this.getIdentifier() == other.getIdentifier())
				return true;
			else
				return false;

		//Check if two sets of identifiers equal. This is performed between two sets of Integers 
		if( super.equals( other.getClass().getSuperclass() ) ) 
		     return true;
 
		return false;
	}

	
	public String toString(ReasonerData data) {
		StringBuilder sb = new StringBuilder();
		
		for (Integer i : this) {
			sb.append(data.getConcepts().get( i.intValue() ).toString());
			sb.append(System.getProperty("line.separator"));
		}

		return sb.toString();
	}
}
