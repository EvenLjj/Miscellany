package org.apache.spark.mllib.clustering

import java.io.{File, PrintWriter}

import org.apache.spark.HashPartitioner
import org.apache.spark.mllib.linalg.BLAS.dot
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.mllib.linalg.{SparseVector, Vector, Vectors}
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel

import scala.collection.mutable.{ArrayBuffer, HashMap, PriorityQueue}


/**
 * 谱聚类算法的实现
 * 主要根据下面的论文作为参考:
 * Chen W Y, Song Y, Bai H, et al.
 * Parallel spectral clustering in distributed systems[J].
 * Pattern Analysis and Machine Intelligence, IEEE Transactions on, 2011, 33(3): 568-586.
 * 但是, 算法的实现并不稳定. 经常会因为一些math问题报错，比如会报矩阵不是半正定的。
 */

/**
  *
  * @param k  聚类中心的个数（默认值为2）
  * @param numDims   数据的个数（不知道请填写小于0的值,默认值为-1）
  * @param sparsity   矩阵的稀疏度(和数据点的个数相乘得到t的值，默认值为0.1）
  */
class SpectralKMeans(private var k:Int,
                     private var numDims:Int,
                     private var sparsity: Double) extends KMeans with Serializable{
  //包含多种类型
  implicit object TupleOrdering extends Ordering[(Long, Double)] with Serializable {
    def compare(a: (Long, Double), b: (Long, Double)) = if (a._2 < b._2) -1 else if (a._2 > b._2) 1 else 0
  }

  def this() = this(2, -1, 0.1)

  def setk(k: Int): this.type = {
    this.k = k
    this
  }

  def setDims(Dim: Int): this.type = {
    this.numDims = Dim
    this
  }

  def setSparsity(p: Double): this.type = {
    this.sparsity = p
    this
  }

  /**
   *
   * @param data
   * @return RDD[(Vector, Vector)] 原始的数据集和降维之后的数据集
   */
  def SpectralDimReduction(data: RDD[Vector], nParts: Int): RDD[(Long,(Vector, Vector))] = {
    @transient val sc = data.sparkContext
    val out = new PrintWriter(new File("/home/ljj/IdeaProjects/Result/metadata"))
    out.println("降维开始")
    if (numDims < 0) numDims = data.count().toInt
    val start1=System.currentTimeMillis();
    //为原始数据建立索引，并缓存起来
    val DataWithIndex = data.zipWithIndex().map(i => (i._2, new VectorWithNorm(i._1))).partitionBy(new HashPartitioner(nParts)).persist(StorageLevel.DISK_ONLY)
    val end1=System.currentTimeMillis();
    out.println("建立索引的时间："+(end1-start1)/1000)

    val start2=System.currentTimeMillis();
    //为矩阵的稀疏化创建一个堆
    var tempRDD = DataWithIndex.map(i => (i._1, (i._2,  new PriorityQueue[(Long, Double)])))
    val end2=System.currentTimeMillis();
    out.println("创建堆的时间："+(end2-start2)/1000)

    val t_value = (numDims * sparsity).ceil.toInt
    val k_br = sc.broadcast(k)//向集群广播聚类中心的个数
    //val t_br = sc.broadcast(t_value)

    val start3=System.currentTimeMillis()
    //遍历各个分区
    val parts = DataWithIndex.partitions
    for (p <- parts) {
      val t_br = sc.broadcast(t_value)//t邻近的值
      val idx = p.index//分区索引
      val partRdd = DataWithIndex.mapPartitionsWithIndex((index, element) =>
        if (index == idx) element else Iterator(), true)
      val PartialData = partRdd.collect//返回该索引的分区的数据集

      //广播分区数据
      val br_pd = sc.broadcast(PartialData)
      //构建相似矩阵以及每个数据点最近的t个近邻
      tempRDD = tempRDD.mapPartitions { iter => {
        val pd = br_pd.value
        val t = t_br.value
        //一条数据和一个分区进行比较，构建该数据t个最邻近点
        iter.map { case (index, (vector, queue)) => {
          for (j <- pd) {
            j match {
              case (index2, vector2) => {

                //计算两点之间的距离
                val dist = SpectralKMeans.fastSquaredDistance(vector, vector2)

                //计算该数据点的t个最邻近值
                if (queue.size < t) {
                  queue.enqueue((index2, dist))
                }
                else if (queue.size >= t) {
                  if (dist < queue.head._2) {
                    queue.dequeue()
                    queue.enqueue((index2, dist))
                  }
                }
              }
              case _ => throw new IllegalArgumentException("")
            }
          }
          (index, (vector, queue))
        }
        }
      }
      }
    }
    val end3=System.currentTimeMillis();
    out.println("计算堆的时间："+(end3-start3)/1000)

    val start4=System.currentTimeMillis();
    //距离矩阵
    val disData = tempRDD.map{
       case (index, (dimdata, queue)) => (index, queue.toArray)
      }.flatMap { case (row, col_dis_array) => {
          val parse = ArrayBuffer[(Long, (Long, Double))]()
          for (m <- col_dis_array) {
            //构建对阵的距离矩阵
            parse ++= ArrayBuffer((row, (m._1, m._2)), (m._1, (row, m._2)))
          }
          parse
        }
    }
    val end4=System.currentTimeMillis();
    out.println("计算距离矩阵的时间："+(end4-start4)/1000)

    val numDims_br = sc.broadcast(numDims)

    val start5=System.currentTimeMillis()
    //对称矩阵
    val disData_sym = disData.groupByKey()
      .map { case (row, iter) => {
      val DistFac = new HashMap[Int, Double]
      val numDims = numDims_br.value
      iter.map(j => {
        if (j._1 >= numDims) throw new IllegalArgumentException("维度出错")
        DistFac.getOrElseUpdate(j._1.toInt, j._2)
      })
      val ave_row = iter.map(a => (a._2, 1)).reduce((a, b) => (a._1 + b._1, a._2 + b._2))
      val avg = ave_row._1.toDouble / ave_row._2
      val sortedDistFac = DistFac.toSeq.sortBy(_._1).toMap//升序保存稀疏向量的指数
      (row, sortedDistFac, avg)
      }
    }
    val end5=System.currentTimeMillis();
    out.println("计算对称矩阵的时间："+(end5-start5)/1000)

    val avg_col_br = sc.broadcast(disData_sym.map(i => (i._1,i._3)).collect)


    val start6=System.currentTimeMillis()
    //相似度矩阵
    val simiData = disData_sym.mapPartitions { iter => {
      val col_avg = avg_col_br.value.toMap
      iter.map { i =>
        val simi = i._2.map(j => (j._1, math.exp((-j._2 * j._2) / (col_avg(i._1) * col_avg(j._1)))))//计算i与j之间的相似度
        //new IndexedRow(i._1.toLong, Vectors.sparse(num_dim, simi.keys.toArray, simi.values.toArray))
        (i._1, simi)
      }
    }
    }
    val end6=System.currentTimeMillis();
    out.println("计算相似度矩阵的时间："+(end6-start6)/1000)

    //out.println("Row ID: Feature1,Feature2,Feature3,Feature4,Feature5,Feature6")
    //out.print(simiData.map(i => i._1 + ":" + " " + Vectors.dense(Vectors.sparse(numDims, i._2.keys.toArray, i._2.values.toArray).toArray).toArray.mkString(",")).collect.mkString("\n"))

    //构造度矩阵（map）
    val D_diag = simiData.mapValues(i => math.pow(i.values.sum, 0.5)).collect.toMap
    val D_br = sc.broadcast(D_diag)

    val dims_br = sc.broadcast(numDims)

    val start7=System.currentTimeMillis()
    val L = simiData.mapPartitions { iter => {
        val D_exp = D_br.value
        val dims = dims_br.value
        iter.map { case (row_idx, simimap) => {
          val res = simimap.map { case (col_idx, value) => {
                var L_elements = 0.0
                val DSD = D_exp(row_idx) * value * D_exp(col_idx)//矩阵相乘
                if(col_idx == row_idx) L_elements =DSD
                else L_elements = DSD
                (col_idx, L_elements)
              }
            }
          (row_idx.toInt, Vectors.sparse(dims, res.keys.toArray, res.values.toArray))
          }
        }
      }
    }.map(i => (i._1.toLong, i._2))
    val end7=System.currentTimeMillis();
    out.println("计算拉普拉斯矩阵的时间："+(end7-start7)/1000)
   // out.println("Laplacian Matrix")
   // out.println("Row ID: Feature1,Feature2,Feature3,Feature4,Feature5,Feature6")
   // out.print(L.map(i => i._1 + ":" + " " + Vectors.dense(i._2.toArray).toArray.mkString(",")).collect.mkString("\n"))

    //System.exit(1)
    val start8=System.currentTimeMillis()
    //val mat0 = new PatchedRowMatrix(sc, L.partitions.length, L.map(i => i._2), numDims, L.map(i => i._2).first.size)
    val mat0 = new RowMatrix(L.map(i => i._2), numDims, numDims)
    //parallel eigensolver
    val mat1 = L.map(i => i._1).zip(mat0.computeSVD(k, true).U.rows)
    val end8=System.currentTimeMillis();
    out.println("矩阵分解的时间："+(end8-start8)/1000)

    def isallzero(input:Array[Double]): Boolean = {
      var flag:Boolean = true
      input.foreach(i => if(i!=0.0) flag=false)
      flag
    }

    val start9=System.currentTimeMillis()
    //计算规范拉普拉斯矩阵
    val mat = mat1.mapPartitions { iter =>
      val k_0 = k_br.value
      iter.map { i => {
        if (isallzero(i._2.toArray)) {
          (i._1, Vectors.dense(Array.fill[Double](k_0)(1.0)))
        }
        else {
          val norm = Vectors.norm(i._2, 2)
          (i._1, Vectors.dense(i._2.toArray.map(j => j / norm)))
        }
      }
      }
    }
    val end9=System.currentTimeMillis();
    out.println("规范化向量的时间："+(end9-start9)/1000)
    out.flush()
    out.close()
    DataWithIndex.map(i => (i._1, i._2.vector)).join(mat)//.map(_._2)
  }

  override def run(data:RDD[Vector]): SpectralKMeansModel = {
    //val out=new PrintWriter(new File("/home/ljj/IdeaProjects/Result/metadata"))
    //val start=System.currentTimeMillis()
    val reduced_k = SpectralDimReduction(data, data.partitions.length)
    //val end=System.currentTimeMillis()
    //out.println("总的降维时间的时间："+(end-start)/1000)

    val data_to_cluster = reduced_k.map(_._2._2)

    //val start1=System.currentTimeMillis()
    val kmeans_model = super.run(data_to_cluster)
    //val end1=System.currentTimeMillis()
   // out.println("建立模型时间："+(end1-start1)/1000)
    new SpectralKMeansModel(kmeans_model.clusterCenters, reduced_k, data.partitions.length)
  }
}

