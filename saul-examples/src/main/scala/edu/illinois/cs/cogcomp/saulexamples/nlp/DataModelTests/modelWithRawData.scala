package edu.illinois.cs.cogcomp.saulexamples.nlp.DataModelTests

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Sentence, TextAnnotation }
import edu.illinois.cs.cogcomp.saul.datamodel.DataModel
import edu.illinois.cs.cogcomp.saulexamples.data.{ Document, DocumentReader }
import edu.illinois.cs.cogcomp.saulexamples.nlp.sensors

import scala.collection.JavaConversions._

object modelWithRawData extends DataModel {

  val rawText = node[Document]
  val annotatedText = node[TextAnnotation]
  val sentences = node[Sentence]
  val rawToAnn = edge(rawText, annotatedText)
  val textToCon = edge(annotatedText, sentences)
  textToCon.addSensor(sensors.f(_))
  rawToAnn.addSensor(sensors.curator(_))

  // textToCon.addSensor(sensors.alignment:(TextAnnotation,Sentence)=>Boolean)
}

object myapp {

  def main(args: Array[String]) {
    import modelWithRawData._
    //call the reader
    val dat: List[Document] = new DocumentReader("./data/20newsToy/train").docs.toList.slice(1, 2)
    // val taList = dat.map(x => sensors.curator(x))
    // val sentenceList = taList.flatMap(x => x.sentences())

    //Add the reader objects to the model which contains raw text
    rawText.populate(dat)
    // annotatedText.populate(taList)
    //sentences.populate(sentenceList)

    //populate the graph with sensors
    // modelWithRawData.rawToAnn.populateWith(sensors.curator(_))
    // modelWithRawData.textToCon.populateWith(sensors.f _)

    //TODO: make the below line work, to just use the edge name and depending on the type of sensor a generator or matching edge will be called.
    //test the content of the graph
    val tests = rawText.getAllInstances
    val taa = annotatedText.getAllInstances
    val sen = sentences.getAllInstances
    println(s"tests.size = ${tests.size}")
    println(s"taa.size = ${taa.size}")
    println(s"sen.size = ${sen.size}")
    println(s"textToCon.size = ${textToCon.links.size}")
    println(s"rawToAnn.size = ${rawToAnn.links.size}")
    //The new version
    val x0 = (rawText(tests.head) ~> rawToAnn ~> textToCon).instances
    val x3 = (annotatedText(taa.head) ~> textToCon).instances
    val x4 = (sentences(sen.head) ~> -textToCon).instances
    //The old version
    val x1 = getFromRelation[Sentence, TextAnnotation](sen.head)
    val x2 = getFromRelation[TextAnnotation, Sentence](taa.head)

    println(s"x0.size = ${x0.size}")
    println(s"x1.size = ${x1.size}")
    println(s"x2.size = ${x2.size}")
    println(s"x3.size = ${x3.size}")
    println(s"x4.size = ${x4.size}")
    print("finished")

  }
}