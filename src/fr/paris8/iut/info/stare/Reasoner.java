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
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.AxiomNotInProfileException;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.ClassExpressionNotInProfileException;
import org.semanticweb.owlapi.reasoner.FreshEntitiesException;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;
import org.semanticweb.owlapi.reasoner.TimeOutException;
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;
import org.semanticweb.owlapi.util.Version;

public class Reasoner implements OWLReasoner {

	// protected final Configuration configuration = null;
	protected final OWLOntology initOntology;

	// protected InternalOntology interOntology;

	// protected Tableau m_tableau;

	public Reasoner(OWLOntology onto) {
		this.initOntology = onto;
	}

	public boolean isConsistent() {
		return false;
	}

	public boolean isEntailed(OWLAxiom cons) {
		return false;
	}

	//
	public boolean checkForExpressiveness() {
		return false;
		// AxiomType
	}

	public void dispose() {
		// TODO Auto-generated method stub
	}

	public long getTimeOut() {
		// TODO Auto-generated method stub
		return (long) 0.;
	}

	public org.semanticweb.owlapi.reasoner.IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
		// TODO Auto-generated method stub
		return null;
	}

	public org.semanticweb.owlapi.reasoner.FreshEntityPolicy getFreshEntityPolicy() {
		// TODO Auto-generated method stub
		return null;
	}

	public org.semanticweb.owlapi.reasoner.Node<OWLClass> getEquivalentClasses(
			OWLClassExpression arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<OWLClass> getInconsistentClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Set<OWLClass>> getSubClasses(OWLClassExpression arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Set<OWLClass>> getSuperClasses(OWLClassExpression arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isEquivalentClass(OWLClassExpression arg0,
			OWLClassExpression arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSubClassOf(OWLClassExpression arg0, OWLClassExpression arg1) {
		for (OWLSubClassOfAxiom subClassOfAxiom : initOntology
				.getSubClassAxiomsForSubClass(arg0.asOWLClass()))
			if (subClassOfAxiom.getSubClass().equals(arg1))
				return true;
		return false;
	}

	public boolean isSatisfiable(OWLClassExpression arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public Map<OWLDataProperty, Set<OWLLiteral>> getDataPropertyRelationships(
			OWLIndividual arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<OWLIndividual> getIndividuals(OWLClassExpression arg0,
			boolean arg1) {
		Set<OWLIndividual> individuals = new HashSet<OWLIndividual>();

		for (OWLNamedIndividual individual : arg0.getIndividualsInSignature())
			individuals.add(individual);

		return individuals;
	}

	public Map<OWLObjectProperty, Set<OWLIndividual>> getObjectPropertyRelationships(
			OWLIndividual arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<OWLIndividual> getRelatedIndividuals(OWLIndividual arg0,
			OWLObjectPropertyExpression arg1) {
		return arg0.getObjectPropertyValues(arg1, initOntology);
	}

	public Set<OWLLiteral> getRelatedValues(OWLIndividual arg0,
			OWLDataPropertyExpression arg1) {
		return arg0.getDataPropertyValues(arg1, initOntology);
	}

	public Set<Set<OWLClass>> getTypes(OWLIndividual arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasDataPropertyRelationship(OWLIndividual arg0,
			OWLDataPropertyExpression arg1, OWLLiteral arg2) {
		return arg0.hasDataPropertyValue(arg1, arg2, initOntology);
	}

	public boolean hasObjectPropertyRelationship(OWLIndividual arg0,
			OWLObjectPropertyExpression arg1, OWLIndividual arg2) {
		return arg0.hasObjectPropertyValue(arg1, arg2, initOntology);
	}

	public boolean hasType(OWLIndividual arg0, OWLClassExpression arg1,
			boolean arg2) {
		// TODO arg2 ???
		return arg0.getTypes(initOntology).contains(arg1);
	}

	public Set<Set<OWLObjectProperty>> getAncestorProperties(
			OWLObjectProperty arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Set<OWLDataProperty>> getAncestorProperties(OWLDataProperty arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Set<OWLObjectProperty>> getDescendantProperties(
			OWLObjectProperty arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Set<OWLDataProperty>> getDescendantProperties(
			OWLDataProperty arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Set<OWLClassExpression>> getDomains(OWLObjectProperty arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Set<OWLClassExpression>> getDomains(OWLDataProperty arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<OWLObjectProperty> getEquivalentProperties(OWLObjectProperty arg0) {
		Set<OWLObjectProperty> set = new HashSet<OWLObjectProperty>();

		for (OWLObjectPropertyExpression propertyExpression : arg0
				.getEquivalentProperties(initOntology))
			set.add(propertyExpression.asOWLObjectProperty());

		return set;
	}

	public Set<OWLDataProperty> getEquivalentProperties(OWLDataProperty arg0) {
		Set<OWLDataProperty> set = new HashSet<OWLDataProperty>();

		for (OWLDataPropertyExpression propertyExpression : arg0
				.getEquivalentProperties(initOntology))
			set.add(propertyExpression.asOWLDataProperty());

		return set;
	}

	public Set<Set<OWLObjectProperty>> getInverseProperties(
			OWLObjectProperty arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<OWLClassExpression> getRanges(OWLObjectProperty arg0) {
		return arg0.getRanges(initOntology);
	}

	public Set<OWLDataRange> getRanges(OWLDataProperty arg0) {
		return arg0.getRanges(initOntology);
	}

	public Set<Set<OWLObjectProperty>> getSubProperties(OWLObjectProperty arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Set<OWLDataProperty>> getSubProperties(OWLDataProperty arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Set<OWLObjectProperty>> getSuperProperties(OWLObjectProperty arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Set<OWLDataProperty>> getSuperProperties(OWLDataProperty arg0) {

		arg0.getSuperProperties(initOntology);
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isAntiSymmetric(OWLObjectProperty arg0) {
		return arg0.isAsymmetric(initOntology);
	}

	public boolean isFunctional(OWLObjectProperty arg0) {
		return arg0.isFunctional(initOntology);
	}

	public boolean isFunctional(OWLDataProperty arg0) {
		return arg0.isFunctional(initOntology);
	}

	public boolean isInverseFunctional(OWLObjectProperty arg0) {
		return arg0.isInverseFunctional(initOntology);
	}

	public boolean isIrreflexive(OWLObjectProperty arg0) {
		return arg0.isIrreflexive(initOntology);
	}

	public boolean isReflexive(OWLObjectProperty arg0) {
		return arg0.isReflexive(initOntology);
	}

	public boolean isSymmetric(OWLObjectProperty arg0) {
		return arg0.isSymmetric(initOntology);
	}

	public boolean isTransitive(OWLObjectProperty arg0) {
		return arg0.isTransitive(initOntology);
	}

	public NodeSet<OWLNamedIndividual> getDifferentIndividuals(
			OWLNamedIndividual ind) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	public Node<OWLNamedIndividual> getSameIndividuals(OWLNamedIndividual ind)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<OWLLiteral> getDataPropertyValues(OWLNamedIndividual ind,
			OWLDataProperty pe) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeSet<OWLNamedIndividual> getObjectPropertyValues(
			OWLNamedIndividual ind, OWLObjectPropertyExpression pe)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeSet<OWLNamedIndividual> getInstances(OWLClassExpression ce,
			boolean direct) throws InconsistentOntologyException,
			ClassExpressionNotInProfileException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;

	}

	public NodeSet<OWLClass> getTypes(OWLNamedIndividual ind, boolean direct)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;

	}

	public NodeSet<OWLClass> getDataPropertyDomains(OWLDataProperty pe,
			boolean direct) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		// TODO Auto-generated method stub
		return null;

	}

	public NodeSet<OWLDataProperty> getDisjointDataProperties(
			OWLDataPropertyExpression pe) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		// TODO Auto-generated method stub
		return null;

	}

	public Node<OWLDataProperty> getEquivalentDataProperties(OWLDataProperty pe)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;

	}

	public NodeSet<OWLDataProperty> getSuperDataProperties(OWLDataProperty pe,
			boolean direct) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		// TODO Auto-generated method stub
		return null;

	}

	public void flush() {
		// TODO Auto-generated method stub

	}

	public Node<OWLClass> getBottomClassNode() {
		// TODO Auto-generated method stub
		return null;
	}

	public Node<OWLDataProperty> getBottomDataPropertyNode() {
		// TODO Auto-generated method stub
		return null;
	}

	public Node<OWLObjectPropertyExpression> getBottomObjectPropertyNode() {
		// TODO Auto-generated method stub
		return null;
	}

	public BufferingMode getBufferingMode() {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeSet<OWLClass> getDisjointClasses(OWLClassExpression arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeSet<OWLObjectPropertyExpression> getDisjointObjectProperties(
			OWLObjectPropertyExpression arg0)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	public Node<OWLObjectPropertyExpression> getEquivalentObjectProperties(
			OWLObjectPropertyExpression arg0)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	public Node<OWLObjectPropertyExpression> getInverseObjectProperties(
			OWLObjectPropertyExpression arg0)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeSet<OWLClass> getObjectPropertyDomains(
			OWLObjectPropertyExpression arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeSet<OWLClass> getObjectPropertyRanges(
			OWLObjectPropertyExpression arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<OWLAxiom> getPendingAxiomAdditions() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<OWLAxiom> getPendingAxiomRemovals() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<OWLOntologyChange> getPendingChanges() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getReasonerName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Version getReasonerVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLOntology getRootOntology() {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeSet<OWLClass> getSubClasses(OWLClassExpression arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeSet<OWLDataProperty> getSubDataProperties(OWLDataProperty arg0,
			boolean arg1) throws InconsistentOntologyException,
			FreshEntitiesException, ReasonerInterruptedException,
			TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeSet<OWLObjectPropertyExpression> getSubObjectProperties(
			OWLObjectPropertyExpression arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeSet<OWLClass> getSuperClasses(OWLClassExpression arg0,
			boolean arg1) throws InconsistentOntologyException,
			ClassExpressionNotInProfileException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	public NodeSet<OWLObjectPropertyExpression> getSuperObjectProperties(
			OWLObjectPropertyExpression arg0, boolean arg1)
			throws InconsistentOntologyException, FreshEntitiesException,
			ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	public Node<OWLClass> getTopClassNode() {
		// TODO Auto-generated method stub
		return null;
	}

	public Node<OWLDataProperty> getTopDataPropertyNode() {
		// TODO Auto-generated method stub
		return null;
	}

	public Node<OWLObjectPropertyExpression> getTopObjectPropertyNode() {
		// TODO Auto-generated method stub
		return null;
	}

	public Node<OWLClass> getUnsatisfiableClasses()
			throws ReasonerInterruptedException, TimeOutException {
		// TODO Auto-generated method stub
		return null;
	}

	public void interrupt() {
		// TODO Auto-generated method stub

	}

	public boolean isEntailed(Set<? extends OWLAxiom> arg0)
			throws ReasonerInterruptedException,
			UnsupportedEntailmentTypeException, TimeOutException,
			AxiomNotInProfileException, FreshEntitiesException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isEntailmentCheckingSupported(AxiomType<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public void prepareReasoner() throws ReasonerInterruptedException,
			TimeOutException {
		// TODO Auto-generated method stub

	}

	public Set<InferenceType> getPrecomputableInferenceTypes() {
		return null;
	}

	public boolean isPrecomputed(InferenceType inferenceType) {
		return false;
	}

	public void precomputeInferences(InferenceType... inferenceTypes)
			throws ReasonerInterruptedException, TimeOutException,
			InconsistentOntologyException {
	}

}
