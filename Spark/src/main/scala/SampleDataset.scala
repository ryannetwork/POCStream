
import Fakers._
import org.apache.spark.sql._
import Config.ConfigLoader
import org.apache.spark.sql.functions.udf
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import java.util.Properties
object SampleDataset {

  def main(args: Array[String]) {

    Logger.getLogger("org").setLevel(Level.ERROR)
    Logger.getLogger("akka").setLevel(Level.ERROR)
    //Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
    if (args.length < 1) {
      System.err.println(
        "Usage: filename.jar filepath.jobcfg")
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
      //.config("spark.driver.memory", "1g")
      //.config("spark.executor.memory", "2g")
    .getOrCreate()

    import sparkSession.implicits._
    val sc = sparkSession.sparkContext
    val sqlContext = sparkSession.sqlContext

    sparkSession.sqlContext.udf.register("strLen", (s: String) => s.length())
    sparkSession.sqlContext.udf.register("rndValue", FakerData.rndValue _) //_ is passing param

    //small dataset claims

    val dfClaim = sparkSession.read.format("jdbc")
      .option("url", urlSrc).option("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver").option("dbtable", "dbo.Claim")
      .option("user", userSrc)
      .option("password", passwordSrc).load().filter(filter).sample(false, percent)

    dfClaim.show(4)

    //join with smallter claim
    val dfClaimDetail = sparkSession.read.format("jdbc")
      .option("url", urlSrc).option("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver").option("dbtable", "dbo.ClaimDetail")
      .option("user", userSrc)
      .option("password", passwordSrc).load().as("cd").join(dfClaim.as("c"), $"cd.ClaimID" === $"c.ClaimID").select($"cd.*")


    dfClaim.createOrReplaceTempView("Claims")
    dfClaimDetail.createOrReplaceTempView("ClaimDetails")

    val dfMemberSmall = sparkSession.read.format("jdbc")
      .option("url", urlSrc).option("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver").option("dbtable", "dbo.Member")
      .option("user", userSrc)
      .option("password", passwordSrc).load()
      .as("m").join(dfClaim.as("c"), $"m.PlanMemberID" === $"c.MemberAltID").select($"m.*")


   dfMemberSmall.createOrReplaceTempView("Members")

    //dfMemberSmall.show(4)
    val dfProvider = sparkSession.read.format("jdbc")
      .option("url", urlSrc).option("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver").option("dbtable", "dbo.Provider")
      .option("user", userSrc)
      .option("password", passwordSrc).load()

    val dfProviderSmall = dfProvider.as("p").join(dfClaim.as("c"), $"p.ProviderID" === $"c.RenderingProvID").select($"p.*").union(dfProvider.as("p")
      .join(dfMemberSmall.as("m"), $"p.ProviderID" === $"m.PCPID").select($"p.*"))distinct()

    dfProviderSmall.createOrReplaceTempView("Providers")


   val dfProviderSmallNew = sparkSession.sql("select "+
    "  ProviderID as ProviderID_Old" +
      ", rndValue('providerid') as ProviderID" +
      ", rndValue('tin') as TIN" +
      ", rndValue('npi') as NPI" +
      ", rndValue('lastname') as LastName" +
      ", rndValue('firstname') as FirstName"+
      ", rndValue('address') as Address1"+
      ", rndValue('city') as City"+
      ", rndValue('state') as State"+
      ", rndValue('zip') as Zip"+
      ", rndValue('phone') as Phone"+
      ", SpecCode"+
      ", SpecDesc"+
      ", GroupID"+
      ", GroupName"+
      ", SettlementEntityTIN"+
      ", Type"+
      ", Location from Providers").cache()

    dfProviderSmallNew.show(4)
    dfProviderSmallNew.createOrReplaceTempView("Providers_New")

    val dfMemberSmallNew =  sparkSession.sql("select m.PlanMemberID as PlanMemberID_old" +
      ",rndValue('memberid') as PlanMemberID" +
      ",rndValue('lastname') as LastName " +
      ",rndValue('firstname') as FirstName " +
      ",rndValue('phone') as PhoneNumber " +
      ",rndValue('dob') as DOB " +
      ",Gender " +
      ", EffDate" +
      ", TermDate " +
      ", Race " +
      ",rndValue('address') as Address1 " +
      ",rndValue('city') as City " +
      ",rndValue('state') as State " +
      ",rndValue('zip') as Zip " +
      ",County " +
      ",rndValue('phone') as Phone " +
      ", p.ProviderID " +
      ", PlanID " +
      ", ProductCode " +
      ",ActuarialValue " +
      ", LOB " +
      ",ACAMember " +
      ",m.GroupID " +
      ",rndValue('hicn') as HICN " +
      ",rndValue('') as SubscriberID" +
      ",rndValue('') as CTGCode from Members m LEFT OUTER JOIN Providers_New p On m.PCPID = p.ProviderID_Old" ).cache()

/*
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
      ",Pulse8PCPID " +
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
      ",ACAMember " +
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

*/


    dfMemberSmallNew.show(4)
    dfMemberSmallNew.createOrReplaceTempView("Members_New")

    val dfClaimSmallNew = sparkSession.sql("select 	ClaimID" +
      ",ClaimSysID" +
      ",m.PlanMemberID as MemberAltID" +
      ",MRN" +
      ",ReceivedDate" +
      ",PaidDate" +
      ",ClaimType" +
      ",ServiceType" +
      ",BillType" +
      ",IPOPFlag" +
      ",p.ProviderID as RenderingProvID" +
      ",PayToProvID" +
      ",FacilityProvID" +
      ",PatientCtlNum" +
      ",DeniedDate" +
      ",DeniedDays" +
      ",FromDOS" +
      ",ThruDOS" +
      ",POS" +
      ",AdjustedClaimID" +
      ",AdjustedClaimCode" +
      ",SourceFile" +
      ",TotalPaid" +
      ",TotalCharges" +
      ",TotalAllowed" +
      ",ServiceYear" +
      ",VisitType" +
      ",m.PlanMemberID" +
      " from Claims C LEFT OUTER JOIN Members_New m ON C.MemberAltID = m.PlanMemberID_old " +
      " LEFT OUTER JOIN Providers_New P ON P.ProviderID_Old = C.RenderingProvID").cache()

    dfClaimSmallNew.show(4)


    if (outputPath.length > 0)
      {

        /*
        dfProviderSmallNew.drop("ProviderID_Old")
         // .coalesce(1)
          .write
          .option("header", "true")
          .mode(SaveMode.Overwrite)
          .csv(outputPath + "\\providerSmalldata")

        dfMemberSmallNew.drop("PlanMemberID_old")
          .coalesce(1)
          .write
          .option("header", "true")
          .mode(SaveMode.Overwrite)
          .csv(outputPath + "\\MemberSmalldata")

    */


  /*
        val connectProperties = new Properties()
        connectProperties.put("user", userSrc)
        connectProperties.put("password", passwordSrc)


        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance()
        dfMemberNew.write.mode(SaveMode.Append).jdbc(urlSrc, "dbo.members", connectProperties)
  */
      }

  }

  implicit class DataFrameHelper(df:DataFrame){
    import scala.util.Try
    def SampleFilter(filter: String) : DataFrame ={

      if (filter.length == 0)
      {
          return df;
      }
      else
        {
          df.filter(filter)
        }
    }

  }

}



