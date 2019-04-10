
import java.sql.DriverManager
import java.util.Properties
import Config.ConfigLoader
import Fakers.FakerData.dob
import Fakers._
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql._
import org.apache.spark.sql.functions.udf
import com.microsoft.sqlserver.jdbc.SQLServerBulkCopy
import org.apache.spark.sql.execution.datasources.jdbc.JdbcUtils

object DeIdentify {

  def main(args: Array[String]) {

    Logger.getLogger("org").setLevel(Level.ERROR)
    Logger.getLogger("akka").setLevel(Level.ERROR)
    //Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
    if (args.length < 1) {
      System.err.println(
        "Usage: filename.jar filepath.conf")
      System.exit(1)
    }

    val jobConfigFile = args(0)
    println(jobConfigFile)
    val config = new ConfigLoader(jobConfigFile);

    val urlSrc = config.sampleConfig.urlSrc;
    val userSrc = config.sampleConfig.userSrc;
    val passwordSrc = config.sampleConfig.passwordSrc;

    val urlDest = config.sampleConfig.urlDest
    val userDest = config.sampleConfig.userDest;
    val passwordDest = config.sampleConfig.passwordDest;

    val outputPath = config.sampleConfig.outputPath

    val filter = config.sampleConfig.filter
    val percent = config.sampleConfig.percent / 100.0

    val sparkSession = SparkSession.builder().appName("SampleDataset").master("local").config("spark.local.dir", "c:\\tmp\\spark\\")
    .getOrCreate()
    val sc = sparkSession.sparkContext
    val sqlContext = sparkSession.sqlContext

    import sparkSession.implicits._
    sparkSession.sqlContext.udf.register("strLen", (s: String) => s.length())
    sparkSession.sqlContext.udf.register("rndValue", FakerData.rndValue _) //_ is passing param

    val connectProperties = new Properties()
    connectProperties.put("user", userSrc)
    connectProperties.put("password", passwordSrc)

    val timeBefore2 = java.lang.System.currentTimeMillis()


    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
    val con = DriverManager.getConnection(urlSrc,userSrc, passwordSrc)
    val SQL = "SELECT max(Pulse8MemberID) FROM dbo.Member20171218"
    val stmt = con.createStatement

    val rs = stmt.executeQuery(SQL)
    rs.next

    val upperBound = rs.getInt(1)
    val lowerBound = 1
    val numPartitions = 64
    val fetchsize= 5000

    stmt.executeUpdate("truncate TABLE dbo.MemberDeIdentify;" );

    val dfMember : Dataset[Row] = sparkSession.read.format("jdbc")
      .option("url",urlSrc)
      .option("dbtable", "dbo.Member20171218")
      .option("user", userSrc)
      .option("password", passwordSrc)
      .option("partitionColumn","Pulse8MemberID")
      .option( "numPartitions", numPartitions)
      .option("lowerBound", lowerBound)
      .option("upperBound", upperBound)
      .option("fetchsize", fetchsize)
      .load()

    dfMember.show(5)

    /*
     val dfMember = sparkSession.read.format("jdbc")
       .option("url", urlSrc).option("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver").option("dbtable", "dbo.Member20171218")
       .option("user", userSrc)
       .option("password", passwordSrc).load()
     dfMember.show(5)

 */

    //DataFrame Approach

     dfMember
       .withColumn("FirstName",   FakerData.randFirstName($"FirstName"))
       .withColumn("LastName",   FakerData.randLastName($"LastName"))
       .withColumn("PhoneNumber",   FakerData.randPhone($"PhoneNumber"))
       .withColumn("PlanMemberID",   FakerData.rndMemberId($"PlanMemberID"))
       .withColumn("DOB",   FakerData.rndDOB($"DOB"))
       .withColumn("Address1",   FakerData.randAddress1($"Address1"))
       .withColumn("Address2",   FakerData.randAddress2($"Address2"))
       .withColumn("State",   FakerData.randState($"State"))
       .withColumn("Zip",   FakerData.randZip($"Zip"))
       .withColumn("Phone",   FakerData.randPhone($"Phone"))
       .write.mode(SaveMode.Append)
       .jdbc(urlSrc, "dbo.MemberDeIdentify", connectProperties)


    /*

    //SQL Approach
    dfMember.createOrReplaceTempView("Members")

    val dfMemberNew =  sparkSession.sql("SELECT Pulse8MemberID " +
      ",rndValue('memberid') as PlanMemberID" +
    ",AltID " +
      ",rndValue('lastname') as LastName " +
      ",rndValue('firstname') as FirstName " +
      ",rndValue('phone') as PhoneNumber " +
      ",rndValue('dob') as DOB  " +
      ",Gender " +
      ",EffDate " +
      ",TermDate " +
      ",Race " +
      ",rndValue('address') as Address1 " +
      ",'' as Address2 " +
      ",rndValue('city') as City " +
      ",rndValue('state') as State " +
      ",rndValue('zip') as Zip " +
      ",County " +
      ",rndValue('phone') as Phone " +
      ",AltPhone1 " +
      ",Email " +
      ",PCPID " +
      ", 0 as Pulse8PCPID " +
      ",PCPAttribNumVisits " +
      ",PCPAttribLastVisitDate " +
      ",PlanID " +
      ",ProductCode " +
      ",Age " +
      ",AgeGroup " +
      ",AgeBand " +
      ",MetalLevel " +
      ",RatingArea " +
      ",ActuarialValue " +
      ",Premium " +
      ",LOB " +
      ",CurrentRiskScore " +
      ",CurrentFinancialValue " +
      ", 1 as ACAMember " +
      ",SourceFile " +
      ",GroupID " +
      ",CurrentRiskScoreConfirmed " +
      ",CurrentRiskScoreDemographic " +
      ",SubscriberID " +
      ",CurrentRiskScoreInteraction " +
      ",CSR_INDICATOR " +
      ",QHPID " +
      ",ProductDescription " +
      ",HIOSID " +
      ",PreferredLanguage " +
      ",TrendingRiskScore " +
      ",TrendingFinancialValue " +
      ",TrendingRiskScoreConfirmed " +
      ",TrendingRiskScoreDemographic " +
      ",TrendingRiskScoreInteraction " +
      ",SubscriberRelation " +
      ",DoD " +
      ",MaritalStatus " +
      ",HNumber " +
      ",NetworkType " +
      ",PayersProduct " +
      ",GroupName " +
      ",GroupType " +
      ",PrimaryIns " +
      ",OtherIns " +
      ",Hospice " +
      ",HPEmployee " +
      ",Dental " +
      ",Drug " +
      ",MHInpatient " +
      ",MHDayNight " +
      ",MHAmbulatory " +
      ",CDInPatient " +
      ",CDDayNight " +
      ",CDAmbulatory " +
      ",ESRD " +
      " FROM Members")

    dfMemberNew.show(5)

    dfMemberNew.write.mode(SaveMode.Append).jdbc(urlSrc, "dbo.MemberDeIdentify", connectProperties)

*/
    println("call procMemberDeIdentify ...")
    val cstmt = con.prepareCall("{call procMemberDeIdentify}")
    cstmt.execute();

    val timeAfter2 = java.lang.System.currentTimeMillis()
    val timeDifference2 = timeAfter2 - timeBefore2
    println("TIME difference: " + timeDifference2)

  }

}



