import java.io.{File, FileWriter}

import Config.ConfigLoader
import Fakers._
import org.apache.commons.io.FileUtils

import scala.util.Random

object GenerateMember2 {
  def main(args: Array[String]): Unit = {
    val genders = Array("F", "M")

    val rnd = new Random()
    val jobConfigFile = args(0)
    println(jobConfigFile)
    val config = new ConfigLoader(jobConfigFile);
    val filePath = config.MembersConfig2.filePath

    FileUtils.deleteQuietly(new File(filePath))

    val fw = new FileWriter(filePath, true)

    val header = "record_id,firstName,lastName,dob,gender,hicn,address1,address2,city,state,zip,phone,memberid,\n"
    fw.write(header)

    for (iteration <- 1 to config.MembersConfig2.records) {

      var memberid = (rnd.alphanumeric take 12 mkString("")).toUpperCase
      var hicn = (rnd.alphanumeric take 12 mkString("")).toUpperCase
      val gender = genders(rnd.nextInt(genders.length))
      val lastName = FakerData.faker.name.lastName
      val firstName = FakerData.faker.name.firstName
      val phone = FakerData.faker.phoneNumber.cellPhone

      val address1 = FakerData.faker.address.streetAddress(false)
      val address2 = ""
      val city =  FakerData.faker.address.city
      val state = FakerData.faker.address.stateAbbr
      val zip   =FakerData.faker.address.zipCode

      val dob = FakerData.dob

      val line = s"$iteration,$firstName,$lastName,$dob,$gender,$hicn,$address1,$address2,$city,$state,$zip,$phone,$memberid\n"

      fw.write(line)

    }
    fw.close()

  }

}
