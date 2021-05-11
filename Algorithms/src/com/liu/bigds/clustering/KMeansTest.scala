package com.liu.bigds.clustering

import org.apache.spark.mllib.clustering.{KMeans, SpectralKMeans}
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.mllib.linalg.{Vector,Vectors}
import org.apache.spark.mllib.clustering.KMeans.{K_MEANS_PARALLEL, RANDOM}

object KMeansTest extends Serializable {
  def run(args: Array[String]): (Array[(Int, Vector)], Array[(Int,Int)]) = {
    println("KMeans normal referece")
    if (args.length != 4) {
      System.err.println("ERROR:Spectral Clustering: <spark master> <path to data> <nParts>  <number of clusters>")
    }
    println("===========================" + args.mkString(",") + "===============================")
    val conf = new SparkConf()
      .setMaster(args(0))
      .setAppName("KMeans Clustering as reference")
      //.setJars(List("hdfs://node10:9000/program/MySpectralClusteringAlgorithms.jar"))
      //.setJars(List("/home/ljj/IdeaProjects/MySpectralClusteringAlgorithms/out/artifacts/targetJar/MySpectralClusteringAlgorithms.jar"))
    @transient val sc = new SparkContext(conf)

    val data_address = args(1)
    val nParts = args(2).toInt
    val numcluster = args(3).toInt
    val br_nametolabel = sc.broadcast(1)
    val parsed = sc.textFile(data_address, nParts).map(_.split(" ").map(_.toDouble)).map(Vectors.dense(_)).distinct.cache()

    val numDim = parsed.count
    val numfeatures = parsed.first.size
    //val model = SpectralKMeans.train(parsed, numcluster, numDim.toInt, sparsity, 100, 1, K_MEANS_PARALLEL, 29)
    val start = System.currentTimeMillis / 1000
    val model = KMeans.train(parsed, numcluster, 100)
    val end = System.currentTimeMillis / 1000
    val N = parsed.count
    val ASE_res = model.computeCost(parsed)

    val centers = model.clusterCenters.zipWithIndex.map(i => (i._2,i._1))
    val center_num = model.predict(parsed).map(i => (i,1)).groupByKey.map(i => (i._1, i._2.size)).collect()


    println("*********************************************************************************")
    println("*********************************************************************************")
    println("K-Means==>Training costs " + (start - end) + " seconds")
    println("ASE_res="+ASE_res)
    println("WSSSE value")
    println("*********************************************************************************")
    println("*********************************************************************************")

    (centers,center_num)
  }

  def runWithSC(args: Array[String],sparkContext: SparkContext): (Array[(Int, Vector)], Array[(Int,Int)]) = {
    println("KMeans normal referece")
    if (args.length != 4) {
      System.err.println("ERROR:Spectral Clustering: <spark master> <path to data> <nParts>  <number of clusters>")
    }
    println("===========================" + args.mkString(",") + "===============================")
    @transient val sc = sparkContext

    val data_address = args(1)
    val nParts = args(2).toInt
    val numcluster = args(3).toInt
    val br_nametolabel = sc.broadcast(1)
    val parsed = sc.textFile(data_address, nParts).map(_.split(" ").map(_.toDouble)).map(Vectors.dense(_)).distinct.cache()

    val numDim = parsed.count
    val numfeatures = parsed.first.size
    //val model = SpectralKMeans.train(parsed, numcluster, numDim.toInt, sparsity, 100, 1, K_MEANS_PARALLEL, 29)
    val start = System.currentTimeMillis
    val model = KMeans.train(parsed, numcluster, 100)
    val end = System.currentTimeMillis
    val N = parsed.count
    val ASE_res = model.computeCost(parsed)

    val centers = model.clusterCenters.zipWithIndex.map(i => (i._2,i._1))
    val center_num = model.predict(parsed).map(i => (i,1)).groupByKey.map(i => (i._1, i._2.size)).collect()


    val m=model.predict(parsed)
    val predictions=parsed.zip(m)


    val meta_res = predictions.map(i => (i._2,i._1) ).groupByKey
    val C = meta_res.count().toInt



    val centers2 = meta_res.map(i => {
      val NN = i._2.size
      (i._1, i._2.foldLeft[Array[Double]](new Array[Double](numfeatures))((U, V) => V.toArray.zip(U).map(i => i._1+i._2)).map(i => i/NN))
    }).collect.map(i => (i._1, Vectors.dense(i._2))).toMap

    val centers_br = sc.broadcast(centers2)

    val meta_dis = meta_res.map{ i =>
      val centers_node = centers_br.value(i._1)
      (i._1, i._2.map(j => Vectors.sqdist(j, centers_node)))
    }

    val cluster_avg = meta_dis.map(i => (i._1, i._2.sum / i._2.size)).collect

    val meta_DBI = for (i <- 0 until C) yield {
      var max_meta = 0.0
      for (j <- 0 until C if j!=i) {
        val m = cluster_avg(i)
        val n = cluster_avg(j)
        val meta_res = (m._2 + n._2) / Vectors.sqdist(centers2(m._1), centers2(n._1))
        if (meta_res > max_meta) max_meta = meta_res
      }
      max_meta
    }
    val DBI_res = meta_DBI.sum / C

    println("*********************************************************************************")
    println("*********************************************************************************")
    println("K-Means==>Training costs " + (start - end) + " seconds")
    println("ASE_res="+ASE_res)
    println("DBI="+DBI_res)
    println("WSSSE value")
    println("*********************************************************************************")
    println("*********************************************************************************")

    (centers,center_num)
  }

  def main(args:Array[String]): Unit = {
    run(args)
  }
}