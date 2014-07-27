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

import java.util.Collection;
import java.util.List;
import java.util.Set;

// a ray of a StarType
public class Ray {
	private int id = -1;
	private Integer ridgeId;
	private Integer coreId;
	private Integer tipId;
	//Used for startype
	private int counter = 0;
	public Ray() {
		this.ridgeId = null; 
		this.coreId = null;
		this.tipId  =  null;
	}

	//Create a ray when we don't know ids. This costs exp
	public Ray( RoleLabel rl, ConceptLabel cl, ConceptLabel tl, ReasonerData data) {
		//for identifying 
		data.addRidge(rl);
		data.addCore(cl);
		data.addCore(tl);
		this.ridgeId = data.giveRidgeIdentifier(rl).getIdentifier();
		this.coreId = data.giveCoreIdentifier(cl).getIdentifier();
		this.tipId  = data.giveCoreIdentifier(tl).getIdentifier();
		data.addRay(this);
	}

	//Create a ray when we have only a simple role for ridge and a simple concept for tip 
	public Ray( Role srl, ConceptLabel cl, Concept tl, ReasonerData data) {
		RoleLabel rl = new RoleLabel();
		rl = rl.getNewRoleLabel(srl.getIdentifier(), data);
		ConceptLabel conceptl = new ConceptLabel();
		conceptl = conceptl.getNewConceptLabel(tl.getIdentifier(), data);
		//data.addCore(cl);
		this.ridgeId = data.giveRidgeIdentifier(rl).getIdentifier();
		this.coreId = data.giveCoreIdentifier(cl).getIdentifier();
		this.tipId  = data.giveCoreIdentifier(conceptl).getIdentifier();
		data.addRay(this);
	}

	//Create a ray when we know ids
	public Ray( Integer rl, Integer cl, Integer tl, ReasonerData data) {
		this.ridgeId = rl;
		this.coreId = cl;
		this.tipId = tl;
		data.addRay(this);
	}
	/*
	public boolean matches(Role role, Concept c, Concept cr) {
		if (this.rl.contains(role))
			if (this.cl.contains(c))
			    if(this.crl.contains(cr))
				return true;
		return false;
	}
	*/
	//return a new ray if r is not contained in ridge 
	public Ray getNewRayByRole(Integer r, ReasonerData data) {
	       if( ! data.getRidges().containsKey(r) ) {
		    RoleLabel rl =  data.getRidges().get(this.getRidgeId()).getNewRoleLabel(r, data);
                    Ray ray = new Ray(rl, data.getCores().get(this.getCoreId()), data.getCores().get(this.getTipId()), data);
		    data.addRay(ray);
		    return ray;
                } else
		return this;
	}

	public Ray getNewRayByRole(Set<Integer> roleList, ReasonerData data) {
	       RoleLabel rl = data.getRidges().get(ridgeId); 
	       rl = rl.getNewRoleLabel(roleList, data);
	       Ray ray = new Ray(new  Integer(rl.getIdentifier()), coreId, tipId, data );
	       data.addRay(ray); 
	       return ray;
	}

	//return a new ray if c is not contained in core 
	public Ray getNewRayByCore(Integer c, ReasonerData data) {
	       if( ! data.getCores().containsKey(c)  ) {
		    ConceptLabel cl =  data.getCores().get(this.getCoreId()).getNewConceptLabel(c, data);
                    Ray ray = new Ray(data.getRidges().get(this.getRidgeId()), cl, data.getCores().get(this.getTipId()), data);
		    data.addRay(ray);
		    return ray;
                } else
		return this;
	}

	public Ray getNewRayByCore(Set<Integer> conceptList, ReasonerData data) {
	       ConceptLabel cl = data.getCores().get(coreId); 
	       cl = cl.getNewConceptLabel(conceptList, data);
	       Ray ray = new Ray(ridgeId, new Integer(cl.getIdentifier()), tipId, data );
	       data.addRay(ray); 
	       return ray;
	}

	//return a new ray if c is not contained in tip 
	public Ray getNewRayByTip(Integer c, ReasonerData data) {
	       if( ! data.getCores().containsKey(c)   ) {
		    ConceptLabel cl =  data.getCores().get(this.getTipId()).getNewConceptLabel(c, data);
                    Ray ray = new Ray(data.getRidges().get(this.getRidgeId()), data.getCores().get(this.getCoreId()), cl, data);
		    data.addRay(ray);
		    return ray;
                } else
		return this;
	}

	public Ray getNewRayByTip(Set<Integer> conceptList, ReasonerData data) {
	       ConceptLabel cl = data.getCores().get(this.getTipId());
	       cl = cl.getNewConceptLabel(conceptList, data);
	       Ray ray = new Ray(ridgeId, coreId, new Integer(cl.getIdentifier()), data );
	       data.addRay(ray); 
	       return ray;
	}

	public void addNNFToTip(ReasonerData data) {
		data.getCores().get(this.getTipId().intValue()).addAll(data.getAxiomNNFs());      
	}

	public void addNNFToCore(ReasonerData data) {
		data.getCores().get(this.getCoreId().intValue()).addAll(data.getAxiomNNFs());  
	}

	public Integer getRidgeId() {
		return ridgeId;
	}

	public int getCounter() {
		return counter;
	}

	public void incCounter() {
		++counter;
	}
	public void decCounter() {
		--counter;
	}	
	public void setCounter(int c) {
		counter = c;
	}

	public Integer getTipId() {
		return tipId;
	}

	public Integer getCoreId() {
		return coreId;
	}
	public int getIdentifier() {
		return id;
	}
	public void setIdentifier(int id) {
		this.id = id;
	}

	public Ray fusion(Integer ray2, ReasonerData data) {
		Ray rl= null;
		for(Integer i : data.getRidges().get(ray2) ){
		    rl = getNewRayByRole(i, data);
		}
		for(Integer i : data.getCores().get(ray2) ){
		    rl = getNewRayByCore(i, data);
		}
		for(Integer i : data.getCores().get(ray2) ){
		    rl = getNewRayByTip(i, data);
		}
		return rl;
	}
	 
	public boolean isInverseOf(Ray ray, ReasonerData data) {
		 //if "ray" is not identified, it is now
		data.addRay(ray);
		if ( ! this.coreId.equals( ray.getTipId()) )
			return false;

		if ( ! this.tipId.equals( ray.getCoreId()) )
			return false;

		if ( ! data.getRidges().get(ridgeId).isInverseOf(data.getRidges().get(ray.getIdentifier()), data)  )
			return false;
		else
			return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null )
			return false;

		if (getClass() != obj.getClass())
			return false;

		Ray other = (Ray) obj;

		//if (this.size() != other.size())
		//	return false;

		if ( ! this.ridgeId.equals(other.getRidgeId()) )
			return false;

		if ( ! this.coreId.equals(other.getCoreId()) )
			return false;

		if ( ! this.tipId.equals(other.getTipId()) )
			return false;

		return true;
	}

	//@Override
	public String toString(ReasonerData data) {
		StringBuilder sb = new StringBuilder();

		sb.append("Ray " + id + System.getProperty("line.separator"));
		sb.append("Core " +  System.getProperty("line.separator"));
		sb.append( data.getCores().get( coreId.intValue() ).toString(data) );
		sb.append("Ridge:" + System.getProperty("line.separator"));
		sb.append( data.getRidges().get( ridgeId.intValue() ).toString(data) );
		sb.append(System.getProperty("line.separator"));
		sb.append("Tip:" + System.getProperty("line.separator"));
		sb.append(data.getCores().get( tipId.intValue() ).toString(data));

		return sb.toString();
	}
}
