import Fakers._
import org.apache.spark.sql._
import Config.ConfigLoader
import org.apache.spark.sql.functions.udf
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql._
import org.apache.spark.sql.functions._

object TestValidation {

  def main(args: Array[String]) {

    Logger.getLogger("org").setLevel(Level.ERROR)
    Logger.getLogger("akka").setLevel(Level.ERROR)

    if (args.length < 1) {
      System.err.println(
        "Usage: filename.jar filepath.jobcfg")
      System.exit(1)
    }

    val jobConfigFile = args(0)
    println(jobConfigFile)
    val config = new ConfigLoader(jobConfigFile);

    //spark configurations
    val sparkSession = SparkSession.builder().appName("SmallDataset")
      .master("local")
      .config("spark.local.dir", "c:\\tmp\\spark\\")
      .config("spark.driver.memory", "1g")
      .config("spark.executor.memory", "2g")
      .getOrCreate()

    import sparkSession.implicits._
    val sc = sparkSession.sparkContext
    val sqlContext = sparkSession.sqlContext

    sparkSession.sqlContext.udf.register("strLen", (s: String) => s.length())

    val schemaMember1 = Dataframes.dynamicSchema(config.MembersConfig1.layout)
    val memberDataRdd = sc.textFile(config.MembersConfig1.filePath)
    val memberDF1 = Dataframes.genDataFrame(sqlContext, memberDataRdd, schemaMember1, ",", "true")
      .cache()
    memberDF1.show(5)
    memberDF1.filter($"invalidData" === 1).show()
    //.write.option("header", "true")
    //  .mode(SaveMode.Overwrite).csv(ErrorOutput + "Member\\")
    memberDF1
      .filter("invalidData is null")
      .createOrReplaceTempView("Members1");


    val schemaMember2 = Dataframes.dynamicSchema(config.MembersConfig2.layout)
    val memberDataRdd2 = sc.textFile(config.MembersConfig2.filePath)
    val memberDF2 = Dataframes.genDataFrame(sqlContext, memberDataRdd2, schemaMember2, ",", "true")
      .cache()
    memberDF2.show(5)
    memberDF2.filter($"invalidData" === 1).show()
    //.write.option("header", "true")
    //  .mode(SaveMode.Overwrite).csv(ErrorOutput + "Member\\")
    memberDF2
      .filter("invalidData is null")
      .createOrReplaceTempView("Members2");

    sparkSession.sql("select memberid, firstname, lastname, dob, gender from  Members1").show(5)
    sparkSession.sql("select memberid, firstname, lastname, dob, gender from  Members2").show(5)


    sc.stop()

  }

}
