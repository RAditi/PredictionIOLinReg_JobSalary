package org.template.vanilla

import io.prediction.controller.PDataSource
import io.prediction.controller.EmptyEvaluationInfo
import io.prediction.controller.EmptyActualResult
import io.prediction.controller.Params
import io.prediction.data.storage.Event
import io.prediction.data.storage.Storage

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vectors

import grizzled.slf4j.Logger

case class DataSourceParams(val appId: Int) extends Params

class DataSource(val dsp: DataSourceParams)
  extends PDataSource[TrainingData,
      EmptyEvaluationInfo, Query, EmptyActualResult] {

  @transient lazy val logger = Logger[this.type]

  override
  def readTraining(sc: SparkContext): TrainingData = {
    val eventsDb = Storage.getPEvents()

//Read all events involving "Point_Feature" type 
       println("Gathering data from the event server")
       
//training_points is an RDD[Point_Features] that is passed to Preparator class from DataSource
    val training_points: RDD[Point_Features] = eventsDb.aggregateProperties(
      
      appId = dsp.appId,
      entityType = "training_point",

      // only keep entities with these required properties defined

      required = Some(List("plan", "attr0", "attr1", "attr2", "attr3", "attr4", "attr5", "attr6", "attr7", "attr8", "attr9")))(sc)

      // aggregateProperties() returns RDD pair of
      // entity ID and its aggregated properties
      .map { case (entityId, properties) =>

        try {
	//The target salary is doubel value
	//All other features are Strings which are preprocessed later inside Preparator.scala
          Point_Features(math.log(properties.get[Double]("plan")),
            Array(
          properties.get[String]("attr0"),
          properties.get[String]("attr1"),
	  properties.get[String]("attr2"),
	  properties.get[String]("attr3"),
	  properties.get[String]("attr4"),	
	  properties.get[String]("attr5"),
	  properties.get[String]("attr6"),
          properties.get[String]("attr7"),
          properties.get[String]("attr8"),
          properties.get[String]("attr9")

	      ) )
            
        
	  
        } catch {
          case e: Exception => {
            logger.error(s"Failed to get properties ${properties} of" +
              s" ${entityId}. Exception: ${e}.")
            throw e
          }
        }
      }

    new TrainingData(training_points)
  }
}

//Point_Feature is a new class to describe the data that is collected
//Each point has a double value : which is the target value to be learnt
//The features extracted at this stage are in String format : Hence features is an array of Strings

case class Point_Features (

     value : Double, 
     features : Array[String]
)

//The data that is passed onto Preparator which is of the form RDD[Point_Feature]
class TrainingData(
  val training_points: RDD[Point_Features]
) extends Serializable
