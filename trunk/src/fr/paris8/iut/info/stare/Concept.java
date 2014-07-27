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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Representation of a concept.<br/>
 * If a concept is terminal, it is either a class (from the ontology) or a
 * datatype (a literal). In both cases, the name/value of the terminal is
 * stored, and an identifier is associated.<br/>
 * 
 * If it is not a terminal, then the concept is an operation of a certain type,
 * and this operation applies to a certain number of arguments. The type of
 * operation is stored, along with the arguments. Sometimes the operation needs
 * more parameters, as a role or a number; then can also be stored using a
 * different constructor.<br/>
 * 
 * The children of a concept are also concepts, that means one creates concepts
 * recursively. The class also overrides the equals method from object, in order
 * to see if two concepts are equivalent.
 * 
 * @author Jeremy Lhez
 * 
 */
public class Concept {
	/** List of members for the concept, null if the concept is terminal */
	private List<Concept> children;
	/** Whether the concept is a terminal or not */
	private boolean isTerminal, isDatatype, isNominal;
	/** The operator for the concept, if it is not terminal */
	private Type operator;
	/** The identifier of the concept, */
	private int identifier;
	/** The cardinality of the operator, if it is MIN or MAX */
	private int cardinality;
	private Role role;
	private String name;

	/**
	 * Enumeration of all the types of non-terminal concepts.
	 * 
	 * @author Jeremy Lhez
	 * 
	 */
	public enum Type {
		ALL, SOME, UNION, INTERSECTION, COMPLEMENT, MIN, MAX
	}

	/**
	 * Constructor for a terminal concept.
	 * 
	 * @param name
	 *            The value of the class/individual.
	 * @param identifier
	 *            Its identifier.
	 * @param isDatatype
	 *            Defines if the terminal is a DataType.
	 * @param isNominal
	 *            Defines if the startype is nominal.
	 */
	public Concept(String name, Integer identifier, boolean isDatatype,
			boolean isNominal) {
		this.name = name;
		this.role = null;
		this.operator = null;
		this.children = null;
		this.cardinality = -1;
		this.isTerminal = true;
		this.isNominal = isNominal;
		this.isDatatype = isDatatype;
		this.identifier = identifier;
	}

	/**
	 * Constructor for a nominal non-singleton concept.
	 * 
	 * @param children
	 *            The individual represented by the concept.
	 */
	public Concept(Concept... children) {
		this.role = null;
		this.name = null;
		this.operator = null;
		this.isNominal = true;
		this.cardinality = -1;
		this.isTerminal = false;
		this.isDatatype = false;
		this.identifier = -1;
		this.children = new ArrayList<Concept>(Arrays.asList(children));
	}

	/**
	 * Constructor for an anonymous class.
	 * 
	 * @param type
	 *            The operator of the class.
	 * @param children
	 *            The arguments of the anonymous class.
	 */
	public Concept(Type type, Concept... children) {
		this.role = null;
		this.name = null;
		this.operator = type;
		this.cardinality = -1;
		this.identifier = -1;
		this.isNominal = false;
		this.isTerminal = false;
		this.isDatatype = false;
		this.children = new ArrayList<Concept>(Arrays.asList(children));
	}

	/**
	 * Constructor for an anonymous class, with restriction operators.
	 * 
	 * 
	 * @param type
	 *            The operator of the class.
	 * @param role
	 *            The property to apply the restriction.
	 * @param children
	 *            The arguments of the anonymous class.
	 */
	public Concept(Type type, Role role, Concept... children) {
		this.role = role;
		this.name = null;
		this.operator = type;
		this.cardinality = -1;
		this.isNominal = false;
		this.identifier = -1;
		this.isTerminal = false;
		this.isDatatype = false;
		this.children = new ArrayList<Concept>(Arrays.asList(children));
	}

	/**
	 * Constructor for an anonymous class, with cardinality restriction
	 * operators.
	 * 
	 * @param type
	 *            The operator of the class.
	 * @param cardinality
	 *            The cardinality for the operator.
	 * @param role
	 *            The property to apply the restriction.
	 * @param children
	 *            The arguments of the anonymous class.
	 */
	public Concept(Type type, int cardinality, Role role, Concept... children) {
		this.role = role;
		this.name = null;
		this.operator = type;
		this.identifier = -1;
		this.isNominal = false;
		this.isTerminal = false;
		this.isDatatype = false;
		this.cardinality = cardinality;
		this.children = new ArrayList<Concept>(Arrays.asList(children));
	}

