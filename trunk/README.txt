A Star-type based Reasoner (STARE)
==================================

STARE is an OWL reasoner, i.e., it offers main inference services over an OWL ontology such as consistency checking, entailment and query answering. 
The specific features of STARE are 

(1) implementating an algorithm that uses a compressed structure for representing a model of an ontology. Instead of unfolding all individuals of a model, the algorithm groups those that have the same structure, namely a star-type. A frame that is tiled from star-types represents a model of the ontology.  

(2) using of a database for storing and retrieving individuals and concepts 

(3) dealing with some features that are beyond FOL, for instance, transitive closure of relations

(4) storing a model for applications 

(5) revising an ontology

(6) allowing a human-reasoner interaction when reasoning

 
===================================

To compile the whole project, run 

$ ant compileall


Then, one can test an implemented class by add ding some code to trunk/test/src/*.java, and run

$ ant test


