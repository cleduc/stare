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
 * Evolved representation of axioms concerning properties. Both member, left and
 * right, are stored as variables, considering that the left one if a sub
 * property of the right one.
 * 
 * @author Jérémy Lhez
 * 
 */
public class RoleAxiom {
	private int identifier;
	private Role leftRole, rightRole;

	public RoleAxiom(int id, Role left, Role right) {
		this.identifier = id;
		this.leftRole = left;
		this.rightRole = right;
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
	
	public int getIdentifier() {
		return identifier;
	}

	public Role getLeftRole() {
		return leftRole;
	}

	public Role getRightRole() {
		return rightRole;
	}

	@Override
	public String toString() {
		return identifier + " Role " + leftRole.getName() + " subproperty of "
				+ rightRole.getName();
	}

}
