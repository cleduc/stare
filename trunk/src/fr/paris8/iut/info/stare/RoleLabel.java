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

//a set of roles
public class RoleLabel extends HashSet<Integer> {
	private static final long serialVersionUID = 1L;
	private int identifier = -1;

	public RoleLabel() {
		super();
	}
	//create a RoleLabel from a single role that must be identified 
	public RoleLabel(Role r, ReasonerData data) {
		super();
		this.add(r.getIdentifier()); 
		for(Integer i : data.getTransitiveClosureOfRoleHierarchy().getSubsumers( r.getIdentifier(), data ))
		    this.add(i);
		//if "this" is not identified,
		data.addRidge(this);
	}
	
	//If concept is already identifed
	public RoleLabel(Integer id, ReasonerData data) {
		super();
		this.add(id);
		for(Integer i : data.getTransitiveClosureOfRoleHierarchy().getSubsumers( id, data ))
		    this.add(i);
		//if "this" is not identified, it is now
		data.addRidge(this);
	}

	//If "r" is not in "this", a new RoleLabel is created 
	public  RoleLabel getNewRoleLabel(Integer r, ReasonerData data) {
		if( ! this.contains(r) ) {
		    RoleLabel rl = new RoleLabel();
		    for(Integer role : this){ 
		        rl.add(role);
                    }
		    rl.add( r );
		    for(Integer i : data.getTransitiveClosureOfRoleHierarchy().getSubsumers( r, data ))
		        rl.add(i);
		    data.addRidge(rl);
		    return rl;
                } else
		return this;
	}

	public  RoleLabel getNewRoleLabel(Set<Integer> lr, ReasonerData data) {
		RoleLabel rl = new RoleLabel();
		for(Integer i : lr) {
		    rl.add(i);
		    for(Integer j : data.getTransitiveClosureOfRoleHierarchy().getSubsumers(i , data)) 
		       rl.add(j);
		}
		rl.addAll(lr);
		
		data.addRidge(rl);
		return rl;
	}

	public  boolean contains(Role role) {
		return this.contains(role.getIdentifier());
	}

	public  boolean contains(Integer role) {
		return this.contains(role);
	}

	public boolean isInverseOf(RoleLabel roleLabel, ReasonerData data) {
		boolean inverseFound = false;
		for (Integer role : this) {
			for (Integer r : roleLabel)
				if (data.getRoles().get(role).isInverseOf(data.getRoles().get( r ) )) {
					inverseFound = true;
					continue;
				}

			if (inverseFound) {
				inverseFound = false;
				continue;
			} else
				return false;
		}

		return true;
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
		if (this == obj)
			return true;
		if (obj == null )
			return false;

		if (getClass() != obj.getClass())
			return false;

		RoleLabel other = (RoleLabel) obj;

		if (this.identifier > 0 && other.identifier > 0)
			if (this.identifier == other.identifier)
				return true;
			else
				return false;
		//if (this.size() != other.size())
		//	return false;

		//for (Role r : this)
		//	if (!other.contains(r))
		//		return false;

		//Check if two sets of identifiers equal. This is performed between two sets of Integers 
		if( super.equals( other.getClass().getSuperclass() ) ) 
		     return true;
 
		return false;
	}

	public RoleLabel getInverseOf(ReasonerData data) {
		RoleLabel rl = new RoleLabel();
		for (Integer r : this) {
		     Role rInverse = new Role( data.getRoles().get(r).getName(), -1, data.getRoles().get(r).isTransitive(), 
					data.getRoles().get(r).isFunctional(), data.getRoles().get(r).isInverse(), data.getRoles().get(r).isTransitiveClosure());
		     if(data.getRoles().get(r).isInverse()){
		   	rInverse = data.giveRoleIdentifier(rInverse);
		        rInverse.setInverse(false);
		     } else {
		        rInverse = data.giveRoleIdentifier(rInverse);
		        rInverse.setInverse(true);
		     }
		     data.addRole(rInverse);
		     rl = rl.getNewRoleLabel(rInverse.getIdentifier(), data);
		      
		}

		data.addRidge(data.giveRidgeIdentifier(rl));
		return rl;
	}

	 
	public String toString(ReasonerData data) {
		StringBuilder sb = new StringBuilder();

		for (Integer i : this) {
			sb.append(data.getRoles().get( i.intValue() ).toString());
			sb.append(System.getProperty("line.separator"));
		}

		return sb.toString();
	}
}
