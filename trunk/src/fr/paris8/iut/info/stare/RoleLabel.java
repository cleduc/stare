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
public class RoleLabel {
	private static final long serialVersionUID = 1L;
	private int identifier = -1;
	private Set<Integer> roleIds;

	public RoleLabel() {
		roleIds = new HashSet<Integer>();
	}
	//create a RoleLabel from a single role that must be identified 
	public RoleLabel(Role r, ReasonerData data) {
		roleIds = new HashSet<Integer>();
		roleIds.add(r.getIdentifier());
		for(Integer i : data.getSubsumers( r.getIdentifier() ))
		    roleIds.add(i);
	}
	
	//If concept is already identifed
	public RoleLabel(Integer id, ReasonerData data) {
		roleIds = new HashSet<Integer>();
		roleIds.add(id);
		for(Integer i : data.getSubsumers( id ))
		    roleIds.add(i);
	}

	public void add(Role r) {
		roleIds.add(r.getIdentifier());
	}
	public void add(Integer id) {
		roleIds.add(id);
	}

	//If "r" is not in "this", a new RoleLabel is created 
	public  RoleLabel getNewRoleLabel(Integer r, ReasonerData data) {
		if( ! contains(r) ) {
		    RoleLabel rl = new RoleLabel();
		    for(Integer role : roleIds){ 
		        rl.add(role);
                    }
		    rl.add( r );
		    for(Integer i : data.getSubsumers( r ))
		        rl.add(i);
		    rl = data.addRidge(rl);
		    return rl;
                } else
		return this;
	}

	public  RoleLabel getNewRoleLabel(Set<Integer> lr, ReasonerData data) {
		RoleLabel rl = new RoleLabel();
		for(Integer i : lr) {
		    rl.add(i);
		    for(Integer j : data.getSubsumers(i)) 
		       rl.add(j);
		}
		rl.getRoleIds().addAll(lr);
		
		data.addRidge(rl);
		return rl;
	}

	public  boolean contains(Role role) {
		return roleIds.contains(role.getIdentifier());
	}

	public  boolean contains(Integer role) {
		return roleIds.contains(role);
	}

	public boolean isInverseOf(RoleLabel roleLabel, ReasonerData data) {
		RoleLabel rl = roleLabel.getInverseOf(data);
		return rl.getInverseOf(data).equals(roleLabel);
	}

	public void setIdentifier(int id) {
		this.identifier = id;
	}

	public int getIdentifier() {
		return this.identifier;
	}

	public void setRoleIds(Set<Integer> s) {
		this.roleIds = s;
	}

	public Set<Integer> getRoleIds() {
		return this.roleIds;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null )
			return false;
		if (getClass() != obj.getClass())
			return false;
		RoleLabel other = (RoleLabel) obj;
		if (this.identifier >= 0 && other.identifier >= 0)
			if (this.identifier == other.identifier)
				return true;
			else
				return false;
		//Check if two sets of identifiers equal. This is performed between two sets of Integers 
		if( roleIds.equals(other.getRoleIds() ))  
		     return true;
 
		return false;
	}

	public RoleLabel getInverseOf(ReasonerData data) {
		RoleLabel rl = new RoleLabel();
		for (Integer r : roleIds) {
		     boolean inv = (data.getRoles().get(r).isInverse() ? false : true );
		     Role rInverse = new Role( data.getRoles().get(r).getName(), -1, data.getRoles().get(r).isTransitive(), 
					data.getRoles().get(r).isFunctional(), inv, data.getRoles().get(r).isTransitiveClosure());
		     rInverse = data.addRole(rInverse);	
		     rl = rl.getNewRoleLabel(rInverse.getIdentifier(), data);
		}
		return rl;
	}

	 
	public String toString(ReasonerData data) {
		StringBuilder sb = new StringBuilder();

		for (Integer i : roleIds) {
			sb.append(data.getRoles().get( i.intValue() ).toString());
			sb.append(System.getProperty("line.separator"));
		}

		return sb.toString();
	}
}
