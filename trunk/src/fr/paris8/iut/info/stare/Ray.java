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
		this.ridgeId = new Integer(rl.getIdentifier());
		this.coreId = new Integer(cl.getIdentifier());
		this.tipId  = new Integer(tl.getIdentifier());
	}

	//Create a ray when we have only a simple role for ridge and a simple concept for tip 
	public Ray( Role srl, ConceptLabel cl, Concept tl, ReasonerData data) {
		RoleLabel rl = new RoleLabel();
		rl = rl.getNewRoleLabel(srl.getIdentifier(), data);
		ConceptLabel conceptl = new ConceptLabel();
		conceptl = conceptl.getNewConceptLabel(tl.getIdentifier(), data);
		this.ridgeId = new Integer(rl.getIdentifier());
		this.coreId = new Integer(cl.getIdentifier());
		this.tipId  = new Integer(conceptl.getIdentifier());
	}

	//Create a ray when we know ids
	public Ray( Integer rl, Integer cl, Integer tl, ReasonerData data) {
		this.ridgeId = new Integer(rl);
		this.coreId = new Integer(cl);
		this.tipId = new Integer(tl);
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
		    ray = data.addRay(ray);
		    return ray;
                } else
		return this;
	}

	public Ray getNewRayByRole(Set<Integer> roleList, ReasonerData data) {
	       RoleLabel rl = data.getRidges().get(ridgeId); 
	       rl = rl.getNewRoleLabel(roleList, data);
	       Ray ray = new Ray(new  Integer(rl.getIdentifier()), coreId, tipId, data );
	       ray = data.addRay(ray); 
	       return ray;
	}

	//return a new ray if c is not contained in core 
	public Ray getNewRayByCore(Integer c, ReasonerData data) {
	       if( ! data.getCores().containsKey(c)  ) {
		    ConceptLabel cl =  data.getCores().get(this.getCoreId()).getNewConceptLabel(c, data);
                    Ray ray = new Ray(data.getRidges().get(this.getRidgeId()), cl, data.getCores().get(this.getTipId()), data);
		    ray = data.addRay(ray);
		    return ray;
                } else
		return this;
	}

	public Ray getNewRayByCore(Set<Integer> conceptList, ReasonerData data) {
	       ConceptLabel cl = data.getCores().get(coreId); 
	       cl = cl.getNewConceptLabel(conceptList, data);
	       Ray ray = new Ray(ridgeId, new Integer(cl.getIdentifier()), tipId, data );
	       ray = data.addRay(ray); 
	       return ray;
	}

	//return a new ray if c is not contained in tip 
	public Ray getNewRayByTip(Integer c, ReasonerData data) {
	       if( ! data.getCores().containsKey(c)   ) {
		    ConceptLabel cl =  data.getCores().get(this.getTipId()).getNewConceptLabel(c, data);
                    Ray ray = new Ray(data.getRidges().get(this.getRidgeId()), data.getCores().get(this.getCoreId()), cl, data);
		    ray = data.addRay(ray);
		    return ray;
                } else
		return this;
	}

	public Ray getNewRayByTip(Set<Integer> conceptList, ReasonerData data) {
	       ConceptLabel cl = data.getCores().get(this.getTipId());
	       cl = cl.getNewConceptLabel(conceptList, data);
	       Ray ray = new Ray(ridgeId, coreId, new Integer(cl.getIdentifier()), data );
	       ray = data.addRay(ray); 
	       return ray;
	}

	public boolean tipContains(Integer concept, ReasonerData data){
		if (data.getCores().get(tipId).contains(concept))
		   return true;
		else return false;
	}

	public void addNNFToTip(ReasonerData data) {
		data.getCores().get(this.getTipId().intValue()).getConceptIds().addAll(data.getAxiomNNFs());      
	}

	public void addNNFToCore(ReasonerData data) {
		data.getCores().get(this.getCoreId().intValue()).getConceptIds().addAll(data.getAxiomNNFs());  
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
		for(Integer i : data.getRidges().get(ray2).getRoleIds() ){
		    rl = getNewRayByRole(i, data);
		}
		for(Integer i : data.getCores().get(ray2).getConceptIds() ){
		    rl = getNewRayByCore(i, data);
		}
		for(Integer i : data.getCores().get(ray2).getConceptIds() ){
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
		if (this.getIdentifier() >= 0 && other.getIdentifier() >= 0) {
			if (this.getIdentifier() == other.getIdentifier() )
			   return true;
			else 
			   return false;
		}
		if ( ! this.coreId.equals(other.getCoreId()) )
			return false;
		//System.out.println("ridge other ="+other.getRidgeId());
		//System.out.println("ridge this ="+this.getRidgeId());
		//System.out.println("ridge= "+data.getRidges().get(this.ridgeId));
		if ( ! this.ridgeId.equals(other.getRidgeId()) )
			return false;
		if ( ! this.tipId.equals(other.getTipId() ) )
			return false;
		return true;
	}

	//@Override
	public String toString(ReasonerData data) {
		StringBuilder sb = new StringBuilder();

		sb.append("Ray " + id + System.getProperty("line.separator"));
		sb.append("Core " +  System.getProperty("line.separator"));
		sb.append( data.getCores().get( coreId).toString(data) );
		sb.append("Ridge:" + System.getProperty("line.separator"));
		sb.append( data.getRidges().get( ridgeId ).toString(data) );
		sb.append(System.getProperty("line.separator"));
		sb.append("Tip:" + System.getProperty("line.separator"));
		sb.append("Id= "+tipId +", "+ data.getCores().get( tipId ).toString(data));

		return sb.toString();
	}
}
