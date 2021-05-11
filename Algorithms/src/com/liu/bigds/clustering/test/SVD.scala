package com.liu.bigds.clustering.test

import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.mllib.linalg.{Matrix, SingularValueDecomposition, Vector, Vectors}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by ljj on 16-6-13.
  */
class SVD {

}

object SVD{
  def main(args:Array[String]): Unit ={
    println("Hello,MyBegin")
    val conf=new SparkConf().setMaster("local").setAppName("SVD")
    val sc=new SparkContext(conf)
    val rdd=sc.textFile("/home/ljj/TestData/data")
    val ml=rdd.map(_.split(' ').map(_.toDouble)).map(line =>Vectors.dense(line))
    val mat:RowMatrix=new RowMatrix(ml)
    val svd:SingularValueDecomposition[RowMatrix, Matrix] = mat.computeSVD(4,computeU = true)
    val U:RowMatrix=svd.U
    val Rows:RDD[Vector]=U.rows
    val s:Vector=svd.s
    val V:Matrix=svd.V
    Rows.foreach(f=>println(f))
    println("--------------------")
    println(s)
    println("--------------------")
    println(V.toString())
  }
}
