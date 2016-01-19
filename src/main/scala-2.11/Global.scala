import java.lang.management.ManagementFactory

import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by bahadir on 18/01/16.
 */
object Global {

  val heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax()
  val executorMemory:Int = (heap * 0.7).toInt

  val sparkConf = new SparkConf()
    .setAppName("socstats-sample")
    .setMaster("local[150]")
    .set("spark.io.compression.codec","org.apache.spark.io.LZ4CompressionCodec")
    .set("spark.driver.allowMultipleContexts","true")
    .set("spark.task.maxFailures","40")
    .set("spark.executor.memory", executorMemory.toString + "b")
    .set("spark.driver.memory",   executorMemory.toString + "b")

  val sparkContext = new SparkContext(sparkConf)

  sparkContext.setLogLevel("WARN")

}
