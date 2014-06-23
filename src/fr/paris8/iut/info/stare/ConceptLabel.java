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
import java.util.Iterator;

// a set of concepts
public class ConceptLabel extends HashSet<Concept> {
	private static final long serialVersionUID = 1L;
	private int identifier = -1;

	public boolean equals(ConceptLabel other) {
		Iterator<Concept> it1 = this.iterator();
		Iterator<Concept> it2 = other.iterator();
		
		if(this.size() != other.size())
			return false;
		
		while(it1.hasNext())
			if(it1.next().equals(it2.next()))
				return false;
		
		return true;
	}
	
	public void setIdentifier(int id){
		this.identifier = id;
	}
	
	public int getIdentifier(){
		return this.identifier;
	}
}
