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


import java.net.URI;
import java.util.Map;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.ArrayList;
import java.util.List;
import java.util.Enumeration;

import fr.paris8.iut.info.stare.ConceptLabel;
import fr.paris8.iut.info.stare.Ray;
import fr.paris8.iut.info.stare.RoleLabel;
import fr.paris8.iut.info.stare.Concept;

public class  StarType {
       //identity
       private int id;
       
       //Core Concept       
       ConceptLabel core;

       //set of rays 
       HashSet<Ray> rays; 

       //Creation with an id
       StarType(int id) { }

       //Creation with a core label 
       StarType(int id, ConceptLabel cb) {}

       //Creation with a ray = (edge + tip)
       StarType(int id, ConceptLabel cb, RoleLabel edge, ConceptLabel tip) {}
       
       //modify core concept
       void setCore(Concept co) {}

       //modify a ray
       void setRay(Ray ray) {}

       //add a new ray : ensure that all rays are different
       void addRay(Ray ray) {}

       //check if the startype is semantically valid  
       boolean checkValidity() {
          return false;
	}

       //Check if the startype matches another startype "rt" over a ray "r" of "st"  
       //It returns a ray of the startype that matches "r", or null
       Ray match(StarType st, Ray r){ return null;}

       //Check if the startype with a ray "r1" matches another startype "rt" with a ray r2    
       //It returns a ray of the startype that matches "r", or null
       boolean match(Ray r1, StarType st, Ray r2){
	return false;	
	}

}

