package org.template.vanilla

import io.prediction.controller.PPreparator

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.Vector
import java.io._

//PreparedDate is the class that is sent to the Algorithm class
//In the case of Linear Regression algorithm in MLLib, the expected format is RDD[LabeledPoint]

class PreparedData(
    val training_points: RDD[LabeledPoint]
) extends Serializable

    
    
//Preparator class has functionality that prepares the data in the required format : preprocessing

class Preparator extends PPreparator[TrainingData, PreparedData] {


//Function that assigns a numerical value to the categorical attribute indexed into training points by the parameter index

  def get_categorical (TrainingPoints : Array[Point_Features], index :Int):Array[Double] = {
      val values : Array[String] = new Array[String](TrainingPoints.length)
    //Getting all the items
     for(i <- 0 until TrainingPoints.length){
     values(i) = TrainingPoints(i).features(index);     

     }

     //Getting distinct values from them
     val  unique_values = values.distinct
     // newFeatures : the feature that has numeric values   
     val newFeatures:Array[Double] = new Array[Double](TrainingPoints.length)
     //Obtaining the features : the index of the value in the set of distinct items
      for(i <-0 until TrainingPoints.length){
      newFeatures(i) = unique_values.indexOf(TrainingPoints(i).features(index))
      }
      return newFeatures

  }



  //Function that assigns a one hot vector for a categorical variable. Linear Regression learns better when the categorical features are represented as one-hot coding
  //The number of features = number of distinct values
  //The vector correspoding the value of the variable is set to 1, all others are set to 0
  //This encoding takes care of the situation where there is a relative ordering between the categorical features
  // Parameter index : the attribute to be modified
  
  def get_binarise (TrainingPoints :Array[Point_Features], index : Int) : Array[Array[Double]] = {
  val values : Array[String] = new Array[String](TrainingPoints.length)
   
  //Getting all the features of the items
  
  for(i <- 0 until TrainingPoints.length){
   values(i) = TrainingPoints(i).features(index);     

   }

  //Getting distinct values from among them
  val  unique_values = values.distinct
  //Writing to a file

  
  val fout = new FileOutputStream(index.toString);
  val oos = new ObjectOutputStream(fout);
  oos.writeObject(unique_values);


  //Creating new features of length the number of unique items
  val newFeatures = Array.ofDim[Double](TrainingPoints.length, unique_values.length)
    //Initialising all of them to 0
     for(i <- 0 until TrainingPoints.length){
    	   for(j <-0 until unique_values.length){
	   newFeatures(i)(j) = 0;
	   }
   }

  //Setting appropriate one hot encoding to 1

     for(i <-0 until TrainingPoints.length){
     newFeatures(i)(unique_values.indexOf(TrainingPoints(i).features(index))) = 1

    }
  
  return newFeatures

}


// Function that converts a list of the training points explanatory variables and the target variables into RDD[LabeledPoint] that the Preparator class expects
//xValue : The features : Each point is expressed as an array of double values
// All the points collected together in a 2D array format : xValue
def toList(xValue:Array[Array[Double]],yValue:Array[Double]):
 Array[LabeledPoint] ={
     xValue.zipWithIndex.map{
       case (row,rowIndex) => {
         val features = row.map(value => value.toDouble)
         val label = yValue(rowIndex).toDouble
         LabeledPoint(label,Vectors.dense(features))
       }
     }
   }



//Function that actually prepares the data, from trainingData that it receives from DataSource class

def prepare(sc: SparkContext, trainingData: TrainingData) : PreparedData =  {
 
  //Train array contains the training points in an array format : Array[Point_Features]
  val train_array = trainingData.training_points.collect
  //NewFeature1 is the binarised version of feature 4 : location
  val newFeature1 = get_binarise(train_array, 4);	
  //NewFeature2 is the binarised version of feature 6 : the type of contract
  val newFeature2 = get_binarise(train_array, 6);
  //NewFeature3 is the binarised version of feature 5 : the source of data
  val newFeature3 = get_binarise(train_array, 5);     
  //Creating a new training data here
  val train_y :Array[Double]  = new Array[Double](train_array.length)
     //Setting the value of train_y
    for(i <- 0 until train_array.length){
     	train_y(i) = train_array(i).value
  }
     	      
   // Obtaining the sizes of the newly created features 
    val size1 = newFeature1(0).length
    val size2 = newFeature2(0).length
    val size3 = newFeature3(0).length
    //Summing the individual values
    val numFeatureNew = size1 + size2 + size3
  
    //Creating the training data	
    val train_x = Array.ofDim[Double](train_array.length,numFeatureNew)
    
    //Copying the appropriate feature values
    for(i <- 0 until train_array.length){
   	  for(j <-0 until size1)
	  train_x(i)(j) = newFeature1(i)(j)
   }

    //Adding next feature 

    for(i <- 0 until train_array.length){
    	  for(j <-0 until size2)
	  train_x(i)(size1 + j) = newFeature2(i)(j)
    }

    //Adding next feature
   for(i <- 0 until train_array.length){
    	  for(j <-0 until size3)
	  train_x(i)(size1 + size2 +  j) = newFeature3(i)(j)
    }



    val trainList = toList(train_x, train_y)
    val trainRDD = sc.makeRDD(trainList)
    val return_trainRDD = new PreparedData(trainRDD)
    //Returning the prepared RDD[LabeledPoint]
   return return_trainRDD
     
  }








}
