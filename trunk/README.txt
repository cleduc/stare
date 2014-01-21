A Star-type based Reasoner (STARE)
==================================

STARE is an OWL reasoner, i.e., it offers main inference services over an OWL ontology such as consistency checking, entailment and query answering. 
The specific feature of STARE is to implement an algorithm that uses a compressed structure for representing a model of an ontology. Instead of unfolding all individuals of a model, the algorithm groups those that have the same structure, namely a star-type. A frame that is tiled from star-types represents a model of the ontology.  

To compile the whole project, run 

$ ant compileall