	/**
	 * Informs on the state of the concept.
	 * 
	 * @return true if it is a terminal one, false otherwise.
	 */
	public boolean isTerminal() {
		return isTerminal;
	}

	/**
	 * Gives the value of the concept (should be used only if it is a terminal).
	 * 
	 * @return The value of the concept, -1 if it is not a terminal one.
	 */
	public int getIdentifier() {
		return identifier;
	}

	/**
	 * Changes the value of the identifier.
	 * 
	 * @param id
	 *            The new value for the identifier.
	 */
	public void setIdentifier(int id) {
		this.identifier = id;
	}

	/**
	 * Gives the members of the concept (should be used only if it is not a
	 * terminal).
	 * 
	 * @return The members of the concept, null if it is a terminal.
	 */
	public List<Concept> getChildren() {
		return children;
	}

	/**
	 * Gives the operator of the concept (should be used only if it is not a
	 * terminal).
	 * 
	 * @return The operator of the concept, null if it is a terminal.
	 */
	public Type getOperator() {
		return operator;
	}

	/**
	 * Gives the cardinality for the operator of the concept (should be used
	 * only if it is not a terminal, and if the operator is a cardinality
	 * restriction).
	 * 
	 * @return The cardinality for the operator of the concept, null if it is a
	 *         terminal or not a cardinality restriction.
	 */
	public int getCardinality() {
		return cardinality;
	}

	/**
	 * Gives the role of the concept (should be used only if it is a
	 * restriction).
	 * 
	 * @return true if the concept is a role, false if it is anything else.
	 */
	public Role getRole() {
		return role;
	}

	/**
	 * Gives the name of the concept (should be used only if it is a terminal)
	 * 
	 * @return The name of the concept, null if it is not a terminal.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Checks if the concept is a Datatype.
	 * 
	 * @return true if it is, false otherwise.
	 */
	public boolean isDatatype() {
		if (this.isTerminal)
			return isDatatype;
		return false;
	}

	/**
	 * Checks if the concept is nominal.
	 * 
	 * @return true if it is, false otherwise.
	 */
	public boolean isNominal() {
		return this.isNominal;
	}

