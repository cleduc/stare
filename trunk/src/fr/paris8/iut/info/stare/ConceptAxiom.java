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
 * Evolved representation of axioms concerning classes. Splits it in two parts;
 * the part on the left is a class, the part on the right is an expression (both
 * are registered as OWLClassExpressions, for convenience). Both parts are
 * separated by a subset operator.
 * 
 * @author Jérémy Lhez
 * 
 */
public class ConceptAxiom {
	private int identifier;
	private Concept left, right;
	private Concept NNF;

	/**
	 * Default constructor.
	 * 
	 * @param id
	 *            The identifier used for the ConceptAxiom.
	 * @param l
	 *            The left member of the axiom.
	 * @param r
	 *            The right member of the axiom.
	 * @param nnf
	 *            The axiom in a negative normal form.
	 */
	public ConceptAxiom(int id, Concept l, Concept r, Concept nnf) {
		this.identifier = id;
		this.left = l;
		this.right = r;
		this.NNF = nnf;
	}

	/**
	 * Getter for the left member.
	 * 
	 * @return The left member.
	 */
	public Concept getLeftMember() {
		return left;
	}

	/**
	 * Getter for the right member.
	 * 
	 * @return The right member.
	 */
	public Concept getRightMember() {
		return right;
	}

	/**
	 * Getter for the identifier.
	 * 
	 * @return The identifier associated to the axiom.
	 */
	public int getIdentifier() {
		return identifier;
	}

	/**
	 * Getter for the NNF form of the axiom.
	 * 
	 * @return The negative normal form of the axiom.
	 */
	public Concept getNNF() {
		return NNF;
	}

	@Override
	public String toString() {
		return "ConceptAxiom " + NNF + ", identifier=" + identifier + ", left="
				+ left + ", right=" + right;
	}

}
