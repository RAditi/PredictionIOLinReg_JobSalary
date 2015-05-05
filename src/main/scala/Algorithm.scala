package org.template.vanilla

import io.prediction.controller.P2LAlgorithm
import io.prediction.controller.Params


import org.apache.spark.mllib.regression.LinearRegressionWithSGD
import org.apache.spark.mllib.regression.LinearRegressionModel
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._

import org.apache.spark.SparkContext
import grizzled.slf4j.Logger
import org.apache.spark.mllib.regression.LabeledPoint
import java.io._


case class AlgorithmParams(
//Whether the model should train with an intercept
  val intercept : Double
) extends Params


// extends P2LAlgorithm if Model contains RDD[]

class algo(val ap: AlgorithmParams)
  extends P2LAlgorithm[PreparedData, LinearRegressionModel, Query, PredictedResult] {

  @transient lazy val logger = Logger[this.type]

  def train(sc:SparkContext, data: PreparedData): LinearRegressionModel = {
    // MLLib Linear Regression cannot handle empty training data.
    require(!data.training_points.take(1).isEmpty,
      s"RDD[labeldPoints] in PreparedData cannot be empty." +
      " Please check if DataSource generates TrainingData" +
      " and Preprator generates PreparedData correctly.")
    val lin = new LinearRegressionWithSGD() 

    


    //It is set to True only in the intercept field is set to 1
    //Right now, I am inputting this parameter as an integer, could be changed to String or Bool as necessary
    
    lin.setIntercept(ap.intercept.equals(1.0))	
    


    val model = lin.run(data.training_points)

    val valuesAndPreds = data.training_points.map { point =>
     val prediction = model.predict(point.features)
     (point.label, prediction)
     }
     val MSE = valuesAndPreds.map{case(v, p) => math.abs(math.exp(v) - math.exp(p))/math.exp(v)}.mean()
     println("Training Error")
     println(MSE)

return model


  }
  
  
  //Predict takes in a feature that is an array of Strings. We have to convert it to the required double values for the LinearRegressionModel
  

  def get_new_features(unique_values : Array[String], old_value : String) : Array[Double] = {
  val newFeatures = new Array[Double](unique_values.length)
 
  for(j <-0 until unique_values.length){
  	newFeatures(j) = 0;
	}
//Setting the required value to 1
  newFeatures(unique_values.indexOf(old_value)) = 1	
  return newFeatures
  }



  def predict(model: LinearRegressionModel, query: Query): PredictedResult = {

  //Reading unique values from file
    val streamIn = new FileInputStream("4")
    val objectinputstream = new ObjectInputStream(streamIn);
    val obj = objectinputstream.readObject();
    val unique4 : Array[String] =  obj.asInstanceOf[Array[String]];
    
  //Reading unique values from file  
    val streamIn2 = new FileInputStream("5")
    val objectinputstream2 = new ObjectInputStream(streamIn2);
    val unique5 : Array[String] = objectinputstream2.readObject().asInstanceOf[Array[String]];
//Reading unique values from file
    val streamIn3 = new FileInputStream("6")
    val objectinputstream3 = new ObjectInputStream(streamIn3);
    val unique6 :Array[String]= objectinputstream3.readObject().asInstanceOf[Array[String]];
  //Concatenating the features
  
    val test_feature:Array[Double] = Array.concat(get_new_features(unique4, query.features(4)), get_new_features(unique6, query.features(6)),get_new_features(unique5, query.features(5)))
      
      val result = model.predict(Vectors.dense(test_feature))
    
    new PredictedResult(math.exp(result))

  }

}
