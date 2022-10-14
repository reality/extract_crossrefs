@Grapes([
    @Grab(group='org.semanticweb.elk', module='elk-owlapi', version='0.4.3'),
    @Grab(group='net.sourceforge.owlapi', module='owlapi-api', version='5.1.14'),
    @Grab(group='net.sourceforge.owlapi', module='owlapi-apibinding', version='5.1.14'),
    @Grab(group='net.sourceforge.owlapi', module='owlapi-impl', version='5.1.14'),
    @Grab(group='net.sourceforge.owlapi', module='owlapi-parsers', version='5.1.14'),
    @Grab(group='net.sourceforge.owlapi', module='owlapi-distribution', version='5.1.14'),
    @GrabConfig(systemClassLoader=true)
])

import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.parameters.*
import org.semanticweb.elk.owlapi.*
import org.semanticweb.elk.reasoner.config.*
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.reasoner.*
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary
import org.semanticweb.owlapi.model.*
import org.semanticweb.owlapi.io.*
import org.semanticweb.owlapi.owllink.*
import org.semanticweb.owlapi.util.*
import org.semanticweb.owlapi.search.*
import org.semanticweb.owlapi.manchestersyntax.renderer.*
import org.semanticweb.owlapi.reasoner.structural.*

def ontPath = args[0]
def outPath = args[1]

def manager = OWLManager.createOWLOntologyManager()
def fac = manager.getOWLDataFactory()
def ont = manager.loadOntologyFromOntologyDocument(new File(ontPath))

def mappings = [:]


ont.getClassesInSignature(false).each { cl ->
  def iri = cl.getIRI().toString()
	EntitySearcher.getAnnotations(cl, ont).each { anno ->
		def property = anno.getProperty()
			OWLAnnotationValue val = anno.getValue()
			if(val instanceof OWLLiteral) {
				def literal = val.getLiteral()
				if(property.toString() =~ 'hasDbXref') {
          mappings[iri] = literal
        }
			}
	}
}

new File(outPath).text = mappings.collect { k, v -> "$k\t$v" }.join('\n')
