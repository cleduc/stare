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

import java.util.Stack;

import fr.paris8.iut.info.stare.Concept.Type;

/**
 * Class transforming a string expression into a concept. The expression must
 * respect a special format.<br/>
 * <br/>
 * Binary operators recognized: AND, OR<br/>
 * Unary operators recognized: SOME, ALL, MAX, MIN, NOT<br/>
 * <br/>
 * SOME, ALL, MAX and MIN are restriction operators, therefore they must be
 * followed by a property name. Moreover, in the case of MAX and MIN, there must
 * be a number between the operator and the property.<br/>
 * Example:<br/>
 * SOME hasAncestor Jack<br/>
 * MAX 3 hasChild children<br/>
 * <br/>
 * Each Binary operator applies to two operands, and must be framed by
 * parenthesis. Unary operators applies to only one operand, but must be framed
 * the same way. Each term of an expression must also be separated by spaces.<br/>
 * Example:<br/>
 * (SOME hasAncestor Paul)<br/>
 * (Marie AND Pierre)<br/>
 * <br/>
 * One expression can be composed of multiple expressions, recursively; in that
 * case, apply all the previous rules. Do not forget to frame the final
 * expression with parenthesis. The parenthesis must be pasted to the words:
 * they must not be separated by spaces.<br/>
 * Example:<br/>
 * ((SOME R (C AND D)) OR C)<br/>
 * 
 * @author Jeremy Lhez
 * 
 */
public class StringToConcept {
	private static Stack<String> stack;

	/**
	 * Transforms a string into a concept. The concept is simplified: each
	 * terminal role or concept is identified by -2. Moreover, each role is
	 * considered non-transitive, non-functional, non-inverse and non-transitive
	 * closure. The concept are never considered as datatypes.
	 * 
	 * @param string
	 *            The string to be converted.
	 * @return The concept obtained, null if the given string is invalid
	 *         (according to the parenthesis).
	 */
	public static Concept stringToConcept(String string, ReasonerData data) {
		stack = new Stack<String>();
		stackComponents(string);
		return makeConcept(data);
	}

	/**
	 * Stacks each component of the string into a stack, sorting them into a
	 * logical order. Two stacks are used in this function, but they are merged
	 * according to the parenthesis found to obtain a logical order.
	 * 
	 * @param string
	 *            The string to be converted.
	 * @return true if the string is semantically valid according to the
	 *         parenthesis, false otherwise.
	 */
	private static void stackComponents(String string) {
		Stack<String> operators = new Stack<String>();
		String[] components = string.split(" ");
		String component, top;
		int index = 0, pop = 0;

		while (index < components.length) {
			component = components[index];

			// stacking opening parenthesis (they can be several)
			while (component.startsWith("(")) {
				operators.push("(");
				component = component.substring(1);
			}

			// when a closing parenthesis is met
			if (component.endsWith(")")) {
				/*
				 * push the component in the first stack (in case of a closing
				 * parenthesis, the component can only be a terminal)
				 */
				stack.push(component.replaceAll("\\)", ""));
				while (component.endsWith(")")) {
					// pop from the stack until the last opening parenthesis
					top = operators.pop();
					while (!top.equals("(")) {
						stack.push(top);
						top = operators.pop();
						pop++;
					}
					pop = 0;
					component = component.substring(0, component.length() - 1);
				}

				index++;
				continue;
			}

			/*
			 * once rid of its parenthesis, check if the component is an
			 * operator
			 */
			if (isOperator(component)) {
				// if it is, push it in the second stack
				operators.push(component);
				index++;

				/*
				 * if next component is a number, it is a restriction for MAX or
				 * MIN
				 */
				if (components[index].matches("[0-9]+")) {
					operators.push(components[index]);
					index++;
				}

				// restriction properties need a role
				if ((component.equals("MAX")) || (component.equals("MIN"))
						|| (component.equals("SOME"))
						|| (component.equals("ALL"))) {
					operators.push(components[index]);
					index++;
				}
			} else {
				// if it is not an operator, just push it in the first stack
				stack.push(component);
				index++;
			}
		}
	}

