package com.liu.bigds.clustering

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.clustering.KMeans.K_MEANS_PARALLEL
import org.apache.spark.mllib.clustering.{KMeans, SpectralKMeans}
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
/**
 * 基于t-nearrest neighbors的谱聚类测试.
 *
 * Parameters: spark的地址, HDFS的地址, 分区数, 稀疏度, sigma, 聚类中心数
 */
object SpectralKMeansTest extends Serializable {
  val NametoLabel = Map("C15" -> 0, "CCAT" -> 1, "E21" -> 2, "ECAT" -> 3, "GCAT" -> 4, "M11" -> 5)
  def run(args: Array[String]): (Array[(Int, Vector)],Array[(Int, Int)]) = {
    println("谱聚类算法启动")
    if (args.length != 6) {
      System.err.println("ERROR:Parameters: spark的地址, HDFS的地址, 分区数, 稀疏度, sigma, 聚类中心数")
    }
    println("===========================" + args.mkString(",") + "===============================")
    val conf = new SparkConf()
      .setMaster(args(0))
      .setAppName("t-nearrest neighbors Spectral Clustering")
      .set("spark.app.id","1001")
      //.setJars(List("hdfs://node10:9000/program/MySpectralClusteringAlgorithms.jar"))
      //.setJars(List("/home/ljj/IdeaProjects/MySpectralClusteringAlgorithms/out/artifacts/targetJar/MySpectralClusteringAlgorithms.jar"))
    @transient val sc = new SparkContext(conf)

    val data_address = args(1)
    val nParts = args(2).toInt
    val sparsity = args(3).toDouble
    val sigma = args(4).toDouble
    val numcluster = args(5).toInt
    val start1 = System.currentTimeMillis / 1000
    val br_nametolabel = sc.broadcast(NametoLabel)
    val parsed = sc.textFile(data_address, nParts).map(_.split(" ").map(_.toDouble)).map(Vectors.dense(_)).distinct.persist(StorageLevel.DISK_ONLY)
    println("分区数："+parsed.partitions.length)
    val numDim = parsed.count
    val numfeatures = parsed.first.size//属性个数
    val end1 = System.currentTimeMillis / 1000

    val start = System.currentTimeMillis
    //数据集 聚类中心数 个数 稀疏度 迭代次数 运行次数 状态 速度
    val model = SpectralKMeans.train(parsed, numcluster, numDim.toInt, sparsity, 100, 1, K_MEANS_PARALLEL, 29)
    val end = System.currentTimeMillis

    val predictions = model.predictall()
    val meta_res = predictions.map(i => (i._2,i._1) ).groupByKey
    val C = meta_res.count().toInt
    val N = parsed.count

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
    println("centers:"+centers.seq.toString())
    println("PSC==>加载数据:costs " + (start1 - end1) + " seconds")
    println("PSC==>Training costs " + (start - end) + " seconds")
    println("ASE="+ASE_res)
    println("DBI="+DBI_res)
    println("WSSSE="+WSSSE)
    println("*********************************************************************************")
    println("*********************************************************************************")
    (centers.toArray,center_num)

  }

  def runWithSC(args: Array[String],sparkContext:SparkContext): (RDD[(Int, Array[Double],Int)],RDD[(Int, Int)],RDD[(Int,Iterable[(Long,Vector)])]) = {
    println("谱聚类算法启动")
    if (args.length != 6) {
      System.err.println("ERROR:Parameters: spark的地址, HDFS的地址, 分区数, 稀疏度, sigma, 聚类中心数")
    }
    println("===========================" + args.mkString(",") + "===============================")
    @transient val sc = sparkContext

    val data_address = args(1)
    val nParts = args(2).toInt
    val sparsity = args(3).toDouble
    val sigma = args(4).toDouble
    val numcluster = args(5).toInt
    val start1 = System.currentTimeMillis / 1000
    val br_nametolabel = sc.broadcast(NametoLabel)
    val parsed = sc.textFile(data_address, nParts).map(_.split(" ").map(_.toDouble)).map(Vectors.dense(_)).distinct.persist(StorageLevel.DISK_ONLY)
    println("分区数："+parsed.partitions.length)
    val numDim = parsed.count
    val numfeatures = parsed.first.size//属性个数
    val end1 = System.currentTimeMillis / 1000

    val start = System.currentTimeMillis
    //数据集 聚类中心数 个数 稀疏度 迭代次数 运行次数 状态 速度
    val model = SpectralKMeans.train(parsed, numcluster, numDim.toInt, sparsity, 100, 1, K_MEANS_PARALLEL, 29)
    val end = System.currentTimeMillis

    val predictions = model.predictall()

    val meta_res = predictions.map(i => (i._2,i._1) ).groupByKey
    val meta_res2 = predictions.map(i => (i._2,(i._3,i._1))).groupByKey
    val C = meta_res.count().toInt
    val N = parsed.count

    val centers_rdd = meta_res.map(i => {
      val NN = i._2.size
      (i._1, i._2.foldLeft[Array[Double]](new Array[Double](numfeatures))((U, V) => V.toArray.zip(U).map(i => i._1+i._2)).map(i => i/NN),NN)
    })//.collect.map(i => (i._1, Vectors.dense(i._2))).toMap
    val centers=centers_rdd.collect().map(i=>(i._1,Vectors.dense(i._2))).toMap
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
    println("centers:"+centers.seq.toString())
    println("PSC==>加载数据:costs " + (start1 - end1) + " seconds")
    println("PSC==>Training costs :" + (start - end) + " seconds")
    println("ASE="+ASE_res)
    println("DBI="+DBI_res)
    println("WSSSE="+WSSSE)
    println("*********************************************************************************")
    println("*********************************************************************************")
    (centers_rdd,center_num,meta_res2)

  }

  def main(args: Array[String]): Unit = {
    val result=run(args)
    println(result._1)
  }

  def test(sc: SparkContext):Unit={
    println("test"+sc.version)
  }
}

