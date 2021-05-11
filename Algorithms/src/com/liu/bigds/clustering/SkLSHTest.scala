
package com.liu.bigds.clustering

import org.apache.spark.mllib.clustering._
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.mllib.clustering.KMeans.{K_MEANS_PARALLEL, RANDOM}
import org.apache.spark.rdd.RDD

/**
  * 基于LSH的谱聚类测试.
  *
  * Parameters: spark的地址, HDFS的地址, 分区数, sigma, 聚类中心数, 稀疏度
  */
object SkLSHTest extends Serializable {
  def run(args: Array[String]): (Array[(Int, Vector)],Array[(Int, Int)]) = {
    //println("Spectral KMeans method on Synthetic data")
    if (args.length != 6) {
      System.err.println("ERROR:Spectral Clustering: <spark master> <path to data> <nParts> <sigma> <number of clusters> <sub partial>")
    }
    println("===========================" + args.mkString(",") + "===============================")
    val conf = new SparkConf()
      .setMaster(args(0))
      .setAppName("Spectral Clustering with LSH(RP)")
      //.setJars(List("hdfs://node10:9000/program/MySpectralClusteringAlgorithms.jar"))
      //.setJars(List("/home/ljj/IdeaProjects/MySpectralClusteringAlgorithms/out/artifacts/targetJar/MySpectralClusteringAlgorithms.jar"))
    @transient val sc = new SparkContext(conf)

    val data_address = args(1)
    val nParts = args(2).toInt
    val sigma = args(3).toDouble
    val numcluster = args(4).toInt
    val subpartial = args(5).toDouble
    val parsed = sc.textFile(data_address, nParts).map(_.split(" ").map(_.toDouble)).map(Vectors.dense(_)).distinct.repartition(64).cache()
    val numDim = parsed.count
    val numfeatures = parsed.first.size

    val start = System.currentTimeMillis / 1000

    val model = SkLSH.train(parsed, numcluster, numDim.toInt, sigma, subpartial, 100, 1, K_MEANS_PARALLEL, 29)
    val predictions = model.predictall()

    val end = System.currentTimeMillis / 1000

    val meta_res = predictions.map(i => (i._2,i._1) ).groupByKey


    val C = meta_res.count().toInt
    val N = parsed.count

    //compute centers in original data
    val centers = meta_res.map(i => {
      val NN = i._2.size
      (i._1, i._2.foldLeft[Array[Double]](new Array[Double](numfeatures))((U, V) => V.toArray.zip(U).map(i => i._1+i._2)).map(i => i/NN))
    }).collect.map(i => (i._1, Vectors.dense(i._2))).toMap

    val center_num = meta_res.map(i => (i._1, i._2.size)).collect()


    val centers_br = sc.broadcast(centers)
    val meta_dis = meta_res.map{ i =>
      val centers_node = centers_br.value(i._1)
      (i._1, i._2.map(j => Vectors.sqdist(j, centers_node)))
    }

    val ASE_res = meta_dis.map(i => math.pow(i._2.sum,2)).sum / N
    val WSSSE = meta_dis.map(i => i._2.sum).sum

    val cluster_avg = meta_dis.map(i => (i._1, i._2.sum / i._2.size)).collect

    val meta_DBI = for (i <- 0 until C) yield {
      var max_meta = 0.0
      for (j <- 0 until C if j!=i) {
        val m = cluster_avg(i)
        val n = cluster_avg(j)
        val meta_res = (m._2 + n._2) / Vectors.sqdist(centers(m._1), centers(n._1))
        if (meta_res > max_meta) max_meta = meta_res
      }
      max_meta
    }
    val DBI_res = meta_DBI.sum / C

    println("*********************************************************************************")
    println("*********************************************************************************")
    println("LSHSC==>cost Time " + (start - end) + " ms")
    //println("ASE value="+ASE_res)
    println("LSHSC==>DBI value==>"+DBI_res)
    //println("WSSSE="+WSSSE)
    println("*********************************************************************************")
    println("*********************************************************************************")

    sc.stop()

    (centers.toArray,center_num)
  }
  def main(args: Array[String]): Unit = {
    run(args)
  }

  def runWithSparkContext(args:Array[String],sparkContext: SparkContext): (RDD[(Int, Array[Double],Int)],RDD[(Int, Int)],RDD[(Int,Iterable[(Long,Vector)])]) ={
    //println("Spectral KMeans method on Synthetic data")
    if (args.length != 6) {
      System.err.println("ERROR:Spectral Clustering: <spark master> <path to data> <nParts> <sigma> <number of clusters> <sub partial>")
    }
    println("===========================" + args.mkString(",") + "===============================")
    @transient val sc = sparkContext

    val data_address = args(1)
    val nParts = args(2).toInt
    val sigma = args(3).toDouble
    val numcluster = args(4).toInt
    val subpartial = args(5).toDouble
    val parsed = sc.textFile(data_address, nParts).map(_.split(" ").map(_.toDouble)).map(Vectors.dense(_)).distinct.repartition(64).cache()

    val numDim = parsed.count
    val numfeatures = parsed.first.size

    val start = System.currentTimeMillis
    val model = SkLSH.train(parsed, numcluster, numDim.toInt, sigma, subpartial, 100, 1, K_MEANS_PARALLEL, 29)
    val end = System.currentTimeMillis

    val predictions = model.predictall()
    val meta_res = predictions.map(i => (i._2,i._1)).groupByKey

    val meta_res2 = predictions.map(i => (i._2,(i._3,i._1))).groupByKey

    val C = meta_res.count().toInt
    val N = parsed.count

    //compute centers in original data
    val centers_rdd = meta_res.map(i => {
      val NN = i._2.size
      (i._1, i._2.foldLeft[Array[Double]](new Array[Double](numfeatures))((U, V) => V.toArray.zip(U).map(i => i._1+i._2)).map(i => i/NN),NN)
    })//.collect.map(i => (i._1, Vectors.dense(i._2)))

    val centers=centers_rdd.collect.map(i => (i._1, Vectors.dense(i._2))).toMap

    val center_num = meta_res.map(i => (i._1, i._2.size))


    val centers_br = sc.broadcast(centers)
    val meta_dis = meta_res.map{ i =>
      val centers_node = centers_br.value(i._1)
      (i._1, i._2.map(j => Vectors.sqdist(j, centers_node)))
    }

    val ASE_res = meta_dis.map(i => math.pow(i._2.sum,2)).sum / N
    val WSSSE = meta_dis.map(i => i._2.sum).sum

    val cluster_avg = meta_dis.map(i => (i._1, i._2.sum / i._2.size)).collect

    val meta_DBI = for (i <- 0 until C) yield {
      var max_meta = 0.0
      for (j <- 0 until C if j!=i) {
        val m = cluster_avg(i)
        val n = cluster_avg(j)
        val meta_res = (m._2 + n._2) / Vectors.sqdist(centers(m._1), centers(n._1))
        if (meta_res > max_meta) max_meta = meta_res
      }
      max_meta
    }
    val DBI_res = meta_DBI.sum / C
    println("*********************************************************************************")
    println("*********************************************************************************")
    println("LSHSC==>cost Time " + (start - end) + " ms")
    //println("ASE value="+ASE_res)
    println("LSHSC==>DBI value="+DBI_res)
    //println("WSSSE="+WSSSE)
    println("*********************************************************************************")
    println("*********************************************************************************")
    //sc.stop()
    (centers_rdd,center_num,meta_res2)
  }
}
