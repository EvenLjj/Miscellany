package com.liu.bigds.clustering.hash

import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.rdd.RDD

import scala.collection.mutable.ListBuffer


class LSHModel(hyperplanes: Array[Int], thresholds: Array[Double], M : Int) extends Serializable {

  /** generate rows hash functions */
  private val _hashFunctions = ListBuffer[Hasher]()
  for (i <- 0 until M)
    _hashFunctions += Hasher.create(hyperplanes, thresholds, i)
  final val hashFunctions : List[(Hasher, Int)] = _hashFunctions.toList.zipWithIndex

  /** (vector id, cluster id) */
  var vector_cluster : RDD[(Long, Int)] = null

  /** (cluster id, List(Vector) */
  var clusters : RDD[(Int, Iterable[(Vector, Long)])] = null

}