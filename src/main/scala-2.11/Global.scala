import java.lang.management.ManagementFactory

import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by bahadir on 18/01/16.
 */
object Global {

  val heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax()
  val sparkMode = sys.env.getOrElse("SPARK_MODE", "local[2]")
  val tmpDir = sys.env.getOrElse("SPARK_TMP_DIR", "/disk2/sparktmp")
  val rpmDir = sys.env.getOrElse("RPM_DIR", "/disk2/rpm")
  val rpmCacheSize = sys.env.getOrElse("RPM_CACHE_SIZE", "200000").toInt
  val executorMemory:Int = (heap * 0.7).toInt

  val sparkConf = new SparkConf()
    .setAppName("socstats-sample")
    .setMaster(sparkMode)
    .set("spark.io.compression.codec","org.apache.spark.io.LZ4CompressionCodec") //default olan serverda hata veriyor
    .set("spark.driver.allowMultipleContexts","true")
    .set("spark.task.maxFailures","40")
    .set("spark.local.dir", tmpDir)
    .set("spark.executor.memory", executorMemory.toString + "b")
    .set("spark.driver.memory",   executorMemory.toString + "b")

  val confInfo = sparkConf.getAll.map(a => a._1 + ": " + a._2).reduce((a,b) => a + "\n" + b)

  val sparkContext = new SparkContext(sparkConf)


}
