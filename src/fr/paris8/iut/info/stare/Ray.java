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

// a ray of a StarType
public class Ray {
	private int id;
	private RoleLabel rl;
	private ConceptLabel cl;
	private static int increment = 0;

	public Ray(RoleLabel rl, ConceptLabel cl) {
		this.id = increment;
		this.rl = rl;
		this.cl = cl;
		increment++;
	}

	/**
	 * Creation with single labels.
	 * 
	 * @param r
	 *            The role to add to the edge.
	 * @param c
	 *            The concept to add to the tip.
	 */
	public Ray(Role r, Concept c) {
		this.id = increment;
		this.rl = new RoleLabel();
		this.cl = new ConceptLabel();

		rl.add(r);
		cl.add(c);
		increment++;
	}

	public boolean matches(Role role, Concept c) {
		if (this.rl.contains(role))
			if (this.cl.contains(c))
				return true;
		return false;
	}

	public void addRole(Role r) {
		rl.add(r);
	}

	public boolean addConcept(Concept c) {
		return cl.add(c);
	}

	public RoleLabel getRidge() {
		return rl;
	}

	public ConceptLabel getTip() {
		return cl;
	}

	public int getId() {
		return id;
	}

	public Ray fusion(Ray ray2) {
		RoleLabel rl = new RoleLabel();
		ConceptLabel cl = new ConceptLabel();

		rl.addAll(this.getRidge());
		rl.addAll(ray2.getRidge());
		cl.addAll(this.getTip());
		cl.addAll(ray2.getTip());

		return new Ray(rl, cl);
	}
}