	/**
	 * Adds children to the children list.
	 * 
	 * @param children
	 *            The list of children to be added.
	 */
	public void addChild(Concept... children) {
		for (Concept child : children)
			this.children.add(child);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Concept node = (Concept) obj;

		/* both concepts have valid and equals identifiers */
		if ((this.getIdentifier() >= 0) && (node.getIdentifier() >= 0))
			if (this.getIdentifier() == node.getIdentifier())
				return true;

		/* both terminals */
		if (this.isTerminal && node.isTerminal) {
			if (this.getName().equals(node.getName()))
				return true;

			return false;
		}
		/* both non terminals */
		else if (!this.isTerminal && !node.isTerminal) {
			/* for nominal non singleton */
			if (this.isNominal) {
				if (node.isNominal
						&& (this.children.size() == node.children.size())) {
					for (Concept child : this.children)
						if (!node.getChildren().contains(child))
							return false;
					return true;
				} else
					return false;
			}

			switch (this.operator) {
			case ALL:
				/* same operator */
				if (node.getOperator() == Type.ALL)
					/* same roles */
					if (this.getRole().equals(node.getRole()))
						/* same members */
						if (this.getChildren().get(0)
								.equals(node.getChildren().get(0)))
							return true;
				return false;
			case SOME:
				/* same operator */
				if (node.getOperator() == Type.SOME)
					/* same roles */
					if (this.getRole().equals(node.getRole())) {
						/* same members */
						if (this.getChildren().get(0)
								.equals(node.getChildren().get(0)))
							return true;
					}  
				return false;
			case MAX:
				/* same operator & cardinality */
				if ((node.getOperator() == Type.MAX)
						&& (node.getCardinality() == this.getCardinality()))
					/* same roles */
					if (this.getRole().equals(node.getRole()))
						/* same members */
						if (this.getChildren().get(0)
								.equals(node.getChildren().get(0)))
							return true;
				return false;
			case MIN:
				/* same operator & cardinality */
				if ((node.getOperator() == Type.MIN)
						&& (node.getCardinality() == this.getCardinality()))
					/* same roles */
					if (this.getRole().equals(node.getRole())) { 
						/* same members */
						if (this.getChildren().get(0)
								.equals(node.getChildren().get(0)))
							return true;
					}  
				return false;
			case INTERSECTION:
				/* same operator */
				if (node.getOperator() == Type.INTERSECTION)
					if ((this.children.containsAll(node.getChildren()))
							&& (node.getChildren().size() == this.getChildren()
									.size()))
						return true;
				return false;
			case UNION:
				/* same operator */
				if (node.getOperator() == Type.UNION)
					if ((this.children.containsAll(node.getChildren()))
							&& (node.getChildren().size() == this.getChildren()
									.size()))
						return true;
				return false;
			case COMPLEMENT:
				if (node.getOperator() == Type.COMPLEMENT)
					if (this.getChildren().get(0).equals(node.children.get(0)))
						return true;
				return false;
			default:
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		if (isTerminal) {
			return name;
		} else {
			StringBuilder string = new StringBuilder();

			string.append(operator);

			if (cardinality != -1)
				string.append(" " + cardinality);

			if (role != null)
				string.append(" " + role.getName() + " ");
			string.append("(");

			for (Concept concept : children) {
				if (concept != null)
					string.append(concept.toString() + " ");
				else
					string.append("null ");
			}
			string.append(")");

			return string.toString();
		}
	}

	/**
	 * Checks if an operator is present inside a concept (recursively).
	 * 
	 * @param type
	 *            The type to be searched.
	 * @return the concept containing the operator, null otherwise.
	 */
	public Concept containsOperator(Type type) {
		Concept concept;

		if (children != null) {
			// has children, not terminal. Test the operator.
			if (operator == type)
				return this;

			for (Concept child : children) {
				if (child != null) {
					concept = child.containsOperator(type);
					// if one of the children contains the type, propagate true.
					if (concept != null)
						return concept;
				} else {
					continue;
				}
			}
		}

		return null;
	}

	/**
	 * Spreads the COMPLEMENT operator into a concept.
	 * 
	 * @param concept
	 *            The concept to apply the complement.
	 * @return The concept under NNF.
	 */
	public static Concept negate(Concept concept, ReasonerData data) {
		Concept c;
		if (concept.isTerminal) {
			c = data.giveConceptIdentifier(new Concept(Type.COMPLEMENT, concept));
			data.addConcept(c);
			return c;
		} else {
			switch (concept.getOperator()) {
			case COMPLEMENT:
				return concept.getChildren().get(0);
			case UNION:
				c = data.giveConceptIdentifier(new Concept(Type.INTERSECTION,
						negate(concept.getChildren().get(0), data), negate(
								concept.getChildren().get(0), data)));
				data.addConcept(c);
				return c;
			case INTERSECTION:
				c = data.giveConceptIdentifier(new Concept(Type.UNION, negate(
						concept.getChildren().get(0), data), negate(concept
						.getChildren().get(0), data)));
				data.addConcept(c);
				return c;
			case SOME:
				c = data.giveConceptIdentifier(new Concept(Type.ALL, concept
						.getRole(), negate(concept.getChildren().get(0), data)));
				data.addConcept(c);
				return c;
			case ALL:
				c = data.giveConceptIdentifier(new Concept(Type.SOME, concept
						.getRole(), negate(concept.getChildren().get(0), data)));
				data.addConcept(c);
				return c;
			case MIN:
				if (concept.getCardinality() == 1) {
					c = data.giveConceptIdentifier(new Concept(Type.ALL,
							concept.getRole(), negate(concept.getChildren()
									.get(0), data)));
					data.addConcept(c);
					return c;
				} else {
					c = data.giveConceptIdentifier(new Concept(Type.MAX,
							concept.getCardinality() - 1, concept.getRole(),
							concept.getChildren().get(0)));
					data.addConcept(c);
					return c;
				}
			case MAX:
				c = data.giveConceptIdentifier(new Concept(Type.MIN, concept
						.getCardinality() + 1, concept.getRole(), concept
						.getChildren().get(0)));
				data.addConcept(c);
				return c;
			default:
				return null;
			}
		}
	}
}