	/**
	 * Takes each element of the stack to create the final concept. If it is not
	 * a terminal, this concept is composed of many others: each concept has a
	 * specific prototype, which allows us to create the final concept
	 * recursively by popping elements of the stack one by one.
	 * 
	 * @return The final concept, representing the expression in the string.
	 */
	private static Concept makeConcept(ReasonerData data) {
		String top = stack.pop();
		Role role;
		Concept concept;

		if (!isOperator(top)) {
			concept = giveConceptIdentifier(new Concept(top, -2, false), data);

			data.addConcept(concept);
			return concept;
		} else {
			if (top.equals("NOT")) {
				return new Concept(Type.COMPLEMENT, makeConcept(data));
			} else if (top.equals("SOME")) {
				role = giveRoleIdentifier(new Role(stack.pop(), -2, false,
						false, false, false), data);
				concept = giveConceptIdentifier(new Concept(Type.SOME, role,
						makeConcept(data)), data);

				data.addRole(role);
				data.addConcept(concept);

				return concept;
			} else if (top.equals("ALL")) {
				role = giveRoleIdentifier(new Role(stack.pop(), -2, false,
						false, false, false), data);
				concept = giveConceptIdentifier(new Concept(Type.ALL, role,
						makeConcept(data)), data);

				data.addRole(role);
				data.addConcept(concept);

				return concept;
			} else if (top.equals("MAX")) {
				int cardinality = Integer.parseInt(stack.pop());
				role = giveRoleIdentifier(new Role(stack.pop(), -2, false,
						false, false, false), data);
				concept = giveConceptIdentifier(new Concept(Type.MAX,
						cardinality, role, makeConcept(data)), data);

				data.addRole(role);
				data.addConcept(concept);

				return concept;
			} else if (top.equals("MIN")) {
				int cardinality = Integer.parseInt(stack.pop());
				role = giveRoleIdentifier(new Role(stack.pop(), -2, false,
						false, false, false), data);
				concept = giveConceptIdentifier(new Concept(Type.MIN,
						cardinality, role, makeConcept(data)), data);
				
				data.addRole(role);
				data.addConcept(concept);

				return concept;
			} else if (top.equals("OR")) {
				Concept left, right;

				left = makeConcept(data);
				right = makeConcept(data);
				concept = giveConceptIdentifier(new Concept(Type.UNION, left,
						right), data);

				data.addConcept(concept);

				return concept;
			} else if (top.equals("AND")) {
				Concept left, right;

				left = makeConcept(data);
				right = makeConcept(data);
				concept = giveConceptIdentifier(new Concept(Type.INTERSECTION,
						left, right), data);

				data.addConcept(concept);

				return concept;
			} else {
				return null;
			}
		}
	}

	/**
	 * A simple comparator, telling if a string represents an operator or not.
	 * 
	 * @param string
	 *            The string to be checked.
	 * @return true if the operator is recognized, false otherwise.
	 */
	private static boolean isOperator(String string) {
		if ((string.equals("AND")) || (string.equals("OR"))
				|| (string.equals("MAX")) || (string.equals("MIN"))
				|| (string.equals("SOME")) || (string.equals("ALL"))
				|| (string.equals("NOT")))
			return true;
		return false;
	}

	/**
	 * Gives an identifier to a newly created Concept.
	 * 
	 * @param concept
	 *            The Concept to be identified.
	 * @return The Concept, with a valid identifier.
	 */
	private static Concept giveConceptIdentifier(Concept concept,
			ReasonerData data) {
		for (Concept c : data.getConcept().values()) {
			if (c.equals(concept)) {
				concept.setIdentifier(c.getIdentifier());
				return concept;
			}
		}
		concept.setIdentifier(data.getConcept().size());
		return concept;
	}

	/**
	 * Gives an identifier to a newly created Role.
	 * 
	 * @param role
	 *            The Role to be identified.
	 * @return The Role, with a valid identifier.
	 */
	private static Role giveRoleIdentifier(Role role, ReasonerData data) {
		for (Role r : data.getRoles().values()) {
			if (r.equals(role)) {
				role.setIdentifier(r.getIdentifier());
				return role;
			}
		}
		role.setIdentifier(data.getRoles().size());
		return role;
	}
}
