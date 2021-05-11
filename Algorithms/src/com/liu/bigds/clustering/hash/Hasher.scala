package com.liu.bigds.clustering.hash

import org.apache.spark.mllib.linalg.Vector


/**
 * Hashing function based on random projection.
 */
class Hasher(hyperplane: Int, threshold: Double) extends Serializable {

  override def toString(): String = "(hyperplane: " + hyperplane + ", threshold: " + threshold + ")"

  def RandomProj(v: Vector) : Int = {
    if (v.toArray(hyperplane) >= threshold) 1 else 0
  }
  
}

object Hasher {
   /** create a new instance providing hyperplanes and thresholds.*/
   def create(hyperplanes: Array[Int], thresholds: Array[Double], index: Int) = new Hasher(hyperplanes(index), thresholds(index))

}
