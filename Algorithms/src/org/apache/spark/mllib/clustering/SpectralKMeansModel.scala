package org.apache.spark.mllib.clustering

import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.rdd.RDD


class SpectralKMeansModel(override val clusterCenters: Array[Vector],
                          val pointProj2:RDD[(Long,(Vector, Vector))],
                          val nParts:Int = 4)
                            extends KMeansModel(clusterCenters) with Serializable {

  override def predict(point:Vector): Int = {
    val pointProj=pointProj2.map(_._2)
    val point_to_find = pointProj.lookup(point)
    if (point_to_find.isEmpty) {
      //println("point to query")
     //println(point.toArray.mkString(","))
     //System.exit(1)
      throw new IllegalArgumentException("Input data point not exist in original data set")
    }
    else KMeans.findClosest(clusterCentersWithNorm, new VectorWithNorm(point_to_find.head))._1
  }

  def predictall(): RDD[(Vector, Int,Long)] = {
    //println(clusterCenters.mkString("\n"))
    //System.exit(1)
    //val pointProj=pointProj2.map(_._2)
    pointProj2.map{ case (id,(vector, proj)) =>
      val res = KMeans.findClosest(clusterCentersWithNorm, new VectorWithNorm(proj))
      (vector, res._1,id)
    }
  }


  private def clusterCentersWithNorm: Iterable[VectorWithNorm] =
    clusterCenters.map(new VectorWithNorm(_))
}