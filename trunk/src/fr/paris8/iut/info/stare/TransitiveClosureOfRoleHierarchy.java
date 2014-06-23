package fr.paris8.iut.info.stare;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Representation of the transitive closure for the roles. A transitive closure
 * can be calculated for an ontology.
 * 
 * @author Jeremy Lhez
 */
public class TransitiveClosureOfRoleHierarchy {
	private Collection<RoleAxiom> closure, ontologyAxioms;
	private Collection<Role> ontologyRoles;
	private int identifier;

	/**
	 * Default constructor. Fills the list of RoleAxiom representing the
	 * transitive closure.
	 * 
	 * @param axioms
	 *            The list of role axioms in the ontology.
	 * @param roles
	 *            The list of roles in the ontology.
	 */
	public TransitiveClosureOfRoleHierarchy(Collection<RoleAxiom> axioms,
			Collection<Role> roles) {
		List<RoleAxiom> tmp1, tmp2;

		closure = new ArrayList<RoleAxiom>();
		ontologyRoles = roles;
		ontologyAxioms = axioms;
		identifier = axioms.size();

		tmp1 = this.step2();
		tmp2 = this.step3();

		closure.addAll(tmp1);
		closure.addAll(tmp2);
	}

	/**
	 * Step 2: adds reflexive axioms to the closure.<br/>
	 * WARNING: there is no verification about duplications, that means there
	 * must be no reflexive axioms (in the ontology) before the call of this
	 * function, or there will be duplicates after merging the return of this
	 * function and the closure.
	 * 
	 * @return A list of reflexive axioms for each role of the ontology (with no
	 *         duplications).
	 */
	private List<RoleAxiom> step2() {
		List<RoleAxiom> reflexive = new ArrayList<RoleAxiom>();

		for (Role role : ontologyRoles) {
			reflexive.add(new RoleAxiom(identifier, role, role));
			identifier++;
		}

		return reflexive;
	}

	/**
	 * Calls the recursive function for each role of the ontology.
	 * 
	 * @return A list of axioms created transitively.
	 */
	private List<RoleAxiom> step3() {
		List<RoleAxiom> transitive = new ArrayList<RoleAxiom>();
		List<Role> nextRoles = new ArrayList<Role>();
		
		for (Role leftRole : ontologyRoles) {
			nextRoles = step3Recursive(leftRole);

			if (nextRoles == null)
				continue;

			for (Role rightRole : nextRoles) {
				transitive.add(new RoleAxiom(identifier, leftRole, rightRole));
				identifier++;
			}
		}

		return transitive;
	}

	/**
	 * Finds and returns all the right members of axioms of the ontology with a
	 * specific left member.<br.>
	 * WARNING: this function is recursive, therefore the ontology must not
	 * contain reflexive axioms.
	 * 
	 * @param left
	 *            Left member for the axioms.
	 * @return List of all the right members possible for the given left member
	 *         (transitively).
	 */
	private List<Role> step3Recursive(Role left) {
		List<Role> rightList = new ArrayList<Role>();
		List<Role> nextRoles = new ArrayList<Role>();
		List<Role> tmp = new ArrayList<Role>();

		// searching all the axioms with the role as left member
		for (RoleAxiom axiom : ontologyAxioms) {
			if (axiom.getLeftRole().equals(left)) {
				// for each axiom found, store the right one
				rightList.add(axiom.getRightRole());
			}
		}

		// if no axiom has been found, end of recursion
		if (rightList.isEmpty()) {
			return null;
		} else {
			for (Role role : rightList) {
				// recall the function for each right member
				nextRoles = step3Recursive(role);
				if (nextRoles != null) {
					// add all the recursive values to the result list
					tmp.addAll(nextRoles);
				}
			}
			rightList.addAll(tmp);
			
			return rightList;
		}
	}

	/**
	 * A getter for the list of RoleAxiom.
	 * 
	 * @return The transitive closure.
	 */
	public Collection<RoleAxiom> getTransitiveClosure() {
		return closure;
	}
}