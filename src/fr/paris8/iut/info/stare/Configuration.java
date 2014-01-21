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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

 
import org.semanticweb.owlapi.reasoner.FreshEntityPolicy;
import org.semanticweb.owlapi.reasoner.IndividualNodeSetPolicy;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;

public class Configuration implements Serializable,Cloneable,OWLReasonerConfiguration {
    private static final long serialVersionUID=7741510316249774519L;

    public IndividualNodeSetPolicy individualNodeSetPolicy;
    public FreshEntityPolicy freshEntityPolicy;
    public long individualTaskTimeout;
    public ReasonerProgressMonitor reasonerProgressMonitor;

    public static enum DirectBlockingType {
         
        PAIR_WISE,
         
        OPTIMAL
    }

    public IndividualNodeSetPolicy getIndividualNodeSetPolicy() {
		return individualNodeSetPolicy;
	}

    public FreshEntityPolicy getFreshEntityPolicy() {
		return freshEntityPolicy;
	}

    public long getTimeOut() {
        return individualTaskTimeout;
    }
    public ReasonerProgressMonitor getProgressMonitor() {
		return reasonerProgressMonitor;
	}

}
