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

/**
 * Representation of a role. Each role has a name and an identifier, that are
 * different for the roles of the ontology. The roles can also be transitive,
 * functional, inverse or transitive closures.<br/>
 * 
 * An inverse role will have the same name and the same properties (functional,
 * transitive), but an identifier negative. A transitive closure role will have
 * the same name, a negative identifier and will never be functional (of course,
 * it will be transitive. A negative transitive closure is a transitive closure
 * with the isInverse variable set at true.set at true.<br/>
 * 
 * Transitive closures, inverses and inverse transitive closures are
 * automatically generated by methods of the LoadOntology classes and will be
 * initialized with an identifier value of -2.
 * 
 * @author Jeremy Lhez
 * 
 */
public class Role {
	protected String name;
	protected int identifier;
	protected boolean isTransitive, isFunctional, isInverse,
			isTransitiveClosure;

	/**
	 * Standard constructor.
	 * 
	 * @param name
	 *            Name of the property.
	 * @param identifier
	 *            Number chosen as identifier.
	 * @param transitive
	 *            If the property is transitive.
	 * @param functional
	 *            If the property is functional.
	 * @param inverse
	 *            If the property is an inverse.
	 * @param closure
	 *            If the property is a transitive closure.
	 */
	public Role(String name, int identifier, boolean transitive,
			boolean functional, boolean inverse, boolean closure) {
		this.name = name;
		this.identifier = identifier;
		this.isTransitive = transitive;
		this.isFunctional = functional;
		this.isInverse = inverse;
		this.isTransitiveClosure = closure;
	}

	/**
	 * A setter for the identifier.
	 * 
	 * @param id
	 *            The new id to be set.
	 * @return true if the id has been changed (the previous id was temporary),
	 *         false otherwise (the previous id was not temporary).
	 */
	public boolean setIdentifier(int id) {
		if (this.identifier >= 0)
			return false;
		else {
			this.identifier = id;
			return true;
		}
	}

	public void setInverse(boolean value) {
		this.isInverse = value;
	}

	/**
	 * Simple getter for the value.
	 * 
	 * @return The identifier of the role.
	 */
	public int getIdentifier() {
		return identifier;
	}

	/**
	 * Checks if the role is transitive.
	 * 
	 * @return true if it is, false otherwise.
	 */
	public boolean isTransitive() {
		return isTransitive;
	}

	/**
	 * Checks if the role is functional.
	 * 
	 * @return true if it is, false otherwise.
	 */
	public boolean isFunctional() {
		return isFunctional;
	}

	/**
	 * Checks if the role is an inverse (generated).
	 * 
	 * @return true if it is, false otherwise.
	 */
	public boolean isInverse() {
		return isInverse;
	}

	/**
	 * Checks if the role is a transitive closure (generated).
	 * 
	 * @return true if it is, false otherwise.
	 */
	public boolean isTransitiveClosure() {
		return isTransitiveClosure;
	}

	/**
	 * Simple getter for the name.
	 * 
	 * @return The name of the role.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Checks if the role is an inverse of another one.
	 * 
	 * @param other
	 *            The possible inverse.
	 * @return true if both roles are inverses, false otherwise.
	 */
	public boolean isInverseOf(Role other) {
		if(this.getName() == null || other.getName() == null)
		   return false;
		if( name.equals(other.getName()) ) {
                    if(this.isInverse() && !other.isInverse())
                       return true;
		    if(!this.isInverse() && other.isInverse())
                       return true;
		}

		return false;
		/*
		if (isFunctional != other.isFunctional)
			return false;
		if (isInverse == other.isInverse)
			return false;
		if (isTransitive != other.isTransitive)
			return false;
		if (isTransitiveClosure != other.isTransitiveClosure)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
		*/
	}

	public Role getInverseOf(Role ini, ReasonerData data) {
		Role rl = new Role(ini.getName(), -1, ini.isTransitive(), ini.isFunctional(), ini.isInverse(), ini.isTransitiveClosure());
		if(ini.isInverse()){
		   rl = data.giveRoleIdentifier(rl);
		   rl.setInverse(false);
		} else {
		   rl = data.giveRoleIdentifier(rl);
		   rl.setInverse(true);
		}
		rl = data.addRole(rl);
		return rl;
	}

	

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder(name + " ");

		if (isFunctional)
			string.append("functional ");

		if (isTransitive)
			string.append("transitive ");

		if (isInverse)
			string.append("inverse");

		return string.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		Role other = (Role) obj;
		if (this.getName()==null && other.getName()!=null)
			return false;
		if (this.getName()!=null && other.getName()==null)
			return false;
		if ((identifier >= 0) && (other.identifier >= 0))
			if (identifier == other.identifier)
				return true;
		if (isFunctional != other.isFunctional)
			return false;
		if (isInverse != other.isInverse)
			return false;
		if (isTransitive != other.isTransitive)
			return false;
		if (isTransitiveClosure != other.isTransitiveClosure)
			return false;
		if (name == null || other.name == null) {
			return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