object SpectralKMeans extends Serializable{
  def train(data: RDD[Vector],
             k: Int,
             Dim: Int,
             sparsity: Double,
             maxIterations: Int,
             runs: Int,
             initializationMode: String,
             seed: Long): SpectralKMeansModel = {
             new SpectralKMeans()
                .setK(k)
                .setk(k)
                .setMaxIterations(maxIterations)
                .setRuns(runs)
                .setInitializationMode(initializationMode)
                .setSeed(seed)
                .setSparsity(sparsity)
                .setDims(Dim)
                .run(data)
  }

  private[clustering] lazy val EPSILON = {
    var eps = 1.0
    while ((1.0 + (eps / 2.0)) != 1.0) {
      eps /= 2.0
    }
    eps
  }

  private[clustering] def fastSquaredDistance(
                                               v1: VectorWithNorm,
                                               v2: VectorWithNorm): Double = {
    fastSquaredDistance(v1.vector, v1.norm, v2.vector, v2.norm)
  }

  private[clustering] def fastSquaredDistance(
                                          v1: Vector,
                                          norm1: Double,
                                          v2: Vector,
                                          norm2: Double,
                                          precision: Double = 1e-6): Double = {
    val n = v1.size
    require(v2.size == n)
    require(norm1 >= 0.0 && norm2 >= 0.0)
    val sumSquaredNorm = norm1 * norm1 + norm2 * norm2
    val normDiff = norm1 - norm2
    var sqDist = 0.0
    /*
     * The relative error is
     * <pre>
     * EPSILON * ( \|a\|_2^2 + \|b\\_2^2 + 2 |a^T b|) / ( \|a - b\|_2^2 ),
     * </pre>
     * which is bounded by
     * <pre>
     * 2.0 * EPSILON * ( \|a\|_2^2 + \|b\|_2^2 ) / ( (\|a\|_2 - \|b\|_2)^2 ).
     * </pre>
     * The bound doesn't need the inner product, so we can use it as a sufficient condition to
     * check quickly whether the inner product approach is accurate.
     */
    val precisionBound1 = 2.0 * EPSILON * sumSquaredNorm / (normDiff * normDiff + EPSILON)
    if (precisionBound1 < precision) {
      sqDist = sumSquaredNorm - 2.0 * dot(v1, v2)
    } else if (v1.isInstanceOf[SparseVector] || v2.isInstanceOf[SparseVector]) {
      val dotValue = dot(v1, v2)
      sqDist = math.max(sumSquaredNorm - 2.0 * dotValue, 0.0)
      val precisionBound2 = EPSILON * (sumSquaredNorm + 2.0 * math.abs(dotValue)) /
        (sqDist + EPSILON)
      if (precisionBound2 > precision) {
        sqDist = Vectors.sqdist(v1, v2)
      }
    } else {
      sqDist = Vectors.sqdist(v1, v2)
    }
    sqDist
  }

}



